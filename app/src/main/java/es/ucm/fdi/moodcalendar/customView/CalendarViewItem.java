package es.ucm.fdi.moodcalendar.customView;

import android.graphics.Color;

import es.ucm.fdi.moodcalendar.R;

public class CalendarViewItem {
    private int year;
    private int month;
    private int day;
    private int colorId;

    public CalendarViewItem(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        colorId = R.color.white;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public String stringify() {
        return String.format("%d-%d-%d", year, month, day);
    }
}
