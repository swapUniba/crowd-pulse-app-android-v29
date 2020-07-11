package com.swapuniba.crowdpulse.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fabio on 20/04/16.
 */
public class DbHelper extends SQLiteOpenHelper
{
    public static final String DBNAME = DatabaseString.db_name;

    public DbHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        String q="CREATE TABLE "+ DatabaseString.tab_gps + " (" +
                DatabaseString.gps_timestamp + " TEXT," +
                DatabaseString.gps_latitude + " TEXT," +
                DatabaseString.gps_longitude + " TEXT," +
                DatabaseString.gps_speed + " TEXT," +
                DatabaseString.gps_accuracy + " TEXT, " +
                DatabaseString.gps_send + " TEXT, " +
                "PRIMARY KEY(" + DatabaseString.gps_timestamp + "))";
        db.execSQL(q);

        String q1="CREATE TABLE "+ DatabaseString.tab_netstats + " (" +
                DatabaseString.netstats_networkType + " TEXT," +
                DatabaseString.netstats_timestamp + " TEXT," +
                DatabaseString.netstats_rxBytes + " TEXT," +
                DatabaseString.netstats_txBytes + " TEXT," +
                DatabaseString.netstats_send + " TEXT, " +
                "PRIMARY KEY(" + DatabaseString.netstats_networkType + ", " + DatabaseString.netstats_timestamp + "))";
        db.execSQL(q1);

        String q2="CREATE TABLE "+ DatabaseString.tab_appinfo+ " (" +
                DatabaseString.appinfo_packageName + " TEXT," +
                DatabaseString.appinfo_timestamp + " TEXT," +
                DatabaseString.appinfo_category + " TEXT," +
                DatabaseString.appinfo_foregroundTime + " TEXT," +
                DatabaseString.appinfo_send + " TEXT, " +
                "PRIMARY KEY(" + DatabaseString.appinfo_packageName + ", " + DatabaseString.appinfo_timestamp + "))";
        db.execSQL(q2);

        String q3="CREATE TABLE "+ DatabaseString.tab_display+ " (" +
                DatabaseString.display_timestamp + " TEXT," +
                DatabaseString.display_state + " TEXT," +
                DatabaseString.display_send + " TEXT, " +
                "PRIMARY KEY(" + DatabaseString.display_timestamp + "))";
        db.execSQL(q3);


        String q4="CREATE TABLE "+ DatabaseString.tab_account+ " (" +
                DatabaseString.account_timestamp + " TEXT," +
                DatabaseString.account_userAccountName + " TEXT, " +
                DatabaseString.account_packageName + " TEXT," +
                DatabaseString.account_send + " TEXT," +
                "PRIMARY KEY(" + DatabaseString.account_timestamp + ", " + DatabaseString.account_userAccountName + ", " + DatabaseString.account_packageName + "))";
        db.execSQL(q4);


        String q5="CREATE TABLE "+ DatabaseString.tab_contact+ " (" +
                DatabaseString.contact_timestamp + " TEXT," +
                DatabaseString.contact_contactId + " TEXT," +
                DatabaseString.contact_contactName + " TEXT, " +
                DatabaseString.contact_contactedTimes + " TEXT," +
                DatabaseString.contact_starred + " TEXT, " +
                DatabaseString.contact_contactPhoneNumbers + " TEXT, " +
                DatabaseString.contact_send + " TEXT, " +
                "PRIMARY KEY(" + DatabaseString.contact_timestamp + ", " + DatabaseString.contact_contactId + "))";
        db.execSQL(q5);


        String q6="CREATE TABLE "+ DatabaseString.tab_activity+ " (" +
                DatabaseString.activity_timestamp + " TEXT," +
                DatabaseString.activity_inVehicle + " TEXT," +
                DatabaseString.activity_onBicycle + " TEXT," +
                DatabaseString.activity_onFoot + " TEXT," +
                DatabaseString.activity_running + " TEXT," +
                DatabaseString.activity_still + " TEXT," +
                DatabaseString.activity_tilting + " TEXT," +
                DatabaseString.activity_walking + " TEXT," +
                DatabaseString.activity_unknown + " TEXT," +
                DatabaseString.activity_send + " TEXT, " +
                "PRIMARY KEY(" + DatabaseString.activity_timestamp + "))";
        db.execSQL(q6);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {  }

}