package in.org.eonline.eblog.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.org.eonline.eblog.Adapters.All_Comments_Adapter;
import in.org.eonline.eblog.Models.BlogModel;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.ConnectivityReceiver;

import static android.content.ContentValues.TAG;
import static in.org.eonline.eblog.Activities.BlogActivity.AUTH_KEY_FCM;

public class CommentActivity extends AppCompatActivity {

    Map<String, String> userReadBlogMap = new HashMap<>();
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs_new";
    private BlogModel blogModel;
    FrameLayout frameLayout;
    String[] userIdBlog;
    FirebaseStorage storage;
    private String bannerId, userId, UserName;
    StorageReference storageRef;
    Typeface tf;
    private String shareLink = "";
    ImageView user_profile_image;
    CollectionReference complaintsRef;
    Map<String, Object> blogMap = new HashMap<>();
    BlogModel blogModelToBeUpdated = new BlogModel();
    MenuItem user_Like_button;
    String likeStatusFirt = "";
    ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    public Dialog dialog;
    private List<BlogModel> blogModelsList = new ArrayList<>();
    private List<String> CommentsList = new ArrayList<>();
    private RelativeLayout commentBlock;
    private TextView noOfComments;
    private TextView commentsTxt;
    private ProgressBar progressBar;
    private RecyclerView AllCamments;
    private RelativeLayout commentEdittext;
    private CircleImageView profilePicimage;
    private EditText commentBox;
    private ImageView btnEnter;
    String[] CommentsData;
    private FirebaseFirestore db;
    private CollectionReference notebookRef;
    All_Comments_Adapter RecyclerViewHorizontalAdapter;
    String blogId, FirstName;
    String GetCommentsDataa;
    ArrayList<String> SenDataToAdpter = new ArrayList<>();
    Map<String, Object> NotifactionblogMap = new HashMap<>();
    String SendNotiFicationUserID = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString("UserIdCreated", "Deepak9702173103");
        UserName = sharedpreferences.getString("UserNameCreated", "Deepak9702173103");
        FirstName = sharedpreferences.getString("UserFirstName", "Deepak");
        initView();
        if (getIntent().hasExtra("blog")) {
            blogModel = new Gson().fromJson(getIntent().getStringExtra("blog"), BlogModel.class);
            connectivityReceiver = new ConnectivityReceiver(this);
            isInternetPresent = connectivityReceiver.isConnectingToInternet();

            if (isInternetPresent) {
                dialog = CommonDialog.getInstance().showProgressDialog(this);
                blogId = blogModel.getBlogId();
                userIdBlog = blogId.split("\\_");
            } else {
                CommonDialog.getInstance().showErrorDialog(this, R.drawable.no_internet);
                //Toast.makeText(Login.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
            }
        }

        loadComments();


        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommnets(v);
            }
        });
      /*  profilePicimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadComments();
            }
        });*/


    }



    public void addCommnets(View v) {
        String UserId = userId;
        String Username = UserName;
        if (commentBox.length() == 0) {
            commentBox.setText("0");
        }
        String Comments = commentBox.getText().toString();
        SenDataToAdpter.add("Name : " + Username + " : Id : " + UserId + " : Comments : " + Comments);
        CommentsData = new String[SenDataToAdpter.size()];
        CommentsData = SenDataToAdpter.toArray(CommentsData);
        ArrayList<Object> arrayExample = new ArrayList<>();
        Collections.addAll(arrayExample, CommentsData);
        DocumentReference userDoc = db.collection("Blogs").document(blogId);
        userDoc.update("AllComments", arrayExample)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        commentBox.setText(" ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        DocumentReference userBlog = db.collection("Users").document(userIdBlog[0]).collection("Blogs").document(blogId);
        userBlog.update("AllComments", arrayExample)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if (userId.equalsIgnoreCase(userIdBlog[0])) {

                        } else {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            NotifactionblogMap.put("BlogId", blogModel.getBlogId());
                            NotifactionblogMap.put("Userid", userId);
                            NotifactionblogMap.put("LikeTimeStamp", FieldValue.serverTimestamp());
                            NotifactionblogMap.put("NotifactionData", FirstName + " Comments On Your Blogs");
                            NotifactionblogMap.put("Views", false);
                            db.collection("Notification").document(userIdBlog[0]).collection("AllNotification").document(userId + "_" + blogId + " _Comments").set(NotifactionblogMap, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Toast.makeText(getActivity(), "New Blog is created in Users collection", Toast.LENGTH_SHORT).show();
                                            SendNotifactionifComments();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });

                        }
                        loadComments();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }


    public void SendNotifactionifComments() {
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


    public void  loadComments() {
        db.collection("Blogs").document(blogId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> arrayList = (ArrayList<String>) document.get("AllComments");
                        //Do what you need to do with your ArrayList
                        SenDataToAdpter.clear();
                        if (arrayList == null) {
                            arrayList = new ArrayList<>();
                        } else {
                            if (arrayList.size() > 0) {
                                for (String s : arrayList) {
                                    SenDataToAdpter.add(s);
                                }
                            }
                        }

                    }
                }
                //  SenDataToAdpter = new ArrayList<String>(Arrays.asList(parts));
                if (SenDataToAdpter.size() > 0) {
                    noOfComments.setText("(" + SenDataToAdpter.size() + ")");
                    RecyclerViewHorizontalAdapter = new All_Comments_Adapter(CommentActivity.this, SenDataToAdpter);
                    AllCamments.setAdapter(RecyclerViewHorizontalAdapter);
                }
            }
        });



       /* FirebaseFirestore.getInstance().collection("Blogs").document(blogId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> map = document.getData();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().equals("AllComments")) {
                        Log.d("TAG", entry.getValue().toString());
                        GetCommentsDataa = entry.getValue().toString() + "";
                    }
                }
                if (GetCommentsDataa.length() > 5) {
                    String[] parts = GetCommentsDataa.split(",");
                    SenDataToAdpter = new ArrayList<String>(Arrays.asList(parts));
                    if (SenDataToAdpter.size() > 0) {
                        RecyclerViewHorizontalAdapter = new All_Comments_Adapter(CommentActivity.this, SenDataToAdpter);
                        AllCamments.setAdapter(RecyclerViewHorizontalAdapter);
                    }
                }
            }
        });*/


    }


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
                info.put("body", FirstName + " Comments On Your Blogs"); // Notification body
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


   /* public void enterBlogsFirebase() {
        String blogId = blogModel.getBlogId();

     *//*   List<String> Comments = new ArrayList<>();
        Comments.add(userId+"__"+"hi how are you "+"__"+blogId);*//*

        userReadBlogMap.put("Commets", userId + "__" + "hi how are you " + "__" + blogId);


      *//*  userReadBlogMap.put("Comment"," Hi How are  ");
        userReadBlogMap.put("UseId", blogModel.getUserId()+" ");
        userReadBlogMap.put("UserName", blogModel.getBlogUser()+" ");
        userReadBlogMap.put("UserImage", blogModel.getUserImageUrl()+" ");
        userReadBlogMap.put("UserImage", blogModel.getUserImageUrl()+" ");*//*

        db.collection("Comments").document(blogId).set(userReadBlogMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommonDialog.getInstance().showErrorDialog(CommentActivity.this, R.drawable.failure_image);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }


    public void DataEnterBlogsFirebase() {
        String blogId = blogModel.getBlogId();
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        rootRef.collection("Blogs").document(blogId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dialog.hide();
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equals("BlogId")) {
                                Log.d("TAG", entry.getValue().toString());
                                CommentsList.add(entry.getValue().toString());

                                enterBlogsFirebase();
                            }
                        }
                    }
                }
            }
        });
    }*/


    private void initView() {
        commentBlock = (RelativeLayout) findViewById(R.id.comment_block);
        noOfComments = (TextView) findViewById(R.id.no_of_comments);
        commentsTxt = (TextView) findViewById(R.id.comments_txt);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        AllCamments = (RecyclerView) findViewById(R.id.All_camments);
        commentEdittext = (RelativeLayout) findViewById(R.id.comment_edittext);
        profilePicimage = (CircleImageView) findViewById(R.id.profile_picimage);
        commentBox = (EditText) findViewById(R.id.comment_box);
        btnEnter = (ImageView) findViewById(R.id.btn_enter);
    }













       /* CollectionReference colRef = db.collection("Blogs");
        colRef.orderBy("BlogTimeStamp", Query.Direction.DESCENDING).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonDialog.getInstance().showErrorDialog(CommentActivity.this, R.drawable.failure_image);
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
                            setBlogModel(document);
                        } else {
                            CommonDialog.getInstance().showErrorDialog(CommentActivity.this, R.drawable.failure_image);
                        }
                    }
                    if (i == 0) {
                        CommonDialog.getInstance().showErrorDialog(CommentActivity.this, R.drawable.no_data);
                    }
                    setPopularBlogsRecyclerView();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } else {
                    CommonDialog.getInstance().showErrorDialog(CommentActivity.this, R.drawable.failure_image);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        });*/


}
