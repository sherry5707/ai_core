package com.kinstalk.her.voip.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by siqing on 17/6/8.
 */

public class RecordSQLiteOpenHelper extends SQLiteOpenHelper {


    public RecordSQLiteOpenHelper(Context context) {
        this(context, RecordMetaDate.DB_NAME, null, RecordMetaDate.DB_VERSION);
    }

    private RecordSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecordMetaDate.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RecordMetaDate.SQL_DELETE);
    }
}
