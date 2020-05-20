package es.ucm.fdi.moodcalendar.dataModel.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

import es.ucm.fdi.moodcalendar.dataModel.MoodSelection;

/**
 * Entity class for representing coloured cells on the calendar.<br>
 * It will contain a string as primary key representing the date
 * of the cell in format YYYY-MM-DD (same format as LocalDate class)
 *
 * @author Alejandro Cancelo Correia
 * @see LocalDate
 * */
@Entity(tableName = "date_with_background_color")
public class DateWithBackground {
    /**
     * date of the object in format "YYYY-MM-DD"<br><br>
     * <i>Example:</i> "2020-5-25"
     * */
    @PrimaryKey
    @NonNull
    private String date;
    @NonNull
    @ColumnInfo(name = "current_mood")
    private MoodSelection mood;

    /**
     * A String containing all the comments a user made for one specific date
     * */
    @Nullable
    @ColumnInfo(name = "comments")
    private String log;

    /**
     * It will create a DateWithBackground object with a date, mood and thoughts of the user
     *
     * @param date A string date in the format of "YYYY-MM-DD"
     * @param mood A mood value representing the felling of the user in the corresponding date
     * @param log A string with the comments/thoughts the user made for the corresponding date
     * */
    public DateWithBackground(@NonNull String date, @NonNull MoodSelection mood, @Nullable String log) {
        this.date = date;
        this.mood = mood;
        this.log = log;
    }

    /**
     * It will retrieve the current day of this object
     * */
    public int getDay() {
        int result = 0;
        //fields will be something like, if date is 2007-12-5, fields will be
        //{"2007", "12", "5"}
        String[] fields = date.split("-");
        result = Integer.parseInt(fields[2]);

        return result;
    }

    /**
     * This method will set the day to the given day
     *
     * @param day The day we want to set, this value must be greater
     *            than zero or it won't take effect
     * */
    public void setDay(int day) {
        if(day > 0) {
            //we replace the last number by te given day. The last number represents the day
            date = date.replaceFirst("[0-9]{1,}$", String.valueOf(day));
        }
    }

    /**
     * Retrieves mood
     * */
    public MoodSelection getMood() {
        return mood;
    }

    /**
     * Get color representing the mood value
     * */
    public int getMoodColor(){
        return MoodSelection.colorOf(mood);
    }

    /**
     * Get the entire string representing the date.<br>
     * This string is in the form of "YYYY-MM-DD"
     * */
    public String getDate() {
        return date;
    }

    /**
     * Get the user's thought for this object date
     * */
    public String getLog() {
        return log;
    }

    /**
     * It will retrieve a string version of the important values.<br>
     * The result will be in the form of: YYYY-MM-DD&mood-ordinal-value&thoughts<br><br>
     *
     * <i>Example:</i> 2020-5-25&2&This day could be worse
     * */
    public String getStringVersion(){
        return date + "&" + mood.ordinal() + "&" + log;
    }
}
