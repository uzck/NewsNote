package com.example.nozoeli.newsnote.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by nozoeli on 16-4-13.
 */
public class NewsDetailBean {
    private String title;
    private String content;
    private String publishTime;
    private String publishOrg;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishOrg() {
        return publishOrg;
    }

    public void setPublishOrg(String publishOrg) {
        this.publishOrg = publishOrg;
    }
}
