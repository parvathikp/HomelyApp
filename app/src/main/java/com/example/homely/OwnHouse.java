package com.example.homely;

public class OwnHouse {
    int hid;
    int oid;
    String place,addr,desc,saleType,type;
    Double sqft;
    int bedr,price;
    OwnHouse(int hid,int oid,String addr,String descr,String st,String t,String place,Double sqft,int bedr,int price){
        this.hid=hid;
        this.oid=oid;
        this.addr=addr;
        this.desc=descr;
        this.saleType=st;
        this.type=t;
        this.place=place;
        this.sqft=sqft;
        this.bedr=bedr;
        this.price=price;
    }
    public String getPlace(){
        return place;
    }
    public  String getAddr(){
        return addr;
    }

    public int getHid() {
        return hid;
    }

    public Double getSqft() {
        return sqft;
    }

    public int getBedr() {
        return bedr;
    }

    public int getOid() {
        return oid;
    }

    public String getDesc() {
        return desc;
    }

    public String getSaleType() {
        return saleType;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSqft(Double sqft) {
        this.sqft = sqft;
    }

    public void setBedr(int bedr) {
        this.bedr = bedr;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    //add sqft
   // String
}
