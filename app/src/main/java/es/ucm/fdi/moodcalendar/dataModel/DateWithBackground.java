package es.ucm.fdi.moodcalendar.dataModel;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

public class DateWithBackground implements Parcelable {
    private int day;
    private MoodSelection mood;

    public DateWithBackground(int day, MoodSelection mood){
        this.day = day;
        this.mood = mood;
    }

    public DateWithBackground(int day){
        this.day = day;
        mood = MoodSelection.NOT_MARKED;
    }
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public MoodSelection getMood() {
        return mood;
    }

    public void setMood(MoodSelection mood) {
        this.mood = mood;
    }

    public int getMoodColor(){
        return MoodSelection.colorOf(mood);
    }

    protected DateWithBackground(Parcel in) {
        day = in.readInt();
        mood = (MoodSelection) in.readValue(MoodSelection.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(day);
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
