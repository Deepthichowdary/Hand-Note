package com.example.talla_000.handnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by talla_000 on 11/16/2014.
 */
public class NotesDAO {

    private SQLiteDatabase database;
    private NotesDBHelper dbHelper;
    private String[] allColumns = { NotesDBHelper.NOTES_NAME_TITLE,
            NotesDBHelper.NOTES_CONTENT_TITLE };
    public NotesDAO(Context context) {
        dbHelper = new NotesDBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void deleteNotes(String notes) {
        //long id = notes.getId();
        System.out.println("Notes deleted with Name: " + notes);
        try {
            String selectString = "SELECT * FROM " + NotesDBHelper.NOTES_TABLE_NAME
                    + " WHERE " + NotesDBHelper.NOTES_NAME_TITLE + " =? ";
            //Cursor cursor = database.rawQuery(selectString, new String[] {"MyNote"}); //add the String your searching by here
            //context.getDatabasePath(DataBaseHelper.dbName);
            database.delete(NotesDBHelper.NOTES_TABLE_NAME, NotesDBHelper.NOTES_NAME_TITLE
                    + "=" + "MyNote", null);
        } catch (SQLiteException e){

        }
    }

    public Notes getNotes(String note) {
        Notes notes = new Notes();
        String selectString = "SELECT * FROM " + NotesDBHelper.NOTES_TABLE_NAME;
        Cursor cursor = database.rawQuery(selectString, new String[]{}); //add the String your searching by here
        if (cursor.moveToFirst()) {
            notes = cursorToNotes(cursor);

        }
        cursor.close();
        return notes;
    }
    public void createNotes(Notes notes) {
        ContentValues values = new ContentValues();
        values.put(NotesDBHelper.NOTES_NAME_TITLE, notes.getName());
        values.put(NotesDBHelper.NOTES_CONTENT_TITLE, notes.getContent());
        String selectString = "SELECT * FROM " + NotesDBHelper.NOTES_TABLE_NAME
                + " WHERE " + NotesDBHelper.NOTES_NAME_TITLE + " =? ";
        Cursor cursor = database.rawQuery(selectString, new String[] {"MyNote"}); //add the String your searching by here

        if(!cursor.moveToFirst()){
            // Notes Doesn't exist so insert it
            long insertId = database.insert(NotesDBHelper.NOTES_TABLE_NAME, null,
                    values);
            //Log.d("NotesDAO","New Record is inserted Successfully at :"+ insertId);
            System.out.print("Id:"+insertId);

        }
        cursor = database.rawQuery(selectString, new String[] {"MyNote"});
        cursor.moveToFirst();
        Notes newNotes = cursorToNotes(cursor);
        cursor.close();

    }

    private Notes cursorToNotes(Cursor cursor) {
        Notes notes = new Notes();

        if(cursor!=null && cursor.getColumnCount()>0) {
            notes.setName(cursor.getString(cursor.getColumnIndex("NotesName")));
            notes.setContent(cursor.getString(cursor.getColumnIndex("NotesContent")));
        }
        cursor.close();
        return notes;
    }
}
