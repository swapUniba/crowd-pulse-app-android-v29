package com.swapuniba.crowdpulse.debug;

import java.util.ArrayList;

import androidx.annotation.NonNull;


/**
 * Si occupa di contenere in un arraylist i dati di tipo DatoAfterSend.
 *
 */
public class AfterSendInfo {
    public ArrayList<DatoAfterSend> dati;
    public AfterSendInfo(){
        dati = new ArrayList<>();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(DatoAfterSend datoAfterSend : dati){
            stringBuilder.append(datoAfterSend.toString());
            stringBuilder.append("\n\n");
        }

        return stringBuilder.toString();
    }

}
