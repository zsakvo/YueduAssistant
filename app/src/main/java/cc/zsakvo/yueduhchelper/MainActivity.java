package cc.zsakvo.yueduhchelper;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.keenfin.sfcdialog.SimpleFileChooser;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cc.zsakvo.yueduhchelper.listener.ReadCacheListener;
import cc.zsakvo.yueduhchelper.listener.SyncBooksListener;
import cc.zsakvo.yueduhchelper.listener.WriteFileListener;
import cc.zsakvo.yueduhchelper.task.ReadCache;
import cc.zsakvo.yueduhchelper.task.SyncBooks;
import cc.zsakvo.yueduhchelper.task.WriteFile;
import cc.zsakvo.yueduhchelper.utils.SnackbarUtil;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity implements SyncBooksListener,AdapterView.OnItemClickListener,ReadCacheListener,WriteFileListener {

    private final int MY_READ_REQUEST_CODE = 0;
    private List<String> books;
    private TextView textView;
    String myCachePath;
    int hudProgress = 0;
    int bookChapNum = 0;
    StringBuilder bookContent;
    String bookName;
    ProgressDialog progressDialog;


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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("阅读缓存提取");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        textView = (TextView)findViewById(R.id.text);
        requestPermission();
    }

    private void requestPermission(){
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(permissions -> {
                    onStart();
                })
                .onDenied(permissions -> {
                    SnackbarUtil.build(this,textView,"未授予存储读写权限，将无法正常工作",Snackbar.LENGTH_LONG).show();
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermission();
                        }
                    });
                })
                .start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        myCachePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/com.gedoor.monkeybook/cache/book_cache";
        myCachePath = getSharedPreferences("settings",MODE_PRIVATE).getString("cachePath",myCachePath);
        new SyncBooks(this).execute(myCachePath);
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



    @Override
    public void showBooks(List<String> books) {
        if (books == null){
            textView.setText(getResources().getText(R.string.no_books));
        }else {
            this.books = books;
            List<book> list = new ArrayList<>();
            for (String book : books) {
                if (!book.contains("-")) continue;
                String[] ba = book.split("-");
                book b = new book(ba[0], "来源：" + ba[1]);
                list.add(b);
            }
            if (list.size()==0){
                textView.setText(getResources().getString(R.string.no_books));
            }else {
                ListView listview = (ListView) findViewById(R.id.list);
                textView.setVisibility(View.GONE);
                listview.setVisibility(View.VISIBLE);
                listview.setAdapter(new BookAdapter(this, list));
                listview.setOnItemClickListener(this);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        progressDialog = new ProgressDialog(MainActivity.this);

        bookContent = new StringBuilder();
        bookName = books.get(position).split("-")[0];
        String bookPath = myCachePath+"/"+books.get(position)+"/";
        File bookFile = new File(bookPath);

        progressDialog.setProgress(0);
        progressDialog.setTitle("合并中，请稍后……");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        new ReadCache(this,progressDialog,hudProgress).execute(bookFile);
}

    @Override
    public void readCache(String content) {
        bookContent.append(content);
        if (hudProgress>=bookChapNum){
            writeFile(content,bookName);
        }
    }

    private void writeFile(String content,String bookName){
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/YueDuTXT/";
        folderPath = getSharedPreferences("settings",MODE_PRIVATE).getString("outPath",folderPath);
        Log.e(TAG, "writeFile: "+folderPath );
        bookName+=".txt";
        Log.e(TAG, "writeFile: "+folderPath );
        new WriteFile(this).execute(content,folderPath,bookName);
    }

    @Override
    public void writeFileResult(Boolean b) {
        progressDialog.dismiss();
        Snackbar snackbar;
        if (b){
            snackbar = SnackbarUtil.build(this,textView," 导出成功！",Snackbar.LENGTH_LONG);
        }else {
            snackbar = SnackbarUtil.build(this,textView," 导出失败！",Snackbar.LENGTH_LONG);
        }
        snackbar.show();
    }

    class book{
        String getBookName() {
            return bookName;
        }

        String getBookSource() {
            return bookSource;
        }

        private String bookName;
        private String bookSource;

        book(String bookName, String bookSource){
            this.bookName = bookName;
            this.bookSource = bookSource;
        }
    }

    class BookAdapter extends BaseAdapter {

        private Context context;
        private List<book> books;
        private LayoutInflater layoutInflater;
        TextView bookName;
        TextView bookSource;

        /**
         * 构造函数，进行初始化
         *
         * @param context
         * @param books
         */
        BookAdapter(Context context, List<book> books) {
            this.context = context;
            this.books = books;
            layoutInflater = LayoutInflater.from(this.context);
        }

        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public Object getItem(int position) {
            return books.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.book_list, null);
            }
            bookName = (TextView) convertView.findViewById(R.id.book_name);
            bookSource = (TextView) convertView.findViewById(R.id.book_source);
            bookName.setText(books.get(position).getBookName());
            bookSource.setText(books.get(position).getBookSource());
            return convertView;
        }

    }
}
