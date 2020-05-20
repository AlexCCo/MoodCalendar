package es.ucm.fdi.moodcalendar.dataModel;

import androidx.room.TypeConverter;

/**
 * Converter object to help room to implement injection inside our MoodCalendarDatabase class
 *
 * @see es.ucm.fdi.moodcalendar.repository.MoodCalendarDatabase
 * */
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
