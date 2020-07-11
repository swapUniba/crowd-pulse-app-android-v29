package com.swapuniba.crowdpulse.business_object;

import com.swapuniba.crowdpulse.utility.Utility;

import org.json.JSONObject;

/**
 * Classe utilizzabile se si decide di estrarre i dati sugli SMS. Consultare la documentazione
 * ufficiale per la gestione di questo tipo di dato
 */

public class SMS extends AbstractData{
    public String id;
    public String thread_id;
    public String address;
    public String person;
    public String date;
    public String date_sent;
    public String protocol;
    public String read;
    public String status;
    public String type;
    public String reply_path_present;
    public String subject;
    public String body;
    public String service_center;
    public String locked;
    public String error_code;
    public String seen;
    public String semc_message_priority;
    public String parent_id;
    public String delivery_status;
    public String star_status;
    public String sequence_time;


    public void print() {
        String separator = "---";
        String s = "SMS: ";
        s += "id: " + id + separator;
        s += "thread_id: " + thread_id + separator;
        s += "address: " + address + separator;
        s += "person: " + person + separator;
        s += "timestamp: " + date + separator;
        s += "date_sent: " + date_sent;
        s += "protocol: " + protocol + separator;
        s += "read: " + read;
        s += "status: " + status + separator;
        s += "networkType: " + type;
        s += "reply_path_present: " + reply_path_present + separator;
        s += "subject: " + subject;
        s += "body: " + body + separator;
        s += "service_center: " + service_center;
        s += "locked: " + locked;
        s += "error_code: " + error_code + separator;
        s += "seen: " + seen;
        s += "semc_message_priority: " + semc_message_priority;
        s += "parent_id: " + parent_id + separator;
        s += "delivery_status: " + delivery_status;
        s += "star_status: " + star_status + separator;
        s += "sequence_time: " + sequence_time;

        Utility.printLog(s);
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
