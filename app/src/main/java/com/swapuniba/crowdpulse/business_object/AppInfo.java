package com.swapuniba.crowdpulse.business_object;

import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fabio on 10/09/17.
 */

public class AppInfo extends AbstractData{

    public String packageName;
    public String timestamp;
    public String category;
    public String foregroundTime;
    public Boolean send;

    public AppInfo(){}

    public AppInfo(String packageName, String timestamp, String category, String foregroundTime, Boolean send){
        this.packageName = packageName;
        this.timestamp = timestamp;
        this.category = category;
        this.foregroundTime = foregroundTime;
        this.send = send;
    }

    @Override
    public String getSource() {
        return Constants.j_type_appinfo;
    }

    @Override
    public JSONObject toJSON(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.j_source_type, getSource());
            jsonObject.put(Constants.j_appinfo_packageName, packageName);
            jsonObject.put(Constants.j_appinfo_timestamp, Long.parseLong(timestamp));
            jsonObject.put(Constants.j_appinfo_category, category);
            jsonObject.put(Constants.j_appinfo_foregroundTime, Long.parseLong(foregroundTime));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void fromJSON(JSONObject jsonObject){

        try {
            this.timestamp = jsonObject.getString(Constants.j_appinfo_timestamp);
            this.packageName = jsonObject.getString(Constants.j_appinfo_packageName);
            try{
                this.category = jsonObject.getString(Constants.j_appinfo_category);
            }catch(JSONException e){
                this.category =null;
            }

            this.foregroundTime = jsonObject.getString(Constants.j_appinfo_foregroundTime);
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
        String s = "AppInfo: ";
        s += "packageName: " + packageName + separator;
        s += "timestamp: " + timestamp;
        s += "category: " + category + separator;
        s += "foregroundTime: " + foregroundTime + separator;

        Utility.printLog(s);
        Utility.printLog("TAG-M-TD-AI","dati app info trasferiti: " + s);
    }

    @Override
    public String toString() {
        String separator = "---";
        String s = "";
        s += "packageName: " + packageName + separator;
        s += "timestamp: " + timestamp;
        s += "category: " + category + separator;
        s += "foregroundTime: " + foregroundTime + separator + "\n";
        return s;
    }
}
