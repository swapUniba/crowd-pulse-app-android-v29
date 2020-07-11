package com.swapuniba.crowdpulse.business_object;

import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONObject;



public class Event extends AbstractData{

    public String calendarId;
    public String nameOfEvent;
    public String startDates;
    public String endDates;
    public String descriptions;
    public String eventLocation;


    /**
     * used for debug
     * @return
     */
    public void print() {
        String separator = "---";
        String s = "Event: ";
        s += "calendarId: " + calendarId + separator;
        s += "nameOfEvent: " + nameOfEvent + separator;
        s += "startDates: " + startDates + separator;
        s += "endDates: " + endDates + separator;
        s += "descriptions: " + descriptions + separator;
        s += "eventLocation: " + eventLocation;

        Utility.printLog(s);
        Utility.printLog("TAG-M-TD-E","dati eventi trasferiti: " + s);
    }

    @Override
    public String getSource() {
        return null;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public void fromJSON(JSONObject jsonObject){}

}
