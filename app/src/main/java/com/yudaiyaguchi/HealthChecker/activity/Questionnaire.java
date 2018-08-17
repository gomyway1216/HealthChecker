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
 *
 */
public class Questionnaire extends BaseActivity {
    public Spinner languageSpinner;
    public int languageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
//        addLanguages();

        QuizUtils quizUtils = QuizUtils.getInstance();

        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        Map<Integer,String> languages = databaseAccess.getLanguage();
        Map<String, int[]> questionIds = databaseAccess.getQuestionIds();
        databaseAccess.close();


        quizUtils.setLanguageIds(languages);

        LinearLayout linearLayout = findViewById(R.id
                .container);

        int numOfLanguage = languages.size();
        for(int i=1; i < numOfLanguage+1; i++) {
            Button lBtn = new Button(this);
            lBtn.setId(i);
            lBtn.setText(languages.get(i));
            ll.addView(lBtn);
            lBtn.setOnClickListener(getOnClickLanguage(lBtn));
        }


        int index = numOfLanguage+1;
    for (String key : questionIds.keySet()) {
        Button lBtn = new Button(this);
        lBtn.setId(index);
        lBtn.setText(key);
        ll.addView(lBtn);
        lBtn.setOnClickListener(getOnClickLanguage(lBtn, questionIds.get(key)));
    }

    sv.addView(ll);
    linearLayout.addView(sv);

    }

    View.OnClickListener getOnClickLanguage(final Button button) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                languageId = button.getId();
                Toast.makeText(getApplicationContext(), button.getText(), Toast.LENGTH_LONG).show();

            }
        };
    }

    View.OnClickListener getOnClickLanguage(final Button button, final int[] ids) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(languageId == 0){
                    Toast.makeText(getApplicationContext(), "Please choose the" +
                            " language first.", Toast.LENGTH_LONG).show();
                    return;
                }

//                Intent questionIntent = new Intent(Questionnaire.this, DatabaseActivity.class);


                QuizUtils quizUtils = QuizUtils.getInstance();
                quizUtils.setQuestionIds(ids);
                quizUtils.setChosenLanguageId(languageId);
                quizUtils.setGroupName(button.getText().toString());
                quizUtils.setIsGoing(true);
                quizUtils.setCurrentQuestionCount(0);
                quizUtils.initializeArray(ids.length);

//                questionIntent.putExtra("questionIds", ids);
//                questionIntent.putExtra("languageId", languageId);

                Intent qIntent = new Intent(Questionnaire.this, DatabaseActivity.class);
                startActivity(qIntent);


            }
        };
    }
}
