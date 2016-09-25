package com.example.nozoeli.newsnote.bean;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by nozoeli on 16-4-13.
 */
public class NewsListBean  {

    private ArrayList<String> titleList;
    private ArrayList<String> digestList;
    private ArrayList<String> articleImgList;
    private ArrayList<String> concreteUrl;
    private ArrayList<ImgExtra> imgExtraList; // 新闻与Banner的JSON数据没有imgExtra，用null填充
    private ArrayList<String> isAlbumFlag;  // 区分图集与新闻
    private ArrayList<String> photoSetIDList;
    private ArrayList<String> docIdList;
    private ArrayList<String> stitleList;

    public NewsListBean() {
        titleList = new ArrayList<>();
        digestList = new ArrayList<>();
        articleImgList = new ArrayList<>();
        concreteUrl = new ArrayList<>();
        imgExtraList = new ArrayList<>();
        isAlbumFlag = new ArrayList<>();
        photoSetIDList = new ArrayList<>();
        docIdList = new ArrayList<>();
        stitleList = new ArrayList<>();
    }

    public ArrayList<String> getTitleList() {
        return titleList;
    }

    public void setTitleList(ArrayList<String> titleList) {
        this.titleList = titleList;
    }

    public ArrayList<String> getDigestList() {
        return digestList;
    }

    public void setDigestList(ArrayList<String> digestList) {
        this.digestList = digestList;
    }

    public ArrayList<String> getArticleImgList() {
        return articleImgList;
    }

    public void setArticleImgList(ArrayList<String> articleImgList) {
        this.articleImgList = articleImgList;
    }

    public ArrayList<String> getConcreteUrl() {
        return concreteUrl;
    }

    public void setConcreteUrl(ArrayList<String> concreteUrl) {
        this.concreteUrl = concreteUrl;
    }

    public ArrayList<ImgExtra> getImgExtraList() {
        return imgExtraList;
    }

    public void setImgExtraList(ArrayList<ImgExtra> imgExtraList) {
        this.imgExtraList = imgExtraList;
    }

    public ArrayList<String> getIsAlbumFlag() {
        return isAlbumFlag;
    }

    public void setIsAlbumFlag(ArrayList<String> isAlbumFlag) {
        this.isAlbumFlag = isAlbumFlag;
    }

    public ArrayList<String> getPhotoSetIDList() {
        return photoSetIDList;
    }

    public void setPhotoSetIDList(ArrayList<String> photoSetIDList) {
        this.photoSetIDList = photoSetIDList;
    }

    public ArrayList<String> getDocIdList() {
        return docIdList;
    }

    public void setDocIdList(ArrayList<String> docIdList) {
        this.docIdList = docIdList;
    }

    public ArrayList<String> getStitleList() {
        return stitleList;
    }

    public void setStitleList(ArrayList<String> stitleList) {
        this.stitleList = stitleList;
    }

}
