package com.swapuniba.crowdpulse.comunication;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.swapuniba.crowdpulse.R;
import com.swapuniba.crowdpulse.business_object.DeviceInfo;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.config.SettingFile;
import com.swapuniba.crowdpulse.handlers.DeviceInfoHandler;
import com.swapuniba.crowdpulse.main.BackgroundService;
import com.swapuniba.crowdpulse.main.Intro;
import com.swapuniba.crowdpulse.utility.NotificationUtility;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Iterator;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.content.ContentValues.TAG;

/**
 * Si occupa di comunicare con il server.
 * I listener permettono di ricevere le chiamate del server una volta eseguiti i metodi socket.on.
 * Questa classe viene avviata automaticamente quando si avvia l'applicazione, ciò è reso possibile
 * dall'attributo android:name presente nel Manifest.
 */
public class SocketApplication extends Application {

  static private Socket socket = null;

  static public Boolean sending = false; //Check if someone are sending data

  static String TAG = "SocketApplication";

  @Override
  public void onCreate() {
    Log.i("TAG-M-SPINFO" , "Sto creando Socket Application");
    super.onCreate();

    initializeSocket();

  }

  static public Socket getSocket() {
    return socket;
  }

  /**
   * Inizializza i ricevitori solo se la socket non è stata ancora creata
   */
  public void initializeSocket() {
    try {
      if (socket == null) {


        // E' possibile ricevere questa chiamata in casi di tentativi di connessione mancati a causa
        // di una connessione ad internet debole o inesistente.
        // oppure se l'indirrizzo del server è errato
        Emitter.Listener onConnectError = new Emitter.Listener() {
          @Override
          public void call(Object... args) {
            //sendBroadcast(new Intent(Constants.erete));
            for(Object obj : args){
              Log.i(TAG, obj.toString());
            }
            Utility.printLog(getResources().getString(R.string.connection_error).toString());
            Log.i( TAG , "In onConnectionError socket = " + socket);
          }
        };


        Emitter.Listener onReload = new Emitter.Listener() {
          @Override
          public void call(Object... args) {

            Intent mServiceIntent = new Intent(getApplicationContext(), Intro.class);
            mServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Starts the IntentService
            startService(mServiceIntent);
          }
        };


        // Chiamata avviata quando vengono cambiate le configurazioni nel pannello di controllo sia
        // dal sito che dalla app
        Emitter.Listener onNewConfiguration = new Emitter.Listener() {
          @Override
          public void call(Object... args) {
            try {

              JSONObject data = (JSONObject) args[0];
              Log.i(TAG, "data: " + data.getInt(Constants.j_code));
              if (data.getInt(Constants.j_code) == Constants.response_receiving) {
                data = data.getJSONObject(Constants.j_config_config);

                Iterator<String> keys = data.keys();
                while (keys.hasNext()) {
                  String setting = keys.next();
                  SettingFile.setSetting(
                      setting,
                      data.getString(setting),
                      getApplicationContext(),
                      Constants.sourceWebUI
                  );
                }
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }

          }

        };

        // Si riceve quando si sono inviati dei dati al server
        Emitter.Listener onSendData = new Emitter.Listener() {
          @Override
          public void call(Object... args) {
            try {
              JSONObject data = (JSONObject) args[0];
              if (data.getInt(Constants.j_code) == Constants.response_receiving) {
                if (!sending) {//se nessuno sta inviando dati

                  Log.i("TAG-M-SOCKET" , "sending su false , avvio BackgroundService");

                } else {
                  Handler handler2 = new Handler(Looper.getMainLooper());
                  handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      sending = false;
                    }
                  }, 10000); //si deve avviare dopo 10 secondi
                }
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }

        };

        Emitter.Listener connesso = new Emitter.Listener() {
          @Override
          public void call(Object... args) {
            Utility.printLog("connect");
          }

        };

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String server_ip = preferences.getString(Constants.pref_server_ip, "");

        IO.Options option = new IO.Options();
        option.reconnection = true;
        if (server_ip.equalsIgnoreCase("")) {
          Log.i("TAG-M-Socket" , "Instanzio socket , caso server ip vuoto");
          socket = IO.socket(Constants.SERVER_URL_MASTER, option);
        } else {
          Log.i("TAG-M-Socket" , "Instanzio socket , caso server ip non vuoto");

          socket = IO.socket(server_ip, option);

        }

        //register the function

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);

        socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
          @Override
          public void call(Object... args) {
            Log.i("TAG-M-Socket-EvErrorTimeOut" , args[0].toString());
          }
        });

        socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
          @Override
          public void call(Object... args) {
            Log.i("TAG-M-Socket-EvError" , args[0].toString());
          }
        });
        socket.on(Constants.channel_reload, onReload);

        socket.on(Constants.channel_config, onNewConfiguration);

        socket.on(Constants.channel_send_data, onSendData);

        socket.on(Socket.EVENT_CONNECT, connesso);


        socket.connect();

      }
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

}

