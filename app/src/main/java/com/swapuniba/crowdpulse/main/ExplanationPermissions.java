package com.swapuniba.crowdpulse.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swapuniba.crowdpulse.R;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.utility.Utility;

/**
 * Activity che si occupa di far comprendere all'utente i permessi che vengono usati
 */
public class ExplanationPermissions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation_permissions);

        if(getSupportActionBar()!=null)
            this.getSupportActionBar().hide();

        Button btnOk = findViewById(R.id.btn_popup_expl_perm_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString(Constants.explanation_permissions , Constants.string_false)
                        .apply();

                Intent intent = new Intent(getApplicationContext() , Intro.class);
                startActivity(intent);
            }
        });


        if(Utility.vGreaterThan28()){
            TextView textView = findViewById(R.id.txtSpiegazionePermessi);
            textView.setText(getResources().getString(R.string.text_explanation_permission_v29));
            LinearLayout linearLayout = findViewById(R.id.ll_popup_expl_perm_activity);
            linearLayout.setVisibility(View.VISIBLE);
        }


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!TextUtils.equals(preferences.getString(Constants.explanation_permissions , Constants.string_true), Constants.string_true)){
            Intent intent = new Intent(getApplicationContext() , Intro.class);
            startActivity(intent);
        }

    }
}
