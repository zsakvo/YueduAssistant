package cc.zsakvo.yueduassistant.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.lapism.searchview.Search;
import com.lapism.searchview.widget.SearchView;
import com.orhanobut.logger.Logger;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.adapter.CacheBookAdapter;
import cc.zsakvo.yueduassistant.bean.CacheBook;
import cc.zsakvo.yueduassistant.utils.SourceUtil;
import cc.zsakvo.yueduassistant.utils.SpUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView mRecyclerView;
    private SearchView searchView;
    private CardView permisson_card;
    private CardView scanning_card;
    private CardView prompt_card;
    private String cacheDirPath = "";
    private HashMap<String, String> source;
    private LinkedHashMap<String, CacheBook> books;
    private List<String> bookNames = new ArrayList<>();
    private List<String> bookInfos = new ArrayList<>();
    private List<String> bookKeys = new ArrayList<>();
    private List<CacheBook> cacheBooks = new ArrayList<>();
    private List<CacheBook> baseDatas = new ArrayList<>();
    List<CacheBook> cacheTmp = new ArrayList<>();

    private CacheBookAdapter adapter;

    private TextView tv_prompt_text;

    private StringBuilder errorLog;


    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case -1:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
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
        return 0;
    }

    @Override
    public void clickMenu(MenuItem item) {

    }

    @Override
    public void initView(View view) {
        drawerLayout = $(R.id.drawer_layout);
        searchView = $(R.id.searchView);
        searchView.setHint("阅读助手");
        searchView.setShadow(false);
        searchView.setClickable(false);
        searchView.setOnMenuClickListener(new Search.OnMenuClickListener() {
            @Override
            public void onMenuClick() {
                Logger.d("menu!");
            }
        });
        searchView.setOnLogoClickListener(new Search.OnLogoClickListener() {
            @Override
            public void onLogoClick() {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        searchView.setOnQueryTextListener(new Search.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(CharSequence query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public void onQueryTextChange(CharSequence newText) {
                if (newText.length() == 0) {
                    searchView.clearFocus();
                }
                cacheBooks.clear();
                cacheTmp.clear();
                for (CacheBook cacheBook : baseDatas) {
                    if (cacheBook.getName().contains(newText)) {
                        cacheTmp.add(cacheBook);
                    }
                }
                cacheBooks.addAll(cacheTmp);
                adapter.cleanItems();
                adapter.setItems(cacheBooks);
            }
        });


        searchView.setOnMicClickListener(new Search.OnMicClickListener() {
            @Override
            public void onMicClick() {
                Logger.d("Mic!");
            }
        });
        NavigationView navigationView = $(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        mRecyclerView = $(R.id.cache_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CacheBookAdapter(MainActivity.this);
        mRecyclerView.setAdapter(adapter);
    }

    private void scanBooks() {
        Logger.d("开始扫描书籍");
        cacheBooks.clear();
        adapter.notifyDataSetChanged();
        Observable.create((ObservableOnSubscribe<Void>) emitter -> {
            try {
                File cacheDir = new File(cacheDirPath);
                for (String cacheName : cacheDir.list()) {
                    if (!cacheName.contains("-")) break;
                    CacheBook cacheBook = new CacheBook();
                    cacheBook.setInfo(cacheName);
                    String[] cacheInfo = cacheName.split(("-"));
                    cacheBook.setName(cacheInfo[0]);
                    cacheBook.setSource(SourceUtil.queryName(cacheInfo[1]));
                    int chapterNum = new File(cacheDirPath + "/" + cacheName + "/").list().length;
                    cacheBook.setChapterNum(chapterNum);
                    cacheBooks.add(cacheBook);
                    baseDatas.add(cacheBook);
                }
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
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
                        errorLog = new StringBuilder();
                        errorLog.append(e.toString())
                                .append("\n")
                                .append(cacheDirPath);

                        showBooks();
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("扫描完毕");
                        showBooks();
                        searchView.setClickable(true);
                    }
                });
    }

    private void showBooks() {
        adapter.setItems(cacheBooks);
        searchView.setMicColor(getResources().getColor(R.color.colorAccent));
        searchView.setMicIcon(R.drawable.ic_scan_status);
        int booksNum;
        String type = "基础功能";
        if (cacheBooks == null || cacheBooks.size() == 0) {
            Logger.e("未扫描到书籍");
        } else {
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void doOnStart() {
        searchView.clearFocus();
        searchView.setText(null);
        baseDatas.clear();
        adapter.cleanItems();
        //获取缓存路径
        cacheDirPath = SpUtil.getCacheDirPath(this);
        //检查权限
        String readPermission = "android.permission.READ_EXTERNAL_STORAGE";
        String writePermission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int rperm = checkCallingOrSelfPermission(readPermission);
        int wperm = checkCallingOrSelfPermission(writePermission);
        if (rperm == PackageManager.PERMISSION_GRANTED && wperm == PackageManager.PERMISSION_GRANTED) {
            scanBooks();
        } else {
            View top = getLayoutInflater().inflate(R.layout.request_permission_card, (ViewGroup) mRecyclerView.getParent(), false);
            searchView.setMicColor(Color.parseColor("#e53935"));
            searchView.setMicIcon(R.drawable.ic_scan_status);
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            mBottomSheetDialog.setCancelable(false);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_scan_books_failed, null);
            MaterialButton appExit = (MaterialButton) dialogView.findViewById(R.id.app_exit);
            MaterialButton appAuth = (MaterialButton) dialogView.findViewById(R.id.app_auth);
            appExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            appAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AndPermission.with(MainActivity.this)
                            .runtime()
                            .permission(Permission.Group.STORAGE)
                            .onGranted(permissions -> {
                                mBottomSheetDialog.cancel();
                                scanBooks();
                            })
                            .onDenied(permissions -> {

                            })
                            .start();
                }
            });
            mBottomSheetDialog.setContentView(dialogView);
            mBottomSheetDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (searchView.getText() != null && searchView.getText().length() > 0) {
            searchView.setText(null);
            searchView.clearFocus();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.drawer_extract:
                menuItem.setChecked(true);
                break;
            case R.id.drawer_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.drawer_help:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}
