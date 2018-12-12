package cc.zsakvo.yueduhchelper.listener;

import java.io.File;
import java.util.List;

public interface SyncChaptersListener {
    void showChapters(List<File> cacheFiles,List<Boolean> isDum);
}
