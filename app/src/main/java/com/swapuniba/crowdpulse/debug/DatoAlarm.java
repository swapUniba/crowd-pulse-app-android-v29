package com.swapuniba.crowdpulse.debug;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Si occupa di contenere alcune informazioni riferite all'allarme per l'estrazione dei dati.
 * Informazioni:
 * - quando viene attivato l'allarme
 * - se la ricezione avviene correttamente
 * - se l'estrazione avviene correttamente e quali dati ha PROVATO ad estrarre
 * - se la riattivazione è avvenuta con successo e quando verrà riattivato il servizio
 * - il bucket in cui si trova l'applicazione
 */
public class DatoAlarm {

    String data  ;
    String bucket ;

    public Ricevitore ricevitore = new Ricevitore();
    public Estrazione estrazione =new Estrazione();
    public Riattivazione riattivazione =  new Riattivazione();
    public  DatoAlarm(){
        data = "null"; bucket="NULL";
    }


    public class Ricevitore{
        public  String attivazione = "Ricezione non avvenuta correttamente";
        public  String conclusione = "Ricezione non conclusa correttamente";
        Ricevitore(){
            attivazione = "Ricezione non avvenuta correttamente";
            conclusione = "Ricezione non conclusa correttamente";
        }

    }

    public class Estrazione{
        public String attivazione = "Estrazione non avvenuta correttamente";
        public String conclusione = "Estrazione non conclusa correttamente";
        public String dati="Nessun dato";
        Estrazione(){
            attivazione = "Estrazione non avvenuta correttamente";
            conclusione = "Estrazione non conclusa correttamente";
            dati= "Nessun dato";
        }

    }

    public class Riattivazione{
        public String attivazione = "Riattivazione non avvenuta correttamente";
        public String prossima_reattivazione = "Nessuna";
        public String conclusione = "Riattivazione non conclusa correttamente";
        Riattivazione(){
            attivazione = "Riattivazione non avvenuta correttamente";
            prossima_reattivazione = "Nessuna";
            conclusione = "Riattivazione non conclusa correttamente";
        }


        public void reattivazione(long mills){
            String PATTERN_DATE = "yyyy.MM.dd - HH:mm:ss.SSS";
            SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN_DATE , Locale.ITALIAN);
            prossima_reattivazione = dateFormat.format(mills);
        }


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

        if(TextUtils.equals(estrazione.dati , "")){
            estrazione.dati="Nessun dato estratto";
        }

        return "Data: " + data + "\n"
                + "-Ricevitore: " + "\n"
                + ricevitore.attivazione + "\n"
                + ricevitore.conclusione + "\n"
                + "-Estrazione: " + "\n"
                + estrazione.attivazione + "\n"
                + estrazione.dati + "\n"
                + estrazione.conclusione + "\n"
                + "-Riattivazione: " + "\n"
                + riattivazione.attivazione + "\n"
                + riattivazione.prossima_reattivazione + "\n"
                + riattivazione.conclusione + "\n"
                + "-Bucket:" + "\n"
                + bucket;

    }
}
