package com.dmss.mypa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;


import static android.content.ContentValues.TAG;

public class ShowEntries extends AppCompatActivity {
    PersonalAssistDbAdaptor PaDbAdaptor;
    TextView SwipeDataTextContainer;
    //    CalendarView FromDate, ToDate;
    EditText FromDate, ToDate, ExcludeNoOfDays;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private static final String ColumSep = "  ";
	boolean ShowArtsEntries = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_entries);
        // Get the Intent that started this activity and extract the string
        //Intent intent = getIntent();
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText("Today's Entries");

        SwipeDataTextContainer = (TextView) findViewById(R.id.timeSheetData);
//        FromDate =(CalendarView) findViewById(R.id.fromDate);
//        ToDate =(CalendarView) findViewById(R.id.toDate);
//
        FromDate = (EditText) findViewById(R.id.fromDate);
        ToDate = (EditText) findViewById(R.id.toDate);
        ExcludeNoOfDays=(EditText) findViewById(R.id.excludeNoOfDays);

        Calendar calendar = Calendar.getInstance();
        Date fromDate = calendar.getTime();
        FromDate.setText(dateFormatter.format(fromDate));

        //calendar.setTime(dateFormatter.parse(dt));
        calendar.add(Calendar.DATE, 1);
        Date toDate = calendar.getTime();
        ToDate.setText(dateFormatter.format(toDate));

        PaDbAdaptor = new PersonalAssistDbAdaptor(this);

        SwipeDataTextContainer.setMovementMethod(new ScrollingMovementMethod());
        //if(MainActivity.Sho)
        ShowArtsEntries = getIntent().getExtras().getBoolean("ShowArtsEntries");
        ShowEntries();
    }

    public void ShowSwipes(View view) {
        ShowEntries();
    }
    private void ShowEntries(){
        if (ShowArtsEntries)
            ShowArtsAndOdcEntries();
        else
            ShowExpenseEntries();
    }

    private void ShowArtsAndOdcEntries() {
        Date fromDate = null, toDate = null;
        try {
            fromDate = dateFormatter.parse(FromDate.getText().toString());
            toDate = dateFormatter.parse(ToDate.getText().toString());
        } catch (ParseException pe) {
            Log.i(TAG, "ShowArtsAndOdcEntries - parse date str: " + pe.getMessage());
        }
        List<ArtsOdcDto> swipeEntries = PaDbAdaptor.GetArtsAndOdcEntries(fromDate, toDate);
        Collections.sort(swipeEntries, new ArtsOdcDtoDateComp());
        StringBuffer swipeEntryText = new StringBuffer();
        Date ArtsIn = null, ArtsOut = null, OdcIn = null, OdcOut;
        long OdcInMilliSecs = 0;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy kk:mm:ss");
        int noOfArtsIns = 0;

        for (ArtsOdcDto swipe : swipeEntries) {
            try {
                if (swipe.SwipeInOrOut.equals(ArtsOdcDto.InEntry)) {
                    if (swipe.ArtsOrOdc.equals(ArtsOdcDto.ArtsEntry)) {
                        noOfArtsIns++;
                        if (ArtsIn == null) ArtsIn = swipe.SwipeDate;
                    } else {
                        OdcIn = swipe.SwipeDate;
                    }
                } else {
                    if (swipe.ArtsOrOdc.equals(ArtsOdcDto.ArtsEntry)) {
                        ArtsOut = swipe.SwipeDate;
                    } else {
                        OdcOut = swipe.SwipeDate;
                        //OdcIn = OdcIn != null ? ArtsIn : OdcIn;
                        if (OdcOut != null && OdcIn != null) {
                            OdcInMilliSecs = OdcInMilliSecs + (OdcOut.getTime() - OdcIn.getTime());
                        }
                        OdcIn = null;
                    }
                }
            } catch (Exception ex) {
                Log.i(TAG, "ShowArtsAndOdcEntries: " + ex.getMessage());
            }

            swipeEntryText.append(String.format("%d  %s  %s  %s  %n", swipe.id,
                    dateFormatter.format(swipe.SwipeDate) ,swipe.ArtsOrOdc, swipe.SwipeInOrOut));
        }

        Calendar calendar = Calendar.getInstance();
        if (OdcIn != null) {
            OdcInMilliSecs = OdcInMilliSecs + (calendar.getTime().getTime() - OdcIn.getTime());
        }
        StringBuffer odcArtsDataText = new StringBuffer();
        odcArtsDataText.append(FormatToHHMMSS(OdcInMilliSecs, ArtsOdcDto.OdcEntry));
        odcArtsDataText.append(String.format("Actual # of Working Days %d%n", noOfArtsIns));
        int excludeNoOfDays  =0;
        if(!ExcludeNoOfDays.getText().toString().isEmpty())
        {
            excludeNoOfDays  = Integer.parseInt(ExcludeNoOfDays.getText().toString());
        }
        int effectiveWorkingDays=noOfArtsIns-excludeNoOfDays;
        odcArtsDataText.append(String.format("Effective # of Working Days (Actual - Excluded days) %d%n", effectiveWorkingDays));
        odcArtsDataText.append(OdcAverage(OdcInMilliSecs, effectiveWorkingDays));
        odcArtsDataText.append(OdcShortage(OdcInMilliSecs, effectiveWorkingDays));
        odcArtsDataText.append("\n");

        if (ArtsOut == null) ArtsOut = calendar.getTime();
        if (ArtsIn == null) odcArtsDataText.append("ARTS Entry Missing!\n");
        else
            odcArtsDataText.append(FormatToHHMMSS((ArtsOut.getTime() - ArtsIn.getTime()), ArtsOdcDto.ArtsEntry));
        odcArtsDataText.append("\n");

        SwipeDataTextContainer.setText(odcArtsDataText.toString() + swipeEntryText.toString());
    }

    private void ShowExpenseEntries() {
		Date fromDate = null, toDate = null;
        try {
            fromDate = dateFormatter.parse(FromDate.getText().toString());
            toDate = dateFormatter.parse(ToDate.getText().toString());
        } catch (ParseException pe) {
            Log.i(TAG, "ShowExpenseEntries - parse date str: " + pe.getMessage());
        }

        List<ExpanseDto> expanseEntries = PaDbAdaptor.GetExpenseEntries(fromDate, toDate);
        Collections.sort(expanseEntries, new ExpanseDtoDateComp());
        StringBuffer expEntryText = new StringBuffer();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy kk:mm:ss");
        for (ExpanseDto expense : expanseEntries) {
            expEntryText.append(String.format("%s - %s - %s - %d %n", dateFormatter.format(expense.ExpenseDate),
                    expense.PayMode, expense.Description, expense.Amount));
        }

        SwipeDataTextContainer.setText(expEntryText.toString());
    }

    private String FormatToHHMMSS(long milliSecs, String prefixText) {
        int seconds = (int) (milliSecs / 1000) % 60;
        int minutes = (int) ((milliSecs / (1000 * 60)) % 60);
        int hours = (int) (milliSecs / (1000 * 60 * 60));
        // if we need no of days then use below
        //int hours = (int) ((milliSecs / (1000 * 60 * 60)) % 24);
        String foramtedText = String.format("Total %s Time: %02d:%02d:%02d%n", prefixText, hours, minutes, seconds);

//        hours = (int) (milliSecs/(60*60*1000));
//        int hrsReminds= (int)(milliSecs%(60*60*1000));
//        minutes = (int) (hrsReminds/(60*1000));
//        int minsReminds= (int)(hrsReminds%(60*1000));
//        seconds = (int) (minsReminds/(1000));
//
//        foramtedText=String.format("%sTotal ODC Time: %02d:%02d:%02d %n", foramtedText, hours, minutes, seconds);

        return foramtedText;
    }

    private String OdcAverage(long milliSecs, int actualWorkingDays) {
        String odcAvgTxt = "";

        int idealOdcMilliSecsPerDay = (9 * (1000 * 60 * 60));
        double expectedOdcMilliSecs = actualWorkingDays * idealOdcMilliSecsPerDay;

        double odcPercentage = 0;
        if (expectedOdcMilliSecs > 0)
            odcPercentage = (milliSecs / expectedOdcMilliSecs) * 100;
        odcAvgTxt = String.format("Average OCD in %.2f%%%n", odcPercentage);
        return odcAvgTxt;
    }

    private String OdcShortage(long milliSecs, int actualWorkingDays){
        String odcShortageTxt = "";
        long expectedOdcMilliSecsPerDay = (7 * (1000 * 60 * 60)) + (42 * (1000 * 60));
        long expectedOdcMilliSecs = actualWorkingDays * expectedOdcMilliSecsPerDay;

        String shortOrExceed = "ODC Shortage";
        long shortageMilliSec = expectedOdcMilliSecs - milliSecs;
        if(milliSecs > expectedOdcMilliSecs){
            shortOrExceed = "ODC Over";
            shortageMilliSec = milliSecs - expectedOdcMilliSecs;
        }

        odcShortageTxt=FormatToHHMMSS(shortageMilliSec, shortOrExceed);
        return odcShortageTxt;
    }
}