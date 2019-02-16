package cc.zsakvo.yueduhchelper.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduhchelper.R;
import cc.zsakvo.yueduhchelper.adapter.ExportBooksAdapter;
import cc.zsakvo.yueduhchelper.bean.CacheBooks;
import cc.zsakvo.yueduhchelper.bean.ExportChapter;
import cc.zsakvo.yueduhchelper.utils.Divider;
import cc.zsakvo.yueduhchelper.utils.SourceUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ExportActivity extends BaseActivity {

    private static final String TAG = "ExportActivity";
    private Toolbar toolbar;

    private String cacheFilePath;
    private List<ExportChapter> exportArray;
    private ExportBooksAdapter adapter;

    private List<Boolean> flag;

    private List<File> cacheFiles;

    private String bookName;
    private TextView exportInfo;

    private String[] source;
    private String[] sourceList;
    private int checkedItem;


    private List<String> chapterNames;
    private List<String> chapterFilePath;


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
        return R.layout.activity_export;
    }

    @Override
    public int bindMenu() {
        return R.menu.export_menu;
    }

    @Override
    public void clickMenu(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_check_invert:
                chooseInvert();
                break;
            case R.id.export_txt:
                exportTXT();
                break;
        }
    }

    @Override
    public void initView(View view) {
        //设置 Toolbar
        toolbar = $(R.id.export_toolbar);
        toolbar.setTitle("导出为TXT");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            @SuppressLint("PrivateResource") Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            assert upArrow != null;
            upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        exportInfo = $(R.id.export_info);

        //初始化 RcyclerView
        exportArray = new ArrayList<>();
        flag = new ArrayList<>();
        adapter = new ExportBooksAdapter(exportArray, flag);
        RecyclerView mRecyclerView = $(R.id.export_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new Divider(this, 24));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        CacheBooks cb = (CacheBooks) getIntent().getSerializableExtra("txt");
        bookName = cb.getName();
        cacheFilePath = cb.getCachePath();
        if (cb.getSourcePath() != null) {
            source = cb.getSourcePath().split(",");
            sourceList = new String[source.length];
            for (int i = 0; i < source.length; i++) {
                sourceList[i] = SourceUtil.trans(source[i]);
                if (source[i].equals(cacheFilePath.split("-")[1])) checkedItem = i;
            }
        }
    }

    private void chooseInvert() {
        for (int i = 0; i < flag.size(); i++) {
            flag.set(i, !flag.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private void exportTXT() {
        ArrayList<String> chapters = new ArrayList<>();
        for (int i = 0; i < flag.size(); i++) {
            if (flag.get(i)) {
                chapters.add(chapterFilePath.get(i));
            }
        }
        if (chapters.size() == 0) {
            showSnackBar( "请至少勾选一章", toolbar);
        } else {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("cps", chapters);
            intent.putExtra("cfp", cacheFilePath);
            setResult(0, intent);
            finish();
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (exportArray.size() == 0) {
            menu.findItem(R.id.export_check_invert).setVisible(false);
            menu.findItem(R.id.export_txt).setVisible(false);
        } else {
            menu.findItem(R.id.export_check_invert).setVisible(true);
            menu.findItem(R.id.export_txt).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    private void showChapters() {

        if (chapterNames == null) {
            exportInfo.setTextColor(Color.RED);
            exportInfo.setText("未扫描到任何章节！");
        } else {
            for (int i = 0; i < chapterNames.size(); i++) {
                exportArray.add(new ExportChapter(chapterNames.get(i), false));
                flag.add(true);
            }
            exportInfo.setTextColor(getResources().getColor(R.color.colorAccent));
            exportInfo.setText(String.format(getResources().getString(R.string.export_info), bookName, chapterNames.size(), SourceUtil.trans(cacheFilePath.split("-")[1])));
            adapter.notifyDataSetChanged();
        }
        exportInfo.setOnClickListener(v -> {
            if (source.length <= 1) {
                showSnackBar("只有一个缓存源，无需切换", toolbar);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ExportActivity.this);
                builder.setTitle("切换缓存源");
                builder.setSingleChoiceItems(sourceList, checkedItem, (dialog, which) -> {
                    exportInfo.setText(getResources().getString(R.string.loading_export_info));
                    exportInfo.setOnClickListener(null);
                    checkedItem = which;
                    dialog.dismiss();
                    cacheFilePath = cacheFilePath.replace(cacheFilePath.split("-")[1], source[which]);
                    exportArray.clear();
                    flag.clear();
                    scanChapters();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void doOnStart() {
        scanChapters();
    }

    @SuppressLint("DefaultLocale")
    private void scanChapters() {
        exportInfo.setOnClickListener(null);
        Observable.create((ObservableOnSubscribe<Void>) emitter -> {
            cacheFiles = new ArrayList<>();
            chapterNames = new ArrayList<>();
            chapterFilePath = new ArrayList<>();

            File[] files = new File(cacheFilePath).listFiles();
            LinkedHashMap<Integer,String> map = new LinkedHashMap<>();

            for (File f : files) {
                int chapterSN = Integer.parseInt(f.getName().split("-")[0]);
                String chapterName = f.getName().split("-")[1].replace(".nb", "");
                map.put(chapterSN,chapterName);
            }
            List<Integer> snList = new ArrayList<>(map.keySet());
            Collections.sort(snList);
            for (int i=0;i<snList.size();i++){
                int sn = snList.get(i);
                String name = map.get(sn);
                chapterNames.add(name);
                chapterFilePath.add(cacheFilePath+"/"+String.format("%05d", snList.get(i))+"-"+name+".nb");
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread())
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
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "complete");
                        showChapters();
                    }
                });
    }

}
