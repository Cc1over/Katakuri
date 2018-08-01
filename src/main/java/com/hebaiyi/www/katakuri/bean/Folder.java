package com.hebaiyi.www.katakuri.bean;

public class Folder {

    private String dir;
    private String folderName;
    private int imageNum;
    private String firstImagePath;

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
