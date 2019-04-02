package in.org.eonline.eblog.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import in.org.eonline.eblog.Models.BlogModel;
import in.org.eonline.eblog.Models.BugModel;
import in.org.eonline.eblog.Models.UserModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.SQLite.DatabaseHelper;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;
import in.org.eonline.eblog.Utilities.FontClass;


public class ReportBugFragment extends Fragment {
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


    public ReportBugFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_bug, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        refreshMyProfile();
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString("UserIdCreated","AdityaKamat75066406850");

        submitBugFunction();

        MobileAds.initialize(getContext(),"ca-app-pub-7722811932766421~9001519486");
        mAdView = (AdView) getView().findViewById(R.id.reportBug_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ViewGroup myMostParentLayout = (ViewGroup) getView().findViewById(R.id.reportBug_layout);
        FontClass.getInstance(getActivity()).setFontToAllChilds(myMostParentLayout);
    }

    public void submitBugFunction(){
        connectivityReceiver = new ConnectivityReceiver(getActivity());
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
            CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);

            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }
    }

    public void initializeViews() {
        bugUserName = (EditText) getView().findViewById(R.id.reporter_name);
        bugBrandMode1 = (EditText) getView().findViewById(R.id.reporter_brand_model);
        bugOSVersion = (EditText) getView().findViewById(R.id.reporter_OS_version);
        bugMessage= (EditText) getView().findViewById(R.id.reporter_message);
        submitBug = (Button) getView().findViewById(R.id.submit_report_bug);
        errorBrand= (TextView) getView().findViewById(R.id.error_brand);
        errorName=(TextView) getView().findViewById(R.id.error_name);
        errorOSVersion=(TextView) getView().findViewById(R.id.error_version);
        errorMessage=(TextView) getView().findViewById(R.id.error_message);
        errorImage1=(ImageView) getView().findViewById(R.id.error1_image);
        errorImage2=(ImageView) getView().findViewById(R.id.error2_image);
        errorImage3=(ImageView) getView().findViewById(R.id.error3_image);
        errorImage4=(ImageView) getView().findViewById(R.id.error4_image);
        setVisibilityGone();
        mySwipeRequestLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh_report_bug);
    }


    public void refreshMyProfile(){

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
    }

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
        connectivityReceiver = new ConnectivityReceiver(getActivity());
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {
            dialog = CommonDialog.getInstance().showProgressDialog(getActivity());
            dialog.show();
            addBugtoUserFirebase();
        } else {
            CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);

            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }

    }

    public void addBugtoUserFirebase(){
        db.collection("ReportedBugs").document(userId).set(bugMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Bug is reported", Toast.LENGTH_LONG).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                        Toast.makeText(getActivity(), "Some error occured while reporting bug", Toast.LENGTH_LONG).show();
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
