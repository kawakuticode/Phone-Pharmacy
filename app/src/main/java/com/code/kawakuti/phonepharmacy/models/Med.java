package com.code.kawakuti.phonepharmacy.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

;

/**
 * Created by Russelius on 26/01/16.
 */
public class Med implements  Parcelable {

    private int id;
    private String name;
    private String description;
    private Date expireDate;
    private String srcImage;

    public Med() {
    }

    public Med(int id, String name, String description, Date expireDate, String srcImage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.srcImage = srcImage;
        this.expireDate = expireDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSrcImage() {
        return srcImage;
    }

    public void setSrcImage(String srcImage) {
        this.srcImage = srcImage;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public String toString() {
        return "Med{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", expireDate=" + expireDate +
                ", srcImage='" + srcImage + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeLong(this.expireDate != null ? this.expireDate.getTime() : -1);
        dest.writeString(this.srcImage);
    }

    protected Med(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        long tmpExpireDate = in.readLong();
        this.expireDate = tmpExpireDate == -1 ? null : new Date(tmpExpireDate);
        this.srcImage = in.readString();
    }

    public static final Parcelable.Creator<Med> CREATOR = new Parcelable.Creator<Med>() {
        @Override
        public Med createFromParcel(Parcel source) {
            return new Med(source);
        }

        @Override
        public Med[] newArray(int size) {
            return new Med[size];
        }
    };
}
