package es.ucm.fdi.moodcalendar.customView;

import es.ucm.fdi.moodcalendar.R;

/**
 * Class which acts a a container of the UI data that any CalendarAdapter will display
 * inside the CalendarView object in which it is attached to
 *
 * @see CalendarView
 * @see CalendarAdapter
 *
 * @author Alejandro Cancelo Correia
 * */
class CalendarViewItem {
    /**
     * Year if the cell that the CalendarAdapter will hold
     * */
    private int year;
    /**
     * Month if the cell that the CalendarAdapter will hold
     * */
    private int month;
    /**
     * Day if the cell that the CalendarAdapter will hold and display
     * */
    private int day;
    /**
     * Id of the color background displayed
     * */
    private int colorId;
    /**
     * Each cell displayed by CalendarAdapter has two states <i>"Marked"</i> and
     * <i>"Not Marked"</i> being the last the default state when creating this object.<br>
     * This object can transition to <i>"Marked"</i> when the user click a cell and insert
     * data.<br><br>
     * This value will tell in which state this object is (false -> Not Marked | true -> Marked)
     * */
    private boolean marked;

    /**
     * This class's constructor only needs the year, month and day of the date the resulting object
     * is representing
     * */
    CalendarViewItem(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        colorId = R.color.white;
        marked = false;
    }

    /**
     * @return true if this object is in <i>"Marked"</i> and false if not
     * */
    boolean isMarked() {
        return marked;
    }

    /**
     * Let you change the sate of this object.<br>
     *
     * @param marked It only accepts true (Mark this object)
     * */
    void setMarked(boolean marked) {
        if(marked) {
            this.marked = marked;
        }
    }

    /**
     * Get year
     * */
    int getYear() {
        return year;
    }

    /**
     * Get month
     * */
    int getMonth() {
        return month;
    }

    /**
     * Get day
     * */
    int getDay() {
        return day;
    }

    /**
     * Changes the day of this object
     *
     * @param day The we want this object to have.<br>
     *            Accepted values 0 <= day <= 31
     * */
    void setDay(int day) {
        if(day >= 0 && day <= 31){
            this.day = day;
        }
    }

    /**
     * Get the background color id
     * */
    int getColorId() {
        return colorId;
    }

    /**
     * Change the background color id
     * */
    void setColorId(int colorId) {
        this.colorId = colorId;
    }

    /**
     * It will return a string version of the date this object represent.<br>
     *
     * @return The string version is in the form of "YYYY-MM-DD"<br><br>
     *     <i>Example:</i> "2020-5-20"
     * */
    String stringify() {
        return year + "-" + month + "-" + day;
    }

}
