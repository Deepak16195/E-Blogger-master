package in.org.eonline.eblog.Activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.org.eonline.eblog.Adapters.ExpandableListAdapter;
import in.org.eonline.eblog.Models.FaqPojo;
import in.org.eonline.eblog.R;
import in.org.eonline.eblog.Utilities.FontClass;
import in.org.eonline.eblog.Utilities.NonScrollExpandableListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FAQActivity extends AppCompatActivity {

    private AdView mAdView;

    public FAQActivity() {
        // Required empty public constructor
    }

    /*-----------------  VIEW COMPONENT DECLARE ------------------*/
    Toolbar mToolbar;
    NonScrollExpandableListView expandableListView;
    ArrayList<FaqPojo.Faq> faqArrayList;
    List<String> listDataQuestions = new ArrayList<>();
    HashMap<String, List<String>> listDataAnswers = new HashMap<>();
    ExpandableListAdapter expandableListAdapter;
    List<String> answer;
    /*------------------------------------------------------------*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To make activity Full Screen
        setContentView(R.layout.fragment_faq);
        ViewGroup myMostParentLayout = (ViewGroup) findViewById(R.id.faq_layout);
        FontClass.getInstance(FAQActivity.this).setFontToAllChilds(myMostParentLayout);
        mAdView = (AdView) findViewById(R.id.faq_adView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        expandableListView = findViewById(R.id.faq);
        ArrayList<FaqPojo.Faq> faqDataFromJson = new ArrayList<>();
        FaqPojo.Faq faq = new FaqPojo.Faq();
        faq.setId("0");
        faq.setQuestion("How I can earn with E-Bogger App?");
        faq.setAnswer("You need to monetize your Blogs in order to earn through Blogs. More the Blogs, more the earning");
        faqDataFromJson.add(faq);
        FaqPojo.Faq faq2 = new FaqPojo.Faq();
        faq2.setId("1");
        faq2.setQuestion("What does it mean to monetize the Blogs?");
        faq2.setAnswer("Whenever you create a Blog in E-Blogger Application, we will load your Google AdMob Ad on your Blog.You can not see your own Ad on your own Blog. Other people will be able to see your Ads on your Blogs");
        faqDataFromJson.add(faq2);


        FaqPojo.Faq faq3 = new FaqPojo.Faq();
        faq3.setId("2");
        faq3.setQuestion("Why I can not see my own Ad on my own Blogs?");
        faq3.setAnswer("To prevent from invalid clicks and impressions we do not allow users to see their own Ads on their own Blogs.Even the Google AdMob's policy has this rule. Google AdMob will block your account if you click on your own Ads and make earnings");
        faqDataFromJson.add(faq3);

        FaqPojo.Faq faq4 = new FaqPojo.Faq();
        faq4.setId("3");
        faq4.setQuestion("What is required to monetize a Blog?");
        faq4.setAnswer("All you need is a Google AdMob account with a Banner Ad Unit Id. You need to enter your Banner Ad Unit Id on Monetization section of E-Blogger App and we will handle the rest.");
        faqDataFromJson.add(faq4);
        

        faqArrayList = faqDataFromJson;
        prepareExpandableListData(faqArrayList);      //  Prepare ExpandableListView Data using ArrayList
        expandableListView.setNestedScrollingEnabled(false);
        expandableListAdapter = new ExpandableListAdapter(FAQActivity.this, listDataQuestions, listDataAnswers,true);
        expandableListView.setAdapter(expandableListAdapter);


    }

    private void prepareExpandableListData(ArrayList<FaqPojo.Faq> faqArrayList) {
        for (int i = 0; i < faqArrayList.size(); i++) {
            listDataQuestions.add(faqArrayList.get(i).getQuestion());
            answer = new ArrayList<>();
            answer.add(faqArrayList.get(i).getAnswer());
            listDataAnswers.put(listDataQuestions.get(i), answer);
        }

    }

   /* public ArrayList<FaqPojo.Faq> getFaqDataFromJson() {
        ArrayList<FaqPojo.Faq> faqDataFromJson = new ArrayList<>();
        String json;
        try {
            InputStream is = getAssets().open("faq.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("faq");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                FaqPojo.Faq faq = new FaqPojo.Faq();
                faq.setQuestion(jo.getString("question"));
                faq.setAnswer(jo.getString("answer"));
                faqDataFromJson.add(faq);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return faqDataFromJson;
    }*/
}
