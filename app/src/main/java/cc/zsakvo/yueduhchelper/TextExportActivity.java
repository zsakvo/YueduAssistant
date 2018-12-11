package cc.zsakvo.yueduhchelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import cc.zsakvo.yueduhchelper.Dao.CacheBook;
import cc.zsakvo.yueduhchelper.utils.SnackbarUtil;

public class TextExportActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    String bookName;
    List<String> bookSources;
    TextExportFragment fragment;
    CoordinatorLayout coordinatorLayout;
    MenuItem check_invert;
    MenuItem check;
    MenuItem export;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();

        setContentView(R.layout.activity_text_export);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setTransparent(this);
        } else {
            StatusBarUtil.setColor(this, Color.parseColor("#ffffff"));
        }
        toolbar = findViewById(R.id.export_toolbar);
        toolbar.setTitle("导出TXT");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.export_coordinatorLayout);

        if (getSupportActionBar() != null) {
            @SuppressLint("PrivateResource") Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            assert upArrow != null;
            upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        CacheBook cb = (CacheBook) getIntent().getSerializableExtra("book");
        bookName = cb.getName();
        bookSources = cb.getBookSources();

        fragment = new TextExportFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.export_fragment, fragment).commit();

    }

    public String getBookName() {
        return bookName;
    }

    public List<String> getBookInfo() {
        return bookSources;
    }

    private void showChooseChaptersDialog() {
        final EditText edit = new EditText(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        edit.setMaxLines(1);
        edit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        layout.setPadding(48, 0, 48, 0);
        layout.addView(edit);
        AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        editDialog.setTitle(getString(R.string.prompt));
        editDialog.setMessage(getString(R.string.choose_chapters));


        TextExportFragment fragment = (TextExportFragment) getSupportFragmentManager().findFragmentById(R.id.export_fragment);
        assert fragment != null;
        int size = fragment.getSize();

        editDialog.setView(layout);

        editDialog.setPositiveButton(getString(R.string.choose_ok)
                , (dialog, which) -> {
                    String[] strs = edit.getText().toString().split("-");
                    int a = Integer.parseInt(strs[0]);
                    int b = Integer.parseInt(strs[1]);
                    fragment.chooseChapters(a, b);
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel),
                        (dialog, which) -> {
                            edit.getText().clear();
                            dialog.dismiss();
                        });

        editDialog.setCancelable(false);

        AlertDialog dialog = editDialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveButton.setEnabled(false);

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String[] strs = edit.getText().toString().split("-");
                    int a = Integer.parseInt(strs[0]);
                    int b = Integer.parseInt(strs[1]);
                    if (0 < a && a <= b && b <= size) {
                        positiveButton.setEnabled(true);
                    } else {
                        positiveButton.setEnabled(false);
                    }
                } catch (Exception e) {
                    positiveButton.setEnabled(false);
                }
            }
        });
    }

    public void initMenuItems() {
        check_invert.setEnabled(true);
        check.setEnabled(true);
        export.setEnabled(true);
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.export_menu, menu);
        check_invert = menu.findItem(R.id.export_check_invert);
        check = menu.findItem(R.id.export_check);
        export = menu.findItem(R.id.export_txt);
        fragment.init();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TextExportFragment fragment = (TextExportFragment) getSupportFragmentManager().findFragmentById(R.id.export_fragment);
        switch (item.getItemId()) {
            case R.id.export_check_invert:
                assert fragment != null;
                fragment.chooseInvert();
                break;
            case R.id.export_check:
                showChooseChaptersDialog();
                break;
            case R.id.export_txt:
                assert fragment != null;
                ArrayList list = fragment.getChapters();
                if (list.size() == 0) {
                    SnackbarUtil.build(this, coordinatorLayout, "请至少勾选一章", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("cps", fragment.getChapters());
                    setResult(0, intent);//requestCode=1
                    finish();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
