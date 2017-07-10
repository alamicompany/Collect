package com.alamicompany.collect;

/**
 * Created by AC04 on 31.05.17.
 */

public class HelpItem {

    int id;
    String name;
    int photo;

    public HelpItem(int id, String name, int photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}
