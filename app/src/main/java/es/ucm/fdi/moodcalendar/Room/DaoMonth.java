package es.ucm.fdi.moodcalendar.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import es.ucm.fdi.moodcalendar.Room.Entities.Month;

@Dao
public interface DaoMonth {

    @Insert(entity = Month.class)
    int insertnewMonth(Month m);

    @Query("SELECT * from  Month where mid = :mid ")
     LiveData<Month> getMonthbyId(int mid);

    @Delete(entity = Month.class)
    int deleteMonth(int mid);
}
