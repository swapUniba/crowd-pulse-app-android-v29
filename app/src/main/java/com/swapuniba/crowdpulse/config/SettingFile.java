package com.swapuniba.crowdpulse.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.swapuniba.crowdpulse.comunication.SocketApplication;
import com.swapuniba.crowdpulse.handlers.DeviceInfoHandler;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.socket.client.Ack;
import io.socket.client.Socket;

import static android.content.ContentValues.TAG;



public class SettingFile {

    private static final String TAG = "SettingFile";


    public static HashMap<String, String> getSettings(Context context){

        HashMap<String, String> settings_map= new HashMap<String, String>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        for (String preference : Constants.setting_keys){
            //if (Constants.setting_permission_keys.contains(preference)){
                settings_map.put(preference, preferences.getString(preference, Constants.default_setting.get(preference)));
            //}
        }

        return settings_map;

    }

    /**
     *
     * @param key
     * @param value
     * @param context
     * @param source 0 if from this smarthone, 1 from UI
     * @return
     */
    public static Boolean setSetting(String key, String value, Context context, int source){

        Boolean is_valid = false;

        if (Constants.setting_keys.contains(key)){
            //check value param consistence

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.apply();

            Utility.printLog("Update default_setting " + key + " with value: " + value);

            Utility.printLog(getSettings(context).toString());

            is_valid = true;
        }

        if (is_valid){
            if(source == Constants.sourceThisSmarthone){
                Socket socket = SocketApplication.getSocket();
                socket.emit(Constants.channel_config, getJSON(context));
                Log.i(TAG, "setSetting emettendo , getJSON: " + getJSON(context).toString());
            }
        }


        return is_valid;

    }



    public static void inizialize(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();


        for(String setting_key :Constants.setting_keys){
            String value = preferences.getString(setting_key, "");
            if (value.equalsIgnoreCase("")){
                editor.putString(setting_key, Constants.default_setting.get(setting_key));
            }
        }
        editor.apply();

    }

    /**
     * Recupera  tutte le configurazioni settate nel pannello di controllo , device Id e username aggiungendole in un jsonObject.
     * @param context
     * @return il jsonObject con i dati memorizzati
     */
    public static JSONObject getJSON(Context context){

        JSONObject jsonObject = new JSONObject();



        HashMap<String, String> settings = getSettings(context);
        try {
            for (String settingKey : settings.keySet()){
                    jsonObject.put(settingKey, settings.get(settingKey));
                }
            jsonObject.put(Constants.j_deviceinfo_deviceId, DeviceInfoHandler.readDeviceInfo(context).deviceId);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            jsonObject.put(Constants.j_username, preferences.getString(Constants.pref_username, ""));

            } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }





}
