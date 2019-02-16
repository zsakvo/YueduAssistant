package cc.zsakvo.yueduhchelper.bean;

import java.io.Serializable;
import java.util.List;

public class CacheBooks implements Serializable {
    private String name;
    private String cachePath;
    private String cacheInfo;
    private int cacheNum;
    private String sourcePath;

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }


    public int getCacheNum() { return cacheNum; }

    public void setCacheNum(int cacheNum) { this.cacheNum = cacheNum; }

    public String getCachePath() { return cachePath; }

    public String getCacheInfo() {
        return cacheInfo;
    }

    public void setCacheInfo(String cacheInfo) {
        this.cacheInfo = cacheInfo;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Epub 相关

    private String author;
    private String intro;
    private String coverUrl;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }



}
