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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            if (Build.VERSION.SDK_INT<Build.VERSION_CODES.O_MR1){
                window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("阅读缓存提取");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        textView = (TextView)findViewById(R.id.text);

        if (doCheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.d(TAG, "onCreate: 应用具有写外部存储的权限");
            //进行依赖权限的代码
        } else {
            Log.d(TAG, "onCreate: 应用不具有写外部存储的权限");
            doRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_READ_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: request write permission success!");
                    //处理依赖权限的代码
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: request write permission fail!");
                    //请求失败，没有权限，不能运行依赖权限的代码
                }
                break;
        }
    }

    private boolean doCheckPermission(String permission) {
        int permissionCheck = checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid());
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    private void doRequestPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{permission}, MY_READ_REQUEST_CODE);
        }
    }

    @Override
    public void showBooks(List<String> books) {
        if (books == null){
            textView.setText(getResources().getText(R.string.no_books));
        }else {
            this.books = books;
            List<book> list = new ArrayList<>();
            for (String book : books) {
                String[] ba = book.split("-");
                book b = new book(ba[0], "来源：" + ba[1]);
                list.add(b);
            }
            ListView listview = (ListView) findViewById(R.id.list);
            textView.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            listview.setAdapter(new BookAdapter(this, list));
            listview.setOnItemClickListener(this);
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
        new WriteFile(this).execute(content,folderPath,bookName);
    }

    @Override
    public void writeFileResult(Boolean b) {
        progressDialog.dismiss();
        Snackbar snackbar;
        if (b){
            snackbar = Snackbar.make(textView," 导出成功！",Snackbar.LENGTH_LONG);
        }else {
            snackbar = Snackbar.make(textView," 导出失败！",Snackbar.LENGTH_LONG);
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
