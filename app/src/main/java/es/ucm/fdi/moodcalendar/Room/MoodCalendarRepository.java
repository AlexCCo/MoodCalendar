package es.ucm.fdi.moodcalendar.Room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import es.ucm.fdi.moodcalendar.Room.DaoUser;
import es.ucm.fdi.moodcalendar.Room.Entities.Log;
import es.ucm.fdi.moodcalendar.Room.Entities.Month;
import es.ucm.fdi.moodcalendar.Room.Entities.User;

public class MoodCalendarRepository {
    private DaoUser daoUser;
    private DaoMonth daoMonth;
    private DaoLog daoLog;

    //Gets
    private User user;
    private Month month;
    private List<Log> logs;



    MoodCalendarRepository(Application application) {
        MoodCalendarDatabase db = MoodCalendarDatabase.getDatabase(application);
        daoUser = db.daoUser();
        daoMonth = db.daoMonth();
        daoLog = db.daoLog();

    }


//Insert
    int insertUser(User u){
          return  daoUser.insertnewUser(u);

    }

    int insertMonth(Month m) {
       return daoMonth.insertnewMonth(m);

    }
    int setInsertLog(Log l){
        return daoLog.insertnewLog(l);
    }
    //Update
    int updateUser(User u){
        return daoUser.updateUser(u);

    }
    //Delete
    int deleteUser(int u){
       return  daoUser.deleteUser(u);

    }
    int deleteLog(int l) {
        return daoLog.deleteLog(l);
    }
    int deleteMonth(int m) {
          return  daoMonth.deleteMonth(m);
    }

    //Show
    LiveData<User> getUserbyname(String username){
        return daoUser.getUserbyId(username);
    }
    LiveData<Month> getMonthbyId(int mid){
        return daoMonth.getMonthbyId(mid);
    }
    LiveData<List<Log>> getLogsbyUser(int uid, int mid){
        return daoLog.getUserLogByMonth(uid, mid);
    }
}
