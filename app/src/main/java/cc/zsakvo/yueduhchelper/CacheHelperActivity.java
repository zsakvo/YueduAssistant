package cc.zsakvo.yueduhchelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cc.zsakvo.yueduhchelper.utils.SnackbarUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.material.snackbar.Snackbar;

public class CacheHelperActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT<Build.VERSION_CODES.O_MR1){
                window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_helper);
        toolbar = findViewById(R.id.cache_toolbar);
        toolbar.setTitle("阅读缓存提取");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            BooksCacheFragment fragment = new BooksCacheFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.cache_fragment,
                    fragment).commit();
        }

        if (getSharedPreferences("settings",MODE_PRIVATE).getBoolean("isFirst",true)) showFirstDialog();
    }

    private void showFirstDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("提示")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences("settings",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("idFirst",false);
                        editor.apply();
                        editor.commit();
                        dialog.dismiss();
                    }
                })
                .setMessage("1.本程序需要存储读写权限以保证正常运行\n2.程序会优先扫描「阅读」App的默认缓存文件夹.\n3.程序的默认输出文件夹为内置存储的Documents/YueDuTXT目录")
                .setCancelable(false)
                .create()
                .show();
    }

    public void showSnackBar(String string){
        SnackbarUtil.build(this,toolbar,string,Snackbar.LENGTH_SHORT).show();
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
