package in.org.eonline.eblog.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import in.org.eonline.eblog.Adapters.BlogAdapter;
import in.org.eonline.eblog.Models.BlogModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.SQLite.DatabaseHelper;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;

public class Show_ProfileActivity extends AppCompatActivity implements BlogAdapter.ClickListener {

    private LinearLayout linearLayout;
    private RoundedImageView blogImageItem;
    private TextView TxEdit;
    private TextView Usename;
    private Button Mobile;
    private TextView Blogcount;
    private TextView blogHeaderText;
    private TextView blogContentText;
    private TextView blogFooterText;
    private RecyclerView yourBlogs;
    FirebaseFirestore db;
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
    String[] userIdBlog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__profile);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString("UserIdCreated", "Deepak9702173103");
        userFullName = sharedpreferences.getString("UserNameCreated", "Deepak9702173103");
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                userId = bundle.getString("UserId");
                userFullName = bundle.getString("UserName");
                userIdBlog = userId.split("\\_");
                userId = userIdBlog[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sqliteDatabaseHelper = new DatabaseHelper(Show_ProfileActivity.this);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        initView();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setYourBlogsFromFirebase();
      /*  MobileAds.initialize(getContext(),"ca-app-pub-7722811932766421~9001519486");
        mAdView = (AdView) getView().findViewById(R.id.yourBlogs_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        Usename.setText(userFullName + "");

        TxEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Show_ProfileActivity.this, MyProfileAcivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


    }

    private void initView() {
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        blogImageItem = (RoundedImageView) findViewById(R.id.blog_image_item);
        TxEdit = (TextView) findViewById(R.id.Tx_Edit);
        Usename = (TextView) findViewById(R.id.Usename);
        Mobile = (Button) findViewById(R.id.Mobile);
        Blogcount = (TextView) findViewById(R.id.Blogcount);
        blogHeaderText = (TextView) findViewById(R.id.blog_header_text);
        blogContentText = (TextView) findViewById(R.id.blog_content_text);
        blogFooterText = (TextView) findViewById(R.id.blog_footer_text);
        yourBlogsRecyclerView = (RecyclerView) findViewById(R.id.your_blogs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }


    public void setYourBlogsFromFirebase() {
        connectivityReceiver = new ConnectivityReceiver(Show_ProfileActivity.this);
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {
            dialog = CommonDialog.getInstance().showProgressDialog(Show_ProfileActivity.this);
            dialog.show();
            enterBlogsFirebase();
        } else {
            CommonDialog.getInstance().showErrorDialog(Show_ProfileActivity.this, R.drawable.no_internet);
        }
    }

    public void enterBlogsFirebase() {
        CollectionReference blogRef = db.collection("Users").document(userId).collection("Blogs");
        blogRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonDialog.getInstance().showErrorDialog(Show_ProfileActivity.this, R.drawable.failure_image);
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
                          CommonDialog.getInstance().showErrorDialog(Show_ProfileActivity.this, R.drawable.no_data);
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


    public void setPopularBlogsRecyclerView() {
        yourBlogsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Show_ProfileActivity.this, LinearLayoutManager.VERTICAL, false);
        yourBlogsRecyclerView.setLayoutManager(linearLayoutManager);
        BlogAdapter adapter = new BlogAdapter(Show_ProfileActivity.this, blogModelsList, Show_ProfileActivity.this);
        yourBlogsRecyclerView.setAdapter(adapter);
        Blogcount.setText(" Blogs( " + blogModelsList.size() + " )");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClickItem(BlogModel model) {
        Intent intent = new Intent(Show_ProfileActivity.this, BlogActivity.class);
        String blogmodel = (new Gson()).toJson(model);
        intent.putExtra("blog", blogmodel);
        startActivity(intent);

    }
}
