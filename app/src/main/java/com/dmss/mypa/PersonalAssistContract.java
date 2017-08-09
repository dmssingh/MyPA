package com.dmss.mypa;

import android.provider.BaseColumns;

/**
 * Created by LingaBhairavi on 7/18/2017.
 */

public final class PersonalAssistContract {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PersonalAssist.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    public static final String[] SQL_CREATE_TABLE_ARRAY = {
            PersonalAssistTimeSheet.SQL_CREATE_TimeSheetTable,
            PersonalAssistDailyExpense.SQL_CREATE_DailyExpTable
    };

    public static final String[] SQL_DELETE_TABLE_ARRAY = {
            PersonalAssistTimeSheet.SQL_DELETE_TimeSheetTable,
            PersonalAssistDailyExpense.SQL_DELETE_DailyExpTable
    };

    public static final String[] SQL_ADD_COLUMS_ARRAY = {
            PersonalAssistDailyExpense.SQL_AddColmn_DailyExpTable
    };
    private PersonalAssistContract() {
    }

    public static class PersonalAssistTimeSheet implements BaseColumns {
        private PersonalAssistTimeSheet() {
        }

        public static final String TABLE_NAME = "TimeSheet";
        public static final String COLUMN_NAME_ENTRYDATE = "EntryDate";
        public static final String COLUMN_NAME_INENTRY = "InEntry";
        public static final String COLUMN_NAME_ARTSENTRY = "ArtsEntry";

        private static final String SQL_CREATE_TimeSheetTable =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_NAME_ENTRYDATE + " TEXT, " +
                        COLUMN_NAME_INENTRY + " INTEGER, " +
                        COLUMN_NAME_ARTSENTRY + " INTEGER)";

        private static final String SQL_DELETE_TimeSheetTable =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class PersonalAssistDailyExpense implements BaseColumns {
        public static final String TABLE_NAME = "DailyExpense";
        public static final String COLUMN_NAME_ENTRYDATE = "EntryDate";
        public static final String COLUMN_NAME_AMOUNT = "Amount";
        public static final String COLUMN_NAME_PAYMODE = "PayMode";
        public static final String COLUMN_NAME_Description = "Description";

        private static final String SQL_CREATE_DailyExpTable =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_NAME_ENTRYDATE + " TEXT, " +
                        COLUMN_NAME_AMOUNT + " INTEGER, " +
                        COLUMN_NAME_PAYMODE + " TEXT, " +
                        COLUMN_NAME_Description + " TEXT)";

        private static final String SQL_DELETE_DailyExpTable =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        private static final String SQL_AddColmn_DailyExpTable =
                "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_NAME_Description + " TEXT";

        private PersonalAssistDailyExpense() {
        }
    }

}
