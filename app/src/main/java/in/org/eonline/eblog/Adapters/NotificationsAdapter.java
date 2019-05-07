package in.org.eonline.eblog.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.org.eonline.eblog.Models.BlogModel;
import in.org.eonline.eblog.Models.NotifactionModel;
import in.org.eonline.eblog.R;


/**
 * Created by Deepak on 04/11/17.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.HorizontalViewHolder> {

    Context context;
    List<NotifactionModel> NotifactionModels =new ArrayList<NotifactionModel>();
    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification, parent, false);
        return new HorizontalViewHolder(view);
    }

    public NotificationsAdapter(Context context, List<NotifactionModel> model) {
        this.context = context;
        this.NotifactionModels = model;
    }

    @Override
    public void onBindViewHolder(HorizontalViewHolder holder, int position) {
        holder.cardImage.setImageResource(R.drawable.user_shadow);
        holder.notification_narration.setText(NotifactionModels.get(position).getNotifactionData()+" ");
        holder.cardTitle.setText(NotifactionModels.get(position).getBlogId()+" ");
    }

    @Override
    public int getItemCount() {
        return NotifactionModels.size();
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImage;
        TextView cardTitle,notification_narration;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.image);
            cardTitle = itemView.findViewById(R.id.text);
            notification_narration = itemView.findViewById(R.id.notification_narration);
        }
    }
}
