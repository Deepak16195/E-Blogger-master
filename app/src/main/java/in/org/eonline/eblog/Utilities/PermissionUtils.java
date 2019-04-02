package in.org.eonline.eblog.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtils {
    private static PermissionUtils permissionUtil;
    private static Context ctx;

    private PermissionUtils() {
    }

    public static PermissionUtils getInstance(Context context) {
        ctx = context;
        if (permissionUtil == null) {
            permissionUtil = new PermissionUtils();
        }
        return permissionUtil;
    }

    public boolean isPermissionGranted(String permssion) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ctx, permssion) != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void requestPermission(String[] permission, int permissionCode) {
        ActivityCompat.requestPermissions((Activity)ctx, permission, permissionCode);
    }
}
