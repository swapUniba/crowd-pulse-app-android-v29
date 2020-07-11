package com.swapuniba.crowdpulse.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.swapuniba.crowdpulse.R;
import com.swapuniba.crowdpulse.config.Constants;

/**
 * Setta l'IP del server, se l'ip non è corretto non sarà possibile connetersi alla socket
 */
public class SetIP extends Activity {

    SharedPreferences preferences;

    EditText editText_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.set_ip);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String server_ip = preferences.getString(Constants.pref_server_ip, "");

        editText_ip = (EditText) findViewById(R.id.text_ip);
        if (server_ip.equalsIgnoreCase("")){
            editText_ip.setText(Constants.SERVER_URL_MASTER);
        }else {
            editText_ip.setText(server_ip);
        }


        Button buttonSetIP = (Button) findViewById(R.id.button_set_ip);
        buttonSetIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Constants.pref_server_ip, editText_ip.getText().toString());
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });



    }












    private void reload(){

        onCreate(getIntent().getExtras());

        //Intent mServiceIntent = new Intent(getApplicationContext(), Intro.class);
        //mServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Starts the IntentService
        //startService(mServiceIntent);
    }


}



