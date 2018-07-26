package com.hebaiyi.www.katakuri.bean;

public class Folder {

    private String folderName;
    private int imageNum;
    private String firstImageUri;

    public Folder(){

    }

    public Folder(String folderName, int imageNum, String firstImageUri) {
        this.folderName = folderName;
        this.imageNum = imageNum;
        this.firstImageUri = firstImageUri;
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

    public String getFirstImageUri() {
        return firstImageUri;
    }

    public void setFirstImageUri(String firstImageUri) {
        this.firstImageUri = firstImageUri;
    }
}
