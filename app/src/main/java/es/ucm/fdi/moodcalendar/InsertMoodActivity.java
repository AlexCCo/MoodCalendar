package es.ucm.fdi.moodcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import es.ucm.fdi.moodcalendar.dataModel.MoodSelection;

/**
 * Activity that allows the user to insert how they felt during the day and
 * write some thoughts about the day.<br>
 * It will return the results to MainActivity
 *
 * @see MainActivity
 * */
public class InsertMoodActivity extends AppCompatActivity {
    /**
     * String identifying intent data from MainActivity
     * */
    private static final String INTENT_EXTRA = "calItem";
    /**
     * String identifying a response data from this activity
     * */
    private static final String INTENT_REPLY = "reply";
    /**
     * Group of items that can be checked. Those items represent
     * the available mood state a user can insert for a day
     * */
    private RadioGroup group;
    /**
     * Comments or thoughts about the current day
     * */
    private EditText comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_mood);

        group = findViewById(R.id.radioMoodBtns);
        comments = findViewById(R.id.comments);

        Button accept = findViewById(R.id.acceptBtn);
        Button goBack = findViewById(R.id.backBtn);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAnswers(true);
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAnswers(false);
            }
        });
    }

    /**
     * It will process is a user clicked accept or reject buttons.<br>
     * The "accept" button will give back a response to MainActivity, the
     * reject button will simply finish this activity and give no response
     * to MainActivity
     *
     * @param accept A boolean denoting if a user clicked accept (true) or
     *               reject (false)
     *
     * */
    private void processAnswers(boolean accept){
        if(!accept){
            setResult(RESULT_CANCELED);
            finish();
        }

        String result = getResult();
        Intent responseIntent = new Intent();
        responseIntent.putExtra(INTENT_REPLY, result);
        setResult(RESULT_OK, responseIntent);
        finish();
    }

    /**
     * Method for obtaining what information a user have inserted.<br>
     * It will construct the result as a string with the following format:<br>
     *     <pre>"YYYY-MM-DD&mood-selection-ordinal&comments"</pre>
     * <i>Example:</i> "2020-5-28&0&It was SUPER nice day"<br><br>
     *
     * The values of "YYYY-MM-DD" are provided by the MainActivity
     *
     * */
    private String getResult() {
        StringBuilder responseBuilder = new StringBuilder();
        String comm = comments.getText().toString();
        MoodSelection selection;

        switch (group.getCheckedRadioButtonId()){
            case R.id.bad: selection = MoodSelection.REALLY_SAD; break;
            case R.id.sBad: selection = MoodSelection.SAD; break;
            case R.id.sHappy: selection = MoodSelection.HAPPY; break;
            case R.id.happy: selection = MoodSelection.SO_HAPPY; break;
            default: selection = MoodSelection.NORMAL;break; //normal by default
        }

        responseBuilder.append(getIntent().getStringExtra(INTENT_EXTRA));
        responseBuilder.append("&");
        responseBuilder.append(selection.ordinal());
        responseBuilder.append("&");

        responseBuilder.append(comm);

        return responseBuilder.toString();
    }
}
