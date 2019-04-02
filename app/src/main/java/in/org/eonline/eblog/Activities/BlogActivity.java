package in.org.eonline.eblog.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import in.org.eonline.eblog.Fragments.ExploreFragment;
import in.org.eonline.eblog.Fragments.MonetizationFragment;
import in.org.eonline.eblog.HomeActivity;
import in.org.eonline.eblog.Models.BlogModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;
import in.org.eonline.eblog.Utilities.FontClass;

import static android.content.ContentValues.TAG;

public class BlogActivity extends AppCompatActivity {
    private TextView blogHeader, blogContent1, blogContent2, blogFooter, blogCategory, blogLikes, blogShare;
    private String bannerId, userId, likeStatus, likeButtonValue;
    private ImageView userLikesButton, blogImageView1, blogImageView2, monetizationIcon;
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
    Typeface tf;
    private String shareLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To make activity Full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_blog);

        InitializeViews();
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
        userId = sharedpreferences.getString("UserIdCreated", "AdityaKamat75066406850");

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

            if(blogModel.getUserId().equalsIgnoreCase(userId)) {
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
    }

    public void setBlogDataAndImage(){
        blogHeader.setText(blogModel.getBlogHeader());
        blogHeader.setTypeface(blogHeader.getTypeface(), Typeface.BOLD);
        blogContent1.setText(blogModel.getBlogContent1());
        blogContent2.setText(blogModel.getBlogContent2());
        blogFooter.setText(blogModel.getBlogFooter());
        blogFooter.setTypeface(blogFooter.getTypeface(), Typeface.BOLD);
        blogCategory.setText(blogModel.getBlogCategory());
        blogId = blogModel.getBlogId();
        blogLikes.setText(blogModel.getBlogLikes() + "Likes");
        bannerId = blogModel.getBannerAdMobId();
        Glide.with(BlogActivity.this)
                .load(blogModel.getUserBlogImage1Url())
                .into(blogImageView1);
        Glide.with(BlogActivity.this)
                .load(blogModel.getUserBlogImage2Url())
                .into(blogImageView2);
        CheckLikes(blogModel);
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

        if(blogModel.getUserId().equalsIgnoreCase(userId) && (blogModel.getBannerAdMobId() == null || !blogModel.getBannerAdMobId().contains("ca-app-pub"))) {
            monetizationIcon.setVisibility(View.VISIBLE);
            ImageViewCompat.setImageTintList(monetizationIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
            //monetizationIcon.setTooltipText("This blog is not monetized");
            monetizationIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialog();
                }
            });
        } else if (blogModel.getUserId().equalsIgnoreCase(userId) && (blogModel.getBannerAdMobId() != null || blogModel.getBannerAdMobId().contains("ca-app-pub"))){
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

        getAppDownloadLink();
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

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void InitializeViews() {
        blogHeader = findViewById(R.id.user_blog_header_text);
        blogContent1 = findViewById(R.id.user_blog_content_text1);
        blogContent2 = findViewById(R.id.user_blog_content_text2);
        blogFooter = findViewById(R.id.user_blog_footer_text);
        blogCategory = findViewById(R.id.user_blog_category);
        blogLikes = findViewById(R.id.user_blog_likes);
        userLikesButton = (ImageView) findViewById(R.id.user_blog_button);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        blogImageView1= (ImageView) findViewById(R.id.blog_image_activity_1);
        blogImageView2= (ImageView) findViewById(R.id.blog_image_activity_2);
        deleteBlog = (Button) findViewById(R.id.delete_blog);
        mySwipeRequestLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_blog_activity);
        updateBlog = (Button) findViewById(R.id.update_blog);
        blogShare = (TextView) findViewById(R.id.user_share_blog);
        monetizationIcon = (ImageView) findViewById(R.id.monetization_icon);
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
                    QuerySnapshot documentSize =  task.getResult();
                    if(documentSize.size() > 0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docid = document.getId();
                            String likeStatus = document.getString("LikeStatus");
                            if (docid.equals(blogId)) {
                                if (likeStatus.toString().equals("true")) {
                                    // userLikesButton.setBackgroundTintList(getResources().getColorStateList(R.color.like));
                                    userLikesButton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                                    likeButtonValue = "LIKED";
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    break;
                                } else {
                                    // userLikesButton.setBackgroundTintList(getResources().getColorStateList(R.color.black));
                                    userLikesButton.setImageResource(R.drawable.ic_thumb_up_black_like_24dp);
                                    likeButtonValue = "LIKE";
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                }
                            } else {
                                //userLikesButton.setBackgroundTintList(getResources().getColorStateList(R.color.black));
                                userLikesButton.setImageResource(R.drawable.ic_thumb_up_black_like_24dp);
                                likeButtonValue = "LIKE";
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }
                    } else {
                        //userLikesButton.setBackgroundTintList(getResources().getColorStateList(R.color.black));
                        userLikesButton.setImageResource(R.drawable.ic_thumb_up_black_like_24dp);
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
                        userLikesButton.setImageResource(R.drawable.ic_thumb_up_black_like_24dp);
                        userLikesButton.setBackgroundColor(getResources().getColor(R.color.white));
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

    public void userLikeButtonLogic(){
        userLikesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                connectivityReceiver = new ConnectivityReceiver(BlogActivity.this);
                // Initialize SDK before setContentView(Layout ID)
                isInternetPresent = connectivityReceiver.isConnectingToInternet();
                if (isInternetPresent)
                {
                if (likeButtonValue == "LIKE") {
                    AddUserBlogMap(blogModel);
                } else {
                    int bloglikesNew = Integer.parseInt(blogModel.getBlogLikes()) - 1;
                    userLikesButton.setImageResource(R.drawable.ic_thumb_up_black_like_24dp);
                    likeButtonValue = "LIKE";
                    likeStatus = "false";
                    String blogLikesNewString = Integer.toString(bloglikesNew);
                    blogModel.setBlogLikes(blogLikesNewString);
                    blogLikes.setText(blogLikesNewString + "Likes ");
                    //  String blogLikesNewString = Integer.toString(bloglikesNew);
                    // blogModel.setBlogLikes(blogLikesNewString);
                    //DocumentReference userDoc = db.collection("Blogs").document(blogId);
                    userReadBlogMap.put("LikeStatus", likeStatus);
                    userReadBlogMap.put("BlogLikes", blogLikesNewString);
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
                }
                else
                {
                    CommonDialog.getInstance().showErrorDialog(BlogActivity.this, R.drawable.no_internet);
                }
            }

        });
    }

    public void AddUserBlogMap(BlogModel blogModel) {
        likeStatus = "true";
        userLikesButton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
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

        DocumentReference userBlog  = db.collection("Users").document(userIdBlog[0]).collection("Blogs").document(blogId);
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
        if(blogModel.getUserBlogImage1Url() != null) {
            String url1 = "Blogs/" + userId + "/" + blogModel.getBlogId() + "img1";
            deleteBlogImages(url1);
        }
        if(blogModel.getUserBlogImage2Url() != null) {
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

    public void openFragment() {
        Fragment frg=new ExploreFragment();
       // frg = getSupportFragmentManager().findFragmentByTag("nav_home");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

}





