package in.org.eonline.eblog.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.org.eonline.eblog.Adapters.BlogAdapter;
import in.org.eonline.eblog.Activities.BlogActivity;
import in.org.eonline.eblog.Models.BlogModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment implements BlogAdapter.ClickListener {

    FirebaseFirestore db;
    private Button button;
    BlogModel blogModel = new BlogModel();
    private RecyclerView yourBlogsRecyclerView;
    private List<BlogModel> blogModelsList = new ArrayList<>();
    private List<BlogModel> blogListCategorywise = new ArrayList<>();
    private int length;
    boolean[] checkedSelectedArray = new boolean[11];
    private AdView mAdView;
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    public SwipeRefreshLayout mySwipeRequestLayout;
    public Dialog dialog;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db= FirebaseFirestore.getInstance();
        initializeViews();
        refreshMyProfile();
        connectivityReceiver = new ConnectivityReceiver(getActivity());
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {
            dialog = CommonDialog.getInstance().showProgressDialog(getActivity());
            dialog.show();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isInternetPresent) {
                        setAlertDialog();
                    }
                    else
                    {
                        CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);
                    }
                }
            });
        } else {
            CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);
            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }
        setYourBlogsFromFirebase();




        MobileAds.initialize(getContext(),"ca-app-pub-7293397784162310~9840078574");
        mAdView = (AdView) getView().findViewById(R.id.tasks_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void initializeViews() {

        yourBlogsRecyclerView = (RecyclerView) getView().findViewById(R.id.your_blogs);
        button = (Button) getView().findViewById(R.id.submit_filter_button);
        mySwipeRequestLayout =(SwipeRefreshLayout)getView().findViewById(R.id.swiperefresh_task);
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
        frg = getFragmentManager().findFragmentByTag("nav_task");
        final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        blogModelsList.clear();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }


    public void setAlertDialog() {
        String abc[] = {"Travelling", "Food", "Cosmetics", "Apparels", "Technology", "Cars and Bikes", "Politics", "Socialism", "Bollywood and entertainment", "Business", "others"};
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
                if(blogListCategorywise.size()==0)
                {
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

    public void setYourBlogsFromFirebase() {
        if (isInternetPresent) {

            enterFirebase();
        } else {
            CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);
            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }

    }

    public  void enterFirebase(){
        CollectionReference blogRef =db.collection("Blogs");

        blogRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i =0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists())
                                    setBlogModel(document);
                                i++;
                            }
                            if(i==0)
                            {
                                CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_data);
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                            setBlogsRecyclerView();
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } else {
                            CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }
                });
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
        blogModelsList.add(blogModel);
    }

    public void setBlogsRecyclerView() { //for populating the recycler view from firebase data
        yourBlogsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        yourBlogsRecyclerView.setLayoutManager(linearLayoutManager);
        BlogAdapter adapter = new BlogAdapter(getActivity(), blogModelsList, TaskFragment.this);
        yourBlogsRecyclerView.setAdapter(adapter);


    }

    public void setBlogsRecyclerViewFromCategory() { // for populating the recycler view as per the filtered categories
        yourBlogsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        yourBlogsRecyclerView.setLayoutManager(linearLayoutManager);
        BlogAdapter adapter = new BlogAdapter(getActivity(), blogListCategorywise, TaskFragment.this);
        yourBlogsRecyclerView.setAdapter(adapter);


    }

    @Override
    public void onClickItem(BlogModel model) {
        Intent intent = new Intent(getActivity(), BlogActivity.class);
        String blogmodel = (new Gson()).toJson(model);
        intent.putExtra("blog", blogmodel);
        getActivity().startActivity(intent);

    }
}