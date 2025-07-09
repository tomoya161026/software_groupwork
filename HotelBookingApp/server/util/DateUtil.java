package server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy/MM/dd");
    static { FORMATTER.setLenient(false); }

    public static String convertToString(Date date) {
        return (date == null) ? "" : FORMATTER.format(date);
    }
    public static Date convertToDate(String dateStr) {
        try {
            return (dateStr != null && !dateStr.isEmpty()) ? FORMATTER.parse(dateStr) : null;
        } catch (ParseException e) {
            return null;
        }
    }
    public static long calculateDaysBetween(Date d1, Date d2) {
        if (d1 == null || d2 == null) return 0;
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}