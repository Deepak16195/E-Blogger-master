package in.org.eonline.eblog.Activities;


import android.app.Dialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.org.eonline.eblog.Models.UserModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.SQLite.DatabaseHelper;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;
import in.org.eonline.eblog.Utilities.FontClass;
import in.org.eonline.eblog.Utilities.ImageUtility;


import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileAcivity extends AppCompatActivity {
    DatabaseHelper sqliteDatabaseHelper;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;
    private ImageView userProfileImage;
    private EditText userFnameEdit;
    private EditText userLnameEdit;
    private EditText userEmailIdEdit;
    private EditText userContactEdit;
    UserModel userModel = new UserModel();
    Map<String, Object> userMap = new HashMap<>();
    private Button submitButton;
    private Boolean isDataInserted = false;
    private Boolean isUserRegisteredAlready = false;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs_new" ;
    private SharedPreferences.Editor editor;
    private String userIdCreated;
    private String userProfileUrl;
    private Uri picUri;
    public Dialog dialog;
    private Bitmap myBitmap;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private AdView mAdView;
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int ALL_WRITE_EXTERNAL_STORAGE = 108;
    private final static int ALL_READ_EXTERNAL_STORAGE = 109;
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    public SwipeRefreshLayout mySwipeRequestLayout;
    private File destFile, sourceFile;
    private File file;
    String dateFormatter;
    public static final String IMAGE_DIRECTORY = "E-Blogger";
    private TextView edit_profile, cancel_profile;


    public MyProfileAcivity() {
        // Required empty public constructor
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To make activity Full Screen
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_my_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sharedpreferences = MyProfileAcivity.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        isUserRegisteredAlready = sharedpreferences.getBoolean("isUserCreated", false);
        userIdCreated = sharedpreferences.getString("UserIdCreated","document");
        //userProfileUrl = sharedpreferences.getString("userProfileUrl", "imageUrl");

        initializeViews();

        ViewGroup myMostParentLayout = (ViewGroup)findViewById(R.id.my_profile_layout);
        FontClass.getInstance(MyProfileAcivity.this).setFontToAllChilds(myMostParentLayout);

        // get instance of Firebase Firestore Database
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        // get instance of Firebase Storage
        storage = FirebaseStorage.getInstance();

        checkUserFirebase();  //call this method only when user has already registered

        sqliteDatabaseHelper = new DatabaseHelper(MyProfileAcivity.this);

        if (savedInstanceState != null) {
            picUri = savedInstanceState.getParcelable("pic_uri");
        }

        allowPermissions();

        uploadImage();

        submitUserProfile();

        editProfile();

        disableProfile();
        //downloadImageFromFirebaseStorage();

        refreshMyProfile();

        /* if(!userProfileUrl.equals("imageUrl") && userProfileUrl != null) {
            Glide.with(MyProfileAcivity.this).load(userProfileUrl).into(userProfileImage);
        } */

        MobileAds.initialize(MyProfileAcivity.this,"ca-app-pub-7293397784162310~9840078574");
        mAdView = (AdView) findViewById(R.id.myProfile_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Calendar ct = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        dateFormatter = df.format(ct.getTime());

    }

    public void editProfile(){
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_profile.setVisibility(View.VISIBLE);
                edit_profile.setVisibility(View.GONE);
                submitButton.setEnabled(true);
                submitButton.setAlpha((float)1);
                userFnameEdit.setEnabled(true);
                userFnameEdit.setFocusable(true);
                userFnameEdit.setFocusableInTouchMode(true);
                userFnameEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                userLnameEdit.setEnabled(true);
                userLnameEdit.setFocusable(true);
                userLnameEdit.setFocusableInTouchMode(true);
                userLnameEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                userContactEdit.setEnabled(true);
                userContactEdit.setFocusable(true);
                userContactEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                userContactEdit.setFocusableInTouchMode(true);
                userProfileImage.setEnabled(true);
            }
        });
    }

    public void disableProfile() {
        cancel_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableEditText();
            }
        });
    }

    public void refreshMyProfile(){

        mySwipeRequestLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

               // onRefreshOperation();
                mySwipeRequestLayout.setRefreshing(false);
            }
        }
        );

}
    /*public void onRefreshOperation(){
       // getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag("nav_profile");
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }*/



    public void submitUserProfile() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedpreferences.getBoolean("isUserCreated", false)) {
                    userModel.setUserId(userIdCreated);
                    connectivityReceiver = new ConnectivityReceiver(MyProfileAcivity.this);
                    // Initialize SDK before setContentView(Layout ID)
                    isInternetPresent = connectivityReceiver.isConnectingToInternet();
                    if (isInternetPresent) {
                        dialog = CommonDialog.getInstance().showProgressDialog(MyProfileAcivity.this);
                        dialog.show();
                        setUserModelAndUserMap();
                        updateDataToUserFirebase();
                    } else {
                        CommonDialog.getInstance().showErrorDialog(MyProfileAcivity.this, R.drawable.no_internet);
                        //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    connectivityReceiver = new ConnectivityReceiver(MyProfileAcivity.this);
                    // Initialize SDK before setContentView(Layout ID)
                    isInternetPresent = connectivityReceiver.isConnectingToInternet();
                    if (isInternetPresent) {
                        dialog = CommonDialog.getInstance().showProgressDialog(MyProfileAcivity.this);
                        dialog.show();
                        setUserModelAndUserMap();
                        startUploadingImageToFirebase();
                    } else {
                        CommonDialog.getInstance().showErrorDialog(MyProfileAcivity.this, R.drawable.no_internet);
                        //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    public void startUploadingImageToFirebase() {
        setUserIdInSharedPref();
        if(userIdCreated != null && userProfileImage != null && sharedpreferences.getString("UserIdCreated","document").equals(userModel.getUserId())) {
            uploadImageToFirebaseStorage();
        }
    }


    public void checkUserFirebase() {
        connectivityReceiver = new ConnectivityReceiver(MyProfileAcivity.this);
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {
            dialog = CommonDialog.getInstance().showProgressDialog(MyProfileAcivity.this);
            dialog.show();
            enterUserFirebase();
        } else {
            CommonDialog.getInstance().showErrorDialog(MyProfileAcivity.this, R.drawable.no_internet);
            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }
    }

    public void enterUserFirebase(){
        DocumentReference docRef = db.collection("Users").document(userIdCreated);
        docRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonDialog.getInstance().showErrorDialog(MyProfileAcivity.this, R.drawable.failure_image);
                Toast.makeText(MyProfileAcivity.this, "Server is down", Toast.LENGTH_SHORT).show();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        setUserModel(document);
                        //edit_profile.setVisibility(View.VISIBLE);
                        try {
                            String userImageUrl = document.getString("UserImageUrl");
                            if(userImageUrl != null) {
                                downloadImageFromFirebaseStorage();
                            } else {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }
                        catch(NullPointerException e) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                        //Toast.makeText(MyProfileAcivity.this, "Data is retrieved from firebase", Toast.LENGTH_LONG).show();
                    } else {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                } else {
                    CommonDialog.getInstance().showErrorDialog(MyProfileAcivity.this, R.drawable.failure_image);
                    Toast.makeText(MyProfileAcivity.this, "server is down", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    public void downloadImageFromFirebaseStorage() {
        storageRef = storage.getReference();

        storageRef.child("Users/" + sharedpreferences.getString("UserIdCreated", "document")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                userModel.setUserImage(uri.toString());
                Glide.with(MyProfileAcivity.this)
                        .load(uri)
                        .into(userProfileImage);
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                CommonDialog.getInstance().showErrorDialog(MyProfileAcivity.this, R.drawable.failure_image);
                dialog.dismiss();
            }
        });
    }

    public void setUserModel(DocumentSnapshot document){
        userModel = new UserModel();
        userModel.setUserFName(document.getString("UserFirstName"));
        userFnameEdit.setText(userModel.getUserFName());
        userFnameEdit.setEnabled(false);
        userFnameEdit.setFocusable(false);
        userFnameEdit.setInputType(InputType.TYPE_NULL);
        userModel.setUserLName(document.getString("UserLastName"));
        userLnameEdit.setText(userModel.getUserLName());
        userLnameEdit.setEnabled(false);
        userLnameEdit.setFocusable(false);
        userLnameEdit.setInputType(InputType.TYPE_NULL);
        userModel.setUserEmail(document.getString("UserEmailId"));

        userModel.setAllFollow(document.get("AllFollow"));
        userModel.setAllFollowing(document.get("AllFollowing"));

        userEmailIdEdit.setText(userModel.getUserEmail());
        userEmailIdEdit.setEnabled(false);
        userEmailIdEdit.setFocusable(false);
        // blogModel.setBlogLikes(Integer.parseInt(document.getString("BlogLikes")));
        userModel.setUserContact(document.getString("UserContact"));
        userContactEdit.setText(userModel.getUserContact());
        userContactEdit.setFocusable(false);
        userContactEdit.setEnabled(false);
        userContactEdit.setInputType(InputType.TYPE_NULL);

        edit_profile.setVisibility(View.VISIBLE);
        cancel_profile.setVisibility(View.GONE);
    }

    public void setUserModelAndUserMap() {
        userModel.setUserFName(userFnameEdit.getText().toString());
        userModel.setUserLName(userLnameEdit.getText().toString());
        userModel.setUserEmail(userEmailIdEdit.getText().toString());
        userModel.setUserContact(userContactEdit.getText().toString());
        userMap.put("UserFirstName", userModel.getUserFName());
        userMap.put("UserLastName", userModel.getUserLName());
        userMap.put("UserEmailId", userModel.getUserEmail());
        userMap.put("UserContact", userModel.getUserContact());
    }

    public void setUserIdInSharedPref() {
        String userId = userModel.getUserEmail();
        userModel.setUserId(userId);
        editor = sharedpreferences.edit();
        editor.putString("UserIdCreated",userId);
        editor.putString("UserNameCreated",userModel.getUserFName() +" "+userModel.getUserLName());
        editor.commit();
    }

    public void addDataToUserFirebase(){
        db.collection("Users").document(userModel.getUserId()).set(userMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MyProfileAcivity.this, "Data is successfully saved", Toast.LENGTH_SHORT).show();
                        isDataInserted = sqliteDatabaseHelper.insertUserDataInSQLite(userModel.getUserFName(),
                                userModel.getUserLName(), userModel.getUserEmail(), userModel.getUserContact()); //this method returns boolean value
                        editor = sharedpreferences.edit();
                        editor.putBoolean("isUserCreated", isDataInserted);
                        editor.putString("UserFirstName",userModel.getUserFName());
                        editor.commit();
                        dialog.dismiss();
                      //  startUploadingImageToFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(MyProfileAcivity.this, R.drawable.failure_image);
                        Toast.makeText(MyProfileAcivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    public void updateDataToUserFirebase() {
             DocumentReference userDoc = db.collection("Users").document(userIdCreated);
             userDoc.update(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        //Toast.makeText(MyProfileAcivity.this, "Data is updated successfully", Toast.LENGTH_SHORT).show();
                        startUploadingImageToFirebase();
                        editor.putString("UserFirstName",userFnameEdit.getText().toString());
                        editor.commit();
                        disableEditText();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(MyProfileAcivity.this, R.drawable.failure_image);
                        Log.w(TAG, "Error updating document", e);
                        dialog.dismiss();

                    }
                });
    }

    public void disableEditText(){
        userFnameEdit.setEnabled(false);
        userFnameEdit.setFocusable(false);
        userFnameEdit.setFocusableInTouchMode(false);
        userFnameEdit.setInputType(InputType.TYPE_NULL);
        userLnameEdit.setEnabled(false);
        userLnameEdit.setFocusable(false);
        userLnameEdit.setFocusableInTouchMode(false);
        userLnameEdit.setInputType(InputType.TYPE_NULL);
        userContactEdit.setEnabled(false);
        userContactEdit.setFocusable(false);
        userContactEdit.setInputType(InputType.TYPE_NULL);
        userContactEdit.setFocusableInTouchMode(false);
        submitButton.setEnabled(false);
        submitButton.setAlpha((float)0.5);
        edit_profile.setVisibility(View.VISIBLE);
        cancel_profile.setVisibility(View.GONE);
        userProfileImage.setEnabled(false);
    }

    public void uploadImageToFirebaseStorage() {
        // Create a storage reference from our app
        storageRef = storage.getReference();

        // Create a child reference, imagesRef now points to "mountains.jpg"
        final StorageReference imagesRef = storageRef.child("Users/" + sharedpreferences.getString("UserIdCreated", "document"));

        // Get the data from an ImageView as bytes
        userProfileImage.setDrawingCacheEnabled(true);
        userProfileImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) userProfileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        editor.putString("UserImagePath",saveToInternalStorage(bitmap));
        editor.commit();
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                CommonDialog.getInstance().showErrorDialog(MyProfileAcivity.this, R.drawable.failure_image);
                Toast.makeText(MyProfileAcivity.this, "File could not be uploaded", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                //Toast.makeText(MyProfileAcivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                getimageUrl();
                    //downloadImageFromFirebaseStorage();
                /*userProfileUrl = imagesRef.getDownloadUrl().toString();
                editor = sharedpreferences.edit();
                editor.putString("userProfileUrl",userProfileUrl);
                editor.apply(); */
                }
            });
    }

    public String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(MyProfileAcivity.this);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir

        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }


  public void getimageUrl(){
      storageRef = storage.getReference();
      storageRef.child("Users/" + sharedpreferences.getString("UserIdCreated", "document")).getDownloadUrl()
              .addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                      String downloadUrl = uri.toString();
                      if (downloadUrl != null) {
                      //updateUserImageUrlInFirebase(downloadUrl);
                          userModel.setUserImage(downloadUrl);
                          userMap.put("UserImageUrl", downloadUrl);
                          addDataToUserFirebase();
                          //update or create user collection
                      }
                  }
              }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure (@NonNull Exception exception){
                       CommonDialog.getInstance().showErrorDialog(MyProfileAcivity.this, R.drawable.failure_image);
                       dialog.dismiss();

                  Toast.makeText(MyProfileAcivity.this, "Could not get user image url", Toast.LENGTH_LONG).show();
                  }
              });

  }

  public void updateUserImageUrlInFirebase(String downloadUrl){
      DocumentReference userDoc = db.collection("Users").document(userIdCreated);
      userDoc.update("UserImageUrl", downloadUrl)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                      Log.d(TAG, "DocumentSnapshot successfully updated!");
                      //Toast.makeText(MyProfileAcivity.this, "Image Id updated successfully", Toast.LENGTH_SHORT).show();
                      startUploadingImageToFirebase();
                  }
              })
              .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Log.w(TAG, "Error updating document", e);
                  }
              });
  }


    public void initializeViews() {
        userProfileImage = (ImageView) findViewById(R.id.user_profile_image);
        userFnameEdit = (EditText) findViewById(R.id.first_name);
        userLnameEdit = (EditText) findViewById(R.id.last_name);
        userEmailIdEdit = (EditText) findViewById(R.id.email_id);
        userContactEdit = (EditText) findViewById(R.id.mobile_no);
        submitButton = (Button) findViewById(R.id.submit_button);
        mySwipeRequestLayout =(SwipeRefreshLayout) findViewById(R.id.swiperefresh_profile);
        edit_profile  = (TextView) findViewById(R.id.user_edit);
        cancel_profile = (TextView) findViewById(R.id.user_cancel);
        submitButton.setEnabled(false);
        submitButton.setAlpha((float)0.5);
        userProfileImage.setEnabled(false);
    }

    public void allowPermissions() {
        permissions.add(CAMERA);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(WRITE_EXTERNAL_STORAGE);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_READ_EXTERNAL_STORAGE);
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_WRITE_EXTERNAL_STORAGE);
            }
        }
    }



    public void uploadImage() {
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = MyProfileAcivity.this.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = MyProfileAcivity.this.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;

        /*File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Toast.makeText(MyProfileAcivity.this, "Error While Capturing Image", Toast.LENGTH_SHORT).show();
        }
        Uri outputFileUri = null;
        if (photoFile != null) {
            outputFileUri = FileProvider.getUriForFile(MyProfileAcivity.this, "in.org.eonline.eblog.fileprovider", photoFile);
        }
        return outputFileUri; */
    }

    private File createImageFile() throws IOException {
        String dateFormatter;
        Calendar ct = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        dateFormatter = df.format(ct.getTime());
        String imageFileName = "img_" + dateFormatter.format(new Date().toString());
        File storageDir = MyProfileAcivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    // onActivityResult() is executed when startActivityForResult(getPickImageChooserIntent(), 200); is called
    // The onActivityResult essentially returns a URI to the image. Some devices do return the bitmap as data.getExtras().get("data");
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == RESULT_OK) {
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);

                bitmap = compressImage(picUri);
                try{
                    bitmap = rotateImageIfRequired(MyProfileAcivity.this, bitmap, picUri);
                } catch (IOException e) {
                    userProfileImage.setImageBitmap(bitmap);
                }

                userProfileImage.setImageBitmap(bitmap);

                /*try {
                    myBitmap = MediaStore.Images.Media.getBitmap(MyProfileAcivity.this.getContentResolver(), picUri);
                    //myBitmap = rotateImageIfRequired(myBitmap, picUri);
                    //myBitmap = getResizedBirotateImageIfRequiredtmap(myBitmap, 500);
                    userProfileImage.setImageBitmap(myBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MyProfileAcivity.this, "Unable to set Image", Toast.LENGTH_SHORT).show();
                } */
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                myBitmap = bitmap;
                userProfileImage.setImageBitmap(myBitmap);
            }
        }
    }

    public Bitmap compressImage(Uri picuri) {
        Bitmap bmp;
        file = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
        sourceFile = new File(ImageUtility.getInstance().getPathFromGooglePhotosUri(MyProfileAcivity.this, picuri));
        destFile = new File(file, "img_" + dateFormatter.format(new Date().toString() + ".png"));

        try {
            ImageUtility.getInstance().copyFile(sourceFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bmp = ImageUtility.getInstance().decodeFile(destFile);
        return bmp;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    // Devices like Samsung galaxy are known to capture the image in landscape orientation.
    // Retrieving the image and displaying as it is can cause it to be displayed in the wrong orientation.
    // Hence we’ve called the method rotateImageIfRequired(myBitmap, picUri);
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation changes or returning back from camera to this fragment
        outState.putParcelable("pic_uri", picUri);
    }

   /* @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            picUri = savedInstanceState.getParcelable("pic_uri");
        }
    }*/

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (MyProfileAcivity.this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:

            case ALL_READ_EXTERNAL_STORAGE:

            case ALL_WRITE_EXTERNAL_STORAGE:

                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MyProfileAcivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
