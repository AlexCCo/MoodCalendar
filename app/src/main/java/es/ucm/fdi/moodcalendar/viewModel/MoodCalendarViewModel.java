package es.ucm.fdi.moodcalendar.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;
import es.ucm.fdi.moodcalendar.dataModel.entities.User;
import es.ucm.fdi.moodcalendar.repository.MoodCalendarRepository;

public class MoodCalendarViewModel extends AndroidViewModel {
    private MoodCalendarRepository repository;
    private LiveData<User> user;
    private LiveData<List<DateWithBackground>> dateLiveData;

    public MoodCalendarViewModel(@NonNull Application application) {
        super(application);
        repository = new MoodCalendarRepository(application);

    }

    public LiveData<List<DateWithBackground>> getDatesByYearAndMonth(int year, int month){
        dateLiveData = repository.getDatesByYearAndMonth(year, month);
        return dateLiveData;
    }

    public LiveData<List<DateWithBackground>> getAllDates(){
        dateLiveData = repository.getAllDates();
        return dateLiveData;
    }

    public void insertEntityDate(String toParse){
        repository.insertDate(toParse);
    }
}
