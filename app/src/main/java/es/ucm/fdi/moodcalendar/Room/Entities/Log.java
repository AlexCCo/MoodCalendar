package es.ucm.fdi.moodcalendar.Room.Entities;
import androidx.room.*;

@Entity(foreignKeys = {@ForeignKey(entity = User.class, parentColumns = "uid", childColumns = "uid"),
                        @ForeignKey(entity = Month.class, parentColumns = "mid", childColumns= "mid")})
public class Log {
    @PrimaryKey (autoGenerate=true)
    private int lid;
    @ColumnInfo(name = "uid")
    private String uid;
    @ColumnInfo(name = "mid")
    private String mid;
    @ColumnInfo(name="day")
    private String day;
    @ColumnInfo(name="mood")
    private String mood;
    @ColumnInfo(name="comment")
    private String comment;


    public int getLid() {
        return lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }
}
