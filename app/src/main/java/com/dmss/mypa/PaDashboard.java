package com.dmss.mypa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class PaDashboard extends AppCompatActivity {
    public boolean ShowArtsEntries = true;
    RadioButton ArtsEntry;
    EditText Amount, PaymentMode, Description, TimeSheetId, TimeSheetDate,TimeSheetArtsOrOdc,TimeSheetSwipeInOrOut;

    PersonalAssistDbAdaptor PaDbAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pa_dashboard);
        ArtsEntry = (RadioButton) findViewById(R.id.ArtsEntry);
        Amount = (EditText) findViewById(R.id.Amount);
        PaymentMode = (EditText) findViewById(R.id.PaymentMode);
        Description = (EditText) findViewById(R.id.Description);

        TimeSheetId = (EditText) findViewById(R.id.TimeSheetId);
        TimeSheetDate = (EditText) findViewById(R.id.TimeSheetDate);

        TimeSheetArtsOrOdc = (EditText) findViewById(R.id.TimeSheetArtsOrOdc);
        TimeSheetSwipeInOrOut = (EditText) findViewById(R.id.TimeSheetSwipeInOrOut);

        PaDbAdaptor = new PersonalAssistDbAdaptor(this);
    }

    public void RecordArtsOrOdcIn(View view) {
        boolean artsEntry = ArtsEntry.isChecked();
        long id = PaDbAdaptor.InsertArtsAndOdcEntries(artsEntry ? 1 : 0, 1);
    }

    public void RecordArtsOrOdcOut(View view) {
        boolean artsEntry = ArtsEntry.isChecked();
        long id = PaDbAdaptor.InsertArtsAndOdcEntries(artsEntry ? 1 : 0, 0);
    }

    public void RecordExpense(View view) {
        long id = PaDbAdaptor.InsertExpenseEntry(Amount.getText().toString(), PaymentMode.getText().toString(), Description.getText().toString());
    }

    public void ShowSwipes(View view) {
        ShowArtsEntries = true;
        ShowEntries(true);
    }

    public void ShowExpenses(View view) {
        ShowArtsEntries = false;
        ShowEntries(false);
    }

    private void ShowEntries(boolean showArts) {
        Intent intent = new Intent(PaDashboard.this, ShowEntries.class);
        intent.putExtra("ShowArtsEntries", showArts);
        startActivity(intent);
    }

    public void ExportExpense(View view) {
        PaDbAdaptor.exportExpenseTable(this.getApplicationContext(), PersonalAssistContract.PersonalAssistDailyExpense.TABLE_NAME);
        PaDbAdaptor.exportExpenseTable(this.getApplicationContext(), PersonalAssistContract.PersonalAssistTimeSheet.TABLE_NAME);
    }

    public void SelectTimeSheetEntry(View view) {
        int id = Integer.parseInt( TimeSheetId.getText().toString());
		ArtsOdcDto swipeData = PaDbAdaptor.getEntryDate(id);
        TimeSheetDate.setText(swipeData.SwipeDateString);
		TimeSheetArtsOrOdc.setText(swipeData.ArtsOrOdc);
		TimeSheetSwipeInOrOut.setText(swipeData.SwipeInOrOut);
    }
    public void UpdateTimeSheetEntry(View view) {
		ArtsOdcDto swipeData = new ArtsOdcDto();
		swipeData.id = Integer.parseInt(TimeSheetId.getText().toString());
		swipeData.SwipeDateString = TimeSheetDate.getText().toString();
		swipeData.ArtsOrOdc = TimeSheetArtsOrOdc.getText().toString();
		swipeData.SwipeInOrOut = TimeSheetSwipeInOrOut.getText().toString();
        PaDbAdaptor.UpdateTimeSheet(swipeData);
    }
}
