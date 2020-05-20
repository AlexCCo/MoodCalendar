package es.ucm.fdi.moodcalendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for visualizing the information entered by a user in a
 * specific day
 * */
public class ViewMoodActivity extends AppCompatActivity {
    private static final String TAG = "ViewMoodActivity";
    private static final String INTENT_EXTRA_VIEW = "calItemView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mood);

        Intent given = getIntent();

        //it will receive data from MainActivity in the format of;
        //mood-string-associated-with-its-ordinal&thoughts
        //example:  Bad&i didn't pass my last test
        String[] parameters = given.getStringExtra(INTENT_EXTRA_VIEW).split("&");

        ((TextView)findViewById(R.id.moodSelected)).setText(parameters[0]);
        ((TextView)findViewById(R.id.logComments)).setText(parameters[1]);
    }
}
