package com.swapuniba.crowdpulse.reactive;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.main.BackgroundService;
import com.swapuniba.crowdpulse.main.SendDataWorker;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

/**
 * Questa classe molto importante si occupa di reattivare l'allarme quando il telefono viene spento.
 * Quando il telefono si spegne per la batteria scarica tutti gli allarmi in coda vengono eliminati,
 * quindi è necessario che vengano reattivati.
 * Ciò è possibile farlo mettendo in attesa un ricevitore con l'intento BOOT_COMPLETED.
 */
public class RestartApp extends BroadcastReceiver {
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "RestartApp";
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceive");

        this.context = context;

        if(TextUtils.equals(intent.getAction() , BOOT_COMPLETED)){
            Intent restartService = new Intent(context , BackgroundService.class);
            context.sendBroadcast(restartService);
        }
    }

}
