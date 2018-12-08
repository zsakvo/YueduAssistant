package cc.zsakvo.yueduhchelper.listener;

import java.util.List;
import java.util.Map;

public interface SyncBooksListener  {
    void showBooks(List<String> list,Map<String, String> bsm,Map<String, Integer> bcm,Map<String, Integer> bsnm,int validNum);
}
