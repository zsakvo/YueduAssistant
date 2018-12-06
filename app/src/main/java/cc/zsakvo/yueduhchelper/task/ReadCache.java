package cc.zsakvo.yueduhchelper.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.zsakvo.yueduhchelper.listener.ReadCacheListener;

import static android.content.ContentValues.TAG;

public class ReadCache extends AsyncTask<Object,Integer,String> {

    private ReadCacheListener rcl;
    private ProgressDialog progressDialog;
    private int progress;

    public ReadCache(ReadCacheListener rcl, ProgressDialog progressDialog , int progress){
        this.rcl = rcl;
        this.progressDialog = progressDialog;
        this.progress = progress;
    }

    @Override
    protected final String doInBackground(Object... objects) {
        StringBuilder content = new StringBuilder();
        List<String> chapterPath;
        switch(objects[0].toString()){
            case "file":
                chapterPath = new ArrayList<>();
                File[] files = ((File)objects[1]).listFiles();
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
                    chapterPath.add(f.getAbsolutePath());
                }

                progressDialog.setMax(chapterPath.size());
                for (String s:chapterPath){
                    File file = new File(s);
                    if (!file.isDirectory()) {
                        if (file.getName().endsWith("nb")) {
                            try {
                                InputStream instream = new FileInputStream(file);
                                InputStreamReader inputreader
                                        = new InputStreamReader(instream, "UTF-8");
                                BufferedReader buffreader = new BufferedReader(inputreader);
                                String line = "";
                                while ((line = buffreader.readLine()) != null) {
                                    content.append(line).append("\n");
                                }
                                instream.close();
                            } catch (java.io.FileNotFoundException e) {
                                Log.d("ReadCache", "The File doesn't not exist.");
                            } catch (IOException e) {
                                Log.d("ReadCache", e.getMessage());
                            }
                        }
                    }
                    progress++;
                    publishProgress(progress);
                }
                break;
            case "list":
                chapterPath = (List<String>)objects[1];
                progressDialog.setMax(chapterPath.size());
                for (String s:chapterPath){
                    File file = new File(s);
                    if (!file.isDirectory()) {
                        if (file.getName().endsWith("nb")) {
                            try {
                                InputStream instream = new FileInputStream(file);
                                InputStreamReader inputreader
                                        = new InputStreamReader(instream, "UTF-8");
                                BufferedReader buffreader = new BufferedReader(inputreader);
                                String line = "";
                                while ((line = buffreader.readLine()) != null) {
                                    content.append(line).append("\n");
                                }
                                instream.close();
                            } catch (java.io.FileNotFoundException e) {
                                Log.d("ReadCache", "The File doesn't not exist.");
                            } catch (IOException e) {
                                Log.d("ReadCache", e.getMessage());
                            }
                        }
                    }
                    progress++;
                    publishProgress(progress);
                }
                break;
                default:
                    break;
        }

//        List<String> chapterPath = new ArrayList<>();
//
//        File[] files = fs[0].listFiles();
//        List fileList = Arrays.asList(files);
//        Collections.sort(fileList, new Comparator<File>() {
//            @Override
//            public int compare(File o1, File o2) {
//                if (o1.isDirectory() && o2.isFile())
//                    return -1;
//                if (o1.isFile() && o2.isDirectory())
//                    return 1;
//                return o1.getName().compareTo(o2.getName());
//            }
//        });
//
//        for (File f:files){
//            if (!f.getName().contains(".nb")) continue;
//            chapterPath.add(f.getAbsolutePath());
//        }
//
//        progressDialog.setMax(chapterPath.size());
//
//        StringBuilder content = new StringBuilder();
//        for (String s:chapterPath){
//            File file = new File(s);
//            if (!file.isDirectory()) {
//                if (file.getName().endsWith("nb")) {
//                    try {
//                        InputStream instream = new FileInputStream(file);
//                        InputStreamReader inputreader
//                                = new InputStreamReader(instream, "UTF-8");
//                        BufferedReader buffreader = new BufferedReader(inputreader);
//                        String line = "";
//                        while ((line = buffreader.readLine()) != null) {
//                            content.append(line).append("\n");
//                        }
//                        instream.close();
//                    } catch (java.io.FileNotFoundException e) {
//                        Log.d("ReadCache", "The File doesn't not exist.");
//                    } catch (IOException e) {
//                        Log.d("ReadCache", e.getMessage());
//                    }
//                }
//            }
//            progress++;
//            publishProgress(progress);
//        }
        return content.toString();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int vlaue = values[0];
        progressDialog.setProgress(vlaue);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        rcl.readCache(result);
    }

}
