package wrappers;

import java.util.Calendar;

public class DateTimeWrapper {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int seconds;

    public DateTimeWrapper() {
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        seconds = cal.get(Calendar.SECOND);
    }

    public DateTimeWrapper(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        hour = -1;
    }

    public String sqlReady() {
        String date = year + "-" + makeTen(month) + "-" + makeTen(day);
        if (hour < 0) {
            return date;
        } else {
            return date + " " + makeTen(hour) + ":" + makeTen(minute) + ":" + makeTen(seconds);
        }
    }

    private String makeTen(int value) {
        if (value < 10) {
            return "0" + String.valueOf(value);
        } else {
            return String.valueOf(value);
        }

    }
}
