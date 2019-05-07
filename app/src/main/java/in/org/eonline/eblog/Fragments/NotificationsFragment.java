package in.org.eonline.eblog.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import in.org.eonline.eblog.Adapters.BlogAdapter;
import in.org.eonline.eblog.Adapters.NotificationsAdapter;
import in.org.eonline.eblog.Models.BlogModel;
import in.org.eonline.eblog.Models.NotifactionModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.SQLite.DatabaseHelper;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {


    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    RecyclerView Notifications;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs_new" ;
    private String userId;
    DatabaseHelper sqliteDatabaseHelper;
    public Dialog dialog;
    List<NotifactionModel> NotifactionModels =new ArrayList<NotifactionModel>();
    public SwipeRefreshLayout mySwipeRequestLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Notifications = getView().findViewById(R.id.Row2);
        mySwipeRequestLayout = (SwipeRefreshLayout)getView().findViewById(R.id.swipeContainer);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString("UserIdCreated","Deepak9702173103");

        sqliteDatabaseHelper = new DatabaseHelper(getActivity());
        db= FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        setYourBlogsFromFirebase();

        refreshMyProfile();
        //MobileAds.initialize(getContext(),"ca-app-pub-7722811932766421~9001519486");
       /* mAdView = (AdView) getView().findViewById(R.id.yourBlogs_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
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
        NotifactionModels.clear();
        setYourBlogsFromFirebase();

    }


    public void setYourBlogsFromFirebase() {
        connectivityReceiver = new ConnectivityReceiver(getActivity());
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {
            dialog = CommonDialog.getInstance().showProgressDialog(getActivity());
            dialog.show();
            enterBlogsFirebase();
        } else {
            CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);

            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }


    }

    public void enterBlogsFirebase() {
       // CollectionReference blogRef = db.collection("Notification").document(userId).collection("AllNotification");

        CollectionReference colRef = db.collection("Notification").document(userId).collection("AllNotification");
        colRef.orderBy("LikeTimeStamp", Query.Direction.DESCENDING).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonDialog.getInstance().showErrorDialog(getContext(), R.drawable.failure_image);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            setBlogModel(document);
                        }
                        i++;
                    }
                    if (i == 0) {
                        CommonDialog.getInstance().showErrorDialog(getContext(), R.drawable.nonotification);
                    }
                    setPopularBlogsRecyclerView();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


    }


    private void setBlogModel(QueryDocumentSnapshot document) {

        NotifactionModel notifactionModel = new NotifactionModel();
        notifactionModel.setBlogId(document.getString("BlogId"));
        notifactionModel.setUserid(document.getString("Userid"));
        notifactionModel.setNotifactionData(document.getString("NotifactionData"));
       // notifactionModel.setLikeTimeStamp(String.valueOf(document.getString("LikeTimeStamp")));
        NotifactionModels.add(notifactionModel);
    }

    public void setPopularBlogsRecyclerView() {
        Notifications.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        Notifications.setLayoutManager(linearLayoutManager);
        Notifications.setAdapter(new NotificationsAdapter(getActivity(),NotifactionModels));

    }




}
