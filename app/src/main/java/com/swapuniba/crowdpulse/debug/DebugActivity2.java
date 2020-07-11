package com.swapuniba.crowdpulse.debug;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;

import com.swapuniba.crowdpulse.R;
import com.swapuniba.crowdpulse.business_object.Account;
import com.swapuniba.crowdpulse.business_object.ActivityData;
import com.swapuniba.crowdpulse.business_object.AppInfo;
import com.swapuniba.crowdpulse.business_object.Contact;
import com.swapuniba.crowdpulse.business_object.Display;
import com.swapuniba.crowdpulse.business_object.GPS;
import com.swapuniba.crowdpulse.business_object.NetStats;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.database.DbManager;
import com.swapuniba.crowdpulse.main.Login;
import com.swapuniba.crowdpulse.main.SendDataWorker;
import com.swapuniba.crowdpulse.reactive.DailyCheck;
import com.swapuniba.crowdpulse.reactive.HandlerReactive;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *Questa activity permette un ulteriore livello di testing.
 * Le funzionalità presenti permettono:
 * - stampa di un log contentente i dati selezionati (checkbox)
 * - setta i tempi di estrazione a 0 per i dati selezionti (checkbox)
 * - falsifica i tempi relativi all'allarme e al lavoro
 * - controllare immediatamente se l'allarme e il lavoro hanno problemi
 * - visualizzare la spiegazione dei permessi una volta riaperta la app
 */
public class DebugActivity2 extends AppCompatActivity {

    CheckBox cb_account ,  cb_account_db;
    CheckBox cb_activity , cb_activity_db;
    CheckBox cb_appinfo , cb_appinfo_db;
    CheckBox cb_contacts , cb_contacts_db;
    CheckBox cb_display , cb_display_db;
    CheckBox cb_gps , cb_gps_db;
    CheckBox cb_netstatt , cb_netstatt_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug2);

        //Per il time
        cb_account  = (CheckBox) findViewById(R.id.cb_account);
        cb_activity = (CheckBox) findViewById(R.id.cb_activity);
        cb_appinfo  = (CheckBox) findViewById(R.id.cb_appinfo);
        cb_contacts = (CheckBox) findViewById(R.id.cb_contacts);
        cb_display  = (CheckBox) findViewById(R.id.cb_display);
        cb_gps      = (CheckBox) findViewById(R.id.cb_gps);
        cb_netstatt = (CheckBox) findViewById(R.id.cb_newtork_stats);

        //Per il log del database
        cb_account_db  = (CheckBox) findViewById(R.id.cb_account_db);
        cb_activity_db = (CheckBox) findViewById(R.id.cb_activity_db);
        cb_appinfo_db  = (CheckBox) findViewById(R.id.cb_appinfo_db);
        cb_contacts_db = (CheckBox) findViewById(R.id.cb_contacts_db);
        cb_display_db  = (CheckBox) findViewById(R.id.cb_display_db);
        cb_gps_db      = (CheckBox) findViewById(R.id.cb_gps_db);
        cb_netstatt_db = (CheckBox) findViewById(R.id.cb_newtork_stats_db);
    }


    //TAG-M
    public void clickLogDB(View view) {
        DbManager dbManager = new DbManager(getApplicationContext());
        ArrayList<Account> accounts = dbManager.getAllAccount();
        ArrayList<ActivityData> activityDatas = dbManager.getAllActivity();
        ArrayList<AppInfo> appInfos = dbManager.getAllAppInfo();
        ArrayList<Contact> contacts = dbManager.getAllContact();
        ArrayList<Display> displays = dbManager.getAllDisplay();
        ArrayList<GPS> gpss = dbManager.getGPS();
        ArrayList<NetStats> netStatss = dbManager.getAllNetStats();

        if (cb_account_db.isChecked()) {
            if (!accounts.isEmpty()) {
                Utility.printLog("TAG-M-DB-PRINT-ACCOUNTS", "Size: " + accounts.size() + " Accounts: " + accounts.toString());
            } else {
                Utility.printLog("TAG-M-DB-PRINT-ACCOUNTS", "Accounts: Vuoto");
            }
        }
        if (cb_activity_db.isChecked()) {
            if (!activityDatas.isEmpty()) {
                Utility.printLog("TAG-M-DB-PRINT-ACTY", "Size: " + activityDatas.size() + " Attività" + activityDatas.toString());
            } else {
                Utility.printLog("TAG-M-DB-PRINT-ACTY", "Attività: Vuoto");
            }
        }
        if (cb_appinfo_db.isChecked()) {
            Utility.printLog("TAG-M-DB-ISCHECKED", "Sono nell'if");
            if (!appInfos.isEmpty()) {
                Utility.printLog("TAG-M-DB-PRINT-AI", "Size: " + appInfos.size() + " AppInfo" + appInfos.toString());
            } else {
                Utility.printLog("TAG-M-DB-PRINT-AI", "AppInfo: Vuoto");
            }
        }else{
            Utility.printLog("TAG-M-DB-ISCHECKED", "Non Sono nell'if");
        }
        if (cb_contacts_db.isChecked()) {
            if (!contacts.isEmpty()) {
                Utility.printLog("TAG-M-DB-PRINT-CONT", "Size: " + contacts.size() + " Contatti" + contacts.toString());
            } else {
                Utility.printLog("TAG-M-DB-PRINT-CONT", "Contatti: Vuoto");
            }
        }
        if (cb_display_db.isChecked()) {
            if (!displays.isEmpty()) {
                Utility.printLog("TAG-M-DB-PRINT-DY", "Size: " + displays.size() + " Display" + displays.toString());
            } else {
                Utility.printLog("TAG-M-DB-PRINT-DY", "Display: Vuoto");
            }
        }
        if (cb_gps_db.isChecked()) {
            if (!gpss.isEmpty()) {
                Utility.printLog("TAG-M-DB-PRINT-GPS", "Size: " + gpss.size());
                for(GPS gps : gpss)
                    Utility.printLog("TAG-M-DB-PRINT-GPS","Gps: " + gps.toString());
            } else {
                Utility.printLog("TAG-M-DB-PRINT-GPS", "Gps: Vuoto");
            }
        }
        if (cb_netstatt_db.isChecked()){
            if (!netStatss.isEmpty()) {

                Utility.printLog("TAG-M-DB-PRINT-NET", "Size: " + netStatss.size() );
                for(NetStats netStats : netStatss)
                    Utility.printLog("TAG-M-DB-PRINT-NET",  "NetStats: " + netStats.toString());


            } else {
                Utility.printLog("TAG-M-DB-PRINT-NET", "NetStats: Vuoto");
            }
        }


    }
    //TAG-M
    /**
     * Permette di settare a 0 il tempo che bisogna attendere per estrarre i dati
     * @param view
     */
    public void clickSetTimeToZero(View view){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        if(cb_account.isChecked()){
            editor.putString(Constants.last_accounts_send , "0");
        }
        if(cb_activity.isChecked()){
            editor.putString(Constants.last_activity_send , "0");
        }
        if(cb_appinfo.isChecked()){
            editor.putString(Constants.last_app_send , "0");
        }
        if(cb_contacts.isChecked()){
            editor.putString(Constants.last_contacts_send , "0");
        }
        if(cb_display.isChecked()){
            editor.putString(Constants.last_display_send , "0");
        }
        if(cb_gps.isChecked()){
            editor.putString(Constants.last_gps_send , "0");
        }
        if(cb_netstatt.isChecked()){
            editor.putString(Constants.last_netstats_send , "0");
        }

        editor.apply();
    }

    /**
     * Una volta cliccato verra resettato subito l'allarme per l'estrazione e il lavoro per l'invio
     * al server, inoltre verrai rimandato alla schermata di login
     * @param view
     */
    public void clickResetPrimoAccesso(View view){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        WorkManager wm = WorkManager.getInstance(getApplicationContext());
        wm.cancelAllWork();
        wm.pruneWork();

        preferences.edit().clear().apply();
        Login.cancelLogin(getApplicationContext());

        Intent intent = new Intent(getApplicationContext() , Login.class);
        startActivity(intent);




    }

    public void clickFalsificaLavoro(View view){
        HandlerReactive.falsificaSend(getApplicationContext());
    }

    public void clickFalsificaAllarme(View view){
        HandlerReactive.falsificaAllarme(getApplicationContext());
    }

    public void clickExplanationPermisisons(View view){
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putString(Constants.explanation_permissions , Constants.string_true)
                .apply();
    }

    public void clickChecDailyNow(View view){
        DailyCheck.createAndExecute(getApplicationContext());
    }

    public void clickAvviaTra5Minuti(View view){
        SendDataWorker.cancel(getApplicationContext());
        SendDataWorker.createAndEnqueue(
                getApplicationContext() ,
                5 ,
                TimeUnit.MINUTES ,
                true
                );
    }

}
