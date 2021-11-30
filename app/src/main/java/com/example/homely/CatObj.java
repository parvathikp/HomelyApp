package com.example.homely;

public class CatObj {
    private int img;
    private String title;
    public CatObj(int img,String title){
        this.img=img;
        this.title=title;
    }
    public int getImg(){
        return img;
    }
    public String getTitle(){
        return title;
    }
    public void setImg(int id){
        this.img=id;
    }
    public void setTitle(String s){
        this.title=s;
    }
}
