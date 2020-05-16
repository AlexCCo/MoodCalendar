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
import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;


/**
 * Our custom Calendar view (CalendarView class) needs an adapter to render the cells
 * representing days of the month
 *
 * @author Alejandro Cancelo Correia
 * */
public class CalendarAdapter extends ArrayAdapter<DateWithBackground> {
    private static final String TAG = "CalendarAdapter";
    private List<CalendarViewItem> currentDates;
    private int parentHeight;

    //TODO: CalendarAdapter: Document each method
    public CalendarAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        currentDates = new ArrayList<>();
    }

    public void setContainerHeight(int height){
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

        if(view == null){
            //obtain inflater
            LayoutInflater inflater = LayoutInflater.from(getContext());
            //create view
            view = inflater.inflate(R.layout.mood_cal_date_view, null);
            view.setId(View.generateViewId());
            view.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, parentHeight));

        }

        TextView dateNumber = view.findViewById(R.id.dateNumber);

        if(currentDates.get(i).getDay() != 0) {
            dateNumber.setText(String.format("%s", currentDates.get(i).getDay()));
        }
        view.setBackgroundColor(ContextCompat.getColor(this.getContext(), currentDates.get(i).getColorId()));

        return view;
    }

    public List<CalendarViewItem> getCurrentDates() {
        return currentDates;
    }

    void setCurrentDates(List<CalendarViewItem> newList){
        currentDates = newList;
        notifyDataSetChanged();
    }

    public String getStringyfiedIn(int pos){
        return currentDates.get(pos).stringify();
    }

    public void updateBackground(List<DateWithBackground> data){
        Log.d(TAG, "updateBackground: called by observer");
        boolean finish = false, edited = false;
        int bottom = 0, top = currentDates.size() -1;

        for (DateWithBackground dw: data) {
            Log.d(TAG, "updateBackground: loggin data = " + dw.getDate() + ", " +  dw.getMood().name() + ", " + dw.getLog() + ", " + dw.getMoodColor());
        }
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
                Log.d(TAG, "updateBackground: found!");
            }
        }

        notifyDataSetChanged();
    }

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
}
