package com.swapuniba.crowdpulse.debug;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Si occupa di contenere alcune informazioni riferite al post-invio al server.
 * Informazioni:
 * - quando viene ricevuta una  chiamata dal server ( dopo aver inviato i dati )
 * - se la ricezione avviene correttamente
 * - quanti dati ha rievuto il server
 * - in che bucket si trova in questo momento l'applicazione
 */
public class DatoAfterSend {
    public static final String RIC_YES = "Ricezione avvenuta con successo";
    public static final String RIC_NO = "Ricezione non avvenuta";
    public static final String NUM_DATI_NONE = "-1";

   public String data;
   public String ricezione;
   public String num_dati_inviati;
   public String bucket;


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
                +"-Ricezione:"+"\n"
                +ricezione+"\n"
                +"-Numero dati inviati correttamente:" + "\n"
                +num_dati_inviati+"\n"
                +"-Bucket:"+"\n"
                +bucket+"\n";
    }
}
