package com.swapuniba.crowdpulse.business_object;

import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fabio on 10/09/17.
 */

public class Contact extends AbstractData{

    public String timestamp;
    public String contactId;
    public String contactName;
    public String contactedTimes;
    public String starred;
    public ArrayList<String> contactPhoneNumbers = new ArrayList<String>();
    public Boolean send;

    public Contact(){}

    public Contact(String timestamp, String contactId, String contactName, String contactedTimes, String starred, ArrayList<String> contactPhoneNumbers, Boolean send){
        this.timestamp = timestamp;
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactedTimes = contactedTimes;
        this.starred = starred;
        this.contactPhoneNumbers = contactPhoneNumbers;
        this.send = send;
    }

    @Override
    public String getSource() {
        return Constants.j_type_contact;
    }

    @Override
    public JSONObject toJSON(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.j_source_type, getSource());
            jsonObject.put(Constants.j_contact_timestamp, Long.parseLong(timestamp));
            jsonObject.put(Constants.j_contact_contactId, contactId);
            jsonObject.put(Constants.j_contact_contactName, contactName);
            jsonObject.put(Constants.j_contact_contactedTimes, Integer.parseInt(contactedTimes));
            jsonObject.put(Constants.j_contact_starred, starred);
            JSONArray jsonArrayPhoneNumbers = new JSONArray();
            for (String phoneNumber : contactPhoneNumbers){
                jsonArrayPhoneNumbers.put(phoneNumber);
            }
            jsonObject.put(Constants.j_contact_contactPhoneNumbers, jsonArrayPhoneNumbers);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void fromJSON(JSONObject jsonObject){

        try {
            this.timestamp = jsonObject.getString(Constants.j_contact_timestamp);
            this.contactId = jsonObject.getString(Constants.j_contact_contactId);
            this.contactName = jsonObject.getString(Constants.j_contact_contactName);
            this.contactedTimes = jsonObject.getString(Constants.j_contact_contactedTimes);
            this.starred = jsonObject.getString(Constants.j_contact_starred);
            JSONArray jsonArrayPhoneNumbers = jsonObject.getJSONArray(Constants.j_contact_contactPhoneNumbers);
            for (int i = 0; i<jsonArrayPhoneNumbers.length(); i++){
                this.contactPhoneNumbers.add(jsonArrayPhoneNumbers.getString(i));
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
        String separator = "---";
        String s = "Contact: ";
        s += "contactId: " + contactId + separator;
        s += "contactName: " + contactName + separator;
        s += "contactedTimes: " + contactedTimes + separator;
        s += "starred: " + starred + separator;
        s += "contactPhoneNumbers: " + contactPhoneNumbers.toString() + separator;

        Utility.printLog(s);
        Utility.printLog("TAG-M-TD-C","dati contatti trasferiti: " + s);
    }

    @Override
    public String toString() {
        String separator = "---";
        String s = "";
        s += "contactId: " + contactId + separator;
        s += "contactName: " + contactName + separator;
        s += "contactedTimes: " + contactedTimes + separator;
        s += "starred: " + starred + separator;
        s += "contactPhoneNumbers: " + contactPhoneNumbers.toString() + separator  + "\n";
        return s;
    }
}
