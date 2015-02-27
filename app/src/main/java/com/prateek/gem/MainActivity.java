package com.prateek.gem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.prateek.gem.model.Group;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.widgets.ConfirmationDialog;

/**
 * Created by prateek on 23/11/14.
 */
public abstract class MainActivity extends BaseActivity implements ConfirmationDialog.OnModeConfirmListener {

    protected Toolbar mToolBar = null;
    protected Group mGroup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        mToolBar = (Toolbar) findViewById(R.id.vToolbar);
        DebugLogger.message("MainActivity :: toolbar"+mToolBar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        DebugLogger.message("MainActivity mGroup"+mGroup);
    }

    protected abstract int getLayoutResource();

    protected void setToolbar(int titleId, int ic_group) {
        DebugLogger.message("MainActivity :: setToolbar :: toolbar"+mToolBar);
        mToolBar.setTitle(getString(titleId));
        mToolBar.setLogo(ic_group);
    }

    protected void setToolbar(String titleName, int ic_group) {
        DebugLogger.message("MainActivity :: setToolbar :: toolbar"+mToolBar);
        mToolBar.setTitle(titleName);
        mToolBar.setLogo(ic_group);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugLogger.message("MainActivity :: onActivityResult");
    }

    @Override
    public void modeConfirmed() {

    }

    @Override
    public void deleteMemberConfirmed(int memberId) {

    }

    @Override
    public void deleteExpenseConfirmed(int expenseId) {

    }

    @Override
    public void deleteItemConfirmed(boolean deleteItemConfirmed) {

    }

    @Override
    public void openNewActivity(int requestCodeClickImage) {

    }

    @Override
    public void onItemsAdded(String category, String item) {

    }
}
