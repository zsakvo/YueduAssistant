package cc.zsakvo.yueduhchelper;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
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
import moe.shizuku.preference.SimpleMenuPreference;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class BooksCacheFragment extends PreferenceFragment implements SyncBooksListener,Preference.OnPreferenceClickListener,ReadCacheListener,WriteFileListener,Preference.OnPreferenceChangeListener {

    private String myCachePath;
    private CacheHelperActivity cha;
    private StringBuilder bookContent;
    private String bookName;
    private ProgressDialog progressDialog;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        cha = (CacheHelperActivity)getActivity();

        getPreferenceManager().setDefaultPackages(new String[]{BuildConfig.APPLICATION_ID + "."});
        getPreferenceManager().setSharedPreferencesName("settings");
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        setPreferencesFromResource(R.xml.books_cache,null);
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



    @Override
    public void showBooks( Map<String, Integer> map) {
        PreferenceScreen p = getPreferenceScreen();
        p.removeAll();
        PreferenceCategory preferenceCategory = new PreferenceCategory(cha);

        Map<String, Integer> map1 = map;
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

                SimpleMenuPreference simpleMenuPreference = new SimpleMenuPreference(cha);

                simpleMenuPreference.setTitle(ba[0]);
                simpleMenuPreference.setEntries(new CharSequence[]{"导出为TXT"});
                simpleMenuPreference.setEntryValues(new CharSequence[]{"0"});
                simpleMenuPreference.setSummary("来源："+ba[1]+"\n"+"缓存章节："+map.get(key));
                simpleMenuPreference.setKey(key);
                simpleMenuPreference.setOnPreferenceClickListener(this);
                simpleMenuPreference.setOnPreferenceChangeListener(this);
                p.addPreference(simpleMenuPreference);

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
            cha.showSnackBar("导出失败！");
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (newValue.toString()){
//            case "0":
//                progressDialog = new ProgressDialog(cha);
//                bookContent = new StringBuilder();
//                bookName = preference.getKey().split("-")[0];
//                String bookPath = myCachePath + "/" + preference.getKey() + "/";
//                File bookFile = new File(bookPath);
//                progressDialog.setProgress(0);
//                progressDialog.setTitle("合并中，请稍后……");
//                progressDialog.setCancelable(false);
//                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                progressDialog.show();
//                new ReadCache(this, progressDialog, 0).execute("file",bookFile);
//                break;
            case "0":
                String bookPath = myCachePath + "/" + preference.getKey() + "/";
                bookName = preference.getKey().split("-")[0];
                bookPath = myCachePath + "/" + preference.getKey() + "/";
                Intent intent = new Intent(cha,TextExportActivity.class);
                intent.putExtra("bp",preference.getKey());
                startActivityForResult(intent,0);
                break;
                default:
                    break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if (data!=null) {
                ArrayList<String> list = data.getStringArrayListExtra("cps");
                progressDialog = new ProgressDialog(cha);
                bookContent = new StringBuilder();
                progressDialog.setProgress(0);
                progressDialog.setTitle("合并中，请稍后……");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                new ReadCache(this, progressDialog, 0).execute("list",list);
            }
        }
    }

}
