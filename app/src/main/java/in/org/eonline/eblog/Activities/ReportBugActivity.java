package in.org.eonline.eblog.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import in.org.eonline.eblog.Models.BugModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;
import in.org.eonline.eblog.Utilities.FontClass;


public class ReportBugActivity extends AppCompatActivity {
    private EditText bugUserName;
    private EditText bugBrandMode1;
    private EditText bugOSVersion;
    private EditText bugMessage;
    FirebaseFirestore db;
    private Button submitBug;
    BugModel bugModel = new BugModel();
    private String userId;
    Map<String, String> bugMap = new HashMap<>();
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs_new" ;
    private TextView errorName;
    private TextView errorBrand;
    private TextView errorOSVersion;
    private TextView errorMessage;
    private ImageView errorImage1;
    private ImageView errorImage2;
    private ImageView errorImage3;
    private ImageView errorImage4;
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    public SwipeRefreshLayout mySwipeRequestLayout;
    public Dialog dialog;
    private AdView mAdView;


    public ReportBugActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To make activity Full Screen
        setContentView(R.layout.fragment_report_bug);
        initializeViews();
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


        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
       // refreshMyProfile();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString("UserIdCreated","Deepak9702173103");

        submitBugFunction();

        MobileAds.initialize(ReportBugActivity.this,"ca-app-pub-7722811932766421~9001519486");
        mAdView = (AdView) findViewById(R.id.reportBug_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ViewGroup myMostParentLayout = (ViewGroup) findViewById(R.id.reportBug_layout);
        FontClass.getInstance(ReportBugActivity.this).setFontToAllChilds(myMostParentLayout);
    }

    public void submitBugFunction(){
        connectivityReceiver = new ConnectivityReceiver(ReportBugActivity.this);
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {

            submitBug.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setVisibilityGone();
                    setBugUserMap();
                }
            });
        } else {
            CommonDialog.getInstance().showErrorDialog(ReportBugActivity.this, R.drawable.no_internet);

            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }
    }

    public void initializeViews() {
        bugUserName = (EditText) findViewById(R.id.reporter_name);
        bugBrandMode1 = (EditText) findViewById(R.id.reporter_brand_model);
        bugOSVersion = (EditText) findViewById(R.id.reporter_OS_version);
        bugMessage= (EditText) findViewById(R.id.reporter_message);
        submitBug = (Button) findViewById(R.id.submit_report_bug);
        errorBrand= (TextView) findViewById(R.id.error_brand);
        errorName=(TextView) findViewById(R.id.error_name);
        errorOSVersion=(TextView) findViewById(R.id.error_version);
        errorMessage=(TextView) findViewById(R.id.error_message);
        errorImage1=(ImageView) findViewById(R.id.error1_image);
        errorImage2=(ImageView) findViewById(R.id.error2_image);
        errorImage3=(ImageView) findViewById(R.id.error3_image);
        errorImage4=(ImageView) findViewById(R.id.error4_image);
        setVisibilityGone();
        mySwipeRequestLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_report_bug);
    }


  /*  public void refreshMyProfile(){

        mySwipeRequestLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshOperation();
                mySwipeRequestLayout.setRefreshing(false);
            }
        });
    }
    public void onRefreshOperation(){
      //  getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag("nav_report_bug");
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
        clearEditText();
    }*/

    public void setVisibilityGone(){
        errorMessage.setVisibility(View.GONE);
        errorOSVersion.setVisibility(View.GONE);
        errorName.setVisibility(View.GONE);
        errorBrand.setVisibility(View.GONE);
        errorImage1.setVisibility(View.GONE);
        errorImage2.setVisibility(View.GONE);
        errorImage4.setVisibility(View.GONE);
        errorImage3.setVisibility(View.GONE);

    }

  public void setBugUserMap(){
       bugModel.setBugUserName(bugUserName.getText().toString());
       bugModel.setBugBrandMode1(bugBrandMode1.getText().toString());
       bugModel.setBugOSVersion(bugOSVersion.getText().toString());
       bugModel.setBugMessage(bugMessage.getText().toString());
      validateData();
  }
public void validateData(){
      if(bugModel.getBugUserName().toString().equals(""))
      {
          errorName.setVisibility(View.VISIBLE);
          errorImage1.setVisibility(View.VISIBLE);
      }
      if (bugModel.getBugBrandMode1().toString().equals(""))
      {
          errorBrand.setVisibility(View.VISIBLE);
          errorImage2.setVisibility(View.VISIBLE);
      }
      if (bugModel.getBugOSVersion().toString().equals(""))
      {
          errorOSVersion.setVisibility(View.VISIBLE);
          errorImage3.setVisibility(View.VISIBLE);
      }
      if (bugModel.getBugMessage().toString().equals(""))
      {
          errorMessage.setVisibility(View.VISIBLE);
          errorImage4.setVisibility(View.VISIBLE);
      }
      else{
          bugMap.put("BugUserName",bugModel.getBugUserName());
          bugMap.put("BugBrandModel",bugModel.getBugBrandMode1());
          bugMap.put("BugOSVersion",bugModel.getBugOSVersion());
          bugMap.put("BugMessage",bugModel.getBugMessage());
          addBugDataToFirebase();
      }
}
    public void addBugDataToFirebase(){
        connectivityReceiver = new ConnectivityReceiver(ReportBugActivity.this);
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {
            dialog = CommonDialog.getInstance().showProgressDialog(ReportBugActivity.this);
            dialog.show();
            addBugtoUserFirebase();
        } else {
            CommonDialog.getInstance().showErrorDialog(ReportBugActivity.this, R.drawable.no_internet);

            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }

    }

    public void addBugtoUserFirebase(){
        db.collection("ReportedBugs").document(userId).set(bugMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ReportBugActivity.this, "Bug is reported", Toast.LENGTH_LONG).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(ReportBugActivity.this, R.drawable.failure_image);
                        Toast.makeText(ReportBugActivity.this, "Some error occured while reporting bug", Toast.LENGTH_LONG).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    public void clearEditText(){
        bugUserName.setText("");
        bugBrandMode1.setText("");
        bugOSVersion.setText("");
        bugMessage.setText("");
    }




}
