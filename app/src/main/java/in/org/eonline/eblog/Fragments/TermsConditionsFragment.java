package in.org.eonline.eblog.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import in.org.eonline.eblog.R;
import in.org.eonline.eblog.Utilities.FontClass;


public class TermsConditionsFragment extends Fragment {

   private TextView termsHeader;
   private TextView termsContent;
   private AdView mAdView;

    public TermsConditionsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms_conditions, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewGroup myMostParentLayout = (ViewGroup) getView().findViewById(R.id.tnc_layout);
        FontClass.getInstance(getActivity()).setFontToAllChilds(myMostParentLayout);

        mAdView = (AdView) getView().findViewById(R.id.tnc_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
