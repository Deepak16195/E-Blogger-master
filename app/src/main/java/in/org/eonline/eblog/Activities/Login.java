package in.org.eonline.eblog.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import in.org.eonline.eblog.Adapters.ViewPagerAdapter;
import in.org.eonline.eblog.HomeActivity;
import in.org.eonline.eblog.Models.UserModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;


public class Login extends AppCompatActivity {

    SignInButton signInButton;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 101;
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    Map<String, Object> userMap = new HashMap<>();
    Map<String, String> FireBaseTokenMap = new HashMap<>();
    FirebaseFirestore db;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs_new" ;
    private SharedPreferences.Editor editor;
    private String userIdCreated;
    private ViewPager viewPager;
    private TextView backButton, nextButton;
    private ViewPagerAdapter mAdapter;
    public String flag = "";
    public Dialog dialog;
    private Button Click_SingnIn;

    private int[] mImageResources = {
            R.drawable.viewpagerone,
            R.drawable.viewpagertwo,
            R.drawable.viewpagerthree,
            R.drawable.viewpagerfour,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        viewPager = (ViewPager) findViewById(R.id.viewPagerContainer);
        Click_SingnIn = (Button) findViewById(R.id.Click_SingnIn);
      //  backButton = (TextView) findViewById(R.id.viewPagerBack);
       // nextButton = (TextView) findViewById(R.id.viewPagerNext);
        flag = "MainViewPager";
        mAdapter = new ViewPagerAdapter(Login.this, mImageResources, flag);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0, true);
        viewPager.setOffscreenPageLimit(1);

        TabLayout tabLayout =  findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);

       // backButton.setOnClickListener(this);
       // nextButton.setOnClickListener(this);


        Click_SingnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectivityReceiver = new ConnectivityReceiver(getApplicationContext());
                // Initialize SDK before setContentView(Layout ID)
                isInternetPresent = connectivityReceiver.isConnectingToInternet();
                if (isInternetPresent) {
                    signIn();
                } else {
                    CommonDialog.getInstance().showErrorDialog(Login.this, R.drawable.no_internet);
                    //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
                }
            }
        });

        initializeViews();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userIdCreated = sharedpreferences.getString("UserIdCreated","document");

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .requestProfile()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        callLoginWithGoogle();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser != null) {
            Toast.makeText(Login.this, "User is already signed in...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Login.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void initializeViews() {
        signInButton = findViewById(R.id.sign_in_button);
    }

    public void callLoginWithGoogle() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectivityReceiver = new ConnectivityReceiver(getApplicationContext());
                // Initialize SDK before setContentView(Layout ID)
                isInternetPresent = connectivityReceiver.isConnectingToInternet();
                if (isInternetPresent) {
                    signIn();
                } else {
                    CommonDialog.getInstance().showErrorDialog(Login.this, R.drawable.no_internet);
                    //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signIn() {
        //call this signout() function before every sign in call in order to get google login account chooser/picker
        //mGoogleSignInClient.signOut();
        dialog = CommonDialog.getInstance().showProgressDialog(Login.this);
        dialog.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(Login.this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null) {
                                String FullName = "";
                                String FirstName = "";
                                String LastName = "";
                                UserModel userModel = new UserModel();
                                try{
                                    FullName = user.getDisplayName();
                                } catch(NullPointerException e) {
                                    Toast.makeText(Login.this, "Display Name from FirebaseUser is empty", Toast.LENGTH_SHORT).show();
                                }
                                for (UserInfo userInfo : user.getProviderData()) {
                                    if (FullName == null && userInfo.getDisplayName() != null) {
                                        FullName = userInfo.getDisplayName();
                                    }
                                }
                                if(FullName.contains(" ")) {
                                    FirstName = FullName.substring(0, FullName.indexOf(" "));
                                    LastName = FullName.substring(FullName.indexOf(" "));
                                } else {
                                    FirstName = FullName;
                                    LastName = "Not Available";
                                }
                                userModel.setUserFName(FirstName);
                                userModel.setUserLName(LastName);
                                userModel.setUserEmail(user.getEmail());
                                userModel.setUserId(user.getEmail());

                                editor = sharedpreferences.edit();
                                editor.putString("UserIdCreated",userModel.getUserId());
                                editor.putBoolean("isUserCreated", true);
                                editor.putString("UserNameCreated",userModel.getUserFName() +" "+userModel.getUserLName());
                                editor.putString("UserFirstName",userModel.getUserFName());
                                editor.commit();
                                addDataToUserFirebase(userModel);
                            }
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            //Toast.makeText(Login.this, "signInWithCredential:success", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Login.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                            Toast.makeText(Login.this, "Authentication Failed.", Toast.LENGTH_LONG).show();
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }

                        // ...
                    }
                });
    }

    public void addDataToUserFirebase(UserModel userModel){
        userMap.put("UserFirstName", userModel.getUserFName()+"");
        userMap.put("UserLastName", userModel.getUserLName()+"");
        userMap.put("UserEmailId", userModel.getUserEmail()+"");
        userMap.put("AllFollow", userModel.getAllFollow());
        userMap.put("AllFollowing", userModel.getAllFollowing());
        db.collection("Users").document(userModel.getUserId()).set(userMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(Login.this, "Data is successfully saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Some error occured", Toast.LENGTH_SHORT).show();
                    }
                });

        String data = FirebaseInstanceId.getInstance().getToken();
        FireBaseTokenMap.put("TokenKey", data);
        db.collection("FcmIDs").document(userModel.getUserId()).set(FireBaseTokenMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Login.this, "Data is successfully saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Some error occured", Toast.LENGTH_SHORT).show();
                    }
                });

    }





    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        /*FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser != null) {
            Toast.makeText(Login.this, "User is already signed in...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Login.this, HomeActivity.class);
            startActivity(intent);
        }
        */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
