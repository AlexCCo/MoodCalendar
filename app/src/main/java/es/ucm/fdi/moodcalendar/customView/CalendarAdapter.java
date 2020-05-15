package es.ucm.fdi.moodcalendar.customView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.moodcalendar.R;
import es.ucm.fdi.moodcalendar.dataModel.DateWithBackground;


/**
 * Our custom Calendar view (CalendarView class) needs an adapter to render the cells
 * representing days of the month
 *
 * @author Alejandro Cancelo Correia
 * */
public class CalendarAdapter extends ArrayAdapter<DateWithBackground> {
    private static final String TAG = "CalendarAdapter";
    private ArrayList<DateWithBackground> currentDates;
    private int parentHeight;

    //TODO: CalendarAdapter: Document each method
    public CalendarAdapter(@NonNull Context context, int resource, @NonNull ArrayList<DateWithBackground> objects) {
        super(context, resource, objects);
        this.currentDates = objects;
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

        dateNumber.setText( String.format("%s",currentDates.get(i).getDay()) );
        view.setBackgroundColor(currentDates.get(i).getMoodColor());


        return view;
    }

    public ArrayList<DateWithBackground> getCurrentDates() {
        return currentDates;
    }

    public void changeCurrentDates(ArrayList<DateWithBackground> newList){
        currentDates = newList;
        notifyDataSetChanged();
    }
}
