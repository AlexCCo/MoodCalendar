package es.ucm.fdi.moodcalendar.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;

/**
 * DAO object with the unique purpose of accessing to our database
 *
 * @author Alejandro Cancelo Correia
 * */
@Dao
public interface DateWithBackgroundDAO {

    /**
     * It will insert the given date into the database.<br>
     * If that value already exists, it will replace it
     * */
    @Insert(entity = DateWithBackground.class ,onConflict = OnConflictStrategy.REPLACE)
    public void insertDate(DateWithBackground date);

    /**
     * It will retrieve the list of days belonging to a month of a year
     *
     * @param year int representing the year we want
     * @param month int representing the month we want
     *
     * We make it to return a LiveData object because Room will take care of
     * notify all observers when the database state change
     */
    @Query("SELECT * FROM date_with_background_color WHERE date LIKE  :year || \"-\" || :month || \"-%\"")
    public LiveData<List<DateWithBackground>> queryDatesByYearAndMonth(int year, int month);

    @Query("SELECT * FROM date_with_background_color")
    public List<DateWithBackground> queryAllDates();
    /**
     * We make it to return a LiveData object because Room will take care of
     * notify all observers when the database state change
     * */
    @Query("SELECT * FROM date_with_background_color WHERE date LIKE :year || \"-%-%\"")
    public LiveData<List<DateWithBackground>> queryDatesByYear(int year);

    @Query("DELETE FROM date_with_background_color")
    public void deletedAllData();
}
