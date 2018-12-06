package cc.zsakvo.yueduhchelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;

import com.jaeger.library.StatusBarUtil;

public class TextExportActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    String bookInfo;
    TextExportFragment fragment;

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
        toolbar.setTitle("自定义导出");
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

        this.bookInfo = getIntent().getStringExtra("bp");

        fragment = new TextExportFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.export_fragment,fragment).commit();

        AppCompatButton btn_ok = (AppCompatButton)findViewById(R.id.export_btn_ok);
        AppCompatButton btn_cancel = (AppCompatButton)findViewById(R.id.export_btn_cancel);

        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);


    }

    public String getBookInfo(){
        return bookInfo;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.export_btn_ok:
                Intent intent = new Intent();
                intent.putStringArrayListExtra("cps",fragment.getChapters());
                setResult(0,intent);//requestCode=1
                finish();
                break;
            case R.id.export_btn_cancel:
                finish();
                break;
                default:
                    break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSharedPreferences("checkbox",MODE_PRIVATE).edit().clear().apply();
    }
}
