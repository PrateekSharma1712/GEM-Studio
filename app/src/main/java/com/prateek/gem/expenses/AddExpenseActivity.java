package com.prateek.gem.expenses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.prateek.gem.MainActivity;
import com.prateek.gem.R;
import com.prateek.gem.items.SelectingItemsActivity;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.utility.AppDataManager;
import com.prateek.gem.utility.Utils;
import com.prateek.gem.widgets.FloatingHint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;


public class AddExpenseActivity extends MainActivity implements View.OnClickListener{

    private FloatingHint vExpenseDate = null, vExpenseAmount = null, vExpenseItem = null;
    private Spinner vExpenseBy = null;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_expense;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vExpenseDate = (FloatingHint) findViewById(R.id.vExpenseDate);
        vExpenseAmount = (FloatingHint) findViewById(R.id.vExpenseAmount);
        vExpenseBy = (Spinner) findViewById(R.id.vExpenseBy);
        vExpenseItem = (FloatingHint) findViewById(R.id.vExpenseItem);

        ArrayAdapter<String> memberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, DBImpl.getMemberName(AppDataManager.getCurrentGroup().getGroupIdServer()));
        memberAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        vExpenseBy.setAdapter(memberAdapter);

        vExpenseDate.setText(Utils.formatDate(String.valueOf(Calendar.getInstance(Locale.getDefault()).getTimeInMillis())));

        vExpenseItem.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar("Add Expense",R.drawable.ic_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(vExpenseItem)) {
            openSelectItemsScreen();
        }
    }

    private void openSelectItemsScreen() {
        Intent selectItemsIntent = new Intent(this, SelectingItemsActivity.class);
        startActivityForResult(selectItemsIntent, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123) {
            if(resultCode == Activity.RESULT_OK) {
                DebugLogger.message("returned");
                DebugLogger.message(data.getStringExtra("items"));
                vExpenseItem.setText(data.getStringExtra("items"));
            }
        }
    }
}
