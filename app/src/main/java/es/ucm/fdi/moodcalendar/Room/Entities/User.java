package es.ucm.fdi.moodcalendar.Room.Entities;
import androidx.room.*;
@Entity
public class User {

    @PrimaryKey (autoGenerate=true)
    private int uid;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name="password")
    private String password;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
