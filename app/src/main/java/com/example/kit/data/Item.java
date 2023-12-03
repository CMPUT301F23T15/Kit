package com.example.kit.data;

import android.media.Image;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Item is a single item data type, it is a serializable data type
 */

public class Item implements Serializable {
    private String id;
    private String name;
    private Timestamp acquisitionDate;
    private String description;
    private String comment;
    private String value;
    private String make;
    private String model;
    private String serialNumber;
    private final ArrayList<Tag> tags;
    private ArrayList<String> base64Images;

    /**
     * Constructs an Item with an empty list of {@link Tag}s.
     */
    public Item() {
        tags = new ArrayList<>();
        base64Images = new ArrayList<>();
    }

    /**
     * Gets the name of the Item.
     * @return The name of the Item.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the Item.
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the acquisition date of the Item.
     * @return The acquisition date of the Item.
     */
    public Timestamp getAcquisitionDate() {
        return acquisitionDate;
    }

    /**
     * Sets the acquisition date of the Item.
     * @param acquisitionDate The acquisition date to set.
     */
    public void setAcquisitionDate(Timestamp acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    /**
     * Gets the description of the Item.
     * @return The description of the Item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the Item.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the comment on the Item.
     * @return The comment on the Item.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment on the Item.
     * @param comment The comment to set.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the value of the Item as a {@link BigDecimal}.
     * @return The BigDecimal value of the Item.
     */
    public BigDecimal valueToBigDecimal() {
        return new BigDecimal(value);
    }

    /**
     * Gets the value of the Item as a String.
     * @return The value of the Item.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the Item from a String.
     * @param value The value String to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the Make of the Item.
     * @return The Make of the Item.
     */
    public String getMake() {
        return make;
    }

    /**
     * Sets the Make of the Item.
     * @param make The Make to set.
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Gets the Model of the Item.
     * @return The model of the Item.
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the Model of the Item.
     * @param model The Model to set.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the serial number of the Item.
     * @return The serial number of the Item.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the Item.
     * @param serialNumber The serial number to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the list of {@link Tag}s on the Item.
     * @return The list of Tags from the Item.
     */
    public ArrayList<Tag> getTags() {
        return tags;
    }

    /**
     * Add a {@link Tag} to the Item.
     * @param tag The Tag to add.
     */
    public void addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    /**
     * Add a {@link android.util.Base64} string that represents an image to this item
     * @param base64 A Base64 string for an image.
     */
    public void addBase64ImageString(String base64) {
        if (!base64Images.contains(base64)) {
            base64Images.add(base64);
        }
    }

    /**
     * Retrieve all the {@link android.util.Base64} string representation of the images for this item
     * @return The list of Base64 strings
     */
    public ArrayList<String> getBase64Images() {
        return base64Images;
    }

    /**
     * Set a list of {@link android.util.Base64} string representations of images for this item.
     * @param base64Images The list of Base64 strings.
     */
    public void setBase64Images (ArrayList<String> base64Images) {
        this.base64Images = base64Images;
    }

    /**
     * Gets the ID of the Item.
     * @return The ID of the Item.
     */
    public String findID() {
        return id;
    }

    /**
     * Sets the ID of the Item.
     * @param id The ID to set.
     */
    public void attachID(String id) {
        this.id = id;
    }

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

    /**
     * Equality comparison of two objects, false if it is not an Item, or true if it is an Item
     * and their ID's match.
     * @param obj Object to test equality.
     * @return The equality of the other Item's ID to this Item's ID.
     */
    @Override
    public boolean equals(Object obj){
        // If compared to another Item, check their ID's to see if they are the same Item
        if (obj instanceof Item) {
            Item otherItem = (Item) obj;
            return this.findID().equals(otherItem.findID());
        }

        return false;
    }

    /**
     * String representation of an item is it's ID
     * @return The ID of the this item.
     */
    @Override
    public String toString(){
        return this.findID();
    }
}
