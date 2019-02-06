package cc.zsakvo.yueduhchelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduhchelper.adapter.CacheBooksAdapter;
import cc.zsakvo.yueduhchelper.bean.CacheBooks;
import cc.zsakvo.yueduhchelper.listener.ReadCacheListener;
import cc.zsakvo.yueduhchelper.listener.SyncBooksListener;
import cc.zsakvo.yueduhchelper.listener.WriteFileListener;
import cc.zsakvo.yueduhchelper.task.ReadCache;
import cc.zsakvo.yueduhchelper.task.SyncBooks;
import cc.zsakvo.yueduhchelper.task.WriteFile;
import cc.zsakvo.yueduhchelper.utils.Divider;
import cc.zsakvo.yueduhchelper.utils.SnackbarUtil;

public class CacheHelperActivity extends AppCompatActivity implements SyncBooksListener,ReadCacheListener,WriteFileListener {

    private static final String TAG = "CacheHelperActivity";
    Toolbar toolbar;
    private Boolean autoMerge;
    private String myCachePath;
    private CacheBooksAdapter adapter;

    private List<String> bookNames = new ArrayList<>();
    private List<String> bookInfos = new ArrayList<>();
    private List<String> bookKeys = new ArrayList<>();

    private ProgressDialog progressDialog;
    private StringBuilder bookContent;
    private String bookName;

    private LinkedHashMap<String, CacheBooks> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_helper);

        toolbar = findViewById(R.id.cache_toolbar);
        toolbar.setTitle("阅读缓存提取");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.cache_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new Divider(this,36));

        adapter = new CacheBooksAdapter(bookNames,bookInfos);
        adapter.setOnItemClickListener(new CacheBooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showSingleChoiceDialog(position);
                bookName = bookNames.get(position);
            }
        });

        mRecyclerView.setAdapter(adapter);

        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("isFirst", true)) {
            showFirstDialog();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        bookNames.clear();
        bookInfos.clear();
        bookKeys.clear();

        autoMerge = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("cs_auto_merge", false);
        myCachePath = getSharedPreferences("settings", MODE_PRIVATE).getString("cachePath", Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.gedoor.monkeybook/cache/book_cache/");

        if (AndPermission.hasPermissions(this, Permission.Group.STORAGE)) {
            new SyncBooks(this, autoMerge).execute(myCachePath);
        } else {
            requestPermission();
        }
    }

    private void showFirstDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("提示")
                .setPositiveButton("知道了", (dialog, which) -> {
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
                .setMessage("1.本程序需要存储权限以保证正常运行\n2.程序会优先扫描「阅读」的默认缓存文件夹.\n3.默认输出文件夹为 /内置存储/Documents/YueDuTXT 目录")
                .setCancelable(false)
                .create()
                .show();
    }

    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(permissions -> {
                    new SyncBooks(this, autoMerge).execute(myCachePath);
                })
                .onDenied(permissions -> {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("提示")
                            .setPositiveButton("退出", (dialog, which) -> finish())
                            .setMessage("权限未授予，将无法正常运行！")
                            .setCancelable(false)
                            .create()
                            .show();
                })
                .start();
    }

    public void showSnackBar(String string) {
        SnackbarUtil.build(this, toolbar, string, Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void showBooks(LinkedHashMap<String, CacheBooks> books) {

        this.books = books;

        if (books == null || books.size() == 0) {
            this.bookNames = null;
            this.bookInfos = null;
            this.bookKeys = null;
        } else {
            for (String key : books.keySet()) {
                bookKeys.add(key);
                CacheBooks cb = books.get(key);
                if (autoMerge) {
                    assert cb != null;
                    bookNames.add(cb.getName());
                    bookInfos.add("总来源数目：" + cb.getBookSources().size() + "\n" + "总章节数：" + cb.getAllBookChapters() + "\n有效章节数：" + cb.getChapterNum().size());
                } else {
                    assert cb != null;
                    String source = cb.getBookSources().get(0).replace(Environment.getExternalStorageDirectory().getAbsolutePath(), "/内置存储");
                    bookNames.add(cb.getName());
                    bookInfos.add("总章节数：" + cb.getAllBookChapters() + "\n缓存路径：" + source);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void readCache(String content) {
        bookContent.append(content);
        writeFile(content, bookName);
    }

    private void writeFile(String content, String bookName) {
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/YueDuTXT";
        folderPath = getSharedPreferences("settings", MODE_PRIVATE).getString("outPath", folderPath) + "/";
        bookName += ".txt";
        new WriteFile(this).execute(content, folderPath, bookName);
    }

    @Override
    public void writeFileResult(Boolean b) {
        progressDialog.dismiss();
        if (b) {
            showSnackBar("导出成功！");
        } else {
            showSnackBar("导出失败！");
        }
    }

    String[] single_list = {"导出为 TXT", "导出为 Epub"};
    private void showSingleChoiceDialog(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择一个操作");
        builder.setSingleChoiceItems(single_list, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String str = single_list[which];
                dialog.dismiss();
                switch(which){
                    case 0:
                        Intent intent = new Intent(CacheHelperActivity.this, ExportActivity.class);
                        intent.putExtra("book", books.get(bookKeys.get(position)));
                        intent.putExtra("cp", myCachePath);
                        startActivityForResult(intent, 0);
                        break;
                    case 1:
                        break;
                        default:
                            break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                ArrayList<String> list = data.getStringArrayListExtra("cps");
                progressDialog = new ProgressDialog(this);
                bookContent = new StringBuilder();
                progressDialog.setProgress(0);
                progressDialog.setTitle("合并中，请稍后……");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                new ReadCache(this, progressDialog, 0).execute("list", list);
            }
        }
    }
}
