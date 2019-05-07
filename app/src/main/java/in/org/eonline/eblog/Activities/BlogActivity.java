package in.org.eonline.eblog.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.org.eonline.eblog.Adapters.All_Comments_Adapter;
import in.org.eonline.eblog.HomeActivity;
import in.org.eonline.eblog.Models.BlogModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;
import in.org.eonline.eblog.Utilities.FontClass;

import static com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import static com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import static com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import static com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import static com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import static com.google.android.youtube.player.YouTubePlayer.Provider;

import static android.content.ContentValues.TAG;

public class BlogActivity extends AppCompatActivity implements OnInitializedListener {
    private AppCompatDelegate delegate;
    private TextView blogHeader, blogContent1, blogContent2, blogFooter, blogCategory, blogLikes, blogShare, UserName, no_of_comments;
    private String bannerId, userId, likeStatus, likeButtonValue, FirstName;
    private ImageView blogImageView1, blogImageView2, monetizationIcon, userLikesButton;
    private Button deleteBlog, updateBlog;
    private String blogId;
    FirebaseFirestore db;
    Map<String, String> userReadBlogMap = new HashMap<>();
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs_new";
    private BlogModel blogModel;
    FrameLayout frameLayout;
    String[] userIdBlog;
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    public SwipeRefreshLayout mySwipeRequestLayout;
    public Dialog dialog;
    FirebaseStorage storage;
    StorageReference storageRef;
    private String shareLink = "";
    ImageView user_profile_image;
    CollectionReference complaintsRef;
    Map<String, Object> blogMap = new HashMap<>();
    BlogModel blogModelToBeUpdated = new BlogModel();
    MenuItem user_Like_button;
    String likeStatusFirt, SendNotiFicationUserID = " ";
    public String VideoPathurl, videoUrl;
    public static final String API_KEY = "AIzaSyBx7v0YOb140fDO7EbfMx4l87raxezDWFw";
    //YouTubePlayerView youTubePlayerView;
    //https://www.youtube.com/watch?v=<VIDEO_ID>
    public String VIDEO_ID = "RyZZD2w9OOc";
    FrameLayout YoutubeFame;
    public final static String AUTH_KEY_FCM = "AAAAMI0TpYQ:APA91bFYRhC6OJSFQZkesXLLPdd08ynh2iMBanhUKodp-wqnyERCVqp48Xbh6DWLWqOqqBKkQ5ZNYmfMG9HSCuSAgGIul5Y4QzdZi4JoUYwYsiu5-QiMv0Lz1_k1LdeRC2qUw-_zU6m31SC5zO1xlN6sGoKTS9jj6w";
    Map<String, Object> NotifactionblogMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To make activity Full Screen
        setContentView(R.layout.activity_blog);
        //super.onCreate(savedInstanceState);
        //we need to call the onCreate() of the AppCompatDelegate
        InitializeViews();
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
        ViewGroup myMostParentLayout = (ViewGroup) findViewById(R.id.blogActivity);
        FontClass.getInstance(BlogActivity.this).setFontToAllChilds(myMostParentLayout);
        refreshMyProfile();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        storage = FirebaseStorage.getInstance();
        sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString("UserIdCreated", "Deepak");
        FirstName = sharedpreferences.getString("UserFirstName", "Deepak");

        if (getIntent().hasExtra("blog")) {
            blogModel = new Gson().fromJson(getIntent().getStringExtra("blog"), BlogModel.class);
            connectivityReceiver = new ConnectivityReceiver(this);
            // Initialize SDK before setContentView(Layout ID)
            isInternetPresent = connectivityReceiver.isConnectingToInternet();
            if (isInternetPresent) {
                dialog = CommonDialog.getInstance().showProgressDialog(this);
                dialog.show();
                setBlogDataAndImage();

            } else {
                CommonDialog.getInstance().showErrorDialog(this, R.drawable.no_internet);
                //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
            }

            if (blogModel.getUserId().equalsIgnoreCase(userId)) {
                deleteBlog.setVisibility(View.VISIBLE);
                deleteBlog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog = CommonDialog.getInstance().showProgressDialog(BlogActivity.this);
                        dialog.show();
                        deleteBlog();
                    }
                });

                updateBlog.setVisibility(View.VISIBLE);
                updateBlog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Bundle bundle = new Bundle();
                        String blogmodel = (new Gson()).toJson(blogModel);
                        bundle.putString("update_blog", blogmodel);
                        CreateNewBlogFragment createNewBlogFragment = new CreateNewBlogFragment();
                        createNewBlogFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, createNewBlogFragment).commit(); */

                        Intent intent = new Intent(BlogActivity.this, HomeActivity.class);
                        String blogmodel = (new Gson()).toJson(blogModel);
                        intent.putExtra("update_blog", blogmodel);
                        intent.putExtra("update_key", "blog_update");
                        startActivity(intent);
                    }
                });
            } else {
                deleteBlog.setVisibility(View.GONE);
                updateBlog.setVisibility(View.GONE);
            }
        }

        shareBlog();


        UserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BlogActivity.this, VideoActivity.class);
                String blogmodel = (new Gson()).toJson(blogModel);
                intent.putExtra("VideoActivity.URL", "https://content.jwplatform.com/manifests/yp34SRmf.m3u8");
                startActivity(intent);


            }
        });


// Initializing YouTube player view


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog, menu);
        super.onCreateOptionsMenu(menu);
        user_Like_button = menu.findItem(R.id.action_heart);
        CheckLikes(blogModel);
        return true;
    }


    public void UpdateViews(BlogModel blogModel) {
        if (blogModel.getViews() == null) {
            blogModel.setViews("0");
        }
        int Views = Integer.valueOf(blogModel.getViews());
        Views++;
        int bloglikesNew = Integer.parseInt(blogModel.getBlogLikes());
        String blogLikesString = Integer.toString(bloglikesNew);
        blogModel.setBlogLikes(blogLikesString);
        userReadBlogMap.put("LikeStatus", likeStatusFirt);
        userReadBlogMap.put("BlogLikes", blogLikesString);
        userReadBlogMap.put("Views", Views + "");
        DocumentReference userDoc = db.collection("Blogs").document(blogId);
        userDoc.update("Views", Views + "")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
        DocumentReference userBlog = db.collection("Users").document(userIdBlog[0]).collection("Blogs").document(blogId);
        userBlog.update("Views", Views + "")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
        db.collection("Users").document(userId).collection("UserReadBlogs").document(blogId).set(userReadBlogMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle item selection
        switch (item.getItemId()) {
            case R.id.action_comment:

                Intent intent = new Intent(BlogActivity.this, CommentActivity.class);
                String blogmodel = (new Gson()).toJson(blogModel);
                intent.putExtra("blog", blogmodel);
                startActivity(intent);
                return true;
            case R.id.action_heart:

                userLikesButton.performClick();

                //  pushFCMNotification("eEQs9nBGK5M:APA91bGPORlGB5-Lp6ApQPleaM1-q2UyWHI-6BABKOdKIKBfF-nhfaQRv0eeDzJU2dhxgNshobzMDwDc5r8XXbUD5YSgM9tF3lUWpMebq9paaa42j6dkRHFKNP_3NKjXCB6rZy6OWBbu");


                return true;


            case R.id.action_add_contact:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                //i.setType("image*//*");
                //i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                //i.putExtra(Intent.EXTRA_TEXT, FullName + " has invited you to the most exciting event " + explorePojo.getChatTitle() + " organized by " + explorePojo.getEventOwner() + " at " + explorePojo.getEventLocation() + "." + " Now join it via " + "https://play.google.com/store/apps/details?id=com.temp.tempdesign&hl=en");
                i.putExtra(Intent.EXTRA_TEXT, blogModel.getBlogHeader() + " by " + blogModel.getBlogUser() + ". Read this blog here: " + shareLink);
                startActivity(Intent.createChooser(i, "Share This Blog"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void setBlogDataAndImage() {
        blogHeader.setText(blogModel.getBlogHeader());
        blogHeader.setTypeface(blogHeader.getTypeface(), Typeface.BOLD);
        blogContent1.setText(blogModel.getBlogContent1());
        blogContent2.setText(blogModel.getBlogContent2());
        blogFooter.setText(blogModel.getBlogFooter());
        blogFooter.setTypeface(blogFooter.getTypeface(), Typeface.BOLD);
        blogCategory.setText(blogModel.getBlogCategory());
        blogId = blogModel.getBlogId();
        blogLikes.setText(blogModel.getBlogLikes() + " ");
        UserName.setText(blogModel.getBlogUser().toString() + " ");
        bannerId = blogModel.getBannerAdMobId();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_user_dummy);
        requestOptions.error(R.drawable.ic_user_dummy);

        Glide.with(BlogActivity.this)
                .load(blogModel.getUserBlogImage1Url())
                .into(blogImageView1);
        Glide.with(BlogActivity.this)
                .load(blogModel.getUserImageUrl())
                .apply(requestOptions)
                .into(user_profile_image);

        Glide.with(BlogActivity.this)
                .load(blogModel.getUserBlogImage2Url())
                .into(blogImageView2);

        userIdBlog = blogId.split("\\_");

        if (bannerId != null && !blogModel.getUserId().equalsIgnoreCase(userId)) {
            View adContainer = findViewById(R.id.blogAdMobView);
            AdView userAdView = new AdView(this);
            userAdView.setAdSize(AdSize.SMART_BANNER);
            userAdView.setAdUnitId(bannerId);
            ((RelativeLayout) adContainer).addView(userAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            userAdView.loadAd(adRequest);
        }

        if (blogModel.getUserId().equalsIgnoreCase(userId) && (blogModel.getBannerAdMobId() == null || !blogModel.getBannerAdMobId().contains("ca-app-pub"))) {
            monetizationIcon.setVisibility(View.VISIBLE);
            ImageViewCompat.setImageTintList(monetizationIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
            //monetizationIcon.setTooltipText("This blog is not monetized");
            monetizationIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialog();
                }
            });
        } else if (blogModel.getUserId().equalsIgnoreCase(userId) && (blogModel.getBannerAdMobId() != null || blogModel.getBannerAdMobId().contains("ca-app-pub"))) {
            monetizationIcon.setVisibility(View.VISIBLE);
            ImageViewCompat.setImageTintList(monetizationIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)));
            //monetizationIcon.setTooltipText("This blog is monetized");
            monetizationIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(BlogActivity.this, "This blog is monetized", Toast.LENGTH_SHORT).show();
                }
            });
        }


        if (blogModel.getYouTubeLinks() != null) {
            connectivityReceiver = new ConnectivityReceiver(this);
            // Initialize SDK before setContentView(Layout ID)
            isInternetPresent = connectivityReceiver.isConnectingToInternet();
            if (isInternetPresent) {

                // no_of_comments.setText(blogModel.getYouTubeLinks() + " ");
                VideoPathurl = blogModel.getYouTubeLinks();
                try {
                    VIDEO_ID = extractYoutubeId(VideoPathurl);
                    VIDEO_ID = getYoutubeVideoId(VideoPathurl);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                if (VIDEO_ID != null) {

                    YoutubeFame.setVisibility(View.VISIBLE);
                    YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                 /*   FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_fragment, youTubePlayerFragment);
                    transaction.commit();*/

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_fragment, youTubePlayerFragment)
                            .commit();

                    youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {

                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                            if (!b) {
                                youTubePlayer = youTubePlayer;
                                youTubePlayer.setFullscreen(false);
                                youTubePlayer.loadVideo(VIDEO_ID);
                                youTubePlayer.play();
                            }
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                            // TODO Auto-generated method stub

                        }
                    });


                  /*
                    youTubePlayerView.initialize(API_KEY, this);*/


                }


            } else {

                CommonDialog.getInstance().showErrorDialog(this, R.drawable.no_internet);
                //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
            }


        }


        getAppDownloadLink();
    }

    public String extractYoutubeId(String url) throws MalformedURLException {
        String id = null;
        try {
            String query = new URL(url).getQuery();
            if (query != null) {
                String[] param = query.split("&");
                for (String row : param) {
                    String[] param1 = row.split("=");
                    if (param1[0].equals("v")) {
                        id = param1[1];
                    }
                }
            } else {
                if (url.contains("embed")) {
                    id = url.substring(url.lastIndexOf("/") + 1);
                }
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
        }
        return id;
    }


    public  String getYoutubeVideoId(String youtubeUrl)
    {
        String video_id="";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http"))
        {

            String expression = "^.*((youtu.be"+ "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches())
            {
                String groupIndex1 = matcher.group(7);
                if(groupIndex1!=null && groupIndex1.length()==11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }


    public void createDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("This blog is not monetized");
        alertDialogBuilder.setMessage("Click on 'Redirect To Monetization' button and enter your Google AdMob Banner Id. Once you have entered it in Monetization screen then update this blog to reflect changes.");

        alertDialogBuilder.setPositiveButton("Redirect To Monetization", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(BlogActivity.this, HomeActivity.class);
                intent.putExtra("monetize_key", "open_monetization");
                startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void InitializeViews() {
        UserName = findViewById(R.id.UserName);
        blogHeader = findViewById(R.id.user_blog_header_text);
        blogContent1 = findViewById(R.id.user_blog_content_text1);
        blogContent2 = findViewById(R.id.user_blog_content_text2);
        blogFooter = findViewById(R.id.user_blog_footer_text);
        blogCategory = findViewById(R.id.user_blog_category);
        blogLikes = findViewById(R.id.user_blog_likes);
        user_profile_image = findViewById(R.id.user_profile_image);
        userLikesButton = (ImageView) findViewById(R.id.user_blog_button);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        YoutubeFame = (FrameLayout) findViewById(R.id.frame_fragment);
        blogImageView1 = (ImageView) findViewById(R.id.blog_image_activity_1);
        blogImageView2 = (ImageView) findViewById(R.id.blog_image_activity_2);
        deleteBlog = (Button) findViewById(R.id.delete_blog);
        mySwipeRequestLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_blog_activity);
        updateBlog = (Button) findViewById(R.id.update_blog);
        blogShare = (TextView) findViewById(R.id.user_share_blog);
        monetizationIcon = (ImageView) findViewById(R.id.monetization_icon);
        no_of_comments = (TextView) findViewById(R.id.no_of_comments);

        //youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);
    }

    public void refreshMyProfile() {
        mySwipeRequestLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshOperation();
                mySwipeRequestLayout.setRefreshing(false);
            }
        });
    }

    public void onRefreshOperation() {
        //recreate();
        finish();
        startActivity(getIntent());
    }

    public void CheckLikes(final BlogModel blogModel) {
        CollectionReference blogRef = db.collection("Users").document(userId).collection("UserReadBlogs");
        blogRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot documentSize = task.getResult();
                    if (documentSize.size() > 0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docid = document.getId();
                            String likeStatusFirts = document.getString("LikeStatus");
                            if (likeStatusFirts == null) {
                                likeStatusFirts = "false";
                            }
                            if (docid.equals(blogId)) {
                                if (likeStatusFirts.toString().equals("true")) {
                                    likeStatusFirt = "true";
                                    // userLikesButton.setBackgroundTintList(getResources().getColorStateList(R.color.like));
                                    user_Like_button.setIcon(R.drawable.uncards_heart);
                                    likeButtonValue = "LIKED";
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    break;
                                } else {
                                    likeStatusFirt = "false";
                                    // userLikesButton.setBackgroundTintList(getResources().getColorStateList(R.color.black));
                                    user_Like_button.setIcon(R.drawable.heart);
                                    likeButtonValue = "LIKE";
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                }
                            } else {
                                //userLikesButton.setBackgroundTintList(getResources().getColorStateList(R.color.black));
                                user_Like_button.setIcon(R.drawable.heart);
                                likeButtonValue = "LIKE";
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }

                        UpdateViews(blogModel);

                    } else {
                        //userLikesButton.setBackgroundTintList(getResources().getColorStateList(R.color.black));
                        user_Like_button.setIcon(R.drawable.heart);
                        likeButtonValue = "LIKE";
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        likeButtonValue = "LIKE";
                        //  userLikesButton.setBackgroundTintList(getResources().getColorStateList(R.color.black));
                        user_Like_button.setIcon(R.drawable.heart_on);
                        // user_Like_button.setBackgroundColor(getResources().getColor(R.color.white));
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                });
            }
        });


        connectivityReceiver = new ConnectivityReceiver(this);
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {
            userLikeButtonLogic();
        } else {
            CommonDialog.getInstance().showErrorDialog(this, R.drawable.no_internet);
            //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }


    }

    public void userLikeButtonLogic() {
        userLikesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectivityReceiver = new ConnectivityReceiver(BlogActivity.this);
                // Initialize SDK before setContentView(Layout ID)
                isInternetPresent = connectivityReceiver.isConnectingToInternet();
                if (isInternetPresent) {
                    if (likeButtonValue == "LIKE") {
                        AddUserBlogMap(blogModel);

                    } else {
                        int bloglikesNew = Integer.parseInt(blogModel.getBlogLikes()) - 1;
                        user_Like_button.setIcon(R.drawable.heart);
                        likeButtonValue = "LIKE";
                        likeStatus = "false";
                        String blogLikesNewString = Integer.toString(bloglikesNew);
                        blogModel.setBlogLikes(blogLikesNewString);
                        blogLikes.setText(blogLikesNewString + "");
                        //  String blogLikesNewString = Integer.toString(bloglikesNew);
                        // blogModel.setBlogLikes(blogLikesNewString);
                        //DocumentReference userDoc = db.collection("Blogs").document(blogId);
                        userReadBlogMap.put("LikeStatus", likeStatus);
                        userReadBlogMap.put("BlogLikes", blogLikesNewString);
                        db.collection("Users").document(userId).collection("UserReadBlogs").document(blogId).set(userReadBlogMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                      /*  NotifactionblogMap.put("BlogId",blogModel.getBlogId());
                                        NotifactionblogMap.put("Userid",userId);
                                        NotifactionblogMap.put("LikeTimeStamp", FieldValue.serverTimestamp());
                                        NotifactionblogMap.put("NotifactionData",FirstName + " DisLiked Your Blogs");
                                        db.collection("Notification").document(userIdBlog[0]).collection("AllNotification").document(userId+"_"+blogId+" _Dislike").set(NotifactionblogMap, SetOptions.merge())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //Toast.makeText(getActivity(), "New Blog is created in Users collection", Toast.LENGTH_SHORT).show();
                                                        SendNotifactionifDisLike();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                    }
                                                });*/
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                });
                        DocumentReference userBlog = db.collection("Users").document(userIdBlog[0]).collection("Blogs").document(blogId);
                        userBlog.update("BlogLikes", blogLikesNewString)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                        CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                });
                        DocumentReference userDoc = db.collection("Blogs").document(blogId);
                        userDoc.update("BlogLikes", blogLikesNewString)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                        CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                });


                    }
                } else {
                    CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.no_internet);
                }
            }

        });
    }

    public void AddUserBlogMap(BlogModel blogModel) {
        likeStatus = "true";
        user_Like_button.setIcon(R.drawable.uncards_heart);
        likeButtonValue = "LIKED";
        int bloglikesNew = Integer.parseInt(blogModel.getBlogLikes()) + 1;
        String blogLikesString = Integer.toString(bloglikesNew);
        blogLikes.setText(blogLikesString + "Likes");
        blogModel.setBlogLikes(blogLikesString);
        userReadBlogMap.put("LikeStatus", likeStatus);
        userReadBlogMap.put("BlogLikes", blogLikesString);
        DocumentReference userDoc = db.collection("Blogs").document(blogId);
        userDoc.update("BlogLikes", blogLikesString)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

        DocumentReference userBlog = db.collection("Users").document(userIdBlog[0]).collection("Blogs").document(blogId);
        userBlog.update("BlogLikes", blogLikesString)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
        db.collection("Users").document(userId).collection("UserReadBlogs").document(blogId).set(userReadBlogMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if (userId.equalsIgnoreCase(userIdBlog[0])) {


                        } else {
                            NotifactionblogMap.put("BlogId", blogId);
                            NotifactionblogMap.put("Userid", userId);
                            NotifactionblogMap.put("LikeTimeStamp", FieldValue.serverTimestamp());
                            NotifactionblogMap.put("NotifactionData", FirstName + " Liked Your Blogs");
                            NotifactionblogMap.put("Views", false);
                            db.collection("Notification").document(userIdBlog[0]).collection("AllNotification").document(userId + "_" + blogId + " _ Liked").set(NotifactionblogMap, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Toast.makeText(getActivity(), "New Blog is created in Users collection", Toast.LENGTH_SHORT).show();
                                            SendNotifactionifLike();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });


    }

    public void deleteBlog() {
        if (blogModel.getUserBlogImage1Url() != null) {
            String url1 = "Blogs/" + userId + "/" + blogModel.getBlogId() + "img1";
            deleteBlogImages(url1);
        }
        if (blogModel.getUserBlogImage2Url() != null) {
            String url2 = "Blogs/" + userId + "/" + blogModel.getBlogId() + "img2";
            deleteBlogImages(url2);
        }

        /*String[] urls = new String[] {blogModel.getUserBlogImage1Url(), blogModel.getUserBlogImage2Url()};
        for(int i = 0; i < urls.length; i++) {
            deleteBlogImages(urls[i]);
        } */

        deleteBlogDocuments();
    }

    public void deleteBlogImages(String url) {
        // Create a storage reference from our app
        storageRef = storage.getReference();

        // Create a reference to the file to delete
        StorageReference desertRef = storageRef.child(url);

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(BlogActivity.this, "File deleted successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(BlogActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    public void deleteBlogDocuments() {
        //Deleting blog from Users > Blogs collection
        db.collection("Users").document(userId).collection("Blogs").document(blogId)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(BlogActivity.this, "Deleted blog from Users collection", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BlogActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

        //Deleting blog from standalone Blogs collection
        db.collection("Blogs").document(blogId)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(BlogActivity.this, "Blog deleted", Toast.LENGTH_SHORT).show();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                onBackPressed();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BlogActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    public void shareBlog() {
        blogShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                //i.setType("image*//*");
                //i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                //i.putExtra(Intent.EXTRA_TEXT, FullName + " has invited you to the most exciting event " + explorePojo.getChatTitle() + " organized by " + explorePojo.getEventOwner() + " at " + explorePojo.getEventLocation() + "." + " Now join it via " + "https://play.google.com/store/apps/details?id=com.temp.tempdesign&hl=en");
                i.putExtra(Intent.EXTRA_TEXT, blogModel.getBlogHeader() + " by " + blogModel.getBlogUser() + ". Read this blog here: " + shareLink);
                startActivity(Intent.createChooser(i, "Share This Blog"));
            }
        });
    }

    public void getAppDownloadLink() {
        DocumentReference docRef = db.collection("Users").document("AppDownloadLink");
        docRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                Toast.makeText(BlogActivity.this, "Server is down", Toast.LENGTH_SHORT).show();
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
                        shareLink = document.getString("DownloadUrl");
                    } else {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                } else {
                    CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.failure_image);
                    Toast.makeText(BlogActivity.this, "server is down", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        //Intent intent = new Intent(BlogActivity.this, HomeActivity.class);
        //startActivity(intent);
        // openFragment();
        super.onBackPressed();
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (null == player) return;

        // Start buffering
        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }
        // Add listeners to YouTubePlayer instance
        player.setPlayerStateChangeListener(new PlayerStateChangeListener() {
            @Override
            public void onAdStarted() {
            }

            @Override
            public void onError(ErrorReason arg0) {
            }

            @Override
            public void onLoaded(String arg0) {
            }

            @Override
            public void onLoading() {
            }

            @Override
            public void onVideoEnded() {
            }

            @Override
            public void onVideoStarted() {
            }
        });


        player.setPlaybackEventListener(new PlaybackEventListener() {
            @Override
            public void onBuffering(boolean arg0) {
            }

            @Override
            public void onPaused() {
            }

            @Override
            public void onPlaying() {
            }

            @Override
            public void onSeekTo(int arg0) {
            }

            @Override
            public void onStopped() {
            }
        });
    }


    public void SendNotifactionifLike() {
        db.collection("FcmIDs").document(userIdBlog[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> map = document.getData();
                if (map != null) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().equals("TokenKey")) {
                            Log.d("TAG", entry.getValue().toString());
                            SendNotiFicationUserID = entry.getValue().toString() + "";
                        }
                    }

                    if (SendNotiFicationUserID.equalsIgnoreCase(" ")) {

                    } else {
                        new NotifyLike().execute();
                    }

                }

            }
        });
    }

   /* public void SendNotifactionifDisLike() {
        db.collection("FcmIDs").document(userIdBlog[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> map = document.getData();
                if (map != null) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().equals("TokenKey")) {
                            Log.d("TAG", entry.getValue().toString());
                            SendNotiFicationUserID = entry.getValue().toString() + "";
                        }
                    }

                    if (SendNotiFicationUserID.equalsIgnoreCase(" ")) {

                    } else {
                        new NotifyDisLike().execute();
                    }

                }
            }
        });
    }*/

    public class NotifyLike extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
                conn.setRequestProperty("Content-Type", "application/json");
                JSONObject json = new JSONObject();
                json.put("to", SendNotiFicationUserID);
                JSONObject info = new JSONObject();
                info.put("title", "E Blogger"); // Notification title
                info.put("body", FirstName + " Liked Your Blogs"); // Notification body
                info.put("image", "https://lh6.googleusercontent.com/-sYITU_cFMVg/AAAAAAAAAAI/AAAAAAAAABM/JmQNdKRPSBg/photo.jpg");
                info.put("type", "message");
                json.put("data", info);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();
            } catch (Exception e) {
                Log.d("Error", "" + e);
            }
            return null;
        }
    }

    /*public class NotifyDisLike extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
                conn.setRequestProperty("Content-Type", "application/json");
                JSONObject json = new JSONObject();
                json.put("to", SendNotiFicationUserID);
                JSONObject info = new JSONObject();
                info.put("title", "E Blogger"); // Notification title
                info.put("body", FirstName + " DisLiked Your Blogs"); // Notification body
                info.put("image", "https://lh6.googleusercontent.com/-sYITU_cFMVg/AAAAAAAAAAI/AAAAAAAAABM/JmQNdKRPSBg/photo.jpg");
                info.put("type", "message");
                json.put("data", info);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();
            } catch (Exception e) {
                Log.d("Error", "" + e);
            }
            return null;
        }
    }*/


}





