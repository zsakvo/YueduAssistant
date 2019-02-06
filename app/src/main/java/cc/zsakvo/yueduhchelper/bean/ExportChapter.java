package cc.zsakvo.yueduhchelper.bean;


public class ExportChapter {

    private String chapterInfo;

    public void setChapterInfo(String chapterInfo) {
        this.chapterInfo = chapterInfo;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private boolean isChecked;

    public ExportChapter(String chapterInfo,boolean isChecked){
        this.chapterInfo = chapterInfo;
        this.isChecked = isChecked;
    }

    public String getChapterInfo() {
        return chapterInfo;
    }

    public boolean isChecked() {
        return isChecked;
    }

}
