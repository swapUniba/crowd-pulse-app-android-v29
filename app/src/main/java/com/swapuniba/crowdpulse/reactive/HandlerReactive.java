package com.swapuniba.crowdpulse.reactive;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextDirectionHeuristic;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.config.SettingFile;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;
import java.util.UUID;

/**
 * In questa classe sono presenti tutti i metodi necessari a gestire le reattivazioni dei lavori e
 * dell'allarme
 */
public class HandlerReactive {


    /**
     * Permette di capire in che intervallo di tempo ci si trova
     * @return 1 se ci si trova tra le 00 e 03 , 2 se ci si trova tra le 03 e 06 , 3 se siamo al
     * di fuori di questi intervalli
     */
    public static int timeToSleep(){
        Calendar c = Calendar.getInstance();

        if(c.after(c_00_00()) && c.before(c_03_00())){
                return Constants.FIRST_SLEEP;
        }
        else if(c.after(c_03_00()) && c.before(c_06_00())){
                return Constants.SECOND_SLEEP;
        }
        else{
                return Constants.SLEEP_OFF;
        }

    }

    /**
     * Salva nelle preferenze l'UUID dell'attuale lavoro di invio dei dati al server attivo
     * @param context
     * @param uuid
     */
    public static void saveWorkSend(Context context , UUID uuid){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(Constants.work_reactive_send_uuid , uuid.toString()).apply();

    }

    /**
     * Salva nelle preferenze l'UUID dell'attuale lavoro per il controllo giornaliero
     * @param context
     * @param uuid
     */
    public static void saveWorkDelayCheck(Context context , UUID uuid){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(Constants.work_delay_check_uuid , uuid.toString()).apply();
    }

    /**
     * Salva nelle preferenze i tempi di esecuzione e prossima reattivazione dell'allarme (BackgroundService)
     * @param context
     * @param execute data in millisecondi della prossima reattivazione
     */
    public static void saveAlarmBS(Context context , long execute){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        DataObject dataObject = new DataObject(System.currentTimeMillis() , execute);
        Gson gson = new Gson();
        String dataObjectString = gson.toJson(dataObject.getJsonOggetto());
        editor.putString(Constants.alarm_reactive_bs , dataObjectString);
        editor.apply();
    }

    /**
     * Salva nelle preferenze i tempi di esecuzione e prossima reattivazione del lavoro di
     * invio dei dati al server (SendDataWorker)
     * @param context
     * @param execute data in millisecondi della prossima reattivazione
     */
    public static void saveWorkSend(Context context , long execute){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        DataObject dataObject = new DataObject(System.currentTimeMillis() , execute);
        Gson gson = new Gson();
        String dataObjectString = gson.toJson(dataObject.getJsonOggetto());
        editor.putString(Constants.work_reactive_send , dataObjectString);
        editor.apply();
    }

    /**
     * Salva nelle preferenze i tempi di esecuzione e prossima reattivazione del lavoro per il
     * controllo giornaliero (DailyCheck)
     * @param context
     * @param execute data in millisecondi della prossima reattivazione
     */
    public static void saveWorkDelayCheck(Context context , long execute){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        DataObject dataObject = new DataObject(System.currentTimeMillis() , execute);
        Gson gson = new Gson();
        String dataObjectString = gson.toJson(dataObject.getJsonOggetto());
        editor.putString(Constants.work_reactive_delaycheck , dataObjectString);
        editor.apply();
    }

    /**
     * PER IL DEBUG
     * Falsifica le date di creazione ed esecuzione dell'allarme (BackgroundService).
     * @param context
     */
    public static void falsificaAllarme(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        DataObject dataObject = new DataObject(System.currentTimeMillis() - Constants.time_one_day , System.currentTimeMillis() -  (Constants.time_one_day / 2));
        Gson gson = new Gson();
        String dataObjectString = gson.toJson(dataObject.getJsonOggetto());
        editor.putString(Constants.alarm_reactive_bs , dataObjectString);
        editor.apply();
    }


    /**
     * PER IL DEBUG
     * Falsifica le date di creazione ed esecuzione del lavoro (SendDataWorker)
     * @param context
     */
    public static void falsificaSend(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        DataObject dataObject = new DataObject(System.currentTimeMillis() - Constants.time_one_day , System.currentTimeMillis() -  (Constants.time_one_day / 2));
        Gson gson = new Gson();
        String dataObjectString = gson.toJson(dataObject.getJsonOggetto());
        editor.putString(Constants.work_reactive_send , dataObjectString);
        editor.apply();
    }

    /**
     * Restituisce la data di creazione ed esecuzione dell'allarme
     * @param context
     * @return
     */
    public static DataObject getAlarmBS(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sharedPreferences.getString(Constants.alarm_reactive_bs , "NULL");
        if(!TextUtils.equals(json , "NULL")){
            Gson gson = new Gson();
            DataObject dataObject = new DataObject(gson.fromJson(json , DataObject.JsonOggetto.class));
            return dataObject;
        }

        return null;
    }

    /**
     * Restituisce la data di creazione ed esecuzione del lavoro per il controllo giornaliero (DailyCheck)
     * @param context
     * @return
     */
    public static DataObject getWorkReactive(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sharedPreferences.getString(Constants.work_reactive_delaycheck , "NULL");
        if(!TextUtils.equals(json , "NULL")){
            Gson gson = new Gson();
            DataObject dataObject = new DataObject(gson.fromJson(json , DataObject.JsonOggetto.class));
            return dataObject;
        }

        return null;
    }

    /**
     * Restituisce la data di creazione ed esecuzione del lavoro di invio dei dati al server (SendDataWorker)
     * @param context
     * @return
     */
    public static DataObject getWorkSend(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sharedPreferences.getString(Constants.work_reactive_send , "NULL");
        if(!TextUtils.equals(json , "NULL")){
            Gson gson = new Gson();
            DataObject dataObject = new DataObject(gson.fromJson(json , DataObject.JsonOggetto.class));
            return dataObject;
        }

        return null;
    }

    public static long get_c_00_00(){

        return c_00_00().getTime().getTime();

    }

    /**
     * Calcola in milliseondi la data attuale impostata alle 3 di mattina
     * @return data in millisecondi
     */
    public static long get_c_03_00(){
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY , 3);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND,0);
        c1.set(Calendar.MILLISECOND,0);
        return  c1.getTime().getTime();
    }

    /**
     * Calcola in milliseondi la data attuale impostata alle 6 di mattina
     * @return data in millisecondi
     */
    public static long get_c_06_00(){
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY , 6);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND,0);
        c1.set(Calendar.MILLISECOND,0);
        return  c1.getTime().getTime();
    }

    private static Calendar c_00_00(){

        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY , 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND,0);
        c1.set(Calendar.MILLISECOND,0);
        return  c1;

    }

    private static  Calendar c_03_00(){
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY , 2);
        c1.set(Calendar.MINUTE, 59);
        c1.set(Calendar.SECOND,59);
        c1.set(Calendar.MILLISECOND,999);
        return  c1;
    }

    private static  Calendar c_06_00(){
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY , 5);
        c1.set(Calendar.MINUTE, 59);
        c1.set(Calendar.SECOND,59);
        c1.set(Calendar.MILLISECOND,999);
        return  c1;
    }

    /**
     * Sceglie il più piccolo tra gli intervalli di tempo definiti nel Pannello di Controllo
     * @param context
     * @return il tempo in millisecondi
     */
    public static long choiseNextTime(Context context){
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);

        long nextTime = 0;
        TreeSet<Long> times = new TreeSet<>();
        HashMap<String,String> setting_time = Constants.mappingSettingTimeRead;

        //setting_time ... trovo le costanti che mi danno i tempi effettivi
        //valori di default ?
        for(Map.Entry<String,String> entry : setting_time.entrySet()){

            String key = entry.getKey(); // chiave in preference se deve essere letto o meno
            String key_s = preference.getString(key,Constants.no_record); //valore effettivo in preference
            if(TextUtils.equals(key_s , Constants.record)){ //se deve essere estratta

                String value = entry.getValue(); //il valore effettivo attualmente
                String def_value = Constants.default_setting.get(value);   //il valore di default da inserire nelle preferenze
                String value_s = preference.getString(value , def_value);
                if(value_s != null) {
                    times.add(Long.valueOf(value_s));
                }
            }
        }

        //Una volta ottenuti tutti i tempi in millisecondi


        if(times.isEmpty()){
            nextTime = Constants.restart_alarm;
        }else{
            nextTime = times.first(); //il primo sarà il più piccolo
        }



        return nextTime;
    }










}
