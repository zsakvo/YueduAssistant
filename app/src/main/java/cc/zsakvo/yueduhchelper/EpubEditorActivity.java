package cc.zsakvo.yueduhchelper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import cc.zsakvo.yueduhchelper.bean.CacheBooks;

public class EpubEditorActivity extends AppCompatActivity {

    private String bookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_editor);

        CacheBooks cb = (CacheBooks) getIntent().getSerializableExtra("book");
        bookName = cb.getName();
    }
}
