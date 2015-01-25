package com.example.talla_000.handnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by talla_000 on 11/16/2014.
 */
public class NotesDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "NOTES.db";
    private static final int DATABASE_VERSION = 1;
    public static final String NOTES_TABLE_NAME = "TBL_NOTES";
    public static final String NOTES_NAME_TITLE = "NotesName";
    public static final String NOTES_CONTENT_TITLE = "NotesContent";
    private static final String SPACE_SEP = " ";

    private static final String NOTES_TABLE_CREATE =
            "CREATE TABLE " + NOTES_TABLE_NAME + "(" +
                    NOTES_NAME_TITLE + " TEXT PRIMARY KEY," +
                    NOTES_CONTENT_TITLE + SPACE_SEP + "blob" +" );";


    NotesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("NotesDBHelper","Creating the Note DB TABLE");
        db.execSQL(NOTES_TABLE_CREATE);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        onCreate(db);
    }


}