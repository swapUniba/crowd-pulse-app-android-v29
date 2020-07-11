package com.swapuniba.crowdpulse.utility;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.swapuniba.crowdpulse.config.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;



public class Utility {

    /**
     * Calcola l'ora attuale
     * @param mills tempo in millisecondi
     * @return data attuale in milliseondi ma solo con l'ora
     */
    public static long getCurrentHHMills(long mills){
        String data_s = "";
        Date data;
        long data_mills = currentMidnightTimestamp();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH" , Locale.ITALIAN);
        data_s = simpleDateFormat.format(mills);
        try {
            data = simpleDateFormat.parse(data_s);
            data_mills = data.getTime();
        }catch (ParseException e){
            Utility.printLog("TAG-M-TIMEINFOHH-ERROR" , "Errore nel parse");
        }
        return data_mills;
    }


    public static String getDataFromMillis(long millis){
        String data = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z" , Locale.ITALIAN);
        data = simpleDateFormat.format(millis);
        return data;
    }

    public static void printLog(String message){
        if(Constants.print_log){
            Log.i(Constants.log_tag, message);
        }
    }
    public static void printLog(String tag, String message){
        if(Constants.print_log){
            Log.i(tag, message);
        }
    }


    /**
     * today midnight
     * @return
     */
    public static long currentMidnightTimestamp(){

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_YEAR, 0);

        //Utility.printLog("TAG-M-TimeStamp" , "Calendario oggi: " + cal.getTime().toString());
        Utility.printLog("TAG-M-TimeStamp" , "TIMEZONE CURRENT: " + cal.getTimeZone().getRawOffset());


        return cal.getTimeInMillis() + cal.getTimeZone().getRawOffset();

    }

    public static long yesterdayMidnightTimestamp(){

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_YEAR, -1);
      //  Utility.printLog("TAG-M-TimeStamp" , "Calendario ieri: " + cal.getTime().toString());
        Utility.printLog("TAG-M-TimeStamp" , "TIMEZONE  MIDNIGHT: " + cal.getTimeZone().getRawOffset());

        return cal.getTimeInMillis() + cal.getTimeZone().getRawOffset();
    }


    public static String threeDaysAgoTimestamp(){

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_YEAR, -3);

        long t = cal.getTimeInMillis() + cal.getTimeZone().getRawOffset();

        return t + "";
    }


    static public ArrayList<Long> splitTime(Long startTime, Long endTime, Long interval){
        ArrayList<Long> intervalTimeArrayList = new ArrayList<Long>();

        while (endTime >= startTime){
            intervalTimeArrayList.add(startTime);
            startTime += interval;
        }

        return intervalTimeArrayList;
    }


    static String ARRAY_DIVIDER = "#xix#";


    static public String serialize(ArrayList<String> content){
        return TextUtils.join(ARRAY_DIVIDER, content);
    }

    static public ArrayList<String> derialize(String content){
        return new ArrayList<String>(Arrays.asList(content.split(ARRAY_DIVIDER)));
    }


    static public String randomString(){
        char[] chars = "abcdefghijklmnopqrstuvwxyzBCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }


    static public int millisecondInUinit(String unit){

        switch (unit){
            case Constants.type_minute:
                return 60000;

            case Constants.type_hour:
                return 3600000;

            case Constants.type_day:
                return 86400000;

            default:
                return 0;
        }

    }


    /**
     * Controlla se la versione attuale è maggiore di quella 28
     * @return true se è maggiore , false altrimenti
     */
    public static boolean vGreaterThan28 (){
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.P;
    }

    public static int getTime(TimeUnit timeUnit , int time){
        int effectiveDelay = 0;

        switch (timeUnit){
            case HOURS:
                effectiveDelay = Utility.millisecondInUinit(Constants.type_hour) * time ;
                break;

            case MINUTES:
                effectiveDelay = Utility.millisecondInUinit(Constants.type_minute) * time;
                break;

            case SECONDS:
                effectiveDelay = 1000 * time;
                break;

            default:
                break;
        }

        return  effectiveDelay;
    }


}
