package cc.zsakvo.yueduhchelper.task;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.zsakvo.yueduhchelper.listener.SyncChaptersListener;

public class SyncChapters extends AsyncTask<String,Void,List<String>> {

    private SyncChaptersListener scl;

    public SyncChapters(SyncChaptersListener scl){
        this.scl = scl;
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> list = new ArrayList<>();

        File[] files = new File(strings[0]).listFiles();
        List fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (File f:files){
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
