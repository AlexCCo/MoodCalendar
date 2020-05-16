package es.ucm.fdi.moodcalendar.dataModel;

import androidx.room.TypeConverter;

public class MoodConverterType {

    @TypeConverter
    public static int convertMoodSelectionToIntValue(MoodSelection mood){
        return mood.ordinal();
    }

    @TypeConverter
    public static MoodSelection convertIntToMoodSelection(int value){
        return MoodSelection.valueOf(value);
    }
}
