package com.example.kit.util;

import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class to provide common Date and Value conversions used throughout the app
 */
public class FormatUtils {
    private static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
    private static final SimpleDateFormat DATE_FORMAT_LONG = new SimpleDateFormat("MMM d, yyyy", Locale.CANADA);
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.CANADA);

    /**
     * Format a date to a short string of format
     * "dd/MM/yyyy"
     * @param date The date to be formatted
     * @return Formatted string
     */
    public static String formatDateStringShort(Date date) {
        return DATE_FORMAT_SHORT.format(date);
    }

    /**
     * Format a date to a long string of format
     * "MMM d, yyyy"
     * @param date The date to be formatted
     * @return Formatted string
     */
    public static String formatDateStringLong(Date date) {
        return DATE_FORMAT_LONG.format(date);
    }

    /**
     * Format a timestamp to a short string of format
     * "dd/MM/yyyy"
     * @param timestamp The date to be formatted
     * @return Formatted string
     */
    public static String formatDateStringShort(Timestamp timestamp) {
        return formatDateStringShort(timestamp.toDate());
    }

    /**
     * Format a timestamp to a long string of format
     * "MMM d, yyyy"
     * @param timestamp The date to be formatted
     * @return Formatted string
     */
    public static String formatDateStringLong(Timestamp timestamp) {
        return formatDateStringLong(timestamp.toDate());
    }

    /**
     * Parse a string and extract a date in the format "dd/MM/yyyy"
     * @param dateString The string to parse
     * @return A timestamp object, or null if there was no string in the correct format
     */
    public static Timestamp parseDateString(String dateString) {
        Date date;
        try {
            date = DATE_FORMAT_SHORT.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
        if (date == null) return null;

        return new Timestamp(date);
    }

    /**
     * Format a BigDecimal value into a string with the format "($)##,###.##" with the symbol optional
     * @param value The BigDecimal to format
     * @param withSymbol Flag to return with the currency symbol or not
     * @return The formatted string
     */
    public static String formatValue(BigDecimal value, boolean withSymbol) {
        String formatted = CURRENCY_FORMAT.format(value);
        if (withSymbol) return formatted;
        // Currency Format adds "$" automatically, sometimes this is not desired.
        return formatted.substring(1);
    }

    /**
     * Format a string representing a value into a string with the format "($)##,###.##" with the
     * symbol optional
     * @param value The string to format
     * @param withSymbol Flag to return with the currency symbol or not
     * @return The formatted string
     */
    public static String formatValue(String value, boolean withSymbol) {
        String cleanValue = cleanupDirtyValueString(value);
        return formatValue(new BigDecimal(cleanValue), withSymbol);
    }

    /**
     * Removes any non decimal characters from a string that represents a value. If the string contains
     * no numbers after the cleaning, returns 0;
     * @param dirtyValue A contaminated string with decimals and other characters
     * @return A decimal-only string from the input
     */
    public static String cleanupDirtyValueString(String dirtyValue) {
        String cleanValue = dirtyValue.replaceAll("[^\\d.]", "");
        if (cleanValue.isEmpty()) return "0";
        return cleanValue;
    }
}
