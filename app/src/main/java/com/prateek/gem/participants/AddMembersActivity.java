package com.prateek.gem.participants;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.provider.ContactsContract.Data;

import com.prateek.gem.AppConstants;
import com.prateek.gem.BaseActivity;
import com.prateek.gem.MainActivity;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Member;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.service.FullFlowService;
import com.prateek.gem.utility.AppDataManager;
import com.prateek.gem.utility.LoadingScreen;
import com.prateek.gem.utility.Utils;

import java.util.ArrayList;

import static com.prateek.gem.participants.ContactsListFragment.OnContactsInteractionListener;

/**
 * Created by prateek on 5/12/14.
 */
public class AddMembersActivity extends MainActivity implements
        OnContactsInteractionListener, LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mExistingMembersRecyclerView = null;
    private RecyclerView.LayoutManager mGridLayoutManager = null;
    private Uri mContactUri = null;
    private static String mLookUpKey = null;
    private boolean isSearchResultView = false;
    private AddMembersAdapter mExistingMembersAdapter = null;
    private BaseActivity currentScreen = null;
    public static ArrayList<Member> mExistingMembers = new ArrayList<Member>();
    private AddMemberRecevier addMemberReceiver;
    IntentFilter addMemberIntentFilter;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_member;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToolbar("Add Members", R.drawable.ic_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentScreen = this;
        mExistingMembersRecyclerView = (RecyclerView) findViewById(R.id.existingMembersRecyclerView);
        mGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mExistingMembersRecyclerView.setLayoutManager(mGridLayoutManager);
        mExistingMembersRecyclerView.setHasFixedSize(true);
        mExistingMembersRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mExistingMembersAdapter = new AddMembersAdapter(currentScreen);
        mExistingMembers.addAll(DBImpl.getMembers(AppDataManager.getCurrentGroup().getGroupIdServer()));
        mExistingMembersRecyclerView.setAdapter(mExistingMembersAdapter);

        addMemberReceiver = new AddMemberRecevier();
        addMemberIntentFilter = new IntentFilter(AddMemberRecevier.ADDMEMBERSUCCESSRECEIVER);
        addMemberIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {

            // Fetch query from intent and notify the fragment that it should display search
            // results instead of all contacts.
            String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);
            ContactsListFragment mContactsListFragment = (ContactsListFragment)
                    getSupportFragmentManager().findFragmentById(R.id.contact_list);

            // This flag notes that the Activity is doing a search, and so the result will be
            // search results rather than all contacts. This prevents the Activity and Fragment
            // from trying to a search on search results.
            isSearchResultView = true;
            mContactsListFragment.setSearchQuery(searchQuery);

            // Set special title for search results
            String title = "Search by contact name";
            setTitle(title);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(addMemberReceiver, addMemberIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(addMemberReceiver);
    }

    @Override
    public void onContactSelected(Uri contactUri, String lookUpKey) {
        if (Utils.hasHoneycomb()) {
            mContactUri = contactUri;
        } else {
            // For versions earlier than Android 3.0, stores a contact Uri that's constructed from
            // contactLookupUri. Later on, the resulting Uri is combined with
            // Contacts.Data.CONTENT_DIRECTORY to map to the provided contact. It's done
            // differently for these earlier versions because Contacts.Data.CONTENT_DIRECTORY works
            // differently for Android versions before 3.0.
            mContactUri = ContactsContract.Contacts.lookupContact(this.getContentResolver(),
                    contactUri);

        }
        mLookUpKey = lookUpKey;
        DebugLogger.message("mLookUpKey"+mLookUpKey);
        DebugLogger.message("mContactUri"+mContactUri.toString());
        getLoaderManager().restartLoader(ContactDetailQuery.QUERY_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case ContactDetailQuery.QUERY_ID:
                // This query loads main contact details, see
                // ContactDetailQuery for more information.
                DebugLogger.message("look"+mLookUpKey);
                return new CursorLoader(this, Data.CONTENT_URI,
                        ContactDetailQuery.PROJECTION,
                        ContactDetailQuery.SELECTION, new String[]{mLookUpKey}, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // If this fragment was cleared while the query was running
        // eg. from from a call like setContact(uri) then don't do
        // anything.
        if (mContactUri == null) {
            return;
        }

        switch (loader.getId()) {
            case ContactDetailQuery.QUERY_ID:
                // Moves to the first row in the Cursor
                DebugLogger.message("data"+data.moveToFirst());
                if (data.moveToFirst()) {
                    DebugLogger.message("contact "+data.getString(ContactDetailQuery.NUMBER));
                    DebugLogger.message("contact "+data.getString(ContactDetailQuery.NAME));
                    String number = Utils.correctNumber(data.getString(ContactDetailQuery.NUMBER));
                    String name = data.getString(ContactDetailQuery.NAME);

                    if(number == null) {
                        Utils.showToast(baseActivity,"No number");
                    } else {
                        Member member = new Member();
                        member.setMemberName(name);
                        member.setPhoneNumber(number);
                        member.setGroupIdFk(AppDataManager.getCurrentGroup().getGroupIdServer());
                        if(mExistingMembers.contains(member)) {
                            Utils.showToast(this, getString(R.string.already_exists, member.getMemberName()));
                        } else {
                            mExistingMembers.add(member);
                        }
                        mExistingMembersAdapter.notifyDataSetChanged();
                    }
                } else {
                    Utils.showToast(baseActivity,"No number");
                }

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * This interface defines constants used by contact retrieval queries.
     */
    public interface ContactDetailQuery {
        // A unique query ID to distinguish queries being run by the
        // LoaderManager.
        final static int QUERY_ID = 1;

        // The query projection (columns to fetch from the provider)
        @SuppressLint("InlinedApi")
        final static String[] PROJECTION = {
                Data._ID,
                Data.MIMETYPE,
                Data.LOOKUP_KEY,
                Utils.hasHoneycomb() ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME,
                Data.DATA4

        };

        // Defines the selection clause
        static final String SELECTION = Data.LOOKUP_KEY + " = ?  AND " +
        Data.MIMETYPE + " = " +
                "'" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";


        // The query column numbers which map to each value in the projection
        final static int ID = 0;
        final static int MIMETYPE = 1;
        final static int LOOKUPKEY = 2;
        final static int NAME = 3;
        final static int NUMBER = 4;
    }

    @Override
    public void onSelectionCleared() {

    }

    @Override
    public void onSaveSelectedMembers() {
        DebugLogger.message("mExistingMembers"+mExistingMembers);
        DebugLogger.message("previous"+DBImpl.getMembers(AppDataManager.getCurrentGroup().getGroupIdServer()));
        if(!mExistingMembers.equals(DBImpl.getMembers(AppDataManager.getCurrentGroup().getGroupIdServer()))) {
            DebugLogger.message("need to add");
            mExistingMembers.removeAll(DBImpl.getMembers(AppDataManager.getCurrentGroup().getGroupIdServer()));
            DebugLogger.message("mExistingMembers"+mExistingMembers);
            if(Utils.isConnected(this)) {
                LoadingScreen.showLoading(this, "Adding members");
                FullFlowService.ServiceAddMembers(this, AppConstants.NOT_ADDMEMBERS, mExistingMembers, AppDataManager.getCurrentGroup().getGroupIdServer(), AppDataManager.getCurrentGroup().getGroupId());
                DebugLogger.message(mExistingMembers);
            } else {
                Utils.showToast(this, "Adding Members requires internet connection");
            }
        } else {
            DebugLogger.message("no need to add");
            finish();
        }
    }

    @Override
    public boolean onSearchRequested() {
        return !isSearchResultView && super.onSearchRequested();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExistingMembers = new ArrayList<Member>();
    }

    public class AddMemberRecevier extends BroadcastReceiver {

        public static final String ADDMEMBERSUCCESSRECEIVER = "com.prateek.gem.views.AddExpenseActivity.ADDMEMBERSUCCESSRECEIVER";

        @Override
        public void onReceive(Context context, Intent intent) {
            LoadingScreen.dismissProgressDialog();
            int notId = intent.getIntExtra(FullFlowService.EXTRA_NOTID, 0);
            boolean result = intent.getBooleanExtra(AppConstants.RESULT, false);
            switch (notId) {
                case AppConstants.NOT_ADDMEMBERS:
                    if(result){
                        Utils.showToast(context, "Added Succesfully");
                        finish();
                    }else{
                        Utils.showToast(context, "Cannot add members, Please try after some time");
                        mExistingMembers = DBImpl.getMembers(AppDataManager.getCurrentGroup().getGroupIdServer());
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
