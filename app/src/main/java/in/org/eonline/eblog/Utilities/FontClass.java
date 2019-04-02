package in.org.eonline.eblog.Utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FontClass {

    private static FontClass fontInstance;
    private static Context ctx;
    Typeface tf;
    private FontClass() {
        tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-Regular.ttf");
    }

    public static FontClass getInstance(Context context) {
        ctx=context;
        if (fontInstance == null) {
            fontInstance = new FontClass();
        }
        return fontInstance;
    }

    public void setFontToAllChilds(ViewGroup myMostParentLayout) {
        int childCount = myMostParentLayout.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View child = myMostParentLayout.getChildAt(i);

            if (child instanceof ViewGroup)
                setFontToAllChilds((ViewGroup) child);
            else if (child instanceof TextView)
                ((TextView) child).setTypeface(tf);
        }
    }
}
