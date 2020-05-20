package es.ucm.fdi.moodcalendar.customViewTesting;

import android.app.Instrumentation;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.ucm.fdi.moodcalendar.customView.CalendarView;
import es.ucm.fdi.moodcalendar.dataModel.entities.DateWithBackground;

/**
 * @see <a href="https://developer.android.com/training/testing/junit-rules">junit-rules</a>
 * @see <a href="https://developer.android.com/training/testing/unit-testing/instrumented-unit-tests">junit for instrumented test</a>
 * @see <a href="https://developer.android.com/training/testing/junit-runner">info about AndroidJunit4</a>
 * @see <a href="https://developer.android.com/training/testing">Android training general testing documentation</a>
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @see <a href="https://developer.android.com/training/testing/fundamental">Testing fundamentals</a>
 * @see <a href="https://developer.android.com/training/testing/unit-testing">Unit Test</a>
 * */
@RunWith(AndroidJUnit4.class)
public class CalendarViewTest {
    private static final String TAG = "CalendarViewTest";
    private Instrumentation instrumentation;
    private CalendarView calendarView;

    @Before
    public void createCalendarViewAndInstrumentation(){
        instrumentation = InstrumentationRegistry.getInstrumentation();
        calendarView = new CalendarView(instrumentation.getTargetContext(), null);

        Assert.assertNotNull(calendarView);
        Assert.assertNotNull(instrumentation);
    }

    @Test
    public void should_return_a_calendar_list(){
        //ArrayList<DateWithBackground> result = calendarView.obtainCalendarDataUNIT_TEST();

        //Assert.assertNotNull(result);
    }

    @Test
    public void should_return_correct_name_and_value(){
        String value = calendarView.obtainCalendarActionNameUNIT_TEST();
        String expected = "first: CURRENT\tsecond: NEXT\tthird: PREVIOUS";

        Assert.assertEquals(expected, value);

        int intValue = calendarView.obtainCalendarActionValueUNIT_TEST(0);

        Assert.assertEquals(0, intValue);

        intValue = calendarView.obtainCalendarActionValueUNIT_TEST(1);

        Assert.assertEquals(1, intValue);

        intValue = calendarView.obtainCalendarActionValueUNIT_TEST(2);

        Assert.assertEquals(-1, intValue);
    }

    @Test
    public void should_return_correct_list_if_given_month(){
        Calendar instanceOf = Calendar.getInstance();
        int[] staticMonthQualifiers = new int[]{
                Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY,
                Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER,
                Calendar.NOVEMBER, Calendar.DECEMBER
        };

        for(int i = 0; i< 12; i++) {
            instanceOf.set(Calendar.MONTH, staticMonthQualifiers[i]);
            /*ArrayList<DateWithBackground> monthDates = calendarView.obtainCalendarDataGivenMonthUNIT_TEST(staticMonthQualifiers[i]);
            int days = numberOfDays(monthDates);
            Log.d(TAG, String.format("should_return_correct_list_if_given_month: month %d calendar %d | calculated %d ", i+1, instanceOf.getActualMaximum(Calendar.DAY_OF_MONTH), days));
            Assert.assertEquals(instanceOf.getActualMaximum(Calendar.DAY_OF_MONTH), days);*/
        }

    }

    private int numberOfDays(ArrayList<DateWithBackground> list){
        int numberOfDays = 0;

        for(DateWithBackground date: list){
            if (date.getDay()!=0){
                numberOfDays++;
            }
        }

        return numberOfDays;
    }
}
