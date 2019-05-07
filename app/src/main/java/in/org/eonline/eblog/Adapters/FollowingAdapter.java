package in.org.eonline.eblog.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.org.eonline.eblog.Models.FollowModel;
import in.org.eonline.eblog.R;


/**
 * Created by Deepak on 04/11/17.
 */

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.HorizontalViewHolder> {
    Context context;
    ArrayList<FollowModel> SenDataToAdpter = new ArrayList<FollowModel>();
    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_follow_unfollow_friends, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorizontalViewHolder holder, int position) {
        holder.cardTitle.setText(SenDataToAdpter.get(position).getUserFirstName() + " "+SenDataToAdpter.get(position).getUserLastName());
        holder.follow_btn.setVisibility(View.GONE);
        if (SenDataToAdpter.get(position).getUserImageUrl() != null) {
            Glide.with(context)
                    .load(SenDataToAdpter.get(position).getUserImageUrl())
                    .into(holder.UserImageItem);
        }

    }

    public FollowingAdapter(Context context, ArrayList<FollowModel> model) {
        this.context = context;
        this.SenDataToAdpter = model;
    }


    @Override
    public int getItemCount() {
        return SenDataToAdpter.size();
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView UserImageItem,follow_btn;
        TextView cardTitle;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            UserImageItem = itemView.findViewById(R.id.image);
            cardTitle = itemView.findViewById(R.id.profile_name);
            follow_btn = itemView.findViewById(R.id.follow_btn);

        }
    }
}
