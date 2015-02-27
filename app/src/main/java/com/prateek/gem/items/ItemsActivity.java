package com.prateek.gem.items;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.prateek.gem.AppConstants;
import com.prateek.gem.MainActivity;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Items;
import com.prateek.gem.persistence.DB;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.service.FullFlowService;
import com.prateek.gem.utility.AppDataManager;
import com.prateek.gem.utility.LoadingScreen;
import com.prateek.gem.utility.Utils;
import com.prateek.gem.widgets.AddFloatingActionButton;
import com.prateek.gem.widgets.ConfirmationDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prateek on 9/2/15.
 */
public class ItemsActivity extends MainActivity implements RecyclerView.OnItemTouchListener, View.OnClickListener, ConfirmationDialog.OnModeConfirmListener{

    private RecyclerView vItemsList = null;
    private RecyclerView vCategoriesList = null;
    private TextView vEmptyView = null;

    private LinearLayoutManager mCategoryLayoutManager;
    private RecyclerView.LayoutManager mItemLayoutManager;
    private AddFloatingActionButton vAddItemButton;

    private CategoriesAdapter mCategoriesAdapter = null;
    private ItemsAdapter mItemsAdapter = null;
    private GestureDetectorCompat gestureDetector = null;
    int selectedCategoryIndex = 0;

    ItemsReceiver itemsReceiver;
    IntentFilter itemSuccessFilter;

    private ActionMode mDeleteMode = null;

    private List<Integer> itemIdsToDelete;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vAddItemButton = (AddFloatingActionButton) findViewById(R.id.vAddItemsButton);
        vAddItemButton.setOnClickListener(this);
        vItemsList = (RecyclerView) findViewById(R.id.vItems);
        vCategoriesList = (RecyclerView) findViewById(R.id.vCategories);
        vEmptyView = (TextView) findViewById(android.R.id.text1);

        vItemsList.setHasFixedSize(true);
        vCategoriesList.setHasFixedSize(true);

        // use a linear layout manager
        mItemLayoutManager = new LinearLayoutManager(this);
        mCategoryLayoutManager = new LinearLayoutManager(this);
        vItemsList.setLayoutManager(mItemLayoutManager);
        vItemsList.setItemAnimator(new DefaultItemAnimator());
        vCategoriesList.setLayoutManager(mCategoryLayoutManager);
        vCategoriesList.setItemAnimator(new DefaultItemAnimator());

        mCategoriesAdapter = new CategoriesAdapter(this);
        vCategoriesList.setAdapter(mCategoriesAdapter);
        vCategoriesList.addOnItemTouchListener(this);
        gestureDetector =
                new GestureDetectorCompat(this, new RecyclerViewDemoOnGestureListener());

        itemsReceiver = new ItemsReceiver();
        itemSuccessFilter = new IntentFilter(ItemsReceiver.ITEMSUCCESSRECEIVER);
        itemSuccessFilter.addCategory(Intent.CATEGORY_DEFAULT);

        mCategoriesAdapter.toggleSelection(selectedCategoryIndex);
        loadItems(mCategoriesAdapter.getSelectedCategory());

        vItemsList.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                DebugLogger.message("dx"+dx+"dy"+dy);
                if(mDeleteMode == null) {
                    if (dy < 0) {
                        if (vAddItemButton.getVisibility() == View.GONE) {
                            Utils.toggleVisibility(vAddItemButton, true);
                        }
                    } else if (dy > 5) {
                        if (vAddItemButton.getVisibility() == View.VISIBLE) {
                            Utils.toggleVisibility(vAddItemButton, true);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar("Items", R.drawable.ic_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(itemsReceiver, itemSuccessFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(itemsReceiver);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(vAddItemButton)) {
            openDialog();
        }
    }

    private void openDialog() {
        ConfirmationDialog mcd = new ConfirmationDialog();
        Bundle bundle = new Bundle();
        bundle.putString(ConfirmationDialog.TITLE, getString(R.string.addGroup));
        bundle.putInt(AppConstants.ConfirmConstants.CONFIRM_KEY, AppConstants.ConfirmConstants.ITEM_ADD);
        bundle.putString(ConfirmationDialog.BUTTON1, getString(R.string.button_cancel));
        bundle.putString(ConfirmationDialog.BUTTON2, getString(R.string.button_add));
        bundle.putInt(ConfirmationDialog.SELECTED_CATEGORY, selectedCategoryIndex);
        mcd.setArguments(bundle);
        mcd.show(getSupportFragmentManager(), "ComfirmationDialog");
    }


    private ActionMode.Callback mDeleteModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate( R.menu.add_group_screen, menu );
            MenuItem item = menu.findItem(R.id.editGroup);
            item.setVisible(false);
            if(vAddItemButton.getVisibility() == View.VISIBLE)
                Utils.toggleVisibility(vAddItemButton, true);
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mItemsAdapter.resetSelectedPositions();
            actionMode.finish();
            mDeleteMode = null;
            if(vAddItemButton.getVisibility() == View.GONE)
                Utils.toggleVisibility(vAddItemButton, true);

            mItemsAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            int totalSelected = mItemsAdapter.getSelectedCount();
            if(totalSelected > 0) {
                actionMode.setTitle(totalSelected + " selected");
            } else {
                actionMode.finish();
                mDeleteMode = null;
                if(vAddItemButton.getVisibility() == View.GONE)
                    Utils.toggleVisibility(vAddItemButton, true);
            }

            DebugLogger.message("getSelectedPositions"+mItemsAdapter.getSelectedPositions());
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.deleteGroup) {
                itemIdsToDelete = mItemsAdapter.getItemIds();
                DebugLogger.message("to be deleted" + itemIdsToDelete);
                String itemNamesString = "";
                for(int i = 0;i< itemIdsToDelete.size();i++) {
                    itemNamesString += DBImpl.getItem(itemIdsToDelete.get(i)).getItemName() + ", ";
                }

                if(itemNamesString.length() > 2) {
                    itemNamesString = itemNamesString.substring(0, itemNamesString.length() -2);
                }

                ConfirmationDialog mcd = new ConfirmationDialog();
                Bundle bundle = new Bundle();
                bundle.putString(ConfirmationDialog.TITLE, getString(R.string.button_delete));
                bundle.putInt(AppConstants.ConfirmConstants.CONFIRM_KEY, AppConstants.ConfirmConstants.ITEM_DELETE);
                bundle.putString(ConfirmationDialog.BUTTON1, getString(R.string.button_cancel));
                bundle.putString(ConfirmationDialog.BUTTON2, getString(R.string.button_delete));
                bundle.putString(ConfirmationDialog.MESSAGE, "Are you sure to delete "+itemNamesString+"?");
                mcd.setArguments(bundle);
                mcd.show(getSupportFragmentManager(), "ComfirmationDialog");
            }
            return true;

        }

    };

    public void runActionMode() {
        if(mDeleteMode == null) {
            mDeleteMode = this.startSupportActionMode(mDeleteModeCallback);
        } else {
            mDeleteModeCallback.onCreateActionMode(mDeleteMode, mDeleteMode.getMenu());
        }
    }

    private class RecyclerViewDemoOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            DebugLogger.message("onSing");
            View view = vCategoriesList.findChildViewUnder(e.getX(), e.getY());

            int idx = vCategoriesList.getChildPosition(view);
            if(mDeleteMode != null) {
                if(mItemsAdapter != null) {
                    mItemsAdapter.resetSelectedPositions();
                }
                mDeleteMode.finish();
                mDeleteMode = null;
            }
            mCategoriesAdapter.toggleSelection(idx);
            selectedCategoryIndex = idx;
            loadItems(mCategoriesAdapter.getSelectedCategory());
            return super.onSingleTapConfirmed(e);
        }
    }

    private void loadItems(String category) {
        DebugLogger.message("category"+category);
        ArrayList<Items> items = DBImpl.getItems(AppDataManager.getCurrentGroup().getGroupIdServer(), category);
        if(items.size() > 0) {
            if(mItemsAdapter != null) {
                mItemsAdapter.setDataset(items);
            } else {
                mItemsAdapter = new ItemsAdapter(this);
                vItemsList.setAdapter(mItemsAdapter);
                mItemsAdapter.setDataset(items);
            }
            vItemsList.setVisibility(View.VISIBLE);
            vEmptyView.setVisibility(View.GONE);
        } else {
            vItemsList.setVisibility(View.GONE);
            vEmptyView.setVisibility(View.VISIBLE);
            vEmptyView.setText(getString(R.string.no, "items in " + category));
        }
    }

    @Override
    public void onItemsAdded(String category, String item) {
        DebugLogger.message("category"+category);
        DebugLogger.message("item"+item);
        ContentValues cv = new ContentValues();
        cv.put(DB.TItems.ITEM_NAME, item);
        cv.put(DB.TItems.GROUP_FK, AppDataManager.getCurrentGroup().getGroupIdServer());
        cv.put(DB.TItems.CATEGORY, category);
        cv.put(DB.TItems.ITEM_ID_SERVER, 0);

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair(DB.TItems.ITEM_NAME, item));
        list.add(new BasicNameValuePair(DB.TItems.GROUP_FK, String.valueOf(AppDataManager.getCurrentGroup().getGroupIdServer())));
        list.add(new BasicNameValuePair(DB.TItems.CATEGORY, category));
        list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ AppConstants.ServiceIDs.ADD_ITEM));
        LoadingScreen.showLoading(this, "Adding "+item);
        if(Utils.isConnected(this)) {
            LoadingScreen.showLoading(this, "Adding "+item);
            FullFlowService.ServiceAddItem(this, AppConstants.NOT_ADDITEM, list, cv);
        } else {
            Utils.showToast(this, "Adding item requires internet connection");
        }
    }

    @Override
    public void deleteItemConfirmed(boolean delete) {
        if(delete) {
            DebugLogger.message("itemIdsToDelete"+itemIdsToDelete);
            if(itemIdsToDelete.size() > 0) {
                LoadingScreen.showLoading(this, "Deleting item "+DBImpl.getItem(itemIdsToDelete.get(0)).getItemName());
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair(DB.TItems.ITEM_ID, String.valueOf(itemIdsToDelete.get(0))));
                list.add(new BasicNameValuePair(DB.TItems.GROUP_FK, String.valueOf(AppDataManager.getCurrentGroup().getGroupIdServer())));
                list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ AppConstants.ServiceIDs.DELETE_ITEM_BY_ID));
                if(Utils.isConnected(this)) {
                    FullFlowService.ServiceDeleteItem(this, AppConstants.NOT_DELETEITEM, list);
                } else {
                    Utils.showToast(this, "Removing item requires internet connection");
                    LoadingScreen.dismissProgressDialog();
                }
            } else {
                LoadingScreen.dismissProgressDialog();
                Utils.showToast(this, "Deleted Succesfully");
                if(mDeleteMode != null) {
                    mDeleteMode.finish();
                }
            }
        }
    }


    public class ItemsReceiver extends BroadcastReceiver {

        public static final String ITEMSUCCESSRECEIVER = "com.prateek.gem.views.ItemsActivity.ITEMSUCCESSRECEIVER";

        @Override
        public void onReceive(Context context, Intent intent) {
            LoadingScreen.dismissProgressDialog();
            int notId = intent.getIntExtra(FullFlowService.EXTRA_NOTID, 0);
            boolean result = intent.getBooleanExtra(AppConstants.RESULT, false);
            switch (notId) {
                case AppConstants.NOT_ADDITEM:
                    if(result){
                        Utils.showToast(context, "Added Succesfully");
                        loadItems(mCategoriesAdapter.getSelectedCategory());
                    }else{
                        Utils.showToast(context, "Cannot add, Please try after some time");
                    }
                    break;
                case AppConstants.NOT_DELETEITEM:
                    if(result){
                        itemIdsToDelete.remove(new Integer(intent.getIntExtra(DB.TItems.ITEM_ID_SERVER, 0)));
                        deleteItemConfirmed(true);
                        loadItems(mCategoriesAdapter.getSelectedCategory());
                    }else{
                        Utils.showToast(context, "Cannot Delete, Please try after some time");
                    }

                    break;

                default:
                    break;
            }
        }

    }
}
