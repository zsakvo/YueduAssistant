package cc.zsakvo.yueduassistant.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CacheBook implements Serializable {

    private String name;
    private String source;
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
