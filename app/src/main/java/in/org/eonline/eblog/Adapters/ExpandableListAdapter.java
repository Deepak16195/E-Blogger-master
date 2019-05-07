package in.org.eonline.eblog.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.HashMap;
import java.util.List;

import in.org.eonline.eblog.R;

/**
 * Created by Android Dev on 31-01-2018.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> listDataQuestion; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> listDataAnswer;
    boolean hide;


    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData, boolean mhide) {
        this._context = context;
        this.listDataQuestion = listDataHeader;
        this.listDataAnswer = listChildData;
        this.hide = mhide;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataAnswer.get(this.listDataQuestion.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.faq_child_layout, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.tv_answer);
        txtListChild.setTextSize(15);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataAnswer.get(this.listDataQuestion.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataQuestion.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataQuestion.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.faq_parent_layout, null);
        }
        ImageView image = convertView.findViewById(R.id.faq_title_icon);

        if (hide) {

            image.setVisibility(View.VISIBLE);
        }else

        {
            image.setVisibility(View.INVISIBLE);
        }

        TextView lblListHeader = convertView.findViewById(R.id.faq_question);
        ImageView iv_expand = convertView.findViewById(R.id.iv_expand);
        lblListHeader.setText(headerTitle);
        iv_expand.setSelected(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
