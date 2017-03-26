package wrappers;

public class DateTimeWrapper {
    private int year;
    private int month;
    private int day;

    public DateTimeWrapper(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String sqlReady() {
        return this.year + makeTen(this.month) + makeTen(this.day);
    }

    private String makeTen(int value) {
        if (value < 10) {
            return "0" + String.valueOf(value);
        } else {
            return String.valueOf(value);
        }

    }
}
