package com.prateek.gem.persistence;

import android.content.Context;
import android.database.Cursor;

import com.prateek.gem.groups.Group;
import com.prateek.gem.logger.DebugLogger;

import java.util.ArrayList;

/**
 * Created by prateek on 8/12/14.
 */
public class DBImpl extends DB {
    private static DBImpl instanceHelper = null;
    private static Context context = null;

    public DBImpl(Context ctx) {
        super(ctx);
        context = ctx;
    }

    public static DBImpl sharedHelper() {
        if (instanceHelper == null) {
            instanceHelper = new DBImpl(context);
        }
        return instanceHelper;
    }

    /**
     * Method to clear all the records from all the tables
     *
     * @return none
     */
    public void clearAllTables() {
        DebugLogger.method("DBImpl :: clearAllTables");
        clearTables();
    }

    /**
     * Method to get all groups of the user
     *
     * @return {ArrayList of Groups}
     */
    public static ArrayList<Group> getGroups() {
        ArrayList<Group> mGroups = new ArrayList<Group>();
        Cursor c = getDatabase().query(TGroups.TGROUPS, new String[]{
                TGroups.GROUPID,
                TGroups.GROUPID_SERVER,
                TGroups.GROUPNAME,
                TGroups.GROUPICON,
                TGroups.TOTALMEMBERS,
                TGroups.DATEOFCREATION,
                TGroups.ADMIN,
                TGroups.TOTALOFEXPENSE
        }, null, null, null, null, null);

        if(c.moveToFirst()){
            do{
                Group group = new Group();
                group.setGroupId(c.getInt(c.getColumnIndex(TGroups.GROUPID)));
                group.setGroupIdServer(c.getInt(c.getColumnIndex(TGroups.GROUPID_SERVER)));
                group.setGroupName(c.getString(c.getColumnIndex(TGroups.GROUPNAME)));
                group.setGroupIcon(c.getString(c.getColumnIndex(TGroups.GROUPICON)));
                group.setDate(c.getString(c.getColumnIndex(TGroups.DATEOFCREATION)));
                group.setMembersCount(c.getInt(c.getColumnIndex(TGroups.TOTALMEMBERS)));
                group.setTotalOfExpense(c.getFloat(c.getColumnIndex(TGroups.TOTALOFEXPENSE)));
                group.setAdmin(c.getString(c.getColumnIndex(TGroups.ADMIN)));
                mGroups.add(group);
            }while(c.moveToNext());
        }

        return mGroups;

    }
}
