package es.ucm.fdi.moodcalendar.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;
import es.ucm.fdi.moodcalendar.dataModel.MoodConverterType;

/**
 * An abstract class encapsulating all the logic for represent our app database.<br>
 * Room will take care of the implementation
 *
 * @author Alejandro Cancelo Correia
 * */
@TypeConverters({MoodConverterType.class})
@Database( entities = {DateWithBackground.class}, version = 1, exportSchema = false)
public abstract class MoodCalendarDatabase extends RoomDatabase {
    /**
     * The database will be shared among all background threads because
     * we remember that Room will execute our queries in a background thread,
     * because of that it is a good idea to implement a Singleton Pattern
     * */
    private static volatile MoodCalendarDatabase INSTANCE;

    /**
     * Maximum number of threads we want to have in the background, this will
     * be handle by an Executor object
     * */
    private static final int NUMBER_OF_THREADS = 4;

    /**
     * The Executor Service will take care of managing all background threads
     * creating an abstract layer to you. You don't need, then, to track every
     * thread progress
     * */
    static final ExecutorService databaseAccessExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Room encourage you to use the DAO design pattern (which helps you
     * abstract all database implementation), it needs a function that
     * retrieves a DAO object
     * */
    public abstract DateWithBackgroundDAO dateDao();

    /**
     * It will return just one instance of DateWithBackgroundDatabase
     *
     * @param context The UI context in which this database will be used
     * @return A DateWithBackgroundDatabase instance
     * */
    static MoodCalendarDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            //This must be done atomically, remember that room will create background threads
            //for each query that returns LiveData objects
            synchronized (MoodCalendarDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MoodCalendarDatabase.class,
                            "mood_database")
                            /*.addCallback(sRoomDatabaseCallback)*/
                            .build();
                }
            }
        }

        return INSTANCE;
    }

}
