package in.org.eonline.eblog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import in.org.eonline.eblog.Activities.BlogActivity;
import in.org.eonline.eblog.Activities.CreateNewBlogActivity;
import in.org.eonline.eblog.Activities.Login;
import in.org.eonline.eblog.Fragments.CreateNewBlogFragment;
import in.org.eonline.eblog.Fragments.ExploreFragment;
import in.org.eonline.eblog.Activities.FAQActivity;
import in.org.eonline.eblog.Fragments.Main_ProfileFragment;
import in.org.eonline.eblog.Fragments.MonetizationFragment;
import in.org.eonline.eblog.Activities.MyProfileAcivity;
import in.org.eonline.eblog.Fragments.NotificationsFragment;
import in.org.eonline.eblog.Activities.ReportBugActivity;
import in.org.eonline.eblog.Fragments.TaskFragment;
import in.org.eonline.eblog.Fragments.TermsConditionsFragment;
import in.org.eonline.eblog.Fragments.YourBlogsFragment;
import in.org.eonline.eblog.Fragments.YourFriendsFragment;
import in.org.eonline.eblog.Utilities.BottomNavigationViewHelper;
import in.org.eonline.eblog.Utilities.CommonDialog;
import in.org.eonline.eblog.Utilities.FontClass;

import static android.content.ContentValues.TAG;

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
    public static final String MyPREFERENCES = "MyPrefs_new";
    private String isUserRegisteredAlready;
    private String isUserNamePresent;
    RelativeLayout ShowMain;
    LinearLayout mAppBarLayout;
    ImageView iv_ic_search, iv_ic_reward, iv_ic_menu;
    BottomNavigationView bottomNavigationItemView;
    ImageView Logo;
    String userId;
    Map<String, String> FireBaseTokenMap = new HashMap<>();
    ImageView Filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        isUserRegisteredAlready = sharedpreferences.getString("UserImagePath", "false");
        isUserNamePresent = sharedpreferences.getString("UserFirstName", "false");
        userId = sharedpreferences.getString("UserIdCreated", "Deepak9702173103");
        initializeViews();
        ViewGroup myMostParentLayout = (ViewGroup) findViewById(R.id.drawer_layout);
        FontClass.getInstance(HomeActivity.this).setFontToAllChilds(myMostParentLayout);
        bottomNavigationItemView = (BottomNavigationView) findViewById(R.id.bnv_booking_list);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationItemView);
        android.support.v4.app.Fragment selectedFragment = null;
        bottomNavigationItemView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationItemView.setSelectedItemId(R.id.navigation_home);
        selectedFragment = ExploreFragment.newInstance();
        if (getIntent().hasExtra("update_blog")) {
            bottomNavigationItemView.setSelectedItemId(R.id.navigation_newBlog);
            toolbar.setTitle("Create/Update Blog");
            selectedFragment = CreateNewBlogFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction()
                .add(selectedFragment, "home")
                // Add this transaction to the back stack (name is an optional name for this back stack state, or null).
                .addToBackStack(null)
                .commit();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


        transaction.replace(R.id.content_frame, selectedFragment);
        transaction.commit();
        bottomNavigationItemView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                android.support.v4.app.Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        toolbar.setTitle("E - Blogger");
                        ShowMain.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.GONE);
                        selectedFragment = ExploreFragment.newInstance();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        break;
                    case R.id.navigation_friends:
                        toolbar.setTitle("Friends");
                        ShowMain.setVisibility(View.GONE);
                        toolbar.setVisibility(View.VISIBLE);
                        selectedFragment = YourFriendsFragment.newInstance();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        break;
                    case R.id.navigation_newBlog:
                        toolbar.setTitle("Create/Update Blog");
                        ShowMain.setVisibility(View.GONE);
                        toolbar.setVisibility(View.VISIBLE);
                        selectedFragment = CreateNewBlogFragment.newInstance();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        break;
                    case R.id.navigation_Notification:
                        toolbar.setTitle("Notification");
                        fragmentTag = "Notification";
                        ShowMain.setVisibility(View.GONE);
                        toolbar.setVisibility(View.VISIBLE);
                        selectedFragment = NotificationsFragment.newInstance();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        break;
                    case R.id.navigation_profile:
                        toolbar.setTitle("Profile");
                        ShowMain.setVisibility(View.GONE);
                        toolbar.setVisibility(View.VISIBLE);
                        selectedFragment = Main_ProfileFragment.newInstance();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, selectedFragment);
                transaction.commit();
                return true;
            }
        });


        Filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExploreFragment.filterBlogs.performClick();
            }
        });

        fragmentTag = "nav_home";
        if (getIntent().hasExtra("update_blog")) {
            String bm = getIntent().getStringExtra("update_blog");
            String key = getIntent().getStringExtra("update_key");
            ShowMain.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            //BlogModel blogModel = new Gson().fromJson(getIntent().getStringExtra("blog"), BlogModel.class);
            Bundle bundle = new Bundle();
            //String blogmodel = (new Gson()).toJson(blogModel);
            bundle.putString("update_blog", bm);
            bundle.putString("update_key", key);
            CreateNewBlogFragment createNewBlogFragment = new CreateNewBlogFragment();
            createNewBlogFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, createNewBlogFragment).commit();
        } else if (getIntent().hasExtra("monetize_key")) {
            ShowMain.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            MonetizationFragment monetizationFragment = new MonetizationFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, monetizationFragment).commit();
        } else {
            openFragment(new ExploreFragment(), "nav_home");
        }
        db = FirebaseFirestore.getInstance();
        toolbar.setTitle("E-Blogger");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_camera);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.menu));

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


        iv_ic_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);

            }
        });
        UpDateToken();

    }

    public void UpDateToken() {
        String data = FirebaseInstanceId.getInstance().getToken();
        DocumentReference userDoc = db.collection("FcmIDs").document(userId);
        userDoc.update("TokenKey", data + "")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        CreateDateToken();

                    }
                });


    }

    public void CreateDateToken() {
        String data = FirebaseInstanceId.getInstance().getToken();
        FireBaseTokenMap.put("TokenKey", data);
        db.collection("FcmIDs").document(userId).set(FireBaseTokenMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomeActivity.this, "Data is successfully saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        ShowMain = (RelativeLayout) findViewById(R.id.ShowMain);
        Filter = (ImageView) findViewById(R.id.Filter);
        // mAppBarLayout = findViewById(R.id.toolbar2);
        iv_ic_menu = findViewById(R.id.ic_menu);
        Logo = findViewById(R.id.Logo);
        getNavHeaderTextImage();

    }

    public void getNavHeaderTextImage() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.nav_user_name);
        if (isUserNamePresent != "false") {
            nav_user.setText(sharedpreferences.getString("UserFirstName", "EBlog User"));
        } else {
            nav_user.setText("EBlog User");
        }
        imageView = (ImageView) hView.findViewById(R.id.nav_imageView);
        //imageView.setImageResource();
        if (isUserRegisteredAlready != "false") {
            loadImageFromStorage(sharedpreferences.getString("UserImagePath", "0"));
        } else {
            imageView.setImageResource(R.drawable.ic_user_dummy);
        }
    }


    private void loadImageFromStorage(String path) {

        try {
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = (ImageView) hView.findViewById(R.id.nav_imageView);
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    // Back button click handled by this method
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnv_booking_list);
            int seletedItemId = bottomNavigationView.getSelectedItemId();
            if (R.id.navigation_home != seletedItemId) {
                setHomeItem(HomeActivity.this);
            } else {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAffinity();
                            }
                        }).create().show();
            }



           /* android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment != null) {
                if(fragment.getTag() == "nav_home") {
                    finish();
                    //System.exit(0);
                    //super.onBackPressed();
                    //finishAndRemoveTask();
                } else {
                    Logo.setVisibility(View.VISIBLE);
                    openFragment(new ExploreFragment(), "nav_home");
                }
            } else {
                finish();
                //System.exit(0);
                //finishAndRemoveTask();
            }*/
        }
    }

    public static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.bnv_booking_list);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

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
            fragmentTag = "nav_home";
            toolbar.setTitle("E - Blogger");
            Logo.setVisibility(View.VISIBLE);
            //toolbar.setTitle("Explore Blogs");
            openFragment(exploreFragment, fragmentTag);
        } else if (id == R.id.nav_new_blog) {
            /*CreateNewBlogFragment createNewBlogFragment = new CreateNewBlogFragment();
            fragmentTag="create_new_fragment";
            toolbar.setTitle("Create/Update Blog");
            openFragment(createNewBlogFragment,fragmentTag);*/

            Intent intent = new Intent(HomeActivity.this, CreateNewBlogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        } else if (id == R.id.nav_blog_history) {
            Logo.setVisibility(View.GONE);
            ShowMain.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            YourBlogsFragment yourBlogsFragment = new YourBlogsFragment();
            fragmentTag = "nav_blog_history";
            toolbar.setTitle("Your Blogs");
            openFragment(yourBlogsFragment, fragmentTag);
        } else if (id == R.id.nav_monetize) {
            Logo.setVisibility(View.GONE);
            ShowMain.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            MonetizationFragment monetizationFragment = new MonetizationFragment();
            fragmentTag = "nav_monetize";
            toolbar.setTitle("Monetize Blog");
            openFragment(monetizationFragment, fragmentTag);
        }
       /* else if (id == R.id.nav_task) {
            TaskFragment taskFragment = new TaskFragment();
            fragmentTag="nav_task";
            openFragment(taskFragment,fragmentTag);
        }*/
        else if (id == R.id.nav_profile) {
            Intent intent = new Intent(HomeActivity.this, MyProfileAcivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_about_us) {
            FAQActivity faqActivity = new FAQActivity();
           /* fragmentTag="nav_faq";
            toolbar.setTitle("FAQ and About Us");
            openFragment(faqActivity,fragmentTag);*/
            Intent intent = new Intent(HomeActivity.this, FAQActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        } else if (id == R.id.nav_tc) {
            Logo.setVisibility(View.GONE);
            ShowMain.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            TermsConditionsFragment termsConditionsFragment = new TermsConditionsFragment();
            fragmentTag = "nav_tc";
            toolbar.setTitle("Terms & Conditions");
            openFragment(termsConditionsFragment, fragmentTag);
        } else if (id == R.id.nav_report_bug) {
           /* ReportBugActivity reportBugFragment = new ReportBugActivity();
            fragmentTag="nav_report_bug";
            toolbar.setTitle("Feedback/Report Bug");
            openFragment(reportBugFragment,fragmentTag);*/
            Intent intent = new Intent(HomeActivity.this, ReportBugActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment, fragmentTag);
        fragmentTransaction.addToBackStack(fragmentTag);
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

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }*/


}
