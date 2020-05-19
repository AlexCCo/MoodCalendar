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

    public GridView getCalendarBody(){
        return calendarBody;
    }

    public Calendar getCurrentCalendar(){
        return currentCalendar;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void attachAdapter(CalendarAdapter adapter) {
        bodyDataAdapter = adapter;

        //TODO: all this calculations could be passed to our CalendarAdapter. This way
        // we could isolate this view an make it more generic
        float window = Resources.getSystem().getDisplayMetrics().heightPixels;
        float itemRowHeight = getResources().getDimension(R.dimen.calendar_days_row_height);

        itemRowHeight += getResources().getDimension(R.dimen.calendar_top_info_height);
        itemRowHeight += getResources().getDimension(R.dimen.custom_action_bar_size);

        itemRowHeight = window - itemRowHeight;
        itemRowHeight = itemRowHeight/MAX_ROWS;

        //The height of our items won't fill up the entire screen as we like so we need to do
        //this workaround to fix it
        bodyDataAdapter.setContainerHeight(Math.round(itemRowHeight));

        window = Resources.getSystem().getDisplayMetrics().widthPixels;
        window = window/ MAX_COLUMNS;

        calendarBody.setColumnWidth(Math.round(window+1));
        calendarBody.setAdapter(bodyDataAdapter);

        //Override this method could lead to a worse UX for visually impaired people.
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
     * This method will initialize all the attributes needed to control calendar's logic
     * */
    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        headerYearText = findViewById(R.id.CaHeDateInfoYear);
        headerMonthText = findViewById(R.id.CaHeDateInfoMonth);
        btnPrev = findViewById(R.id.CaHeaderArrowLeft);
        btnNext = findViewById(R.id.CaHeaderArrowRight);
        calendarBody = findViewById(R.id.CalendarBody);
    }

    //TODO: button listeners outside this class

    private void changeMonthText() {
        //this value will be in the range of 0 to 11
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        headerMonthText.setText(MONTH_TITLES[currentMonth]);
    }

    private void changeYearText(){
        int currentYear = currentCalendar.get(Calendar.YEAR);
        headerYearText.setText(String.valueOf(currentYear));
    }

    public int getYear(){
        return currentCalendar.get(Calendar.YEAR);
    }

    public int getMonth(){
        return  currentCalendar.get(Calendar.MONTH);
    }


    public void createBtnListeners(boolean type,final Observer callback){
        if(type) {
            btnNext.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    bodyDataAdapter.setCurrentDates(obtainCalendarData(CalendarAction.NEXT));
                    changeMonthText();
                    callback.update(null, new UpdateDateTransfer(currentCalendar.get(Calendar.YEAR),
                                                                    currentCalendar.get(Calendar.MONTH)+1));
                }
            });
        }else{
            btnPrev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    bodyDataAdapter.setCurrentDates(obtainCalendarData(CalendarAction.PREVIOUS));
                    changeMonthText();
                    callback.update(null, new UpdateDateTransfer(currentCalendar.get(Calendar.YEAR),
                                                                    currentCalendar.get(Calendar.MONTH)+1));
                }
            });
        }

    }

    private ArrayList<CalendarViewItem> obtainCalendarData(int month){
        currentCalendar.set(Calendar.MONTH, month);
        return obtainCalendarData(CalendarAction.CURRENT);
    }

    private ArrayList<CalendarViewItem> obtainCalendarData(CalendarAction selectedMonth){
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        currentCalendar.set(Calendar.MONTH,  currentMonth + selectedMonth.value);
        currentMonth = currentCalendar.get(Calendar.MONTH);

        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        ArrayList<CalendarViewItem> monthDays = new ArrayList<>();

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
            while(day <= maxDayOfCurrentMonth && columnRestOfDays < MAX_COLUMNS){
                monthDays.get(rowRestOfDays*MAX_COLUMNS + columnRestOfDays).setDay(day);
                day++;
                columnRestOfDays++;
            }
        }

        //restore the day
        currentCalendar.set(Calendar.DAY_OF_MONTH, currentDay);

        return monthDays;
    }

    private void debug(Calendar currentCalendar, List<DateWithBackground> monthDays){
        Log.d(TAG, " L\t M\t X\t J\t V\t S\t D");
        StringBuilder row = new StringBuilder();
        for(int i = 0; i < MAX_ROWS ; i++){
            for (int j = 0; j < MAX_COLUMNS; j++){
                row.append(Integer.toString(monthDays.get(i*MAX_COLUMNS + j).getDay()) + "\t");
            }
            Log.d(TAG, row.toString());
            row.setLength(0);
        }

        Log.d(TAG, "updateCalendar: " + currentCalendar.getMaximum(Calendar.DAY_OF_MONTH));
        Log.d(TAG, "updateCalendar: " + currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Log.d(TAG, "updateCalendar: " + currentCalendar.get(Calendar.DAY_OF_WEEK));
        Log.d(TAG, "updateCalendar: " + currentCalendar.get(Calendar.MONTH));
    }







    /*======================= GETTERS =======================*/

    public List<CalendarViewItem> getDateLists(){
        return bodyDataAdapter.getCurrentDates();
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

    private enum CalendarAction{
        CURRENT (0),
        NEXT (1),
        PREVIOUS (-1);

        private final int value;

        CalendarAction(int value) {
            this.value = value;
        }
    }

    public static class UpdateDateTransfer {
        private int year;
        private int month;

        public UpdateDateTransfer(int year, int month) {
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
