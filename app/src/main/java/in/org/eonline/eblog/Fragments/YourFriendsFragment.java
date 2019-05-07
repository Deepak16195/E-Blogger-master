package in.org.eonline.eblog.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import in.org.eonline.eblog.Activities.MyProfileAcivity;
import in.org.eonline.eblog.Adapters.FollowAdapter;
import in.org.eonline.eblog.Models.FollowModel;
import in.org.eonline.eblog.Pages.PagerAdapterInviteFriendsScreen;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;

import static in.org.eonline.eblog.HomeActivity.MyPREFERENCES;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourFriendsFragment extends Fragment {

    /*-----------------  VIEW COMPONENT DECLARE ------------------*/
    public TabLayout mTabLayout;
    ViewPager mViewPager;
    Intent mIntent;
    int UserId;
    FragmentManager mFragmentManager;
    ArrayList<String> StoreFollow = new ArrayList<String>();
    PagerAdapterInviteFriendsScreen adapter;
    /*------------------------------------------------------------*/
    public YourFriendsFragment() {
        // Required empty public constructor
    }

    public static YourFriendsFragment newInstance() {
        YourFriendsFragment fragment = new YourFriendsFragment();
        return fragment;
    }

    String[] userIdBlog;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    public Dialog dialog;
    private SharedPreferences sharedpreferences;
    private String bannerId, userId, UserName, FirstName;
    ArrayList<FollowModel> SenDataToFollow = new ArrayList<FollowModel>();
    ArrayList<FollowModel> SenDataToFollowing = new ArrayList<FollowModel>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_your_friends, container, false);
        initView(rootView);
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString("UserIdCreated", "Deepak9702173103");
        UserName = sharedpreferences.getString("UserNameCreated", "Deepak9702173103");
        FirstName = sharedpreferences.getString("UserFirstName", "Deepak");
        userIdBlog = userId.split("\\_");
        setYourBlogsFromFirebase();
    }

    public void GetBlogsFirebase() {
        db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> arrayList = (ArrayList<String>) document.get("AllFollow");
                        //Do what you need to do with your ArrayList
                        StoreFollow.clear();
                        if (arrayList == null) {
                            arrayList = new ArrayList<>();
                        } else {
                            if (arrayList.size() > 0) {
                                for (String s : arrayList) {
                                    StoreFollow.add(s);
                                }
                            }
                        }
                    }
                    enterBlogsFirebase();
                }
            }
        });
    }

    public void setYourBlogsFromFirebase() {
        connectivityReceiver = new ConnectivityReceiver(getActivity());
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {
            dialog = CommonDialog.getInstance().showProgressDialog(getActivity());
            dialog.show();
            GetBlogsFirebase();

        } else {
            CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);

            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }
    }

    public void enterBlogsFirebase() {
        CollectionReference colRef = db.collection("Users");
        colRef.orderBy("UserEmailId", Query.Direction.ASCENDING).get().addOnFailureListener(new OnFailureListener() {
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
                        i++;
                        if (document.exists()) {
                            FollowModel followModel = new FollowModel();
                            followModel.setUserEmailId(document.getString("UserEmailId"));
                            followModel.setUserFirstName(document.getString("UserFirstName"));
                            followModel.setUserLastName(document.getString("UserLastName"));
                            String ImageUrl = document.getString("UserImageUrl");
                            if (ImageUrl != null) {
                                followModel.setUserImageUrl(ImageUrl);
                            }
                            String data = followModel.getUserEmailId();
                            if (StoreFollow.contains(data)) {
                                followModel.setFolloers(true);
                            } else {
                                followModel.setFolloers(false);
                            }
                            SenDataToFollow.add(followModel);
                        } else {
                            CommonDialog.getInstance().showErrorDialog(getContext(), R.drawable.failure_image);
                        }
                    }
                    if (i == 0) {
                        CommonDialog.getInstance().showErrorDialog(getContext(), R.drawable.no_data);
                    }
                    db.collection("Users").document(userIdBlog[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    ArrayList<String> arrayList = (ArrayList<String>) document.get("AllFollowing");
                                    //Do what you need to do with your ArrayList
                                    SenDataToFollowing.clear();
                                    if (arrayList == null) {
                                        arrayList = new ArrayList<>();
                                    } else {
                                        if (arrayList.size() > 0) {
                                            for (String s : arrayList) {
                                                enterUserFirebase(s);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });

                    if (SenDataToFollow.size() > 0) {
                        mFragmentManager = getChildFragmentManager();
                        /*< ---------------- Tab Layout  ------------- > */
                        mTabLayout.addTab(mTabLayout.newTab().setText("Follow"));
                        mTabLayout.addTab(mTabLayout.newTab().setText("Following"));
                        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
                        /*< ------------------------------------------ > */
                        /*< ---------------- View Pager   ------------- > */
                        adapter = new PagerAdapterInviteFriendsScreen(mFragmentManager, mTabLayout.getTabCount(), SenDataToFollow, SenDataToFollowing);
                        mViewPager.setSaveFromParentEnabled(false);
                        mViewPager.setAdapter(adapter);
                        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
                        /* < ------------------------------------------ > */
                    }

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } else {
                    CommonDialog.getInstance().showErrorDialog(getContext(), R.drawable.failure_image);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    public void enterUserFirebase(String Userid) {
        DocumentReference docRef = db.collection("Users").document(Userid);
        docRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);

            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        FollowModel followModel = new FollowModel();
                        followModel.setUserEmailId(document.getString("UserEmailId"));
                        followModel.setUserFirstName(document.getString("UserFirstName"));
                        followModel.setUserLastName(document.getString("UserLastName"));
                        String ImageUrl = document.getString("UserImageUrl");
                        if (ImageUrl != null) {
                            followModel.setUserImageUrl(ImageUrl);
                        }
                        String data = followModel.getUserEmailId();
                        if (StoreFollow.contains(data)) {
                            followModel.setFolloers(true);
                        } else {
                            followModel.setFolloers(false);
                        }
                        SenDataToFollowing.add(followModel);
                    }
                }
                adapter = new PagerAdapterInviteFriendsScreen(mFragmentManager, mTabLayout.getTabCount(), SenDataToFollow, SenDataToFollowing);
                mViewPager.setAdapter(adapter);
            }
        });
    }

    private void initView(View rootView) {
        mViewPager = rootView.findViewById(R.id.ViewPages);
        mTabLayout = rootView.findViewById(R.id.tab_layout);
    }

    TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };


}
