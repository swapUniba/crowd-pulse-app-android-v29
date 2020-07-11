package com.swapuniba.crowdpulse.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.swapuniba.crowdpulse.R;
import com.swapuniba.crowdpulse.business_object.DeviceInfo;
import com.swapuniba.crowdpulse.comunication.SocketApplication;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.config.SettingFile;
import com.swapuniba.crowdpulse.handlers.DeviceInfoHandler;
import com.swapuniba.crowdpulse.reactive.DailyCheck;
import com.swapuniba.crowdpulse.reactive.HandlerReactive;
import com.swapuniba.crowdpulse.utility.NotificationUtility;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Activity che si occupa di gestire il login e quindi di connettere il profilo su myrror
 */
public class Login extends Activity {
    private static final String TAG = "Login";

    static Boolean confirmExit = false;
    static boolean isCheck = false;
    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextPhoneNumber;


    ProgressBar progressBarLogin;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        final Button login_button = (Button) findViewById(R.id.button_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        progressBarLogin = (ProgressBar) findViewById(R.id.progressLogin);
        progressBarLogin.setVisibility(View.VISIBLE);


        editTextUsername = (EditText) findViewById(R.id.editText_username);
        assert editTextUsername != null;
        editTextUsername.setText(preferences.getString(Constants.pref_email, ""));

        editTextPassword = (EditText) findViewById(R.id.editText_password);
        assert editTextPassword != null;
        editTextPassword.setText(preferences.getString(Constants.pref_password, ""));

        editTextPhoneNumber = (EditText) findViewById(R.id.editText_phoneNumber);



        assert editTextPhoneNumber != null;
        editTextPhoneNumber.setText(preferences.getString(Constants.pref_phoneNumber, ""));


        //Per visualizzare la password
        Button button_show_password = (Button) findViewById(R.id.button_show_password);
        button_show_password.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                switch ( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }

                return true;
            }
        });





        //Questa è la parte relativa al click del login , tutto quello che succede dopo il click è inserito qui.
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheck = true;


                final Socket socket = SocketApplication.getSocket();
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put(Constants.j_email, editTextUsername.getText());
                    jsonObject.put(Constants.j_password, editTextPassword.getText());
                    DeviceInfo deviceInfo = DeviceInfoHandler.readDeviceInfo(getApplicationContext());
                    jsonObject.put(Constants.j_deviceinfo_deviceId,deviceInfo.deviceId);
                    jsonObject.put(Constants.j_deviceinfo_brand,deviceInfo.brand);
                    jsonObject.put(Constants.j_deviceinfo_sdk,deviceInfo.sdk);
                    jsonObject.put(Constants.j_deviceinfo_model,deviceInfo.model);

                    deviceInfo.phoneNumbers.add(editTextPhoneNumber.getText().toString());

                    JSONArray jsonArrayPhoneNumbers = new JSONArray();
                    for (String phoneNumber : deviceInfo.phoneNumbers){
                        jsonArrayPhoneNumbers.put(phoneNumber);
                    }

                    jsonObject.put(Constants.j_deviceinfo_phoneNumbers, jsonArrayPhoneNumbers);
                    Utility.printLog("TAG-M-DeviceInfo","dati jsonObject: "+ jsonObject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.emit(Constants.channel_login, jsonObject);


                //Chaiamata ottenuta dopo aver richiesto di loggarsi con la mail e la password
                Emitter.Listener onLogin = new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {

                            Log.i("TAG-M-Login","entrato in call onLogin");
                            JSONObject data = (JSONObject) args[0];

                            final Context context = getApplicationContext();

                            // NotificationUtility.showToastSocket(context, data.getString(Constants.j_description));

                            if(data.getInt(Constants.j_code) == Constants.response_success){

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(Constants.pref_username, data.getString(Constants.j_username));
                                editor.putString(Constants.pref_email, editTextUsername.getText().toString().trim());
                                editor.putString(Constants.pref_password, editTextPassword.getText().toString());
                                editor.putString(Constants.pref_phoneNumber, editTextPhoneNumber.getText().toString());
                                editor.apply();

                                //IF IS THE FIRST TIME LOGGED
                                if (preferences.getBoolean(Constants.pref_firstLogin, false)){
                                    //SEND INITIAL CONFIG
                                    SocketApplication.getSocket().emit(Constants.channel_config, SettingFile.getJSON(getApplicationContext()));
                                }
                                else {
                                    JSONObject coingingJSON = new JSONObject();
                                    socket.emit(Constants.channel_config, coingingJSON); //perchè ?
                                }

                                if(TextUtils.equals(preferences.getString(Constants.first_installation , Constants.string_false) , Constants.string_false)){
                                    preferences.edit()
                                            .putString(Constants.first_installation , Constants.string_true)
                                            .apply();

                                }

                                if(TextUtils.equals(preferences.getString(Constants.work_status , Constants.WORK_OFF) , Constants.WORK_OFF)){
                                    preferences.edit().putString(Constants.work_status , Constants.WORK_ON).apply();
                                    startSendData();
                                }

                                if(TextUtils.equals(preferences.getString(Constants.job_status , Constants.WORK_OFF) , Constants.WORK_OFF)){
                                    preferences.edit().putString(Constants.job_status , Constants.WORK_ON).apply();
                                    startBackgroundService();
                                }

                                if(TextUtils.equals(preferences.getString(Constants.handler_reactive_status , Constants.WORK_OFF) , Constants.WORK_OFF)){
                                    preferences.edit().putString(Constants.handler_reactive_status , Constants.WORK_ON).apply();
                                    startHandlerReactive();
                                }

                                Intent intent = new Intent(getApplicationContext(), Main.class);
                                startActivity(intent);

                            }
                            else {


                                if(isCheck) {

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Password o E-mail errati", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    }).start();
                                    isCheck = false;
                                }
                                login_button.setEnabled(true);
                                progressBarLogin.setVisibility(View.GONE);
                            }

                            Utility.printLog(data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("TAG-M-BtnEnabled" , "Enabled su true all'interno del click 2 ");
                            login_button.setEnabled(true);
                            progressBarLogin.setVisibility(View.GONE);
                        }
                    }

                };

                socket.on(Constants.channel_login, onLogin);

                if(!SocketApplication.getSocket().connected()){
                    login_button.setEnabled(true);
                    progressBarLogin.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext() , "Connessione non avvenuta" , Toast.LENGTH_SHORT).show();
                }

                //Intent intent = new Intent(getApplicationContext(), ControlPanel.class);
                //startActivity(intent);
            }
        }); //fine click del login


        if (!preferences.getString(Constants.pref_password, "").equalsIgnoreCase("")){
            login_button.setEnabled(false);
            login_button.callOnClick();
            Log.i("TAG-M-BtnEnabled" , "Enabled su false");
        }
        else{
            progressBarLogin.setVisibility(View.GONE);
            login_button.setEnabled(true);
            Log.i("TAG-M-BtnEnabled" , "Enabled su true");
        }








        
    }

    /**
     * Cancella i dati : email , password e phoneNumber dalle preferenze
     * @param context
     */
    public static void cancelLogin(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor
                .putString(Constants.pref_email , "")
                .putString(Constants.pref_password , "")
                .putString(Constants.pref_phoneNumber , "")
                .apply();

    }


    private void startSendData(){                   //inserire salvataggio per il send , manca nextTime


        SendDataWorker.createAndEnqueue(getApplicationContext());

    }

    private void startHandlerReactive(){

        DailyCheck.createAndEnqueue(getApplicationContext());

    }

    private void startBackgroundService(){
        Utility.printLog("TAG-M-"+TAG,"Avvio il servizio in background");
        Intent startBS = new Intent(getApplicationContext() , BackgroundService.class);
        sendBroadcast(startBS);
    }

    @Override
    public void onBackPressed() {

        if(confirmExit){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else {

            confirmExit = true;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    confirmExit = false;
                }
            }, 2000);

            NotificationUtility.showToast(getApplicationContext(), getString(R.string.press_again_exit));
        }
    }


}



