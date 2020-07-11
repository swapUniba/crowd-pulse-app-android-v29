package com.swapuniba.crowdpulse.business_object;

import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;


public class NetStats extends AbstractData{
    public String networkType;
    public String timestamp;
    public String rxBytes;
    public String txBytes;
    public Boolean send;

    public NetStats(){}

    public NetStats(String networkType, String timestamp, String rxBytes, String txBytes, Boolean send){
        this.networkType = networkType;
        this.timestamp = timestamp;
        this.rxBytes = rxBytes;
        this.txBytes = txBytes;
        this.send = send;
    }

    @Override
    public String getSource() {
        return Constants.j_type_netstats;
    }

    @Override
    public JSONObject toJSON(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.j_source_type, getSource());
            jsonObject.put(Constants.j_netstats_networkType, networkType);
            jsonObject.put(Constants.j_netstats_timestamp, Long.parseLong(timestamp));
            jsonObject.put(Constants.j_netstats_rxBytes, Long.parseLong(rxBytes));
            jsonObject.put(Constants.j_netstats_txBytes, Long.parseLong(txBytes));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void fromJSON(JSONObject jsonObject){

        try {
            this.networkType = jsonObject.getString(Constants.j_netstats_networkType);
            this.timestamp = jsonObject.getString(Constants.j_netstats_timestamp);
            this.rxBytes = jsonObject.getString(Constants.j_netstats_rxBytes);
            this.txBytes = jsonObject.getString(Constants.j_netstats_txBytes);

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
        String s = "NetStats: ";
        s += "networkType: " + networkType + separator;
        s += "timestamp: " + timestamp + separator;
        s += "rxBytes: " + rxBytes + separator;
        s += "txBytes: " + txBytes + separator;
        s += "send: " + send ;

        Utility.printLog("TAG-M-NS" , "estrazione neststats: " + s);
        Utility.printLog(s);
    }

    @Override
    public String toString(){
        String separator = "---";
        String s = "";
        s += "networkType: " + networkType + separator;
        s += "timestamp: " + timestamp + separator;
        s += "rxBytes: " + rxBytes + separator;
        s += "txBytes: " + txBytes + separator;
        s += "send: " + send  + "\n";
        return s;
    }

}
