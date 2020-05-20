package es.ucm.fdi.moodcalendar.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observer;

import es.ucm.fdi.moodcalendar.R;
import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;

/**
 * Custom Calendar View.<br>
 *
 * @author Alejandro Cancelo Correia
 * */
public class CalendarView extends LinearLayout {
    private static final String TAG = "CalendarView";
    private final int MAX_ROWS = 6;
    private final int MAX_COLUMNS = 7;

    /**
     * TextView displaying the Year of the current calendar
     * */
    private TextView headerYearText;
    /**
     * TextView displaying the month of the current calendar
     * */
    private TextView headerMonthText;
    /**
     * Arrow left image representing a button to change to the previous month
     * */
    private ImageView btnPrev;
    /**
     * Arrow right image representing a button to change to the next month
     * */
    private ImageView btnNext;
    /**
     * Body of this calendar view, it will display the dates of the current month and year
     * @see GridView
     * */
    private GridView calendarBody;
    /**
     * Adapter needed to display the body of this view
     * */
    private CalendarAdapter bodyDataAdapter;
    /**
     * Calendar object to help us calculate the dates of the current month, the current month
     * and the current year
     * */
    private Calendar currentCalendar;
    private String[] MONTH_TITLES;

    /**
     * It will create the calendar view object.
     *
     * @param context The application context in which this object will be used
     * @param attrs An attribute set to indicate values like height and width of this view.<br>
     *              <b>Note:</b> It is not needed, please, set as null
     *
     * */
    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentCalendar = Calendar.getInstance();
        MONTH_TITLES = new String[]{
                "January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"
        };

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mood_calendar_view, this);
        /*
        LayoutParams:
        https://developer.android.com/guide/topics/ui/declaring-layout#layout-params
        https://developer.android.com/reference/android/view/ViewGroup.LayoutParams
         */
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        assignUiElements();
        changeMonthText();
        changeYearText();

        setId(View.generateViewId());
    }

    /**
     * It will retrieve the body of this view.<br>
     * The body of this view is the part where the dates are displayed
     * */
    public GridView getCalendarBody(){
        return calendarBody;
    }

    /**
     * Obtain a clon of the calendar this view is using to know and display
     * the current year, month and days
     * */
    public Calendar getCurrentCalendar(){
        return (Calendar) currentCalendar.clone();
    }

    /**
     * Attach and adapter to this view, the adapter will take care of updating the body
     * interface to the maintain consistency
     *
     * @param adapter The CalendarAdapter object you want to attach to this view
     *
     * @see CalendarAdapter
     * */
    @SuppressLint("ClickableViewAccessibility")
    public void attachAdapter(CalendarAdapter adapter) {
        bodyDataAdapter = adapter;

        //TODO: all this calculations could be passed to our CalendarAdapter. This way
        // we could isolate this view an make it more generic
        float window = Resources.getSystem().getDisplayMetrics().heightPixels;
        float itemRowHeight = getResources().getDimension(R.dimen.calendar_days_row_height);

        itemRowHeight += getResources().getDimension(R.dimen.calendar_top_info_height);
        //here we have the sum up size of all the items but the CalendarView
        itemRowHeight += getResources().getDimension(R.dimen.custom_action_bar_size);

        //we calculate the height of the body
        itemRowHeight = window - itemRowHeight;
        itemRowHeight = itemRowHeight/MAX_ROWS;

        //The height of our items won't fill up the entire screen as we like so we need to do
        //this workaround to fix it.
        bodyDataAdapter.setContainerHeight(Math.round(itemRowHeight));

        window = Resources.getSystem().getDisplayMetrics().widthPixels;
        window = window/ MAX_COLUMNS;

        //The problem here is the same as above, the view doesn't fit the entire width of the screen
        //so we need to do this to force that behaviour
        calendarBody.setColumnWidth(Math.round(window+1));
        calendarBody.setAdapter(bodyDataAdapter);

        //Override this method could lead to a worse UX for visually impaired people.
        //We do it because if not he body will have an undesired vertical scroll functionality
        //reference: https://stackoverflow.com/questions/47107105/android-button-has-setontouchlistener-called-on-it-but-does-not-override-perform
        calendarBody.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });

        adapter.setCurrentDates(obtainCalendarData(CalendarAction.CURRENT));
    }

    /**
     * This method will initialize all the attributes needed to control calendar's UI
     * */
    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        headerYearText = findViewById(R.id.CaHeDateInfoYear);
        headerMonthText = findViewById(R.id.CaHeDateInfoMonth);
        btnPrev = findViewById(R.id.CaHeaderArrowLeft);
        btnNext = findViewById(R.id.CaHeaderArrowRight);
        calendarBody = findViewById(R.id.CalendarBody);
    }

    /**
     * It will change the text displayed inside the month TextView
     * */
    private void changeMonthText() {
        //this value will be in the range of 0 to 11
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        headerMonthText.setText(MONTH_TITLES[currentMonth]);
    }

    /**
     * It will change the text displayed inside the year TextView
     * */
    private void changeYearText(){
        int currentYear = currentCalendar.get(Calendar.YEAR);
        headerYearText.setText(String.valueOf(currentYear));
    }

    /**
     * It will create the previous and/or next arrows buttons functionality
     *
     * @param type A boolean indicating if we want to create the button next functionality (true)
     *             or button previous functionality (false)
     * @param callback An observer that will perform as a callback when the corresponding button
     *                 functionality has been triggered.<br><br>
     *                 The callback will receive an UpdateDateTransfer object in its <i>args</i>
     *                 parameter. That object will contain the month and the year used by the
     *                 CalendarView after performing the next or previous action
     *
     * @see Observer
     * @see UpdateDateTransfer
     * */
    public void createBtnListeners(boolean type, @Nullable final Observer callback){
        if(type) {
            btnNext.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    bodyDataAdapter.setCurrentDates(obtainCalendarData(CalendarAction.NEXT));
                    changeMonthText();

                    if(callback != null) {
                        callback.update(null, new UpdateDateTransfer(currentCalendar.get(Calendar.YEAR),
                                currentCalendar.get(Calendar.MONTH) + 1));
                    }
                }
            });
        }else{
            btnPrev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    bodyDataAdapter.setCurrentDates(obtainCalendarData(CalendarAction.PREVIOUS));
                    changeMonthText();
                    if(callback != null) {
                        callback.update(null, new UpdateDateTransfer(currentCalendar.get(Calendar.YEAR),
                                currentCalendar.get(Calendar.MONTH) + 1));
                    }
                }
            });
        }

    }

    /**
     * It will obtain the calendar body items for the given month
     *
     * @param month An integer representing the month from which we want the body items.<br>
     *              It must be in the range of 0<= month <= 11
     * @return An ArrayList of view items for the given month or null if the given month
     * is out or range
     *
     * @see CalendarViewItem
     * @see CalendarAdapter
     * */
    private ArrayList<CalendarViewItem> obtainCalendarData(int month){
        if(month < 0 || month > 11){
            return null;
        }
        currentCalendar.set(Calendar.MONTH, month);
        return obtainCalendarData(CalendarAction.CURRENT);
    }

    /**
     * Retrieve the list of view items needed by the attached adapter
     *
     * @param selectedMonth A value representing if we wan the current month, the next month or
     *                      the previous one
     *
     * @return A list of CalendarViewItems with the correct dates of the wanted month
     *
     * @see CalendarViewItem
     * @see CalendarAdapter
     * */
    private ArrayList<CalendarViewItem> obtainCalendarData(CalendarAction selectedMonth){
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int previousMont = currentMonth;

        currentCalendar.set(Calendar.MONTH,  currentMonth + selectedMonth.value);
        currentMonth = currentCalendar.get(Calendar.MONTH);

        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        ArrayList<CalendarViewItem> monthDays = new ArrayList<>();

        //we clear the array body
        for(int i = 0; i < MAX_ROWS ; i++){
            for (int j = 0; j < MAX_COLUMNS; j++){
                monthDays.add(new CalendarViewItem(currentCalendar.get(Calendar.YEAR), currentMonth+1, 0));
            }
        }

        int init;
        //we set current day to first of the current month
        currentCalendar.set(Calendar.DAY_OF_MONTH, 1);
        //For USA people, weeks starts at sunday
        //They associate week days as follows
        // sunday(0), monday(1), tuesday(2), wednesday(3), thursday(4), friday(5), saturday(6)
        //In europe, week starts at monday
        if(currentCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            init = 6;
        } else {
            init = currentCalendar.get(Calendar.DAY_OF_WEEK)-2;
        }
        //will keep track of the day number
        char day = 1;
        //we set up the correct values of the first row
        for(int i = init; i < MAX_COLUMNS ; i++){
            monthDays.get(i).setDay(day);
            day++;
        }

        //we fill the rest
        int columnRestOfDays;
        int maxDayOfCurrentMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for(int rowRestOfDays = 1; rowRestOfDays < MAX_ROWS; rowRestOfDays++){
            columnRestOfDays = 0;
            //the last day of the month could end up in between a row
            while(day <= maxDayOfCurrentMonth && columnRestOfDays < MAX_COLUMNS){
                monthDays.get(rowRestOfDays*MAX_COLUMNS + columnRestOfDays).setDay(day);
                day++;
                columnRestOfDays++;
            }
        }

        //restore the current day
        currentCalendar.set(Calendar.DAY_OF_MONTH, currentDay);

        //if we go from january to december of the previous year or we go from
        //december to january of the next year
        if((previousMont == 0 && selectedMonth == CalendarAction.PREVIOUS) ||
           (previousMont == 11 && selectedMonth == CalendarAction.NEXT)){
            changeYearText();
        }

        return monthDays;
    }

    /*======================= TEST METHODS =======================*/

    public ArrayList<CalendarViewItem> obtainCalendarDataGivenMonthUNIT_TEST(int month){
        return obtainCalendarData(month);
    }

    public ArrayList<CalendarViewItem> obtainCalendarDataUNIT_TEST(){
        return obtainCalendarData(CalendarAction.CURRENT);
    }

    public String obtainCalendarActionNameUNIT_TEST(){
        return String.format("first: %s\tsecond: %s\tthird: %s", CalendarAction.CURRENT,
                                                               CalendarAction.NEXT,
                                                               CalendarAction.PREVIOUS);
    }

    public int obtainCalendarActionValueUNIT_TEST(int who){
        switch (who){
            case 0: return CalendarAction.CURRENT.value;
            case 1: return CalendarAction.NEXT.value;
            case 2: return CalendarAction.PREVIOUS.value;
            default:return -2;
        }
    }

    /**
     * Enum class denoting and action we want to perform to our calendar. Get the next
     * month or previous or get the current month     *
     * */
    private enum CalendarAction{
        CURRENT (0),
        NEXT (1),
        PREVIOUS (-1);

        private final int value;

        CalendarAction(int value) {
            this.value = value;
        }
    }

    /**
     * A transfer object to obtain the current month and year which is being used by the
     * corresponding CalendarView object.<br><br>
     *
     * <b>NOTE:</b> This object will help implement some queries to a ViewModel object
     *
     * @see androidx.lifecycle.ViewModel
     * @see CalendarView
     * */
    public static class UpdateDateTransfer {
        private int year;
        private int month;

        UpdateDateTransfer(int year, int month) {
            this.year = year;
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }
    }
}
