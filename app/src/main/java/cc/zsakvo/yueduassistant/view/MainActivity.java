package cc.zsakvo.yueduassistant.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.adapter.CacheBookAdapter;
import cc.zsakvo.yueduassistant.bean.CacheBook;
import cc.zsakvo.yueduassistant.utils.SpUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.navigation.NavigationView;
import com.orhanobut.logger.Logger;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView mRecyclerView;
    private CardView permisson_card;
    private CardView scanning_card;
    private CardView prompt_card;
    private String cacheDir;
    private HashMap<String, String> source;
    private LinkedHashMap<String, CacheBook> books;
    private List<String> bookNames = new ArrayList<>();
    private List<String> bookInfos = new ArrayList<>();
    private List<String> bookKeys = new ArrayList<>();
    private List<CacheBook> cacheBooks = new ArrayList<>();

    private CacheBookAdapter adapter;

    private TextView tv_prompt_text;


    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
//            case R.id.card_permission:
//
//                //开始授权
//                AndPermission.with(this)
//                        .runtime()
//                        .permission(Permission.Group.STORAGE)
//                        .onGranted(permissions -> {
//                            permisson_card.setVisibility(View.GONE);
//                            scanning_card.setVisibility(View.VISIBLE);
//                        })
//                        .onDenied(permissions -> {
//                            permisson_card.setVisibility(View.VISIBLE);
//                            scanning_card.setVisibility(View.GONE);
//                        })
//                        .start();
//                break;
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
        Toolbar toolbar = $(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        // 设置汉堡键
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        NavigationView navigationView = $(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void setListener() {

    }

    private View getHeaderView(int id) {
        return getLayoutInflater().inflate(id, (ViewGroup) mRecyclerView.getParent(), false);
    }

    @Override
    public void doBusiness(Context mContext) {

        String defaultCacheDirPath = SpUtils.defaultCacheDirPath;
        String configPath = SpUtils.configPath;
        //获取缓存路径
        cacheDir = SpUtils.getCacheDirPath(this);

        mRecyclerView = $(R.id.cache_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CacheBookAdapter(R.layout.list_cache_book, cacheBooks);

        adapter.openLoadAnimation();

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Logger.d("onItemClick: ");
                Toast.makeText(MainActivity.this, "onItemClick" + position, Toast.LENGTH_SHORT).show();
            }
        });

        //检查权限
        String readPermission = "android.permission.READ_EXTERNAL_STORAGE";
        String writePermission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int rperm = checkCallingOrSelfPermission(readPermission);
        int wperm = checkCallingOrSelfPermission(writePermission);
        if (rperm == PackageManager.PERMISSION_GRANTED && wperm == PackageManager.PERMISSION_GRANTED) {
            adapter.addHeaderView(getHeaderView(R.layout.scanning_card));
            scanBooks(configPath);
        } else {
            View top = getLayoutInflater().inflate(R.layout.request_permission_card, (ViewGroup) mRecyclerView.getParent(), false);
            adapter.addHeaderView(top);
            adapter.getHeaderLayout().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.removeAllHeaderView();
                    AndPermission.with(MainActivity.this)
                            .runtime()
                            .permission(Permission.Group.STORAGE)
                            .onGranted(permissions -> {
                                adapter.getHeaderLayout().setOnClickListener(null);
                                adapter.addHeaderView(getHeaderView(R.layout.scanning_card));
                                scanBooks(configPath);
                            })
                            .onDenied(permissions -> {
                                adapter.addHeaderView(getHeaderView(R.layout.request_permission_card));
                            })
                            .start();
                }
            });
        }
        mRecyclerView.setAdapter(adapter);
    }

    private void scanBooks(String configPath) {
        Logger.d("开始扫描书籍");
        Observable.create((ObservableOnSubscribe<Void>) emitter -> {
            try {
                source = new HashMap<>();
                books = new LinkedHashMap<>();
                File configFile = new File(configPath);
                FileReader r = new FileReader(configFile);
                BufferedReader br = new BufferedReader(r);
                StringBuffer json = new StringBuffer();
                String s;
                while ((s = br.readLine()) != null) {
                    json = json.append(s).append("\n");
                }
                br.close();
                JSONArray jsonArray = JSON.parseArray(json.toString());
                if (jsonArray != null) {
                    for (Object object : jsonArray) {
                        JSONObject jsonBook = (JSONObject) JSONObject.toJSON(object);
                        JSONObject bookInfoBean = (JSONObject) JSONObject.toJSON(jsonBook.get("bookInfoBean"));
                        assert bookInfoBean != null;
                        String name = bookInfoBean.getString("name");
                        String source = bookInfoBean.getString("origin");
                        CacheBook cacheBook = new CacheBook();
                        cacheBook.setName(name);
                        cacheBook.setSource(source);
                        cacheBooks.add(cacheBook);
                    }
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable("no bookcaches！"));
                }
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
                        adapter.removeAllHeaderView();
                        adapter.addHeaderView(getHeaderView(R.layout.scan_failed_card));
                        showBooks();
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("扫描完毕");
                        adapter.removeAllHeaderView();
                        adapter.addHeaderView(getHeaderView(R.layout.scan_succes_card));
                        showBooks();
                    }
                });
    }

    private void showBooks() {
        int booksNum;
        String type = "备份扫描";
        if (cacheBooks == null || cacheBooks.size() == 0) {
            Logger.e("未扫描到书籍");
        } else {
            booksNum = cacheBooks.size();
            TextView tv_scan_success = (TextView) adapter.getHeaderLayout().findViewById(R.id.card_scan_success_sub);
            tv_scan_success.setText(String.format(getResources().getString(R.string.scan_result_text), type, booksNum));
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void doOnStart() {

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
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
                menuItem.setChecked(true);
                break;
            case R.id.drawer_help:
                menuItem.setChecked(true);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}
