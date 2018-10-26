package util;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;

import com.jhordyabonia.ag.R;

public class Style {
    public static int STYLE=R.color.colorGreen;
    public static String _STYLE="style";
    public static void bar(Activity activity) {
            activity.getActionBar().setBackgroundDrawable(
                    new ColorDrawable(activity.getResources().getColor(STYLE)));
    }
    public static void set(View v){

        View vTmp = v.findViewById(R.id.pager_title_strip);
        if (vTmp != null)
            vTmp.setBackgroundResource(STYLE);

        vTmp = v.findViewById(R.id.paginator);
        if (vTmp != null)
            vTmp.setBackgroundResource(STYLE);

        ImageView src= v.findViewById(R.id.add);
        if(src!=null){
            Drawable background = src.getBackground();
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(v.getResources().getColor(STYLE));
        }
    }
}
