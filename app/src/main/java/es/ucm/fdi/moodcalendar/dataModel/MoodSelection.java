package es.ucm.fdi.moodcalendar.dataModel;

import android.graphics.Color;

import es.ucm.fdi.moodcalendar.R;

/**
 * Enum class helping us to determine the mood state of our users.<br><br>
 *
 * <pre>                                <b>DISCLAIMER</b> </pre>
 * We are not psychologist neither experts in the whole total of feelings a
 * human can have, these are the first 5 we came up while thinking and the first 5 that
 * help us implement this app
 *
 * */
public enum MoodSelection {
    SO_HAPPY, //this is the maximum value
    HAPPY,
    NORMAL,
    SAD,
    REALLY_SAD;

    /**
     * @param mood A MoodSelection value representing the current feeling of the user
     *
     * @return A color Id for that mood value
     * */
    //TODO: implement some kind of support to change the set of colors
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

    /**
     * @param val The value we want to obtain the MoodSelection from
     *
     * @return A MoodSelection element for that value of null if it couldn't be associated
     * */
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

    /**
     * @param mood Feeling of the current user
     *
     * @return An String representing the feeling given as argument
     * */
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
