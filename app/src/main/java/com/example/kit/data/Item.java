package com.example.kit.data;

import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Item {
    private String name;
    private Timestamp acquisitionDate;
    private String description;
    private String comment;
    private BigDecimal value;
    private String make;
    private String model;
    private String serialNumber;
    private ArrayList<String> tags;

    // TODO: Images

    public Item() {

    }

    public Item (String name) {
        this.name = name;
        this.acquisitionDate = null;
        this.description = "";
        this.comment = "";
        this.make = "";
        this.model = "";
        this.serialNumber = "";
        this.tags = new ArrayList<>();
    }

//    public Item(String name, Timestamp acquisitionDate, String description, String comment, BigDecimal value, String make, String model, String serialNumber, ArrayList<Tag> tags) {
//        this.name = name;
//        this.acquisitionDate = acquisitionDate;
//        this.description = description;
//        this.comment = comment;
//        this.value = value;
//        this.make = make;
//        this.model = model;
//        this.serialNumber = serialNumber;
//        this.tags = tags;
//    }

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

    public BigDecimal getValueBigDecimal() {
        return value;
    }

    public void setValueBigDecimal(BigDecimal value) {
        this.value = value;
    }

    public String getValue() {
        return value.toString();
    }

    public void setValue(String value) {
        this.value = new BigDecimal(value);
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

//    public void addTag(Tag tag) {
//        if (!tags.contains(tag)) {
//            tags.add(tag);
//        }
//    }

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
}
