package cc.zsakvo.yueduhchelper.listener;

import java.util.LinkedHashMap;

import cc.zsakvo.yueduhchelper.bean.CacheBooks;

public interface SyncBooksListener {
    void showBooks(LinkedHashMap<String, CacheBooks> books,int type);
}
