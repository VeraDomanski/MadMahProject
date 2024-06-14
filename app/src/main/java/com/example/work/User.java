package com.example.work;

public class User {

    private String name;
    private String phoneNum;
    private String mail;
    private String pass;
    private String Role;

    public User(){

    }



    public User(String name, String phoneNum, String mail, String pass, String Role){

      this.phoneNum=phoneNum;
      this.pass=pass;
      this.mail=mail;
      this.name=name;
      this.Role=Role;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    public String getRole() {
        return Role;
    }

    public void setRole(String Role) {
        this.Role = Role;
    }
}
