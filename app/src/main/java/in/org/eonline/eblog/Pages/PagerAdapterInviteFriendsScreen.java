package in.org.eonline.eblog.Pages;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import in.org.eonline.eblog.Fragments.FollowFragment;
import in.org.eonline.eblog.Fragments.FollowingFragment;
import in.org.eonline.eblog.Models.FollowModel;


/**
 * Created by user on 03-08-2017.
 */

public class PagerAdapterInviteFriendsScreen extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    private int tabCount;
    public static ArrayList<FollowModel> SenDataFollw = new ArrayList<FollowModel>();
    public static ArrayList<FollowModel> SenDataUnFollowing = new ArrayList<FollowModel>();

    //Constructor to the class
    public PagerAdapterInviteFriendsScreen(FragmentManager fm, int tabCount, ArrayList<FollowModel> senDataFollw, ArrayList<FollowModel> senDataFollowing) {
        super(fm);
        this.tabCount = tabCount;
        this.SenDataFollw = senDataFollw;
        this.SenDataUnFollowing = senDataFollowing;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                return new FollowFragment();
            case 1:
                return new FollowingFragment();
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}