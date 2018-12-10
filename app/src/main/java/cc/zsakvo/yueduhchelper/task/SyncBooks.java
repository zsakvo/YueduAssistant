package cc.zsakvo.yueduhchelper.task;

import android.os.AsyncTask;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.zsakvo.yueduhchelper.Dao.CacheBook;
import cc.zsakvo.yueduhchelper.listener.SyncBooksListener;

import static androidx.constraintlayout.widget.Constraints.TAG;


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

    private Map<String,CacheBook> books;

    private List<CacheBook> bookList;

    @Override
    protected Void doInBackground(String... strings) {
        books = new HashMap<>();
        if (autoMerge){
            String path = strings[0];
            File cacheFile = new File(path);
            try {
                for (File bookCacheDirs : cacheFile.listFiles()) {
                    if (!bookCacheDirs.isDirectory()) continue;
                    CacheBook cb;
                    String cacheBookDirName = bookCacheDirs.getName();
                    String[] cacheBookInfo = cacheBookDirName.split("-");
                    String cacheBookName = cacheBookInfo[0];
                    String cacheBookSource = bookCacheDirs.getAbsolutePath();

                    if (books.keySet().contains(cacheBookName)){
                        cb = books.get(cacheBookName);
                        List<String> list0 = cb.getBookSources();
                        list0.add(cacheBookSource);
                        cb.setBookSources(list0);

                        int chaptersNum = 0;
                        List<String> chapterNumList = new ArrayList<>();
                        for (File chapterCaches:bookCacheDirs.listFiles()){
                            if (!chapterCaches.getName().contains("-")) continue;
                            String[] chapterInfo = chapterCaches.getName().split("-");
                            String chapterNum = chapterInfo[0];
                            chapterNumList.add(chapterNum);
                            chaptersNum++;
                        }

                        List<String> list1 = cb.getChapterNum();
                        list1.removeAll(chapterNumList);
                        list1.addAll(chapterNumList);
                        cb.setChapterNum(list1);

                        cb.setAllBookChapters(cb.getAllBookChapters()+chaptersNum);
                        books.put(cacheBookName,cb);

                    }else {
                        cb = new CacheBook();
                        cb.setName(cacheBookName);
                        List<String> list = new ArrayList<String>();
                        list.add(cacheBookSource);
                        cb.setBookSources(list);
                        int chaptersNum = 0;
                        List<String> chapterNumList = new ArrayList<>();
                        for (File chapterCaches:bookCacheDirs.listFiles()){
                            if (!chapterCaches.getName().contains("-")) continue;
                            String[] chapterInfo = chapterCaches.getName().split("-");
                            String chapterNum = chapterInfo[0];
                            chapterNumList.add(chapterNum);
                            chaptersNum++;
                        }
                        cb.setChapterNum(chapterNumList);
                        cb.setAllBookChapters(cb.getAllBookChapters()+chaptersNum);
                        books.put(cacheBookName,cb);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            String path = strings[0];
            File cacheFile = new File(path);
            try {
                for (File bookCacheDirs : cacheFile.listFiles()) {
                    if (!bookCacheDirs.isDirectory()) continue;
                    CacheBook cb;
                    String cacheBookDirName = bookCacheDirs.getName();
                    String[] cacheBookInfo = cacheBookDirName.split("-");
                    String cacheBookName = cacheBookInfo[0];
                    String cacheBookSource = bookCacheDirs.getAbsolutePath();

                    cb = new CacheBook();
                    cb.setName(cacheBookName);
                    List<String> list = new ArrayList<String>();
                    list.add(cacheBookSource);
                    cb.setBookSources(list);
                    int chaptersNum = 0;
                    List<String> chapterNumList = new ArrayList<>();
                    for (File chapterCaches:bookCacheDirs.listFiles()){
                        if (!chapterCaches.getName().contains("-")) continue;
                        chaptersNum++;
                    }
                    cb.setAllBookChapters(chaptersNum);
                    books.put(cacheBookDirName,cb);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
//        bookList = new ArrayList<>();
//        for (String s:books.keySet()){
//            bookList.add(books.get(s));
//        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        super.onPostExecute (v);
        sbl.showBooks(books);
    }
}








//package cc.zsakvo.yueduhchelper.task;
//
//        import android.os.AsyncTask;
//        import android.util.Log;
//
//        import java.io.File;
//        import java.util.ArrayList;
//        import java.util.HashMap;
//        import java.util.List;
//        import java.util.Map;
//
//        import cc.zsakvo.yueduhchelper.listener.SyncBooksListener;
//
//        import static android.content.ContentValues.TAG;
//
//@SuppressWarnings("ALL")
//public class SyncBooks extends AsyncTask<String, Void, Void> {
//
//    private SyncBooksListener sbl;
//    private Boolean autoMerge;
//
//    public SyncBooks(SyncBooksListener sbl,Boolean autoMerge){
//        this.sbl = sbl;
//        this.autoMerge = autoMerge;
//    }
//
//    private Map<String, Integer> bookChapterNumMaps;
//    private Map<String, Integer> bookSourceNumMaps;
//    private Map<String,String> bookSourceMaps;
//    private List<String> bookSourceList;
//
//    @Override
//    protected Void doInBackground(String... strings) {
//        if (autoMerge){
//            bookSourceList = new ArrayList<>();
//            bookChapterNumMaps = new HashMap<String, Integer>();
//            bookSourceNumMaps = new HashMap<String, Integer>();
//            bookSourceMaps = new HashMap<>();
//            String path = strings[0];
//            File cacheFile = new File(path);
//            try {
//                for (File f : cacheFile.listFiles()) {
//                    if (!f.isDirectory()) continue;
//                    int cp = 0;
//                    String name = f.getName();
//                    String source = name.split("-")[1];
//                    name = name.split("-")[0];
//                    for (File file:f.listFiles()){
//                        if (!file.getName().contains("-")) continue;
//                        cp++;
//                    }
//
//                    if (!bookSourceMaps.keySet().contains(name)){
//                        bookSourceMaps.put(name,source);
//                        bookChapterNumMaps.put(name,cp);
//                        bookSourceNumMaps.put(name,1);
//                        bookSourceList.add(name);
//                    }else {
//                        bookSourceMaps.put(name,bookSourceMaps.get(name)+","+source);
//                        bookChapterNumMaps.put(name,bookChapterNumMaps.get(name)+cp);
//                        bookSourceNumMaps.put(name,bookSourceNumMaps.get(name)+1);
//                    }
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }else {
//            bookSourceMaps = new HashMap<>();
//            bookSourceList = new ArrayList<>();
//            bookChapterNumMaps = new HashMap<String, Integer>();
//            bookSourceNumMaps = new HashMap<String, Integer>();
//            String path = strings[0];
//            File cacheFile = new File(path);
//            try {
//                for (File f : cacheFile.listFiles()) {
//                    if (!f.isDirectory()) continue;
//                    int cp = 0;
//                    for (File file:f.listFiles()){
//                        if (!file.getName().contains("-")) continue;
//                        cp++;
//                    }
//                    String name = f.getName();
//                    String source = name.split("-")[1];
//                    bookSourceMaps.put(name,source);
//                    bookChapterNumMaps.put(name,cp);
//                    bookSourceList.add(name);
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute( Void v){
//        super.onPostExecute (v);
//        sbl.showBooks(bookSourceList,bookSourceMaps,bookChapterNumMaps,bookSourceNumMaps);
//    }
//}
