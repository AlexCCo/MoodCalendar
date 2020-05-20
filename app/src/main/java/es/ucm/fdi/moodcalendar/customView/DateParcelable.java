package es.ucm.fdi.moodcalendar.customView;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;

/**
 * @see <a href="https://developer.android.com/guide/components/activities/parcelables-and-bundles">Parcelable and Bundles</a>
 * @see <a href="https://developer.android.com/reference/android/os/Parcelable">Parcelable documentation</a>
 * @see <a href="http://www.parcelabler.com/">Web to create a Parcelable object easier</a>
 * @see <a href="https://stackoverflow.com/questions/59453520/use-of-classloader-in-parcelable-readarraylist-in-android">Post talking about parcelable</a>
 * */
//TODO: implement support for landscape mode for our custom CalendarView
public class DateParcelable implements Parcelable {
    private List<DateWithBackground> dateList;

    public DateParcelable(){}

    public List<DateWithBackground> getDateList(){
        return dateList;
    }

    public void setDateList(List<DateWithBackground> list){
        dateList = list;
    }


    protected DateParcelable(Parcel in) {
        if (in.readByte() == 0x01) {
            dateList = new ArrayList<DateWithBackground>();
            in.readList(dateList, DateWithBackground.class.getClassLoader());
        } else {
            dateList = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (dateList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(dateList);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DateParcelable> CREATOR = new Parcelable.Creator<DateParcelable>() {
        @Override
        public DateParcelable createFromParcel(Parcel in) {
            return new DateParcelable(in);
        }

        @Override
        public DateParcelable[] newArray(int size) {
            return new DateParcelable[size];
        }
    };
}
