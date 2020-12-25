package com.example.flower;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.flower.model.Diary;

import java.util.ArrayList;
import java.util.List;

public class Database {
    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;

    private static final String[] columns = {
            DiaryDatabase.ID,
            DiaryDatabase.CONTENT,
            DiaryDatabase.TIME,
            DiaryDatabase.MODE
    };



    public Database(){

    }

    public Database(Context mContext ){
        dbHandler = new DiaryDatabase(mContext);
    }

    public void open(){
        db = dbHandler.getWritableDatabase();
    }

    public void close(){
        dbHandler.close();
    }

    public Diary addDiary(Diary diary){
        //add a diary object to database
        ContentValues contentValues = new ContentValues();
        contentValues.put(DiaryDatabase.CONTENT, diary.getContent());
        contentValues.put(DiaryDatabase.TIME, diary.getTime());
        contentValues.put(DiaryDatabase.MODE, diary.getTag());
        long insertId = db.insert(DiaryDatabase.TABLE_NAME, null, contentValues);
        diary.setId(insertId);
        return diary;
    }
    public Diary getDiary(long id){
        //get a diary from database using cursor index
        Cursor cursor = db.query(DiaryDatabase.TABLE_NAME,columns,DiaryDatabase.ID + "=?",
                new String[]{String.valueOf(id)},null,null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Diary d = new Diary(cursor.getString(1),cursor.getString(2), cursor.getInt(3));
        return d;
    }

    public List<Diary> getAllDiaries(){
        Cursor cursor = db.query(DiaryDatabase.TABLE_NAME,columns,null,null,null, null, null);

        List<Diary> diaries = new ArrayList<>();
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                Diary diary = new Diary();
                diary.setId(cursor.getLong(cursor.getColumnIndex(DiaryDatabase.ID)));
                diary.setContent(cursor.getString(cursor.getColumnIndex(DiaryDatabase.CONTENT)));
                diary.setTime(cursor.getString(cursor.getColumnIndex(DiaryDatabase.TIME)));
                diary.setTag(cursor.getInt(cursor.getColumnIndex(DiaryDatabase.MODE)));
                diaries.add(diary);
            }
        }
        return diaries;
    }

    public int updateDiary(Diary diary) {
        //update the info of an existing note
        ContentValues values = new ContentValues();
        values.put(DiaryDatabase.CONTENT, diary.getContent());
        values.put(DiaryDatabase.TIME, diary.getTime());
        values.put(DiaryDatabase.MODE, diary.getTag());
        // updating row
        return db.update(DiaryDatabase.TABLE_NAME, values,
                DiaryDatabase.ID + "=?",new String[] { String.valueOf(diary.getId())});
    }

    public void removeDiary(Diary diary) {
        //remove a note according to ID value
        db.delete(DiaryDatabase.TABLE_NAME, DiaryDatabase.ID + "=" + diary.getId(), null);
    }


}
