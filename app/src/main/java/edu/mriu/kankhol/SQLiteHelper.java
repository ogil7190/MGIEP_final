package edu.mriu.kankhol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by meetesh on 10/01/18.
 */

/**
 * Helper class to create database and manage it
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "local.db";
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String session_list = "CREATE TABLE session_list (session_id varchar primary key);";
    public static final String session_data = "CREATE TABLE session_data (session_id references session_list(session_id), opt1 varchar, opt2 varchar, opt3 varchar, testing_for varchar, time_taken varchar, result varchar);";
    public static final String cum_word_analysis = "CREATE TABLE cum_word_analysis (word varchar primary key, average_right varchar default '0', average_response_time varchar default '0');";

    private SQLiteDatabase database;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(session_list);
        db.execSQL(session_data);
        db.execSQL(cum_word_analysis);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + session_list.toString());
        db.execSQL("DROP TABLE IF EXISTS " + session_data.toString());
        db.execSQL("DROP TABLE IF EXISTS " + cum_word_analysis.toString());
        onCreate(db);
    }

    public void flushData() {
        database = this.getWritableDatabase();

        database.execSQL("delete from session_list; ");
        database.execSQL("delete from session_data; ");
        database.execSQL("delete from cum_word_analysis; ");
        database.close();
    }

    public void createSession(String session_id) {
        database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("session_id", session_id);

        database.insert("session_list", null, values);

        Cursor cursor = database.rawQuery("SELECT * FROM session_list", null);
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
            }
        }
        cursor.close();
        database.close();
    }
    public void insertSessionStateElement(String session_id, SessionState state) {
        database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("session_id", session_id);
        values.put("opt1", state.getOpt1());
        values.put("opt2", state.getOpt2());
        values.put("opt3", state.getOpt3());
        values.put("testing_for", state.getSubject());
        values.put("time_taken", state.getTimeTakenInMs());
        values.put("result", state.isResult());

        database.insert("session_data", null, values);

        Cursor cursor = database.rawQuery("SELECT * FROM session_data", null);

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
            }
        }
        cursor.close();
        database.close();
    }

    public void createWordList(String[] alphabets,String[] words) {
        database = this.getWritableDatabase();

        Cursor cursor1;
        cursor1 = database.rawQuery("SELECT * FROM cum_word_analysis", null);
        if (cursor1.getCount() > 0) {
            for (int i = 0; i < cursor1.getCount(); i++) {
                cursor1.moveToNext();
            }
        }
        else {
            for(String i:alphabets) {
                database.execSQL("insert into cum_word_analysis values"+"('"+i+"','0','0');");
            }
            for(String i:words) {
                database.execSQL("insert into cum_word_analysis values"+"('"+i+"','0','0');");
            }
        }

        cursor1.close();
        database.close();
    }

    public void printPerf() {
        database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM cum_word_analysis", null);

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
            }
        }
        cursor.close();

        database.close();
    }

    public void insertCumWordAnalysis(String word, int t, float responseTime) {
        database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery("select * from cum_word_analysis where word = '"+word+"';", null);
        float average = 0;
        float response = 0;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                average = Float.parseFloat(cursor.getString(1));
                response = Float.parseFloat(cursor.getString(2));
            }
        }

        average = (average + t)/2;
        response = (responseTime + response)/2;

        database.execSQL("update cum_word_analysis set average_right='"+average+"',average_response_time='"+response+"'  where word = '"+word+"';");

        database.close();
    }

    public String getWordAnalysis(String word) {
        database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery("select average_right from cum_word_analysis where word = '"+word+"';", null);
        float average = 0;

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                average = Float.parseFloat(cursor.getString(0));
            }
        }

        database.close();

        average = average*100;

        return ((int)average)+"";
    }

    public SessionState getSessionState(String sessionId) {
        database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery("select * from session_data where session_id = '"+sessionId+"';", null);

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                SessionState s = new SessionState();
                s.setId(cursor.getString(0));
                s.setOpt1(cursor.getString(1));
                s.setOpt2(cursor.getString(2));
                s.setOpt3(cursor.getString(3));
                s.setSubject(cursor.getString(4));
                s.setTimeTakenInMs(Float.parseFloat(cursor.getString(5)));
                s.setResult(Integer.parseInt(cursor.getString(6)) ==1 ? true : false);
                database.close();
                return s;
            }
        }
        database.close();
        return null;
    }
}
