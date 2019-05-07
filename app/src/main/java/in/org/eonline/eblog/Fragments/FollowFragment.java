package in.org.eonline.eblog.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import in.org.eonline.eblog.Adapters.FollowAdapter;
import in.org.eonline.eblog.Models.FollowModel;
import in.org.eonline.eblog.Pages.PagerAdapterInviteFriendsScreen;
import in.org.eonline.eblog.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowFragment extends Fragment {

    ArrayList<FollowModel> SenData= new ArrayList<FollowModel>();
    public FollowFragment() {
        // Required empty public constructor
    }


    RecyclerView Follow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow_un_follow, container, false);
        Follow = view.findViewById(R.id.Follow);
        SenData = PagerAdapterInviteFriendsScreen.SenDataFollw;
        if (SenData.size() > 0) {
            Follow.setAdapter(new FollowAdapter(getActivity(), SenData));
        }

        return view;
    }

}
