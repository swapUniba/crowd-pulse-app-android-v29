package com.swapuniba.crowdpulse.debug;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * Si occupa di contenere in un arraylist i dati di tipo DatoDailyCheck.
 *
 */
public class DailyCheckInfo {
    public ArrayList<DatoDailyCheck> dati;
    public DailyCheckInfo(){dati = new ArrayList<>(); }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(DatoDailyCheck datoDailyCheck : dati){
            stringBuilder.append(datoDailyCheck.toString());
            stringBuilder.append("\n\n");
        }

        return stringBuilder.toString();
    }
}
