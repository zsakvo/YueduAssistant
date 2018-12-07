package cc.zsakvo.yueduhchelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import cc.zsakvo.yueduhchelper.utils.SnackbarUtil;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;

public class TextExportActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    String bookInfo;
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
            window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setTransparent(this);
        }else {
            StatusBarUtil.setColor(this,Color.parseColor("#ffffff"));
        }
        toolbar = (Toolbar)findViewById(R.id.export_toolbar);
        toolbar.setTitle("导出TXT");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.export_coordinatorLayout);

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

        this.bookInfo = getIntent().getStringExtra("bp");

        fragment = new TextExportFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.export_fragment,fragment).commit();

    }

    public String getBookInfo(){
        return bookInfo;
    }

    private void showChooseChaptersDialog(){
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


        TextExportFragment fragment = (TextExportFragment)getSupportFragmentManager().findFragmentById(R.id.export_fragment);
        assert fragment != null;
        int size = fragment.getSize();

        //设置dialog布局
        editDialog.setView(layout);

        //设置按钮
        editDialog.setPositiveButton(getString(R.string.choose_ok)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] strs = edit.getText().toString().split("-");
                        int a = Integer.parseInt(strs[0]);
                        int b = Integer.parseInt(strs[1]);
                        fragment.chooseChapters(a,b);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                edit.getText().clear();
                                dialog.dismiss();
                            }
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
                try{
                    String[] strs = edit.getText().toString().split("-");
                    int a = Integer.parseInt(strs[0]);
                    int b = Integer.parseInt(strs[1]);
                    if (0<a&&a<=b&&b<=size){
                        positiveButton.setEnabled(true);
                    }else {
                        positiveButton.setEnabled(false);
                    }
                }catch (Exception e){
                    positiveButton.setEnabled(false);
                }
            }
        });
    }

    public void initMenuItems(){
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
        TextExportFragment fragment = (TextExportFragment)getSupportFragmentManager().findFragmentById(R.id.export_fragment);
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
                if (list.size()==0){
                    SnackbarUtil.build(this,coordinatorLayout,"请至少勾选一章",Snackbar.LENGTH_SHORT).show();
                }else {
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
