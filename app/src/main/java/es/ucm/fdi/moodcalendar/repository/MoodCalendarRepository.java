package es.ucm.fdi.moodcalendar.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import es.ucm.fdi.moodcalendar.dataModel.MoodSelection;
import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;
import es.ucm.fdi.moodcalendar.dataModel.entities.User;

public class MoodCalendarRepository {
    private static final String TAG = "MoodCalendarRepository";
    private DaoUser daoUser;
    private DateWithBackgroundDAO dwbDao;
    //Gets
    private User user;

    public MoodCalendarRepository(Application application) {
        MoodCalendarDatabase db = MoodCalendarDatabase.getDatabase(application);
        daoUser = db.userDao();
        dwbDao = db.dateDao();
    }


    //Insert
    public void insertUser(User u){
          daoUser.insertNewUser(u);
    }

    //Update
    public void updateUser(User u){
        daoUser.updateUser(u);
    }

    //Delete
    public void deleteUser(User u){
       daoUser.deleteUser(u);
    }

    //Show
    public LiveData<User> getUserbyname(String username){
        return daoUser.getUserbyId(username);
    }

    //TODO: document
    public LiveData<List<DateWithBackground>> getDatesByYearAndMonth(int year, int month){
        return dwbDao.queryDatesByYearAndMonth(year, month);
    }

    public LiveData<List<DateWithBackground>> getAllDates(){
        return dwbDao.queryAllDates();
    }

    public void insertDate(final String toParse) {
        MoodCalendarDatabase.databaseAccessExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String[] response = toParse.split("&");
                MoodSelection sel = MoodSelection.valueOf(Integer.parseInt(response[1]));
                DateWithBackground dataToInsert = new DateWithBackground(response[0], sel, response[2]);
                Log.d(TAG, "run: " + response[0] + "|" + sel.name() + "|" + response[2]);
                dwbDao.insertDate(dataToInsert);
            }
        });
    }
}
