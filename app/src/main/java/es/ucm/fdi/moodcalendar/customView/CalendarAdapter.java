package es.ucm.fdi.moodcalendar.customView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.moodcalendar.R;
import es.ucm.fdi.moodcalendar.dataModel.MoodSelection;
import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;


/**
 * This is an adapter class for CalendarView objects, it will let you display
 * each cell inside that view.<br>
 * Each cell is a CalendarViewItem please, refer to this class if any doubt of what
 * it is and what it contains
 *
 * @see CalendarView
 *
 * @author Alejandro Cancelo Correia
 * */
public class CalendarAdapter extends ArrayAdapter<DateWithBackground> {
    private static final String TAG = "CalendarAdapter";
    /**
     * List of items controlled by this view
     * @see CalendarViewItem
     * */
    private List<CalendarViewItem> currentDates;
    /**
     * height specified with setContainerHeight() method
     * */
    private int parentHeight;

    /**
     * Creates the CalendarAdapter object, this object is intended to be used along with CalendarView
     * and CalendarViewItem
     *
     * @param context Application context where this object is going to be used
     * @param resource Id for this adapter
     *
     * @see Context
     * */
    public CalendarAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        currentDates = new ArrayList<>();
    }

    /**
     * Each cell is constrained by a width and a height parameter.<br>
     * This method allows you to specify the preferred height for each cell.
     *
     * @param height The preferred height in PIXELS
     * */
    void setContainerHeight(int height){
        parentHeight = height;
    }

    @Override
    public int getCount() {
        return currentDates.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        /*
        * NOTE: this view could follow the ViewHolder design pattern, currently it doesn't
        * support that kind of functionality
        * more of ViewHolder in the following link
        * https://androidacademic.blogspot.com/2016/12/android-viewholder-pattern.html
        * https://willowtreeapps.com/ideas/android-fundamentals-working-with-the-recyclerview-adapter-and-viewholder-pattern
        *
        * example of use in the official Android site:
        * https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder
        * */
        if(view == null){
            //obtain inflater
            LayoutInflater inflater = LayoutInflater.from(getContext());
            //create view
            view = inflater.inflate(R.layout.mood_cal_date_view, null);
            view.setId(View.generateViewId());
            view.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, parentHeight));
        }

        TextView dateNumber = view.findViewById(R.id.dateNumber);

        //it won't display days with a 0 value
        if(currentDates.get(i).getDay() != 0) {
            dateNumber.setText(String.format("%s", currentDates.get(i).getDay()));
        }else {
            dateNumber.setText("");
        }
        view.setBackgroundColor(ContextCompat.getColor(this.getContext(), currentDates.get(i).getColorId()));

        return view;
    }

    /**
     * The access of this function is package private, it will only be use by a CalendarView object
     *
     * @return It will return the list of items that this adapter is controlling
     * */
    List<CalendarViewItem> getCurrentDates() {
        return currentDates;
    }

    /**
     * This function allows to change the item list of this adapter and redraw the corresponding
     * view this object is attached to
     *
     * @param newList List of new items to be drawn
     * */
    void setCurrentDates(List<CalendarViewItem> newList){
        currentDates = newList;
        notifyDataSetChanged();
    }

    /**
     * It will obtain the date inside the item object selected by its parameter.
     * @param pos Position of the item which we want to obtain the date of
     * @return A string representing the date of the item selected.<br>
     *     <b>NOTE:</b> The string will be something like "YYYY-MM-DD"<br><br>
     *     <i>Example:</i> "2020-5-10"
     * */
    public String getStringifiedIn(int pos){
        return currentDates.get(pos).stringify();
    }

    /**
     * This only purpose of this method is to return a transfer object.<br>
     * @param pos Position of the item displayed by this adapter
     * @return A transfer object with all the important values of this adapter's items
     * @see <a href="https://www.tutorialspoint.com/design_pattern/transfer_object_pattern.htm">to know more of transfer objects</a>
     * */
    public CalendarAdapterItemTransfer getDateOf(int pos){
        return new CalendarAdapterItemTransfer(currentDates.get(pos));
    }

    /**
     * It will let you update the background of the view items controlled by this Adapter
     *
     * @param data A list of DateWithBackground entities needed to update the background
     *             of each cell
     *
     * @see DateWithBackground
     * */
    public void updateBackground(List<DateWithBackground> data){
        boolean finish = false, edited = false;
        int bottom = 0, top = currentDates.size() -1;
        /*
        * NOTE: we will implement an algorithm with a computational cost of O(n + nlog(n))
        * because it will be the more efficient way of updating each cell's background.
        *
        * Our cells are an array with zeroes at the beginning and/or the end, we are
        * trimming the bottom and top (cost O(n)) for perform later a binary search
        * for each item in the list given as argument (cost O(nlog(n))). We can do
        * a binary search because after trimming our bottom and top ends what's left
        * is an ordered array
        * */

        //our list will have zeroes in the beginning and the end, we just calculate where
        //those zeroes stop
        while(!finish){
            if(currentDates.get(bottom).getDay() == 0){
                bottom++;
                edited = true;
            }
            if(currentDates.get(top).getDay() == 0){
                top--;
                edited = true;
            }
            if(!edited){
                finish = true;
            }
            edited = false;
        }

        Log.d(TAG, "updateBackground: (bottom, top, data.size())=(" + bottom + "," + top + "," + data.size()+")");
        //now we have our sublist with numbers in ascending order, we can implement a
        //binary search

        for(DateWithBackground dwbData : data){
            CalendarViewItem item = getItemByDay(bottom, top, dwbData.getDay());
            if(item != null){
                item.setColorId(dwbData.getMoodColor());
                item.setMarked(true);
            }
        }

        notifyDataSetChanged();
    }

    /**
     * Method implementing a binary search for the cell which contains the <i>day</i> parameter
     *
     * @param bottom An integer representing the bottom end of the array (beginning)
     * @param top An integer representing the top end of the array (end)
     * @param day An integer representing the day we are looking for
     *
     * @return It will return the CalendarViewItem which holds that day or null if
     * it doesn't exist
     * */
    private CalendarViewItem getItemByDay(int bottom, int top, int day){
        int middle = (top+bottom)/2;
        CalendarViewItem item = null;

        while (top >= bottom){
            item = currentDates.get(middle);

            if(item.getDay() == day){
                return item;
            } else if(item.getDay() > day){
                top = middle - 1;
            } else {
                bottom = middle + 1;
            }
            middle = (top + bottom)/2;
        }

        return item;
    }

    /**
     * Once the item have been clicked, it will change its state to <i>"marked"</i>
     * in this state, if you click that item again it will show you the data you inserted
     * before<br>
     * This method allows you to see if the item in the given position is marked or not
     *
     * @param pos Position of the item we want to know its state
     * @return It will return a <b>true</b> if that item is marked or <b>false</b> if not
     *
     * @throws IndexOutOfBoundsException if the given position (pos) is out of the range of
     *  0 <= pos < total items controlled by this adapter
     *
     * */
    public boolean isItemInViewMark(int pos) {
        return currentDates.get(pos).isMarked();
    }


    /**
     * A class implementing the transfer design pattern for each view item managed by
     * CalendarAdapter objects
     *
     * @see CalendarAdapter
     * */
    public static class CalendarAdapterItemTransfer{
        private int year;
        private int month;
        private int day;
        /**
         * It will return an instance of this class with the important data from the parameter
         *
         * @param item A CalendarViewItem from which obtain the important data
         *
         * @see CalendarViewItem
         * */
        CalendarAdapterItemTransfer(CalendarViewItem item) {
            this.year = item.getYear();
            this.month = item.getMonth();
            this.day = item.getDay();
        }

        /**
         * Get year
         * */
        public int getYear() {
            return year;
        }

        /**
         * Get month
         * */
        public int getMonth() {
            return month;
        }


        /**
         * Get day
         * */
        public int getDay() {
            return day;
        }
    }
}
