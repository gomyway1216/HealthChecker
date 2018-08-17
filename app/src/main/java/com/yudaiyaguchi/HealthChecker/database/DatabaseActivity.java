package com.yudaiyaguchi.HealthChecker.database;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yudaiyaguchi.HealthChecker.R;
import com.yudaiyaguchi.HealthChecker.activity.AfterQuestionActivity;
import com.yudaiyaguchi.HealthChecker.activity.BaseActivity;
import com.yudaiyaguchi.HealthChecker.Settings.QuizUtils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class DatabaseActivity extends BaseActivity {
//    private ListView listView;
    private int[] choiceValues;
    private boolean[] selectedChoices;
    private String answer;
    private EditText editText;
    private String qText;
    // use for speech becuase speech function can be only called once somehow
    private String qAndChoices;
    // each button first one is Id and second one is value
    private Map<Integer,Integer> radioSelected;
    private int chosenId;
    private String languagePref;

    private boolean isTTS;
    private boolean isSTT;

    private int seekBarPitchProgress;
    private int seekBarSpeedProgress;

    private TextToSpeech mTTS;
    private Button mButtonSpeak;

    private Button ttsChoicesButton;
    private String[] choices;
    private int speakingChoice = 0;
    private Thread thread1, thread2, thread3, thread4;

    private int questionTypeNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        // from the setting, user can enabele TTS and STT
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        isTTS = settings.getBoolean("tts_switch", true);
        isSTT = settings.getBoolean("stt_switch", true);

        // setting the language
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        languagePref = sharedPref.getString("language_preference", "EN");

        // made a button to speak question
        mButtonSpeak = findViewById(R.id.button_speak_question);

        // This seems not working. Some work should be done here
        seekBarPitchProgress = sharedPref.getInt("seekBarPitchProgress", 50);
        seekBarSpeedProgress = sharedPref.getInt("seekBarSpeedProgress", 50);


        // initialize mTTS
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = 0;
                    if(languagePref.equals("EN")) {
                        result = mTTS.setLanguage(Locale.getDefault());
                    } else if(languagePref.equals("PT")) {
                        Locale locPor = new Locale("pt_PT");
                        result = mTTS.setLanguage(locPor);
                    }

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        // listener for the speak question button
        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(qText);
            }
        });


        // this stores the information of questions
        // this would be updated every single time this activity is called
        QuizUtils quizUtils = QuizUtils.getInstance();

        // modify into getting language from setting
        int languageId = 0;
        if(languagePref.equals("EN")) {
            languageId = 2;
        }
        else if(languagePref.equals("PT")) {
            languageId = 1;
        }

        int quesitonId = quizUtils.getQuestionId();

        // get the access to database access class
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        // get the information regarding to each question
        int[] temp = databaseAccess.getQuestion(quesitonId);
        int questionTypeId  = temp[0];
        int listId = temp[1];
        questionTypeNumber = databaseAccess.getQuestionType(questionTypeId);
        qText = databaseAccess.getQuestionText(quesitonId, languageId);

        qAndChoices += qText + "     ";
        // second value is value in quer
        int[][] query;
        int[] altId;

        // if there are multiple choices in the question
        // this is the radio button and check box type
        if(listId != 0) {
            query = databaseAccess.getAlternative(listId);
            altId = new int[query.length];
            choiceValues = new int[query.length];
            selectedChoices = new boolean[query.length];
            for (int i = 0; i < query.length; i++) {
                altId[i] = query[i][0];
                choiceValues[i] = query[i][1];
            }
            choices = databaseAccess.getAlternativeText(altId, languageId);
            if (questionTypeNumber == 3) {
                thread3 = new Thread() {
                    @Override
                    public void run() {
                        try {
                            if (isTTS) {
                                sleep(1500);
                                if(languagePref.equals("EN"))
                                    speak("Please choose one choice after the speech and say the number");
                                else if(languagePref.equals("PT"))
                                    speak("Após a gravação, selecione uma opção dizendo o número correspondente");

                                while (mTTS.isSpeaking())
                                    sleep(100);
                                sleep(500);
                                speak(qText);
                                sleep(500);
                                for (int i = 0; i < choices.length; i++) {
                                    final int index = i;
                                    sleep(1000);
                                    while (mTTS.isSpeaking())
                                        sleep(500);
                                    speak(i + 1 + "");
                                    while (mTTS.isSpeaking())
                                        sleep(500);
                                    sleep(500);
                                    speak(choices[index]);
                                }

                                while (mTTS.isSpeaking())
                                    sleep(500);
                                if(isSTT)
                                    getSpeechInputSingle();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread3.start();
            } else {
                thread4 = new Thread() {
                    @Override
                    public void run() {
                        try {
                            if (isTTS) {
                                sleep(1500);
                                if(languagePref.equals("EN"))
                                    speak("Please choose as many as you want");
                                else if(languagePref.equals("PT"))
                                    speak("Pode selecionar mais do que uma opção");

                                while (mTTS.isSpeaking())
                                    sleep(100);
                                speak(qText);
                                while (mTTS.isSpeaking())
                                    sleep(100);

                                if(languagePref.equals("EN"))
                                    speak("Please answer yes or no after question is read");
                                else if(languagePref.equals("PT"))
                                    speak("Após a gravação, por favor responda sim ou não");

                                sleep(500);
                                for (int i = 0; i < choices.length; i++) {
                                    final int index = i;
                                    sleep(1000);
                                    while (mTTS.isSpeaking())
                                        sleep(500);
                                    speak(i + 1 + "");
                                    while (mTTS.isSpeaking())
                                        sleep(500);
                                    sleep(500);
                                    speak(choices[index]);
                                    while (mTTS.isSpeaking())
                                        sleep(1000);
                                    speakingChoice = i + 1;
                                    if(isSTT) {
                                        getSpeechInputMulti();
                                        sleep(5000);
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread4.start();
            }
        } else {
            // question type is number input
            if(questionTypeNumber == 1) {
                query = null;
                altId = null;
                choices = null;
                choiceValues = null;
                selectedChoices = null;

                thread1 = new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(1500);
                            speak(qText);
                            if (isSTT) {
                                while (mTTS.isSpeaking()) {
                                    sleep(500);
                                }

                                if(languagePref.equals("EN"))
                                    speak("Please say your answer");
                                else if(languagePref.equals("PT"))
                                    speak("Indique a sua resposta, dizendo-a em voz alta");

                                sleep(500);
                                while (mTTS.isSpeaking())
                                    sleep(1000);
                                getSpeechInputInteger();
                                sleep(500);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread1.start();
            } else {
                // question type is text input
                thread2 = new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(1500);
                            speak(qText);
                            if (isSTT) {
                                while (mTTS.isSpeaking()) {
                                    sleep(500);
                                }

                                if(languagePref.equals("EN"))
                                    speak("Please say your answer");
                                else if(languagePref.equals("PT"))
                                    speak("Indique a sua resposta, dizendo-a em voz alta");

                                sleep(500);
                                while (mTTS.isSpeaking())
                                    sleep(1000);
                                getSpeechInputString();
                                sleep(500);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread2.start();
            }
        }

        databaseAccess.close();

        // Layout container, to format the layout, you should modify from here
        LinearLayout linearLayout = findViewById(R.id
                .linear_quiz_container);

        TextView questionTextView = new TextView(this);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        questionTextView.setText(qText);
        questionTextView.setLayoutParams(textViewParams);
        int qViewId = 0;
        questionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);
        questionTextView.setId(qViewId);

        linearLayout.addView(questionTextView);

        // set the elements into screen
        if(questionTypeNumber == 1) {
                // integer input
            editText = new EditText(this);
            if(languagePref.equals("EN"))
                editText.setHint("type a number");
            else if(languagePref.equals("PT"))
                editText.setHint("Escreva o número");

            editText.setInputType(InputType.TYPE_CLASS_PHONE);
            int editTextId = 1;
            editText.setId(editTextId);
            linearLayout.addView(editText);

        } else if(questionTypeNumber == 2) {
            // string input
            editText = new EditText(this);
            if(languagePref.equals("EN"))
                editText.setHint("type a text");
            else if(languagePref.equals("PT"))
                editText.setHint("Escreva a resposta");

            int editTextId = 1;
            editText.setId(editTextId);
            linearLayout.addView(editText);

        } else if(questionTypeNumber == 3) {
            // radio button
            ScrollView sv = new ScrollView(this);

            RadioGroup rg = new RadioGroup(this);
            rg.setOrientation(LinearLayout.VERTICAL);

            int number = choices.length;
            for (int i = 1; i < number + 1; i++) {
                RadioButton rdbtn = new RadioButton(this);
                rdbtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 300));
                rdbtn.setId(i);
                rdbtn.setText(i + ". " + choices[i-1]);
                rg.addView(rdbtn);
//                speak(choices[i-1]);
            }
            sv.addView(rg);

            linearLayout.addView(sv);

            // set on click method
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = findViewById(checkedId);
//                    Toast.makeText(applicationContext, text, duration)
                    // getBaseContext() also works
                    chosenId = checkedId;
                    for(int i = 0; i < selectedChoices.length; i++)
                        selectedChoices[i] = false;
                    selectedChoices[checkedId-1] = true;
                    Toast.makeText(getApplicationContext(), radioButton.getText(), Toast.LENGTH_LONG).show();
                }
            });
        } else if(questionTypeNumber == 4) {
            // check box
            ScrollView sv = new ScrollView(this);
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            int number = choices.length;
            CheckBox checkBox;
            for(int i = 1; i < number + 1; i++) {
                checkBox = new CheckBox(this);
                checkBox.setId(i);
                checkBox.setText(i + ". " + choices[i-1]);
//                speak(choices[i-1]);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int checkIndex = buttonView.getId();
                        if(isChecked == true) {
                            selectedChoices[checkIndex-1] = true;
                            Toast.makeText(getApplicationContext(), buttonView.getText(), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), buttonView.getText() + "unchecked", Toast.LENGTH_LONG).show();
                            selectedChoices[checkIndex-1] = false;
                        }
                    }
                });

               ll.addView(checkBox);

            }
                sv.addView(ll);
                linearLayout.addView(sv);
        }

        // this is the layout for the previous and next button
            LinearLayout bottomLinearLayout = new LinearLayout(this);
            LinearLayout.LayoutParams buttomLinearParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            bottomLinearLayout.setGravity(Gravity.BOTTOM);
        bottomLinearLayout.setLayoutParams(buttomLinearParams);


        // repeat the activity
        Button repeatButton = new Button(this);
        if(languagePref.equals("EN"))
            repeatButton.setText("Repeat");
        else if(languagePref.equals("PT"))
            repeatButton.setText("Repetir");
        repeatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent currentQuestionIntent = new Intent(DatabaseActivity.this, DatabaseActivity.class);
                finish();
                startActivity(currentQuestionIntent);
            }
        });
        linearLayout.addView(repeatButton);

        // previous button discard the answers for this question
        Button previousButton = new Button(this);
        LinearLayout.LayoutParams previousButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
//            previousButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if(languagePref.equals("EN"))
            previousButton.setText("previous");
        else if(languagePref.equals("PT"))
            previousButton.setText("anterior");

        // go to previous question when previous button is clicked
        previousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent nextQuestionIntent = new Intent(DatabaseActivity.this, DatabaseActivity.class);
                QuizUtils quizUtils = QuizUtils.getInstance();
                quizUtils.decrementCounter();
                finish();
                startActivity(nextQuestionIntent);

            }
        });
        if(quizUtils.getCurrentQuestionCount() != 0)
            bottomLinearLayout.addView(previousButton);


        Button nextButton = new Button(this);
        LinearLayout.LayoutParams nextButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
//            nextButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if(languagePref.equals("EN"))
            nextButton.setText("next");
        else if(languagePref.equals("PT"))
            nextButton.setText("seguinte");

        // go to next question when previous button is clicked
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                QuizUtils quizUtils = QuizUtils.getInstance();
                Intent nextQuestionIntent = new Intent(DatabaseActivity.this, DatabaseActivity.class);

                // the score is based on what the user choose. this is for quetions type 3 and 4.
                // for question 3, only one choice is true and 4, there are multiple one set to be true
                if(selectedChoices != null) {
                    int score = 0;
                    for (int i = 0; i < selectedChoices.length; i++) {
                        if(selectedChoices[i] == true)
                            score += choiceValues[i];
                    }
                    quizUtils.setEachValue(score);
                } else {
                    quizUtils.setAnswer(editText.getText().toString());
                }
                // this is making sure all the running thread ends
                finish();
                if(quizUtils.isQuizEnded()) {
                    Intent endQuizIntent = new Intent(DatabaseActivity.this, AfterQuestionActivity.class);
                    startActivity(endQuizIntent);
                } else {
                    quizUtils.increaseCounter();
                    startActivity(nextQuestionIntent);
                }
            }
        });

            bottomLinearLayout.addView(nextButton);
//        linearLayout.addView(nextButton);
        linearLayout.addView(bottomLinearLayout);

    }


    // further work should be done if you want to change the voice pitch and speed
    private void speak(String text) {
        float pitch = (float) seekBarPitchProgress / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) seekBarSpeedProgress/ 50;
        if (speed < 0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        mTTS.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
    }

    // speech recognition for question type 4
    public void getSpeechInputMulti() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        if(languagePref.equals("EN")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        } else if(languagePref.equals("PT")) {
            Log.i("speech recg", "speech recg");
//            Locale locPor = new Locale("pt_PT");
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt_PT");
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }

    }

    // speech recognition for question type 3
    public void getSpeechInputSingle() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        Locale loc;
        if(languagePref.equals("EN")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        } else if(languagePref.equals("PT")) {
            Log.i("speech recg", "speech recg");
//            Locale locPor = new Locale("pt_PT");
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt_PT");
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 100);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }

    }

    // speech recognition for question type 1
    public void getSpeechInputInteger() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        Locale loc;
        if(languagePref.equals("EN")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        } else if(languagePref.equals("PT")) {
            Log.i("speech recg", "speech recg");
//            Locale locPor = new Locale("pt_PT");
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt_PT");
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 105);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }

    }

    // speech recognition for question type 2
    public void getSpeechInputString() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        Locale loc;
        if(languagePref.equals("EN")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        } else if(languagePref.equals("PT")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"pt_PT");
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 110);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    // based on the type of question, request code changes, request code is just the arbitrary number
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(), result.get(0), Toast.LENGTH_LONG).show();
                    understandSpeechMulti(result.get(0));
                }
                break;
            case 100:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(), result.get(0), Toast.LENGTH_LONG).show();
                    for(int i = 0; i < result.size(); i++) {
                        Log.d("Result print", "Result print : " + result.get(i));
                    }
                    understandSpeechSingle(result);
                }
                break;
            case 105:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(), result.get(0), Toast.LENGTH_LONG).show();
                    understandSpeechInteger(result);
                }
                break;
            case 110:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(), result.get(0), Toast.LENGTH_LONG).show();
                    understandSpeechString(result.get(0));
                }
                break;
        }
    }

    // recognizer method for question type 4
    private void understandSpeechMulti(String text) {
        Log.e("Speech", "" + text);
        String[] speech = text.split(" ");

        if (languagePref.equals("EN")){
            if (text.contains("yes")) {
                CheckBox cb = (CheckBox) findViewById(speakingChoice);
                cb.setChecked(true);
            }
        } else if(languagePref.equals("PT")) {
            if(text.contains("sim")) {
                CheckBox cb = (CheckBox) findViewById(speakingChoice);
                cb.setChecked(true);
            }
        }
    }

    private boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean tryParseDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // recognizer method for question type 3
    private void understandSpeechSingle(ArrayList<String> result) {
//            Log.d("other log", "other log");
//            Log.d("the number", "the number: " + text);
            int index = -1;
            for(int i = 0; i < result.size(); i++) {
                if(tryParseInt(result.get(i))) {
                    index = i;
                    break;
                }
            }

            Log.d("index of int", "index of int : " + index);
            if(index != -1) {
                int choice = Integer.parseInt(result.get(index));
                for(int i = 0; i < choices.length; i++) {
                    if(choice == i + 1) {
                        RadioButton rdbtn = (RadioButton) findViewById(choice);
                        rdbtn.setChecked(true);
                        break;
                    }
                }
            }
    }

    // recognizer method for question type 1
    private void understandSpeechInteger(ArrayList<String> result) {

        int index = -1;
        for(int i = 0; i < result.size(); i++) {
            if(tryParseDouble(result.get(i))) {
                index = i;
                break;
            }
        }

        if(index != -1) {
            double ansDouble = Double.parseDouble(result.get(index));
            int eTextChoice = 1;
            EditText et = (EditText) findViewById(eTextChoice);
            et.setText(String.valueOf(ansDouble));
        }


//        try {
//
//
//
//
//
//
//
//            double ansDouble = Double.parseDouble(text);
//            int eTextChoice = 1;
//            EditText et = (EditText) findViewById(eTextChoice);
//            et.setText(String.valueOf(ansDouble));
//
//            Log.d("No convertion", "No convertion ");
//
//        } catch(Exception e) {
//            Toast.makeText(getApplicationContext(), "Please Use Number!", Toast.LENGTH_LONG).show();
//            getSpeechInputInteger();
//            Log.d("Exception convertion", "Exception convertion " + e.getMessage());
//            int eTextChoice = 1;
//            EditText et = (EditText) findViewById(eTextChoice);
//            et.setText("");
//            // repeat asking
//            // I will do it later
//        }
    }

    // recognizer method for question type 2
    private void understandSpeechString(String text) {
        int eTextChoice = 1;
        EditText et = (EditText) findViewById(eTextChoice);
        et.setText(text);
    }

    // it may not be called
    @Override
    public void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
}