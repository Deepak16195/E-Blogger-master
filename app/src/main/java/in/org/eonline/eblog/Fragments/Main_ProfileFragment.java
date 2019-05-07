package in.org.eonline.eblog.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import in.org.eonline.eblog.Activities.BlogActivity;
import in.org.eonline.eblog.Activities.MyProfileAcivity;
import in.org.eonline.eblog.Adapters.BlogAdapter;
import in.org.eonline.eblog.Models.BlogModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.SQLite.DatabaseHelper;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;

/**
 * A simple {@link Fragment} subclass.
 */
public class Main_ProfileFragment extends Fragment implements BlogAdapter.ClickListener {

    FirebaseFirestore db;
    private TextView blogHeaderTextView, Usename, Blogcount,followingcount,followcount;
    private String blogHeader;
    private TextView blogContentTextView;
    private String blogContent;
    private TextView blogFooterTextView, Tx_Edit;
    private String blogFooter;
    private String userName;
    private String blogLikes;
    BlogModel blogModel = new BlogModel();
    private RecyclerView yourBlogsRecyclerView;
    private List<BlogModel> blogModelsList = new ArrayList<>();
    private static final String TAG = "FireLog";
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs_new";
    private String userId;
    private String blogId, userFullName;
    DatabaseHelper sqliteDatabaseHelper;
    private AdView mAdView;
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    public Dialog dialog;
    FirebaseStorage storage;
    StorageReference storageRef;
    RoundedImageView User_image;
    ArrayList<String> Followers = new ArrayList<String>();
    ArrayList<String> Followings = new ArrayList<String>();
    public Main_ProfileFragment() {
        // Required empty public constructor
    }

    public static Main_ProfileFragment newInstance() {
        Main_ProfileFragment fragment = new Main_ProfileFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_main__profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString("UserIdCreated", "Deepak9702173103");
        userFullName = sharedpreferences.getString("UserNameCreated", "Deepak9702173103");
        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                userId = bundle.getString("UserId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sqliteDatabaseHelper = new DatabaseHelper(getActivity());
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        InitializeViews();
        setYourBlogsFromFirebase();
      /*  MobileAds.initialize(getContext(),"ca-app-pub-7722811932766421~9001519486");
        mAdView = (AdView) getView().findViewById(R.id.yourBlogs_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
        storage = FirebaseStorage.getInstance();
        Usename.setText(userFullName + "");

        Tx_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileAcivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        User_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileAcivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        downloadImageFromFirebaseStorage();

        db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> arrayList = (ArrayList<String>) document.get("AllFollowing");
                        if (arrayList == null) {
                            arrayList = new ArrayList<>();
                        } else {
                            if (arrayList.size() > 0) {
                                for (String s : arrayList) {
                                    Followings.add(s);
                                }
                            }
                        }
                        ArrayList<String> arrayList2 = (ArrayList<String>) document.get("AllFollow");
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList<>();
                        } else {
                            if (arrayList2.size() > 0) {
                                for (String s : arrayList2) {
                                    Followers.add(s);
                                }
                            }
                        }

                        followcount.setText(Followers.size()+" ");
                        followingcount.setText(Followings.size()+" ");


                    }
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
                //userModel.setUserImage(uri.toString());
                Glide.with(getActivity())
                        .load(uri)
                        .into(User_image);
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                dialog.dismiss();
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
            enterBlogsFirebase();
        } else {
            CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);
        }
    }

    public void enterBlogsFirebase() {
        CollectionReference blogRef = db.collection("Users").document(userId).collection("Blogs");
        blogRef.get().addOnFailureListener(new OnFailureListener() {
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
                        //  CommonDialog.getInstance().showErrorDialog(getContext(), R.drawable.no_data);
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
        blogModel = new BlogModel();
        blogModel.setBlogHeader(document.getString("BlogHeader"));
        blogModel.setBlogFooter(document.getString("BlogFooter"));
        blogModel.setBlogContent1(document.getString("BlogContent1"));
        blogModel.setBlogContent2(document.getString("BlogContent2"));
        blogModel.setBlogLikes(document.getString("BlogLikes"));
        blogModel.setBlogUser(document.getString("BlogUser"));
        blogModel.setBlogCategory(document.getString("BlogCategory"));
        blogModel.setBlogId(document.getString("BlogId"));
        blogModel.setBannerAdMobId(document.getString("BlogUserBannerId"));
        blogModel.setUserBlogImage1Url(document.getString("BlogImage1Url"));
        blogModel.setUserBlogImage2Url(document.getString("BlogImage2Url"));
        blogModel.setUserImageUrl(document.getString("BlogUserImageUrl"));
        blogModel.setUserId(document.getString("UserId"));
        if (document.getString("Views") != null) {
            blogModel.setViews("" + document.getString("Views"));
        }
        if (document.getString("BlogYoutubeUrl") != null) {
            blogModel.setYouTubeLinks(document.getString("BlogYoutubeUrl"));
        }
        if (document.exists()) {
            ArrayList<String> arrayList = (ArrayList<String>) document.get("AllComments");
            if (arrayList == null) {
                arrayList = new ArrayList<>();
            }
            blogModel.setAllComments(arrayList);
        }
        blogModelsList.add(blogModel);
        // setPopularBlogsRecyclerView();
    }

    public void InitializeViews() {
        yourBlogsRecyclerView = (RecyclerView) getView().findViewById(R.id.your_blogs);
        blogHeaderTextView = (TextView) getView().findViewById(R.id.blog_header_text);
        Usename = (TextView) getView().findViewById(R.id.Usename);
        blogContentTextView = (TextView) getView().findViewById(R.id.blog_content_text);
        blogFooterTextView = (TextView) getView().findViewById(R.id.blog_footer_text);
        Tx_Edit = (TextView) getView().findViewById(R.id.Tx_Edit);
        Blogcount = (TextView) getView().findViewById(R.id.Blogcount);
        followingcount = (TextView) getView().findViewById(R.id.followingcount);
        followcount = (TextView) getView().findViewById(R.id.followcount);
        User_image = (RoundedImageView) getView().findViewById(R.id.User_image);
    }

    public void setPopularBlogsRecyclerView() {
        yourBlogsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        yourBlogsRecyclerView.setLayoutManager(linearLayoutManager);
        BlogAdapter adapter = new BlogAdapter(getActivity(), blogModelsList, Main_ProfileFragment.this);
        yourBlogsRecyclerView.setAdapter(adapter);
        Blogcount.setText(" Blogs( " + blogModelsList.size() + " )");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClickItem(BlogModel model) {
        Intent intent = new Intent(getActivity(), BlogActivity.class);
        String blogmodel = (new Gson()).toJson(model);
        intent.putExtra("blog", blogmodel);
        getActivity().startActivity(intent);

    }


}
