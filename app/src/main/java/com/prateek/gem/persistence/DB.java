package com.prateek.gem.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.prateek.gem.App;
import com.prateek.gem.logger.DebugLogger;

/**
 * Created by prateek on 22/11/14.
 */
public class DB extends SQLiteOpenHelper{

    private static Context context;
    public static final String DATABASE_NAME = "gemdatabase";
    public static final int DATABASE_VERSION = 6;
    private static DB mInstance = null;

    public class TGroups{
        public static final String TGROUPS = "t_groups";
        public static final String GROUPID = "group_id";
        public static final String GROUPID_SERVER = "group_id_server";
        public static final String GROUPNAME = "group_name";
        public static final String GROUPICON = "group_icon";
        public static final String DATEOFCREATION = "date_of_creation";
        public static final String TOTALOFEXPENSE = "total_of_expense";
        public static final String TOTALMEMBERS = "total_members";
        public static final String ADMIN = "admin";
        public static final String LASTUPDATEDON = "lastupdatedon";

        public static final String CREATE_QUERY_GROUPS = "CREATE TABLE IF NOT EXISTS "+ TGROUPS+" ("
                + GROUPID
                + " integer primary key autoincrement, "
                + GROUPID_SERVER
                + " integer, "
                + GROUPNAME
                + " text not null, "
                + GROUPICON
                + " text not null, "
                + DATEOFCREATION
                + " text not null, "
                + TOTALOFEXPENSE
                + " float, "
                + TOTALMEMBERS
                + " int, "
                + ADMIN
                + " text, "
                + LASTUPDATEDON
                + " text"
                + ")";

    }

    public class TMembers{
        public static final String TMEMBERS = "table_members";
        public static final String MEMBER_ID = "member_id";
        public static final String MEMBER_ID_SERVER = "member_id_server";
        public static final String GROUP_ID_FK = "group_fk";
        public static final String NAME = "member_name";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String GCM_REG_NO = "gcm_reg_no";
        public static final String CREATE_QUERY_MEMBERS = "CREATE TABLE IF NOT EXISTS "+ TMEMBERS+" ("
                + MEMBER_ID
                + " integer primary key autoincrement, "
                + MEMBER_ID_SERVER
                + " integer, "
                + GROUP_ID_FK
                + " integer, "
                + NAME
                + " text not null, "
                + PHONE_NUMBER
                + " text not null)";
    }

    public class TItems{
        public static final String TITEMS = "t_items";
        public static final String ITEM_ID = "item_id";
        public static final String ITEM_ID_SERVER = "item_id_server";
        public static final String ITEM_NAME = "item_name";
        public static final String GROUP_FK = "group_fk";
        public static final String CATEGORY = "category";
        public static final String IS_SYNCED = "is_synced";
        public static final String CREATE_QUERY_ITEMS = "CREATE TABLE IF NOT EXISTS "+ TITEMS+" ("
                + ITEM_ID
                + " integer primary key autoincrement, "
                + ITEM_ID_SERVER
                + " integer, "
                + ITEM_NAME
                + " text not null, "
                + GROUP_FK
                + " int, "
                + CATEGORY
                + " text not null, "
                + IS_SYNCED
                + " integer"
                + ")";

    }

    public class TCategories{
        public static final String TCATEGORIES = "t_categories";
        public static final String CATEGORY_ID = "t_category";
        public static final String CATEGORY_ID_SERVER = "t_category_server";
        public static final String CATEGORY_NAME = "category_name";
        public static final String GROUP_FK = "group_fk";
        public static final String IS_SYNCED = "is_synced";
        public static final String CREATE_QUERY_CATEGORY = "CREATE TABLE IF NOT EXISTS " + TCATEGORIES + "(" +
                CATEGORY_ID +
                " integer primary key autoincrement, " +
                CATEGORY_ID_SERVER +
                " integer, " +
                CATEGORY_NAME +
                " text not null, " +
                GROUP_FK +
                " integer, " +
                IS_SYNCED +
                " integer" +
                ")";
    }

    public class TExpenses{
        public static final String TABLENAME = "t_expenses";
        public static final String EXPENSE_ID = "expense_id";
        public static final String EXPENSE_ID_SERVER = "expense_id_server";
        public static final String GROUP_ID_FK = "group_id_fk";
        public static final String DATE_OF_EXPENSE = "date_of_expense";
        public static final String AMOUNT = "amount";
        public static final String SHARE = "share";
        public static final String ITEM = "item";
        public static final String EXPENSE_BY = "expense_by";
        public static final String PARTICIPANTS = "participants";
        public static final String IS_SYNCED = "is_synced";
        public static final String REFERENCE = "reference";
        public static final String CREATE_QUERY_EXPENSES = "CREATE TABLE IF NOT EXISTS " +
                TABLENAME +
                "(" +
                EXPENSE_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EXPENSE_ID_SERVER +
                " integer, " +
                GROUP_ID_FK +
                " INTEGER, " +
                DATE_OF_EXPENSE +
                " TEXT, " +
                AMOUNT +
                " FLOAT, " +
                SHARE +
                " FLOAT, " +
                ITEM +
                " TEXT, " +
                EXPENSE_BY +
                " TEXT, " +
                PARTICIPANTS +
                " TEXT, " +
                IS_SYNCED +
                " INTEGER, " +
                REFERENCE +
                " text" +
                ")";
    }

    public class TSettlement{
        public static final String TABLENAME = "t_settlement";
        public static final String SET_ID = "set_id";
        public static final String SET_ID_SERVER = "set_id_server";
        public static final String GROUP_ID_FK = "group_id_fk";
        public static final String GIVEN_BY = "givenby";
        public static final String TAKEN_BY = "takenby";
        public static final String AMOUNT = "amount";
        public static final String DATE = "date";
        public static final String CREATE_QUERY_SETTLEMENT = "CREATE TABLE IF NOT EXISTS " +
                TABLENAME +
                "(" +
                SET_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SET_ID_SERVER +
                " integer, " +
                GROUP_ID_FK +
                " INTEGER, " +
                GIVEN_BY +
                " TEXT NOT NULL, " +
                TAKEN_BY +
                " TEXT NOT NULL, " +
                AMOUNT +
                " FLOAT, " +
                DATE +
                " TEXT NOT NULL)";
    }

    private static SQLiteDatabase database;

    /**
     * constructor should be private to prevent direct instantiation. make call
     * to static factory method "getInstance()" instead.
     */
    public DB(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        context = ctx;
    }

    public static DB getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare. this will
         * ensure that you don't accidentally leak an Activity context (see this
         * article for more information:
         * http://developer.android.com/resources/articles
         * /avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            DebugLogger.error("mInstance.."+mInstance);
            DebugLogger.error("ctx.."+ctx);
            if (ctx == null) { // safe check
                DebugLogger.error("App.getInstance().."+App.getInstance());
                mInstance = new DB(App.getInstance());
            } else {
                DebugLogger.error("ctx.getApplicationContext().."+ctx.getApplicationContext());
                mInstance = new DB(ctx.getApplicationContext());
            }
        }
        DebugLogger.error("mInstance.."+mInstance);
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TGroups.CREATE_QUERY_GROUPS);
        database.execSQL(TMembers.CREATE_QUERY_MEMBERS);
        database.execSQL(TExpenses.CREATE_QUERY_EXPENSES);
        database.execSQL(TItems.CREATE_QUERY_ITEMS);
        database.execSQL(TSettlement.CREATE_QUERY_SETTLEMENT);
        database.execSQL(TCategories.CREATE_QUERY_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DB.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        /*for (String table : TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }*/
        onCreate(db);
    }

    /**
     * Method to clear data from all table
     */
    public void clearTables() {
        /*DebugLogger.method("BabyBerryDBHelper :: clearDB");
        SQLiteDatabase database = getDatabase();
        if (database != null && database.isOpen()) {
            for (String table : TABLES) {
                database.delete(table, null, null);
            }
        } else {
            DebugLogger.message("Unable to clear the records in table.");
        }*/
    }

    public static SQLiteDatabase getDatabase() {
        DebugLogger.error("context.."+context);
        if (database == null) {
            return DB.getInstance(context).getWritableDatabase();
        }
        return database;
    }

    public void closeDatabase() {
        DebugLogger.method("DB :: databaseClose");
        if (database != null && database.isOpen()) {
            database.close();
            database = null;
        }
    }
}
