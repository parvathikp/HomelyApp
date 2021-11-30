package com.example.homely;

public class houseImg {
    String imgurl;int hid,id;
    houseImg(int id,int hid,String imgurl){
        this.imgurl=imgurl;this.hid=hid;this.id=id;
    }
    public String getImgurl() {
        return imgurl;
    }

    public int getHid() {
        return hid;
    }


    public int getId() {
        return id;
    }
    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
