package cc.zsakvo.yueduassistant.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Source extends LitePalSupport {
    @Column(nullable = false)
    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
