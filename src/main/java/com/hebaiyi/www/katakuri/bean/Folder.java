package com.hebaiyi.www.katakuri.bean;

import java.util.List;

public class Folder {

    private String folderName;
    private int imageNum;
    private String firstImagePath;

    public Folder(){

    }

    public Folder(String folderName, int imageNum, String firstImagePath) {
        this.folderName = folderName;
        this.imageNum = imageNum;
        this.firstImagePath = firstImagePath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

}
