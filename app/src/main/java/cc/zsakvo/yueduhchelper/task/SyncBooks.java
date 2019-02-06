package cc.zsakvo.yueduhchelper.task;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.zsakvo.yueduhchelper.bean.CacheBooks;
import cc.zsakvo.yueduhchelper.listener.SyncBooksListener;


@SuppressWarnings("ALL")
public class SyncBooks extends AsyncTask<String, Void, Void> {

    private SyncBooksListener sbl;
    private Boolean autoMerge;

    public SyncBooks(SyncBooksListener sbl, Boolean autoMerge) {
        this.sbl = sbl;
        this.autoMerge = autoMerge;
    }

    private Map<String, Integer> bookChapterNumMaps;
    private Map<String, Integer> bookSourceNumMaps;
    private Map<String, String> bookSourceMaps;
    private List<String> bookSourceList;

    private LinkedHashMap<String, CacheBooks> books;

    private List<CacheBooks> bookList;

    private String cachePath;

    @Override
    protected Void doInBackground(String... strings) {
        books = new LinkedHashMap<>();
        String backupPath = strings[0]+"autoSave/myBookShelf.json";
        this.cachePath = strings[1];
        readJson(new File(backupPath));
        return null;
    }

    private void readJson(File jsonFile) {
        try {
            FileReader r = new FileReader(jsonFile);
            BufferedReader br = new BufferedReader(r);
            StringBuffer json = new StringBuffer();
            String s;
            while ((s = br.readLine()) != null) {
                json = json.append(s).append("\n");
            }
            br.close();
            JSONArray jsonArray = JSON.parseArray(json.toString());
            for (Object object:jsonArray){
                JSONObject jsonBook = (JSONObject) JSONObject.toJSON(object);
                JSONObject bookInfoBean = (JSONObject) JSONObject.toJSON(jsonBook.get("bookInfoBean"));
                CacheBooks cacheBook = new CacheBooks();
                cacheBook.setName(bookInfoBean.get("name").toString());
                cacheBook.setAuthor(bookInfoBean.get("author").toString());
                cacheBook.setSource(bookInfoBean.get("origin").toString());
//                cacheBook.setCacheInfo(jsonBook.get("chapterListSize").toString());
                cacheBook.setCachePath(cachePath+bookInfoBean.get("name").toString()
                        +"-"
                        +bookInfoBean.get("tag")
                        .toString()
                        .replace(":" +
                                "//","")
                        .replace(".","")
                        +"/");
                books.put(bookInfoBean.get("name")
                        +"-"+bookInfoBean.get("author"),cacheBook);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        sbl.showBooks(books);
    }
}


