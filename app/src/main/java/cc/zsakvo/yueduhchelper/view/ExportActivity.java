package cc.zsakvo.yueduhchelper.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduhchelper.R;
import cc.zsakvo.yueduhchelper.adapter.ExportBooksAdapter;
import cc.zsakvo.yueduhchelper.bean.CacheBooks;
import cc.zsakvo.yueduhchelper.bean.ExportChapter;
import cc.zsakvo.yueduhchelper.utils.Divider;
import cc.zsakvo.yueduhchelper.utils.EpubUtil;
import cc.zsakvo.yueduhchelper.utils.SourceUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ExportActivity extends BaseActivity {

    private static final String TAG = "ExportActivity" + "";
    private Toolbar toolbar;
    private String bookName;
    private String author;
    private String intro;
    private String coverUrl;

    private TextView tvBookName;
    private TextView tvAuthor;
    private TextView tvIntro;
    private ImageView ivCover;
    private TextView exportInfo;
    private RelativeLayout epubInfo;
    private SpeedDialView speedDialView;
    private CoordinatorLayout coord;

    private String cacheFilePath;
    private List<ExportChapter> exportArray;
    private ExportBooksAdapter adapter;

    private List<Boolean> flag;

    private List<File> cacheFiles;

    private String[] source;
    private String[] sourceList;
    private int checkedItem;

    private List<String> chapterNames;
    private List<String> chapterFilePath;

    private boolean exportMore;


    private AlertDialog progressDialog;
    private int progress;
    private String outPath;
    private boolean autoDel;

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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void doOnStart() {
        scanChapters();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.export_menu, menu);
        return true;
    }

    @Override
    public void clickMenu(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_check_invert:
                chooseInvert();
                break;
            case R.id.exchange_source:
                if (source.length <= 1) {
                    showSnackBar("只有一个缓存源，无需切换", speedDialView);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ExportActivity.this);
                    builder.setTitle("切换缓存源");
                    builder.setSingleChoiceItems(sourceList, checkedItem, (dialog, which) -> {
                        exportInfo.setText(getResources().getString(R.string.loading_export_info));
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
                break;
        }
    }

    @Override
    public void initView(View view) {
        toolbar = $(R.id.epub_toolbar);
        toolbar.setTitle("导出书籍");
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

        exportInfo = $(R.id.epub_cache_info);
        coord = $(R.id.coord);

        epubInfo = $(R.id.epub_info);
        tvBookName = $(R.id.epub_name);
        tvAuthor = $(R.id.epub_author);
        tvIntro = $(R.id.epub_intro);
        ivCover = $(R.id.epub_cover);
        speedDialView = $(R.id.speedDial);

        exportArray = new ArrayList<>();
        flag = new ArrayList<>();

        RecyclerView mRecyclerView = $(R.id.export_epub_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new Divider(this, 24));

        adapter = new ExportBooksAdapter(exportArray, flag);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        exportMore = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("ad_export_more", false);
        autoDel = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("ad_auto_del", false);
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/YueDuTXT";
        outPath = getSharedPreferences("settings", MODE_PRIVATE).getString("outPath", folderPath) + "/";

        CacheBooks cb = (CacheBooks) getIntent().getSerializableExtra("books");
        cacheFilePath = cb.getCachePath();
        if (cb.isDetail()) {
            epubInfo.setVisibility(View.VISIBLE);
        } else {
            epubInfo.setVisibility(View.GONE);
        }
        bookName = cb.getName();
        author = cb.getAuthor();
        intro = cb.getIntro();
        coverUrl = cb.getCoverUrl();
        tvBookName.setText(bookName);
        tvAuthor.setText(String.format(getResources().getString(R.string.epub_author), author));
        tvIntro.setText(String.format(getResources().getString(R.string.epub_intro), intro));
        if (coverUrl == null) {
            ivCover.setImageBitmap(getNoCover());
        } else {
            Glide.with(this)
                    .load(coverUrl)
                    .into(ivCover);
        }

        if (!new File(outPath).exists()) {
            if (!new File(outPath).mkdirs()) {
                showSnackBar("环境初始化失败……！", speedDialView);
                return;
            }
        }

        if (cb.getSourcePath() != null) {
            source = cb.getSourcePath().split(",");
            sourceList = new String[source.length];
            for (int i = 0; i < source.length; i++) {
                sourceList[i] = SourceUtil.trans(source[i]);
                if (source[i].equals(cacheFilePath.split("-")[1])) checkedItem = i;
            }
        }
        if (exportMore) {
            //Sth
            speedDialView.addActionItem(
                    new SpeedDialActionItem.Builder(R.id.fab_epub, R.drawable.ic_epub)
                            .setLabel("导出为Epub")
                            .create()
            );
            speedDialView.addActionItem(
                    new SpeedDialActionItem.Builder(R.id.fab_txt, R.drawable.ic_txt)
                            .setLabel("导出为TXT")
                            .create()
            );
            speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
                @Override
                public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                    switch (speedDialActionItem.getId()) {
                        case R.id.fab_txt:
                            exportTXT();
                            return false;
                        case R.id.fab_epub:
                            exportEpub();
                            return false;
                        default:
                            return false;
                    }
                }
            });
        } else {
            speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
                @Override
                public boolean onMainActionSelected() {
                    exportTXT();
                    return false;
                }

                @Override
                public void onToggleChanged(boolean isOpen) {

                }
            });
        }
    }

    private void exportTXT() {
        ArrayList<String> chapters = new ArrayList<>();
        for (int i = 0; i < flag.size(); i++) {
            if (flag.get(i)) {
                chapters.add(chapterFilePath.get(i));
            }
        }
        if (chapters.size() == 0) {
            showSnackBar("请至少勾选一章", speedDialView);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExportActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.loading_dialog);
            progressDialog = builder.create();
            writeCacheForTXT(cacheFilePath, chapters);
        }
    }

    private void exportEpub() {
        ArrayList<String> chapters = new ArrayList<>();
        for (int i = 0; i < flag.size(); i++) {
            if (flag.get(i)) {
                chapters.add(chapterFilePath.get(i));
            }
        }
        if (chapters.size() == 0) {
            showSnackBar("请至少勾选一章", speedDialView);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExportActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.loading_dialog);
            progressDialog = builder.create();
            writeCacheForEpub(cacheFilePath, chapters);
        }
    }

    private void writeCacheForEpub(String cacheFilePath, ArrayList<String> list) {
        progress = 0;
        progressDialog.show();
        String epubCachePath = outPath + "cache/" + bookName;

        TextView tv_progress = progressDialog.findViewById(R.id.progress_text);
        Observable.create((ObservableOnSubscribe<String>) emitter -> {

            try {
                deleteDirectory(new File(epubCachePath));
                if (!new EpubUtil(bookName,author,coverUrl,epubCachePath,exportArray,ExportActivity.this).build()) emitter.onError(new Throwable("init failed !"));
                int c = 0;
                for (String s : list) {
                    StringBuilder content = new StringBuilder();
                    File file = new File(s);
                    if (!file.isDirectory()) {
                        if (file.getName().endsWith("nb")) {
                            try {
                                InputStream instream = new FileInputStream(file);
                                InputStreamReader inputreader
                                        = new InputStreamReader(instream, StandardCharsets.UTF_8);
                                BufferedReader buffreader = new BufferedReader(inputreader);
                                String line;
                                while ((line = buffreader.readLine()) != null) {
                                    content.append(line).append("\n");
                                }
                                instream.close();
                                String str = content.toString().replace("　　", "<p/>");
                                String fileText = EpubUtil.CHAPTER.replace("toreplace0", "chapter" + c);
                                fileText = fileText.replace("toreplace1", str);
                                emitter.onNext(fileText);
                                c++;
                            } catch (FileNotFoundException e) {
                                Log.d("ReadCache", "The File doesn't not exist.");
                            } catch (IOException e) {
                                Log.d("ReadCache", e.getMessage());
                            }
                        }
                    }
                }
                emitter.onComplete();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    int i = 0;
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (tv_progress != null) tv_progress.setText("正在初始化……");
                        Log.d(TAG, "subscribe");
                        if (!new File(outPath).exists()) {
                            if (new File(outPath).mkdirs()) {
                                Log.d(TAG, "onSubscribe: " + "文件夹创建成功");
                            }
                        } else if (new File(outPath + bookName + ".txt").exists()) {
                            if (new File(outPath + bookName + ".txt").delete()) {
                                Log.d(TAG, "已清除原有导出数据");
                            }
                        }
                    }

                    @Override
                    public void onNext(String string) {
                        try {
                            EpubUtil.writeFile(epubCachePath + "/OEBPS/chapter" + i + ".html", string);
                            i++;
                            if (tv_progress != null)
                                tv_progress.setText(String.format(getResources().getString(R.string.exporting), i, list.size()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "error");
                        progressDialog.cancel();
                        showSnackBar("导出失败！", speedDialView);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "complete");
                        ZipUtil.pack(new File(epubCachePath), new File(outPath + bookName + ".epub"), new NameMapper() {
                            public String map(String name) {
                                return name;
                            }
                        });
                        deleteDirectory(new File(epubCachePath));
                        if (autoDel) {
                            deleteDirectory(new File(cacheFilePath));
                        }

                        final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        final Uri contentUri = Uri.fromFile(new File(outPath + bookName + ".epub"));
                        scanIntent.setData(contentUri);
                        sendBroadcast(scanIntent);


                        progressDialog.cancel();
                        showSnackBar("导出成功！", speedDialView);
                    }
                });
    }

    private void writeCacheForTXT(String cacheFilePath, ArrayList<String> list) {
        progress = 0;
        progressDialog.show();

        TextView tv_progress = progressDialog.findViewById(R.id.progress_text);
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            for (String s : list) {
                StringBuilder content = new StringBuilder();
                File file = new File(s);
                if (!file.isDirectory()) {
                    if (file.getName().endsWith("nb")) {
                        try {
                            InputStream instream = new FileInputStream(file);
                            InputStreamReader inputreader
                                    = new InputStreamReader(instream, StandardCharsets.UTF_8);
                            BufferedReader buffreader = new BufferedReader(inputreader);
                            String line;
                            while ((line = buffreader.readLine()) != null) {
                                content.append(line).append("\n");
                            }
                            instream.close();
                            emitter.onNext(content.toString());
                        } catch (FileNotFoundException e) {
                            Log.d("ReadCache", "The File doesn't not exist.");
                        } catch (IOException e) {
                            Log.d("ReadCache", e.getMessage());
                        }
                    }
                }
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    File f;
                    FileWriter fw;
                    PrintWriter pw;

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "subscribe");
                        if (!new File(outPath).exists()) {
                            if (new File(outPath).mkdirs()) {
                                Log.d(TAG, "onSubscribe: " + "文件夹创建成功");
                            }
                        } else if (new File(outPath + bookName + ".txt").exists()) {
                            if (new File(outPath + bookName + ".txt").delete()) {
                                Log.d(TAG, "已清除原有导出数据");
                            }
                        }
                    }

                    @Override
                    public void onNext(String string) {
                        Log.d(TAG, "onNext: ");

                        try {
                            if (f == null) f = new File(outPath + bookName + ".txt");
                            if (fw == null) fw = new FileWriter(f, true);
                            if (pw == null) pw = new PrintWriter(fw);
                            pw.println(string);
                            pw.flush();
                            fw.flush();
                            progress++;
                            if (tv_progress != null)
                                tv_progress.setText(String.format(getResources().getString(R.string.exporting), progress, list.size()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "error");
                        progressDialog.cancel();
                        pw.close();
                        try {
                            fw.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        showSnackBar("导出失败！", speedDialView);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "complete");
                        pw.close();
                        try {
                            fw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (autoDel) {
                            deleteDirectory(new File(cacheFilePath));
                        }
                        progressDialog.cancel();
                        showSnackBar("导出成功！", speedDialView);
                    }
                });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteDirectory(File file) {
        File files[] = file.listFiles();
        if (files!=null){
            for (File file1 : files) {
                if (file1.isFile()) {
                    file1.delete();
                } else if (file1.isDirectory()) {
                    deleteDirectory(file1);
                }
            }
            file.delete();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (exportArray.size() == 0) {
            menu.findItem(R.id.export_check_invert).setVisible(false);
            menu.findItem(R.id.exchange_source).setVisible(false);
        } else {
            menu.findItem(R.id.export_check_invert).setVisible(true);
            menu.findItem(R.id.exchange_source).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private Bitmap getNoCover() {
        AssetManager assetManager = getAssets();
        try {
            InputStream in = assetManager.open("nocover.jpg");
            return BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void chooseInvert() {
        for (int i = 0; i < flag.size(); i++) {
            flag.set(i, !flag.get(i));
        }
        adapter.notifyDataSetChanged();
    }


    private void showChapters() {
        if (cacheFiles != null) {
            exportInfo.setText(String.format(getResources().getString(R.string.export_info), chapterNames.size(), SourceUtil.trans(cacheFilePath.split("-")[1])));
            for (int i = 0; i < chapterNames.size(); i++) {
                exportArray.add(new ExportChapter(chapterNames.get(i), false));
                flag.add(true);
            }
            adapter.notifyDataSetChanged();
            speedDialView.setVisibility(View.VISIBLE);
        }
        invalidateOptionsMenu();
    }

    @SuppressLint("DefaultLocale")
    private void scanChapters() {
        speedDialView.setVisibility(View.GONE);
        exportArray.clear();
        flag.clear();
        invalidateOptionsMenu();
        Observable.create((ObservableOnSubscribe<Void>) emitter -> {
            cacheFiles = new ArrayList<>();
            chapterNames = new ArrayList<>();
            chapterFilePath = new ArrayList<>();

            File[] files = new File(cacheFilePath).listFiles();
            LinkedHashMap<Integer, String> map = new LinkedHashMap<>();

            for (File f : files) {
                int chapterSN = Integer.parseInt(f.getName().split("-")[0]);
                String chapterName = f.getName().split("-")[1].replace(".nb", "");
                map.put(chapterSN, chapterName);
            }
            List<Integer> snList = new ArrayList<>(map.keySet());
            Collections.sort(snList);
            for (int i = 0; i < snList.size(); i++) {
                int sn = snList.get(i);
                String name = map.get(sn);
                chapterNames.add(name);
                chapterFilePath.add(cacheFilePath + "/" + String.format("%05d", snList.get(i)) + "-" + name + ".nb");
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
