package com.example.nozoeli.newsnote.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by nozoeli on 16-4-23.
 */
public class NoteBean extends DataSupport {

    private String date;
    private String title;
    private String content;

    public NoteBean() {

    }

    public NoteBean(String title, String content, String date) {
        this.date = date;
        this.title = title;
        this.content = content;
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
