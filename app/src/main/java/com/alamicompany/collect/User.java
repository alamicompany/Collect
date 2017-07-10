package com.alamicompany.collect;

/**
 * Created by AC04 on 20.03.17.
 */

public class User {

    private String id;
    private String name;
    private String photo;
    private String slogan;

    public User(){}

    public User(String id, String name,String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public User(String id,String name) {
        this.name = name;
        this.id = id;
    }

    public User(String id, String name, String photo, String slogan) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.slogan = slogan;
    }

    public User(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }
}
