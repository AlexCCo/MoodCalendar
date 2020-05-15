package es.ucm.fdi.moodcalendar.customViewTesting;

import android.app.Instrumentation;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import es.ucm.fdi.moodcalendar.MainActivity;
import es.ucm.fdi.moodcalendar.customView.CalendarView;
import es.ucm.fdi.moodcalendar.dataModel.DateWithBackground;

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
        List<DateWithBackground> result = calendarView.obtainCalendarDataUNIT_TEST();

        Assert.assertNotNull(result);
    }
}
