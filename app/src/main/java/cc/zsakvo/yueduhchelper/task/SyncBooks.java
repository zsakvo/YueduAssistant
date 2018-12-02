package cc.zsakvo.yueduhchelper.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.zsakvo.yueduhchelper.listener.SyncBooksListener;

import static android.content.ContentValues.TAG;

public class SyncBooks extends AsyncTask<String,Void,List<String>> {

    private SyncBooksListener sbl;

    public SyncBooks(SyncBooksListener sbl){
        this.sbl = sbl;
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> books = new ArrayList<>();
        String path = strings[0];
        File cacheFile = new File(path);
        try {
            for (File f : cacheFile.listFiles()) {
                if (!f.isDirectory()) continue;
                books.add(f.getName());
            }
        }catch (Exception e){
            return null;
        }
        return books;
    }

    @Override
    protected void onPostExecute(List<String> books){
        super.onPostExecute (books);
        sbl.showBooks(books);
    }
}
