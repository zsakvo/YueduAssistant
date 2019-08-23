package cc.zsakvo.yueduassistant.utils;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.bean.CacheChapter;
import cc.zsakvo.yueduassistant.bean.ExportBook;
import cc.zsakvo.yueduassistant.listener.ExportListener;
import cc.zsakvo.yueduassistant.view.BookDetailActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BookUtil {

    private View v;
    private List<CacheChapter> cacheChapters;
    private List<Boolean> flags;
    private List<String> chapters;
    private String bookPath;
    private String fileName;
    private String outputDirPath;
    private Context mContext;
    private AlertDialog progressDialog;
    private TextView tv_progress;
    private ExportListener el;

    public BookUtil(ExportBook exportBook,View view,ExportListener el){
        this.v = view;
        this.el = el;
        this.cacheChapters = exportBook.getCacheChapters();
        this.flags = exportBook.getFlags();
        this.bookPath = exportBook.getBookPath();
        this.outputDirPath = exportBook.getOutputDirPath();
        this.fileName = exportBook.getFileName();
        this.mContext = exportBook.getmContext();
    }

    private List<String> selectChapters(){
        List<String> chapters = new ArrayList<>();
        for (int i=0;i<flags.size();i++){
            if (flags.get(i)) chapters.add(bookPath+"/"+cacheChapters.get(i).getFileName());
        }
        return chapters;
    }

    private void fileInit(){
        File outPutDir = new File(outputDirPath);
        if (!outPutDir.exists()){
            if(!outPutDir.mkdirs()){
                Logger.e("输出目录生成失败！");
            }
        }else {
            File outFile = new File(outputDirPath+"/"+fileName);
            if (outFile.exists()){
                if (!outFile.delete()){
                    Logger.e("已存在目标清除失败！");
                }
            }else {
                try {
                    if (!outFile.createNewFile()){
                        Logger.e("输出文件创建失败！");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void extractTXT(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        View view = View.inflate(mContext, R.layout.loading_dialog, null);
        builder.setView(view);
        progressDialog = builder.create();
        tv_progress =  view.findViewById(R.id.progress_text);
        progressDialog.show();
        Observable.create((ObservableEmitter<Integer> emitter) -> {
            try {
                fileInit();
                chapters = BookUtil.this.selectChapters();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(new File(outputDirPath+"/"+fileName)), StandardCharsets.UTF_8));
                int i = 0;
                for (String chapter : chapters) {
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(chapter)));
                    BufferedReader br = new BufferedReader(reader);
                    String s;
                    while ((s=br.readLine())!=null) {
                        writer.write(s);
                        writer.newLine();
                        writer.flush();
                    }
                    i++;
                    emitter.onNext(i);
                }
                writer.close();
                emitter.onComplete();
            } catch (Exception e) {
                Logger.e(e.toString());
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(Integer i) {
                        tv_progress.setText(String.format(mContext.getResources().getString(R.string.exporting), i, chapters.size()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("完毕！");
                        progressDialog.cancel();
                        SnackbarUtil.build(mContext, v, "导出成功", Snackbar.LENGTH_SHORT).show();
                        if(SpUtil.getAutoDel(mContext)){
                            deleteDirectory(bookPath);
                        }
                        el.exportFinish();
                    }
                });
    }

    private void deleteDirectory(String path) {
        File file = new File(path);
        File files[] = file.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.isFile()) {
                    file1.delete();
                } else if (file1.isDirectory()) {
                    deleteDirectory(file1.getAbsolutePath());
                }
            }
            file.delete();
        }
    }
}
