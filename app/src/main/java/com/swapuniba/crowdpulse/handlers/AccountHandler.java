package com.swapuniba.crowdpulse.handlers;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;

import com.swapuniba.crowdpulse.business_object.AbstractData;
import com.swapuniba.crowdpulse.business_object.Account;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.comunication.SocketApplication;
import com.swapuniba.crowdpulse.database.DbManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;


public class AccountHandler {


    public static ArrayList<Account> readAccounts(Context context) {

        ArrayList<Account> accounts = new ArrayList<Account>();

        AccountManager am = AccountManager.get(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            //Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        android.accounts.Account[] acc = am.getAccounts();
        if (acc.length > 0) {

            for (int i = 0; i < acc.length; i++) {

                Account account = new Account();

                account.timestamp = System.currentTimeMillis() + "";
                account.userAccountName = acc[i].name;
                account.packageName = acc[i].type;
                account.send = false;

                account.print();

                accounts.add(account);

            }
        }

        return accounts;
    }


    public static JSONArray accountsJSONArray(ArrayList<Account> accountArrayList){
        JSONArray jsonArray = new JSONArray();

        for (Account a : accountArrayList){
            jsonArray.put(a.toJSON());
        }

        return jsonArray;

    }

    /**
     * check if is pass the right time between 2 get
     * @param context
     * @return
     */
    public static Boolean checkTimeBetweenRequest(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return  Long.parseLong(preferences.getString(Constants.last_accounts_send, "0")) < System.currentTimeMillis();

    }

    /**
     * check the next time to register the data
     * @param context
     * @return
     */
    public static void setNetxTime(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Long netxTime = Long.parseLong(preferences.getString(Constants.setting_time_read_accounts, "0")) + System.currentTimeMillis();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.last_accounts_send, netxTime+ "");
        editor.apply();

    }


    public static Boolean send(ArrayList<Account> accountArrayList, Context context){

        JSONObject obj = new JSONObject();
        try {
            obj.put(Constants.j_deviceinfo_deviceId, DeviceInfoHandler.readDeviceInfo(context).deviceId);
            obj.put(Constants.j_type_account, accountsJSONArray(accountArrayList));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Socket socket = SocketApplication.getSocket();

        socket.emit(Constants.channel_send_data, obj);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        long new_time =  Long.parseLong(
                                    preferences.getString(Constants.setting_time_read_accounts,
                                    Constants.default_setting.get(Constants.setting_time_read_accounts))
                                        ) + System.currentTimeMillis();
        editor.putString(Constants.last_accounts_send, new_time + "");
        editor.apply();


        return true;
        
    }

    public static Boolean saveAccount(Account account, Context context){
        Boolean done = true;

        DbManager db = new DbManager(context);

        Account dbAccount = db.getAccount(account.timestamp, account.userAccountName, account.packageName);

        if (dbAccount == null){
            done = db.saveAccount(account);
        }
        else {
            db.updateAccount(account);
        }

        return done;
    }


    public static Boolean saveAccountArray(ArrayList<Account> accountArrayList, Context context){
        Boolean done = true;

        for (Account account : accountArrayList){
            saveAccount(account, context);
        }

        return done;

    }

    public static ArrayList<? extends AbstractData> getNotSendAccount(Context context){
        DbManager dbManager = new DbManager(context);
        return dbManager.getNotSendAccount();
    }





}