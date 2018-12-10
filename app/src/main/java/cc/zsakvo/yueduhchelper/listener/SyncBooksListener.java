package cc.zsakvo.yueduhchelper.listener;

import java.util.List;
import java.util.Map;

import cc.zsakvo.yueduhchelper.Dao.CacheBook;

public interface SyncBooksListener  {
    void showBooks(Map<String,CacheBook> books);
}
