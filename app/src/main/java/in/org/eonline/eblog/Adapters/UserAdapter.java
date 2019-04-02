package in.org.eonline.eblog.Adapters;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.org.eonline.eblog.Models.UserModel;
import in.org.eonline.eblog.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<UserModel> userModels;
    private UserAdapter.ClickListener clickListner;


    public UserAdapter(Context context, List<UserModel> model, UserAdapter.ClickListener listener) {
        this.context = context;
        this.userModels = model;
        this.clickListner = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.userNameItem.setText(userModels.get(position).getUserFName());
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImageItem;
        TextView userNameItem;

        public UserViewHolder(View itemView) {
            super(itemView);
            userNameItem = (TextView) itemView.findViewById(R.id.user_name_item);
            userImageItem = (ImageView) itemView.findViewById(R.id.user_image_item);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public interface ClickListener {

    }
}
