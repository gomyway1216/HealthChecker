package com.yudaiyaguchi.HealthChecker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.yudaiyaguchi.HealthChecker.R;
import com.yudaiyaguchi.HealthChecker.Settings.QuizUtils;
import com.yudaiyaguchi.HealthChecker.database.DatabaseAccess;
import com.yudaiyaguchi.HealthChecker.database.DatabaseActivity;

import java.util.Map;

/**
 * This activity let user choose question group
 */
public class Questionnaire extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        QuizUtils quizUtils = QuizUtils.getInstance();

        ScrollView sv = new ScrollView(this);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        // get the data from database
        Map<String, int[]> questionIds = databaseAccess.getQuestionIds();
        databaseAccess.close();


        // this is the layout connected to xml file.
        LinearLayout linearLayout = findViewById(R.id
                .container);

        // create the buttons dynamically
        int index = 1;
        for (String key : questionIds.keySet()) {
            Button lBtn = new Button(this);
            lBtn.setId(index);
            lBtn.setText(key);
            ll.addView(lBtn);
            lBtn.setOnClickListener(getOnClickGroup(lBtn, questionIds.get(key)));
            index++;
        }

        sv.addView(ll);
        linearLayout.addView(sv);
    }

    // based on the question group user choose, start activity of each question
    View.OnClickListener getOnClickGroup(final Button button, final int[] ids) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                QuizUtils quizUtils = QuizUtils.getInstance();
                quizUtils.setQuestionIds(ids);
//                quizUtils.setChosenLanguageId(languageId);
                quizUtils.setGroupName(button.getText().toString());
                quizUtils.setCurrentQuestionCount(0);
                quizUtils.initializeArray(ids.length);
                Intent qIntent = new Intent(Questionnaire.this, DatabaseActivity.class);
                startActivity(qIntent);
            }
        };
    }
}