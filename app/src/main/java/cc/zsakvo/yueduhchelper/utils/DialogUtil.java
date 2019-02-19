package cc.zsakvo.yueduhchelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

import androidx.fragment.app.FragmentManager;
import cc.zsakvo.yueduhchelper.listener.ChangePathListener;

import static android.content.Context.MODE_PRIVATE;

public class DialogUtil {

    private static String TAG = "DialogUtil";

    public static void fileChoose(final String str, Context mContext, FragmentManager fm, ChangePathListener cpl) {
        DirChooseUtil sfcDialog = new DirChooseUtil();
        sfcDialog.setOnChosenListener(new DirChooseUtil.SimpleFileChooserListener() {
            @Override
            public void onFileChosen(File file) {
            }

            @Override
            public void onDirectoryChosen(File directory) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(str, directory.getAbsolutePath());
                cpl.changePath(str, directory.getAbsolutePath()+"/");
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
