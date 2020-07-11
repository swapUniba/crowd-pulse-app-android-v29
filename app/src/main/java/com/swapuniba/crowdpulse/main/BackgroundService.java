  package com.swapuniba.crowdpulse.main;


  import android.app.AlarmManager;
  import android.app.PendingIntent;
  import android.app.usage.UsageStatsManager;
  import android.content.BroadcastReceiver;
  import android.content.Context;
  import android.content.Intent;
  import android.content.IntentFilter;
  import android.os.Build;
  import android.preference.PreferenceManager;
  import android.text.TextUtils;
  import android.util.Log;


  import com.google.gson.Gson;
  import com.swapuniba.crowdpulse.business_object.Account;
  import com.swapuniba.crowdpulse.business_object.AppInfo;
  import com.swapuniba.crowdpulse.business_object.Contact;
  import com.swapuniba.crowdpulse.business_object.NetStats;
  import com.swapuniba.crowdpulse.config.Constants;
  import com.swapuniba.crowdpulse.config.SettingFile;
  import com.swapuniba.crowdpulse.debug.AlarmInfo;
  import com.swapuniba.crowdpulse.debug.DatoAlarm;
  import com.swapuniba.crowdpulse.handlers.AccountHandler;
  import com.swapuniba.crowdpulse.handlers.ActivityHandler;
  import com.swapuniba.crowdpulse.handlers.ActivityHandler_v28;
  import com.swapuniba.crowdpulse.handlers.AppInfoHandler;
  import com.swapuniba.crowdpulse.handlers.ContactHandler;
  import com.swapuniba.crowdpulse.handlers.GpsHandler;
  import com.swapuniba.crowdpulse.handlers.DisplayHandler;
  import com.swapuniba.crowdpulse.handlers.NetStatsHandler;
  import com.swapuniba.crowdpulse.reactive.HandlerReactive;
  import com.swapuniba.crowdpulse.utility.Utility;

  import java.util.ArrayList;
  import java.util.Calendar;
  import java.util.HashMap;


  /**
   * E' la classe che si occupa di estrarre i dati. Questa classe viene attivata attraverso la creazione
   * di una allarme attivato minimo ogni 30 minuti e massimo ogni 24 ore.
   * Essa è divisa in 2 macroprocessi che sono quello di estrazione (extract()) e quello di
   * reattivazione (reactive()). Se non viene attivato il ricevitore i processi non vengono attivati.
   *
   */
  public class BackgroundService extends BroadcastReceiver{

    //For debug
    private Gson gson;
    private AlarmInfo alarmInfo=null;
    private DatoAlarm datoAlarm;


    private static final String TAG = "BackgroundService";
    static IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    BroadcastReceiver mReceiver = new DisplayHandler();
    Context context;

    private Context getApplicationContext(){
      return context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      this.context = context;

      Log.i(TAG, "onReceive,  bucket: " + actualBucketApp() );

      gson = new Gson();

      String jsonAlarmInfo = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
              .getString(Constants.tag_m_bs_data, "NULL" );

      if(!TextUtils.equals(jsonAlarmInfo , "NULL")){
        alarmInfo = gson.fromJson(jsonAlarmInfo , AlarmInfo.class);
      }else{
        alarmInfo = new AlarmInfo();
      }

      datoAlarm = new DatoAlarm();
      datoAlarm.insertData();
      datoAlarm.insertBucket(context);
      datoAlarm.ricevitore.attivazione = "Attivazione avvenuta con successo";

      Utility.printLog("TAG-M-BS", "onReceive attivato");

      //Create a listener for screen on/off
      if (SettingFile.getSettings(getApplicationContext()).get(Constants.setting_read_display).
              equalsIgnoreCase(Constants.record)){
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        Utility.printLog("TAG-M-BS-Display","Registratore attivato");
        try {
          context.getApplicationContext().registerReceiver(mReceiver, filter);
        }catch (Exception e){
          e.printStackTrace();
        }
      }else {
        try {
          context.getApplicationContext().unregisterReceiver(mReceiver);
        }
        catch (Exception e){
          e.printStackTrace();
        }

      }

      extract();

      reactivate();

      Utility.printLog("TAG-M-BS","OnReceive Concluso");

      datoAlarm.ricevitore.conclusione = "Conclusione avvenuta con successo";

      alarmInfo.dati.add(datoAlarm);

      String json_ = gson.toJson(alarmInfo);
      PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
              .edit()
              .putString(Constants.tag_m_bs_data, json_)
              .apply();


    }

    public static void cancel(Context context){
      AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
      Intent intent_alarm = new Intent(context , BackgroundService.class );
      PendingIntent pendingIntent = PendingIntent.getBroadcast(context , Constants.REQUEST_ALARM_EXTRACTION, intent_alarm , 0 );
      alarmManager.cancel(pendingIntent);
    }

    private void extract(){

      datoAlarm.estrazione.attivazione = "Attivazione avvenuta con successo";
      datoAlarm.estrazione.dati="";

      Utility.printLog("TAG-M-BS" , "Run in esecuzione");

      HashMap<String, String> settings = SettingFile.getSettings(context);
      for (String setting_key : Constants.setting_permission_keys) {
        //Utility.printLog("TAG-M-SETTING_KEY", setting_key + "is set: " + settings.get(setting_key));
        //execute only enabled service

        //Se trova una settaggio abilitato entra nell'if
        if (settings.get(setting_key).equalsIgnoreCase(Constants.record)) {
          Log.i(TAG, "extract: setting key: " + setting_key);
          switch (setting_key) {
            case Constants.setting_read_gps:

              //Se è passato il tempo corretto
              if (GpsHandler.checkTimeBetweenRequest(getApplicationContext())) {
                datoAlarm.estrazione.dati += "gps ";
                //Avvio la richiesta
                GpsHandler gpsHandler = new GpsHandler();
                gpsHandler.createRequest(getApplicationContext());
                GpsHandler.setNetxTime(getApplicationContext());


              }

              break;

            case Constants.setting_read_contacts:
              if (ContactHandler.checkTimeBetweenRequest(getApplicationContext())) {
                ArrayList<Contact> contactArrayList = ContactHandler.readContact(getApplicationContext());
                if(!contactArrayList.isEmpty()){
                  Utility.printLog("TAG-M-BS-CONTACTS","contacts: " + contactArrayList.toString());
                }
                ContactHandler.saveContactArray(contactArrayList, getApplicationContext());
                ContactHandler.setNetxTime(getApplicationContext());
                datoAlarm.estrazione.dati += "contatti ";


              }

              break;

            case Constants.setting_read_accounts:
              if (AccountHandler.checkTimeBetweenRequest(getApplicationContext())) {
                ArrayList<Account> accountArrayList = AccountHandler.readAccounts(getApplicationContext());
                Utility.printLog("TAG-M-BS-Accounts", "accountArray: " + accountArrayList.size());
                AccountHandler.saveAccountArray(accountArrayList, getApplicationContext());
                AccountHandler.setNetxTime(getApplicationContext());
                datoAlarm.estrazione.dati += "accounts ";


              }
              break;

            case Constants.setting_read_app:
              if (AppInfoHandler.checkTimeBetweenRequest(getApplicationContext())) {
                ArrayList<AppInfo> appInfoArrayList = AppInfoHandler.readAppInfo(getApplicationContext());
                AppInfoHandler.saveAppInfoArray(appInfoArrayList, getApplicationContext());
                AppInfoHandler.setNetxTime(getApplicationContext());
                datoAlarm.estrazione.dati += "app info ";


              }

              break;
            //controllare il netstats
            case Constants.setting_read_netstats:
              if (NetStatsHandler.checkTimeBetweenRequest(getApplicationContext())) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

                }else {
                  Utility.printLog("TAG-M-BS-NS-TIME", "Controllo passato ");
                  ArrayList<NetStats> netStatsArrayList = NetStatsHandler.readNetworkStats(getApplicationContext());
                  NetStatsHandler.saveNetStatsArray(netStatsArrayList, getApplicationContext());
                  NetStatsHandler.setNetxTime(getApplicationContext());
                  datoAlarm.estrazione.dati += "dati di rete ";
                }

              }

              break;

            case Constants.setting_read_activity:

              if (ActivityHandler.checkTimeBetweenRequest(getApplicationContext())) {
                ActivityHandler.setNetxTime(getApplicationContext());
                    /*if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.O_MR1) {
                     ActivityHandler.deleteCache(getApplicationContext());
                      Intent intentJobActivity = new Intent(getApplicationContext(), ActivityHandler.class);
                      ActivityHandler.enqueueWork(getApplicationContext(), intentJobActivity);
                    }else{*/
                ActivityHandler_v28.execute(getApplicationContext());
                    //}
                datoAlarm.estrazione.dati += "attività ";



              }

            default:

              break;

          }
        }

      }
      datoAlarm.estrazione.conclusione = "Estrazione conclusa con successo";


    }
    private void reactivate(){
      Log.i(TAG, "reactivate");

      datoAlarm.riattivazione.attivazione = "Reattivazione avvenuta con successo";

      Calendar calendar = Calendar.getInstance();

      long data;
      int sleep = HandlerReactive.timeToSleep();

      //
      if(sleep == Constants.FIRST_SLEEP){

        data = HandlerReactive.get_c_03_00();


      }
      else if(sleep == Constants.SECOND_SLEEP){

        data = HandlerReactive.get_c_06_00();


      }
      else {
         data = calendar.getTime().getTime();
         data = data + HandlerReactive.choiseNextTime(context);



      }

      datoAlarm.riattivazione.reattivazione(data);

      //restart alarm
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      Intent intent_alarm = new Intent(context , BackgroundService.class );
      PendingIntent pendingIntent = PendingIntent.getBroadcast(context , Constants.REQUEST_ALARM_EXTRACTION, intent_alarm , 0 );

      try {
       // alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(data, pendingIntent), pendingIntent);

        alarmManager.cancel(pendingIntent);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP , data , pendingIntent);


        HandlerReactive.saveAlarmBS(context , data);
        datoAlarm.riattivazione.conclusione = "Conclusione avvenuta con successo";



      }catch (Exception e){
        Log.i(TAG, "startAlarm failed");


      }





    }
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
