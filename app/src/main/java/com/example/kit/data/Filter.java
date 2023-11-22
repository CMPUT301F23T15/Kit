package com.example.kit.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;

/**
 * Class to encapsulate the settings of the Filter Sheet.
 * Keeps track of the sorting parameter and order to be sorted, as well as any filters for a date
 * range, value range,
 */
public class Filter {
    private EnumSet<SortFlags> sortFlags;
    private final ArrayList<Tag> tagFilters;
    private final ArrayList<String> makeFilters;
    private final ArrayList<String> keywords;
    private Date lowerDate;
    private Date higherDate;
    private BigDecimal lowerValue;
    private BigDecimal higherValue;


    public Filter() {
        tagFilters = new ArrayList<>();
        makeFilters = new ArrayList<>();
        keywords = new ArrayList<>();
    }

    public Filter setSortFlags(EnumSet<SortFlags> flags) {
        if (flags.size() != 2) {
            throw new IllegalArgumentException("Filter Error: Missing/too many sort parameters");
        }

        // If both flags are an order flag, there is an error.
        if (flags.contains(SortFlags.ASCENDING) && flags.contains(SortFlags.DESCENDING)) {
            throw new IllegalArgumentException("Filter Error: Multiple sorting orders defined");
        }

        // If neither flag is an order flag, there is an error.
        if (!(flags.contains(SortFlags.ASCENDING) || flags.contains(SortFlags.DESCENDING))) {
            throw new IllegalArgumentException("Filter Error: No sorter order defined");
        }

        this.sortFlags = flags;
        return this;
    }

    public Filter setLowerDate(Date date) {
        this.lowerDate = date;
        return this;
    }

    public Filter setHigherDate(Date date) {
        this.higherDate = date;
        return this;
    }

    public Filter setLowerValue(String value) {
        // TODO: format checking?
        this.lowerValue = new BigDecimal(value);
        return this;
    }

    public Filter setHigherValue(String value) {
        // TODO: format checking?
        this.higherValue = new BigDecimal(value);
        return this;
    }

    public Filter setTagsFilter(ArrayList<Tag> tagFilters) {
        this.tagFilters.clear();
        this.tagFilters.addAll(tagFilters);
        return this;
    }

    public Filter addTagFilter(Tag tag) {
        if (!tagFilters.contains(tag)) {
            tagFilters.add(tag);
        }

        return this;
    }

    public Filter setMakeFilters(ArrayList<String> makesFilter) {
        this.makeFilters.clear();
        this.makeFilters.addAll(makesFilter);
        return this;
    }

    public Filter addMakeFilter(String make) {
        if (!makeFilters.contains(make)) {
            makeFilters.add(make);
        }
        return this;
    }

    public Filter setKeywordFilter(ArrayList<String> keywords) {
        this.keywords.clear();
        this.keywords.addAll(keywords);
        return this;
    }

    public Filter addKeyword(String keyword) {
        if (!keywords.contains(keyword)) {
            keywords.add(keyword);
        }

        return this;
    }

    public EnumSet<SortFlags> getSortFlags() {
        return sortFlags;
    }

    public ArrayList<Tag> getTagFilters() {
        return tagFilters;
    }

    public ArrayList<String> getMakeFilters() {
        return makeFilters;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public Date getLowerDate() {
        return lowerDate;
    }

    public Date getHigherDate() {
        return higherDate;
    }

    public BigDecimal getLowerValue() {
        return lowerValue;
    }

    public BigDecimal getHigherValue() {
        return higherValue;
    }

    public enum SortFlags {
        ASCENDING, DESCENDING, DATE, VALUE, MAKE, DESCRIPTION, TAG;
    }
}
