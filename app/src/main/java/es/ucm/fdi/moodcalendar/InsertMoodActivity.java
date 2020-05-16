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

public class InsertMoodActivity extends AppCompatActivity {
    private static final String INTENT_EXTRA = "calItem";
    private static final String INTENT_REPLY = "reply";
    private RadioGroup group;
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

        responseBuilder.append(comm.isEmpty()?"_": comm);

        return responseBuilder.toString();
    }
}
