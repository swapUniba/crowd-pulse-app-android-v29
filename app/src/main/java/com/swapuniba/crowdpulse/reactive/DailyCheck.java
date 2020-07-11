package com.swapuniba.crowdpulse.reactive;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.debug.BeforeSendInfo;
import com.swapuniba.crowdpulse.debug.DailyCheckInfo;
import com.swapuniba.crowdpulse.debug.DatoDailyCheck;
import com.swapuniba.crowdpulse.main.BackgroundService;
import com.swapuniba.crowdpulse.main.SendDataWorker;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * E' il lavoro che si occupa di controllare sia l'allarme per l'estrazione dei dati (BackgroundService)
 * e sia l'invio dei
 * dati al server (SendDataWorker)
 */
public class DailyCheck extends Worker {
    private static final String TAG = "DailyCheck";

    Context context;
    public DailyCheck(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.i(TAG, "doWork: Avvio controllo");

        Gson gson = new Gson();
        DailyCheckInfo dailyCheckInfo;
        DatoDailyCheck datoDailyCheck;

        String jsonDailyCheck = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.tag_m_daily_check_data , "NULL");
        if(!TextUtils.equals(jsonDailyCheck , "NULL")){
            dailyCheckInfo = gson.fromJson(jsonDailyCheck , DailyCheckInfo.class);
        }else{
            dailyCheckInfo = new DailyCheckInfo();
        }

        datoDailyCheck = new DatoDailyCheck(context);
        datoDailyCheck.insertData();






        WorkManager wm = WorkManager.getInstance(context);
        //Controllo quali e quanti sono i lavori in coda con il tag com.swapuniba.crowdpulse.main.SendDataWorker
        //Se in coda c'è un solo lavoro allora non faccio niente
        //Altrimenti devo eliminare i vecchi lavori (se esistenti) e reinserire il lavoro in coda.
        //L'obiettivo è fare in modo che ci sia sempre e solo un lavoro.
        //Nota: c'è un metodo checkSendDataWorker() che fa la stessa cosa , ma non è stato utilizzato
        //per mancanza di tempo nel testarlo.
        try {
            List<WorkInfo> workInfoList = wm.getWorkInfosByTag(Constants.tag_send_data_work).get();

            int num_attivi=0;

            for(WorkInfo workInfo : workInfoList){
                if(workInfo.getState() == WorkInfo.State.ENQUEUED){
                    ++num_attivi;
                }
            }
            if(num_attivi!=1){
                datoDailyCheck.reattivazione_send = DatoDailyCheck.REACT_SI;
                wm.cancelAllWorkByTag(Constants.tag_send_data_work);
                SendDataWorker.createAndEnqueue(context);
            }

        } catch (Exception e) {
            datoDailyCheck.reattivazione_send = DatoDailyCheck.REACT_SI_PROBLEM;
            wm.cancelAllWorkByTag(Constants.tag_send_data_work);
            SendDataWorker.createAndEnqueue(context);
            e.printStackTrace();
        }


        //Reattivazione dell'allarme se necessario
        if(checkBackgroundService(context)){
            datoDailyCheck.reattivazione_alarm = DatoDailyCheck.REACT_SI;
        }

        dailyCheckInfo.dati.add(datoDailyCheck);

        String json_ = gson.toJson(dailyCheckInfo);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putString(Constants.tag_m_daily_check_data , json_)
                .apply();

        saveTime();
      return  Worker.Result.success();
    }

    /**
     * Reattiva l'allarme BackgroundService se è passato il tempo necessario alla sua attivazione
     * (ad esempio se è l'allarme doveva attivarsi 10 minuti fa)
     * @param context
     * @return
     */
    public static boolean checkBackgroundService(Context context){
        DataObject infoAlarm = HandlerReactive.getAlarmBS(context);
        if(infoAlarm.toReactive()){

            try {
                context.getApplicationContext().sendBroadcast(new Intent(context.getApplicationContext() , BackgroundService.class));
               return true;
            }catch (Exception e){
                Log.i(TAG, "startAlarm failed");
            }

        }

        return false;

    }

    /**
     * Reattivazione del lavoro SendDataWorker, guardare il corpo del metodo doWork() presente in questa
     * classe
     * @param datoDailyCheck
     * @return
     */
    private  boolean checkSendDataWorker (DatoDailyCheck datoDailyCheck){
        WorkManager wm = WorkManager.getInstance(context);

        try {
            List<WorkInfo> workInfoList = wm.getWorkInfosByTag(Constants.tag_send_data_work).get();

            int num_attivi=0;

            for(WorkInfo workInfo : workInfoList){
                if(workInfo.getState() == WorkInfo.State.ENQUEUED){
                    ++num_attivi;
                }
            }
            if(num_attivi!=1){
                wm.cancelAllWorkByTag(Constants.tag_send_data_work);
                SendDataWorker.createAndEnqueue(context);
                return  true;
            }else{
                return false;
            }

        } catch (Exception e) {
            datoDailyCheck.reattivazione_send = DatoDailyCheck.REACT_SI_PROBLEM;
            wm.cancelAllWorkByTag(Constants.tag_send_data_work);
            SendDataWorker.createAndEnqueue(context);
            e.printStackTrace();
            return true;
        }
    }

    private void saveTime(){                             //1 ora * 24
        long nextExecute = nextTime();
        HandlerReactive.saveWorkDelayCheck(getApplicationContext() , nextExecute);
    }

    public static long nextExecuteDelay(int delay){
        long nextExecute = System.currentTimeMillis() + delay; //un ora di regola
        return nextExecute;
    }

    public static long nextTime(){
        long nextExecute = System.currentTimeMillis() + (60000*60*Constants.checkDilay_hours);
        return  nextExecute;

    }

    /**
     * Crea e mette in coda un nuovo lavoro.
     * Viene messo in coda con un tempo di ritardo pari a 1 ora
     * @param context
     */
    public static void createAndEnqueue(Context context)
    {
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();


        WorkManager workManager = WorkManager.getInstance(context);
        PeriodicWorkRequest dailyCheck = new PeriodicWorkRequest.Builder(DailyCheck.class , Constants.checkDilay_hours, TimeUnit.HOURS)
                .setInitialDelay(Constants.delay_checkDaily_hours  , TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();

        workManager.enqueue(dailyCheck);

        int effectiveDelay = Utility.getTime(TimeUnit.HOURS , Constants.delay_checkDaily_hours);

        HandlerReactive.saveWorkDelayCheck(context , DailyCheck.nextExecuteDelay(effectiveDelay));
        HandlerReactive.saveWorkDelayCheck(context , dailyCheck.getId());
    }

    public static void createAndExecute(Context context){
        WorkManager workManager = WorkManager.getInstance(context);
        OneTimeWorkRequest dailyCheck = new OneTimeWorkRequest.Builder(DailyCheck.class)
                .build();
        workManager.enqueue(dailyCheck);
    }

    /**
     * Controlla il lavoro o i lavori sotto il tag com.swapuniba.crowdpulse.reactive.DailyCheck ,
     *
     * @param context
     * @return true se ci sono più lavori in coda o se sono tutti cancellati
     */
    public static boolean problem(Context context){
        WorkManager wm = WorkManager.getInstance(context);
        try{
            List<WorkInfo> workInfoList = wm.getWorkInfosByTag(Constants.tag_reactive_work).get();
            int num_attivi = 0 ;
            for(WorkInfo workInfo : workInfoList){
                if(workInfo.getState() == WorkInfo.State.ENQUEUED){
                    ++num_attivi;
                }
            }
            if(num_attivi==0 || num_attivi>1){
                return true;
               // wm.cancelAllWorkByTag(Constants.tag_reactive_work);
               // DailyCheck.createAndEnqueue(context);
            }else{
                return  false;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Rimuove dalla coda tutti lavori con il tag com.swapuniba.crowdpulse.reactive.DailyCheck
     * @param context
     */
    public static void cancel(Context context){
        WorkManager wm = WorkManager.getInstance(context);
        wm.cancelAllWorkByTag(Constants.tag_reactive_work);
    }



}
