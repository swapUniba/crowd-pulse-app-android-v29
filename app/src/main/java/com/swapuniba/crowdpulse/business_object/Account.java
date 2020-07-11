package com.swapuniba.crowdpulse.business_object;

import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;


public class Account extends AbstractData{

    public String timestamp;
    public String userAccountName;
    public String packageName;
    public Boolean send;

    public Account(){}

    public Account(String timestamp, String userAccountName, String packageName, Boolean send){
        this.timestamp = timestamp;
        this.userAccountName = userAccountName;
        this.packageName = packageName;
        this.send = send;
    }

    @Override
    public String getSource() {
        return Constants.j_type_account;
    }

    @Override
    public JSONObject toJSON(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.j_source_type, getSource());
            jsonObject.put(Constants.j_account_timestamp, Long.parseLong(timestamp));
            jsonObject.put(Constants.j_account_accountUserName, userAccountName);
            jsonObject.put(Constants.j_account_packageName, packageName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void fromJSON(JSONObject jsonObject){

        try {
            this.timestamp = jsonObject.getString(Constants.j_account_timestamp);
            this.userAccountName = jsonObject.getString(Constants.j_account_accountUserName);
            this.packageName = jsonObject.getString(Constants.j_account_packageName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Usato per il debug
     * @return
     */
    public void print() {
        String separator = "---";
        String s = "Account: ";
        s += "userAccountName: " + userAccountName + separator;
        s += "packageName: " + packageName + separator;

        Utility.printLog(s);
    }
    @Override
    public String toString() {
        String separator = "---";
        String s = "";
        s += "userAccountName: " + userAccountName + separator;
        s += "packageName: " + packageName + separator;
        s += "send: " +  send  + "\n";
        return s;

    }
}
