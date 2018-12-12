package cc.zsakvo.yueduhchelper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import cc.zsakvo.yueduhchelper.classes.CacheBook;

public class EpubEditorActivity extends AppCompatActivity {

    private String bookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_editor);

        CacheBook cb = (CacheBook) getIntent().getSerializableExtra("book");
        bookName = cb.getName();
    }
}
