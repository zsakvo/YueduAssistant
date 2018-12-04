package cc.zsakvo.yueduhchelper;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import cc.zsakvo.yueduhchelper.listener.ReadCacheListener;
import cc.zsakvo.yueduhchelper.listener.SyncBooksListener;
import cc.zsakvo.yueduhchelper.listener.WriteFileListener;
import cc.zsakvo.yueduhchelper.task.ReadCache;
import cc.zsakvo.yueduhchelper.task.SyncBooks;
import cc.zsakvo.yueduhchelper.task.WriteFile;
import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceCategory;
import moe.shizuku.preference.PreferenceFragment;
import moe.shizuku.preference.PreferenceScreen;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class BooksCacheFragment extends PreferenceFragment implements SyncBooksListener,Preference.OnPreferenceClickListener,ReadCacheListener,WriteFileListener {

    private String myCachePath;
    private CacheHelperActivity cha;
    private StringBuilder bookContent;
    private String bookName;
    private ProgressDialog progressDialog;
    private  Map<String, Integer> map;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        cha = (CacheHelperActivity)getActivity();

        getPreferenceManager().setDefaultPackages(new String[]{BuildConfig.APPLICATION_ID + "."});
        getPreferenceManager().setSharedPreferencesName("settings");
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        setPreferencesFromResource(R.xml.books_cache,null);

//        requestPermission();

    }

    private void getBooksCache(){
        myCachePath = cha.getSharedPreferences("settings",MODE_PRIVATE).getString("cachePath",Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/com.gedoor.monkeybook/cache/book_cache/");
        new SyncBooks(this).execute(myCachePath);
    }


    private void writeFile(String content,String bookName){
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Documents/YueDuTXT";
        folderPath = cha.getSharedPreferences("settings",MODE_PRIVATE).getString("outPath",folderPath)+"/";
        bookName+=".txt";
        new WriteFile(this).execute(content,folderPath,bookName);
    }

//    private void requestPermission(){
//        AndPermission.with(this)
//                .runtime()
//                .permission(Permission.Group.STORAGE)
//                .onGranted(permissions -> {
//                    getBooksCache();
//                })
//                .onDenied(permissions -> {
//                    requestPermission();
//                })
//                .start();
//    }

    @Override
    public void showBooks( Map<String, Integer> map) {
        PreferenceScreen p = getPreferenceScreen();
        p.removeAll();
        PreferenceCategory preferenceCategory = new PreferenceCategory(cha);

        this.map = map;
        if (map== null||map.isEmpty()){
            preferenceCategory.setTitle("未扫描到书籍缓存");
            p.addPreference(preferenceCategory);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cha);
            alertDialogBuilder.setTitle("提示")
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(cha,SettingsActivity.class));
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setMessage("没有扫描到已缓存内容，是否需要自行去设置缓存路径？")
                    .setCancelable(false)
                    .create()
                    .show();
            Log.e(TAG, "showBooks: "+"null !" );
        }else {
            preferenceCategory.setTitle("扫描到的书籍");
            p.addPreference(preferenceCategory);
            for (String key : map.keySet()) {
                Log.e(TAG, "showBooks: "+key );
                if (!key.contains("-")) continue;
                String[] ba = key.split("-");
                Preference preference = new Preference(cha);
                preference.setTitle(ba[0]);
                preference.setSummary("来源："+ba[1]+"\n"+"缓存章节："+map.get(key));
                preference.setKey(key);
                preference.setOnPreferenceClickListener(this);
                p.addPreference(preference);
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        getBooksCache();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
            progressDialog = new ProgressDialog(cha);
            bookContent = new StringBuilder();
            bookName = preference.getKey().split("-")[0];
            String bookPath = myCachePath + "/" + preference.getKey() + "/";
            File bookFile = new File(bookPath);

        int bookChapNum = map.get(preference.getKey());

            progressDialog.setProgress(0);
            progressDialog.setTitle("合并中，请稍后……");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();

        int progress = 0;
        new ReadCache(this, progressDialog, progress).execute(bookFile);
        return false;
    }

    @Override
    public void readCache(String content) {
        bookContent.append(content);
        writeFile(content,bookName);
    }

    @Override
    public void writeFileResult(Boolean b) {
        progressDialog.dismiss();
        Snackbar snackbar;
        if (b){
            cha.showSnackBar("导出成功！");
        }else {
            cha.showSnackBar("到处失败！");
        }
    }
}
