package es.ucm.fdi.moodcalendar.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import java.util.List;

import es.ucm.fdi.moodcalendar.Room.Entities.Log;

public interface DaoLog {

    @Insert(entity = Log.class)
    int insertnewLog(Log l);

    @Query("SELECT * from Log where uid = :uid & mid = :mid")
    LiveData<List<Log>> getUserLogByMonth(int uid, int mid);

    @Delete(entity = Log.class)
    int deleteLog(int lid);

}
