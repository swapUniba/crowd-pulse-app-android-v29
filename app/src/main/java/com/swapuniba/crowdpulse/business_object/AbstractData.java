package com.swapuniba.crowdpulse.business_object;

import org.json.JSONObject;


public abstract class AbstractData {

    /**
     * Restituisce le costanti che rappresentano il business_object in formato json.
     * @return la risorsa in formato stringa
     */
    public abstract String getSource();

    /**
     * Prende un business_object lo rielabora inserendolo in un oggetto JSONObject (i valori non ven
     * alterati) e lo restituisce.
     * @return il business_object in formato json
     */
    public abstract JSONObject toJSON();

    /**
     * A partire da un oggetto json 'riempie' le variabili del business_object
     * @param jsonObject
     */
    public abstract void fromJSON(JSONObject jsonObject);

}
