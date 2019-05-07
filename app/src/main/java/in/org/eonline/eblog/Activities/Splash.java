package in.org.eonline.eblog.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import in.org.eonline.eblog.HomeActivity;
import in.org.eonline.eblog.R;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    Thread splashTread;
    LinearLayout show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To make activity Full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        show = findViewById(R.id.show);
        StartAnimations();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash.this, Login.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void StartAnimations() {
        Animation loadAnimation;
        loadAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        loadAnimation.reset();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lin_lay);
        linearLayout.clearAnimation();
        linearLayout.startAnimation(loadAnimation);
        loadAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_anim);
        loadAnimation.reset();
        show.clearAnimation();
        show.startAnimation(loadAnimation);
       /* splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 400;
                    while (waited < 2000) {
                        sleep(200);
                        waited += 100;
                    }
                    Intent intent = new Intent(Splash.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    Splash.this.finish();
                } catch (InterruptedException e) {

                } finally {
                    Splash.this.finish();
                }

            }
        };
        splashTread.start();*/

    }

}