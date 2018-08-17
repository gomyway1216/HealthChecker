package com.yudaiyaguchi.HealthChecker.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.yudaiyaguchi.HealthChecker.R;
import com.yudaiyaguchi.HealthChecker.Settings.QuizUtils;
import com.yudaiyaguchi.HealthChecker.database.DatabaseAccess;
import com.yudaiyaguchi.HealthChecker.database.DatabaseWriteHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;



/**
 * This Activity is called after user finishes all the questions.
 * This Activity is responsible to save data from QuizUtils to database.
 */
public class AfterQuestionActivity extends AppCompatActivity {
    DatabaseWriteHelper myDb;

    /**
     * Save data from QuizUtils to database.
     * @param savedInstanceState : has settings
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_question);
        // get access to database
        myDb = new DatabaseWriteHelper(this);

        // store the data to the database
        // show the result to make sure it is woking correctly
        // get all the stored data from quizUtils and save it to database
        QuizUtils quizUtils = QuizUtils.getInstance();
        int[] ids = quizUtils.getQuestionIds();

        String[] answers = quizUtils.getAnswerArray();
        int[] values = quizUtils.getValueArray();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        String formattedDate = df.format(c);
        String groupName = quizUtils.getGroupName();

        // insert data to SQL
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        databaseAccess.recordAnswer(formattedDate, groupName, ids, answers, values);

        // Go back to home
        final Button button = findViewById(R.id.backtohome);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goHomeIntent = new Intent(AfterQuestionActivity.this, WelcomeActivity.class);
                startActivity(goHomeIntent);
            }
        });
    }
}
