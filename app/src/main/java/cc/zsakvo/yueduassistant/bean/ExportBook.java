package cc.zsakvo.yueduassistant.bean;

import android.content.Context;

import java.util.List;

public class ExportBook {
    private  String bookPath;
    private  String outputDirPath;
    private String fileName;
    private  List<CacheChapter> cacheChapters;
    private  List<Boolean> flags;

    public String getOutputDirPath() {
        return outputDirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getBookPath() {
        return bookPath;
    }


    public List<CacheChapter> getCacheChapters() {
        return cacheChapters;
    }

    public List<Boolean> getFlags() {
        return flags;
    }

    public Context getmContext() {
        return mContext;
    }

    private  Context mContext;

    // 私有构造器，因此Person对象的创建必须依赖于Builder
    private ExportBook(Builder builder) {
        this.bookPath = builder.bookPath;
        this.outputDirPath = builder.outputDirPath;
        this.fileName = builder.fileName;
        this.cacheChapters = builder.cacheChapters;
        this.flags = builder.flags;
        this.mContext = builder.mContext;
    }

    public static class Builder{

        private  String bookPath;
        private  String outputDirPath;
        private  String fileName;
        private  List<CacheChapter> cacheChapters;
        private  List<Boolean> flags;
        private  Context mContext;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }
        
        public Builder bookPath(String bookPath){
            this.bookPath = bookPath;
            return this;
        }

        
        public Builder outputDirPath(String outputDirPath){
            this.outputDirPath = outputDirPath;
            return this;
        }

        public Builder fileName(String fileName){
            this.fileName = fileName;
            return this;
        }

        
        public Builder cacheChapters(List<CacheChapter> cacheChapters){
            this.cacheChapters = cacheChapters;
            return this;
        }

        
        public Builder flags( List<Boolean> flags){
            this.flags = flags;
            return this;
        }

        public ExportBook build(){
            return new ExportBook(this);
        }
    }
}
