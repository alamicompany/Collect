package com.alamicompany.collect;

/**
 * Created by AC04 on 21.03.17.
 */

public class Project {
    private String id;
    private String name;
    private int imagePhoto;



    public Project() {

    }

    public Project(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Project(String id, String name, int imagePhoto) {
        this.id = id;
        this.name = name;
        this.imagePhoto = imagePhoto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImagePhoto() {
        return imagePhoto;
    }

    public void setImagePhoto(int imagePhoto) {
        this.imagePhoto = imagePhoto;
    }
}
