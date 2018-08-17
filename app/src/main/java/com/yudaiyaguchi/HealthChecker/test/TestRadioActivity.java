package com.yudaiyaguchi.HealthChecker.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.yudaiyaguchi.HealthChecker.R;

public class TestRadioActivity extends AppCompatActivity {
    RadioGroup rg;
    RelativeLayout rl;
    RadioButton rb1,rb2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_radio);

        rg = new RadioGroup(this);
        rl = findViewById(R.id.relativeLayout);
        rb1 = new RadioButton(this);
        rb2 = new RadioButton(this);

        rb1.setText("Male");
        rb2.setText("Female");
        rg.addView(rb1);
        rg.addView(rb2);
        rg.setOrientation(RadioGroup.HORIZONTAL);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin =150;
        params.topMargin = 100;

        rg.setLayoutParams(params);
        rl.addView(rg);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                Toast.makeText(getApplicationContext(),radioButton.getText(),Toast.LENGTH_LONG).show();
            }
        });
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_practice);
//
//        final EditText editText=(EditText)findViewById(R.id.et_no);
//
//
//        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                int number=Integer.parseInt(editText.getText().toString());
//                addRadioButtons(number);
//            }
//        });
//    }
//
//
//    public void addRadioButtons(int number) {
//        for (int row = 0; row < 1; row++) {
//            RadioGroup ll = new RadioGroup(this);
//            ll.setOrientation(LinearLayout.HORIZONTAL);
//
//            for (int i = 1; i <= number; i++) {
//                RadioButton rdbtn = new RadioButton(this);
//                rdbtn.setId((row * 2) + i);
//                rdbtn.setText("Radio " + rdbtn.getId());
//                ll.addView(rdbtn);
//            }
//            ((ViewGroup) findViewById(R.id.radiogroup)).addView(ll);
//        }
//    }
}