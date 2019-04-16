package cc.zsakvo.yueduassistant.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CacheBook implements Serializable {

    private String name;
    private String source;

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
