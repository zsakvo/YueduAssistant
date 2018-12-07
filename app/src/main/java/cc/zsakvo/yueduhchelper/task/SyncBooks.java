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

public class SyncBooks extends AsyncTask<String,Void, Map<String, Integer>> {

    private SyncBooksListener sbl;

    public SyncBooks(SyncBooksListener sbl){
        this.sbl = sbl;
    }

    @Override
    protected  Map<String, Integer> doInBackground(String... strings) {
        Map<String, Integer> map = new HashMap<String, Integer>();
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
                map.put(f.getName(),cp);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return map;
    }

    @Override
    protected void onPostExecute( Map<String, Integer> maps){
        super.onPostExecute (maps);
        sbl.showBooks(maps);
    }
}
