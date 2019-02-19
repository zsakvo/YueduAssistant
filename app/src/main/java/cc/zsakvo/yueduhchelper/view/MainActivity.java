package cc.zsakvo.yueduhchelper.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduhchelper.R;
import cc.zsakvo.yueduhchelper.adapter.CacheBooksAdapter;
import cc.zsakvo.yueduhchelper.bean.CacheBooks;
import cc.zsakvo.yueduhchelper.utils.Divider;
import cc.zsakvo.yueduhchelper.utils.SourceUtil;
import io.github.tonnyl.whatsnew.WhatsNew;
import io.github.tonnyl.whatsnew.item.WhatsNewItem;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView tv_CacheInfo;

    private List<String> bookNames = new ArrayList<>();
    ;
    private List<String> bookInfos = new ArrayList<>();
    ;
    private List<String> bookKeys = new ArrayList<>();
    ;

    private CacheBooksAdapter adapter;

    private String myBackupPath;

    private LinkedHashMap<String, CacheBooks> books;

    private String bookName;

    private String cachePath;

    private long autoBackupTime = 0;
    private long backupTime = 0;
    private long cacheTime = 0;
    private int syncType = 0;

    private AlertDialog progressDialog;
    private int progress;

    private HashMap<String, String> source;

    private String outPath;


    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public int bindMenu() {
        return R.menu.toolbar;
    }

    @Override
    public void clickMenu(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }

    @Override
    public void initView(View view) {
        // 绑定控件
        toolbar = $(R.id.cache_toolbar);
        tv_CacheInfo = $(R.id.cache_info);
        RecyclerView mRecyclerView = $(R.id.cache_recyclerView);

        //设置 Toolbar 信息
        toolbar.setTitle("阅读缓存提取");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);

        //RecyclerView 绑定适配器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new Divider(this, 36));
        adapter = new CacheBooksAdapter(bookNames, bookInfos);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void doOnStart() {

        tv_CacheInfo.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_CacheInfo.setText(getResources().getString(R.string.loading_export_info));
        tv_CacheInfo.setOnClickListener(null);

        cachePath = getSharedPreferences("settings", MODE_PRIVATE).getString("cachePath", Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.gedoor.monkeybook/cache/book_cache/");
        myBackupPath = getSharedPreferences("settings", MODE_PRIVATE).getString("backupPath", Environment.getExternalStorageDirectory().getAbsolutePath() + "/YueDu/");

        adapter.setOnItemClickListener((view, position) -> {
            bookName = bookNames.get(position);
            Intent intent = new Intent(this, ExportActivity.class);
            intent.putExtra("books", books.get(bookKeys.get(position)));
            intent.putExtra("cachePath", cachePath);
            startActivityForResult(intent, 0);
        });

        if (!getSharedPreferences("settings", MODE_PRIVATE).getBoolean("isFirst", true)) {
            if (AndPermission.hasPermissions(this, Permission.Group.STORAGE)) {
                scanBooks();
            } else {
                requestPermission();
            }
        } else {
            showFirstDialog();
        }
    }

    private void showFirstDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(" 提示 ")
                .setPositiveButton(" 知道了 ", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isFirst", false);
                    editor.apply();
                    editor.commit();
                    dialog.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermission();
                    }
                })
                .setMessage("1. 本程序需要存储权限以保证正常运行 \n2. 程序会优先扫描「阅读」的默认缓存文件夹.\n3. 默认输出文件夹为 /内置存储/Documents/YueDuTXT")
                .setCancelable(false)
                .create()
                .show();
    }

    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(permissions -> {

                    WhatsNew whatsNew = WhatsNew.newInstance(
                            new WhatsNewItem("改善", "重写异步线程，精简代码"),
                            new WhatsNewItem("优化", "优化排序逻辑，提高章节扫描速度"),
                            new WhatsNewItem("UI", "默认只导出 TXT 文件"));

                    whatsNew.setTitleColor(ContextCompat.getColor(this, R.color.colorAccent));
                    whatsNew.setTitleText(" 更新日志 v1.1.0209");
                    whatsNew.setButtonText(" 我知道了 ");
                    whatsNew.setButtonBackground(ContextCompat.getColor(this, R.color.colorAccent));
                    whatsNew.setButtonTextColor(ContextCompat.getColor(this, R.color.white));
                    whatsNew.setItemTitleColor(ContextCompat.getColor(this, R.color.colorAccent));
                    whatsNew.setItemContentColor(Color.parseColor("#808080"));
                    whatsNew.presentAutomatically(MainActivity.this);
                    scanBooks();
                })
                .onDenied(permissions -> {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle(" 提示 ")
                            .setPositiveButton(" 退出 ", (dialog, which) -> finish())
                            .setMessage(" 权限未授予，将无法正常运行！")
                            .setCancelable(false)
                            .create()
                            .show();
                })
                .start();
    }

    private void scanBooks() {

        Log.e(TAG, "scanBooks: begin! ");

        if (bookNames != null) {
            bookNames.clear();
        }
        if (bookInfos != null) {
            bookInfos.clear();
        }
        if (bookKeys != null) {
            bookKeys.clear();
        }
        adapter.notifyDataSetChanged();

        Observable.create((ObservableOnSubscribe<Void>) emitter -> {
            source = new HashMap<>();
            books = new LinkedHashMap<>();
            String autoBackupPath = myBackupPath + "autoSave/myBookShelf.json";
            String backupPath = myBackupPath + "myBookShelf.json";
            File autoBackupFile = new File(autoBackupPath);
            File backupFile = new File(backupPath);
            File bookCache = new File(cachePath);
            File readFile = null;
            if (autoBackupFile.exists()) this.autoBackupTime = autoBackupFile.lastModified();
            if (backupFile.exists()) this.backupTime = backupFile.lastModified();
            if (bookCache.exists() && bookCache.listFiles() != null) {
                if (bookCache.listFiles().length != 0) {
                    for (File cacheDir : bookCache.listFiles()) {
                        String cacheName = cacheDir.getName();
                        if (!cacheName.contains("-")) continue;
                        if (cacheDir.lastModified() > cacheTime)
                            cacheTime = cacheDir.lastModified();
                        String bookName = cacheName.split("-")[0];
                        source.put(bookName, source.get(bookName) + "," + cacheName.split("-")[1]);
                    }
                    if (autoBackupTime > backupTime) {
                        readFile = autoBackupFile;
                    } else {
                        readFile = backupFile;
                    }
                    if (autoBackupTime > backupTime && autoBackupTime > cacheTime) {
                        syncType = 0;
                        readFile = autoBackupFile;
                    } else if (backupTime > autoBackupTime && backupTime > cacheTime) {
                        syncType = 1;
                        readFile = backupFile;
                    } else if (cacheTime > autoBackupTime && cacheTime > backupTime) {
                        syncType = 2;
                    }

                    FileReader r = new FileReader(readFile);
                    BufferedReader br = new BufferedReader(r);
                    StringBuffer json = new StringBuffer();
                    String s;
                    while ((s = br.readLine()) != null) {
                        json = json.append(s).append("\n");
                    }
                    br.close();
                    JSONArray jsonArray = JSON.parseArray(json.toString());
                    if (syncType < 2) {
                        for (Object object : jsonArray) {
                            JSONObject jsonBook = (JSONObject) JSONObject.toJSON(object);
                            if (jsonBook.containsKey("bookInfoBean")) {
                                JSONObject bookInfoBean = (JSONObject) JSONObject.toJSON(jsonBook.get("bookInfoBean"));
                                CacheBooks cacheBook = new CacheBooks();
                                String name = Objects.requireNonNull(Objects.requireNonNull(bookInfoBean).get("name")).toString();
                                String author = "";
                                if (bookInfoBean.containsKey("author"))
                                    author = (String)bookInfoBean.get("author");
                                cacheBook.setName(name);
                                String origin = Objects.requireNonNull(bookInfoBean.get("origin")).toString();
                                cacheBook.setCachePath(cachePath + name
                                        + "-"
                                        + Objects.requireNonNull(bookInfoBean.get("tag"))
                                        .toString()
                                        .replace(":" +
                                                "//", "")
                                        .replace(".", ""));
                                cacheBook.setCacheInfo("作者：" + author + "\n" + "来源：" + origin);
                                cacheBook.setSourcePath(Objects.requireNonNull(source.get(name)).substring(5));


                                // epub 相关
                                String coverUrl = "";
                                String intro = "";
                                if (bookInfoBean.containsKey("coverUrl"))
                                    coverUrl = (String) bookInfoBean.get("coverUrl");
                                if (bookInfoBean.containsKey("introduce"))
                                    intro = (String) bookInfoBean.get("introduce");
                                cacheBook.setAuthor(author);
                                cacheBook.setIntro(intro);
                                cacheBook.setCoverUrl(coverUrl);
                                cacheBook.setDetail(Objects.requireNonNull(author).length()!=0&& Objects.requireNonNull(coverUrl).length()!=0&& Objects.requireNonNull(intro).length()!=0);
                                books.put(name, cacheBook);
                            }
                        }
                    }else {
                            HashMap<String, JSONObject> jsonMap = new HashMap<>();
                            for (Object object : jsonArray) {
                                JSONObject jsonBook = (JSONObject) JSONObject.toJSON(object);
                                JSONObject bookInfoBean = (JSONObject) JSONObject.toJSON(jsonBook.get("bookInfoBean"));
                                String name = Objects.requireNonNull(Objects.requireNonNull(bookInfoBean).get("name")).toString();
                                jsonMap.put(name, (JSONObject) JSONObject.toJSON(jsonBook.get("bookInfoBean")));
                            }
                            for (File cacheDir : bookCache.listFiles()) {
                                String cacheName = cacheDir.getName();
                                if (!cacheName.contains("-")) continue;
                                String bookName = cacheName.split("-")[0];
                                int i = 0;
                                for (File chapterCache : cacheDir.listFiles()) {
                                    String chapterCacheName = chapterCache.getName();
                                    if (!chapterCacheName.substring(chapterCacheName.lastIndexOf(".")).equals(".nb"))
                                        continue;
                                    i++;
                                }
                                CacheBooks cacheBook = new CacheBooks();
                                cacheBook.setName(bookName);
                                cacheBook.setCacheNum(i);
                                cacheBook.setCachePath(cacheDir.getAbsolutePath());
                                cacheBook.setSourcePath(Objects.requireNonNull(source.get(bookName)).substring(5));

                                // 修正缓存来源网址
                                String coverUrl = "";
                                String intro = "";
                                String author = "";

                                cacheName = cacheName.split("-")[1];
                                cacheBook.setCacheInfo("缓存数量：" + i + "\n" + "来源：" + SourceUtil.trans(cacheName));
                                if (jsonMap.containsKey(bookName)){
                                    JSONObject object = jsonMap.get(bookName);
                                    assert object != null;
                                    if (object.containsKey("author"))
                                        author = (String) object.get("author");
                                    if (object.containsKey("coverUrl"))
                                        coverUrl = (String) object.get("coverUrl");
                                    if (object.containsKey("introduce"))
                                        intro = (String) object.get("introduce");
                                }
                                cacheBook.setAuthor(author);
                                cacheBook.setIntro(intro);
                                cacheBook.setCoverUrl(coverUrl);
                                cacheBook.setDetail(Objects.requireNonNull(author).length() != 0 && Objects.requireNonNull(coverUrl).length() != 0 && Objects.requireNonNull(intro).length() != 0);

                                if (books.get(bookName) != null) {
                                    if (Objects.requireNonNull(books.get(bookName)).getCacheNum() < i) {
                                        books.put(bookName, cacheBook);
                                    }
                                } else {
                                    books.put(bookName, cacheBook);
                                }
                            }
                    }
                    emitter.onComplete();
                }
            } else {
                emitter.onError(new Throwable("no bookcaches！"));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "subscribe");
                    }

                    @Override
                    public void onNext(Void v) {
                        Log.d(TAG, "onNext: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "error");
                        Log.e(TAG, "scanBooks: " + "null");
                        tv_CacheInfo.setTextColor(Color.RED);
                        tv_CacheInfo.setText(getResources().getString(R.string.no_books));
                        tv_CacheInfo.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "complete");
                        showBooks();
                    }
                });


    }


    private void showBooks() {
        int booksNum;
        String type = "";
        switch (syncType) {
            case 0:
                type = "备份扫描";
                tv_CacheInfo.setText("备份扫描");
                break;
            case 1:
                type = "备份扫描";
                tv_CacheInfo.setText("备份扫描");
                break;
            case 2:
                type = "缓存扫描";
                tv_CacheInfo.setText("缓存扫描");
                break;
        }
        if (books == null || books.size() == 0) {
            tv_CacheInfo.setText("没有扫描到任何书籍，请点击以设置缓存读取路径");
            tv_CacheInfo.setTextColor(Color.RED);
            tv_CacheInfo.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
        } else {
            booksNum = books.size();
            tv_CacheInfo.setTextColor(getResources().getColor(R.color.colorAccent));
            tv_CacheInfo.setOnClickListener(null);
            tv_CacheInfo.setText(String.format(getResources().getString(R.string.sync_book_info), booksNum, type));
        }
        if (books != null && books.size() != 0) {
            for (String key : books.keySet()) {
                bookKeys.add(key);
                CacheBooks cb = books.get(key);
                assert cb != null;
                bookNames.add(cb.getName());
                bookInfos.add(cb.getCacheInfo());
            }
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            //sth
        }
    }
}
