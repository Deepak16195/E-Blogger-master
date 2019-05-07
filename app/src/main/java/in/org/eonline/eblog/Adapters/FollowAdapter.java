package in.org.eonline.eblog.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.org.eonline.eblog.Activities.CommentActivity;
import in.org.eonline.eblog.Models.FollowModel;
import in.org.eonline.eblog.R;

import static android.content.ContentValues.TAG;
import static in.org.eonline.eblog.Activities.BlogActivity.AUTH_KEY_FCM;
import static in.org.eonline.eblog.HomeActivity.MyPREFERENCES;


/**
 * Created by Deepak on 04/11/17.
 */

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.HorizontalViewHolder> {

    Context context;
    ArrayList<FollowModel> SendFollow = new ArrayList<FollowModel>();
    ArrayList<String> StoreFollow = new ArrayList<String>();
    ArrayList<String> StoreFollowing = new ArrayList<String>();
    String UserEmailId, PostUserid, SendNotiFicationUserID;
    private SharedPreferences sharedpreferences;
    private FirebaseFirestore db;
    String[] CommentsData;
    Map<String, Object> NotifactionblogMap = new HashMap<>();
    String FirstName;

    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_follow_unfollow_friends, parent, false);
        return new HorizontalViewHolder(view);
    }

    public FollowAdapter(Context context, ArrayList<FollowModel> model) {
        this.context = context;
        this.SendFollow = model;
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        UserEmailId = sharedpreferences.getString("UserIdCreated", "Deepak9702173103");
        FirstName = sharedpreferences.getString("UserFirstName", "Deepak");
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onBindViewHolder(final HorizontalViewHolder holder, int position) {
        holder.cardTitle.setText(SendFollow.get(position).getUserFirstName() + " " + SendFollow.get(position).getUserLastName());
        if (SendFollow.get(position).getUserImageUrl() != null) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.user_shadow);
            requestOptions.error(R.drawable.user_shadow);
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(SendFollow.get(position).getUserImageUrl())
                    .into(holder.UserImageItem);
        }
        holder.mfollow_btn.setSelected(SendFollow.get(position).isFolloers());
        /*--------------------- Hide Follow_unFollow (ToggleBTN) is same user exits in list --------------------*/
        if (SendFollow.get(position).getUserEmailId() == UserEmailId) {
            holder.mfollow_btn.setVisibility(View.INVISIBLE);
        } else {
            holder.mfollow_btn.setVisibility(View.VISIBLE);
        }
        /*-----------------------------------------------------------------------------------------------------*/
        holder.mfollow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.mfollow_btn.isSelected()) {
                    String username = SendFollow.get(holder.getAdapterPosition()).getUserFirstName();
                    String profileUrl = SendFollow.get(holder.getAdapterPosition()).getUserImageUrl();
                    String user_id = SendFollow.get(holder.getAdapterPosition()).getUserEmailId();
                    showUnFollowDialog(context, username, profileUrl, user_id, holder.getAdapterPosition(), holder);
                } else {
                    String username = SendFollow.get(holder.getAdapterPosition()).getUserFirstName();
                    String profileUrl = SendFollow.get(holder.getAdapterPosition()).getUserImageUrl();
                    String user_id = SendFollow.get(holder.getAdapterPosition()).getUserEmailId();
                    showFollowDialog(context, username, profileUrl, user_id, holder.getAdapterPosition(), holder);
                }

            }
        });

    }


    private void showFollowDialog(final Context context, String username, String profileUrl, final String user_id, final int position, final FollowAdapter.HorizontalViewHolder holder) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.follow_user_dialog_layout);
        TextView user_name = dialog.findViewById(R.id.user_profile_name);
        TextView btn_cancel = dialog.findViewById(R.id.tv_cancel);
        TextView btn_follow = dialog.findViewById(R.id.tv_follow);
        CircleImageView profile_pic = dialog.findViewById(R.id.user_profile_photo);
        user_name.setText(username);
        Glide.with(context).load(profileUrl)
                .into(profile_pic);
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Users").document(UserEmailId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                            DocumentReference userDoc = db.collection("Users").document(UserEmailId);
                            ArrayList<Object> arrayExample = new ArrayList<>();
                            StoreFollow.add(user_id);
                            CommentsData = new String[StoreFollow.size()];
                            CommentsData = StoreFollow.toArray(CommentsData);
                            Collections.addAll(arrayExample, CommentsData);
                            userDoc.update("AllFollow", arrayExample)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            dialog.dismiss();
                                            SendFollow.get(position).setFolloers(true);
                                            holder.mfollow_btn.setSelected(true);

                                            db.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            ArrayList<String> arrayList = (ArrayList<String>) document.get("AllFollowing");
                                                            //Do what you need to do with your ArrayList
                                                            StoreFollowing.clear();
                                                            if (arrayList == null) {
                                                                arrayList = new ArrayList<>();
                                                            } else {
                                                                if (arrayList.size() > 0) {
                                                                    for (String s : arrayList) {
                                                                        StoreFollowing.add(s);
                                                                    }
                                                                }
                                                            }
                                                        }


                                                        DocumentReference userDoc = db.collection("Users").document(user_id);
                                                        ArrayList<Object> arrayExample = new ArrayList<>();
                                                        StoreFollowing.add(UserEmailId);
                                                        CommentsData = new String[StoreFollowing.size()];
                                                        CommentsData = StoreFollowing.toArray(CommentsData);
                                                        Collections.addAll(arrayExample, CommentsData);
                                                        userDoc.update("AllFollowing", arrayExample)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {


                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {

                                                                    }
                                                                });

                                                    }
                                                }

                                            });


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    }
                });


                NotifactionblogMap.put("BlogId", user_id + "");
                NotifactionblogMap.put("Userid", UserEmailId);
                NotifactionblogMap.put("LikeTimeStamp", FieldValue.serverTimestamp());
                NotifactionblogMap.put("NotifactionData", FirstName + " is Now Following You");
                NotifactionblogMap.put("Views", false);
                db.collection("Notification").document(user_id).collection("AllNotification").document(user_id + "_" + UserEmailId + " _Follow").set(NotifactionblogMap, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(getActivity(), "New Blog is created in Users collection", Toast.LENGTH_SHORT).show();
                                PostUserid = user_id;
                                SendNotifactionifComments();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.show();

    }

    private void showUnFollowDialog(final Context context, String username, String profileUrl, final String user_id, final int position, final FollowAdapter.HorizontalViewHolder holder) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.unfollow_user_dialog_layout);
        TextView user_name = dialog.findViewById(R.id.user_profile_name);
        TextView btn_cancel = dialog.findViewById(R.id.tv_cancel);
        TextView btn_unfollow = dialog.findViewById(R.id.tv_unfollow);
        CircleImageView profile_pic = dialog.findViewById(R.id.user_profile_photo);
        user_name.setText(username);

        Glide.with(context).load(profileUrl)
                .thumbnail(0.5f)
                .into(profile_pic);

        btn_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("Users").document(UserEmailId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                            DocumentReference userDoc = db.collection("Users").document(UserEmailId);
                            ArrayList<Object> arrayExample = new ArrayList<>();
                            StoreFollow.remove(user_id);
                            CommentsData = new String[StoreFollow.size()];
                            CommentsData = StoreFollow.toArray(CommentsData);
                            Collections.addAll(arrayExample, CommentsData);
                            userDoc.update("AllFollow", arrayExample)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            dialog.dismiss();
                                            SendFollow.get(position).setFolloers(false);
                                            holder.mfollow_btn.setSelected(false);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                        }
                    }
                });


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.show();

    }

    public void SendNotifactionifComments() {
        db.collection("FcmIDs").document(PostUserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                info.put("body", FirstName + " is Now Following You"); // Notification body
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


    @Override
    public int getItemCount() {
        return SendFollow.size();
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView UserImageItem;
        TextView cardTitle;
        ImageButton mfollow_btn;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            mfollow_btn = itemView.findViewById(R.id.follow_btn);
            UserImageItem = itemView.findViewById(R.id.image);
            cardTitle = itemView.findViewById(R.id.profile_name);
        }
    }
}
