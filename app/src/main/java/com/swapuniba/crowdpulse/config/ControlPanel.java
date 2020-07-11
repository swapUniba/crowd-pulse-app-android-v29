package com.swapuniba.crowdpulse.config;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.swapuniba.crowdpulse.R;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.HashMap;


public class ControlPanel extends Activity {

    //static Boolean confirmExit = false;
    SharedPreferences preferences;
    HashMap<String, String> settins;
    private static final String TAG = "ControlPanel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.control_panel);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        settins = SettingFile.getSettings(getApplicationContext());

        dispalySetting(Constants.setting_read_gps, R.id.switchSettingGPS, R.id.imageHelpGPS, R.string.text_help_gps,
                R.id.switchShareGPS, R.id.seekBarSettingGPS, R.id.textSeekbarCurrentGPS,
                R.id.textSeekbarIntervalTypeGPS, R.id.textSeekbarStartGPS, R.id.textSeekbarEndGPS);

        dispalySetting(Constants.setting_read_contacts, R.id.switchSettingContact,  R.id.imageHelpContact, R.string.text_help_contacts,
                R.id.switchShareContact, R.id.seekBarSettingContact, R.id.textSeekbarCurrentContact,
                R.id.textSeekbarIntervalTypeContact, R.id.textSeekbarStartContact, R.id.textSeekbarEndContact);

        dispalySetting(Constants.setting_read_accounts, R.id.switchSettingAccount,  R.id.imageHelpAccount, R.string.text_help_accounts,
                R.id.switchShareAccount, R.id.seekBarSettingAccount, R.id.textSeekbarCurrentAccount,
                R.id.textSeekbarIntervalTypeAccount, R.id.textSeekbarStartAccount, R.id.textSeekbarEndAccount);

        dispalySetting(Constants.setting_read_app, R.id.switchSettingAppInfo,  R.id.imageHelpAppInfo, R.string.text_help_app,
                R.id.switchShareAppInfo, null, null,
                null, null, null);

        dispalySetting(Constants.setting_read_netstats, R.id.switchSettingNetStat,  R.id.imageHelpNetStat, R.string.text_help_netstats,
                R.id.switchShareNetStat, R.id.seekBarSettingNetStat, R.id.textSeekbarCurrentNetStat,
                R.id.textSeekbarIntervalTypeNetStat, R.id.textSeekbarStartNetStat, R.id.textSeekbarEndNetStat);

        dispalySetting(Constants.setting_read_display, R.id.switchSettingDisplay,  R.id.imageHelpDisplay, R.string.text_help_display,
                R.id.switchShareDisplay, null, null,
                null, null, null);

        dispalySetting(Constants.setting_read_activity, R.id.switchSettingActivity, R.id.imageHelpActivity, R.string.text_help_activity,
                R.id.switchShareActivity, R.id.seekBarSettingActivity, R.id.textSeekbarCurrentActivity,
                R.id.textSeekbarIntervalTypeActivity, R.id.textSeekbarStartActivity, R.id.textSeekbarEndActivity);

    }
    
    
    void dispalySetting(final String settingKey, Integer ResSwitch, Integer ResImageHelp, final Integer ResHelpMessage,
                        Integer ResSwitchShare, Integer ResSeekBar, Integer ResTextSeekbarCurrent,
                        Integer ResTextSeekbarIntervalType, Integer ResTextSeekbarStart, Integer ResTextSeekbarEnd){

        //BUTTON ENABLE/DISABLE PERMISSION
        Switch switchButton = (Switch) findViewById(ResSwitch);
        if (settins.get(settingKey).equalsIgnoreCase(Constants.record)) {
            switchButton.setChecked(true);
        } else {
            switchButton.setChecked(false);
        }

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingFile.setSetting(settingKey, Constants.record, getApplicationContext(), Constants.sourceThisSmarthone);
                } else {
                    SettingFile.setSetting(settingKey, Constants.no_record, getApplicationContext(), Constants.sourceThisSmarthone);
                }
            }
        });

        ImageView imageHelp = (ImageView) findViewById(ResImageHelp);
        imageHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ControlPanel.this, R.style.DialogHelp));
                builder.setMessage(getString(ResHelpMessage));
                builder.setCancelable(false);
                builder.create();
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
                builder.show();
            }
        });

        //BUTTON ENABLE/DISABLE THE SHAREING OF DATA
        Switch switchButtonShare = (Switch) findViewById(ResSwitchShare);
        if (settins.get(Constants.mappingSettingShare.get(settingKey)).equalsIgnoreCase(Constants.string_true)) {
            switchButtonShare.setChecked(true);
        } else {
            switchButtonShare.setChecked(false);
        }

        switchButtonShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingFile.setSetting(Constants.mappingSettingShare.get(settingKey), Constants.string_true, getApplicationContext(), Constants.sourceThisSmarthone);
                } else {
                    SettingFile.setSetting(Constants.mappingSettingShare.get(settingKey), Constants.string_false, getApplicationContext(), Constants.sourceThisSmarthone);
                }
            }
        });
        //END BUTTON ENABLE/DISABLE PERMISSION


        if (Constants.mappingSettingAdmissibleValue.get(settingKey).size() > 0){
            //SEEDK BAR
            SeekBar seekbar = (SeekBar) findViewById(ResSeekBar);

            seekbar.setMax(Constants.mappingSettingAdmissibleValue.get(settingKey).get((
                    Constants.mappingSettingAdmissibleValue.get(settingKey).size()-1)));


            final TextView textSeekbarCurrent = (TextView) findViewById(ResTextSeekbarCurrent);

            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                /**
                 * Nota: nel caso in cui si stia modificando la barra degli intervalli del GPS
                 * o delle attività la barra viene automaticamente settata ai valori che non siano
                 * minori di 30 minuti.
                 */
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    if(isGPSOrActivity(settingKey)){
                        if(progress<30){
                            seekBar.setProgress(29);
                            textSeekbarCurrent.setText(30 + "");
                        }else{
                            textSeekbarCurrent.setText(progress + "");

                        }
                  }else{
                        textSeekbarCurrent.setText(progress + "");

                 }


                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if(!Constants.mappingSettingAdmissibleValue.get(settingKey).contains(seekBar.getProgress())){
                        int i = 0;
                        while(Constants.mappingSettingAdmissibleValue.get(settingKey).get(i) < seekBar.getProgress()){
                            ++i;
                        }

                        Log.i(TAG, "onStopTrackingTouch: i: " +  Constants.mappingSettingAdmissibleValue.get(settingKey).get(i) );
                            seekBar.setProgress(Constants.mappingSettingAdmissibleValue.get(settingKey).get(i));



                    }

                    Log.i(TAG, "onStopTrackingTouch: progress: " +  seekBar.getProgress());
                    SettingFile.setSetting(Constants.mappingSettingTimeRead.get(settingKey),
                            seekBar.getProgress()* Utility.millisecondInUinit(Constants.mappingSettingTimeTimeType.get(settingKey)) + "",
                            getApplicationContext(), Constants.sourceThisSmarthone);


                }
            });

            int progress = Integer.parseInt(preferences.getString(Constants.mappingSettingTimeRead.get(settingKey), Constants.default_setting.get(settingKey)));
            progress = progress / Utility.millisecondInUinit(Constants.mappingSettingTimeTimeType.get(settingKey));
            seekbar.setProgress(progress);



            TextView textSeekbarIntervalType = (TextView) findViewById(ResTextSeekbarIntervalType);
            textSeekbarIntervalType.setText(Constants.mappingSettingTimeTimeType.get(settingKey));

            TextView textSeekbarStart = (TextView) findViewById(ResTextSeekbarStart);
            textSeekbarStart.setText(Constants.mappingSettingAdmissibleValue.get(settingKey).get(0) +"");
            TextView textSeekbarEnd = (TextView) findViewById(ResTextSeekbarEnd);
            textSeekbarEnd.setText(Constants.mappingSettingAdmissibleValue.get(settingKey).get((
                    Constants.mappingSettingAdmissibleValue.get(settingKey).size()-1)) + "");
        }

    }
    




    //momentanea risoluzione per il gps e le attività che devono essere prese almeno ogni 30 minuti
    //necessaria una modifica sul sito
    private boolean isGPSOrActivity(String setting_key){
        if(setting_key.equals(Constants.setting_read_gps) || setting_key.equals(Constants.setting_read_activity))
            return true;
        else
            return false;
    }

}



