package com.swapuniba.crowdpulse.debug;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
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
import com.swapuniba.crowdpulse.reactive.DataObject;
import com.swapuniba.crowdpulse.reactive.HandlerReactive;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Questa activity è accessibile solo se si è impostata su true la varaibile SHOW_DEBUG presente nel Main.
 * In questa Activity vengono mostrati alcuni bottoni e una textview.
 * Ogni bottone permette di stampare nella textview i dati salvati nelle varie classi Info presenti
 * in questo package.
 * Un  bottone che permette di cancellare tutte le info, le quali verranno perse.
 * Un bottone per spostarsi nel debug2.
 */
public class DebugActivity extends AppCompatActivity {

    private static final String TAG = "DebugActivity";

    TextView log;

    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        log = findViewById(R.id.txtShow);
        gson = new Gson();


    }

    /**
     * Cancella tutte le info presenti, quindi quelle relative al pre e post invio , quelle relative
     * all'estrazione e quelle relative all'attivazione degli allarmi.
     * @param view
     */
    public void cancelAllDetailFromPreference(View view){
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putString(Constants.tag_m_bs_data, "NULL")
                .apply();

        WorkManager.getInstance(getApplicationContext()).pruneWork();
    }

    /**
     * Stampa nella textview i dati presenti in AlarmiInfo
     * @param view
     */
    public void showAlarmDetailProcess(View view){

        String json;
        //lo prendo dalle preferenze
        json = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.tag_m_bs_data, "NULL");

        if(!TextUtils.equals(json , "NULL")){
            AlarmInfo alarmInfo = gson.fromJson(json , AlarmInfo.class);

            log.setText(alarmInfo.toString());

        }else{
            log.setText("Nessuna informazione relativa all'esecuzione degli allarmi per l'estrazione dei dati");
        }

    }

    /**
     * Stampa nella textview le informazioni sui lavori.
     * Più precisamente aiuta a capire quali lavori sono stati eseguiti con successo, quali sono in
     * coda e quali sono stati cancellati.
     * Avviso: Se ci sono più lavori di invio al server o di controllo giornaliero in coda (ENQUEUED)
     * allora c'è un problema.
     * @param view
     */
    public void showWorkDetailWorks(View view) {

        StringBuilder sBuilderSend = new StringBuilder();
        StringBuilder sBuilderDelayCheck = new StringBuilder();


        try {
            List<WorkInfo> wInfoSend = WorkManager.getInstance(getApplicationContext())
                    .getWorkInfosByTag(Constants.tag_send_data_work)
                    .get();

            for (WorkInfo workInfo : wInfoSend) {
                sBuilderSend
                        .append("Lavoro per inviare i dati al server ogni 6 ore \n")
                        .append("Stato attuale: ")
                        .append(workInfo.getState().toString())
                        .append("\n");
                DataObject times = HandlerReactive.getWorkSend(getApplicationContext());
                if (times != null) {
                    sBuilderSend.append(times);
                } else {
                    sBuilderSend.append("Non ci sono dati per i tempi \n");
                }
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            List<WorkInfo> wInfoSend = WorkManager.getInstance(getApplicationContext())
                    .getWorkInfosByTag(Constants.tag_reactive_work)
                    .get();

            for (WorkInfo workInfo : wInfoSend) {
                sBuilderDelayCheck
                        .append("Lavoro per effettuare il reset ogni 24 ore quando il telefono è carica \n")
                        .append("Stato attuale: ")
                        .append(workInfo.getState().toString())
                        .append("\n");
                DataObject times = HandlerReactive.getWorkReactive(getApplicationContext());
                if (times != null) {
                    sBuilderDelayCheck.append(times);
                } else {
                    sBuilderDelayCheck.append("Non ci sono dati per i tempi \n");
                }
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (sBuilderSend.length() == 0) {
            sBuilderSend.append("Non ci sono informazioni relative all'invio dei dati , dovresti reinserire il lavoro");

        }
        if (sBuilderDelayCheck.length() == 0) {
                sBuilderDelayCheck.append("Non ci sono informazioni relative al controllo giornaliero , dovresti reinserire il lavoro");
        }

        Log.i(TAG, "showWorkDetailWorks send " + sBuilderSend.length() + " delay " + sBuilderDelayCheck.length());
        log.setText(" ");
        log.setText(sBuilderSend.toString());
        log.append("\n\n");
        log.append(sBuilderDelayCheck.toString());



    }

    /**
     * Stampa nella textview i dati presenti in BeforeSendInfo
     * @param view
     */
    public void showDetailBeforeSend(View view){
        String jsonBeforeSend = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.tag_m_before_send_data, "NULL");

        if(!TextUtils.equals(jsonBeforeSend , "NULL")){
            BeforeSendInfo beforeSendInfo = gson.fromJson(jsonBeforeSend , BeforeSendInfo.class);
            log.setText(beforeSendInfo.toString());
        }else{
            log.setText("Nessuna informazione relativa alla preparazione dell'invio dei dati");
        }
    }

    /**
     * Stampa nella textview i dati presenti in AfterSendInfo
     * @param view
     */
    public void showDetailAfterSend(View view){
        String jsonAfterSend = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.tag_m_after_send_data, "NULL");

        if(!TextUtils.equals(jsonAfterSend , "NULL")){
            AfterSendInfo afterSendInfo = gson.fromJson(jsonAfterSend , AfterSendInfo.class);
            log.setText(afterSendInfo.toString());
        }else{
            log.setText("Nessuna informazione relativa alla ricezione dei dati inviati");
        }
    }

    /**
     * Stampa nella textview i dati presenti in DailyCheckInfo
     * @param view
     */
    public void showDetailDailyCheck(View view){
        String jsonDailyCheck = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.tag_m_daily_check_data, "NULL");

        if(!TextUtils.equals(jsonDailyCheck , "NULL")){
            DailyCheckInfo afterSendInfo = gson.fromJson(jsonDailyCheck , DailyCheckInfo.class);
            log.setText(afterSendInfo.toString());
        }else{
            log.setText("Nessuna informazione relativa al lavoro di reattivazione , probabilmente non è stato ancora attivato");
        }
    }

    /**
     * Permette di spostarsi sull'activity DebugActivity2
     * @param view
     */
    public void goDebug2(View view){
        startActivity(new Intent(getApplicationContext() , DebugActivity2.class));
    }





}
