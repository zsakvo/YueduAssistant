package cc.zsakvo.yueduassistant.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.snackbar.Snackbar;
import com.orhanobut.logger.Logger;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;
import org.litepal.exceptions.DataSupportException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import cc.zsakvo.yueduassistant.MyApplication;
import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.bean.CacheBook;
import cc.zsakvo.yueduassistant.bean.Source;
import cc.zsakvo.yueduassistant.listener.PathListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

public class SourceUtil {

    public static void update(FragmentManager fm,Context context,View view) {
        FileChooseUtil sfcDialog = new FileChooseUtil();
        sfcDialog.setOnChosenListener(new FileChooseUtil.SimpleFileChooserListener() {
            @Override
            public void onFileChosen(File file) {
                SourceUtil sutil = new SourceUtil();
                sutil.rwFile(file,context,view);
            }

            @Override
            public void onDirectoryChosen(File directory) {

            }

            @Override
            public void onCancel() {

            }
        });
        sfcDialog.show(fm, "FileChoose");
    }

    private void rwFile(File file,Context context, View view) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在升级来源数据库……");
        progressDialog.show();
        Observable.create(new ObservableOnSubscribe<JSONObject>() {
            @Override
            public void subscribe(ObservableEmitter<JSONObject> emitter) throws Exception {
                StringBuilder res = new StringBuilder();
                try {
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                    BufferedReader br = new BufferedReader(reader);
                    String s;
                    while ((s = br.readLine()) != null) {
                        res.append(s);
                    }
                    JSONArray jsonArray = JSON.parseArray(res.toString());
                    for (Object object : jsonArray) {
                        JSONObject jsonObject = (JSONObject) object;
                        String sourceName = (String) jsonObject.get("bookSourceName");
                        String sourceKey = ((String) jsonObject.get("bookSourceUrl"))
                                .replace("://", "")
                                .replace("://", "")
                                .replace(".", "");
                        JSONObject nObject = new JSONObject();
                        nObject.put("sKey", sourceKey);
                        nObject.put("sName", sourceName);
                        emitter.onNext(nObject);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    SQLiteDatabase db;

                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.d("subscribe");
                        db = LitePal.getDatabase();
                        LitePal.deleteAll("source");
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Source source = new Source();
                        source.setKey((String) jsonObject.get("sKey"));
                        source.setName((String) jsonObject.get("sName"));
                        source.save();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.toString());
                        progressDialog.dismiss();
                        SnackbarUtil.build(context,view,"数据处理失败", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("数据升级完毕");
                        progressDialog.dismiss();
                        SnackbarUtil.build(context,view,"数据升级成功", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public static String queryName(String key) {
        SQLiteDatabase db = LitePal.getDatabase();
        Cursor cursor = db.query("source", null, "key=?", new String[]{key}, null, null, null);
        String name = "";
        if (cursor.getCount()==0){
            String sql = "SELECT * FROM source where key like '%"+key.substring(5)+"%'";
            cursor = db.rawQuery(sql,null);
            if (cursor.getCount()==0){
                name = key;
            }else {
                while (cursor.moveToNext()) {
                    name = cursor.getString(cursor.getColumnIndex("name"));
                }
            }
        }else {
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex("name"));
            }
        }
        return name;
    }
}
