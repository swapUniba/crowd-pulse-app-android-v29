package com.swapuniba.crowdpulse.business_object;

import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DeviceInfo extends AbstractData{

    public String deviceId;
    public String brand;
    public String model;
    public String sdk;
    public ArrayList<String> phoneNumbers = new ArrayList<String>();


    @Override
    public String getSource() {
        return Constants.j_type_deviceinfo;
    }

    public JSONObject toJSON(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.j_source_type, getSource());
            jsonObject.put(Constants.j_deviceinfo_deviceId, deviceId);
            jsonObject.put(Constants.j_deviceinfo_brand, brand);
            jsonObject.put(Constants.j_deviceinfo_model, model);
            jsonObject.put(Constants.j_deviceinfo_sdk, sdk);
            JSONArray jsonArrayPhoneNumbers = new JSONArray();
            for (String phoneNumber : phoneNumbers){
                jsonArrayPhoneNumbers.put(phoneNumber);
            }
            jsonObject.put(Constants.j_deviceinfo_phoneNumbers, jsonArrayPhoneNumbers);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void fromJSON(JSONObject jsonObject){

        try {
            this.deviceId = jsonObject.getString(Constants.j_deviceinfo_deviceId);
            this.brand = jsonObject.getString(Constants.j_deviceinfo_brand);
            this.model = jsonObject.getString(Constants.j_deviceinfo_model);
            this.sdk = jsonObject.getString(Constants.j_deviceinfo_sdk);
            JSONArray jsonArrayPhoneNumbers = jsonObject.getJSONArray(Constants.j_deviceinfo_phoneNumbers);
            for (int i = 0; i<jsonArrayPhoneNumbers.length(); i++){
                this.phoneNumbers.add(jsonArrayPhoneNumbers.getString(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * used for debug
     * @return
     */
    public void print() {
        String separator = "---\n";
        String s = "DeviceInfo: ";
        s += "deviceId: " + deviceId + separator;
        s += "brand: " + brand + separator;
        s += "model: " + model + separator;
        s += "sdk: " + sdk + separator;
        s += "phoneNumbers: " + phoneNumbers.toString() + separator;
        Utility.printLog(s);
        Utility.printLog("TAG-M-TD-DI","dati device info trasferiti: " + s);
    }

}
