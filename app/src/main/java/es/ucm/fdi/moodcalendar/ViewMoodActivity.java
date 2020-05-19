package es.ucm.fdi.moodcalendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewMoodActivity extends AppCompatActivity {
    private static final String TAG = "ViewMoodActivity";
    private static final String INTENT_EXTRA_VIEW = "calItemView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mood);

        Intent given = getIntent();

        String[] parameters = given.getStringExtra(INTENT_EXTRA_VIEW).split("&");

        ((TextView)findViewById(R.id.moodSelected)).setText(parameters[0]);
        ((TextView)findViewById(R.id.logComments)).setText(parameters[1]);
    }
}
