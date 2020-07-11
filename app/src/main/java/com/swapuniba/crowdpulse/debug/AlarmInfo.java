package com.swapuniba.crowdpulse.debug;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * Si occupa di contenere in un arraylist i dati di tipo DatoAlarm.
 *
 */
public class AlarmInfo {
    public ArrayList<DatoAlarm> dati;

    public AlarmInfo(){
        dati = new ArrayList<>();
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(DatoAlarm datoAlarm : dati){
            stringBuilder.append(datoAlarm.toString());
            stringBuilder.append("\n\n");
        }

        return stringBuilder.toString();
    }
}
