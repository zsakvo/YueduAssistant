package cc.zsakvo.yueduassistant.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentManager;

import java.io.File;

import cc.zsakvo.yueduassistant.listener.PathListener;

import static android.content.Context.MODE_PRIVATE;

public class DialogUtil {

    private static String TAG = "DialogUtil";
    private static String SPNAME = "cc.zsakvo.yueduassistant_preferences";

    public static void fileChoose(final String str, Context mContext, FragmentManager fm, PathListener pl,String rootPath) {
        DirChooseUtil sfcDialog = new DirChooseUtil(rootPath);
        sfcDialog.setOnChosenListener(new DirChooseUtil.SimpleFileChooserListener() {
            @Override
            public void onFileChosen(File file) {

            }

            @Override
            public void onDirectoryChosen(File directory) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(SPNAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(str, directory.getAbsolutePath());
                pl.changePath(str, directory.getAbsolutePath());
                editor.apply();
                editor.commit();
            }

            @Override
            public void onCancel() {

            }
        });
        sfcDialog.show(fm, "DirChooseUtil");
    }
}