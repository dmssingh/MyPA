package com.dmss.mypa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LingaBhairavi on 7/19/2017.
 */

public class PersonalAssistDatabaseHelper extends SQLiteOpenHelper {
    public PersonalAssistDatabaseHelper(Context context) {
        super(context, PersonalAssistContract.DATABASE_NAME, null, PersonalAssistContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (String createStatement : PersonalAssistContract.SQL_CREATE_TABLE_ARRAY) {
            sqLiteDatabase.execSQL( createStatement);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        for (String createStatement : PersonalAssistContract.SQL_CREATE_TABLE_ARRAY) {
            sqLiteDatabase.execSQL( createStatement);
        }
        onCreate(sqLiteDatabase);
    }
}
