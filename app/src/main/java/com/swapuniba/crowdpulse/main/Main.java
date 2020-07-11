package com.swapuniba.crowdpulse.main;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.swapuniba.crowdpulse.R;

import com.swapuniba.crowdpulse.business_object.*;

import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.config.ControlPanel;
import com.swapuniba.crowdpulse.database.DbManager;
import com.swapuniba.crowdpulse.debug.DebugActivity;
import com.swapuniba.crowdpulse.reactive.DailyCheck;
import com.swapuniba.crowdpulse.reactive.DataObject;
import com.swapuniba.crowdpulse.reactive.HandlerReactive;
import com.swapuniba.crowdpulse.utility.NotificationUtility;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;


public class Main extends Activity {

    private static final String TAG = "Main";
    private static final boolean SHOW_DEBUG = true;

    static Boolean confirmExit = false;

    View view ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        view = findViewById(R.id.main);

        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ControlPanel.class);
                startActivity(intent);
            }
        });


        Button button_logout = (Button) findViewById(R.id.button_logout);
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Login.cancelLogin(getApplicationContext());
                Intent intent = new Intent(getApplicationContext(), Intro.class);
                startActivity(intent);
            }
        });


        if(!SHOW_DEBUG){
            nascondiImpostazioniDebug();
        }



        checkDailyCheck();

        checkDailyNow();



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SendDataWorker.waitExceeded(getApplicationContext())){
            showSendPopup();
        }

    }

    @Override
    public void onBackPressed() {

        if(confirmExit){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else {

            confirmExit = true;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    confirmExit = false;
                }
            }, 2000);

            NotificationUtility.showToast(getApplicationContext(), getString(R.string.press_again_exit));
        }
    }

    /**
     * Mostra il popup per scegliere se inviare subito i dati o aspettare
     */
    private void showSendPopup(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_send , null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView , width , height , true);
        popupWindow.setElevation(30);
        popupWindow.setOutsideTouchable(false);
        findViewById(R.id.ll_main).post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(findViewById(R.id.ll_main) , Gravity.CENTER , 0 , 0);
            }
        });
        popupView.findViewById(R.id.btnWait).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupView.findViewById(R.id.btnSaveNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //invia al server
                avviaSubitoInvioAlServer();
                popupWindow.dismiss();
            }
        });
    }

    /**
     * Reattiva se necessario il lavoro del controllo giornaliero
     */
    private void checkDailyCheck(){
        if(DailyCheck.problem(getApplicationContext())){
            DailyCheck.cancel(getApplicationContext());
            DailyCheck.createAndEnqueue(getApplicationContext());
            Toast.makeText(getApplicationContext() , "Controllo giornaliero reattivato" , Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Avvia subito il lavoro di invio al server e ne inserisce in coda uno identico periodico
     * con un'attesa di 6 ore  .
     */
    public void avviaSubitoInvioAlServer(){

        //Avvio subito il lavoro
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest sendWork = new OneTimeWorkRequest.Builder(SendDataWorker.class).build();
        workManager.enqueue(sendWork);



    }

    /**
     * Avvia subito il controllo giornaliero
     */
    public void checkDailyNow(){

        DailyCheck.createAndExecute(getApplicationContext());
    }

    public void nascondiImpostazioniDebug(){
        Button btn = findViewById(R.id.btnGoDebug);
        btn.setVisibility(View.GONE);
    }

    /**
     * Per avviare l'activity che si occupa di mostrare alcuni dati per il debug
     * @param view
     */
    public void goDebug(View view){
        Intent intent = new Intent(getApplicationContext() , DebugActivity.class);
        startActivity(intent);
    }



}



