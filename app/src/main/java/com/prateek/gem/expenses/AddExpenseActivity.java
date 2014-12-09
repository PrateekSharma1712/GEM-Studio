package com.prateek.gem.expenses;

import android.os.Bundle;

import com.prateek.gem.MainActivity;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.utility.AppDataManager;


public class AddExpenseActivity extends MainActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_expense;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DebugLogger.message("mGroup"+mGroup);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar(AppDataManager.getCurrentGroup().getGroupName()+"-Add Expense",R.drawable.ic_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
