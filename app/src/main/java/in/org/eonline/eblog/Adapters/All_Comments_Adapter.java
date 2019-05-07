package in.org.eonline.eblog.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.org.eonline.eblog.R;

public class All_Comments_Adapter extends RecyclerView.Adapter<All_Comments_Adapter.QueslistMyView> {


    private List<String> list;
    Context context;
    AlertDialog dialog;

    public class QueslistMyView extends RecyclerView.ViewHolder {

        public TextView username, comment, comment_date;
        public ImageView edit_post;
        public CircleImageView profile_icon;

        public QueslistMyView(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.username);
            comment = (TextView) view.findViewById(R.id.comment);
            profile_icon = (CircleImageView) view.findViewById(R.id.profile_icon);
        }

    }


    public All_Comments_Adapter(Context context, List<String> horizontalList) {
        this.list = horizontalList;
        this.context = context;
    }

    @Override
    public QueslistMyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_comments, parent, false);

        return new QueslistMyView(itemView);
    }

    @Override
    public void onBindViewHolder(final QueslistMyView holder, final int position) {

        String FullDetails = list.get(position).toString();
        String[] Name;
        Name = FullDetails.split(" : ");
        if(Name.length == 6)
        {
            holder.username.setText(Name[1]);
            holder.comment.setText(Name[5]);
        }

//        holder.comment_date.setText(list.get(position).getCommentedon());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}

