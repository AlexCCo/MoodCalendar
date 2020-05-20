package es.ucm.fdi.moodcalendar.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import es.ucm.fdi.moodcalendar.dataModel.MoodSelection;
import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;
import es.ucm.fdi.moodcalendar.viewModel.DriveServiceHelper;

/**
 * A repository for MoodCalendarViewModel objects could access to obtain the data
 * requested by the user.<br><br>
 * With this class we can encapsulate the data access from the rest of the application
 * making it more testable and modular.<br>
 * It will use LiveData objects as a return of the methods.
 *
 * @see es.ucm.fdi.moodcalendar.viewModel.MoodCalendarViewModel
 * @see LiveData
 * @see <a href="https://developer.android.com/topic/libraries/architecture/viewmodel">more on ViewModels</a>
 * @see <a href="https://developer.android.com/topic/libraries/architecture">Google guidlines for architectural components</a>
 * */
public class MoodCalendarRepository {
    private static final String TAG = "MoodCalendarRepository";
    /**
     * Dao for querying data from room database
     * @see DateWithBackgroundDAO
     * */
    private DateWithBackgroundDAO dwbDao;

    /**
     * Create a repository associated to the application given by argument
     *
     * @param application The application where this repository will be needed. It could
     *                    be an Activity, Fragment...etc
     * */
    public MoodCalendarRepository(Application application) {
        MoodCalendarDatabase db = MoodCalendarDatabase.getDatabase(application);
        dwbDao = db.dateDao();
    }

    /**
     * It will query the database all dates from an specific year and month
     *
     * @param year Year from which obtain dates.
     * @param month Month from which obtain dates. It must be in the range of
     *              1<= month <=12
     *
     * @return A LiveData object holding the resulting list of this query
     *
     * @see LiveData
     * @see <a href="https://developer.android.com/topic/libraries/architecture/livedata">for more information about what is a LiveData object and why it is
     * need</a>
     * */
    public LiveData<List<DateWithBackground>> getDatesByYearAndMonth(int year, int month){
        return dwbDao.queryDatesByYearAndMonth(year, month);
    }

    /**
     * It will insert a new entry into the database
     *
     * @param toParse The String representing the date we want to insert into te database.<br>
     *                This string must be a in the form of "YYYY-MM-DD&mood-selected-ordinal&thoughts.<br><br>
     *                <i>Example:s</i>  "2020-5-26&4&I fell and i broke my leg What a horrible day!"<br>
     *                (4 corresponds with a really bad day)<br>
     *                (thoughts could be empty)
     *
     * @see MoodSelection
     * */
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

    /**
     * This method will receive a whole string with more than one date to insert into
     * the database. All dates information must be separated by  a ':' character.<br><br>
     * <i>Example:</i>"2020-5-25&0&I finally get to see Sarah, i've missed her so much:2020-5-26&4&I fell and i broke my leg What a horrible day!"<br>
     *     (note the colon character in between)
     *
     * @param toParse The whole string containing all the dates you want to insert
     * */
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

    /**
     * It will obtain all dates from the database and concatenate them with a ':' character and then
     * insert the resulting String into the user's Google Drive account.<br><br>
     * <b>A Google's login must be done before.</b>
     *
     * @param driveService An object for managing the access to Google's Drive API
     * @param fileId Id of the file where we want to store our data
     *
     * @see DriveServiceHelper
     * @see <a href="https://developers.google.com/drive/api/v3/about-sdk">Official documentation of Google's Drive API</a>
     * @see <a href="https://www.youtube.com/watch?v=t-yZUqthDMM&feature=youtu.be">Simple tutorial using Google's Sign in button</a>
     * @see <a href="https://developers.google.com/identity/sign-in/android">Official site with Google's sign in button information</a>
     * */
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
