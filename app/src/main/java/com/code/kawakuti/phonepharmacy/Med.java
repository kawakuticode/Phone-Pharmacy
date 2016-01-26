package com.code.kawakuti.phonepharmacy;


import java.util.Date;

;

/**
 * Created by Russelius on 26/01/16.
 */
public class Med {

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
}
