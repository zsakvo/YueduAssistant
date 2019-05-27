package cc.zsakvo.yueduassistant.utils;

import android.content.Context;
import android.os.Environment;

import static android.content.Context.MODE_PRIVATE;

public class SpUtil {

    private static String defaultCacheDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.gedoor.monkeybook/files/book_cache";
    private static String defaultBackupPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YueDu";
    private static String defaultOutputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/YueDu";
    public static String configPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YueDu/myBookShelf.json";

    public static String getCacheDirPath(Context context){
        return context.getSharedPreferences("settings", MODE_PRIVATE).getString("cache_path", defaultCacheDirPath);
    }

    public static void setCacheDirPath(Context context){

    }

    public static String getBackupPath(Context context){
        return context.getSharedPreferences("settings", MODE_PRIVATE).getString("backup_path", defaultBackupPath);
    }

    public static String getOutputPath(Context context){
        return context.getSharedPreferences("settings", MODE_PRIVATE).getString("output_path", defaultOutputPath);
    }

    public static int getScanType(Context context){
        return context.getSharedPreferences("settings", MODE_PRIVATE).getInt("scan_type", 0);
    }

    public static boolean getAutoDel(Context context){
        return context.getSharedPreferences("settings", MODE_PRIVATE).getBoolean("auto_del",false);
    }
}
