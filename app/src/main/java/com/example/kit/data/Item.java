package com.example.kit.data;

import android.media.Image;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.NoSuchElementException;


public class Item implements Serializable {
    // Todo: May use Tag data type for tags, for now, use just strings
    private String id;
    private String name;
    private Timestamp acquisitionDate;
    private String description;
    private String comment;
    private String value;
    private String make;
    private String model;
    private String serialNumber;
    private ArrayList<String> tags;
    private ArrayList<Image> images; // Placeholder, may store locally with something like a path to the directory?
                                    // Looking into FireStore and images, we can use a cloud solution and then have
                                    // FireStore manage it (kinda)

    // TODO: Images

    public Item() {
        tags = new ArrayList<>();
    }

    public Item (String name) {
        this.name = name;
        this.id = "";
        this.acquisitionDate = null;
        this.description = "";
        this.comment = "";
        this.make = "";
        this.model = "";
        this.serialNumber = "";
        this.tags = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(Timestamp acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigDecimal valueToBigDecimal() {
        return new BigDecimal(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public String findId() {return id; }

    public void attachID(String id) {this.id = id; }

    /**
     * Removes the provided tag from the Item.
     * @param tag
     *  Tag to be removed from the item.
     * @throws NoSuchElementException
     *  Exception thrown if the tag was not present on the item.
     */
    public void removeTag(Tag tag) {
        if (!tags.remove(tag)) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean equals(Object item){
        if(this.findId() == item.toString()){
            return true;
        } else {
            return false;
        }
    }
    @Override
    public String toString(){
        return this.findId();
    }
}
