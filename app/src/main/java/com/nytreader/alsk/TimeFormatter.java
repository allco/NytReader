package com.nytreader.alsk;

import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;

public class TimeFormatter {

    private final DateFormat fortmatExpected = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
    private final DateFormat fortmatTo = SimpleDateFormat.getDateTimeInstance();

    @Nullable
    public String formatDate(@Nullable String input) {
        if (!isEmpty(input)) {
            try {
                return fortmatTo.format(fortmatExpected.parse(input));
            } catch (ParseException e) {
                // lets do not flood the log with
                //e.printStackTrace();
            }
        }
        return input;
    }
}
