package in.org.eonline.eblog.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.org.eonline.eblog.Adapters.BlogAdapter;
import in.org.eonline.eblog.Adapters.UserAdapter;
import in.org.eonline.eblog.Activities.BlogActivity;
import in.org.eonline.eblog.HomeActivity;
import in.org.eonline.eblog.Models.BlogModel;
import in.org.eonline.eblog.Models.UserModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;
import in.org.eonline.eblog.Utilities.FontClass;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment implements UserAdapter.ClickListener, BlogAdapter.ClickListener {

    FirebaseFirestore db;
    private List<BlogModel> blogModelsList = new ArrayList<>();
    private List<UserModel> userModelsList = new ArrayList<>();
    BlogModel blogModel;
    UserModel userModel;
    private AdView mAdView;
    private RecyclerView popularBlogsRecyclerView;
    private List<UserModel> userModels = new ArrayList<>();
    private List<BlogModel> blogModels = new ArrayList<>();
    FirebaseStorage storage;
    StorageReference storageRef;
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    public Dialog dialog;
    public SwipeRefreshLayout mySwipeRequestLayout;
    View view;
    private List<BlogModel> blogListCategorywise = new ArrayList<>();
    private int length;
    boolean[] checkedSelectedArray = new boolean[22];
    private TextView filterBlogs;
    private InterstitialAd interstitialAd;

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        initializeViews();
        setDataFirebase();
        loadInterstitialAd();
        refreshMyProfile();

        ViewGroup myMostParentLayout = (ViewGroup) getView().findViewById(R.id.swiperefresh_home);
        FontClass.getInstance(getActivity()).setFontToAllChilds(myMostParentLayout);

        // Get the instance of Firebase storage
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();


        MobileAds.initialize(getContext(),"ca-app-pub-7293397784162310~9840078574");
        mAdView = (AdView) getView().findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        filterBlogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlertDialog();
            }
        });

    }

    public void initializeViews() {
        //popularUsersRecyclerView = (RecyclerView) getView().findViewById(R.id.popular_users);
        popularBlogsRecyclerView = (RecyclerView) getView().findViewById(R.id.popular_blogs);
        mySwipeRequestLayout=(SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh_home);
        filterBlogs = (TextView) getView().findViewById(R.id.filter_blogs);
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
       // getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag("nav_home");
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        blogModelsList.clear();
        ft.attach(frg);
        ft.commit();
    }

    public void setDataFirebase(){
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

    public void enterBlogsFirebase(){
        CollectionReference colRef=db.collection("Blogs");

        colRef.orderBy("BlogTimeStamp", Query.Direction.DESCENDING).get().addOnFailureListener(new OnFailureListener() {
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
                    int i=0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        i++;
                        if (document.exists()) {
                            setBlogModel(document);
                        } else {
                            CommonDialog.getInstance().showErrorDialog(getContext(), R.drawable.failure_image);
                        }
                    }
                    if(i==0) {
                        CommonDialog.getInstance().showErrorDialog(getContext(), R.drawable.no_data);
                    }
                    setPopularBlogsRecyclerView();
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

    public void setUserModel(DocumentSnapshot document) {
        //userModelsList = new ArrayList<>();
        userModel = new UserModel();
        userModel.setUserFName(document.getString("UserFirstName"));
        userModel.setUserLName(document.getString("UserLastName"));
        userModel.setUserEmail(document.getString("UserEmailId"));
        // blogModel.setBlogLikes(Integer.parseInt(document.getString("BlogLikes")));
        userModel.setUserContact(document.getString("UserContact"));
        userModelsList.add(userModel);
    }


    private void setBlogModel(QueryDocumentSnapshot doc) {
        //blogModelsList = new ArrayList<>();
        blogModel = new BlogModel();
        blogModel.setBlogHeader(doc.getString("BlogHeader"));
        blogModel.setBlogFooter(doc.getString("BlogFooter"));
        blogModel.setBlogContent1(doc.getString("BlogContent1"));
        blogModel.setBlogContent2(doc.getString("BlogContent2"));
        blogModel.setBlogLikes(doc.getString("BlogLikes"));
        blogModel.setBlogUser(doc.getString("BlogUser"));
        blogModel.setBlogCategory(doc.getString("BlogCategory"));
        blogModel.setBlogId(doc.getString("BlogId"));
        blogModel.setBannerAdMobId(doc.getString("BlogUserBannerId"));
        blogModel.setUserBlogImage1Url(doc.getString("BlogImage1Url"));
        blogModel.setUserBlogImage2Url(doc.getString("BlogImage2Url"));
        blogModel.setUserImageUrl(doc.getString("BlogUserImageUrl"));
        blogModel.setUserId(doc.getString("UserId"));
        blogModelsList.add(blogModel);
    }

    /*public void setPopularUsersRecyclerView() {
        popularUsersRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        popularUsersRecyclerView.setLayoutManager(linearLayoutManager);
        UserAdapter adapter = new UserAdapter(getActivity(),userModelsList , ExploreFragment.this);
        popularUsersRecyclerView.setAdapter(adapter);
    } */

    public void setPopularBlogsRecyclerView() {
        popularBlogsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        popularBlogsRecyclerView.setLayoutManager(linearLayoutManager);
        BlogAdapter adapter = new BlogAdapter(getActivity(),blogModelsList , ExploreFragment.this);
        popularBlogsRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClickItem(BlogModel model) {
        if(interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
        Intent intent = new Intent(getActivity(), BlogActivity.class);
        String blogmodel = (new Gson()).toJson(model);
        intent.putExtra("blog", blogmodel);
        getActivity().startActivity(intent);
    }

    public void setAlertDialog() {
        String abc[] = getActivity().getResources().getStringArray(R.array.blog_categories);
        final List<String> categories = Arrays.asList(abc);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        length = checkedSelectedArray.length;
        builder.setTitle("Select your category");
        builder.setMultiChoiceItems(abc, checkedSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                checkedSelectedArray[which] = isChecked;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                blogListCategorywise.clear();
                int size = blogModelsList.size();
                for (int j = 0 ; j < size; j++) //it will take all the blogs from the blogModelList for comparison
                {
                    for (int i = 0; i < length; i++) { // it will take all the categories from the dialog box for comparison
                        boolean checked = checkedSelectedArray[i];
                        if (checked) {//it will take the selected category from dialog box
                            String blogCategoryCheck = blogModelsList.get(j).getBlogCategory().toString();
                            String blogCategoryFromDialog = categories.get(i).toString();
                            if (blogCategoryCheck.equals(blogCategoryFromDialog)) {//it will compare the category and the blogmodel list
                                setBlogModelFromCategory(blogModelsList.get(j));// it will set the filtered list

                            }
                        }
                    }
                }
                if(blogListCategorywise.size()==0) {
                    CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_data);
                }
                setBlogsRecyclerViewFromCategory();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) { //logic for cancelling

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setBlogModelFromCategory(BlogModel blogModelFromDialog) {
        blogModel = new BlogModel();
        blogModel.setBlogHeader(blogModelFromDialog.getBlogHeader());
        blogModel.setBlogFooter(blogModelFromDialog.getBlogFooter());
        blogModel.setBlogContent1(blogModelFromDialog.getBlogContent1());
        blogModel.setBlogContent2(blogModelFromDialog.getBlogContent2());
        blogModel.setBlogLikes(blogModelFromDialog.getBlogLikes());
        blogModel.setBlogUser(blogModelFromDialog.getBlogUser());
        blogModel.setBlogCategory(blogModelFromDialog.getBlogCategory());
        blogModel.setBlogId(blogModelFromDialog.getBlogId());
        blogModel.setBannerAdMobId(blogModelFromDialog.getBannerAdMobId());
        blogModel.setUserBlogImage1Url(blogModelFromDialog.getUserBlogImage1Url());
        blogModel.setUserBlogImage2Url(blogModelFromDialog.getUserBlogImage2Url());
        blogModel.setUserImageUrl(blogModelFromDialog.getUserImageUrl());
        blogModel.setUserId(blogModelFromDialog.getUserId());
        blogListCategorywise.add(blogModel);
    }

    public void setBlogsRecyclerViewFromCategory() { // for populating the recycler view as per the filtered categories
        popularBlogsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        popularBlogsRecyclerView.setLayoutManager(linearLayoutManager);
        BlogAdapter adapter = new BlogAdapter(getActivity(), blogListCategorywise, ExploreFragment.this);
        popularBlogsRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(requireContext());
        interstitialAd.setAdUnitId("ca-app-pub-7293397784162310/3163493818");
        AdRequest request = new AdRequest.Builder().build();
        interstitialAd.loadAd(request);
    }

}
