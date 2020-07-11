package com.swapuniba.crowdpulse.business_object;

import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;


public class GPS extends AbstractData{

    public String timestamp;
    public String latitude;
    public String longitude;
    public String speed;
    public String accuracy;
    public Boolean send;

    public GPS(){ }

    public GPS(String timestamp, String latitude, String longitude, String speed, String accuracy, Boolean send){
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.accuracy = accuracy;
        this.send = send;
    }

    @Override
    public String getSource() {
        return Constants.j_type_gps;
    }

    @Override
    public JSONObject toJSON(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.j_source_type, getSource());
            jsonObject.put(Constants.j_gps_timestamp, Long.parseLong(timestamp));
            jsonObject.put(Constants.j_gps_latitude, Double.parseDouble(latitude));
            jsonObject.put(Constants.j_gps_longitude, Double.parseDouble(longitude));
            jsonObject.put(Constants.j_gps_speed, Double.parseDouble(speed));
            jsonObject.put(Constants.j_gps_accuracy, Double.parseDouble(accuracy));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void fromJSON(JSONObject jsonObject){

        try {
            this.timestamp = jsonObject.getString(Constants.j_gps_timestamp);
            this.latitude = jsonObject.getString(Constants.j_gps_latitude);
            this.longitude = jsonObject.getString(Constants.j_gps_longitude);
            this.speed = jsonObject.getString(Constants.j_gps_speed);
            this.accuracy = jsonObject.getString(Constants.j_gps_accuracy);

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
        String s = "GPS: ";
        s += "timestamp: " + timestamp + separator;
        s += "latitude: " + latitude + separator;
        s += "longitude: " + longitude + separator;
        s += "speed: " + speed + separator;
        s += "accuracy: " + accuracy;

        Utility.printLog("TAG-M-TD-GPS","dati gps trasferiti: " + s);
        Utility.printLog(s);
    }

    @Override
    public String toString() {
        String separator = "---";
        String s = "";
        s += "timestamp: " + timestamp + separator;
        s += "latitude: " + latitude + separator;
        s += "longitude: " + longitude + separator;
        s += "speed: " + speed + separator;
        s += "accuracy: " + accuracy + separator;
        s += "send:" + send.toString() + "\n";
        return s;
    }
}
