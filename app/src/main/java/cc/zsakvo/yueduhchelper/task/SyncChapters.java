package cc.zsakvo.yueduhchelper.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import cc.zsakvo.yueduhchelper.listener.SyncChaptersListener;


public class SyncChapters extends AsyncTask<String, Void, List<String>> {

    private SyncChaptersListener scl;
    private List<File> cacheFiles;
    private List<Boolean> isDum;
    public SyncChapters(SyncChaptersListener scl) {
        this.scl = scl;
    }

    @SafeVarargs
    @Override
    protected final List<String> doInBackground(String... strings) {
        List<String> nums = new ArrayList<>();
        LinkedHashMap<File,Boolean> fileMap = new LinkedHashMap<>();

            File[] files = new File(strings[0]).listFiles();

            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                }
            });


            for (File f : files) {
                String num = f.getName().split("-")[0] + "-";
                if (!nums.contains(num)) {
                    nums.add(num);
                    fileMap.put(f,true);
                }else {
                    fileMap.put(f,false);
                }
            }


        isDum = new ArrayList<>();
        cacheFiles = new ArrayList<>(fileMap.keySet());
//        removeDuplicate(cacheFiles);
//        按创建时间排序
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
//        Collections.sort(cacheFiles, (o1, o2) -> {
//            if (o1.isDirectory() && o2.isFile())
//                return -1;
//            if (o1.isFile() && o2.isDirectory())
//                return 1;
//            return o1.getName().compareTo(o2.getName());
//        });

        for (File f:cacheFiles){
            isDum.add(fileMap.get(f));
        }

        return nums;
    }

    @Override
    protected void onPostExecute(List<String> list) {
        super.onPostExecute(list);
        scl.showChapters(cacheFiles,isDum);
    }
}
