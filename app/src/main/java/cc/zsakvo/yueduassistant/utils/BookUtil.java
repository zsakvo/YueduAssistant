package cc.zsakvo.yueduassistant.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.orhanobut.logger.Logger;

import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.bean.CacheChapter;
import cc.zsakvo.yueduassistant.bean.ExportBook;
import cc.zsakvo.yueduassistant.listener.ExportListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BookUtil {

    private View v;
    private List<CacheChapter> cacheChapters;
    private List<Boolean> flags;
    private List<CacheChapter> chapters;
    private String bookPath;
    private String fileName;
    private String name;
    private String author;
    private String cover;
    private String outputDirPath;
    private Context mContext;
    private AlertDialog progressDialog;
    private TextView tv_progress;
    private ExportListener el;

    public BookUtil(ExportBook exportBook, ExportListener el) {
        this.el = el;
        this.cacheChapters = exportBook.getCacheChapters();
        this.flags = exportBook.getFlags();
        this.bookPath = exportBook.getBookPath();
        this.outputDirPath = exportBook.getOutputDirPath();
        this.fileName = exportBook.getFileName();
        this.mContext = exportBook.getmContext();
        this.name = exportBook.getName();
        this.author = exportBook.getAuthor();
        this.cover = exportBook.getCover();
    }

    private List<CacheChapter> selectChapters() {
        List<CacheChapter> chapters = new ArrayList<>();
        for (int i = 0; i < flags.size(); i++) {
            if (flags.get(i)) chapters.add(cacheChapters.get(i));
        }
        return chapters;
    }

    private void fileInit(int i) {
        File outPutDir = new File(outputDirPath);
        if (!outPutDir.exists()) {
            if (!outPutDir.mkdirs()) {
                Logger.e("输出目录生成失败！");
            }
        } else {
            switch (i) {
                case 0:
                    File outFile = new File(outputDirPath + "/" + fileName);
                    if (outFile.exists()) {
                        if (!outFile.delete()) {
                            Logger.e("已存在目标清除失败！");
                        }
                    } else {
                        try {
                            if (!outFile.createNewFile()) {
                                Logger.e("输出文件创建失败！");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    File outTmp = new File(outputDirPath + "/" + fileName.replace(".txt", "") + "-ep/");
                    if (outTmp.exists()) {
                        deleteDirectory(outTmp.getAbsolutePath());
                    } else {
                        if (!outTmp.mkdirs()) {
                            Logger.d("epub-临时目录生成失败");
                        }
                    }
                    break;
            }
        }
    }

    private void preEpubFile(String str, String path) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path)), StandardCharsets.UTF_8));
            writer.write(str);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void genCover(String path) {
        try {
            Bitmap bitmap = Glide.with(mContext).asBitmap().load(cover).submit().get();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(path)));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void extractEpub() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        View view = View.inflate(mContext, R.layout.loading_dialog, null);
        builder.setView(view);
        progressDialog = builder.create();
        tv_progress = view.findViewById(R.id.progress_text);
        progressDialog.show();
        String book_ep = outputDirPath + "/" + fileName.replace(".txt", "") + "-ep/";

        Observable.create((ObservableEmitter<Integer> emitter) -> {
            try {
                fileInit(1);
                chapters = BookUtil.this.selectChapters();
                copyAssets(mContext, "epub", book_ep);
                preEpubFile(genOpf(), book_ep + "/OEBPS/content.opf");
                preEpubFile(genTocNcx(), book_ep + "/OEBPS/toc.ncx");
                preEpubFile(genPart0(), book_ep + "/OEBPS/Text/0.xhtml");
                genCover(book_ep + "/OEBPS/Images/cover.jpg");
                int i = 0;
                for (CacheChapter chapter : chapters) {
                    int c = i + 1;
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(book_ep + "/OEBPS/Text/" + c + ".xhtml")), StandardCharsets.UTF_8));
                    File chapterFile = new File(bookPath + "/" + chapter.getFileName());
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(chapterFile));
                    BufferedReader br = new BufferedReader(reader);
                    String s;
                    writer.write("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n" +
                            "  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head>\n" +
                            "  <title></title>\n" +
                            "  <link href=\"../Styles/style0002.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                            "</head><body>\n" +
                            "  <div style=\"page-break-after:always\"></div>");
                    writer.newLine();
                    writer.flush();
                    int li = 0;
                    while ((s = br.readLine()) != null) {
                        if (li == 0) {
                            writer.write("<h1 class=\"kindle-cn-heading-1\">" + s + "</h1>");
                        } else {
                            writer.write("<p>" + s + "</p>");
                        }
                        writer.newLine();
                        writer.flush();
                        li++;
                    }
                    writer.write("</body></html>");
                    writer.newLine();
                    writer.flush();
                    writer.close();
                    i++;
                    emitter.onNext(i);
                }
                emitter.onNext(-1);
                ZipUtil.pack(new File(book_ep), new File(outputDirPath + "/" + fileName.replace(".txt", "") + ".epub"), new NameMapper() {
                    public String map(String name) {
                        return name;
                    }
                });
                emitter.onNext(-2);
                deleteDirectory(book_ep);
                emitter.onComplete();
            } catch (Exception e) {
                Logger.e(e.toString());
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer i) {
                        if (i == -1) {
                            tv_progress.setText("正在打包文件……");
                        } else if (i == -2) {
                            tv_progress.setText("正在清理临时目录……");
                        } else {
                            tv_progress.setText(String.format(mContext.getResources().getString(R.string.exporting), i, chapters.size()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("完毕！");
                        progressDialog.cancel();
                        if (SpUtil.getAutoDel(mContext)) {
                            deleteDirectory(bookPath);
                        }
                        el.exportFinish();
                    }
                });
    }

    public void extractTXT() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        View view = View.inflate(mContext, R.layout.loading_dialog, null);
        builder.setView(view);
        progressDialog = builder.create();
        tv_progress = view.findViewById(R.id.progress_text);
        progressDialog.show();
        Observable.create((ObservableEmitter<Integer> emitter) -> {
            try {
                fileInit(0);
                chapters = BookUtil.this.selectChapters();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputDirPath + "/" + fileName)), StandardCharsets.UTF_8));
                int i = 0;
                for (CacheChapter chapter : chapters) {
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(bookPath + "/" + chapter.getFileName())));
                    BufferedReader br = new BufferedReader(reader);
                    String s;
                    while ((s = br.readLine()) != null) {
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
                    public void onSubscribe(Disposable d) {
                    }

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
                        if (SpUtil.getAutoDel(mContext)) {
                            deleteDirectory(bookPath);
                        }
                        el.exportFinish();
                    }
                });
    }

    private void deleteDirectory(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
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

    private static void copy(Context context, String zipPath, String targetPath) {
        if (TextUtils.isEmpty(zipPath) || TextUtils.isEmpty(targetPath)) {
            return;
        }
        File dest = new File(targetPath);
        dest.getParentFile().mkdirs();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(context.getAssets().open(zipPath));
            out = new BufferedOutputStream(new FileOutputStream(dest));
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void copyAssets(Context context, String assetDir, String targetDir) {
        if (TextUtils.isEmpty(assetDir) || TextUtils.isEmpty(targetDir)) {
            return;
        }
        String separator = File.separator;
        try {
            // 获取assets目录assetDir下一级所有文件以及文件夹
            String[] fileNames = context.getResources().getAssets().list(assetDir);
            // 如果是文件夹(目录),则继续递归遍历
            if (fileNames.length > 0) {
                File targetFile = new File(targetDir);
                if (!targetFile.exists() && !targetFile.mkdirs()) {
                    return;
                }
                for (String fileName : fileNames) {
                    copyAssets(context, assetDir + separator + fileName, targetDir + separator + fileName);
                }
            } else { // 文件,则执行拷贝
                copy(context, assetDir, targetDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String genPart0() {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n" +
                "  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head>\n" +
                "  <title>Contents</title>\n" +
                "  <link href=\"../Styles/style0001.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "</head><body>\n" +
                "  <div class=\"sgc-toc-title\">\n" +
                "    目录\n" +
                "  </div>");
        for (int i = 0; i < chapters.size(); i++) {
            sb.append("<div class=\"sgc-toc-level-1\">\n" +
                    "    <a href=\"")
                    .append(i + 1)
                    .append(".xhtml\">")
                    .append(chapters.get(i).getName())
                    .append("</a>\n" +
                            "  </div>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    private String genTocNcx() {
        StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?>\n" +
                "<ncx xmlns=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\" xml:lang=\"zh\">\n" +
                "  <head>\n" +
                "    <meta content=\"1\" name=\"dtb:depth\"/>\n" +
                "    <meta content=\"YueduAssistant\" name=\"dtb:generator\"/>\n" +
                "    <meta content=\"0\" name=\"dtb:totalPageCount\"/>\n" +
                "    <meta content=\"0\" name=\"dtb:maxPageNumber\"/>\n" +
                "  </head>\n" +
                "  <docTitle>\n" +
                "    <text>");
        sb.append(fileName.substring(0, fileName.length() - 4));
        sb.append("</text>\n" +
                "</docTitle>\n" +
                "<navMap>");
        for (int i = 0; i < chapters.size(); i++) {
            sb.append("<navPoint id=\"np_")
                    .append(i)
                    .append("\" playOrder=\"")
                    .append(i)
                    .append("\">\n" +
                            "    <navLabel>\n" +
                            "    <text>")
                    .append(chapters.get(i).getName())
                    .append("</text>\n" +
                            "    </navLabel>\n" +
                            "    <content src=\"Text/")
                    .append(i + 1)
                    .append(".xhtml\"/>\n" +
                            "    </navPoint>");
        }
        sb.append("</navMap>\n" +
                "</ncx>");
        return sb.toString();
    }

    private String genOpf() {
        StringBuilder sb0 = new StringBuilder();
        StringBuilder sb1 = new StringBuilder("<manifest>");
        StringBuilder sb2 = new StringBuilder("<spine toc=\"ncx\">");
        sb0.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<package version=\"2.0\" \n" +
                "    xmlns=\"http://www.idpf.org/2007/opf\">\n" +
                "    <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\" \n" +
                "        xmlns:opf=\"http://www.idpf.org/2007/opf\">\n" +
                "        <dc:title>")
                .append(name)
                .append("</dc:title>\n" +
                        "        <dc:language>zh</dc:language>\n" +
                        "        <dc:creator>")
                .append(author)
                .append("</dc:creator>\n" +
                        "        <meta name=\"cover\" content=\"cover.jpg\" />\n" +
                        "        <meta name=\"output encoding\" content=\"utf-8\" />\n" +
                        "        <meta name=\"primary-writing-mode\" content=\"horizontal-lr\" />\n" +
                        "    </metadata>");
        sb1.append(" <item id=\"item30\" media-type=\"text/css\" href=\"Styles/style0001.css\" />\n" +
                "        <item id=\"item31\" media-type=\"text/css\" href=\"Styles/style0002.css\" />\n" +
                "        <item id=\"cover.jpg\" media-type=\"image/jpeg\" href=\"Images/cover.jpg\" />\n" +
                "        <item id=\"ncx\" media-type=\"application/x-dtbncx+xml\" href=\"toc.ncx\" />");
        for (int i = 0; i < chapters.size(); i++) {
            sb1.append("<item id=\"x_Section")
                    .append(i + 1)
                    .append(".xhtml\" media-type=\"application/xhtml+xml\" href=\"Text/")
                    .append(i + 1)
                    .append(".xhtml\" />");
            sb2.append("<itemref idref=\"x_Section")
                    .append(i + 1)
                    .append(".xhtml\"/>");
        }
        sb1.append("</manifest>");
        sb2.append("</spine>");
        sb0.append(sb1)
                .append(sb2)
                .append("</package>");
        return sb0.toString();
    }
}
