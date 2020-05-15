package es.ucm.fdi.moodcalendar.Room.Entities;

import androidx.room.*;

@Entity
public class Month {
    @PrimaryKey(autoGenerate=true)
    private int mid;
    @ColumnInfo(name = "month")
    private String month;

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
