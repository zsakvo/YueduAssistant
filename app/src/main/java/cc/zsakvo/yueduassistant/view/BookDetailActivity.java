package cc.zsakvo.yueduassistant.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.adapter.CacheBookAdapter;
import cc.zsakvo.yueduassistant.adapter.CacheChapterAdapter;
import cc.zsakvo.yueduassistant.bean.CacheBook;
import cc.zsakvo.yueduassistant.bean.CacheChapter;
import cc.zsakvo.yueduassistant.bean.ExportBook;
import cc.zsakvo.yueduassistant.listener.ExportListener;
import cc.zsakvo.yueduassistant.listener.FlagsListener;
import cc.zsakvo.yueduassistant.utils.BookUtil;
import cc.zsakvo.yueduassistant.utils.SourceUtil;
import cc.zsakvo.yueduassistant.utils.SpUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leinardi.android.speeddial.SpeedDialView;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookDetailActivity extends BaseActivity implements ExportListener, FlagsListener {

    List<CacheChapter> cacheChapters = new ArrayList<>();
    List<Boolean> chapterFlags = new ArrayList<>();
    private String bookCachePath;
    private String outputPath;
    private CacheChapterAdapter adapter;
    private String bookName;
    private int chapterNum;
    private TextView bookInfoName, bookInfoNum;
    private FrameLayout bookInfoCard;
    private RecyclerView bookChapters;
    private SpeedDialView fab;
    private CoordinatorLayout coordinatorLayout;
    private boolean flagsStatus;

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
        return R.layout.activity_book_detail;
    }

    @Override
    public int bindMenu() {
        return 0;
    }

    @Override
    public void clickMenu(MenuItem item) {

    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar);
        toolbar.setTitle("书籍详情");
        setSupportActionBar(toolbar);

        bookInfoCard = $(R.id.book_info_card);

        coordinatorLayout = $(R.id.coord);

        fab = $(R.id.fab);

        bookChapters = $(R.id.book_chapters);
        bookChapters.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CacheChapterAdapter(BookDetailActivity.this,this::setFlags);

        bookChapters.setAdapter(adapter);

        if (getSupportActionBar() != null) {
            @SuppressLint("PrivateResource") Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            assert upArrow != null;
            upArrow.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        fab.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                ExportBook.Builder bookBuilder = new ExportBook.Builder(BookDetailActivity.this);
                ExportBook exportBook = bookBuilder
                        .bookPath(bookCachePath)
                        .cacheChapters(cacheChapters)
                        .flags(chapterFlags)
                        .outputDirPath(SpUtil.getOutputPath(BookDetailActivity.this))
                        .fileName(bookName + ".txt")
                        .build();
                new BookUtil(exportBook, coordinatorLayout, BookDetailActivity.this).extractTXT();
                return false;
            }

            @Override
            public void onToggleChanged(boolean isOpen) {

            }
        });
    }

    @Override
    public void setListener() {

    }

    private void scanChapters(String path) {
        cacheChapters.clear();
//        chapterFlags.clear();
        adapter.cleanItems();
        Observable.create((ObservableOnSubscribe<Void>) emitter -> {
            try {
                File bookCache = new File(path);
                String[] bookCacheList = bookCache.list();
                Arrays.sort(bookCacheList);
                for (String chapterCacheName : bookCacheList) {
                    if (!chapterCacheName.contains("-") || !chapterCacheName.contains(".nb")) break;
                    CacheChapter cacheChapter = new CacheChapter();
                    cacheChapter.setFileName(chapterCacheName);
                    chapterCacheName = chapterCacheName.replace(".nb", "");
                    String[] chapterCacheNameInfo = chapterCacheName.split(("-"));
                    cacheChapter.setName(chapterCacheNameInfo[1]);
                    cacheChapters.add(cacheChapter);
                    if (flagsStatus) chapterFlags.add(true);
                }
                emitter.onComplete();
            } catch (Exception e) {
                fab.setVisibility(View.GONE);
                View topView = getLayoutInflater().inflate(R.layout.scan_chapters_failed_card, new LinearLayout(BookDetailActivity.this));
                bookInfoCard.removeAllViews();
                bookInfoCard.addView(topView);
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Void v) {
                        Logger.d("onNext: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.toString());
                        fab.setVisibility(View.GONE);
                        View topView = getLayoutInflater().inflate(R.layout.scan_chapters_failed_card,new LinearLayout(BookDetailActivity.this));
                        bookInfoCard.removeAllViews();
                        bookInfoCard.addView(topView);
                    }

                    @Override
                    public void onComplete() {
                        View topView = getLayoutInflater().inflate(R.layout.book_info_card, new LinearLayout(BookDetailActivity.this));
                        bookInfoName = topView.findViewById(R.id.card_book_info_name);
                        bookInfoNum = topView.findViewById(R.id.card_book_info_num);
                        bookInfoCard.removeAllViews();
                        bookInfoCard.addView(topView);
                        chapterNum = cacheChapters.size();
                        bookInfoName.setText(bookName);
                        bookInfoNum.setText(String.format(getResources().getString(R.string.book_info_chapterNum), chapterNum));
                        adapter.setItems(cacheChapters,chapterFlags);

                    }
                });
    }

    @Override
    public void doBusiness(Context mContext) {
        String info = getIntent().getStringExtra("info");
        bookName = info.split("-")[0];
        bookCachePath = SpUtil.getCacheDirPath(this) + "/" + info;
        outputPath = SpUtil.getOutputPath(this) + "/" + bookName + ".txt";
    }

    @Override
    public void doOnStart() {
        adapter.cleanItems();
        flagsStatus = chapterFlags.size() == 0;
        scanChapters(bookCachePath);
    }

    @Override
    public void exportFinish() {
        scanChapters(bookCachePath);
    }

    @Override
    public void setFlags(List<Boolean> flags) {
        this.chapterFlags = flags;
    }
}
