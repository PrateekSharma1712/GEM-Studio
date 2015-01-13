package com.prateek.gem.participants;

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

/**
 * Created by prateek on 5/12/14.
 */
public class MembersActivity extends MainActivity {

    private RecyclerView vMembersList = null;
    private GroupsAdapter mMembersAdapter = null;

    private RecyclerView.LayoutManager mLayoutManager;
    private AddFloatingActionButton vAddMembersButton;

    Intent mAddMemberScreenIntent = null;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_members;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddMemberScreenIntent = new Intent(MembersActivity.this,AddMembersActivity.class);

        vAddMembersButton = (AddFloatingActionButton) findViewById(R.id.vAddMembersButton);
        vMembersList = (RecyclerView) findViewById(R.id.vMembers);

        vMembersList.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        vMembersList.setLayoutManager(mLayoutManager);

        vAddMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mAddMemberScreenIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar(AppDataManager.getCurrentGroup().getGroupName()+"-Members", R.drawable.ic_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
