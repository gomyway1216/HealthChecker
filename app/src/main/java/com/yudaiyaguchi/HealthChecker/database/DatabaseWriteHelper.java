package com.yudaiyaguchi.HealthChecker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseWriteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "questions.db";
    public static final String TABLE_NAME = "answers";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "QUESTIONID";
    public static final String COL_3 = "VALUE";
    public static final String COL_4 = "ANSWER";

    public DatabaseWriteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
//        SQLiteDatabase db = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, QUESTIONID INTEGER, VALUE INTEGER, ANSWER TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(int[] ids, String[] answers, int[] values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(COL_2, ids[0]);
//        contentValues.put(COL_3, null);
        contentValues.put(COL_4, answers[0]);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public boolean insertData2(String formattedDate, String groupName, int[] questionIds, String[] ans, int[] valueArray) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        final String COL_1 = "ID";
        final String COL_2 = "DATE";
        final String COL_3 = "GROUP";
        final String COL_4 = "QUESTIONID";
        final String COL_5 = "ANSWER";

//        for (int i = 0; i < questionIds.length; i++) {
//            Log.d("inside of for loop", i + " times ");
        contentValues.put(COL_2, formattedDate);
        contentValues.put(COL_3, groupName);
        contentValues.put(COL_4, questionIds[0]);
        if (ans == null) {
            contentValues.put(COL_5, String.valueOf(valueArray[0]));
        } else {
            contentValues.put(COL_5, ans[0]);
        }
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

    public boolean updateData(String id, String name, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, username);
        contentValues.put(COL_4, password);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {id});
    }



}
