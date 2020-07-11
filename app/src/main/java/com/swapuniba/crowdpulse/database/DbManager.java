package com.swapuniba.crowdpulse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;


import com.swapuniba.crowdpulse.business_object.AbstractData;
import com.swapuniba.crowdpulse.business_object.Account;
import com.swapuniba.crowdpulse.business_object.ActivityData;
import com.swapuniba.crowdpulse.business_object.AppInfo;
import com.swapuniba.crowdpulse.business_object.Contact;
import com.swapuniba.crowdpulse.business_object.Display;
import com.swapuniba.crowdpulse.business_object.GPS;
import com.swapuniba.crowdpulse.business_object.NetStats;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.ArrayList;

/**
 * Created by fabio on 20/04/16.
 */
public class DbManager
{
    private DbHelper dbhelper;

    public DbManager(Context ctx)
    {
        dbhelper = new DbHelper(ctx);
    }

    public void clearSendData(){
        Utility.printLog("TAG-M-DB-DELETE","Sto cancellando i dati");
        deleteGPSSend();
        deleteNetStatsSend();
        deleteAppInfoSend();
        deleteDisplaySend();
        deleteAccountSend();
        deleteContactSend();


    }


    // GPS

    public Boolean saveGps(GPS gps)
    {
        Boolean done = false;
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.gps_timestamp, gps.timestamp);
        cv.put(DatabaseString.gps_accuracy, gps.accuracy);
        cv.put(DatabaseString.gps_latitude, gps.latitude);
        cv.put(DatabaseString.gps_longitude, gps.longitude);
        cv.put(DatabaseString.gps_speed, gps.speed);
        cv.put(DatabaseString.gps_accuracy, gps.accuracy);
        if(gps.send){
            cv.put(DatabaseString.gps_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.gps_send, Constants.string_false);
        }

        try
        {
            db.insert(DatabaseString.tab_gps, null, cv);

            done = true;
        }
        catch (Exception e)
        {
            Log.i("Test:", "Eccezione Db saveGPS");
            done = false;
        }
        finally {
            db.close();
        }

        return done;
    }


    public Boolean updateGPS(GPS gps)
    {
        Boolean done = false;

        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.gps_timestamp, gps.timestamp);
        cv.put(DatabaseString.gps_accuracy, gps.accuracy);
        cv.put(DatabaseString.gps_latitude, gps.latitude);
        cv.put(DatabaseString.gps_longitude, gps.longitude);
        cv.put(DatabaseString.gps_speed, gps.speed);
        cv.put(DatabaseString.gps_accuracy, gps.accuracy);
        if(gps.send){
        cv.put(DatabaseString.gps_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.gps_send, Constants.string_false);
        }

        try
        {
            db.update(DatabaseString.tab_gps, cv, DatabaseString.gps_timestamp + "=?", new String[]{gps.timestamp});
            done = true;

        }
        catch (SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db updateGPS");
            done = false;
        }
        finally {
            db.close();
        }
        return done;
    }


    public GPS getGPS(String date){

        GPS gps;
        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_gps, null, DatabaseString.gps_timestamp + "=?", new String[]{date}, null, null, null, null);

            if (cursor .moveToFirst()) {

                Boolean send = false;
                if(cursor.getString(cursor.getColumnIndex(DatabaseString.gps_send)).equalsIgnoreCase(Constants.string_true)){
                    send = true;
                }
                gps = new GPS(
                        cursor.getString(cursor.getColumnIndex(DatabaseString.gps_timestamp)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.gps_latitude)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.gps_longitude)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.gps_speed)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.gps_accuracy)),
                        send
                );

            }
            else{
                gps = null;
            }

        }
        catch(SQLiteException sqle)
        {
            gps = null;
            Log.i("Test:", "Eccezione Db getGPS");
        }
        finally {
            db.close();
        }

        return gps;
    }



    public ArrayList<GPS> getGPS(){

        ArrayList<GPS> gps_list  = new ArrayList<GPS>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_gps, null, null, null, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.gps_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    gps_list.add(
                            new GPS(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.gps_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.gps_latitude)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.gps_longitude)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.gps_speed)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.gps_accuracy)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getGPS_list");
            gps_list = null;
        }
        finally {
            db.close();
        }
        return gps_list;
    }

    public ArrayList<GPS> getNotSendGPS(){

        ArrayList<GPS> gpsArrayList  = new ArrayList<GPS>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_gps, null, DatabaseString.gps_send+ "=?", new String[]{Constants.string_false}, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.gps_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    gpsArrayList.add(
                            new GPS(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.gps_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.gps_latitude)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.gps_longitude)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.gps_speed)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.gps_accuracy)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getNotSendGPS");
            gpsArrayList = null;
        }
        finally {
            db.close();
        }
        return gpsArrayList;
    }


    public boolean deleteGPSSend(){

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        boolean fatto = true;

        try
        {
            db.delete(DatabaseString.tab_gps, DatabaseString.gps_send + "=? AND " + DatabaseString.gps_timestamp + " <?", new String[]{Constants.string_true, Utility.threeDaysAgoTimestamp()});
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }
        return fatto;
    }


    public boolean deleteAllGPS(){
        SQLiteDatabase db=dbhelper.getReadableDatabase();
        boolean fatto = true;
        try
        {
            db.delete(DatabaseString.tab_gps, null, null);
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }

        return fatto;
    }






    // NetStats

    public Boolean saveNetStats(NetStats netStats)
    {
        Boolean done = false;
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.netstats_networkType, netStats.networkType);
        cv.put(DatabaseString.netstats_timestamp, netStats.timestamp);
        cv.put(DatabaseString.netstats_rxBytes, netStats.rxBytes);
        cv.put(DatabaseString.netstats_txBytes, netStats.txBytes);
        if(netStats.send){
            cv.put(DatabaseString.netstats_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.netstats_send, Constants.string_false);
        }

        try
        {
            db.insert(DatabaseString.tab_netstats, null, cv);

            done = true;
        }
        catch (Exception e)
        {
            Log.i("Test:", "Eccezione Db saveNetStats");
            done = false;
        }
        finally {
            db.close();
        }

        return done;
    }


    public Boolean updateNetStats(NetStats netStats)
    {
        Boolean done = true;
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.netstats_networkType, netStats.networkType);
        cv.put(DatabaseString.netstats_timestamp, netStats.timestamp);
        cv.put(DatabaseString.netstats_rxBytes, netStats.rxBytes);
        cv.put(DatabaseString.netstats_txBytes, netStats.txBytes);
        if(netStats.send){
            cv.put(DatabaseString.netstats_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.netstats_send, Constants.string_false);
        }

        try
        {
            db.update(DatabaseString.tab_netstats, cv,
                    DatabaseString.netstats_networkType + "=? AND " + DatabaseString.netstats_timestamp + " =?", new String[]{netStats.networkType, netStats.timestamp});

        }
        catch (SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db updateNetStats");
            done = false;
        }
        finally {
            db.close();
        }

        return done;
    }


    public NetStats getNetStat(String type, String date){

        NetStats netStats;
        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_netstats, null,
                    DatabaseString.netstats_networkType + "=? AND " + DatabaseString.netstats_timestamp + "=?", new String[]{type, date}, null, null, null, null);

            if (cursor .moveToFirst()) {

                Boolean send = false;
                if(cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_send)).equalsIgnoreCase(Constants.string_true)){
                    send = true;
                }
                netStats = new NetStats(
                        cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_networkType)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_timestamp)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_rxBytes)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_txBytes)),
                        send
                );

            }
            else{
                netStats = null;
            }

        }
        catch(SQLiteException sqle)
        {
            netStats = null;
            Log.i("Test:", "Eccezione Db getNetStats");
        }
        finally {
            db.close();
        }

        return netStats;
    }



    public ArrayList<NetStats> getAllNetStats(){

        ArrayList<NetStats> netStats_list  = new ArrayList<NetStats>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_netstats, null, null, null, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    netStats_list.add(
                            new NetStats(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_networkType)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_rxBytes)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_txBytes)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getAllNetStats");
            netStats_list = null;
        }
        finally {
            db.close();
        }
        return netStats_list;
    }

    public ArrayList<NetStats> getNotSendNetStats(){

        ArrayList<NetStats> netStatsArrayList  = new ArrayList<NetStats>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_netstats, null, DatabaseString.netstats_send+ "=?", new String[]{Constants.string_false}, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    netStatsArrayList.add(
                            new NetStats(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_networkType)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_rxBytes)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.netstats_txBytes)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getNotSendNetStats");
            netStatsArrayList = null;
        }
        finally {
            db.close();
        }
        return netStatsArrayList;
    }


    public boolean deleteNetStatsSend(){

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        boolean fatto = true;

        try
        {
            db.delete(DatabaseString.tab_netstats, DatabaseString.netstats_send + "=? AND " + DatabaseString.netstats_timestamp + " <? ", new String[]{Constants.string_true, Utility.threeDaysAgoTimestamp()});
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }
        return fatto;
    }


    public boolean deleteAllNetStats(){
        SQLiteDatabase db=dbhelper.getReadableDatabase();
        boolean fatto = true;
        try
        {
            db.delete(DatabaseString.tab_netstats, null, null);
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }

        return fatto;
    }




    // AppInfo

    public Boolean saveAppInfo(AppInfo appInfo)
    {
        Boolean done = false;
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.appinfo_packageName, appInfo.packageName);
        cv.put(DatabaseString.appinfo_timestamp, appInfo.timestamp);
        cv.put(DatabaseString.appinfo_category, appInfo.category);
        cv.put(DatabaseString.appinfo_foregroundTime, appInfo.foregroundTime);
        if(appInfo.send){
            cv.put(DatabaseString.appinfo_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.appinfo_send, Constants.string_false);
        }

        try
        {
            db.insert(DatabaseString.tab_appinfo, null, cv);

            done = true;
        }
        catch (Exception e)
        {
            Log.i("Test:", "Eccezione Db saveAppInfo");
            done = false;
        }
        finally {
            db.close();
        }

        return done;
    }


    public Boolean updateAppInfo(AppInfo appInfo)
    {
        Boolean done = true;

        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.appinfo_packageName, appInfo.packageName);
        cv.put(DatabaseString.appinfo_timestamp, appInfo.timestamp);
        cv.put(DatabaseString.appinfo_category, appInfo.category);
        cv.put(DatabaseString.appinfo_foregroundTime, appInfo.foregroundTime);
        if(appInfo.send){
            cv.put(DatabaseString.appinfo_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.appinfo_send, Constants.string_false);
        }

        try
        {
            db.update(DatabaseString.tab_appinfo, cv,
                    DatabaseString.appinfo_packageName + "=? AND " + DatabaseString.appinfo_timestamp + "=?", new String[]{appInfo.packageName, appInfo.timestamp});

            done = true;

        }
        catch (SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db updateAppInfo");
            done = false;
        }
        finally {
            db.close();
        }

        return done;
    }


    public AppInfo getAppInfo(String id_package, String date){

        AppInfo appInfo;
        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_appinfo, null,
                    DatabaseString.appinfo_packageName + "=? AND " + DatabaseString.appinfo_timestamp + "=?", new String[]{id_package, date}, null, null, null, null);

            if (cursor .moveToFirst()) {

                Boolean send = false;
                if(cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_send)).equalsIgnoreCase(Constants.string_true)){
                    send = true;
                }
                appInfo = new AppInfo(
                        cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_packageName)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_timestamp)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_category)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_foregroundTime)),
                        send
                );

            }
            else{
                appInfo = null;
            }

        }
        catch(SQLiteException sqle)
        {
            appInfo = null;
            Log.i("Test:", "Eccezione Db getAppInfo");
        }
        finally {
            db.close();
        }

        return appInfo;
    }



    public ArrayList<AppInfo> getAllAppInfo(){

        ArrayList<AppInfo> appInfo_list  = new ArrayList<AppInfo>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_appinfo, null, null, null, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    appInfo_list.add(
                            new AppInfo(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_packageName)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_category)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_foregroundTime)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getAllAppInfo");
            appInfo_list = null;
        }
        finally {
            db.close();
        }
        return appInfo_list;
    }

    public ArrayList<AppInfo> getNotSendAppInfo(){

        ArrayList<AppInfo> appInfoArrayList  = new ArrayList<AppInfo>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_appinfo, null, DatabaseString.appinfo_send+ "=?", new String[]{Constants.string_false}, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    appInfoArrayList.add(
                            new AppInfo(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_packageName)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_category)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.appinfo_foregroundTime)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getNotSendAppInfo");
            appInfoArrayList = null;
        }
        finally {
            db.close();
        }
        return appInfoArrayList;
    }


    public boolean deleteAppInfoSend(){

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        boolean fatto = true;

        try
        {
            db.delete(DatabaseString.tab_appinfo, DatabaseString.appinfo_send + "=? AND " + DatabaseString.appinfo_timestamp + " <? ", new String[]{Constants.string_true, Utility.threeDaysAgoTimestamp()});
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }
        return fatto;
    }


    public boolean deleteAllAppInfo(){
        SQLiteDatabase db=dbhelper.getReadableDatabase();
        boolean fatto = true;
        try
        {
            db.delete(DatabaseString.tab_appinfo, null, null);
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }

        return fatto;
    }



    // Display

    public Boolean saveDisplay(Display display)
    {
        Boolean done = false;
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.display_timestamp, display.timestamp);
        cv.put(DatabaseString.display_state, display.state);
        if(display.send){
            cv.put(DatabaseString.display_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.display_send, Constants.string_false);
        }

        try
        {
            db.insert(DatabaseString.tab_display, null, cv);

            done = true;
        }
        catch (Exception e)
        {
            Log.i("Test:", "Eccezione Db saveDisplay");
            done = false;
        }
        finally {
            db.close();
        }

        return done;
    }


    public void updateDisplay(Display display)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.display_timestamp, display.timestamp);
        cv.put(DatabaseString.display_state, display.state);
        if(display.send){
            cv.put(DatabaseString.display_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.display_send, Constants.string_false);
        }

        try
        {
            db.update(DatabaseString.tab_display, cv,
                    DatabaseString.display_timestamp + "=?", new String[]{display.timestamp});

        }
        catch (SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db updateDisplay");
        }
        finally {
            db.close();
        }
    }

    public Display getLastDisplay(){

        Display display;
        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {
            cursor = db.query(DatabaseString.tab_display, null, null, new String[]{}, null, null,
                    DatabaseString.display_timestamp + " DESC", "1");

            if (cursor .moveToFirst()) {

                Boolean send = false;
                if(cursor.getString(cursor.getColumnIndex(DatabaseString.display_send)).equalsIgnoreCase(Constants.string_true)){
                    send = true;
                }
                display = new Display(
                        cursor.getString(cursor.getColumnIndex(DatabaseString.display_timestamp)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.display_state)),
                        send
                );

            }
            else{
                display = null;
            }

        }
        catch(SQLiteException sqle)
        {
            display = null;
            Log.i("Test:", "Eccezione Db getLastDisplay");
        }
        finally {
            db.close();
        }

        return display;
    }

    public Display getDisplay(String date){

        Display display;
        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {
            cursor = db.query(DatabaseString.tab_display, null,
                    DatabaseString.display_timestamp + "=?", new String[]{date}, null, null, null, null);

            if (cursor .moveToFirst()) {

                Boolean send = false;
                if(cursor.getString(cursor.getColumnIndex(DatabaseString.display_send)).equalsIgnoreCase(Constants.string_true)){
                    send = true;
                }
                display = new Display(
                        cursor.getString(cursor.getColumnIndex(DatabaseString.display_timestamp)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.display_state)),
                        send
                );

            }
            else{
                display = null;
            }

        }
        catch(SQLiteException sqle)
        {
            display = null;
            Log.i("Test:", "Eccezione Db getDisplay");
        }
        finally {
            db.close();
        }

        return display;
    }



    public ArrayList<Display> getAllDisplay(){

        ArrayList<Display> display_list  = new ArrayList<Display>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_display, null, null, null, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.display_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    display_list.add(
                            new Display(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.display_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.display_state)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getAllDisplay");
            display_list = null;
        }
        finally {
            db.close();
        }
        return display_list;
    }

    public ArrayList<Display> getNotSendDisplay(){

        ArrayList<Display> displayArrayList  = new ArrayList<Display>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_display, null, DatabaseString.display_send+ "=?", new String[]{Constants.string_false}, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.display_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    displayArrayList.add(
                            new Display(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.display_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.display_state)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getNotSendDisplay");
            displayArrayList = null;
        }
        finally {
            db.close();
        }
        return displayArrayList;
    }


    public boolean deleteDisplaySend(){

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        boolean fatto = true;

        try
        {
            db.delete(DatabaseString.tab_display, DatabaseString.display_send + "=? AND " + DatabaseString.display_timestamp + " <? ", new String[]{Constants.string_true, Utility.threeDaysAgoTimestamp()});
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }
        return fatto;
    }


    public boolean deleteAllDisplay(){
        SQLiteDatabase db=dbhelper.getReadableDatabase();
        boolean fatto = true;
        try
        {
            db.delete(DatabaseString.tab_display, null, null);
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }

        return fatto;
    }





    // Account

    public Boolean saveAccount(Account account)
    {
        Boolean done = false;
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.account_timestamp, account.timestamp);
        cv.put(DatabaseString.account_userAccountName, account.userAccountName);
        cv.put(DatabaseString.account_packageName, account.packageName);
        if(account.send){
            cv.put(DatabaseString.account_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.account_send, Constants.string_false);
        }

        try
        {
            db.insert(DatabaseString.tab_account, null, cv);

            done = true;
        }
        catch (Exception e)
        {
            Log.i("Test:", "Eccezione Db saveAccount");
            done = false;
        }
        finally {
            db.close();
        }

        return done;
    }


    public void updateAccount(Account account)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.account_timestamp, account.timestamp);
        cv.put(DatabaseString.account_userAccountName, account.userAccountName);
        cv.put(DatabaseString.account_packageName, account.packageName);
        if(account.send){
            cv.put(DatabaseString.account_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.account_send, Constants.string_false);
        }

        try
        {
            db.update(DatabaseString.tab_account, cv,
                    DatabaseString.account_timestamp + "=? AND " + DatabaseString.account_userAccountName + "=? AND " + DatabaseString.account_packageName + "=?",
                    new String[]{account.timestamp, account.userAccountName, account.packageName});

        }
        catch (SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db updateAccount");
        }
        finally {
            db.close();
        }
    }


    public Account getAccount(String timestamp, String accountUserName, String packageName){

        Account account;
        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_account, null,
                    DatabaseString.account_timestamp + "=? AND " + DatabaseString.account_userAccountName + "=? AND " + DatabaseString.account_packageName + "=?",
                    new String[]{timestamp, accountUserName, packageName}, null, null, null, null);

            if (cursor .moveToFirst()) {

                Boolean send = false;
                if(cursor.getString(cursor.getColumnIndex(DatabaseString.account_send)).equalsIgnoreCase(Constants.string_true)){
                    send = true;
                }
                account = new Account(
                        cursor.getString(cursor.getColumnIndex(DatabaseString.account_timestamp)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.account_userAccountName)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.account_packageName)),
                        send
                );

            }
            else{
                account = null;
            }

        }
        catch(SQLiteException sqle)
        {
            account = null;
            Log.i("Test:", "Eccezione Db getAccount");
        }
        finally {
            db.close();
        }

        return account;
    }


    public ArrayList<Account> getAllAccount(){

        ArrayList<Account> accountArrayList  = new ArrayList<Account>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_account, null, null, null, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.account_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    accountArrayList.add(
                            new Account(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.account_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.account_userAccountName)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.account_packageName)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getAllAccount");
            accountArrayList = null;
        }
        finally {
            db.close();
        }
        return accountArrayList;
    }

    public ArrayList<Account> getNotSendAccount(){

        ArrayList<Account> accountArrayList  = new ArrayList<Account>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_account, null, DatabaseString.account_send+ "=?", new String[]{Constants.string_false}, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.account_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    accountArrayList.add(
                            new Account(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.account_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.account_userAccountName)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.account_packageName)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getNotSendAccount");
            accountArrayList = null;
        }
        finally {
            db.close();
        }
        return accountArrayList;
    }


    public boolean deleteAccountSend(){

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        boolean fatto = true;

        try
        {
            db.delete(DatabaseString.tab_account, DatabaseString.account_send + "=? AND " + DatabaseString.account_timestamp + " <? ", new String[]{Constants.string_true, Utility.threeDaysAgoTimestamp()});
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }
        return fatto;
    }


    public boolean deleteAllAccount(){
        SQLiteDatabase db=dbhelper.getReadableDatabase();
        boolean fatto = true;
        try
        {
            db.delete(DatabaseString.tab_account, null, null);
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }

        return fatto;
    }



    // Contact

    public Boolean saveContact(Contact contact)
    {
        Boolean done = false;
        SQLiteDatabase db=dbhelper.getWritableDatabase();


        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.contact_timestamp, contact.timestamp);
        cv.put(DatabaseString.contact_contactId, contact.contactId);
        cv.put(DatabaseString.contact_contactName, contact.contactName);
        cv.put(DatabaseString.contact_contactedTimes, contact.contactedTimes);
        cv.put(DatabaseString.contact_starred, contact.starred);
        cv.put(DatabaseString.contact_contactPhoneNumbers, Utility.serialize(contact.contactPhoneNumbers)); //IS SERIALIZAED !!!!!

        if(contact.send){
            cv.put(DatabaseString.contact_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.contact_send, Constants.string_false);
        }

        try
        {
            db.insert(DatabaseString.tab_contact, null, cv);

            done = true;
        }
        catch (Exception e)
        {
            Log.i("Test:", "Eccezione Db saveContact");
            done = false;
        }
        finally {
            db.close();
        }

        return done;
    }


    public void updateContact(Contact contact)
    {
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.contact_timestamp, contact.timestamp);
        cv.put(DatabaseString.contact_contactId, contact.contactId);
        cv.put(DatabaseString.contact_contactName, contact.contactName);
        cv.put(DatabaseString.contact_contactedTimes, contact.contactedTimes);
        cv.put(DatabaseString.contact_starred, contact.starred);
        cv.put(DatabaseString.contact_contactPhoneNumbers, Utility.serialize(contact.contactPhoneNumbers)); //IS SERIALIZAED !!!!!

        if(contact.send){
            cv.put(DatabaseString.contact_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.contact_send, Constants.string_false);
        }

        try
        {
            db.update(DatabaseString.tab_contact, cv,
                    DatabaseString.contact_timestamp + "=? AND " + DatabaseString.contact_contactId + "=?",
                    new String[]{contact.timestamp, contact.contactId});

        }
        catch (SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db updateContact");
        }
        finally {
            db.close();
        }
    }


    public Contact getContact(String timestamp, String contactId){

        Contact contact;
        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_contact, null,
                    DatabaseString.contact_timestamp + "=? AND " + DatabaseString.contact_contactId + "=?",
                    new String[]{timestamp, contactId}, null, null, null, null);

            if (cursor .moveToFirst()) {

                Boolean send = false;
                if(cursor.getString(cursor.getColumnIndex(DatabaseString.contact_send)).equalsIgnoreCase(Constants.string_true)){
                    send = true;
                }
                contact = new Contact(
                        cursor.getString(cursor.getColumnIndex(DatabaseString.contact_timestamp)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactId)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactName)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactedTimes)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.contact_starred)),
                        Utility.derialize(cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactPhoneNumbers))),
                        send
                );

            }
            else{
                contact = null;
            }

        }
        catch(SQLiteException sqle)
        {
            contact = null;
            Log.i("Test:", "Eccezione Db getContact");
        }
        finally {
            db.close();
        }

        return contact;
    }


    public ArrayList<Contact> getAllContact(){

        ArrayList<Contact> contactArrayList  = new ArrayList<Contact>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_contact, null, null, null, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.contact_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    contactArrayList.add(
                            new Contact(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.contact_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactId)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactName)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactedTimes)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.contact_starred)),
                                    Utility.derialize(cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactPhoneNumbers))),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getAllContact");
            contactArrayList = null;
        }
        finally {
            db.close();
        }
        return contactArrayList;
    }

    public ArrayList<Contact> getNotSendContact(){

        ArrayList<Contact> contactArrayList  = new ArrayList<Contact>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_contact, null, DatabaseString.contact_send+ "=?", new String[]{Constants.string_false}, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.contact_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    contactArrayList.add(
                            new Contact(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.contact_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactId)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactName)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactedTimes)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.contact_starred)),
                                    Utility.derialize(cursor.getString(cursor.getColumnIndex(DatabaseString.contact_contactPhoneNumbers))),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getNotSendContact");
            contactArrayList = null;
        }
        finally {
            db.close();
        }
        return contactArrayList;
    }


    public boolean deleteContactSend(){

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        boolean fatto = true;

        try
        {
            db.delete(DatabaseString.tab_contact, DatabaseString.contact_send + "=? AND " + DatabaseString.contact_timestamp + " <? ", new String[]{Constants.string_true, Utility.threeDaysAgoTimestamp()});
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }
        return fatto;
    }


    public boolean deleteAllContact(){
        SQLiteDatabase db=dbhelper.getReadableDatabase();
        boolean fatto = true;
        try
        {
            db.delete(DatabaseString.tab_contact, null, null);
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }

        return fatto;
    }



    // Activity

    public Boolean saveActivity(ActivityData activity)
    {
        Boolean done = false;
        SQLiteDatabase db=dbhelper.getWritableDatabase();


        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.account_timestamp, activity.timestamp);
        cv.put(DatabaseString.activity_inVehicle, activity.inVehicle);
        cv.put(DatabaseString.activity_onBicycle, activity.onBicycle);
        cv.put(DatabaseString.activity_onFoot, activity.onFoot);
        cv.put(DatabaseString.activity_running, activity.running);
        cv.put(DatabaseString.activity_still, activity.still);
        cv.put(DatabaseString.activity_tilting, activity.tilting);
        cv.put(DatabaseString.activity_walking, activity.walking);
        cv.put(DatabaseString.activity_unknown, activity.unknown);

        if(activity.send){
            cv.put(DatabaseString.activity_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.activity_send, Constants.string_false);
        }

        try
        {
            db.insert(DatabaseString.tab_activity, null, cv);

            done = true;
        }
        catch (Exception e)
        {
            Log.i("Test:", "Eccezione Db saveActivity");
            done = false;
        }
        finally {
            db.close();
        }

        return done;
    }


    public Boolean updateActivity(ActivityData activity)
    {
        Boolean done = false;
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DatabaseString.account_timestamp, activity.timestamp);
        cv.put(DatabaseString.activity_inVehicle, activity.inVehicle);
        cv.put(DatabaseString.activity_onBicycle, activity.onBicycle);
        cv.put(DatabaseString.activity_onFoot, activity.onFoot);
        cv.put(DatabaseString.activity_running, activity.running);
        cv.put(DatabaseString.activity_still, activity.still);
        cv.put(DatabaseString.activity_tilting, activity.tilting);
        cv.put(DatabaseString.activity_walking, activity.walking);
        cv.put(DatabaseString.activity_unknown, activity.unknown);

        if(activity.send){
            cv.put(DatabaseString.activity_send, Constants.string_true);
        }
        else {
            cv.put(DatabaseString.activity_send, Constants.string_false);
        }

        try
        {
            db.update(DatabaseString.tab_activity, cv,
                    DatabaseString.activity_timestamp + "=?",
                    new String[]{activity.timestamp});

            done = true;
        }
        catch (SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db updateActivity");
            done = false;
        }
        finally {
            db.close();
        }
        return done;
    }


    public ActivityData getActivity(String timestamp){

        ActivityData activity;
        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_activity, null,
                    DatabaseString.activity_timestamp + "=?",
                    new String[]{timestamp}, null, null, null, null);

            if (cursor .moveToFirst()) {

                Boolean send = false;
                if(cursor.getString(cursor.getColumnIndex(DatabaseString.activity_send)).equalsIgnoreCase(Constants.string_true)){
                    send = true;
                }
                activity = new ActivityData(
                        cursor.getString(cursor.getColumnIndex(DatabaseString.activity_timestamp)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.activity_inVehicle)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.activity_onBicycle)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.activity_onFoot)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.activity_running)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.activity_still)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.activity_tilting)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.activity_walking)),
                        cursor.getString(cursor.getColumnIndex(DatabaseString.activity_unknown)),
                        send
                );

            }
            else{
                activity = null;
            }

        }
        catch(SQLiteException sqle)
        {
            activity = null;
            Log.i("Test:", "Eccezione Db getActivity");
        }
        finally {
            db.close();
        }

        return activity;
    }


    public ArrayList<ActivityData> getAllActivity(){

        ArrayList<ActivityData> activityArrayList  = new ArrayList<ActivityData>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_activity, null, null, null, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.activity_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    activityArrayList.add(
                            new ActivityData(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_inVehicle)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_onBicycle)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_onFoot)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_running)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_still)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_tilting)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_walking)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_unknown)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getAllActivity");
            activityArrayList = null;
        }
        finally {
            db.close();
        }
        return activityArrayList;
    }

    public ArrayList<ActivityData> getNotSendActivity(){

        ArrayList<ActivityData> activityArrayList  = new ArrayList<ActivityData>();

        Cursor cursor;

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        try
        {

            cursor = db.query(DatabaseString.tab_activity, null, DatabaseString.activity_send+ "=?", new String[]{Constants.string_false}, null, null, null, null);

            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {

                    Boolean send = false;
                    if(cursor.getString(cursor.getColumnIndex(DatabaseString.activity_send)).equalsIgnoreCase(Constants.string_true)){
                        send = true;
                    }
                    activityArrayList.add(
                            new ActivityData(
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_timestamp)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_inVehicle)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_onBicycle)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_onFoot)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_running)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_still)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_tilting)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_walking)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseString.activity_unknown)),
                                    send
                            )
                    );

                    cursor.moveToNext();
                }
            }

        }
        catch(SQLiteException sqle)
        {
            Log.i("Test:", "Eccezione Db getNotSendActivity");
            activityArrayList = null;
        }
        finally {
            db.close();
        }
        return activityArrayList;
    }


    public boolean deleteCActivitySend(){

        SQLiteDatabase db=dbhelper.getReadableDatabase();

        boolean fatto = true;

        try
        {
            db.delete(DatabaseString.tab_activity, DatabaseString.account_send + "=? AND " + DatabaseString.activity_timestamp + " <? ", new String[]{Constants.string_true, Utility.threeDaysAgoTimestamp()});
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }
        return fatto;
    }


    public boolean deleteAllActivity(){
        SQLiteDatabase db=dbhelper.getReadableDatabase();
        boolean fatto = true;
        try
        {
            db.delete(DatabaseString.tab_activity, null, null);
        }
        catch(SQLiteException sqle)
        {
            fatto = false;
        }
        finally {
            db.close();
        }

        return fatto;
    }


}