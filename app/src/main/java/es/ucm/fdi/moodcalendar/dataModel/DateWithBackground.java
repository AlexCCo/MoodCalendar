package es.ucm.fdi.moodcalendar.dataModel;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

/**
 * Entity class for representing coloured cells on the calendar.<br>
 * It will contain a string as primary key representing the date
 * of the cell in format YYYY-MM-DD (same format as LocalDate class)
 *
 * @author Alejandro Cancelo Correia
 * @see LocalDate
 * */
@Entity(tableName = "date_with_background_color")
public class DateWithBackground implements Parcelable {
    @PrimaryKey
    private String date;
    @NonNull
    @ColumnInfo(name = "current_mood")
    private MoodSelection mood;

    public DateWithBackground(LocalDate date, MoodSelection mood){
        this.date = date.toString();
        this.mood = mood;
    }

    public DateWithBackground(LocalDate date){
        this.date = date.toString();
        mood = MoodSelection.NOT_MARKED;
    }

    public DateWithBackground(int year, int month, int day){
        this.date = String.format("%d-%d-%d", year, month, day);
        mood = MoodSelection.NOT_MARKED;
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
     * Sets mood to the given value
     * */
    public void setMood(MoodSelection mood) {
        this.mood = mood;
    }

    /**
     * Get color representing the mood value
     * */
    public int getMoodColor(){
        return MoodSelection.colorOf(mood);
    }

    protected DateWithBackground(Parcel in) {
        date = date.replaceFirst("[0-9]{1,}$", String.valueOf(in.readInt()));
        mood = (MoodSelection) in.readValue(MoodSelection.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] fields = date.split("-");
        dest.writeInt(Integer.parseInt(fields[2]));
        dest.writeValue(mood);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DateWithBackground> CREATOR = new Parcelable.Creator<DateWithBackground>() {
        @Override
        public DateWithBackground createFromParcel(Parcel in) {
            return new DateWithBackground(in);
        }

        @Override
        public DateWithBackground[] newArray(int size) {
            return new DateWithBackground[size];
        }
    };
}
