package com.swapuniba.crowdpulse.comunication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.swapuniba.crowdpulse.R;
import com.swapuniba.crowdpulse.business_object.AbstractData;
import com.swapuniba.crowdpulse.business_object.Account;
import com.swapuniba.crowdpulse.business_object.ActivityData;
import com.swapuniba.crowdpulse.business_object.AppInfo;
import com.swapuniba.crowdpulse.business_object.Contact;
import com.swapuniba.crowdpulse.business_object.Display;
import com.swapuniba.crowdpulse.business_object.GPS;
import com.swapuniba.crowdpulse.business_object.NetStats;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.debug.AfterSendInfo;
import com.swapuniba.crowdpulse.debug.BeforeSendInfo;
import com.swapuniba.crowdpulse.debug.DatoAfterSend;
import com.swapuniba.crowdpulse.debug.DatoBeforeSend;
import com.swapuniba.crowdpulse.handlers.AccountHandler;
import com.swapuniba.crowdpulse.handlers.ActivityHandler;
import com.swapuniba.crowdpulse.handlers.AppInfoHandler;
import com.swapuniba.crowdpulse.handlers.ContactHandler;
import com.swapuniba.crowdpulse.handlers.DeviceInfoHandler;
import com.swapuniba.crowdpulse.handlers.DisplayHandler;
import com.swapuniba.crowdpulse.handlers.GpsHandler;
import com.swapuniba.crowdpulse.handlers.NetStatsHandler;
import com.swapuniba.crowdpulse.utility.NotificationUtility;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.work.Constraints;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by fabio on 05/10/17.
 */

public class TransfertData {

    JSONObject jsonObject = new JSONObject();
    String dataIdentifier = null;
    static Context context;
    private static final String TAG = "TransfertData";

    /**
     * Il costruttore si occupa di creare le basi per l'invio dei dati.
     * Ogni volta che si invia qualcosa al server si crea una specie di chiave per inviare i dati.
     * La chiave è formata dall'id del dispositivo, dall'username e da un identificativo ,inoltre,
     * solo dopo viene creato un spazio (un array) per i dati.
     * @param context
     */
    public TransfertData(Context context){
        this.context = context;
        dataIdentifier =  Utility.randomString();
        try {
            jsonObject.put(Constants.j_deviceinfo_deviceId, DeviceInfoHandler.readDeviceInfo(context).deviceId);
            jsonObject.put(Constants.j_dataIdentifier, dataIdentifier);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            jsonObject.put(Constants.j_username, preferences.getString(Constants.pref_username, ""));
            jsonObject.put(Constants.j_data, new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Salva tutti i dati non ancora inviati (send su false) nell'array del jsonObject
        setData();

    }

    public void addList(ArrayList<AbstractData> arrayList ){

        if (arrayList != null && !arrayList.isEmpty()){
            JSONArray jsonArray = null;
            try {
                jsonArray = jsonObject.getJSONArray(Constants.j_data);

                for (AbstractData abstractData : arrayList){
                    jsonArray.put(abstractData.toJSON());

                    //abstractDatas.add(abstractData);
                }
                jsonObject.put(Constants.j_data, jsonArray);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void add(AbstractData abstractData){
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray(Constants.j_data);

            jsonArray.put(abstractData.toJSON());

            jsonObject.put(Constants.j_data, jsonArray);

            //abstractDatas.add(abstractData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Usa l'arraylist 'riempito' con il metodo setData() è setta su true la variabile send di
     * ogni dato.
     * Questo viene fatto per fare in modo che i dati non vengano ulteriormente inviati al server e
     * che vengano cancellati successivamente dal database locale.
     */
    void setSent(){

        JSONArray jsonArray = null;

        try {
            jsonArray = jsonObject.getJSONArray(Constants.j_data);

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String source_type = jsonObject.getString(Constants.j_source_type);

                switch (source_type){
                    case Constants.j_type_account:
                        Account account = new Account();
                        account.fromJSON(jsonObject);
                        account.send = true;
                        account.print();
                        AccountHandler.saveAccount(account, context);

                        break;

                    case Constants.j_type_appinfo:
                        AppInfo appInfo = new AppInfo();
                        appInfo.fromJSON(jsonObject);
                        appInfo.send = true;
                        appInfo.print();
                        AppInfoHandler.saveAppInfo(appInfo, context);

                        break;

                    case Constants.j_type_contact:
                        Contact contact = new Contact();
                        contact.fromJSON(jsonObject);
                        contact.send = true;
                        contact.print();
                        ContactHandler.saveContact(contact, context);

                        break;

                    case Constants.j_type_display:
                        Display display = new Display();
                        display.fromJSON(jsonObject);
                        display.send = true;
                        display.print();
                        DisplayHandler.saveDisplay(display, context);

                        break;

                    case Constants.j_type_gps:
                        GPS gps = new GPS();
                        gps.fromJSON(jsonObject);
                        gps.send = true;
                        gps.print();
                        GpsHandler.saveGPS(gps, context);

                        break;

                    case Constants.j_type_netstats:
                        NetStats netStats = new NetStats();
                        netStats.fromJSON(jsonObject);
                        netStats.send = true;
                        netStats.print();
                        NetStatsHandler.saveNetStats(netStats, context);

                        break;

                    case Constants.j_type_activity:
                        ActivityData activity = new ActivityData();
                        activity.fromJSON(jsonObject);
                        activity.send = true;
                        activity.print();
                        ActivityHandler.saveActivity(activity, context);

                        break;


                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isShare(SharedPreferences sp , String share){
        return TextUtils.equals(sp.getString(share , Constants.SHARE_OFF) , Constants.SHARE_ON);
    }

    /**
     * Carica tutti i dati presenti nel database locale non ancora inviati e cioè che hanno la
     * variabile send su false. I dati vengono caricati nell'array solo se sono condivisibili.
     */
    void setData(){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList a;
        //ACCOUNT
        if(isShare(p , Constants.setting_share_accounts)) {
            a = AccountHandler.getNotSendAccount(context);
            addList(a);
        }

        //APPINFO
        if(isShare(p , Constants.setting_share_app)) {
            a = AppInfoHandler.getNotSendAppInfo(context);
            addList(a);
        }
        //CONTACT
        if(isShare(p , Constants.setting_share_contacts)) {
            a = ContactHandler.getNotSendContact(context);
            addList(a);
        }
        //DISPLAY
        if(isShare(p,Constants.setting_share_display)) {
            a = DisplayHandler.getNotSendDisplay(context);
            addList(a);
        }
        //GPS
        if(isShare(p,Constants.setting_share_gps )) {
            a = GpsHandler.getNotSendGPS(context);
            addList(a);
        }
        //NETSTATS
        if(isShare(p,Constants.setting_share_netstats )) {
            a = NetStatsHandler.getNotSendNetStats(context);
            addList(a);
        }
        //ACTIVITY
        if(isShare(p,Constants.setting_share_activity )) {
            a = ActivityHandler.getNotSendActivity(context);
            addList(a);
        }

    }

    /**
     * Invio dei dati (del jsonObject) al server e ricezione della conferma dei dati ricevuti
     * @return true
     */
    public boolean send(){

        Boolean send = true;
        try {
            if(jsonObject.getString(Constants.j_username).equalsIgnoreCase("")){
                send = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            send = false;
        }

        if(send){

            Socket socket = SocketApplication.getSocket();

            socket.emit(Constants.channel_send_data, jsonObject);


            //Viene avviato dopo aver inviato i dati al server. Se questo sono stati ricevuti allora
            //si rieve la chiamata a questo metodo.
            Emitter.Listener onDataSend = new Emitter.Listener() {
                @Override
                public void call(Object... args) {


                    Gson gson = new Gson();
                    AfterSendInfo afterSendInfo;

                    String jsonAfterSend = PreferenceManager.getDefaultSharedPreferences(context)
                            .getString(Constants.tag_m_after_send_data , "NULL");

                    if(!TextUtils.equals(jsonAfterSend , "NULL")){
                        afterSendInfo = gson.fromJson(jsonAfterSend , AfterSendInfo.class);
                    }else{
                        afterSendInfo = new AfterSendInfo();
                    }

                    DatoAfterSend datoAfterSend = new DatoAfterSend();
                    datoAfterSend.insertData();
                    datoAfterSend.insertBucket(context);

                    datoAfterSend.ricezione = DatoAfterSend.RIC_YES;

                    try {
                        JSONObject data = (JSONObject) args[0];
                        if(data.getInt(Constants.j_code) == Constants.response_success){
                            if (data.getString(Constants.j_dataIdentifier).equalsIgnoreCase(dataIdentifier)){
                                datoAfterSend.num_dati_inviati = String.valueOf(sizeObject());
                                if(sizeObject()>0) {
                                    displayNotificationWorker(sizeObject());
                                }
                                setSent();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    afterSendInfo.dati.add(datoAfterSend);

                    String json_ = gson.toJson(afterSendInfo);
                    PreferenceManager.getDefaultSharedPreferences(context)
                            .edit()
                            .putString(Constants.tag_m_after_send_data , json_)
                            .apply();
                }
            };

            socket.on(Constants.channel_send_data, onDataSend);

        }

        return true;

    }


    public int sizeObject(){
        JSONArray jsonArray;
        try {
             jsonArray =  jsonObject.getJSONArray(Constants.j_data);
        } catch (JSONException e) {
            e.printStackTrace();
            return  0;
        }

        return jsonArray.length();
    }

    /**
     * Mostra una notifica indicante quanti dati sono stati inviati al server.
     * Se non ci sono dati inviati allora non viene mostrata.
     * @param text rappresenta il numero di dati.
     */
    private void displayNotificationWorker(int text){

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("simplifiedcoding" , "simplifiedcoding" , NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);

        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification builder = new Notification.Builder(context , "simplifiedcoding")
                    .setContentTitle("Invio dati al server")
                    .setContentText(
                            "Allunga la notifica per i dettagli" )
                    .setSmallIcon(R.drawable.logo)
                    .setStyle(new Notification.BigTextStyle().bigText("Invio al server: "+text + " dati"
                            + "\n"
                            + actualBucketApp()
                            + "\n "
                            + "data: "
                            + new SimpleDateFormat("yy/MM/dd - HH:mm:ss" , Locale.ITALIAN).format(new Date())))
                    .build();
            manager.notify(1 , builder);



            //manager.notify(1, builder.build());
        }
    }

    /**
     * Restituisce il bucket nel quale si trova in quel momento l'applicazione.
     * Solo per versioni >= 28.
     * Consultare la documentazione ufficiale per maggiori informazioni sulle variazioni che si
     * hanno in ogni bucket.
     * @return
     */
    private String actualBucketApp(){

        String bucket = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            bucket = getStringBucket(usageStatsManager.getAppStandbyBucket());
        }else{
            bucket=" ";
        }
        return bucket;
    }

    private String getStringBucket(int bucket){
        String b = String.valueOf(bucket);

        switch (bucket){
            case 10:
                b="Attivo";
                break;
            case 20:
                b="Set di lavoro";
                break;

            case 30:
                b="Frequente";
                break;

            case 40:
                b="Raro";
                break;

            default:
                break;

        }


        return  b;
    }



}
