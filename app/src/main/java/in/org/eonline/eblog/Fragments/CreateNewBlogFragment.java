package in.org.eonline.eblog.Fragments;


import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.org.eonline.eblog.HomeActivity;
import in.org.eonline.eblog.Models.BlogModel;
import in.org.eonline.eblog.Models.UserModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.SQLite.DatabaseHelper;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;
import in.org.eonline.eblog.Utilities.FontClass;
import in.org.eonline.eblog.Utilities.ImageUtility;
import in.org.eonline.eblog.Utilities.PermissionUtils;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;
import static in.org.eonline.eblog.Fragments.MyProfileFragment.MyPREFERENCES;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateNewBlogFragment extends Fragment  {
    @ServerTimestamp Date time;
    FirebaseFirestore db;
    StorageReference storageRef;
    FirebaseStorage storage;
    private EditText blogHeaderEdit;
    private EditText blogContentEdit1;
    private EditText blogContentEdit2;
    private ImageView blogImageView1, blogImageView2;
    private LinearLayout uploadImage1, uploadImage2;
    private EditText blogFooterEdit;
    //private AdView mAdView;
    //private EditText bannerAdIdEdit;
    private Button submitButton, updateButton;
    private ImageView cancelImage1;
    private ImageView cancelImage2;
    private String bannerAdId;
    private Spinner spinner;
    private String item;
    DatabaseHelper sqliteDatabaseHelper;
    //private String blogId = "Aditya7506640685";
    BlogModel blogmodel = new BlogModel();
    BlogModel blogModelToBeUpdated = new BlogModel();
    UserModel userModel;
    Map<String, Object> blogMap = new HashMap<>();
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs_new" ;
    private String userId;
    private String blogId;
    public String blogIdBase;
    public SharedPreferences.Editor editor;
    public Uri picUri;
    public Bitmap myBitmap1;
    public Bitmap myBitmap2;
    private AdView mAdView;
    private InterstitialAd interstitialAd;
    private TextView errorHeader;
    private TextView errorContent1;
    private TextView errorContent2;
    private TextView errorFooter;
    private ImageView errorImage;
    private ImageView errorImage1;
    private ImageView errorImage2;
    private ImageView errorImage3;
    public Dialog dialog;
    private File destFile, sourceFile;
    private File file;
    String dateFormatter;
    public static final String IMAGE_DIRECTORY = "E-Blogger";
    private boolean isImageOnePresent = false;
    private boolean isImageTwoPresent = false;
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    public SwipeRefreshLayout mySwipeRequestLayout;
    private String updateBlog = "";
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int ALL_WRITE_EXTERNAL_STORAGE = 108;
    private final static int ALL_READ_EXTERNAL_STORAGE = 109;

    public CreateNewBlogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            Bundle bundle = getArguments();
            if(bundle != null) {
                String model = bundle.getString("update_blog");
                updateBlog = bundle.getString("update_key");
                blogModelToBeUpdated = new Gson().fromJson(model, BlogModel.class);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_blog, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();

        ViewGroup myMostParentLayout = (ViewGroup) getView().findViewById(R.id.createNewBlog_layout);
        FontClass.getInstance(getActivity()).setFontToAllChilds(myMostParentLayout);

        sqliteDatabaseHelper = new DatabaseHelper(getActivity());
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        storage = FirebaseStorage.getInstance();
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString("UserIdCreated","AdityaKamat75066406850");
        blogmodel.setUserId(userId);
        blogModelToBeUpdated.setUserId(userId);
        blogIdBase = userId + "_0";
        blogId = sharedpreferences.getString("blogId_new",blogIdBase);

        if((sharedpreferences.getString("blogId_new", blogIdBase) == null || sharedpreferences.getString("blogId_new", blogIdBase).equalsIgnoreCase(blogIdBase))
            && !"blog_update".equalsIgnoreCase(updateBlog)) {
            // This executes when user has uninstalled app and re-installed
            // after re-installing, shared pref may be null or it will be same as userId_0 so we have to get latest blog Id
            getBlogIdNewFromUserCollection();
        }

        setSpinner();

        setBlogImages();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectivityReceiver = new ConnectivityReceiver(getActivity());
                // Initialize SDK before setContentView(Layout ID)
                isInternetPresent = connectivityReceiver.isConnectingToInternet();
                if (isInternetPresent) {
                    submitButtonLogic();
                }
                else {
                    CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);
                }
            }
        });

        cancelImage1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                blogImageView1.setImageBitmap(null);
                uploadImage1.setVisibility(View.VISIBLE);
                cancelImage1.setVisibility(View.GONE);
            }
        });

        cancelImage2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                blogImageView2.setImageBitmap(null);
                uploadImage2.setVisibility(View.VISIBLE);
                cancelImage2.setVisibility(View.GONE);
            }
        });

        MobileAds.initialize(getContext(),"ca-app-pub-7293397784162310~9840078574");
        mAdView = (AdView) getView().findViewById(R.id.createNewBlog_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Calendar ct = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        dateFormatter = df.format(ct.getTime());

        if("blog_update".equalsIgnoreCase(updateBlog)) {
            setUpdateBlogData();
        }

        loadInterstitialAd();

        allowPermissions();
    }

    public void getBlogIdNewFromUserCollection() {
        DocumentReference docRef = db.collection("Users").document(userId);
        docRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                Toast.makeText(getContext(), "Server is down", Toast.LENGTH_SHORT).show();
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
                        if(document.getString("BlogIdNew") != null) {
                            //if BlogIdNew is present means user's latest blog id is stored in db
                            blogId = document.getString("BlogIdNew");
                            editor = sharedpreferences.edit();
                            editor.putString("blogId_new",blogId);
                            editor.apply();
                        } else {
                            //if BlogIdNew is null means user's latest blog id is never stored in db
                            // so write logic to find out which blog id of user is latest
                            findBlogIdNewFromAllBlogs();
                        }
                        if(document.getString("UserBannerId") == null) {
                            //Toast.makeText(getContext(), "Please enter AdMob Banner Id in Monetization Section of application in order to monetize blog and earn", Toast.LENGTH_LONG).show();
                            createDialog();
                        }
                    } else {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    public void findBlogIdNewFromAllBlogs() {
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
                    ArrayList<String> blogIds = new ArrayList<String>();
                    ArrayList<Integer> idNumbers = new ArrayList<Integer>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            blogIds.add(document.getString("BlogId"));
                        }
                    }
                    for(int i = 0; i< blogIds.size(); i++) {
                        String id = blogIds.get(i);
                        int number = Integer.parseInt(id.substring(id.lastIndexOf("_") + 1));
                        idNumbers.add(number);
                    }
                    int max = 0;
                    if(idNumbers.size() > 0) {
                         max =  idNumbers.get(0);
                    }
                    for(int j = 0; j < idNumbers.size(); j++) {
                        if(max < idNumbers.get(j)) {
                            max = idNumbers.get(j);
                        }
                    }
                    blogId = userId + "_" + max;
                    editor = sharedpreferences.edit();
                    editor.putString("blogId_new",blogId);
                    editor.apply();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    public void setUpdateBlogData() {
        submitButton.setVisibility(View.GONE);
        updateButton.setVisibility(View.VISIBLE);

        blogId = blogModelToBeUpdated.getBlogId();

        blogHeaderEdit.setText(blogModelToBeUpdated.getBlogHeader());
        blogContentEdit1.setText(blogModelToBeUpdated.getBlogContent1());
        blogContentEdit2.setText(blogModelToBeUpdated.getBlogContent2());
        blogFooterEdit.setText(blogModelToBeUpdated.getBlogFooter());

        if(blogModelToBeUpdated.getUserBlogImage1Url() != null) {
            blogImageView1.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(blogModelToBeUpdated.getUserBlogImage1Url())
                    .into(blogImageView1);
            uploadImage1.setVisibility(View.GONE);
            cancelImage1.setVisibility(View.VISIBLE);
        } else {
            uploadImage1.setVisibility(View.VISIBLE);
            cancelImage1.setVisibility(View.GONE);
        }

        if(blogModelToBeUpdated.getUserBlogImage2Url() != null) {
            blogImageView2.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(blogModelToBeUpdated.getUserBlogImage2Url())
                    .into(blogImageView2);
            uploadImage2.setVisibility(View.GONE);
            cancelImage2.setVisibility(View.VISIBLE);
        } else {
            uploadImage2.setVisibility(View.VISIBLE);
            cancelImage2.setVisibility(View.GONE);
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibilityGone();
                boolean isValidated = validateData();
                if(isValidated) {
                    dialog = CommonDialog.getInstance().showProgressDialog(getActivity());
                    dialog.show();
                    isImageOnePresent = hasImage(blogImageView1);
                    isImageTwoPresent = hasImage(blogImageView2);

                    if(isImageOnePresent && isImageTwoPresent) {
                        uploadBlogImagesToFirebaseStorage();
                    } else if (!isImageOnePresent && !isImageTwoPresent) {
                        blogModelToBeUpdated.setUserBlogImage1Url(null);
                        blogModelToBeUpdated.setUserBlogImage2Url(null);
                        blogMap.put("BlogImage1Url", blogModelToBeUpdated.getUserBlogImage1Url());
                        blogMap.put("BlogImage2Url", blogModelToBeUpdated.getUserBlogImage2Url());
                        getUserBannerIdAndUserImageUrl();
                    } else if(isImageOnePresent && !isImageTwoPresent) {
                        blogModelToBeUpdated.setUserBlogImage2Url(null);
                        blogMap.put("BlogImage2Url", blogModelToBeUpdated.getUserBlogImage2Url());
                        uploadBlogImagesToFirebaseStorage();
                    } else if(!isImageOnePresent && isImageTwoPresent) {
                        blogModelToBeUpdated.setUserBlogImage1Url(null);
                        blogMap.put("BlogImage1Url", blogModelToBeUpdated.getUserBlogImage1Url());
                        uploadImage2();
                    }
                }
            }
        });
    }

    public void submitButtonLogic(){
        setVisibilityGone();
        //upload blog images to firebase storage first, then get the download url of images to store in Users & Blogs collection
        boolean isValidated = validateData();
        if(isValidated) {
            dialog = CommonDialog.getInstance().showProgressDialog(getActivity());
            dialog.show();
            createBlogId();
            isImageOnePresent = hasImage(blogImageView1);
            isImageTwoPresent = hasImage(blogImageView2);

            if(isImageOnePresent && isImageTwoPresent) {
                uploadBlogImagesToFirebaseStorage();
            } else if (!isImageOnePresent && !isImageTwoPresent) {
                getUserBannerIdAndUserImageUrl();
            } else if(isImageOnePresent && !isImageTwoPresent) {
                uploadBlogImagesToFirebaseStorage();
            } else if(!isImageOnePresent && isImageTwoPresent) {
                uploadImage2();
            }
        }
    }


    public boolean validateData() {

        if (blogHeaderEdit.getText().toString().equals("") || blogHeaderEdit.getText().toString().length() <= 5) {
            errorHeader.setVisibility(View.VISIBLE);
            errorImage.setVisibility(View.VISIBLE);
            return false;
        }
        if (blogContentEdit1.getText().toString().equals("") || blogContentEdit1.getText().toString().length() <= 399) {
            errorContent1.setVisibility(View.VISIBLE);
            errorImage1.setVisibility(View.VISIBLE);
            return false;
        }
        /*if (blogContentEdit2.getText().toString().equals("") || blogContentEdit2.getText().toString().length() <= 100) {
            errorContent2.setVisibility(View.VISIBLE);
            errorImage2.setVisibility(View.VISIBLE);
            return false;
        } */
        /*if (blogFooterEdit.getText().toString().equals("")) {
            errorFooter.setVisibility(View.VISIBLE);
            errorImage3.setVisibility(View.VISIBLE);
            return false;
        } */

        return true;
    }

    public void setBlogImages() {
        uploadImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectivityReceiver = new ConnectivityReceiver(getActivity());
                // Initialize SDK before setContentView(Layout ID)
                isInternetPresent = connectivityReceiver.isConnectingToInternet();
                if (isInternetPresent) {
                    startActivityForResult(getPickImageChooserIntent(), 201);
                    //allowPermissions();
                }
                else {
                    CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);
                }
            }
        });
        uploadImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectivityReceiver = new ConnectivityReceiver(getActivity());
                // Initialize SDK before setContentView(Layout ID)
                isInternetPresent = connectivityReceiver.isConnectingToInternet();
                if (isInternetPresent) {
                    startActivityForResult(getPickImageChooserIntent(), 202);
                    //allowPermissions();
                }
                else {
                    CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.no_internet);
                }

            }
        });
    }

    public void addBlogToSQLite() {
        Boolean blogIdInserted =  sqliteDatabaseHelper.insertBlogDataInSQLite(blogId,blogmodel.getBlogHeader(),blogmodel.getBlogContent1(),blogmodel.getBlogFooter());
        if (blogIdInserted) {
            //Toast.makeText(getContext(), "BlogId inserted properly in SQLite", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getContext(), "BlogId insertion failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void addData() {
        db.collection("Users").document(userId).collection("Blogs").document(blogId).set(blogMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getActivity(), "New Blog is created in Users collection", Toast.LENGTH_SHORT).show();
                        addBlogToSQLite();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                        Toast.makeText(getContext(), "Blog could not be created. Please try again.", Toast.LENGTH_LONG).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
        db.collection("Blogs").document(blogId).set(blogMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Blog is created", Toast.LENGTH_SHORT).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        clearEditText();
                        if(interstitialAd.isLoaded()) {
                            interstitialAd.show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                        Toast.makeText(getContext(), "Blog could not be created. Please try again.", Toast.LENGTH_LONG).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    public void updateData() {
        DocumentReference userDoc = db.collection("Users").document(userId).collection("Blogs").document(blogId);
        userDoc.update(blogMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getActivity(), "Blog is updated in Users collection", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                        Toast.makeText(getContext(), "Some error occured while updating blog", Toast.LENGTH_SHORT).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

        DocumentReference documentReference = db.collection("Blogs").document(blogId);
        documentReference.update(blogMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Blog is updated", Toast.LENGTH_SHORT).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        clearEditText();
                        if(interstitialAd.isLoaded()) {
                            interstitialAd.show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                        Toast.makeText(getActivity(), "Some error occured while updating blog", Toast.LENGTH_SHORT).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    public void clearEditText(){
        blogHeaderEdit.setText("");
        blogContentEdit1.setText("");
        blogContentEdit2.setText("");
        blogFooterEdit.setText("");

        blogImageView1.setImageBitmap(null);
        uploadImage1.setVisibility(View.VISIBLE);
        cancelImage1.setVisibility(View.GONE);

        blogImageView2.setImageBitmap(null);
        uploadImage2.setVisibility(View.VISIBLE);
        cancelImage2.setVisibility(View.GONE);

        submitButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.GONE);
    }

    public void initializeViews() {
        blogHeaderEdit = (EditText) getView().findViewById(R.id.blog_header);
        blogContentEdit1 = (EditText) getView().findViewById(R.id.blog_content1);
        blogContentEdit2 = (EditText) getView().findViewById(R.id.blog_content2);
        blogImageView1 = (ImageView) getView().findViewById(R.id.blog_image_1);
        blogImageView2= (ImageView) getView().findViewById(R.id.blog_image_2);
        uploadImage1 = (LinearLayout) getView().findViewById(R.id.upload_image_1);
        uploadImage1.setVisibility(View.VISIBLE);
        uploadImage2 = (LinearLayout) getView().findViewById(R.id.upload_image_2);
        uploadImage2.setVisibility(View.VISIBLE);
        blogFooterEdit = (EditText) getView().findViewById(R.id.blog_footer);
        //mAdView = (AdView) getView().findViewById(R.id.adView_user_ad);
        submitButton = (Button) getView().findViewById(R.id.submit_blog_button);
        //bannerAdIdEdit = (EditText) getView().findViewById(R.id.adview_user_id);
        spinner = (Spinner) getView().findViewById(R.id.spinner_category);
        errorHeader = (TextView) getView().findViewById(R.id.error_header);
        errorContent1=(TextView) getView().findViewById(R.id.error_content1);
        errorContent2=(TextView) getView().findViewById(R.id.error_content2);
        errorFooter=(TextView) getView().findViewById(R.id.error_footer);
        errorImage=(ImageView) getView().findViewById(R.id.error_image);
        errorImage1=(ImageView) getView().findViewById(R.id.error_image1);
        errorImage2=(ImageView) getView().findViewById(R.id.error_image2);
        errorImage3=(ImageView) getView().findViewById(R.id.error_image3);
        cancelImage1 = (ImageView) getView().findViewById(R.id.submit_cancel_image1);
        cancelImage2 = (ImageView) getView().findViewById(R.id.submit_cancel_image2);
        updateButton = (Button) getView().findViewById(R.id.update_blog_button);
        setVisibilityGone();
    }

    public void setVisibilityGone(){
        errorImage.setVisibility(View.GONE);
        errorImage1.setVisibility(View.GONE);
        errorImage2.setVisibility(View.GONE);
        errorImage3.setVisibility(View.GONE);
        errorHeader.setVisibility(View.GONE);
        errorContent1.setVisibility(View.GONE);
        errorContent2.setVisibility(View.GONE);
        errorFooter.setVisibility(View.GONE);
    }

    public void getUserBannerIdAndUserImageUrl(){
        DocumentReference docRef = db.collection("Users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {  //ToDo: this is wrong condition, need to change
                        try {
                            String bannerId = document.getString("UserBannerId");
                            String userImageUrl = document.getString("UserImageUrl");
                            String userName = document.getString("UserFirstName");
                            blogmodel.setBannerAdMobId(bannerId);
                            blogmodel.setUserImageUrl(userImageUrl);
                            blogmodel.setBlogUser(userName);
                            blogModelToBeUpdated.setBannerAdMobId(bannerId);
                            blogModelToBeUpdated.setUserImageUrl(userImageUrl);
                            blogModelToBeUpdated.setBlogUser(userName);
                        }
                        catch(NullPointerException e) {
                            //Toast.makeText(getActivity(), "Please enter banner ID", Toast.LENGTH_SHORT).show();
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }

                        if("blog_update".equalsIgnoreCase(updateBlog)) {
                            setUpdateBlogModelAndMap();
                            updateData();
                        } else {
                            setBlogModelAndMap();
                            addData();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                        //Toast.makeText(getActivity(), "Please enter banner ID", Toast.LENGTH_SHORT).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        });

    }

    /*public void getUserImageUrl(){

       DocumentReference docRef = db.collection("Users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        blogmodel.setUserImageUrl(document.get("UserImageUrl").toString());
                        if(blogmodel.getUserImageUrl()!=null) {
                            setBlogModelAndMap();
                            addData();
                        }else{
                            Toast.makeText(getActivity(), "Issue in Image loading", Toast.LENGTH_LONG).show();
                        }
                        // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(getActivity(), "Issue in Image loading", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    } */

    public void setSpinner() {
        /*List<String> categories = new ArrayList<>();
        categories.add("Travelling");
        categories.add("Food");
        categories.add("Cosmetics");
        categories.add("Apparels");
        categories.add("Technology");
        categories.add("Cars and Bikes");
        categories.add("Politics");
        categories.add("Socialism");
        categories.add("Bollywood and entertainment");
        categories.add("Business");
        categories.add("others"); */
        String abc[] = getActivity().getResources().getStringArray(R.array.blog_categories);
        List<String> categories = Arrays.asList(abc);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        //spinner.setSelection(0, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();
                blogmodel.setBlogCategory(item);
                blogModelToBeUpdated.setBlogCategory(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void setBlogModelAndMap(){
        blogmodel.setBlogHeader(blogHeaderEdit.getText().toString());
        blogmodel.setBlogContent1(blogContentEdit1.getText().toString());
        blogmodel.setBlogContent2(blogContentEdit2.getText().toString());
        blogmodel.setBlogFooter(blogFooterEdit.getText().toString());
        blogmodel.setBlogLikes("0");
        blogmodel.setBlogTimeStamp(FieldValue.serverTimestamp().toString());
        blogMap.put("BlogHeader",blogmodel.getBlogHeader());
        blogMap.put("BlogContent1",blogmodel.getBlogContent1());
        blogMap.put("BlogContent2",blogmodel.getBlogContent2());
        blogMap.put("BlogFooter", blogmodel.getBlogFooter());
        blogMap.put("BlogCategory", blogmodel.getBlogCategory());
        blogMap.put("UserId",userId);
        blogMap.put("BlogLikes", String.valueOf(blogmodel.getBlogLikes()));
        blogMap.put("BlogUserBannerId",blogmodel.getBannerAdMobId());
        blogMap.put("BlogUserImageUrl",blogmodel.getUserImageUrl());
        blogMap.put("BlogUser", blogmodel.getBlogUser());
        blogMap.put("BlogImage1Url",blogmodel.getUserBlogImage1Url());
        blogMap.put("BlogImage2Url",blogmodel.getUserBlogImage2Url());
        blogMap.put("BlogTimeStamp", FieldValue.serverTimestamp());
    }

    public void setUpdateBlogModelAndMap() {
        blogModelToBeUpdated.setBlogHeader(blogHeaderEdit.getText().toString());
        blogModelToBeUpdated.setBlogContent1(blogContentEdit1.getText().toString());
        blogModelToBeUpdated.setBlogContent2(blogContentEdit2.getText().toString());
        blogModelToBeUpdated.setBlogFooter(blogFooterEdit.getText().toString());
        blogMap.put("BlogHeader",blogModelToBeUpdated.getBlogHeader());
        blogMap.put("BlogContent1",blogModelToBeUpdated.getBlogContent1());
        blogMap.put("BlogContent2",blogModelToBeUpdated.getBlogContent2());
        blogMap.put("BlogFooter", blogModelToBeUpdated.getBlogFooter());
        blogMap.put("BlogCategory", blogModelToBeUpdated.getBlogCategory());
        blogMap.put("UserId",userId);
        blogMap.put("BlogLikes", String.valueOf(blogModelToBeUpdated.getBlogLikes()));
        blogMap.put("BlogUserBannerId",blogModelToBeUpdated.getBannerAdMobId());
        blogMap.put("BlogUserImageUrl",blogModelToBeUpdated.getUserImageUrl());
        blogMap.put("BlogUser", blogModelToBeUpdated.getBlogUser());
        blogMap.put("BlogImage1Url",blogModelToBeUpdated.getUserBlogImage1Url());
        blogMap.put("BlogImage2Url",blogModelToBeUpdated.getUserBlogImage2Url());
    }

    public String createBlogId() {
        // creates blog Id
        String  localblogId = blogId.substring(blogId.length()-1,  blogId.length() );
        int integer = Integer.parseInt(localblogId)+ 1;
        localblogId = Integer.toString(integer);
        blogId = blogId.substring(0, blogId.length() - 1);
        blogId = blogId + localblogId;

        blogmodel.setBlogId(blogId);
        blogModelToBeUpdated.setBlogId(blogId);
        blogMap.put("BlogId",blogmodel.getBlogId());
        editor = sharedpreferences.edit();
        editor.putString("blogId_new",blogId);
        editor.apply();
        saveNewBlogIdToUserCollection(blogId);
        return blogId;
    }

    public void saveNewBlogIdToUserCollection(String blogid) {
        Map<String, Object> data = new HashMap<>();
        data.put("BlogIdNew", blogid);

        db.collection("Users").document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(getActivity(), R.drawable.failure_image);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }


    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getActivity().getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getActivity().getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;

        /*File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Toast.makeText(getContext(), "Error While Capturing Image", Toast.LENGTH_SHORT).show();
        }
        Uri outputFileUri = null;
        if (photoFile != null) {
            outputFileUri = FileProvider.getUriForFile(getContext(), "in.org.eonline.eblog.fileprovider", photoFile);
        }
        return outputFileUri; */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == RESULT_OK && requestCode == 201) {
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);

                bitmap = compressImage(picUri);

                try{
                    bitmap = rotateImageIfRequired(requireContext(), bitmap, picUri);
                } catch (IOException e) {
                    blogImageView1.setVisibility(View.VISIBLE);
                    blogImageView1.setImageBitmap(bitmap);
                    uploadImage1.setVisibility(View.GONE);
                    cancelImage1.setVisibility(View.VISIBLE);
                }

                blogImageView1.setVisibility(View.VISIBLE);
                blogImageView1.setImageBitmap(bitmap);
                uploadImage1.setVisibility(View.GONE);
                cancelImage1.setVisibility(View.VISIBLE);

                /*try {
                    //myBitmap1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
                    //myBitmap = rotateImageIfRequired(myBitmap, picUri);
                    //myBitmap = getResizedBirotateImageIfRequiredtmap(myBitmap, 500);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Unable to set Image", Toast.LENGTH_SHORT).show();
                } */
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                myBitmap1 = bitmap;
                blogImageView1.setVisibility(View.VISIBLE);
                blogImageView1.setImageBitmap(myBitmap1);
                uploadImage1.setVisibility(View.GONE);
                cancelImage1.setVisibility(View.VISIBLE);
            }
        }

        if (resultCode == RESULT_OK && requestCode == 202) {
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);

                bitmap = compressImage(picUri);

                try{
                   bitmap = rotateImageIfRequired(requireContext(), bitmap, picUri);
                } catch (IOException e) {
                    blogImageView2.setVisibility(View.VISIBLE);
                    blogImageView2.setImageBitmap(bitmap);
                    uploadImage2.setVisibility(View.GONE);
                    cancelImage2.setVisibility(View.VISIBLE);
                }

                blogImageView2.setVisibility(View.VISIBLE);
                blogImageView2.setImageBitmap(bitmap);
                uploadImage2.setVisibility(View.GONE);
                cancelImage2.setVisibility(View.VISIBLE);

                /*try {
                    //myBitmap2 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
                    //myBitmap = rotateImageIfRequired(myBitmap, picUri);
                    //myBitmap = getResizedBirotateImageIfRequiredtmap(myBitmap, 500);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Unable to set Image", Toast.LENGTH_SHORT).show();
                } */
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                myBitmap2 = bitmap;
                blogImageView2.setVisibility(View.VISIBLE);
                blogImageView2.setImageBitmap(myBitmap2);
                uploadImage2.setVisibility(View.GONE);
                cancelImage2.setVisibility(View.VISIBLE);
            }
        }
    }


    public Bitmap compressImage(Uri picuri) {
        Bitmap bmp;
        file = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
        sourceFile = new File(ImageUtility.getInstance().getPathFromGooglePhotosUri(getActivity(), picuri));
        destFile = new File(file, "img_" + dateFormatter.format(new Date().toString() + ".png"));

        try {
            ImageUtility.getInstance().copyFile(sourceFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bmp = ImageUtility.getInstance().decodeFile(destFile);
        return bmp;
    }


    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public void uploadBlogImagesToFirebaseStorage() {
        // Create a storage reference from our app
        storageRef = storage.getReference();
        //List<Bitmap> bitmaps = new ArrayList<>();
        Bitmap bitmap1;
        //Bitmap bitmap2;
        // Create a child reference, imagesRef now points to "mountains.jpg"
        //String blogid = createBlogId();

        // Get the data from an ImageView as bytes
        blogImageView1.setDrawingCacheEnabled(true);
        blogImageView1.buildDrawingCache();

        final StorageReference imagesRef1 = storageRef.child("Blogs/" + sharedpreferences.getString("UserIdCreated", "document") + "/" + blogId + "img1");
        //final StorageReference imagesRef2 = storageRef.child("Blogs/" + sharedpreferences.getString("UserIdCreated", "document") + "/" + blogid + "img2");
        bitmap1 = ((BitmapDrawable) blogImageView1.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data1 = baos.toByteArray();
        UploadTask uploadTask1 = imagesRef1.putBytes(data1);
        uploadTask1.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "Blog could not be created. Please try again.", Toast.LENGTH_LONG).show();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getContext(), "File 1 successfully uploaded", Toast.LENGTH_SHORT).show();
                //getimageUrl();
                if(isImageTwoPresent) {
                    uploadImage2();
                } else {
                    getimageUrl();
                }
            }
        });
    }

    public void uploadImage2() {

        storageRef = storage.getReference();
        Bitmap bitmap2;

        blogImageView2.setDrawingCacheEnabled(true);
        blogImageView2.buildDrawingCache();

        final StorageReference imagesRef2 = storageRef.child("Blogs/" + sharedpreferences.getString("UserIdCreated", "document") + "/" + blogId + "img2");
        bitmap2 = ((BitmapDrawable) blogImageView2.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data2 = baos.toByteArray();
        UploadTask uploadTask2 = imagesRef2.putBytes(data2);
        uploadTask2.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "Blog could not be created. Please try again.", Toast.LENGTH_LONG).show();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getContext(), "File 2 successfully uploaded", Toast.LENGTH_SHORT).show();
                if(isImageOnePresent) {
                    getimageUrl();
                } else {
                    getImageUrl2();
                }
            }
        });
    }

    public void getimageUrl(){
        storageRef = storage.getReference();
        String imageRef1 = "Blogs/" + sharedpreferences.getString("UserIdCreated", "document") + "/" + blogId + "img1";

        storageRef.child(imageRef1).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        String downloadUrl = uri.toString();
                        if (downloadUrl != null) {
                            blogmodel.setUserBlogImage1Url(downloadUrl);
                            blogModelToBeUpdated.setUserBlogImage1Url(downloadUrl);
                        }
                        if(isImageTwoPresent) {
                            getImageUrl2();
                        } else {
                            getUserBannerIdAndUserImageUrl();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure (@NonNull Exception exception){
                //Toast.makeText(getContext(), "Blog could not be created. Please try again.", Toast.LENGTH_LONG).show();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

    }

    public void getImageUrl2() {
        String imageRef2 = "Blogs/" + sharedpreferences.getString("UserIdCreated", "document") + "/" + blogId + "img2";

        storageRef.child(imageRef2).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        String downloadUrl = uri.toString();
                        if (downloadUrl != null) {
                            blogmodel.setUserBlogImage2Url(downloadUrl);
                            blogModelToBeUpdated.setUserBlogImage2Url(downloadUrl);
                        }
                        getUserBannerIdAndUserImageUrl();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure (@NonNull Exception exception){
                //Toast.makeText(getContext(), "Blog could not be created. Please try again.", Toast.LENGTH_LONG).show();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    public void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(requireContext());
        interstitialAd.setAdUnitId("ca-app-pub-7293397784162310/2566340475");
        AdRequest request = new AdRequest.Builder().build();
        interstitialAd.loadAd(request);
    }


    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public void allowPermissions() {
        permissions.add(CAMERA);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(WRITE_EXTERNAL_STORAGE);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_READ_EXTERNAL_STORAGE);
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:

            case ALL_READ_EXTERNAL_STORAGE:

            case ALL_WRITE_EXTERNAL_STORAGE:

                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void createDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setTitle("To earn, monetize first and then create blogs");
        alertDialogBuilder.setMessage("Please enter your Google AdMob Banner Id in Monetization Section of application in order to monetize a Blog and earn");

        alertDialogBuilder.setPositiveButton("Redirect To Monetization", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                openFragment(new MonetizationFragment());
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

    public void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

}









