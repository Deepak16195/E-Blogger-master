package in.org.eonline.eblog.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import in.org.eonline.eblog.Activities.Login;
import in.org.eonline.eblog.R;

/**
 * Created by aditya on 23-11-2018.
 */

public class CommonDialog {
    private static CommonDialog ourInstance;
    private static Context ctx;

    public static CommonDialog getInstance() {
        if (ourInstance == null) {
            ourInstance = new CommonDialog();
        }
        return ourInstance;
    }

    private CommonDialog() {
    }


    public void showErrorDialog(Context context, int image){
        this.ctx = context;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.error_dialog);
        ImageView dialogImage = (ImageView) dialog.findViewById(R.id.dialog_image);
        dialogImage.setImageResource(image);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public  Dialog showProgressDialog(Context context){
        this.ctx = context;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressbar);
        ProgressBar progress = (ProgressBar) dialog.findViewById(R.id.progressBarServerData);
        //progress.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }
}
