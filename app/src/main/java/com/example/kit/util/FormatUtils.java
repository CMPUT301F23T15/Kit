package com.example.kit.util;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    private static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
    private static final SimpleDateFormat DATE_FORMAT_LONG = new SimpleDateFormat("MMM d, yyyy", Locale.CANADA);
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.CANADA);

    public static String formatDateStringShort(Date date) {
        return DATE_FORMAT_SHORT.format(date);
    }

    public static String formatDateStringLong(Date date) {
        return DATE_FORMAT_LONG.format(date);
    }

    public static String formatDateStringShort(Timestamp timestamp) {
        return DATE_FORMAT_SHORT.format(timestamp.toDate());
    }

    public static String formatDateStringLong(Timestamp timestamp) {
        return DATE_FORMAT_LONG.format(timestamp.toDate());
    }

    public static Timestamp parseDateString(String dateString) {
        Date date;
        try {
            date = DateFormat.getDateInstance(DateFormat.SHORT).parse(dateString);
        } catch (ParseException e) {
            return null;
        }
        if (date == null) return null;

        return new Timestamp(date);
    }

    public static String formatValue(BigDecimal value, boolean withSymbol) {
        if (withSymbol) return CURRENCY_FORMAT.format(value);
        // Currency Format adds "$" automatically, sometimes this is not desired.
        return CURRENCY_FORMAT.format(value).substring(1);
    }

    public static String formatValue(String value, boolean withSymbol) {
        String cleanValue = cleanupDirtyValueString(value);
        return formatValue(new BigDecimal(cleanValue), withSymbol);
    }

    public static String cleanupDirtyValueString(String dirtyValue) {
        String cleanValue = dirtyValue.replaceAll("[^\\d.]", "");
        if (cleanValue.isEmpty()) return "0";
        return cleanValue;
    }


}
