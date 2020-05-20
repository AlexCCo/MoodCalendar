package es.ucm.fdi.moodcalendar.viewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;

import java.util.Collections;
import java.util.List;

import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;
import es.ucm.fdi.moodcalendar.repository.MoodCalendarRepository;

/**
 * ViewModel class for implement the Model-View-ViewModel architectural design pattern.<br>
 * It will control the access to data that must be preserve or has to be independent of the
 * User Interface (Activities that use it, Fragments...etc)
 *
 * @see AndroidViewModel
 * @see <a href="https://developer.android.com/topic/libraries/architecture/viewmodel">Official info about MVV architecture</a>
 * @see MoodCalendarRepository
 * @see LiveData
 * */
public class MoodCalendarViewModel extends AndroidViewModel {
    private static final String TAG = "MoodCalendarViewModel";
    /**
     * External module where we will query the information requested by our users.<br>
     * Having it this way makes it more modular and therefore, more testable and
     * maintainable.
     *
     * @see MoodCalendarRepository
     * */
    private MoodCalendarRepository repository;
    /**
     * Current data requested by the user.<br>
     * We are storing a reference to this because it help us avoid repeating unnecessary queries
     * to the database.
     *
     * @see LiveData
     * @see DateWithBackground
     * */
    private LiveData<List<DateWithBackground>> dateLiveData;
    /**
     * An object managing all the hard work of dealing with Google's Drive API
     *
     * @see DriveServiceHelper
     * @see <a href="https://developers.google.com/drive/api/v3/about-sdk">Official Google's Drive API site</a>
     * */
    private DriveServiceHelper mDriveServiceHelper;
    /**
     * Id of the User's Google Drive file where we will store data as a backup.<br>
     * @see <a href="https://developers.google.com/drive/api/v3/about-sdk">Official Google's Drive API site</a>
     * */
    private String driveFileId;

    /**
     * It will associate the given application with this ViewModel object.<br>
     * The way we obtain an instance of this object is through a ViewModelProvider.
     *
     * @param application Application which will be using this object
     *
     * @see androidx.lifecycle.ViewModelProvider
     * @see MoodCalendarViewModel
     * */
    public MoodCalendarViewModel(@NonNull Application application) {
        super(application);
        repository = new MoodCalendarRepository(application);
    }

    /**
     * It will query the repository all dates from an specific year and month
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
     * @see MoodCalendarRepository
     * */
    public LiveData<List<DateWithBackground>> getDatesByYearAndMonth(int year, int month, LifecycleOwner owner){
        if(dateLiveData != null) {
            dateLiveData.removeObservers(owner);
        }
        dateLiveData = repository.getDatesByYearAndMonth(year, month);
        return dateLiveData;
    }

    /**
     * It will obtain an specific date information.
     *
     * @param year Year of the date
     * @param month Month of the date. Must be in the range of 1<= mont <=12
     * @param day Day of the date. Must be in the range of 1<= day <= 31
     *
     * @return A single DateWithBackground entity holding all the information you want to
     * process or null if that date doesn't exits
     *
     * @see DateWithBackground
     * */
    public DateWithBackground getDateByYearMonthDay(int year, int month, int day){
        String id = year + "-" + month + "-" +day;

        try{
            for (DateWithBackground dwb : dateLiveData.getValue()){
                if(dwb.getDate().equals(id)) {
                    return dwb;
                }
            }
        } catch (NullPointerException e){
            return null;
        }

        return null;
    }

    /**
     * It will insert a Date into the database.<br>
     * The date we want to insert must be in the form of "YYYY-MM-DD&mood-selected-ordinal&thoughts"<br><br>
     * <i>Example:</i> "2020-5-28&0&This was a wonderful day, i hope tomorrow will be the same
     *
     * @param toParse String containing the date in the specified format
     * */
    public void insertEntityDate(String toParse){
        repository.insertDate(toParse);
    }

    /**
     * It will construct a Google's Drive helper needed by this ViewModel to communicate
     * with Google's Drive API for storing and reading backup information of the entities of this
     * app
     *
     * @param context Application context in which the drive helper will be used
     * @param mAccount A google's account provided by the user, see the "See also" section for more info
     * @param searchScope A string indicating in which scope the drive helper will be looking for the
     *                    back up file
     * @param driveCollectionScope A scope object for letting the drive helper how to communicate with
     *                             the Google's Drive API.<br>
     *                             Supported Scopes : Scopes.DRIVE_APPFOLDER or Scopes.DRIVE
     * @param driveScope A string for letting the drive helper the domain where the backup file will be store.<br>
     *                   "drive" if Scopes.DRIVE_FULL (It will set the domain in "My Drive" folder)<br>
     *                   "appDataFolder" if Scopes.DRIVE_APPFOLDER (It will set the domain in a hidden path called
     *                   appData where the user can't access directly)
     *
     * @see GoogleSignInAccount
     * @see com.google.android.gms.common.Scopes
     * @see <a href="https://developers.google.com/drive/api/v3/about-sdk">Official documentation site about Google's Drive API</a>
     * */
    public void driveSetUp(Context context, GoogleSignInAccount mAccount, Scope driveCollectionScope, String driveScope, String searchScope) {
        if(mDriveServiceHelper == null) {
            Log.i(TAG, "driveSetUp: Creating driveServiceHelper...");
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            context,
                            Collections.singleton(driveCollectionScope.getScopeUri())
                    );

            credential.setSelectedAccount(mAccount.getAccount());

            Drive googleDriveService =
                    new Drive.Builder(
                            new NetHttpTransport(),
                            new GsonFactory(),
                            credential)
                            .setApplicationName("MoodCalendar")
                            .build();

            mDriveServiceHelper = new DriveServiceHelper(googleDriveService, driveScope, searchScope);

            Log.i(TAG, "driveSetUp: driveServiceHelper created!");
            Log.i(TAG, "driveSetUp: searching app files...");

            mDriveServiceHelper.searchFile().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    String fileId = task.getResult();

                    if(fileId != null){
                        driveFileId = fileId;
                        Log.i(TAG, "driveSetUp: file found with id = " + fileId);
                        populateDataBase();
                    }else {
                        Log.i(TAG, "driveSetUp: app files not found, creating app files...");
                        mDriveServiceHelper.createFile()
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        driveFileId = s;
                                        Log.i(TAG, "driveSetUp: file id obtained: " + driveFileId);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "createFile.onFailure: Exception => " + e.getMessage());
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * Method for reading data from the backup file store in the user's drive account
     * and storing it inside the device's local database
     *
     * @see DriveServiceHelper
     * */
    private void populateDataBase() {
        Log.d(TAG, "populateDataBase: reading file with id " + driveFileId);
        mDriveServiceHelper.readFile(driveFileId).addOnCompleteListener(new OnCompleteListener<Pair<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<Pair<String, String>> task) {
                Pair<String, String> dataRead = task.getResult();

                if(dataRead != null){
                    Log.i(TAG, "populateDatabase.onComplete: parsing data read...");
                    parseAndInsertDates(dataRead.second);
                    return;
                }

                Log.d(TAG, "populateDataBase.onComplete: No data was found");
            }
        });
    }

    /**
     * It will receive a whole string will al the information read from the backup file
     * and will request our repository to insert it into the database.<br><br>
     * All dates information must be separated by  a ':' character.<br><br>
     * <i>Example:</i>"2020-5-25&0&I finally get to see Sarah, i've missed her so much:2020-5-26&4&I fell and i broke my leg What a horrible day!"<br>
     *      (note the colon character in between)
     *
     * @param toParse The whole string containing all the dates you want to insert
     *
     * @see MoodCalendarRepository
     * */
    private void parseAndInsertDates(String toParse){
        repository.parseAndInsert(toParse);
    }

    /**
     * Will store all the database into a backup file in the user's drive
     * */
    public void storeInDrive(){
        repository.storeInDrive(mDriveServiceHelper, driveFileId);
    }
}
