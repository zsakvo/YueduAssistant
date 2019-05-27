package cc.zsakvo.yueduassistant.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.adapter.CacheBookAdapter;
import cc.zsakvo.yueduassistant.adapter.CacheChapterAdapter;
import cc.zsakvo.yueduassistant.bean.CacheBook;
import cc.zsakvo.yueduassistant.bean.CacheChapter;
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

import com.orhanobut.logger.Logger;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookDetailActivity extends BaseActivity {

    List<CacheChapter> cacheChapters = new ArrayList<>();
    List<Boolean> chapterFlag = new ArrayList<>();
    private CacheChapterAdapter adapter;
    private String bookName;
    private int chapterNum;
    private TextView bookInfoName,bookInfoNum;
    private FrameLayout bookInfoCard;
    private RecyclerView bookChapters;

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

        bookChapters = $(R.id.book_chapters);
        bookChapters.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CacheChapterAdapter(R.layout.list_cache_chapter, cacheChapters,chapterFlag);
        adapter.openLoadAnimation();
        adapter.setOnItemClickListener((adapter, itemView, position) -> {
            Logger.d(chapterFlag.get(position));
        });
        bookChapters.setAdapter(adapter);

        if (getSupportActionBar() != null) {
            @SuppressLint("PrivateResource") Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            assert upArrow != null;
            upArrow.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void setListener() {

    }

    private void scanChapters(String path) {
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
                    chapterFlag.add(true);
                }
                emitter.onComplete();
            } catch (Exception e) {
                Logger.e(e.toString());
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.d("subscribe");
                    }

                    @Override
                    public void onNext(Void v) {
                        Logger.d("onNext: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("扫描完毕");
                        View topView = getLayoutInflater().inflate(R.layout.book_info_card,new LinearLayout(BookDetailActivity.this));
                        bookInfoName = topView.findViewById(R.id.card_book_info_name);
                        bookInfoNum = topView.findViewById(R.id.card_book_info_num);
                        bookInfoCard.removeAllViews();
                        bookInfoCard.addView(topView);
                        chapterNum = cacheChapters.size();
                        bookInfoName.setText(bookName);
                        bookInfoNum.setText(String.format(getResources().getString(R.string.book_info_chapterNum),chapterNum));
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void doBusiness(Context mContext) {

        String info = getIntent().getStringExtra("info");
        bookName = info.split("-")[0];
        String bookCachePath = SpUtil.getCacheDirPath(this) + "/" + info;
        scanChapters(bookCachePath);
    }

    @Override
    public void doOnStart() {

    }
}
