package es.ucm.fdi.moodcalendar.customView;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import es.ucm.fdi.moodcalendar.R;
import es.ucm.fdi.moodcalendar.dataModel.DateWithBackground;

public class CalendarView extends LinearLayout {
    private static final String TAG = "CalendarView";
    private final int MAX_ROWS = 6;
    private final int MAX_COLUMNS = 7;

    private LinearLayout weekDaysList;
    private TextView headerYearText;
    private TextView headerMonthText;
    //arrow left
    private ImageView btnPrev;
    //arrow right
    private ImageView btnNext;
    //Calendar body
    private GridView calendarBody;
    private CalendarAdapter bodyDataAdapter;
    private Calendar currentCalendar = Calendar.getInstance();


    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void createView(@NonNull ArrayList<DateWithBackground> dateList){
        if(dateList.isEmpty()){
            dateList = obtainCalendarData();
        }

        initControl(getContext(), dateList);
        //we give it a new id
        setId(View.generateViewId());
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events) {

    }

    private ArrayList<DateWithBackground> obtainCalendarData(){
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        ArrayList<DateWithBackground> monthDays = new ArrayList<>();

        for(int i = 0; i < MAX_ROWS ; i++){
            for (int j = 0; j < MAX_COLUMNS; j++){
                monthDays.add(new DateWithBackground(0));
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
        int maxDayOfCurrentMonth = currentCalendar.getMaximum(Calendar.DAY_OF_MONTH);

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


    /**
     * This method will initialize all the attributes needed to control calendar's logic
     * */
    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        weekDaysList = findViewById(R.id.CalendarBodyMetaData);
        headerYearText = findViewById(R.id.CaHeDateInfoYear);
        headerMonthText = findViewById(R.id.CaHeDateInfoMonth);
        btnPrev = findViewById(R.id.CaHeaderArrowLeft);
        btnNext = findViewById(R.id.CaHeaderArrowRight);
        calendarBody = findViewById(R.id.CalendarBody);
    }

    /**
     * It will associate the layout to this class and will initialize the attributes
     * @param context
     * @param attrs
     */
    private void initControl(Context context, ArrayList<DateWithBackground> attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mood_calendar_view, this);
        /*
        LayoutParams:
        https://developer.android.com/guide/topics/ui/declaring-layout#layout-params
        https://developer.android.com/reference/android/view/ViewGroup.LayoutParams
         */
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        assignUiElements();
        attachAdapter(attrs);
    }

    private void attachAdapter(ArrayList<DateWithBackground> datesList) {
        bodyDataAdapter = new CalendarAdapter(getContext(), 0, datesList);

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

        calendarBody.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: " + ((TextView)view.findViewById(R.id.dateNumber)).getText());

                view.setBackgroundColor(getResources().getColor(R.color.teal_green, null));
            }
        });
    }

    /*======================= GETTERS =======================*/

    public ArrayList<DateWithBackground> getDateLists(){
        return bodyDataAdapter.getCurrentDates();
    }

    /*======================= TEST METHODS =======================*/

    public List<DateWithBackground> obtainCalendarDataUNIT_TEST(){
        return obtainCalendarData();
    }



}
