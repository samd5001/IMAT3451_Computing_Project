package wrappers;

import java.util.Calendar;

public class DateTimeWrapper {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public DateTimeWrapper() {
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
    }

    public DateTimeWrapper(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = -1;
    }

    public String sqlReady() {
        String date = this.year + "-" + makeTen(this.month) + "-" + makeTen(this.day);
        if (hour < 0) {
            return date;
        } else {
            return date + " " + makeTen(this.hour) + ":" + makeTen(this.minute);
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
