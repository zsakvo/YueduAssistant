package cc.zsakvo.yueduhchelper.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import cc.zsakvo.yueduhchelper.listener.SyncChaptersListener;


public class SyncChapters extends AsyncTask<String, Void, List<String>> {

    private static final String TAG = "SyncChapters";
    private SyncChaptersListener scl;
    private List<File> cacheFiles;
    public SyncChapters(SyncChaptersListener scl) {
        this.scl = scl;
    }


    @Override
    protected final List<String> doInBackground(String... strings) {
        List<String> nums = new ArrayList<>();
        LinkedHashMap<File,Boolean> fileMap = new LinkedHashMap<>();

        Log.e(TAG, "doInBackground: "+strings[0] );

            File[] files = new File(strings[0]).listFiles();

            if (files!=null){
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
                        Log.e(TAG, "doInBackground: "+num );
                        nums.add(num);
                        fileMap.put(f,true);
                    }else {
                        fileMap.put(f,false);
                    }
                }
                cacheFiles = new ArrayList<>(fileMap.keySet());
            }


        return nums;
    }

    @Override
    protected void onPostExecute(List<String> list) {
        super.onPostExecute(list);
        scl.showChapters(cacheFiles);
    }

}
