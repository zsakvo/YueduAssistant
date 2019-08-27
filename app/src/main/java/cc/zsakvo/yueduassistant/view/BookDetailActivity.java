package cc.zsakvo.yueduassistant.view;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lapism.searchview.Search;
import com.lapism.searchview.widget.SearchView;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.adapter.CacheChapterAdapter;
import cc.zsakvo.yueduassistant.bean.CacheChapter;
import cc.zsakvo.yueduassistant.bean.ExportBook;
import cc.zsakvo.yueduassistant.listener.ExportListener;
import cc.zsakvo.yueduassistant.listener.FlagsListener;
import cc.zsakvo.yueduassistant.utils.BookUtil;
import cc.zsakvo.yueduassistant.utils.SpUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    private FloatingActionButton fab;
    private CoordinatorLayout coordinatorLayout;
    private boolean flagsStatus;
    private SearchView searchView;

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
//        Toolbar toolbar = $(R.id.toolbar);
//        toolbar.setTitle("书籍详情");
//        setSupportActionBar(toolbar);

//        bookInfoCard = $(R.id.book_info_card);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        searchView = $(R.id.searchView);
        searchView.setShadow(false);
        searchView.setClickable(false);
        searchView.setOnLogoClickListener(new Search.OnLogoClickListener() {
            @Override
            public void onLogoClick() {
                finish();
            }
        });

        searchView.setOnOpenCloseListener(new Search.OnOpenCloseListener() {

            @Override
            public void onOpen() {
                searchView.clearFocus();
            }

            @Override
            public void onClose() {
            }
        });
        coordinatorLayout = $(R.id.coord);

        fab = $(R.id.fab);

        bookChapters = $(R.id.book_chapters);
        bookChapters.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CacheChapterAdapter(BookDetailActivity.this,this::setFlags);

        bookChapters.setAdapter(adapter);

        fab.setOnClickListener(view1 -> {
            ExportBook.Builder bookBuilder = new ExportBook.Builder(BookDetailActivity.this);
            ExportBook exportBook = bookBuilder
                    .bookPath(bookCachePath)
                    .cacheChapters(cacheChapters)
                    .flags(chapterFlags)
                    .outputDirPath(SpUtil.getOutputPath(BookDetailActivity.this))
                    .fileName(bookName + ".txt")
                    .build();
            new BookUtil(exportBook, coordinatorLayout, BookDetailActivity.this).extractTXT();
        });
    }

    @Override
    public void setListener() {

    }

    private void scanChapters(String path) {
        cacheChapters.clear();
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
                        View topView = getLayoutInflater().inflate(R.layout.scan_chapters_failed_card,new LinearLayout(BookDetailActivity.this));
                        bookInfoCard.removeAllViews();
                        bookInfoCard.addView(topView);
                    }

                    @Override
                    public void onComplete() {
                        View topView = getLayoutInflater().inflate(R.layout.book_info_card, new LinearLayout(BookDetailActivity.this));
                        bookInfoName = topView.findViewById(R.id.card_book_info_name);
                        bookInfoNum = topView.findViewById(R.id.card_book_info_num);
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
        searchView.setHint(bookName);
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
