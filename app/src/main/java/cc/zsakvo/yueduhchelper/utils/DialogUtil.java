package cc.zsakvo.yueduhchelper.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import cc.zsakvo.yueduhchelper.view.EpubEditorActivity;
import cc.zsakvo.yueduhchelper.view.ExportActivity;
import cc.zsakvo.yueduhchelper.bean.CacheBooks;
import cc.zsakvo.yueduhchelper.listener.ChangePathListener;
import cc.zsakvo.yueduhchelper.view.MainActivity;

import static android.content.Context.MODE_PRIVATE;

public class DialogUtil {

    private static String TAG = "DialogUtil";

    public static void exportTypeDialog(LinkedHashMap<String, CacheBooks> books, List<String> bookKeys,int position,String myCachePath,Context mContext){
        String[] single_list = {"导出为 TXT", "导出为 Epub"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("选择一个操作");
        builder.setSingleChoiceItems(single_list, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent;
                switch(which) {
                    case 0:
                        intent = new Intent(mContext, ExportActivity.class);
                        intent.putExtra("book", books.get(bookKeys.get(position)));
                        intent.putExtra("cp", myCachePath);
                        if (mContext instanceof MainActivity) {
                            ((MainActivity) mContext).startActivityForResult(intent, 0);
                        } else {
                            Log.e(TAG, "mContext should be an instanceof Activity.");
                        }
                        break;
                    case 1:
                        intent = new Intent(mContext, EpubEditorActivity.class);
                        CacheBooks cb = (CacheBooks)books.get(bookKeys.get(position));
                        intent.putExtra("book", books.get(bookKeys.get(position)));
                        intent.putExtra("cp", myCachePath);
                        if (mContext instanceof MainActivity) {
                            ((MainActivity) mContext).startActivityForResult(intent, 0);
                        } else {
                            Log.e(TAG, "mContext should be an instanceof Activity.");
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

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
