package cc.zsakvo.yueduhchelper.task;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import cc.zsakvo.yueduhchelper.listener.SyncChaptersListener;

public class SyncChapters extends AsyncTask<List<String>, Void, List<String>> {

    private SyncChaptersListener scl;

    public SyncChapters(SyncChaptersListener scl) {
        this.scl = scl;
    }

    private static void removeDuplicate(List<File> list) {
        LinkedHashSet<File> set = new LinkedHashSet<>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
    }

    @SafeVarargs
    @Override
    protected final List<String> doInBackground(List<String>... lists) {
//        File[] cacheFiles = new File[]{};
        List<File> cacheFiles = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        List<String> list = new ArrayList<>();

        for (String s : lists[0]) {
            for (File f : new File(s).listFiles()) {
                String num = f.getName().split("-")[0] + "-";
                if (!nameList.contains(num)) {
                    nameList.add(num);
                    cacheFiles.add(f);
                }
            }
        }

        cacheFiles = new ArrayList<>(new HashSet<>(cacheFiles));
//        removeDuplicate(cacheFiles);
//按创建时间排序
//        Collections.sort(cacheFiles, new Comparator<File>() {
//                    public int compare(File f1, File f2) {
//                        long diff = f1.lastModified() - f2.lastModified();
//                        if (diff > 0)
//                            return 1;
//                        else if (diff == 0)
//                            return 0;
//                        else
//                            return -1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
//                    }
//                });

//按文件名排序
        Collections.sort(cacheFiles, (o1, o2) -> {
            if (o1.isDirectory() && o2.isFile())
                return -1;
            if (o1.isFile() && o2.isDirectory())
                return 1;
            return o1.getName().compareTo(o2.getName());
        });

        for (File f : cacheFiles) {
            if (!f.getName().contains(".nb")) continue;
            list.add(f.getAbsolutePath());
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<String> list) {
        super.onPostExecute(list);
        scl.showChapters(list);
    }
}
