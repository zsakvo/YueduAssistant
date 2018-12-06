package cc.zsakvo.yueduhchelper.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import cc.zsakvo.yueduhchelper.listener.WriteFileListener;

public class WriteFile extends AsyncTask<String,Void,Boolean> {

    private WriteFileListener wfl;

    public WriteFile(WriteFileListener wfl){
        this.wfl = wfl;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }else {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        makeFilePath(strings[1],strings[2]);
        try{
            FileOutputStream fout = new FileOutputStream(strings[1]+strings[2]);
            byte [] bytes = strings[0].getBytes();
            fout.write(bytes);
            fout.close();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        wfl.writeFileResult(result);
    }
}
