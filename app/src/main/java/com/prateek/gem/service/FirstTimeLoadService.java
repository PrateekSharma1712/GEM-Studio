package com.prateek.gem.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.prateek.gem.AppConstants;
import com.prateek.gem.AppConstants.ServiceIDs;
import com.prateek.gem.model.Group;
import com.prateek.gem.persistence.DB;
import com.prateek.gem.persistence.DBImpl;
import com.prateek.gem.utility.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FirstTimeLoadService extends IntentService {
	
	Intent broadcastIntent;
	String adminPhoneNumber;

	public FirstTimeLoadService() {
		super("FirstTimeLoadService");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		broadcastIntent = new Intent();
		broadcastIntent.setAction(AppConstants.SUCCESS_RECEIVER);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra("done", true);
		SharedPreferences preferences = getSharedPreferences(AppConstants.CUSTOM_PREFERENCE, 0);
		adminPhoneNumber = preferences.getString(AppConstants.ADMIN_PHONE, null);
		
		/** Insert Group in load groups method**/
		List<Group> groups = loadGroups();
		/** Insert Members **/
		for(Group group:groups){
			insertMembers(group.getGroupIdServer());
			insertExpenses(group.getGroupIdServer());
			insertItems(group.getGroupIdServer());
			insertSettlements(group.getGroupIdServer());
		}
		
		sendBroadcast(broadcastIntent);
	}
	
	private List<Group> loadGroups() {
		List<Group> groups;
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair(DB.TMembers.PHONE_NUMBER, adminPhoneNumber));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.GET_GROUPS_OF_MEMBER));
		ServiceHandler serviceHandler = new ServiceHandler();
		String jsonString = serviceHandler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		//System.out.println("RESPONSE OF GETTING GROUPS"+jsonString);
		JSONArray groupsArray = null;	
		JSONObject jsonObject = null;
		groups = new ArrayList<Group>();
		try {
			groupsArray = new JSONArray(jsonString);
			for(int i = 0;i<groupsArray.length();i++){
				jsonObject = groupsArray.getJSONObject(i);
                Group group = new Group();
				ContentValues cv = new ContentValues();
				cv.put(DB.TGroups.GROUPID_SERVER, jsonObject.getInt(DB.TGroups.GROUPID));				
				cv.put(DB.TGroups.GROUPNAME, jsonObject.getString(DB.TGroups.GROUPNAME));
				cv.put(DB.TGroups.GROUPICON, jsonObject.getString(DB.TGroups.GROUPICON));
				cv.put(DB.TGroups.DATEOFCREATION, jsonObject.getString(DB.TGroups.DATEOFCREATION));
				cv.put(DB.TGroups.TOTALMEMBERS, jsonObject.getInt(DB.TGroups.TOTALMEMBERS));
				cv.put(DB.TGroups.TOTALOFEXPENSE, (float) jsonObject.getDouble(DB.TGroups.TOTALOFEXPENSE));
				cv.put(DB.TGroups.ADMIN, jsonObject.getString("admin"));
                cv.put(DB.TGroups.LASTUPDATEDON, Utils.getCurrentTimeInMilliSecs());
				group.setGroupIdServer(jsonObject.getInt(DB.TGroups.GROUPID));
				group.setGroupName(jsonObject.getString(DB.TGroups.GROUPNAME));
				group.setGroupIcon(jsonObject.getString(DB.TGroups.GROUPICON));
				group.setDate(jsonObject.getString(DB.TGroups.DATEOFCREATION));
				group.setMembersCount(jsonObject.getInt(DB.TGroups.TOTALMEMBERS));
				group.setTotalOfExpense((float) jsonObject.getDouble(DB.TGroups.TOTALOFEXPENSE));
				group.setAdmin(jsonObject.getString("admin"));
                group.setLastUpdatedOn(Utils.getCurrentTimeInMilliSecs());
				//adding group for local references
				groups.add(group);
				
				// adding group to db
				long rowId = DBImpl.insert(DB.TGroups.TGROUPS, cv);
				System.out.println("Inserted group "+rowId);
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groups;
	}
	
	public void insertMembers(Integer groupId) {		
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair(DB.TMembers.GROUP_ID_FK,""+groupId));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.GET_MEMBERS));
		ServiceHandler serviceHandler = new ServiceHandler();
		String jsonString = serviceHandler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		//System.out.println("jsonStrin:::::"+jsonString);
		JSONArray array = null;
		JSONObject jsonObject = null;
		try {
			array = new JSONArray(jsonString);
			//System.out.println("array======="+array);
			for(int i = 0;i<array.length();i++){				
				jsonObject = array.getJSONObject(i);
				ContentValues cv = new ContentValues();
				cv.put(DB.TMembers.MEMBER_ID_SERVER, jsonObject.getInt(DB.TMembers.MEMBER_ID));
				cv.put(DB.TMembers.NAME, jsonObject.getString(DB.TMembers.NAME));
				cv.put(DB.TMembers.PHONE_NUMBER, jsonObject.getString(DB.TMembers.PHONE_NUMBER));
				cv.put(DB.TMembers.GROUP_ID_FK, jsonObject.getInt(DB.TMembers.GROUP_ID_FK));
				//System.out.println("Content Values"+cv.toString());
				DBImpl.insert(DB.TMembers.TMEMBERS, cv);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertExpenses(Integer groupId) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();			
		list.add(new BasicNameValuePair(DB.TExpenses.GROUP_ID_FK, ""+groupId));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.GET_EXPENSES));
		ServiceHandler serviceHandler = new ServiceHandler();
		String jsonString = serviceHandler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		if(jsonString!=""){
			JSONArray array = null;
			JSONObject jsonObject = null;
			try {
				array = new JSONArray(jsonString);
				System.out.println("gettin members from groupId"+groupId);
				System.out.println(jsonString);
				for(int i = 0;i<array.length();i++){
					jsonObject = array.getJSONObject(i);
					ContentValues cv = new ContentValues();					
					cv.put(DB.TExpenses.EXPENSE_ID_SERVER, jsonObject.getInt(DB.TExpenses.EXPENSE_ID));
					cv.put(DB.TExpenses.GROUP_ID_FK, jsonObject.getString(DB.TExpenses.GROUP_ID_FK));
					cv.put(DB.TExpenses.DATE_OF_EXPENSE, jsonObject.getString(DB.TExpenses.DATE_OF_EXPENSE));
					cv.put(DB.TExpenses.AMOUNT, (float)jsonObject.getDouble(DB.TExpenses.AMOUNT));
					cv.put(DB.TExpenses.SHARE, (float)jsonObject.getDouble(DB.TExpenses.SHARE));
					cv.put(DB.TExpenses.ITEM, jsonObject.getString(DB.TExpenses.ITEM));
					cv.put(DB.TExpenses.EXPENSE_BY, jsonObject.getString(DB.TExpenses.EXPENSE_BY));
					cv.put(DB.TExpenses.PARTICIPANTS, jsonObject.getString(DB.TExpenses.PARTICIPANTS));
					long rowIf = DBImpl.insert(DB.TExpenses.TABLENAME, cv);
					System.out.println("----expense "+rowIf);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void insertItems(Integer groupId) {
		
		List<NameValuePair> list = new ArrayList<NameValuePair>();			
		list.add(new BasicNameValuePair("group_fk", ""+groupId));
		list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ServiceIDs.GET_ITEMS));
		ServiceHandler serviceHandler = new ServiceHandler();
		String jsonString = serviceHandler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);
		if(jsonString!=""){
			JSONArray array = null;
			JSONObject jsonObject = null;
			try {
				array = new JSONArray(jsonString);
				//System.out.println("Items for group"+groupId);
				for(int i = 0;i<array.length();i++){
					jsonObject = array.getJSONObject(i);
					ContentValues cv = new ContentValues();
					cv.put(DB.TItems.ITEM_ID_SERVER, jsonObject.getInt(DB.TItems.ITEM_ID));
					cv.put(DB.TItems.ITEM_NAME, jsonObject.getString(DB.TItems.ITEM_NAME));
					cv.put(DB.TItems.GROUP_FK, jsonObject.getInt(DB.TItems.GROUP_FK));
					cv.put(DB.TItems.CATEGORY, jsonObject.getString(DB.TItems.CATEGORY));
					//System.out.println("Item is "+jsonObject.getInt(TItems.ITEM_ID));
					DBImpl.insert(DB.TItems.TITEMS, cv);
					
					System.out.println(cv.toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void insertSettlements(Integer groupId) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair(DB.TExpenses.GROUP_ID_FK, String.valueOf(groupId)));
        list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, "" + ServiceIDs.GET_SETTLEMENT_FOR_GROUP));
        ServiceHandler serviceHandler = new ServiceHandler();
        String jsonString = serviceHandler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST, list);
        System.out.println("Group id for settlement" + groupId);
        System.out.println("RESPONSE OF GETTING GROUPS" + jsonString);
        JSONArray array = null;
        try {
            array = new JSONArray(jsonString);

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                ContentValues cv = new ContentValues();
                cv.put(DB.TSettlement.SET_ID_SERVER, object.getInt(DB.TSettlement.SET_ID));
                cv.put(DB.TSettlement.GROUP_ID_FK, object.getInt(DB.TSettlement.GROUP_ID_FK));
                cv.put(DB.TSettlement.GIVEN_BY, object.getString(DB.TSettlement.GIVEN_BY));
                cv.put(DB.TSettlement.TAKEN_BY, object.getString(DB.TSettlement.TAKEN_BY));
                cv.put(DB.TSettlement.AMOUNT, Float.parseFloat(String.valueOf(object.getDouble(DB.TExpenses.AMOUNT))));
                cv.put(DB.TSettlement.DATE, object.getDouble(DB.TSettlement.DATE));
                DBImpl.insert(DB.TSettlement.TABLENAME, cv);
                //System.out.println("Settlement id "+object.getInt(DB.TSettlement.SET_ID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
