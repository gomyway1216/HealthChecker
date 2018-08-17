package com.yudaiyaguchi.HealthChecker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Hashtable;
import java.util.Map;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseAssetOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * From database, return pair of group name and array of question IDs
     *
     * @return pair of group name and array of question IDs
     */
    public Map<String,int[]> getQuestionIds() {
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM questionnaire", null);
            Map<String,int[]> questionIds = new Hashtable<String, int[]>();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String groupName = cursor.getString(1);
                    String[] temp = cursor.getString(2).split(":");
                    int[] ids = new int[temp.length];
                    for(int i = 0; i < temp.length; i++)
                        ids[i] = Integer.parseInt(temp[i]);

                    questionIds.put(groupName, ids);
                    cursor.moveToNext();
                }
            }
            return questionIds;
        } catch (Exception e) {
            Log.d("Error!!!", e.getMessage());
            return null;
        }
    }

    /**
     * From database, return ID and corresponding language
     *
     * @return ID and corresponding language
     */
    public Map<Integer,String> getLanguage() {
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM language", null);
            Map<Integer,String> languages = new Hashtable<Integer, String>();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    languages.put(cursor.getInt(0), cursor.getString(1));
                    cursor.moveToNext();
                }
            }
            return languages;
        } catch(Exception e) {
            Log.d("Error!!!", e.getMessage());
            return null;
        }
    }

    /**
     * From database, based on ID of question, return question ID and list ID
     *
     * @param questionId : ID of question
     * @return question ID and list ID
     */
    public int[] getQuestion(int questionId) {
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM question "
                + "WHERE ID=?", new String[]{String.valueOf(questionId)});
            cursor.moveToFirst();
            int typeId = cursor.getInt(2);
            int listId = cursor.getInt(3);
            int[] temp = new int[]{typeId, listId};
            cursor.close();
            return temp;

        } catch(Exception e) {
            return null;
        }
    }

    /**
     * From database, based on question type ID, return getQuestionType
     * This might be unnecessary, but it is better to have data base explains what is the question type
     * The question type is 1: Integer input, 2: String input, 3: Radio button, 4: check box
     *
     * @param questionTypeId : ID of question type
     * @return question type
     */
    public int getQuestionType(int questionTypeId) {
        // return the typeNumber
        try {
            Cursor cursor = database.rawQuery("SELECT TYPENUMBER FROM questiontype " +
                "WHERE ID=?", new String[] {String.valueOf(questionTypeId)});
            cursor.moveToFirst();
            int temp = cursor.getInt(0);
            cursor.close();
            return temp;
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * From database, based on question ID and language ID, return text of quesiton
     *
     * @param questionId : ID of question
     * @param languageId : ID of language
     * @return question text
     */
    public String getQuestionText(int questionId, int languageId) {
        try {
            Cursor cursor = database.rawQuery("SELECT TEXT FROM questiontext " +
                            "WHERE QUESTIONID= ? AND LANGUAGEID=?",
                    new String[] {String.valueOf(questionId), String.valueOf(languageId)});
            cursor.moveToFirst();
            Log.d("cursor", cursor.getString(0));
            String temp = cursor.getString(0);
            cursor.close();
            return temp;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * From database, based on list ID, return array of ID, value, and List ID
     * When it is getting data, it is sorted by primary key that specify the order because
     * database can be disorganized by putting so many data
     *
     * @param listId : ID of List
     * @return array of ID, value, and List ID
     */
    public int[][] getAlternative(int listId) {
        // return query(alternativeId, value, order)
        try {
            String sql_query = "SELECT * FROM alternative WHERE LISTID=" + listId + " ORDER BY PRIMARYORDER ASC";
            Cursor cursor = database.rawQuery(sql_query, null);
            cursor.moveToFirst();

            int[][] query = new int[cursor.getCount()][3];
            int index = 0;
            if(cursor.moveToFirst()) {
                do {
                    query[index][0] = cursor.getInt(0);
                    query[index][1] = cursor.getInt(2);
                    query[index][2] = cursor.getInt(3);
                    index++;
                } while (cursor.moveToNext());
            }

            return query;
        } catch(Exception e) {
            Log.d("Error!!!", e.getMessage());
            return null;
        }
    }


    /**
     * From database, based on alternative ID and language ID return list of alternative texts
     *
     * @param altId : alternative ID
     * @param languageId : language ID
     * @return list of alternative texts
     */
    public String[] getAlternativeText(int[] altId, int languageId) {
        try {
            String[] choices = new String[altId.length];
            for(int i = 0; i < altId.length; i++) {
                Cursor cursor = database.rawQuery("SELECT TEXT FROM alternativetext " +
                                "WHERE ALTERNATIVEID=? AND LANGUAGEID=?",
                        new String[] {String.valueOf(altId[i]), String.valueOf(languageId)});
                cursor.moveToFirst();
                choices[i] = cursor.getString(0);
                cursor.close();
            }
            return choices;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * By using date that record is created, name of the question group, question ids,
     * answers, values, write to database
     *
     * @param formattedDate : formatted date
     * @param groupName : name of the group chosen
     * @param questionIds IDs of question
     * @param ans : answers
     * @param valueArray : values
     */
    public void recordAnswer(String formattedDate, String groupName, int[] questionIds, String[] ans, int[] valueArray) {
        Log.d("inside of recordAnswer",  questionIds.length + " ");
        ContentValues contentValues = new ContentValues();
        final String TABLE_NAME = "answers";

        final String COL_1 = "ID";
        final String COL_2 = "DATE";
        final String COL_3 = "GROUPNAME";
        final String COL_4 = "QUESTIONID";
        final String COL_5 = "ANSWER";

        for(int i = 0; i < questionIds.length; i++) {
            // check whether the question type is 1 & 2 or 3 & 4.
            // 1 & 2 requires answer and 3 & 4 needs chosen value.
            if(ans[i] == null)
            database.execSQL("INSERT INTO answers(DATE, GROUPNAME, QUESTIONID, ANSWER) VALUES(?,?,?,?)",
                    new String[]{formattedDate, groupName, String.valueOf(questionIds[i]), String.valueOf(valueArray[i])});
            else
                database.execSQL("INSERT INTO answers(DATE, GROUPNAME, QUESTIONID, ANSWER) VALUES(?,?,?,?)",
                        new String[]{formattedDate, groupName, String.valueOf(questionIds[i]), String.valueOf(ans[i])});
        }
    }
}