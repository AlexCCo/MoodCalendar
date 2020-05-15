package es.ucm.fdi.moodcalendar.Room;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.ucm.fdi.moodcalendar.Room.Entities.Log;
import es.ucm.fdi.moodcalendar.Room.Entities.Month;
import es.ucm.fdi.moodcalendar.Room.Entities.User;

@Database(entities = {User.class, Month.class, Log.class}, version = 1, exportSchema = false)
abstract class MoodCalendarDatabase extends RoomDatabase {

    public abstract DaoUser daoUser();
    public abstract DaoMonth daoMonth();
    public abstract DaoLog daoLog();

    private static volatile MoodCalendarDatabase INSTANCE;


    static MoodCalendarDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MoodCalendarDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MoodCalendarDatabase.class, "MoodCalendar")
                            .fallbackToDestructiveMigration()
                            .build();
                }}}
        return INSTANCE;

    }
}
