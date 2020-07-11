package com.swapuniba.crowdpulse.handlers;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.swapuniba.crowdpulse.business_object.AbstractData;
import com.swapuniba.crowdpulse.business_object.AppInfo;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.database.DbManager;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.ArrayList;

import java.util.List;


//TAG-M


public class AppInfoHandler {


    /**
     * IMPORTANT!!!
     * if android > 21 we can get Time in foreground else no,
     * for android > 21 we can get YESTERDAY app stats
     * else we can get only TODAY app installed
     *
     * @param context
     * @return
     */
    public static ArrayList<AppInfo> readAppInfo(Context context) {


        ArrayList<AppInfo> appInfoArrayList = new ArrayList<AppInfo>();

        long yesterdayMidnight = Utility.yesterdayMidnightTimestamp();
        long todayMidnight = Utility.currentMidnightTimestamp();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

            List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  System.currentTimeMillis()  - 86400000  ,Utility.getCurrentHHMills(System.currentTimeMillis())); //TAG-M modifica da todayMidnight


            //List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY  ,yesterdayMidnight , todayMidnight); //TAG-M modifica da todayMidnight

            for (UsageStats u : queryUsageStats){

                AppInfo appInfo = new AppInfo();

                appInfo.packageName = u.getPackageName();
                appInfo.foregroundTime = u.getTotalTimeInForeground() + "";
                appInfo.timestamp = yesterdayMidnight + ""; //PAY ATTENTION!!!
                appInfo.send = false;
                appInfo.print();
                //testare da zero
                appInfoArrayList.add(appInfo);

            }

        }

        else {
            PackageManager pm = context.getPackageManager();
            //get a list of installed apps.
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo packageInfo : packages) {

                AppInfo appInfo = new AppInfo();
                appInfo.packageName = packageInfo.packageName;
                appInfo.timestamp = todayMidnight + ""; // PAY ATTENTION!!!
                appInfo.foregroundTime = null;
                appInfo.send = false;

                appInfo.print();

                appInfoArrayList.add(appInfo);

            }
        }

        return appInfoArrayList;
    }

    /**
     * check if is pass the right time between 2 get
     * @param context
     * @return
     */
    public static Boolean checkTimeBetweenRequest(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  Long.parseLong(preferences.getString(Constants.last_app_send, "0")) < System.currentTimeMillis();

    }

    /**
     * check the next time to register the data
     * @param context
     * @return
     */
    public static void setNetxTime(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        //tomorrow Midnight Timestamp
        Long netxTime = Long.parseLong(preferences.getString(Constants.setting_time_read_app, "0")) + Utility.currentMidnightTimestamp();//System.currentTimeMillis();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.last_app_send, netxTime+ "");
        editor.apply();

    }


    public static Boolean saveAppInfo(AppInfo appInfo, Context context){
        Boolean done = true;

        DbManager db = new DbManager(context);

        AppInfo dbAppInfo = db.getAppInfo(appInfo.packageName, appInfo.timestamp);

        if (dbAppInfo == null){
            done = db.saveAppInfo(appInfo);
        }
        else {
            //if false nothing to update
            if (appInfo.send != false){
                done = db.updateAppInfo(appInfo);
            }

        }


        return done;
    }


    public static Boolean saveAppInfoArray(ArrayList<AppInfo> appInfoArrayList, Context context){
        Boolean done = true;

        for (AppInfo appInfo : appInfoArrayList){
            saveAppInfo(appInfo, context);
        }


        return done;

    }

    public static ArrayList<? extends AbstractData> getNotSendAppInfo(Context context){
        DbManager dbManager = new DbManager(context);
        return dbManager.getNotSendAppInfo();
    }



}