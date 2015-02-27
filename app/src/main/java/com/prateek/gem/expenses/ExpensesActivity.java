package com.prateek.gem.expenses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.prateek.gem.MainActivity;
import com.prateek.gem.R;
import com.prateek.gem.groups.GroupsAdapter;
import com.prateek.gem.utility.AppDataManager;
import com.prateek.gem.widgets.AddFloatingActionButton;

public class ExpensesActivity extends MainActivity {

    private RecyclerView vExpensesList = null;

    private RecyclerView.LayoutManager mLayoutManager;
    private AddFloatingActionButton vAddExpensesButton;

    Intent mAddExpenseScreenIntent = null;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_expenses;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAddExpenseScreenIntent = new Intent(ExpensesActivity.this,AddExpenseActivity.class);

        vAddExpensesButton = (AddFloatingActionButton) findViewById(R.id.vAddExpenseButton);
        vExpensesList = (RecyclerView) findViewById(R.id.vExpenses);

        vExpensesList.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        vExpensesList.setLayoutManager(mLayoutManager);

        vAddExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mAddExpenseScreenIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar("Expenses", R.drawable.ic_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
