package com.swapuniba.crowdpulse.handlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.swapuniba.crowdpulse.business_object.AbstractData;
import com.swapuniba.crowdpulse.business_object.Display;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.database.DbManager;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.ArrayList;

/**
 * Le informazioni sul display venogno ottenute attraverso un ricevitore che risponde positivamente
 * agli intenti ACTION_SCREEN_OFF e ACTION_SCREEN_ON.
 */

public class DisplayHandler extends BroadcastReceiver {

    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        //USED BECAUSE SOMETIME THE THREAD GO OFF AND NOT CAPTURE THE EVENT
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever you need to do here
            Utility.printLog("screen off");
            Display display = new Display();
            display.timestamp = System.currentTimeMillis() + "";
            display.state = Constants.on;
            display.send = false;

            DbManager db = new DbManager(context);

            //if the last_display have the same state, create a fake state to resume consistency
            Display lastDisplay = db.getLastDisplay();
            if (lastDisplay != null){
                if (lastDisplay.state.equalsIgnoreCase(display.state)){
                    Display fakeDisplay = new Display();

                    fakeDisplay.timestamp = String.valueOf(
                            (Long.parseLong(lastDisplay.timestamp) + Long.parseLong(display.timestamp)) / 2);
                    fakeDisplay.state = Constants.off;
                    fakeDisplay.send = false;
                }

            }
            db.saveDisplay(display);

            wasScreenOn = false;

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // and do whatever you need to do here
            Utility.printLog("screen on");
            Display display = new Display();
            display.timestamp = System.currentTimeMillis() + "";
            display.state = Constants.off;
            display.send = false;

            DbManager db = new DbManager(context);

            //if the last_display have the same state, create a fake state to resume consistency
            Display lastDisplay = db.getLastDisplay();
            if (lastDisplay != null){
                if (lastDisplay.state.equalsIgnoreCase(display.state)){
                    Display fakeDisplay = new Display();

                    fakeDisplay.timestamp = String.valueOf(
                            (Long.parseLong(lastDisplay.timestamp) + Long.parseLong(display.timestamp)) / 2);
                    fakeDisplay.state = Constants.on;
                    fakeDisplay.send = false;
                }

            }
            db.saveDisplay(display);

            wasScreenOn = true;
        }
    }



    public static Boolean saveDisplay(Display display, Context context){
        Boolean done = true;

        DbManager db = new DbManager(context);

        Display dbDisplay = db.getDisplay(display.timestamp);

        if (dbDisplay == null){
            done = db.saveDisplay(display);
        }
        else {
            db.updateDisplay(display);
        }

        return done;
    }


    public static Boolean saveDispalyArray(ArrayList<Display> displayArrayList, Context context){
        Boolean done = true;

        for (Display display : displayArrayList){
            saveDisplay(display, context);
        }

        return done;

    }

    public static ArrayList<? extends AbstractData> getNotSendDisplay(Context context){
        DbManager dbManager = new DbManager(context);
        return dbManager.getNotSendDisplay();
    }


}