package com.swapuniba.crowdpulse.debug;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Si occupa di contenere alcune informazioni relative al controllo giornaliero per riattivare,
 * se necessario, i servizi di estrazione ed invio al server.
 * Informazioni:
 * - quando è stato eseguito il lavoro
 * - se è stato necessario reattivare il lavoro di invio dei dati al server
 * - se è stato necessario reattivare l'allarme per l'estrazione dei dati.
 * - bucket attuale
 */
public class DatoDailyCheck {
    public static final String REACT_NO = "Reattivazione non necessaria";
    public static final String REACT_SI = "Reattivazione necessaria e avvenuta ";
    public static final String REACT_SI_PROBLEM = "Reattivazione necessaria e avvenuta dopo problemi nel recupero del lavoro";

    public String data;
    public String reattivazione_send;
    public String reattivazione_alarm;
    public String bucket;

    public DatoDailyCheck(Context context){
        reattivazione_send = REACT_NO;
        reattivazione_alarm = REACT_NO;
        bucket = actualBucketApp(context);
    }


    public void insertData(){
        String PATTERN_DATE = "yyyy.MM.dd - HH:mm:ss.SSS";
        SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN_DATE , Locale.ITALIAN);
        data = dateFormat.format(System.currentTimeMillis());

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
                +"-Reattivazione send:" + "\n"
                +reattivazione_send+"\n"
                +"-Reattivazione allarme:" + "\n"
                +reattivazione_alarm+"\n"
                +"Bucket: " + "\n"
                +bucket+"\n";
    }
}
