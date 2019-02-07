package cc.zsakvo.yueduhchelper;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.google.android.material.snackbar.Snackbar;
import com.jaeger.library.StatusBarUtil;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import cc.zsakvo.yueduhchelper.listener.ChangePathListener;
import cc.zsakvo.yueduhchelper.utils.DirChooseUtil;
import cc.zsakvo.yueduhchelper.utils.SnackbarUtil;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
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

        if (savedInstanceState == null) {
            SettingsFragment fragment = new SettingsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        }
    }

    public void fileChoose(final String str, ChangePathListener cpl) {
        DirChooseUtil sfcDialog = new DirChooseUtil();
        sfcDialog.setOnChosenListener(new DirChooseUtil.SimpleFileChooserListener() {
            @Override
            public void onFileChosen(File file) {
            }

            @Override
            public void onDirectoryChosen(File directory) {
                SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(str, directory.getAbsolutePath());
                cpl.changePath(str, directory.getAbsolutePath());
                editor.apply();
                editor.commit();
            }

            @Override
            public void onCancel() {
            }
        });

        sfcDialog.show(getSupportFragmentManager(), "DirChooseUtil");
    }
}
