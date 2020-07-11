package com.swapuniba.crowdpulse.business_object;

import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class ActivityData extends AbstractData{

    public String timestamp;
    public String inVehicle = "0";
    public String onBicycle = "0";
    public String onFoot = "0";
    public String running = "0";
    public String still = "0";
    public String tilting = "0";
    public String walking = "0";
    public String unknown = "0";
    public Boolean send;

    public ActivityData(){}

    public ActivityData(String timestamp,String inVehicle, String onBicycle, String onFoot, String running,
            String still, String tilting, String walking, String unknown, Boolean send){
        this.timestamp = timestamp;
        this.inVehicle = inVehicle;
        this.onBicycle = onBicycle;
        this.onFoot = onFoot;
        this.running = running;
        this.still = still;
        this.tilting = tilting;
        this.walking = walking;
        this.unknown = unknown;
        this.send = send;
    }

    @Override
    public String getSource() {
        return Constants.j_type_activity;
    }

    @Override
    public JSONObject toJSON(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.j_source_type, getSource());
            jsonObject.put(Constants.j_activity_timestamp, Long.parseLong(timestamp));
            jsonObject.put(Constants.j_activity_inVehicle, Long.parseLong(inVehicle));
            jsonObject.put(Constants.j_activity_onBicycle, Long.parseLong(onBicycle));
            jsonObject.put(Constants.j_activity_onFoot, Long.parseLong(onFoot));
            jsonObject.put(Constants.j_activity_running, Long.parseLong(running));
            jsonObject.put(Constants.j_activity_still, Long.parseLong(still));
            jsonObject.put(Constants.j_activity_tilting, Long.parseLong(tilting));
            jsonObject.put(Constants.j_activity_walking, Long.parseLong(walking));
            jsonObject.put(Constants.j_activity_unknown, Long.parseLong(unknown));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void fromJSON(JSONObject jsonObject){

        try {
            this.timestamp = jsonObject.getString(Constants.j_activity_timestamp);
            this.inVehicle = jsonObject.getString(Constants.j_activity_inVehicle);
            this.onBicycle = jsonObject.getString(Constants.j_activity_onBicycle);
            this.onFoot = jsonObject.getString(Constants.j_activity_onFoot);
            this.running = jsonObject.getString(Constants.j_activity_running);
            this.still = jsonObject.getString(Constants.j_activity_still);
            this.tilting = jsonObject.getString(Constants.j_activity_tilting);
            this.walking = jsonObject.getString(Constants.j_activity_walking);
            this.unknown = jsonObject.getString(Constants.j_activity_unknown);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * used for debug
     * @return
     */
    public void print() {
        String separator = "---";
        String s = "Activity: ";
        s += "timestamp: " + timestamp + separator;
        s += "inVehicle: " + inVehicle + separator;
        s += "onBicycle: " + onBicycle + separator;
        s += "onFoot: " + onFoot + separator;
        s += "running: " + running + separator;
        s += "still: " + still + separator;
        s += "tilting: " + tilting + separator;
        s += "walking: " + walking + separator;
        s += "unknown: " + unknown + separator;

        Utility.printLog(s);
        Utility.printLog("TAG-M-TD-AD","dati attività trasferiti: " + s);
    }

    @Override
    public String toString() {
        String separator = "---";
        String s = "";
        s += "timestamp: " + timestamp + separator;
        s += "inVehicle: " + inVehicle + separator;
        s += "onBicycle: " + onBicycle + separator;
        s += "onFoot: " + onFoot + separator;
        s += "running: " + running + separator;
        s += "still: " + still + separator;
        s += "tilting: " + tilting + separator;
        s += "walking: " + walking + separator;
        s += "unknown: " + unknown + separator + "\n";
        return  s;
    }
}
