package cc.zsakvo.yueduhchelper.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.zsakvo.yueduhchelper.listener.SyncBooksListener;

import static android.content.ContentValues.TAG;

@SuppressWarnings("ALL")
public class SyncBooks extends AsyncTask<String, Void, Void> {

    private SyncBooksListener sbl;
    private Boolean autoMerge;

    public SyncBooks(SyncBooksListener sbl,Boolean autoMerge){
        this.sbl = sbl;
        this.autoMerge = autoMerge;
    }

    private Map<String, Integer> bookChapterNumMaps;
    private Map<String, Integer> bookSourceNumMaps;
    private Map<String,String> bookSourceMaps;
    private List<String> bookSourceList;
    private int validNum;

    @Override
    protected Void doInBackground(String... strings) {
        if (autoMerge){
            bookSourceList = new ArrayList<>();
            bookChapterNumMaps = new HashMap<String, Integer>();
            bookSourceNumMaps = new HashMap<String, Integer>();
            bookSourceMaps = new HashMap<>();
            List<String> bookNames = new ArrayList<>();
            String path = strings[0];
            File cacheFile = new File(path);
            try {
                for (File f : cacheFile.listFiles()) {
                    if (!f.isDirectory()) continue;
                    int cp = 0;
                    for (File file:f.listFiles()){
                        if (!file.getName().contains("-")) continue;
                        cp++;
                        String num = file.getName().split("-")[0]+"-";
                        if (!bookNames.contains(num)) bookNames.add(num);
                    }
                    String name = f.getName();
                    String source = name.split("-")[1];
                    name = name.split("-")[0];
                    if (!bookSourceMaps.keySet().contains(name)){
                        bookSourceMaps.put(name,source);
                        bookChapterNumMaps.put(name,cp);
                        bookSourceNumMaps.put(name,1);
                        bookSourceList.add(name);
                    }else {
                        bookSourceMaps.put(name,bookSourceMaps.get(name)+","+source);
                        bookChapterNumMaps.put(name,bookChapterNumMaps.get(name)+cp);
                        bookSourceNumMaps.put(name,bookSourceNumMaps.get(name)+1);
                    }
                }
                validNum = bookNames.size();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            bookSourceMaps = new HashMap<>();
            bookSourceList = new ArrayList<>();
            bookChapterNumMaps = new HashMap<String, Integer>();
            bookSourceNumMaps = new HashMap<String, Integer>();
            String path = strings[0];
            File cacheFile = new File(path);
            try {
                for (File f : cacheFile.listFiles()) {
                    if (!f.isDirectory()) continue;
                    int cp = 0;
                    for (File file:f.listFiles()){
                        if (!file.getName().contains("-")) continue;
                        cp++;
                    }
                    String name = f.getName();
                    String source = name.split("-")[1];
                    bookSourceMaps.put(name,source);
                    bookChapterNumMaps.put(name,cp);
                    bookSourceList.add(name);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute( Void v){
        super.onPostExecute (v);
        sbl.showBooks(bookSourceList,bookSourceMaps,bookChapterNumMaps,bookSourceNumMaps,validNum);
    }
}
