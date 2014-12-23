package com.prateek.gem.groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.prateek.gem.AppConstants;
import com.prateek.gem.MainActivity;
import com.prateek.gem.R;
import com.prateek.gem.expenses.AddExpenseActivity;
import com.prateek.gem.expenses.ExpensesActivity;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.utility.AppDataManager;
import com.prateek.gem.widgets.AddFloatingActionButton;

public class MainLandingScreen extends MainActivity {

    private RecyclerView vGroupsList = null;
    private GroupsAdapter mGroupsAdapter = null;

    private RecyclerView.LayoutManager mLayoutManager;
    private AddFloatingActionButton vAddGroupButton;

    Intent mAddGroupScreenIntent = null;
    Intent mExpensesScreenIntent = null;
    Intent mAddExpensesScreenIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar(R.string.title_activity_main_landing_screen, R.drawable.ic_group);

        baseActivity = this;
        mAddGroupScreenIntent = new Intent(MainLandingScreen.this,AddGroupScreen.class);

        vAddGroupButton = (AddFloatingActionButton) findViewById(R.id.vAddGroupButton);
        vGroupsList = (RecyclerView) findViewById(R.id.vGroups);

        vGroupsList.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        vGroupsList.setLayoutManager(mLayoutManager);

        // specify an adapter
        mGroupsAdapter = new GroupsAdapter(baseActivity);
        mGroupsAdapter.setGroups(DBImpl.getGroups());
        vGroupsList.setAdapter(mGroupsAdapter);

        vAddGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(AppConstants.GROUP_SOURCE, true);
                mAddGroupScreenIntent.putExtras(bundle);
                startActivity(mAddGroupScreenIntent);
            }
        });

    }

    public void onItemSelected(Group group, int position) {
        super.onItemSelected(group);
        if(position == 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(AppConstants.GROUP_SOURCE, false);
            bundle.putParcelable(AppConstants.GROUP, group);
            AppDataManager.setCurrentGroup(group);
            DebugLogger.message("onItemSelected" + mGroup);
            if(group.getTotalOfExpense() > 0) {
                mExpensesScreenIntent = new Intent(MainLandingScreen.this, ExpensesActivity.class);
                mExpensesScreenIntent.putExtras(bundle);
                startActivity(mExpensesScreenIntent);
            } else {
                mAddExpensesScreenIntent = new Intent(MainLandingScreen.this, AddExpenseActivity.class);
                mAddExpensesScreenIntent.putExtras(bundle);
                startActivity(mAddExpensesScreenIntent);
            }

        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean(AppConstants.GROUP_SOURCE, false);
            bundle.putParcelable(AppConstants.GROUP, group);
            AppDataManager.setCurrentGroup(group);
            DebugLogger.message("onItemSelected" + mGroup);
            mAddGroupScreenIntent.putExtras(bundle);
            startActivity(mAddGroupScreenIntent);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main_landing_screen;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AppConstants.GROUP, mGroup);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGroupsAdapter == null) {
            mGroupsAdapter.setGroups(DBImpl.getGroups());
        }

        mGroupsAdapter.setGroups(DBImpl.getGroups());
        mGroupsAdapter.notifyDataSetChanged();
    }
}
