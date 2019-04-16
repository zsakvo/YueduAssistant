package cc.zsakvo.yueduassistant.utils;

import android.content.Context;
import android.os.Environment;

import static android.content.Context.MODE_PRIVATE;

public class SpUtils {

    public static String defaultCacheDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.gedoor.monkeybook/cache/book_cache";
    public static String configPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YueDu/myBookShelf.json";

    public static String getCacheDirPath(Context context){
        return context.getSharedPreferences("settings", MODE_PRIVATE).getString("cache_dir", defaultCacheDirPath);
    }

    public static void setCacheDirPath(Context context){

    }

    public static int getScanType(Context context){
        return context.getSharedPreferences("settings", MODE_PRIVATE).getInt("scan_type", 0);
    }
}
