package cc.zsakvo.yueduhchelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduhchelper.adapter.ExportBooksAdapter;
import cc.zsakvo.yueduhchelper.bean.CacheBooks;
import cc.zsakvo.yueduhchelper.bean.ExportChapter;
import cc.zsakvo.yueduhchelper.listener.SyncChaptersListener;
import cc.zsakvo.yueduhchelper.task.SyncChapters;
import cc.zsakvo.yueduhchelper.utils.Divider;
import cc.zsakvo.yueduhchelper.utils.SnackbarUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExportActivity extends AppCompatActivity implements SyncChaptersListener{

    private Toolbar toolbar;

    private List<String> cacheFilePath;
    private List<ExportChapter> exportArray;
    private ExportBooksAdapter adapter;

    private List<Boolean> flag;

    private List<File> cacheFiles;

    private String bookName;
    private TextView exportInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        toolbar = findViewById(R.id.export_toolbar);
        toolbar.setTitle("导出为TXT");
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

        exportArray = new ArrayList<>();

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.export_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new Divider(this,24));

        flag = new ArrayList<>();

        adapter = new ExportBooksAdapter(exportArray,flag);
        mRecyclerView.setAdapter(adapter);

        CacheBooks cb = (CacheBooks) getIntent().getSerializableExtra("book");
        exportInfo = (TextView)findViewById(R.id.export_info);
        bookName = cb.getName();
        cacheFilePath = cb.getBookSources();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.export_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_check_invert:
                chooseInvert();
                break;
            case R.id.export_txt:
                exportTXT();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chooseInvert(){
        for (int i=0;i<flag.size();i++){
            flag.set(i,!flag.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private void exportTXT(){
        ArrayList<String> chapters = new ArrayList<>();
        for (int i=0;i<flag.size();i++){
            if (flag.get(i)){
                chapters.add(cacheFiles.get(i).getAbsolutePath());
            }
        }
        if (chapters.size() == 0) {
            SnackbarUtil.build(this, toolbar, "请至少勾选一章", Snackbar.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("cps", chapters);
            setResult(0, intent);
            finish();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        new SyncChapters(this).execute(cacheFilePath);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (exportArray.size()==0) {
            menu.findItem(R.id.export_check_invert).setVisible(false);
            menu.findItem(R.id.export_txt).setVisible(false);
        }else {
            menu.findItem(R.id.export_check_invert).setVisible(true);
            menu.findItem(R.id.export_txt).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void showChapters(List<File> cacheFiles, List<Boolean> isDum) {
        this.cacheFiles = cacheFiles;
        for (int i = 0; i < cacheFiles.size(); i++) {
            String[] a = cacheFiles.get(i).getName().split("/");
            String[] b = a[a.length - 1].split("-");
            String c = a[a.length - 1].replace(b[0] + "-", "").replace(".nb", "");
            exportArray.add(new ExportChapter(c,false));
            flag.add(true);
        }
        exportInfo.setText(String.format(getResources().getString(R.string.export_info),bookName,cacheFiles.size()));
        adapter.notifyDataSetChanged();
        invalidateOptionsMenu();
    }

}
