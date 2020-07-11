package com.swapuniba.crowdpulse.handlers;

import android.Manifest;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import android.telephony.TelephonyManager;

import com.swapuniba.crowdpulse.business_object.AbstractData;
import com.swapuniba.crowdpulse.business_object.NetStats;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.database.DbManager;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.ArrayList;

/**
 * Recupera i dati di rete generati in un determinato giorno.
 * IMPORTANTE: I dati di rete non possono essere estratti nelle versioni di android superori a 28,
 * è necessario ricercare nuove implementazioni per estrarli in queste versioni.
 *
 */

public class NetStatsHandler {

    public static ArrayList<NetStats> readNetworkStats(Context context) {

        ArrayList<NetStats> netStatsArray = new ArrayList<NetStats>();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {


            NetworkStatsManager service = context.getSystemService(NetworkStatsManager.class);

            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                //un array di millisecondi
                ArrayList<Long> intervaTimeArrayList =
                        Utility.splitTime(Utility.yesterdayMidnightTimestamp(),
                                Utility.currentMidnightTimestamp(),
                                Long.parseLong(preferences.getString(Constants.setting_time_read_netstats, "0")));

                Utility.printLog("TAG-M-NSTimeStamp" , "Ieri : " + Utility.getDataFromMillis(Utility.yesterdayMidnightTimestamp()));
                Utility.printLog("TAG-M-NSTimeStamp" , "oggi : " + Utility.getDataFromMillis(Utility.currentMidnightTimestamp()));

                //24 giri , quindi 24 ore , quindi i dati di un giorno (grandezza totale 25 )
                for(int i = 0; i < intervaTimeArrayList.size() - 1; i++) {


                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                    NetStats netStats_mobile = new NetStats();

                    if(ContextCompat.checkSelfPermission( context , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        //Ottengo il bucket dove vengono salvati i dati di rete
                        NetworkStats.Bucket b = service.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, tm.getSubscriberId(),
                                intervaTimeArrayList.get(i), intervaTimeArrayList.get(i + 1));

                        netStats_mobile.networkType = Constants.TYPE_MOBILE;
                        netStats_mobile.timestamp = b.getStartTimeStamp() + "";
                        netStats_mobile.rxBytes = b.getRxBytes() + "";
                        netStats_mobile.txBytes = b.getTxBytes() + "";
                        netStats_mobile.send = false;


                        //netStats_mobile.print();

                        netStatsArray.add(netStats_mobile);

                        NetStats netStats_wifi = new NetStats();

                        b = service.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "",
                                intervaTimeArrayList.get(i), intervaTimeArrayList.get(i + 1));

                        netStats_wifi.networkType = Constants.TYPE_WIFI;
                        netStats_wifi.timestamp = b.getStartTimeStamp() + "";
                        netStats_wifi.rxBytes = b.getRxBytes() + "";
                        netStats_wifi.txBytes = b.getTxBytes() + "";
                        netStats_wifi.send = false;

                        //netStats_wifi.print();

                        netStatsArray.add(netStats_wifi);
                    }else{
                        Utility.printLog("TAG-M-REQUEST","Richiesta non accettata");
                    }
                }

            } catch (RemoteException e) {
                e.printStackTrace();
                netStatsArray = null;
            }

        }

        return netStatsArray;
    }


    /**
     * check if is pass the right time between 2 get
     * @param context
     * @return
     */
    public static Boolean checkTimeBetweenRequest(Context context){

  //      Utility.printLog("TAG-M-NS-TIME","Controllo tempo net stats");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
       /* Utility.printLog("TAG-M-NetStatsTime","last_netstats_send: " + preferences.getString(Constants.last_netstats_send, "0"));
        Utility.printLog("TAG-M-NetStatsTime","currentTimeMillis: " + System.currentTimeMillis());

        long last_netstats_send =  Long.parseLong(preferences.getString(Constants.last_netstats_send, "0"));
        long currentTimeMillis = System.currentTimeMillis();
        long result = last_netstats_send - currentTimeMillis;
        Utility.printLog("TAG-M-NetStatsTime" , "last_netstats_send - currentTimeMillis = " + result  ); */

        //solo se siamo al giorno successivo
        return  Long.parseLong(preferences.getString(Constants.last_netstats_send, "0")) < System.currentTimeMillis();

    }

    /**
     * check the next time to register the data
     * @param context
     * @return
     */
    public static void setNetxTime(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        //use the last interval to calcolate the netx time relevation
        ArrayList<Long> intervaTimeArrayList =
                Utility.splitTime(Utility.yesterdayMidnightTimestamp(),
                        Utility.currentMidnightTimestamp(),
                        Long.parseLong(preferences.getString(Constants.setting_time_read_netstats, "0")));
        //Long.parseLong(preferences.getString(Constants.setting_time_read_netstats, "0")) +

        Long netxTime = intervaTimeArrayList.get(intervaTimeArrayList.size()-1);

        Utility.printLog("TAG-M-NetStatsTime" , "tempo di default : " + Long.parseLong(preferences.getString(Constants.setting_time_read_netstats, "0")) );
        Utility.printLog("TAG-M-NetStatsTime" , "Tempo al quale verrà aggiunto: " + Utility.getDataFromMillis(intervaTimeArrayList.get(intervaTimeArrayList.size()-1) ));
        Utility.printLog("TAG-M-NetStatsTime" , "next time : " + Utility.getDataFromMillis(netxTime + Constants.time_one_day ) );

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.last_netstats_send, netxTime+Constants.time_one_day+ "");
        editor.apply();

    }

    public static Boolean saveNetStats(NetStats netStats, Context context){
        Boolean done = true;

        DbManager db = new DbManager(context);

        //serve per capire se quel dato esiste già nel db
        NetStats dbNetStat = db.getNetStat(netStats.networkType, netStats.timestamp);

        if (dbNetStat == null){
            done = db.saveNetStats(netStats);
        }
        else {
            //if false nothing to update
            if (netStats.send!=false){
                done = db.updateNetStats(netStats);
            }
        }

        //netStats.print();


        return done;
    }


    public static Boolean saveNetStatsArray(ArrayList<NetStats> netStatsArrayList, Context context){
        Boolean done = true;

        for (NetStats netStats : netStatsArrayList){
            saveNetStats(netStats, context);
        }

        return done;

    }

    public static ArrayList<? extends AbstractData> getNotSendNetStats(Context context){
        DbManager dbManager = new DbManager(context);
        return dbManager.getNotSendNetStats();
    }

}
