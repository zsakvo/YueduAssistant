package cc.zsakvo.yueduassistant.utils;

import android.app.Activity;
import android.os.Build;
import android.view.Window;

public class StatusbarUtil {

    public static void setStatusBarColor(Activity activity, int colorId) {
        Window window = activity.getWindow();
        window.setStatusBarColor(activity.getResources().getColor(colorId));
    }


}
