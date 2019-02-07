package cc.zsakvo.yueduhchelper.bean;

import java.io.Serializable;

public class CacheBooks implements Serializable {
    private String name;

    public int getCacheNum() {
        return cacheNum;
    }

    public void setCacheNum(int cacheNum) {
        this.cacheNum = cacheNum;
    }

    private int cacheNum;

    public String getCacheInfo() {
        return cacheInfo;
    }

    public void setCacheInfo(String cacheInfo) {
        this.cacheInfo = cacheInfo;
    }

    private String cacheInfo;

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    private String cachePath;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
