package in.org.eonline.eblog.Fragments;


import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.org.eonline.eblog.R;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;
import in.org.eonline.eblog.Utilities.FontClass;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonetizationFragment extends Fragment {

    private EditText adMobAdUnitIdEdit;
    private Button submitAdMobAdUnitId;
    private String adMobUnitId;
    private TextView admobLink, monetizationEdit, monetizationCancel;
    FirebaseFirestore db;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs_new" ;
    private  String userId;
    private List<String> blogModelsList = new ArrayList<String>();
    Map<String, String> blogMap = new HashMap<>();
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    public SwipeRefreshLayout mySwipeRequestLayout;
    public Dialog dialog;

    public MonetizationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monetization, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db= FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        initializeViews();
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString("UserIdCreated","AdityaKamat75066406850");
        checkAdmobId();
        refreshMyProfile();

        ViewGroup myMostParentLayout = (ViewGroup) getView().findViewById(R.id.swiperefresh_monetize);
        FontClass.getInstance(getActivity()).setFontToAllChilds(myMostParentLayout);

        submitAdMobAdUnitId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adMobUnitId = adMobAdUnitIdEdit.getText().toString();

                /*View adContainer = getView().findViewById(R.id.adMobView);

                AdView userAdView = new AdView(getActivity());

                userAdView.setAdSize(AdSize.BANNER);
                userAdView.setAdUnitId(adMobUnitId);

                //((RelativeLayout)adContainer).addView(userAdView);

                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("5DDD17EFB41CB40FC08FBE350D11B395").build();
                userAdView.loadAd(adRequest); */

                addAdMobIdToUsers(adMobUnitId);

            }
        });

        admobLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://apps.admob.com"));
                startActivity(browserIntent);

                /*WebView webView = (WebView) findViewById(R.id.webView1);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("http://www.google.com");*/
            }
        });

        monetizationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monetizationCancel.setVisibility(View.VISIBLE);
                monetizationEdit.setVisibility(View.GONE);
                adMobAdUnitIdEdit.setEnabled(true);
                adMobAdUnitIdEdit.setFocusable(true);
                adMobAdUnitIdEdit.setFocusableInTouchMode(true);
                submitAdMobAdUnitId.setEnabled(true);
                submitAdMobAdUnitId.setAlpha((float)1);
            }
        });

        disableEditFields();

        monetizationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableEditFields();
            }
        });
    }

    public void disableEditFields() {
        monetizationEdit.setVisibility(View.VISIBLE);
        monetizationCancel.setVisibility(View.GONE);
        adMobAdUnitIdEdit.setEnabled(false);
        adMobAdUnitIdEdit.setFocusable(false);
        adMobAdUnitIdEdit.setFocusableInTouchMode(false);
        submitAdMobAdUnitId.setEnabled(false);
        submitAdMobAdUnitId.setAlpha((float)0.5);
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
        //getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag("nav_monetize");
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }




    public void addAdMobIdToUsers(String adMobUnitId) {
        connectivityReceiver = new ConnectivityReceiver(getActivity());
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {
            dialog = CommonDialog.getInstance().showProgressDialog(getActivity());
            dialog.show();
            addDataToUserFirebase(adMobUnitId);

        } else {
            CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);
            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }
    }

    public void addDataToUserFirebase(String adMobUnitId){
        db.collection("Users").document(userId).update("UserBannerId", adMobUnitId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        disableEditFields();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    public void initializeViews() {
        adMobAdUnitIdEdit = (EditText) getView().findViewById(R.id.admob_ad_unit_id);
        submitAdMobAdUnitId = (Button) getView().findViewById(R.id.admob_ad_unit_id_submit);
        //userAdView = (AdView) getView().findViewById(R.id.user_ad_view);
        mySwipeRequestLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh_monetize);
        admobLink = (TextView) getView().findViewById(R.id.open_admob_link);
        monetizationEdit = (TextView) getView().findViewById(R.id.monetization_edit);
        monetizationCancel = (TextView) getView().findViewById(R.id.monetization_cancel);
    }

    public void checkAdmobId()
    {

        connectivityReceiver = new ConnectivityReceiver(getActivity());
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {
            dialog = CommonDialog.getInstance().showProgressDialog(getActivity());
            dialog.show();
            enterUsersFirebaseForAmobId();

        } else {
            CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);
            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }


    }

    public void enterUsersFirebaseForAmobId(){
        DocumentReference docRef = db.collection("Users").document(userId);
        docRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try {
                            adMobAdUnitIdEdit.setText(document.getString("UserBannerId").toString());
                            //Toast.makeText(getContext(), "Admob ID present", Toast.LENGTH_LONG).show();
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                        catch(NullPointerException e){
                            Toast.makeText(getContext(), "Enter Admob ID", Toast.LENGTH_LONG).show();
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }

                    else {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        /*setUserModelAndUserMap();
                        addDataToUserFirebase();*/
                    }
                } else {
                    Toast.makeText(getContext(), "Enter Admob ID", Toast.LENGTH_SHORT).show();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

            }
        });

    }

}
