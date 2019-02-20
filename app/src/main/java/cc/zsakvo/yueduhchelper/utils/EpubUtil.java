package cc.zsakvo.yueduhchelper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import cc.zsakvo.yueduhchelper.bean.ExportChapter;

public class EpubUtil {
    private static final String TAG = "EpubUtil";
    public static String BOOKTOC = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\">\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml; charset=utf-8\" />\n" +
            "<meta name=\"generator\" content=\"EasyPub v1.50\" />\n" +
            "<title>\n" +
            "Table Of Contents\n" +
            "</title>\n" +
            "<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\"/>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h2 class=\"titletoc\"> 目录 </h2>\n" +
            "<div class=\"toc\">\n" +
            "<dl>\n" +
            "toreplace0\n" +
            "</dl>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>\n";
    public static String CHAPTER = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\">\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml; charset=utf-8\" />\n" +
            "<meta name=\"generator\" content=\"EasyPub v1.50\" />\n" +
            "<title>\n" +
            "toreplace0 - 0\n" +
            "</title>\n" +
            "<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\"/>\n" +
            "</head>\n" +
            "<body>\n" +
            "toreplace1\n" +
            "</body>\n" +
            "</html>";
    public static String CONTAINER = "<?xml version=\"1.0\"?>\n" +
            "<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">\n" +
            "  <rootfiles>\n" +
            "    <rootfile full-path=\"OEBPS/content.opf\" media-type=\"application/oebps-package+xml\"/>\n" +
            "  </rootfiles>\n" +
            "</container>";
    public static String CONTENT = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n" +
            "<package version=\"2.0\" xmlns=\"http://www.idpf.org/2007/opf\" unique-identifier=\"bookid\">\n" +
            "<metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:opf=\"http://www.idpf.org/2007/opf\">\n" +
            "<dc:identifier id=\"bookid\">easypub-c260e840</dc:identifier>\n" +
            "<dc:title>toreplace0</dc:title>\n" +
            "<dc:creator opf:role=\"aut\">toreplace1</dc:creator>\n" +
            "<dc:date>2018</dc:date>\n" +
            "<dc:rights>Created with EasyPub v1.50</dc:rights>\n" +
            "<dc:language>zh-CN</dc:language>\n" +
            "<meta name=\"cover\" content=\"cover-image\"/>\n" +
            "</metadata>\n" +
            "toreplace2\n" +
            "toreplace3" +
            "<guide>\n" +
            "<reference href=\"cover.html\" type=\"cover\" title=\"Cover\"/>\n" +
            "<reference href=\"book-toc.html\" type=\"toc\" title=\"Table Of Contents\"/>\n" +
            "<reference href=\"chapter0.html\" type=\"text\" title=\"Beginning\"/>\n" +
            "</guide>\n" +
            "</package>\n";
    public static String MIMETYPE = "application/epub+zip";
    public static String STYLE = "@font-face {\n" +
            "      font-family: \"easypub\";\n" +
            "      src: url(res:///system/fonts/DroidSansFallback.ttf),\n" +
            "           url(res:///ebook/fonts/../../system/fonts/DroidSansFallback.ttf);\n" +
            "}\n" +
            "\n" +
            "@page { \n" +
            "      margin-top: 0px;\n" +
            "      margin-bottom: 0px;\n" +
            "}\n" +
            "\n" +
            "body { \n" +
            "      font-family: \"easypub\";\n" +
            "      padding: 0;\n" +
            "      margin-left: 0px;\n" +
            "      margin-right: 0px;\n" +
            "      orphans: 0;\n" +
            "      widows: 0;\n" +
            "}\n" +
            "\n" +
            "p { \n" +
            "      font-family: \"easypub\";\n" +
            "      font-size: 120%;\n" +
            "      line-height: 125%;\n" +
            "      margin-top: 5px;\n" +
            "      margin-bottom: 0;\n" +
            "      margin-left: 0;\n" +
            "      margin-right: 0;\n" +
            "      orphans: 0;\n" +
            "      widows: 0;\n" +
            "}\n" +
            "\n" +
            ".a { \n" +
            "      text-indent: 0em;\n" +
            "}\n" +
            "\n" +
            "div.centeredimage {\n" +
            "      text-align:center;\n" +
            "      display:block;\n" +
            "      margin-top: 0.5em;\n" +
            "      margin-bottom: 0.5em;\n" +
            "}\n" +
            "\n" +
            "img.attpic {\n" +
            "      border: 1px solid #000000;\n" +
            "      max-width: 100%;\n" +
            "      margin: 0;\n" +
            "}\n" +
            "\n" +
            ".booktitle {\n" +
            "      margin-top: 30%;\n" +
            "      margin-bottom: 0;\n" +
            "      border-style: none solid none none;\n" +
            "      border-width: 50px;\n" +
            "      border-color: #4E594D;\n" +
            "      font-size: 3em;\n" +
            "      line-height: 120%;\n" +
            "      text-align: right;\n" +
            "}\n" +
            "\n" +
            ".bookauthor {\n" +
            "      margin-top: 0;\n" +
            "      border-style: none solid none none;\n" +
            "      border-width: 50px;\n" +
            "      border-color: #4E594D;\n" +
            "      page-break-after: always;\n" +
            "      font-size: large;\n" +
            "      line-height: 120%;\n" +
            "      text-align: right;\n" +
            "}\n" +
            "\n" +
            ".titletoc, .titlel1top, .titlel1std,.titlel2top, .titlel2std,.titlel3top, .titlel3std,.titlel4std {\n" +
            "      margin-top: 0;\n" +
            "      border-style: none double none solid;\n" +
            "      border-width: 0px 5px 0px 20px;\n" +
            "      border-color: #586357;\n" +
            "      background-color: #C1CCC0;\n" +
            "      padding: 45px 5px 5px 5px;\n" +
            "      font-size: x-large;\n" +
            "      line-height: 115%;\n" +
            "      text-align: justify;\n" +
            "}\n" +
            "\n" +
            ".titlel1single,.titlel2single,.titlel3single {\n" +
            "      margin-top: 35%;\n" +
            "      border-style: none solid none none;\n" +
            "      border-width: 30px;\n" +
            "      border-color: #4E594D;\n" +
            "      padding: 30px 5px 5px 5px;\n" +
            "      font-size: x-large;\n" +
            "      line-height: 125%;\n" +
            "      text-align: right;\n" +
            "}\n" +
            "\n" +
            ".toc {\n" +
            "      margin-left:16%;\n" +
            "      padding:0px;\n" +
            "      line-height:130%;\n" +
            "      text-align: justify;\n" +
            "}\n" +
            "\n" +
            ".toc a { text-decoration: none; color: #000000; }\n" +
            "\n" +
            ".tocl1 {\n" +
            "      margin-top:0.5em;\n" +
            "      margin-left:-30px;\n" +
            "      border-style: none double double solid;\n" +
            "      border-width: 0px 5px 2px 20px;\n" +
            "      border-color: #6B766A;\n" +
            "      line-height: 135%;\n" +
            "      font-size: 132%;\n" +
            "}\n" +
            "\n" +
            ".tocl2 {\n" +
            "      margin-top: 0.5em;\n" +
            "      margin-left:-20px;\n" +
            "      border-style: none double none solid;\n" +
            "      border-width: 0px 2px 0px 10px;\n" +
            "      border-color: #939E92;\n" +
            "      line-height: 123%;\n" +
            "      font-size: 120%;\n" +
            "}\n" +
            "\n" +
            ".tocl3 {\n" +
            "      margin-top: 0.5em;\n" +
            "      margin-left:-20px;\n" +
            "      border-style: none double none solid;\n" +
            "      border-width: 0px 2px 0px 8px;\n" +
            "      border-color: #939E92;\n" +
            "      line-height: 112%;\n" +
            "      font-size: 109%;\n" +
            "}\n" +
            "\n" +
            ".tocl4 {\n" +
            "      margin-top: 0.5em;\n" +
            "      margin-left:-20px;\n" +
            "      border-style: none double none solid;\n" +
            "      border-width: 0px 2px 0px 6px;\n" +
            "      border-color: #939E92;\n" +
            "      line-height: 115%;\n" +
            "      font-size: 110%;\n" +
            "}\n" +
            "\n" +
            ".subtoc {\n" +
            "      margin-left:15%;\n" +
            "      padding:0px;\n" +
            "      text-align: justify;\n" +
            "}\n" +
            "\n" +
            ".subtoclist {\n" +
            "      margin-top: 0.5em;\n" +
            "      margin-left:-20px;\n" +
            "      border-style: none double none solid;\n" +
            "      border-width: 0px 2px 0px 10px;\n" +
            "      border-color: #939E92;\n" +
            "      line-height: 123%;\n" +
            "      font-size: 120%;\n" +
            "}\n" +
            "\n";
    public static String TOC = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n" +
            "<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\">\n" +
            "<ncx xmlns=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\">\n" +
            "<head>\n" +
            "<meta name=\"cover\" content=\"cover\"/>\n" +
            "<meta name=\"dtb:uid\" content=\"easypub-c260e840\" />\n" +
            "<meta name=\"dtb:depth\" content=\"1\"/>\n" +
            "<meta name=\"dtb:generator\" content=\"EasyPub v1.50\"/>\n" +
            "<meta name=\"dtb:totalPageCount\" content=\"0\"/>\n" +
            "<meta name=\"dtb:maxPageNumber\" content=\"0\"/>\n" +
            "</head>\n" +
            "\n" +
            "<docTitle>\n" +
            "<text>toreplace0</text>\n" +
            "</docTitle>\n" +
            "<docAuthor>\n" +
            "<text>toreplace1</text>\n" +
            "</docAuthor>\n" +
            "\n" +
            "toreplace2" +
            "</ncx>\n";

    public static String COVERHTML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\">\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml; charset=utf-8\" />\n" +
            "<meta name=\"generator\" content=\"EasyPub v1.50\" />\n" +
            "<title>\n" +
            "Cover\n" +
            "</title>\n" +
            "<style type=\"text/css\">\n" +
            "html,body {height:100%; margin:0; padding:0;}\n" +
            ".wedge {float:left; height:50%; margin-bottom: -359px;}\n" +
            ".container {clear:both; height:0em; position: relative;}\n" +
            "table, tr, th {height: 719px; width:100%; text-align:center;}\n" +
            "</style>\n" +
            "<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\"/>\n" +
            "</head>\n" +
            "<body>\n" +
            "<div class=\"wedge\"></div>\n" +
            "<div class=\"container\">\n" +
            "<table><tr><td>\n" +
            "<img src=\"cover.jpg\" alt=\"toreplace0\" />\n" +
            "</td></tr></table>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";

    public static String ELE_DT_A = "<dt class=\"tocl2\"><a href=\"chapter.html\">Table</a></dt>";

    public static String ELE_MANIFEST = "<manifest>\n" +
            "<item id=\"ncxtoc\" href=\"toc.ncx\" media-type=\"application/x-dtbncx+xml\"/>\n" +
            "<item id=\"htmltoc\"  href=\"book-toc.html\" media-type=\"application/xhtml+xml\"/>\n" +
            "<item id=\"css\" href=\"style.css\" media-type=\"text/css\"/>\n" +
            "<item id=\"cover-image\" href=\"cover.jpg\" media-type=\"image/jpeg\"/>\n" +
            "<item id=\"cover\" href=\"cover.html\" media-type=\"application/xhtml+xml\"/>\n" +
            "</manifest>";

    public static String ELE_SPINE = "<spine toc=\"ncxtoc\">\n" +
            "<itemref idref=\"cover\" linear=\"no\"/>\n" +
            "<itemref idref=\"htmltoc\" linear=\"yes\"/>\n" +
            "</spine>";

    public static String ELE_NAV_MAP = "<navMap>\n" +
            "<navPoint id=\"cover\" playOrder=\"POD\">\n" +
            "<navLabel><text>THETEXT</text></navLabel>\n" +
            "<content src=\"THEHTML\"/>\n" +
            "</navPoint>\n";

    public static String ELE_CHAPTER_BODY = "<body>\n" +
            "<h2 id=\"title\" class=\"titlel2std\"></h2>\n" +
            "<p class=\"a\"></p>\n" +
            "</body>";

    private String bookName;
    private String author;
    private String epubCachePath;
    private List<ExportChapter> exportArray;
    private Context mContext;
    private String coverUrl;

    public EpubUtil(String bookName, String author, String coverUrl, String epubCachePath, List<ExportChapter> exportArray, Context mContext) {
        this.bookName = bookName;
        this.author = author;
        this.epubCachePath = epubCachePath;
        this.exportArray = exportArray;
        this.mContext = mContext;
        this.coverUrl = coverUrl;
    }

    public boolean build() {
        if (!new File(epubCachePath + "/META-INF").mkdirs()) return false;
        if (!new File(epubCachePath + "/OEBPS").mkdirs()) return false;
        if (!writeFile(epubCachePath + "/mimetype", MIMETYPE)) return false;
        if (!writeFile(epubCachePath + "/META-INF/container.xml", CONTAINER)) return false;
        if (!writeFile(epubCachePath + "/OEBPS/style.css", STYLE)) return false;
        if (!generateTocNcx(bookName, author, exportArray, epubCachePath)) return false;
        if (!generateContentOpf(bookName, author, epubCachePath, exportArray.size())) return false;
        if (!generateBookToc(exportArray, epubCachePath)) return false;
        if (!generateCoverHtml(bookName, epubCachePath)) return false;
        Drawable drawable = null;
        try {
            drawable = Glide.with(mContext)
                    .load(coverUrl)
                    .submit()
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return saveBitmap(drawableToBitmap2(drawable), epubCachePath + "/OEBPS/cover.jpg");
    }

    private static Bitmap drawableToBitmap2(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }


    private static boolean saveBitmap(Bitmap bitmap, String path) {

        File file = new File(path);
        if(file.exists()) {
            if(!file.delete()) return false;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ((OutputStream)fileOutputStream));//设置PNG的话，透明区域不会变成黑色
            fileOutputStream.close();
            System.out.println("----------save success-------------------");
            return true;
        }
        catch(Exception v0) {
            v0.printStackTrace();
            return false;
        }

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean writeFile(String path, String str) {
        File file = new File(path);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(str);
            bufferedWriter.flush();
            bufferedWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean generateTocNcx(String bookName, String bookAuthor, List<ExportChapter> exportArray, String cachePath) {
        String fileText = TOC;
        {
            StringBuilder d = new StringBuilder();
            String e = ELE_NAV_MAP;
            d.append(e.replace("POD", "1").replace("THETEXT", " 封面 ").replace("THEHTML", "cover.html"));
            d.append(e.replace("POD", "2").replace("THETEXT", " 目录 ").replace("THEHTML", "book-toc.html"));
            int playorder = 3;
            int chapter = 0;
            for (int i = 0; i < exportArray.size(); i++) {
                d.append(e.replace("POD", "" + playorder).replace("THETEXT", exportArray.get(i).getChapterInfo()).replace("THEHTML", "chapter" + chapter + ".html"));
                playorder++;
                chapter++;
            }
            fileText = fileText.replace("toreplace0", bookName);
            fileText = fileText.replace("toreplace1", bookAuthor);
            fileText = fileText.replace("toreplace2", d);
            return writeFile(cachePath + "/OEBPS/toc.ncx", fileText);
        }
    }

    public static boolean generateContentOpf(String bookName, String bookAuthor, String cachePath, int chapterNums) {
        String fileText = CONTENT;
        Element element_manifest = Jsoup.parse(ELE_MANIFEST).selectFirst("manifest");
        Element element_spine = Jsoup.parse(ELE_SPINE).selectFirst("spine");
        Element d = element_manifest.select("item").get(4).clone();
        Element e = element_spine.select("itemref").get(1).clone();
        int chapter = 0;
        for (int i = 0; i < chapterNums; i++) {
            d.selectFirst("item").attr("id", "chapter" + chapter);
            d.selectFirst("item").attr("href", "chapter" + chapter + ".html");
            e.selectFirst("itemref").attr("idref", "chapter" + chapter);
            element_manifest.append(d.toString());
            element_spine.append(e.toString());
            chapter++;
        }
        fileText = fileText.replace("toreplace0", bookName);
        fileText = fileText.replace("toreplace1", bookAuthor);
        fileText = fileText.replace("toreplace2", element_manifest.toString());
        fileText = fileText.replace("toreplace3", element_spine.toString());
        return writeFile(cachePath + "/OEBPS/content.opf", fileText);
    }

    public static boolean generateBookToc(List<ExportChapter> exportArray, String cachePath) {
        String fileText = BOOKTOC;
        StringBuilder tmp = new StringBuilder();
        int chapter = 0;
        for (int i = 0; i < exportArray.size(); i++) {
            tmp.append(ELE_DT_A.replace("chapter.html", "chapter" + chapter).replace("Table", exportArray.get(i).getChapterInfo())).append("\n");
            chapter++;
        }
        fileText = fileText.replace("toreplace0", tmp.toString());
        return writeFile(cachePath + "/OEBPS/book-toc.html", fileText);
    }

    public static boolean generateCoverHtml(String bookName, String cachePath) {
        return writeFile(cachePath + "/OEBPS/cover.html", COVERHTML.replace("toreplace0", bookName));
    }
}
