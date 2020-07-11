package com.swapuniba.crowdpulse.debug;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Si occupa di contenere alcune informazioni riferite al pre-invio dei dati al server, questo perchè
 * può accadere che i dati siano pronti ad essere inviati ma per qualche motivo (ad esempio una connessione
 * debole) non vengono inviati.
 * Informazioni:
 * - quando viene tentato l'invio al server
 * - se si è connessi o meno alla rete
 * - se si è connessi alla socket
 * - se è stata necessaria una reattivazione della socket
 * - numero di dati da inviare
 * - quando verrà reavviato il lavoro
 */
public class DatoBeforeSend {
    public static final String INT_CONN_YES = "Connesso alla rete";
    public static final String INT_CONN_NO = "Non connesso alla rete";
    public static final String REACT_NO = "Prossima reattivazione non disponibile";
    public static final String NUM_DATI_0 = "0";
    public static final String SOCKET_YES = "Connesso alla socket";
    public static final String SOCKET_NO = "Non connesso alla socket";
    public static final String REACT_SOCKET_YES = "Reattivazione necessaria ed eseguita";
    public static final String REACT_SOCKET_NO = "Reattivazione non necessaria";




    public String data;
    public String bucket;
    public String internet;
    public String connesione_socket;
    public String num_dati_da_inviare;
    public String prossima_reattivazione;
    public String reattivazione_socket;

    public DatoBeforeSend(){
        this.data = "null";
        this.bucket = "null";
        this.internet = INT_CONN_NO;
        this.connesione_socket = SOCKET_NO;
        this.num_dati_da_inviare = NUM_DATI_0;
        this.prossima_reattivazione = REACT_NO;
        this.reattivazione_socket=REACT_SOCKET_NO;
    }



    public void insertData(){
        String PATTERN_DATE = "yyyy.MM.dd - HH:mm:ss.SSS";
        SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN_DATE , Locale.ITALIAN);
        data = dateFormat.format(System.currentTimeMillis());

    }
    public void insertBucket(Context context){
        bucket = actualBucketApp(context);
    }


    private String actualBucketApp(Context context){

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

    @NonNull
    @Override
    public String toString() {
        return "-Data: " + data + "\n"
                +"-Connessione alla rete:"+"\n"
                +internet+"\n"
                +"-Reattivazione Socket:" + "\n"
                +reattivazione_socket+"\n"
                +"-Connessione socket: " + "\n"
                +connesione_socket+"\n"
                +"-Numero di dati da inviare:" + "\n"
                +num_dati_da_inviare+"\n"
                +"-Prossima reattivazione:"+"\n"
                +prossima_reattivazione+"\n"
                +"Bucket:"+"\n"
                +bucket+"\n";

    }
}
