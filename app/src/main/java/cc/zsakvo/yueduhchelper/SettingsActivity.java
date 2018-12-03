package cc.zsakvo.yueduhchelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import cc.zsakvo.yueduhchelper.listener.ChangePathListener;
import cc.zsakvo.yueduhchelper.utils.SnackbarUtil;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.snackbar.Snackbar;
import com.keenfin.sfcdialog.SimpleFileChooser;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT<Build.VERSION_CODES.O_MR1){
                window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            @SuppressLint("PrivateResource") Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            assert upArrow != null;
            upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (savedInstanceState==null){
            SettingsFragment fragment = new SettingsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,fragment).commit();
        }
    }

    public void fileChoose(final String str, ChangePathListener cpl){
        SimpleFileChooser sfcDialog = new SimpleFileChooser();
        sfcDialog.setOnChosenListener(new SimpleFileChooser.SimpleFileChooserListener() {
            @Override
            public void onFileChosen(File file) {
                Snackbar snackbar = SnackbarUtil.build(getApplicationContext(),toolbar,"请选择一个目录",Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            @Override
            public void onDirectoryChosen(File directory) {
                SharedPreferences sharedPreferences = getSharedPreferences("settings",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(str,directory.getAbsolutePath());
                cpl.changePath(str,directory.getAbsolutePath());
                editor.apply();
                editor.commit();
            }

            @Override
            public void onCancel() {
                Snackbar snackbar = SnackbarUtil.build(getApplicationContext(),toolbar,"未选择任何目录",Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        sfcDialog.show(getFragmentManager(), "SimpleFileChooserDialog");
    }
}
