package com.swapuniba.crowdpulse.debug;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * Si occupa di contenere in un arraylist i dati di tipo DatoBeforeSend.
 *
 */
public class BeforeSendInfo {

    public ArrayList<DatoBeforeSend> dati;

    public BeforeSendInfo(){
        dati = new ArrayList<>();
    }
    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(DatoBeforeSend datoSend : dati){
            stringBuilder.append(datoSend.toString());
            stringBuilder.append("\n\n");
        }

        return stringBuilder.toString();
    }


}
