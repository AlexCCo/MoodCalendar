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

public class MoodCalendarViewModel extends AndroidViewModel {
    private static final String TAG = "MoodCalendarViewModel";
    private MoodCalendarRepository repository;
    private LiveData<List<DateWithBackground>> dateLiveData;
    private DriveServiceHelper mDriveServiceHelper;
    private String driveFileId;

    public MoodCalendarViewModel(@NonNull Application application) {
        super(application);
        repository = new MoodCalendarRepository(application);
    }

    public LiveData<List<DateWithBackground>> getDatesByYearAndMonth(int year, int month, LifecycleOwner owner){
        if(dateLiveData != null) {
            dateLiveData.removeObservers(owner);
        }
        dateLiveData = repository.getDatesByYearAndMonth(year, month);
        return dateLiveData;
    }

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

    public void insertEntityDate(String toParse){
        repository.insertDate(toParse);
    }

    /**
     * @param driveCollectionScope Scopes.DRIVE_APPFOLDER or Scopes.DRIVE
     * @param driveScope "drive" if Scopes.DRIVE_FULL or "appDataFolder" if Scopes.DRIVE_APPFOLDER
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
    //TODO: read file and write file[DRIVE]

    public DriveServiceHelper getDriveService(){
        return mDriveServiceHelper;
    }

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

    private void parseAndInsertDates(String toParse){
        repository.parseAndInsert(toParse);
    }

    public void storeInDrive(){
        repository.storeInDrive(mDriveServiceHelper, driveFileId);
    }
}
