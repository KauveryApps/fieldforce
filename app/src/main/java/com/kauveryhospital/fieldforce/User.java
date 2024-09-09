package com.kauveryhospital.fieldforce;

public class User {
    private int id;
    private String name, email, gender,password,is_status;

    public User(String name, String password, String is_status) {

        this.name = name;
        this.password=password;
        this.is_status= this.is_status;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

public  String getPassword(){
        return password;
}
public  void setPassword(String password){
        this.password=password;
}

public String getIs_status(){
        return  is_status;
}
public void setIs_status(String is_status){
        this.is_status=is_status;
}
}
