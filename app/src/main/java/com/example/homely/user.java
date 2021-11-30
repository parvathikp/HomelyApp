package com.example.homely;

public class user {
    private int ID;
    private String uname,name,phone,emailID,password;
    public user(int id,String uname,String name,String phone,String emailID,String password){
        this.ID=id;
        this.uname=uname;
        this.name=name;
        this.emailID=emailID;
        this.password=password;
        this.phone=phone;
    }
    public int getID(){
        return this.ID;
    }
    public String getUname(){
        return this.uname;
    }
    public  String getName(){
        return  this.name;
    }
    public String getPhone(){
        return this.phone;
    }
    public String getEmailID(){
        return this.emailID;
    }
    public String getPassword(){
        return this.password;
    }
    public void setPassword(String s){
        this.password=s;
        return;
    }
}
