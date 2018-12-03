package cc.zsakvo.yueduhchelper.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import cc.zsakvo.yueduhchelper.R;

public class SnackbarUtil {
    public static Snackbar build(Context context,View view, String str, int time){
        Snackbar snackbar = Snackbar.make(view,str,time);
        View mView = snackbar.getView();
        mView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        TextView tvSnackbarText = (TextView) mView.findViewById(com.google.android.material.R.id.snackbar_text);
        tvSnackbarText.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        return snackbar;
    }
}
