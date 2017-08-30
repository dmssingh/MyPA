package com.dmss.mypa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by LingaBhairavi on 7/24/2017.
 */

public class PersonalAssistDbAdaptor {

    PersonalAssistDatabaseHelper personalAssistDbHelper;

    public PersonalAssistDbAdaptor(Context context) {
        personalAssistDbHelper = new PersonalAssistDatabaseHelper(context);
    }

    public long InsertArtsAndOdcEntries(int artsOrOdc, int inOrOut) {
        long id = -1;
        try {
            SQLiteDatabase database = personalAssistDbHelper.getWritableDatabase();
            ContentValues tableValues = new ContentValues();
            tableValues.put(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ARTSENTRY, artsOrOdc);
            tableValues.put(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_INENTRY, inOrOut);

            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            tableValues.put(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ENTRYDATE, now.toString());

            try {
                id = database.insert(PersonalAssistContract.PersonalAssistTimeSheet.TABLE_NAME, null, tableValues);
            } catch (Exception ex) {
                Log.i(TAG, "InsertArtsAndOdcEntries insert: " + ex.getMessage());
            }
        } catch (Exception ex) {
            Log.i(TAG, "InsertArtsAndOdcEntries: " + ex.getMessage());
        }
        return id;
    }

    public List<ArtsOdcDto> GetArtsAndOdcEntries(Date fromDate, Date toDate) {
        List<ArtsOdcDto> ArtsOdcData = new ArrayList<ArtsOdcDto>();

        try {
            SQLiteDatabase database = personalAssistDbHelper.getWritableDatabase();
            String[] columns = {PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ARTSENTRY,
                    PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ENTRYDATE,
                    PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_INENTRY
            };
            String whereClause = "CAST(strftime('%s', '" + PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ENTRYDATE + "') AS INT)>=?";
            String[] whereArg = {"date('now', 'start of day')"};
            //Cursor artsTableCursor = database.query(PersonalAssistContract.PersonalAssistTimeSheet.TABLE_NAME, columns, whereClause, whereArg, null, null, null);
            Cursor artsTableCursor = database.query(PersonalAssistContract.PersonalAssistTimeSheet.TABLE_NAME, null, null, null, null, null, null);
            while (artsTableCursor.moveToNext()) {
                ArtsOdcDto swipeData = new ArtsOdcDto();

                swipeData.id= artsTableCursor.getInt(artsTableCursor.getColumnIndex(PersonalAssistContract.PersonalAssistTimeSheet._ID));

                int entryTypeFlag = artsTableCursor.getInt(artsTableCursor.getColumnIndex(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ARTSENTRY));
                swipeData.ArtsOrOdc = entryTypeFlag == 1 ? ArtsOdcDto.ArtsEntry : ArtsOdcDto.OdcEntry;

                entryTypeFlag = artsTableCursor.getInt(artsTableCursor.getColumnIndex(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_INENTRY));
                swipeData.SwipeInOrOut = entryTypeFlag == 1 ? ArtsOdcDto.InEntry : ArtsOdcDto.OutEntry;

                swipeData.SwipeDateString = artsTableCursor.getString(artsTableCursor.getColumnIndex(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ENTRYDATE));

                boolean reqDatesEntry = false;
                if (swipeData.SwipeDateString != null) {
                    try {
                        DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy");
                        swipeData.SwipeDate = df.parse(swipeData.SwipeDateString);

                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                        Date startDate = dateFormatter.parse(dateFormatter.format(fromDate));
                        Date endDate = dateFormatter.parse(dateFormatter.format(toDate));
                        reqDatesEntry = (swipeData.SwipeDate.compareTo(startDate) >= 0 && swipeData.SwipeDate.compareTo(endDate) < 0);
                    } catch (ParseException pe) {
                        Log.i(TAG, "GetArtsAndOdcEntries - parse date str: " + pe.getMessage());
                    }
                }
                if (reqDatesEntry) {
                    ArtsOdcData.add(swipeData);
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, "GetArtsAndOdcEntries: " + ex.getMessage());
        }
        return ArtsOdcData;
    }

    public long InsertExpenseEntry(String amount, String payMode, String description) {
        SQLiteDatabase database = personalAssistDbHelper.getWritableDatabase();
        ContentValues tableValues = new ContentValues();
        tableValues.put(PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_AMOUNT, Integer.parseInt(amount));
        tableValues.put(PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_PAYMODE, payMode);
        tableValues.put(PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_Description, description);

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        tableValues.put(PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_ENTRYDATE, now.toString());

        long id = -1;
        try {
            id = database.insert(PersonalAssistContract.PersonalAssistDailyExpense.TABLE_NAME, null, tableValues);
        } catch (Exception ex) {
            Log.i(TAG, "InsertExpenseEntry: " + ex.getMessage());
        }
        return id;
    }

    public List<ExpanseDto> GetExpenseEntries(Date fromDate, Date toDate) {
        SQLiteDatabase database = personalAssistDbHelper.getWritableDatabase();
        String[] columns = {PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_AMOUNT,
                PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_Description,
                PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_PAYMODE,
                PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_ENTRYDATE
        };

        //database.execSQL(PersonalAssistContract.SQL_ADD_COLUMS_ARRAY[0]);

        List<ExpanseDto> ExpanseData = new ArrayList<ExpanseDto>();

        try {
            Cursor artsTableCursor = database.query(PersonalAssistContract.PersonalAssistDailyExpense.TABLE_NAME, columns, null, null, null, null, null);
            while (artsTableCursor.moveToNext()) {
                ExpanseDto expEntry = new ExpanseDto();

                expEntry.Amount = artsTableCursor.getInt(artsTableCursor.getColumnIndex(PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_AMOUNT));

                expEntry.PayMode = artsTableCursor.getString(artsTableCursor.getColumnIndex(PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_PAYMODE));

                expEntry.ExpenseDateString = artsTableCursor.getString(artsTableCursor.getColumnIndex(PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_ENTRYDATE));
                expEntry.Description = artsTableCursor.getString(artsTableCursor.getColumnIndex(PersonalAssistContract.PersonalAssistDailyExpense.COLUMN_NAME_Description));

                boolean reqDatesEntry = false;
                if (expEntry.ExpenseDateString != null) {
                    try {
                        DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy");
                        expEntry.ExpenseDate = df.parse(expEntry.ExpenseDateString);

                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                        Date startDate = dateFormatter.parse(dateFormatter.format(fromDate));
                        Date endDate = dateFormatter.parse(dateFormatter.format(toDate));
                        reqDatesEntry = (expEntry.ExpenseDate.compareTo(startDate) >= 0 && expEntry.ExpenseDate.compareTo(startDate) < 0);
                    } catch (ParseException pe) {
                        Log.i(TAG, "GetExpenseEntries - parse date str: " + pe.getMessage());
                    }
                }
                if (reqDatesEntry) {
                    ExpanseData.add(expEntry);
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, "GetExpenseEntries: " + ex.getMessage());
        }
        return ExpanseData;
    }

    public void exportExpenseTable(Context context, String tableName) {

        //File dbFile=getDatabasePath("MyDBName.db");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("ddMMyyyy_kkmm");
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        String fileName = tableName + dateFormatter.format(now) + ".csv";

//        File file = new File(Environment.DIRECTORY_DOWNLOADS+"/", fileName);
//        file.getParentFile().mkdirs();
        File file = new File(getDownloadStorageDir("PATracker"), fileName);

        //File file = new File(exportDir, fileName);
        //file = new File(exportDir, PersonalAssistContract.PersonalAssistDailyExpense.TABLE_NAME + ".csv");
        try {
            file.createNewFile();
            //file =File.createTempFile(fileName, null, context.getCacheDir());

            CSVWriter csvWrite;
            csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = personalAssistDbHelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM " + tableName, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            int colCount = curCSV.getColumnCount();
            String arrStr[] = new String[colCount];
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                for (int i = 0; i < colCount; i++) {
                    arrStr[i] = curCSV.getString(i);
                }
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("exportExpenseTable", sqlEx.getMessage(), sqlEx);
        }
    }

    public File getDownloadStorageDir(String folderName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), folderName);
        if (!file.mkdirs()) {
            Log.e("getAlbumStorageDir", "Directory not created");
        }
        return file;
    }

    public void UpdateTimeSheet(ArtsOdcDto swipeData) {
        SQLiteDatabase database = personalAssistDbHelper.getWritableDatabase();

        ContentValues data = new ContentValues();
        data.put(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ENTRYDATE, swipeData.SwipeDateString);
		data.put(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ARTSENTRY, swipeData.ArtsOrOdc);
		data.put(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_INENTRY, swipeData.SwipeInOrOut);
        database.update(PersonalAssistContract.PersonalAssistTimeSheet.TABLE_NAME, data, "_id=" + swipeData.id, null);
    }

    public ArtsOdcDto getEntryDate(int id) {
        Cursor cursor = null;
        String date = "";
        try {
            SQLiteDatabase database = personalAssistDbHelper.getWritableDatabase();
            cursor = database.rawQuery("SELECT " + PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ENTRYDATE
                    + " FROM " + PersonalAssistContract.PersonalAssistTimeSheet.TABLE_NAME + " WHERE _id=?", new String[]{id + ""});
            
			ArtsOdcDto swipeData = new ArtsOdcDto();
			if (cursor.getCount() > 0) {
                cursor.moveToFirst();
				
				int entryTypeFlag = artsTableCursor.getInt(artsTableCursor.getColumnIndex(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ARTSENTRY));
                swipeData.ArtsOrOdc = entryTypeFlag == 1 ? ArtsOdcDto.ArtsEntry : ArtsOdcDto.OdcEntry;

                entryTypeFlag = artsTableCursor.getInt(artsTableCursor.getColumnIndex(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_INENTRY));
                swipeData.SwipeInOrOut = entryTypeFlag == 1 ? ArtsOdcDto.InEntry : ArtsOdcDto.OutEntry;

                swipeData.SwipeDateString = cursor.getString(cursor.getColumnIndex(PersonalAssistContract.PersonalAssistTimeSheet.COLUMN_NAME_ENTRYDATE));
            }
            return swipeData;
        } finally {
            cursor.close();
        }
    }
}
