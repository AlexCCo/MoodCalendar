package es.ucm.fdi.moodcalendar.dataModel;

import android.graphics.Color;

import es.ucm.fdi.moodcalendar.R;

public enum MoodSelection {
    SO_HAPPY, //this is the maximum value
    HAPPY,
    NORMAL,
    SAD,
    REALLY_SAD,
    NOT_MARKED; //this is the minimum value

    public static int colorOf(MoodSelection mood){
        //i'm not a psychologist but i tried to look for the associated
        //emotions to certain colors
        switch (mood){
            case REALLY_SAD:
                return R.color.purple4;
            case SAD:
                return R.color.gray;
            case NORMAL:
                return R.color.diamond_blue;
            case HAPPY:
                return R.color.olive_green;
            case SO_HAPPY:
                return R.color.teal_green;
            default:
                return R.color.white;
        }
    }
    //TODO: MoodSelection: document methods
    public static MoodSelection valueOf(int val){
        MoodSelection mVal = null;

        for(MoodSelection moodVal : MoodSelection.values()){
            if(moodVal.ordinal() == val){
                mVal = moodVal;
                break;
            }
        }

        return mVal;
    }

    public static String nameOf(MoodSelection mood){
        switch (mood){
            case REALLY_SAD: return "Really Sad :(";
            case SO_HAPPY: return "Really Happy :)";
            case NORMAL: return "Normal";
            case HAPPY: return "Happy";
            case SAD: return "Sad";
            default: return "...";
        }
    }
}
