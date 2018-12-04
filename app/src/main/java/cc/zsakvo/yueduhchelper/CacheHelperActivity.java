package cc.zsakvo.yueduhchelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import cc.zsakvo.yueduhchelper.utils.SnackbarUtil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.snackbar.Snackbar;
import com.jaeger.library.StatusBarUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

public class CacheHelperActivity extends AppCompatActivity {

    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_helper);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setTransparent(this);
        }else {
            StatusBarUtil.setColor(this,Color.parseColor("#ffffff"));
        }

        toolbar = findViewById(R.id.cache_toolbar);
        toolbar.setTitle("阅读缓存提取");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.cache_coordinatorLayout);
        coordinatorLayout.bringToFront();

        if (getSharedPreferences("settings",MODE_PRIVATE).getBoolean("isFirst",true)) {
            showFirstDialog();
        }else {
            if(AndPermission.hasPermissions(this, Permission.Group.STORAGE)) {
                BooksCacheFragment fragment = new BooksCacheFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.cache_fragment,
                        fragment).commit();
            } else {
                requestPermission();
            }
        }
    }

    private void showFirstDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("提示")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences("settings",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isFirst",false);
                        editor.apply();
                        editor.commit();
                        dialog.dismiss();
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                            requestPermission();
                        }
                    }
                })
                .setMessage("1.本程序需要存储权限以保证正常运行\n2.程序会优先扫描「阅读」的默认缓存文件夹.\n3.默认输出文件夹为 /内置存储/Documents/YueDuTXT 目录")
                .setCancelable(false)
                .create()
                .show();
    }

    private void requestPermission(){
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(permissions -> {
                    BooksCacheFragment fragment = new BooksCacheFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.cache_fragment,
                            fragment).commit();
                })
                .onDenied(permissions -> {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("提示")
                            .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setMessage("权限未授予，将无法正常运行！")
                            .setCancelable(false)
                            .create()
                            .show();
                })
                .start();
    }

    public void showSnackBar(String string){
        SnackbarUtil.build(this,coordinatorLayout,string,Snackbar.LENGTH_SHORT).show();
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
                startActivity(new Intent(this,SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
