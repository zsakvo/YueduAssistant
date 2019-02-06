package cc.zsakvo.yueduhchelper.bean;

import java.io.Serializable;
import java.util.List;

public class CacheBooks implements Serializable {
    private String name;
    private String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCacheInfo() {
        return cacheInfo;
    }

    public void setCacheInfo(String cacheInfo) {
        this.cacheInfo = cacheInfo;
    }

    private String source;
    private String cacheInfo;

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    private String cachePath;
    private int allBookChapters;
    private List<String> chapterNum;
    private List<String> bookSources;

    public List<String> getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(List<String> chapterNum) {
        this.chapterNum = chapterNum;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAllBookChapters() {
        return allBookChapters;
    }

    public void setAllBookChapters(int allBookChapters) {
        this.allBookChapters = allBookChapters;
    }


    public List<String> getBookSources() {
        return bookSources;
    }

    public void setBookSources(List<String> bookSources) {
        this.bookSources = bookSources;
    }

}
