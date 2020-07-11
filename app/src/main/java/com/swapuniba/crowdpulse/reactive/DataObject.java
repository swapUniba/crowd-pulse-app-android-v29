package com.swapuniba.crowdpulse.reactive;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Questa classe è usata per gestire gli orari di salvataggio ed esecuzione di lavori e allarmi
 */
public class DataObject {

    private static final String TAG = "DataObject";
    JsonOggetto jsonOggetto;

    private static final String PATTERN_DATE = "yyyy.MM.dd - HH:mm:ss.SSS";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN_DATE , Locale.ITALIAN);

    /**
     * Recupera i dati save e execute e avvia il metodo logicReactive(long e)
     * @return
     */
    public  boolean toReactive(){

       long s = 0 ,e = 0;
       try {
           if(jsonOggetto!=null) {
               s = dateFormat.parse(jsonOggetto.save).getTime();
               e = dateFormat.parse(jsonOggetto.execute).getTime();
           }
       }catch (Exception exc){
           Log.e(TAG, "toReactive: la data non è stata convertita in long in maniera corretta" , exc.fillInStackTrace());
           return  true;
       }

       return  logicReactive(e);

   }

    /**
     * Controlla se il tempo attuale e superiore al tempo 'e'
     * @param e
     * @return
     */
   private static boolean logicReactive(long e){
       if( e==0 || e<0){
           return true;
       }else{


           if(e < System.currentTimeMillis()){
               return true;
           }else{
               return false;
           }

       }


   }

    /**
     * Creazione a partire da dei long (millisecondi)
     * @param save
     * @param execute
     */
    public DataObject(long save , long execute){
        jsonOggetto = new JsonOggetto();
        jsonOggetto.save = getDataFromMills(save);
        jsonOggetto.execute = getDataFromMills(execute);
    }

    public DataObject(JsonOggetto jsonOggetto){
        this.jsonOggetto = jsonOggetto;
    }



    public JsonOggetto getJsonOggetto(){
        return jsonOggetto;
    }

    private String getDataFromMills(long mls){
        String data = "NULL";

        try{
            data = dateFormat.format(mls);
        }catch (Exception e){
            Log.e(TAG, "Errore nell'assegnamento della data" );
        }

        return data;
    }


    /**
     * DataObject json da usare con le librerie GSON
     */
    public class JsonOggetto{
        private String save = "NULL" ;
        private String execute = "NULL";

    }
    @Override
    public String toString(){
        return "Save: " + jsonOggetto.save + ", Execute: " + jsonOggetto.execute;
    }





}
