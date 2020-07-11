package com.swapuniba.crowdpulse.main;



import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.swapuniba.crowdpulse.business_object.DeviceInfo;
import com.swapuniba.crowdpulse.comunication.SocketApplication;
import com.swapuniba.crowdpulse.comunication.TransfertData;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.database.DbManager;
import com.swapuniba.crowdpulse.debug.DatoAfterSend;
import com.swapuniba.crowdpulse.debug.DatoBeforeSend;
import com.swapuniba.crowdpulse.debug.BeforeSendInfo;
import com.swapuniba.crowdpulse.handlers.DeviceInfoHandler;
import com.swapuniba.crowdpulse.reactive.DailyCheck;
import com.swapuniba.crowdpulse.reactive.DataObject;
import com.swapuniba.crowdpulse.reactive.HandlerReactive;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.socket.client.Socket;

/**
 * Si occupa di inviare i dati al server.
 * Questa classe estende Worker e quindi implementa doWork() nel quale verrano chiamate le funzioni e
 * i metodi per inviare i dati al server.
 */
public class SendDataWorker extends Worker {

    private static final String TAG = "SendDataWorker";
    private Context context;

    private Gson gson;
    private BeforeSendInfo sendInfo = null;
    private DatoBeforeSend datoSend = new DatoBeforeSend();

    public SendDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {



        gson = new Gson();

        String jsonSendInfo = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.tag_m_before_send_data, "NULL");
        if(!TextUtils.equals(jsonSendInfo , "NULL")){
            sendInfo = gson.fromJson(jsonSendInfo , BeforeSendInfo.class);
        }else{
            sendInfo = new BeforeSendInfo();
        }


        datoSend.insertData();
        datoSend.insertBucket(getApplicationContext());



        // Se si  è connessi alla rete allora si possono inviare i dati al server
        if (isNetworkAvaible()) {

            datoSend.internet = DatoBeforeSend.INT_CONN_YES;

            Socket socket = SocketApplication.getSocket();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();



            //Mi assicuro della connessione
            if (!socket.connected()) {


                datoSend.reattivazione_socket = DatoBeforeSend.REACT_SOCKET_YES;


                JSONObject jsonObject = new JSONObject();


                try {
                    jsonObject.put(Constants.j_email, preferences.getString(Constants.pref_email, ""));
                    jsonObject.put(Constants.j_password, preferences.getString(Constants.pref_password, ""));
                    DeviceInfo deviceInfo = DeviceInfoHandler.readDeviceInfo(getApplicationContext());
                    jsonObject.put(Constants.j_deviceinfo_deviceId, deviceInfo.deviceId);
                    jsonObject.put(Constants.j_deviceinfo_brand, deviceInfo.brand);
                    jsonObject.put(Constants.j_deviceinfo_sdk, deviceInfo.sdk);
                    jsonObject.put(Constants.j_deviceinfo_model, deviceInfo.model);
                    deviceInfo.phoneNumbers.add(preferences.getString(Constants.pref_phoneNumber, ""));

                    JSONArray jsonArrayPhoneNumbers = new JSONArray();
                    for (String phoneNumber : deviceInfo.phoneNumbers) {
                        jsonArrayPhoneNumbers.put(phoneNumber);
                    }
                    jsonObject.put(Constants.j_deviceinfo_phoneNumbers, jsonArrayPhoneNumbers);

                    socket.emit(Constants.channel_login, jsonObject);



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                Utility.printLog(TAG, "La socket è gia connessa");
            }


            waitWhileConnected(socket);

            //Invio i dati se è possibile
            if (!SocketApplication.sending) {


                SocketApplication.sending = true;

                try {

                    final TransfertData transfertData = new TransfertData(getApplicationContext());
                    if(socket.connected()) {

                        datoSend.connesione_socket = DatoBeforeSend.SOCKET_YES;
                        datoSend.num_dati_da_inviare = String.valueOf(transfertData.sizeObject());
                        transfertData.send();


                    }else{
                        Log.i(TAG, "doWork: Non sei connesso alla socket");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    SocketApplication.sending = false;
                }

            } else {
                Utility.printLog(TAG, "Sta ancora inviando");
            }

            Utility.printLog(TAG, "Fine del lavoro");

            editor.apply();

        }
        else {
            Utility.printLog(TAG, "Nessuna connessione internet");
        }

        datoSend.prossima_reattivazione =  Utility.getDataFromMillis(nextExecute());

        sendInfo.dati.add(datoSend);

        String json_ = gson.toJson(sendInfo);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putString(Constants.tag_m_before_send_data, json_)
                .apply();

        saveTime();

        // Prima di completare il lavoro, approffito dell'attivaziozion in background della app
        // per controllare che gli allarmi sono attivi.
        DailyCheck.checkBackgroundService(context);

        return Worker.Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
    }

    /**
     *
     * @return true se si è connessi ad internet , false altrimenti
     */
    private boolean isNetworkAvaible() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * Utile per il debug , salva la prossima data di attivazione
     */
    private void saveTime(){
        long nextExecute = nextExecute();
        HandlerReactive.saveWorkSend(getApplicationContext() , nextExecute);
    }

    /**
     * Restituisce in millisecondi la prossima data di esecuzione
     * @return
     */
    public static long nextExecute(){
        long nextExecute = System.currentTimeMillis() + (60000*60*Constants.send_hours);
        return  nextExecute;

    }

    /**
     * Metodo standard per ottenere il tempo futuro del ritardo
     * @return
     */
    public static long nextExecuteDelay(){
        long nextExecute = System.currentTimeMillis() + (60000*Constants.delay_send_minutes);
        return  nextExecute;
    }

    /**
     * Metodo con parametro
     * @param delay
     * @return
     */
    public static long nextExecuteDelay(int delay){
        long nextExecute = System.currentTimeMillis() + delay;
        return nextExecute;
    }

    /**
     * Crea e mette in coda il lavoro
     * @param context
     */
    public static void createAndEnqueue(Context context){

        Utility.printLog("TAG-M-","Avvio il servizio invio al server");
        WorkManager workManager = WorkManager.getInstance(context);
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest sendData = new PeriodicWorkRequest.Builder(SendDataWorker.class , Constants.send_hours , TimeUnit.HOURS )
                .setConstraints(constraints)
                .setInitialDelay(Constants.delay_send_minutes , TimeUnit.MINUTES)
                .build();


        HandlerReactive.saveWorkSend(context , SendDataWorker.nextExecuteDelay());
        HandlerReactive.saveWorkSend(context , sendData.getId());

        workManager.enqueue(sendData);
    }

    /**
     * Crea e mette in coda il lavoro definendo alcuni parametri
     * @param context
     * @param delay ritardo iniziale
     * @param timeUnit unità di tempo per il ritardo (ore, minuti o secondi)
     * @param charging se il telefono deve essere in carica o meno
     */
    public static void createAndEnqueue(Context context , int delay , TimeUnit timeUnit , boolean charging ){
        Utility.printLog("TAG-M-","Avvio il servizio invio al server");
        WorkManager workManager = WorkManager.getInstance(context);
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(charging)
                .build();

        PeriodicWorkRequest sendData = new PeriodicWorkRequest.Builder(SendDataWorker.class , Constants.send_hours , TimeUnit.HOURS )
                .setConstraints(constraints)
                .setInitialDelay(delay , timeUnit)
                .build();

        int effectiveDelay = Utility.getTime(timeUnit , delay);

        HandlerReactive.saveWorkSend(context , nextExecuteDelay(effectiveDelay));
        HandlerReactive.saveWorkSend(context , sendData.getId());

        workManager.enqueue(sendData);
    }

    /**
     * Crea ed esegue subito il lavoro
     * @param context
     */
   public static void createAndExecute(Context context){
       WorkManager workManager = WorkManager.getInstance(context);
       OneTimeWorkRequest sendWork = new OneTimeWorkRequest.Builder(SendDataWorker.class).build();
       workManager.enqueue(sendWork);
   }

    /**
     * Rimuove dalla coda tutti i lavori con il tag com.swapuniba.crowdpulse.main.SendDataWorker
     * @param context
     */
    public static void cancel(Context context){
        WorkManager wm = WorkManager.getInstance(context);
        wm.cancelAllWorkByTag(Constants.tag_send_data_work);
    }


    /**
     * Controlla i tempi di salvataggio ed esecuzione di invio al server.
     * Se ci si trova all'interno di questo intervallo allora restituisce false altrimenti se
     * ci si trova fuori (superato il tempo di esecuzione) ritorna true
     * @return
     */
    public static boolean waitExceeded(Context context){
        DataObject dataSend = HandlerReactive.getWorkSend(context);
        return  dataSend.toReactive();
    }

    /**
     * E' un ciclio che dura massimo 5 minuti , se durante l'attesa la socket si connette allora esce
     * dal ciclo , se passano i 5 minuti esce dal ciclo.
     * Se passao i 5 minuti ed esce dal ciclo molto probabilmente i dati non verranno inviati visto
     * che la socket non è connessa.
     * @param socket
     */
    private void waitWhileConnected(Socket socket){
        Log.i(TAG, "waitWhileConnected: open");
        long limit = System.currentTimeMillis() + Utility.getTime(TimeUnit.MINUTES , 5);
        while(!socket.connected()){
            SystemClock.sleep(1000);
            Log.i(TAG, "waitWhileConnected: Wait");
            if(System.currentTimeMillis() > limit){
                return;
            }
        }

        Log.i(TAG, "waitWhileConnected: close");
    }


}

