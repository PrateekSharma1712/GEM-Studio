package com.prateek.gem.participants;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.prateek.gem.MainActivity;
import com.prateek.gem.R;

/**
 * Created by prateek on 5/12/14.
 */
public class AddMembersActivity extends MainActivity {

    private RecyclerView mExistingMembersRecyclerView = null;
    private RecyclerView.LayoutManager mGridLayoutManager = null;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_member;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExistingMembersRecyclerView = (RecyclerView) findViewById(R.id.existingMembersRecyclerView);
        mGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mExistingMembersRecyclerView.setLayoutManager(mGridLayoutManager);
        mExistingMembersRecyclerView.setHasFixedSize(true);


    }
}
