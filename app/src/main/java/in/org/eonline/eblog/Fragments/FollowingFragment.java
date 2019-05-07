package in.org.eonline.eblog.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import in.org.eonline.eblog.Adapters.FollowingAdapter;
import in.org.eonline.eblog.Models.FollowModel;
import in.org.eonline.eblog.Pages.PagerAdapterInviteFriendsScreen;
import in.org.eonline.eblog.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowingFragment extends Fragment {

    ArrayList<FollowModel> SenDataToAdpter = new ArrayList<FollowModel>();

    public FollowingFragment() {
        // Required empty public constructor
    }

    RecyclerView Following;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        Following = view.findViewById(R.id.Following);
        SenDataToAdpter = PagerAdapterInviteFriendsScreen.SenDataUnFollowing;
        if (SenDataToAdpter.size() > 0) {
            Following.setAdapter(new FollowingAdapter(getActivity(), SenDataToAdpter));
        }

        return view;
    }


}
