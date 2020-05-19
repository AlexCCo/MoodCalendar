package es.ucm.fdi.moodcalendar.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import es.ucm.fdi.moodcalendar.dataModel.MoodSelection;
import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;
import es.ucm.fdi.moodcalendar.viewModel.DriveServiceHelper;

public class MoodCalendarRepository {
    private static final String TAG = "MoodCalendarRepository";
    private DateWithBackgroundDAO dwbDao;

    public MoodCalendarRepository(Application application) {
        MoodCalendarDatabase db = MoodCalendarDatabase.getDatabase(application);
        dwbDao = db.dateDao();
    }
    //TODO: document
    public LiveData<List<DateWithBackground>> getDatesByYearAndMonth(int year, int month){
        return dwbDao.queryDatesByYearAndMonth(year, month);
    }

    public void insertDate(final String toParse) {
        MoodCalendarDatabase.databaseAccessExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String[] response = toParse.split("&");
                MoodSelection sel = MoodSelection.valueOf(Integer.parseInt(response[1]));

                String comment = response.length <= 2 ? "No entry added" : response[2];

                DateWithBackground dataToInsert = new DateWithBackground(response[0], sel, comment);
                Log.d(TAG, "run: " + response[0] + "|" + sel.name() + "|" + comment);
                dwbDao.insertDate(dataToInsert);
            }
        });
    }

    public void parseAndInsert(final String toParse) {
        MoodCalendarDatabase.databaseAccessExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if(toParse.isEmpty()){
                    return;
                }

                String[] rawListData = toParse.split(":");
                Log.d(TAG, "parseAndInsert.run: toParse=" + toParse);
                for (int i = 0; i < rawListData.length; i++) {
                    String[] response = rawListData[i].split("&");

                    MoodSelection sel = MoodSelection.valueOf(Integer.parseInt(response[1]));

                    String comment = response.length <= 2 ? "No entry added" : response[2];

                    DateWithBackground dataToInsert = new DateWithBackground(response[0], sel, comment);
                    Log.d(TAG, "run: " + response[0] + "|" + sel.name() + "|" + comment);
                    dwbDao.insertDate(dataToInsert);
                }
            }
        });
    }

    public void storeInDrive(final DriveServiceHelper driveService, final String fileId) {
        MoodCalendarDatabase.databaseAccessExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<DateWithBackground> dates = dwbDao.queryAllDates();

                StringBuilder builder = new StringBuilder();

                for (DateWithBackground dwb: dates) {
                    builder.append(dwb.getStringVersion());
                    builder.append(":");
                }
                Log.i(TAG, "storeInDrive.run: storing data in drive file");
                driveService.saveFile(fileId, builder.toString());
            }
        });
    }
}
