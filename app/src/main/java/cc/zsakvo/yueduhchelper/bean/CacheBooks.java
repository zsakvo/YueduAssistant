package cc.zsakvo.yueduhchelper.bean;

import java.io.Serializable;
import java.util.List;

public class CacheBooks implements Serializable {
    private String name;
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
