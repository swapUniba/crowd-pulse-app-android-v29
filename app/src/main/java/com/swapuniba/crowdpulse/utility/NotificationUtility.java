package com.swapuniba.crowdpulse.utility;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import android.view.Gravity;
import android.widget.Toast;

import com.swapuniba.crowdpulse.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class NotificationUtility {

  private static final String APP_NAME = "CrowdPulse";

  public static void showToast(Context context, String mess) {
    Toast toast = Toast.makeText(context, mess, Toast.LENGTH_SHORT);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }

  public static void showToastSocket(final Context context, final String mess) {
    Handler mHandler = new Handler(Looper.getMainLooper());
    mHandler.post(new Runnable() {

      @Override
      public void run() {
        Toast toast = Toast.makeText(context, mess, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
      }
    });
  }

  public static void sendNotification(final Context context, final String mess) {
    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    //Get an instance of NotificationManager
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(APP_NAME)
            .setContentText(mess)
            .setSound(soundUri);

    // Gets an instance of the NotificationManager service
    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(001, mBuilder.build());
  }

  public static void notification(Context context , String title , String text , int launcher_icon){
    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {

      NotificationChannel channel = new NotificationChannel(title, title, NotificationManager.IMPORTANCE_HIGH);
      manager.createNotificationChannel(channel);

      Notification builder = new Notification.Builder(context, title)
              .setContentTitle(title)
              .setContentText("AlarmBS: " +
                      new SimpleDateFormat("yy/MM/dd - HH:mm:ss", Locale.ITALIAN).format(new Date()))
              .setStyle(new Notification.BigTextStyle().bigText(text + "\n" +
                      "AlarmBS: " +
                      new SimpleDateFormat("yy/MM/dd - HH:mm:ss", Locale.ITALIAN).format(new Date())))
              .setSmallIcon(launcher_icon)
              .build();

      manager.notify(getRandomId(), builder);
    }
  }


  private static int getRandomId(){
    Random random = new Random();
    int num = random.nextInt(999);
    return num;
  }




}