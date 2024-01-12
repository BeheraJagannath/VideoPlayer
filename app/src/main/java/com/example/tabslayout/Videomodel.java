package com.example.tabslayout;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.tabslayout.Javaclass.Utils;

import java.util.ArrayList;

public class Videomodel implements Parcelable {

    public String id;
    public String path;
    public String title;
    public String size;
    public String resolution;
    public String duration;
    public String displayName;
    public String wh;
    public String mimeType;
    public String dateAdded;
    public ArrayList<String> pathList;
    public Utils Utils;

    public Videomodel(String id, String path, String title, String size, String resolution, String duration, String displayName, String wh
            , String s, String bucket_display_name, String width_height, String mimeType, String dateAdded ) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.size = size;
        this.resolution = (String) resolution;
        this.duration = duration;
        this.displayName = displayName;
        this.wh = wh;
        this.mimeType = mimeType;
        this.dateAdded = dateAdded;
    }

    public Videomodel(){}


    protected Videomodel(Parcel in) {
        id = in.readString();
        path = in.readString();
        title = in.readString();
        size = in.readString();
        resolution = in.readString();
        duration = in.readString();
        displayName = in.readString();
        wh = in.readString();
        mimeType = in.readString();
        dateAdded = in.readString();
    }

    public static final Creator<Videomodel> CREATOR = new Creator<Videomodel>() {
        @Override
        public Videomodel createFromParcel(Parcel in) {
            return new Videomodel(in);
        }

        @Override
        public Videomodel[] newArray(int size) {
            return new Videomodel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getWh() {
        return wh;
    }

    public void setWh(String wh) {
        this.wh = wh;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(size);
        dest.writeString(resolution);
        dest.writeString(duration);
        dest.writeString(displayName);
        dest.writeString(wh);
        dest.writeString(mimeType);

    }

    public ArrayList<String> getPathList() {
        return pathList;
    }
}