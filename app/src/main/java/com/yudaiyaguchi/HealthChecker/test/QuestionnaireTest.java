package com.yudaiyaguchi.HealthChecker.test;

import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yudaiyaguchi.HealthChecker.R;

public class QuestionnaireTest extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText editName, editUsername, editPassword, editTextId;
    Button submitButton, showButton, updateButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_questionnaire);
        myDb = new DatabaseHelper(this);


        editName = findViewById(R.id.name);
        editUsername = findViewById(R.id.username);
        editPassword = findViewById(R.id.password);
        editTextId = findViewById(R.id.id);

        submitButton = findViewById(R.id.submitButton);
        showButton = findViewById(R.id.showButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        addData();
        viewAll();
        updateData();
        deleteData();

    }

    public void deleteData() {
        deleteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer deletedRows = myDb.deleteData(editTextId.getText().toString());
                        if(deletedRows > 0)
                            Toast.makeText(QuestionnaireTest.this, "Data Deleted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(QuestionnaireTest.this, "Data not Deleted", Toast.LENGTH_LONG).show();

                    }
                }

        );
    }

    public void updateData() {
        updateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isUpdated = myDb.updateData(editTextId.getText().toString(), editName.getText().toString(), editUsername.getText().toString(), editPassword.getText().toString());

                        if(isUpdated == true)
                            Toast.makeText(QuestionnaireTest.this, "Data Updated", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(QuestionnaireTest.this, "Data not Updated", Toast.LENGTH_LONG).show();
                    }
                }

        );
    }

    public void addData() {
        submitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       boolean isInserted = myDb.insertData(editName.getText().toString(), editUsername.getText().toString(), editPassword.getText().toString());
                       if(isInserted == true)
                           Toast.makeText(QuestionnaireTest.this, "Data Inserted", Toast.LENGTH_LONG).show();
                       else
                           Toast.makeText(QuestionnaireTest.this, "Data not Inserted", Toast.LENGTH_LONG).show();

                    }
                }
        );

    }


    public void viewAll() {
        showButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllData();
                        if (res.getCount() == 0) {
                            // show message
                            showMessage("Error", "Nothing found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            buffer.append("Id : " + res.getString(0) + "\n");
                            buffer.append("Name : " + res.getString(1) + "\n");
                            buffer.append("Username : " + res.getString(2) + "\n");
                            buffer.append("Password : " + res.getString(3) + "\n\n");
                        }

                        // show all data
                        showMessage("Data", buffer.toString());
                    }
                }
        );
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

}
