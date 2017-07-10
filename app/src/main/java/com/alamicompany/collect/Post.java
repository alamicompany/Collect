package com.alamicompany.collect;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by AC04 on 23.03.17.
 */

public class Post implements Parcelable{

    private String id;
    private String name;
    private String photo;
    private String date;
    private Boolean favorit;
    private String project_id;
    private String description;
    private String note;
    private Long storageId;

    public Post() {

    }





    public Post(String id, String name, String photo, String date, Boolean favorit, String project_id, String description) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.date = date;
        this.favorit = favorit;
        this.project_id = project_id;
        this.description = description;
    }

    public Post(String id, String name, String photo, String date, Boolean favorit, String project_id, String description, String note) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.date = date;
        this.favorit = favorit;
        this.project_id = project_id;
        this.description = description;
        this.note = note;
    }

    public Post(String id, String name, String photo, String date, Boolean favorit, String project_id, String description, String note, Long storageId) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.date = date;
        this.favorit = favorit;
        this.project_id = project_id;
        this.description = description;
        this.note = note;
        this.storageId = storageId;
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



    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getFavorit() {
        return favorit;
    }

    public void setFavorit(Boolean favorit) {
        this.favorit = favorit;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(photo);
        dest.writeString(date);
        dest.writeByte((byte) (favorit ? 1 : 0));
        dest.writeString(project_id);
        dest.writeString(note);
        dest.writeLong(storageId);



    }
    public Post(Parcel in) {
        id = in.readString();
        name = in.readString();
        photo = in.readString();
        date = in.readString();
        favorit = in.readByte() != 0;
        project_id = in.readString();
        note = in.readString();
        storageId = in.readLong();



    }
    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };



}
