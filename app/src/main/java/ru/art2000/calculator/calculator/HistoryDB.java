package ru.art2000.calculator.calculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDB extends SQLiteOpenHelper {

    HistoryDB(Context context) {
        super(context, "CalculationHistory.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table history ("
                + "id integer primary key autoincrement,"
                + "expression text,"
                + "result text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS history");
        onCreate(db);
    }

    static void recreateDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS history");
        db.execSQL("create table history ("
                + "id integer primary key autoincrement,"
                + "expression text,"
                + "result text" + ");");
    }

    void fixIDs(SQLiteDatabase db, int del){
        ContentValues updatedValues = new ContentValues();
        for (int i = del; i <= lastID(db); i++){
            updatedValues.clear();
            updatedValues.put("id", i-1);
            db.update("history", updatedValues, "id=" + i, null);
        }
    }

    void nextId(SQLiteDatabase db){
        Cursor c = db.query("sqlite_sequence", null, null, null, null, null, null);
        Cursor cc = db.query("history", null, null, null, null, null, null);
        ContentValues cv = new ContentValues();

        if (c.moveToLast() && cc.moveToLast()){
            cv.put("seq", cc.getInt(cc.getColumnIndex("id")));
            db.update("sqlite_sequence", cv, "name = ?", new  String[] {"history"});
        }
        if (getSize() == 0){
            cv.put("seq", 0);
            db.update("sqlite_sequence", cv, "name = ?", new  String[] {"history"});
        }
        c.close();
        cc.close();
    }

    private int lastID(SQLiteDatabase db){
        Cursor cc = db.query("history", null, null, null, null, null, null);
        int last = 0;
        if (cc.moveToLast())
            last = cc.getInt(cc.getColumnIndex("id"));
        cc.close();
        return last;
    }

    int getSize(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cc = db.query("history", null, null, null, null, null, null);
        int ret = 0;
        if (cc.moveToLast())
            ret = cc.getInt(cc.getColumnIndex("id"));
        cc.close();
        return ret;
    }
}