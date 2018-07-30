package com.hebaiyi.www.katakuri.bean;

import android.util.Log;

public class Folder {

    private String dir;
    private String folderName;
    private int imageNum;
    private String firstImagePath;

    public Folder() {

    }

    public Folder(String folderName, int imageNum, String firstImagePath) {
        this.folderName = folderName;
        this.imageNum = imageNum;
        this.firstImagePath = firstImagePath;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/");
        this.folderName = this.dir.substring(lastIndexOf + 1);
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
