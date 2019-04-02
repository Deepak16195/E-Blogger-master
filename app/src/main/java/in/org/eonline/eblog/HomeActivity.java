package in.org.eonline.eblog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import in.org.eonline.eblog.Activities.BlogActivity;
import in.org.eonline.eblog.Activities.Login;
import in.org.eonline.eblog.Fragments.CreateNewBlogFragment;
import in.org.eonline.eblog.Fragments.ExploreFragment;
import in.org.eonline.eblog.Fragments.FAQFragment;
import in.org.eonline.eblog.Fragments.MonetizationFragment;
import in.org.eonline.eblog.Fragments.MyProfileFragment;
import in.org.eonline.eblog.Fragments.ReportBugFragment;
import in.org.eonline.eblog.Fragments.TermsConditionsFragment;
import in.org.eonline.eblog.Fragments.YourBlogsFragment;
import in.org.eonline.eblog.Utilities.FontClass;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseFirestore db;
    Map<String, Object> user = new HashMap<>();

    FrameLayout frameLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawer;
    GoogleSignInClient mGoogleSignInClient;
    public String fragmentTag;
    private Boolean exit = false;
    ImageView imageView;
    View hView;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs_new" ;
    private String isUserRegisteredAlready;
    private String isUserNamePresent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        isUserRegisteredAlready = sharedpreferences.getString("UserImagePath", "false");
        isUserNamePresent = sharedpreferences.getString("UserFirstName", "false");
        initializeViews();

        ViewGroup myMostParentLayout = (ViewGroup) findViewById(R.id.drawer_layout);
        FontClass.getInstance(HomeActivity.this).setFontToAllChilds(myMostParentLayout);

        fragmentTag="nav_home";

        if (getIntent().hasExtra("update_blog")) {
            String bm = getIntent().getStringExtra("update_blog");
            String key = getIntent().getStringExtra("update_key");
            //BlogModel blogModel = new Gson().fromJson(getIntent().getStringExtra("blog"), BlogModel.class);
            Bundle bundle = new Bundle();
            //String blogmodel = (new Gson()).toJson(blogModel);
            bundle.putString("update_blog", bm);
            bundle.putString("update_key", key);
            CreateNewBlogFragment createNewBlogFragment = new CreateNewBlogFragment();
            createNewBlogFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, createNewBlogFragment).commit();
        } else if (getIntent().hasExtra("monetize_key")) {
            MonetizationFragment monetizationFragment = new MonetizationFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, monetizationFragment).commit();
        }
        else {
            openFragment(new ExploreFragment(), "nav_home");
        }

        db = FirebaseFirestore.getInstance();

        toolbar.setTitle("E-Blogger");
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        // Create a new user with a first and last name

        //user.put("first", "Viraj");
        //user.put("last", "Jadhav");
        //user.put("born", 1998);
        //user.put("City", "Panvel");
        //addData();

        /* MobileAds.initialize(this,"ca-app-pub-7293397784162310~9840078574");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                  .addTestDevice("5DDD17EFB41CB40FC08FBE350D11B395").build();
        mAdView.loadAd(adRequest); */

    }

    public void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getNavHeaderTextImage();

    }

    public void getNavHeaderTextImage(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.nav_user_name);
        if(isUserNamePresent!="false")
        {
            nav_user.setText(sharedpreferences.getString("UserFirstName","EBlog User"));
        }
        else
        {
            nav_user.setText("EBlog User");
        }
        imageView = (ImageView)hView.findViewById(R.id.nav_imageView);
        //imageView.setImageResource();
        if(isUserRegisteredAlready!="false") {
            loadImageFromStorage(sharedpreferences.getString("UserImagePath","0"));
        }
        else{
            imageView.setImageResource(R.drawable.ic_user_dummy);
        }
    }


    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)hView.findViewById(R.id.nav_imageView);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    // Back button click handled by this method
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment != null) {
                if(fragment.getTag() == "nav_home") {
                    finish();
                    //System.exit(0);
                    //super.onBackPressed();
                    //finishAndRemoveTask();
                } else {
                    openFragment(new ExploreFragment(), "nav_home");
                }
            } else {
                finish();
                //System.exit(0);
                //finishAndRemoveTask();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /* @Override
    protected void onPause() {
        //Toast.makeText(HomeActivity.this, "on pause is called", Toast.LENGTH_SHORT).show();
        mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        //Toast.makeText(HomeActivity.this, "on resume is called", Toast.LENGTH_SHORT).show();
        mAdView.resume();
        super.onResume();
    } */

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    } */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            ExploreFragment exploreFragment = new ExploreFragment();
            fragmentTag="nav_home";
            //toolbar.setTitle("Explore Blogs");
            openFragment(exploreFragment,fragmentTag);
        } else if (id == R.id.nav_new_blog) {
            CreateNewBlogFragment createNewBlogFragment = new CreateNewBlogFragment();
            fragmentTag="create_new_fragment";
            //toolbar.setTitle("Create/Update Blog");
            openFragment(createNewBlogFragment,fragmentTag);
        } else if (id == R.id.nav_blog_history) {
            YourBlogsFragment yourBlogsFragment = new YourBlogsFragment();
            fragmentTag="nav_blog_history";
            //toolbar.setTitle("Your Blogs");
            openFragment(yourBlogsFragment,fragmentTag);
        } else if (id == R.id.nav_monetize) {
            MonetizationFragment monetizationFragment = new MonetizationFragment();
            fragmentTag="nav_monetize";
            //toolbar.setTitle("Monetize Blog");
            openFragment(monetizationFragment,fragmentTag);
        }
        /*else if (id == R.id.nav_task) {
            TaskFragment taskFragment = new TaskFragment();
            fragmentTag="nav_task";
            openFragment(taskFragment,fragmentTag);
        } */
        else if (id == R.id.nav_profile) {
            MyProfileFragment myProfileFragment = new MyProfileFragment();
            fragmentTag="nav_profile";
            //toolbar.setTitle("My Profile");
            openFragment(myProfileFragment,fragmentTag);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_about_us) {
            FAQFragment faqFragment = new FAQFragment();
            fragmentTag="nav_faq";
            //toolbar.setTitle("FAQ and About Us");
            openFragment(faqFragment,fragmentTag);
        } else if (id == R.id.nav_tc) {
            TermsConditionsFragment termsConditionsFragment = new TermsConditionsFragment();
            fragmentTag="nav_tc";
            //toolbar.setTitle("Terms & Conditions");
            openFragment(termsConditionsFragment,fragmentTag);
        } else if (id == R.id.nav_report_bug){
            ReportBugFragment reportBugFragment = new ReportBugFragment();
            fragmentTag="nav_report_bug";
            //toolbar.setTitle("Feedback/Report Bug");
            openFragment(reportBugFragment,fragmentTag);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment,fragmentTag);
        //fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commit();
    }

    public void addData() {
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(HomeActivity.this, "Added Successfully" + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
