package com.swapuniba.crowdpulse.config;

import com.swapuniba.crowdpulse.main.SendDataWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Ci sono tutte le costanti presenti nell'applicazione.
 * Una variazione involontaria di una di queste costanti potrebbe portare a malfunzionamenti di
 * tutta l'applicazione.
 */
public class Constants {

    //RESTART SERVICE
     public static final int SLEEP_OFF = 0; //it is not in sleep zone
     public static final int FIRST_SLEEP = 1; //it is 00:00 and 02:59:59:999 zone
     public static final int SECOND_SLEEP = 2; //it is 03:00 and 05:59:59:999 zone


    //REQUEST CODE
    public static final int REQUEST_ALARM_EXTRACTION = 4;


    //chiave in preferenze che contiene il file json in formato stringa : AlarmInfo
    public static final String tag_m_bs_data = "tag_m_bs_data";
    //chiave in preferenze che contione il file json in formato stringa : BeforeSendInfo
    public static final String tag_m_before_send_data = "tag_m_before_send_data";
    //chiave in preferenze che contione il file json in formato stringa : AfterSendInfo
    public static final String tag_m_after_send_data = "tag_m_after_send_data";
    //chiave in preferenze che contiene il file json in formato stringa : DailyCheckInfo
    public static String tag_m_daily_check_data = "tag_m_daily_check_data";


        //TEMP STRNIG, USED AS PLACEHOLDER, DELETE IT!!!
    public static final String log_tag = "Log:";
    public static final Boolean print_log = true;


    //Per mostrare Explanation Permissions
    public static final String explanation_permissions = "explanation_permissions";


    // GENERAL INFO
    public static final int app_version = 1;

    public static final int sourceThisSmarthone = 0;
    public static final int sourceWebUI = 1;

    //URL SERVER
    public static final String SERVER_URL_MASTER = "http://" + "90.147.102.243" + ":5000"; //":6542";

    //PREFERENCES
    public static final String pref_server_ip = "server_ip";
    public static final String pref_username = "username";
    public static final String pref_firstLogin = "first_login";
    public static final String pref_email = "email";
    public static final String pref_password= "password";
    public static final String pref_phoneNumber = "phoneNumber";

    //socket channels names
    public static final String channel_login = "login";
    public static final String channel_config = "config";
    public static final String channel_send_data = "send_data";
    public static final String channel_message = "message";
    public static final String channel_reload = "reload";


    //CONSTANT RESPONSE
    public static final int response_failure = 0;
    public static final int response_success = 1;
    public static final int response_receiving = 2;

    //ATTRIBUTE SOCKET config (j_ stand for json)
    public static final String j_config_setting_key = "setting_key";
    public static final String j_config_setting_value = "setting_value";
    public static final String j_config_config = "config";


    //ATTRIBUTE SOCKET send_data (j_ stand for json)
    public static final String j_code= "code";
    public static final String j_description= "description";
    public static final String j_data = "data";
    public static final String j_source_type = "source";
    public static final String j_dataIdentifier = "dataIdentifier";
    public static final String j_email = "email";
    public static final String j_password = "password";
    public static final String j_username = "username";

    public static final String j_type_deviceinfo = "deviceinfo";
    public static final String j_deviceinfo_deviceId = "deviceId";
    public static final String j_deviceinfo_brand = "brand";
    public static final String j_deviceinfo_model = "model";
    public static final String j_deviceinfo_sdk = "sdk";
    public static final String j_deviceinfo_phoneNumbers = "phoneNumbers";


    public static final String j_type_account = "accounts";
    public static final String j_account_timestamp= "timestamp";
    public static final String j_account_accountUserName = "userAccountName";
    public static final String j_account_packageName = "packageName";

    public static final String j_type_appinfo = "appinfo";
    public static final String j_appinfo_packageName = "packageName";
    public static final String j_appinfo_timestamp = "timestamp";
    public static final String j_appinfo_category = "category";
    public static final String j_appinfo_foregroundTime = "foregroundTime";

    public static final String j_type_contact = "contact";
    public static final String j_contact_timestamp = "timestamp";
    public static final String j_contact_contactId = "contactId";
    public static final String j_contact_contactName = "contactName";
    public static final String j_contact_contactedTimes = "contactedTimes";
    public static final String j_contact_starred = "starred";
    public static final String j_contact_contactPhoneNumbers = "contactPhoneNumbers";

    public static final String j_type_display = "display";
    public static final String j_display_timestamp = "timestamp";
    public static final String j_display_state = "state";

    public static final String j_type_gps = "gps";
    public static final String j_gps_timestamp = "timestamp";
    public static final String j_gps_latitude = "latitude";
    public static final String j_gps_longitude = "longitude";
    public static final String j_gps_speed = "speed";
    public static final String j_gps_accuracy = "accuracy";

    public static final String j_type_netstats = "netstats";
    public static final String j_netstats_networkType = "networkType";
    public static final String j_netstats_timestamp = "timestamp";
    public static final String j_netstats_rxBytes = "rxBytes";
    public static final String j_netstats_txBytes = "txBytes";

    public static final String j_type_activity = "activity";
    public static final String j_activity_timestamp= "timestamp";
    public static final String j_activity_inVehicle = "inVehicle";
    public static final String j_activity_onBicycle = "onBicycle";
    public static final String j_activity_onFoot = "onFoot";
    public static final String j_activity_running = "running";
    public static final String j_activity_still = "still";
    public static final String j_activity_tilting = "tilting";
    public static final String j_activity_walking = "walking";
    public static final String j_activity_unknown = "unknown";


    // RECORDING AND WRITING CODE
    public static final String no_record = "0";
    public static final String record = "1";

    public static final String on = "1";
    public static final String off = "0";

    public static final String TYPE_MOBILE = "mobile";
    public static final String TYPE_WIFI = "wifi";


    public static final String work_status = "work_status";
    public static final String job_status = "job_status";
    public static final String handler_reactive_status ="handler_reactive_status";


    public static final String WORK_ON = "1";
    public static final String WORK_OFF = "0";

    public static final String SHARE_ON = "1";
    public static final String SHARE_OFF = "0";

    //chiavi in preferenze che contengono il file json in formato stringa : DataObject
    public static final String alarm_reactive_bs = "alarm_reactive_bs";
    public static final String work_reactive_send = "work_reactive_send";
    public static final String work_reactive_delaycheck = "work_reactive_delaycheck";

    //chiave per salvare l'uuid del sendwork e del workdelaycheck
    public static final String work_reactive_send_uuid = "job_reactive_send_uuid";
    public static final String work_delay_check_uuid = "work_delay_check_uuid";


    public static final String tag_send_data_work = "com.swapuniba.crowdpulse.main.SendDataWorker";
    public static final String tag_reactive_work = "com.swapuniba.crowdpulse.reactive.DailyCheck";


    //Activity
    public static final int JOB_ACT_HANDLER_ID = 4774;

    //SERVICE PARAMETER
    public static final long restart_alarm = 1800000 ; //ogni 30 minuti


    public static final long time_one_day = 86400000; //24 ore

    public static final int delay_send_minutes = 5;
    public static final int delay_checkDaily_hours = 1 ;

    public static final int send_hours = 6;

    public static final long send_in_millis = 60000*60*6;

    public static final int checkDilay_hours = 24;

    public static final String string_true = "1";
    public static final String string_false = "0";

    //verrà utilizzata nel login per capire se si devono creare o meno i lavori e gli allarmi.
    public static final String first_installation = "first_installation";



    //PREFERENCES SETTING PERMISSION
    //IMPORTANT ADD DOWN IN THE ARRAYLIST AFTER DEFINE OTHER
    public static final String setting_read_gps = "readGPS";
    public static final String setting_read_contacts = "readContact";
    public static final String setting_read_accounts = "readAccounts";
    //public static final String setting_read_calendar = "readCalendar";
    //public static final String setting_read_sms = "readSms";
    public static final String setting_read_app = "readAppInfo";
    public static final String setting_read_netstats = "readNetStats";
    public static final String setting_read_display = "readDisplay";
    public static final String setting_read_activity = "readActivity";

    //TIME KEY
    public static final String setting_time_read_gps = "timeReadGPS";
    public static final String setting_time_read_contacts = "timeReadContact";
    public static final String setting_time_read_accounts = "timeReadAccounts";
    //public static final String setting_time_read_calendar = "timeReadCalendar";
    //public static final String setting_time_read_sms = "timeReadSms";
    public static final String setting_time_read_app = "timeReadAppInfo";
    public static final String setting_time_read_netstats = "timeReadNetStats";
    public static final String setting_time_read_activity = "timeReadActivity";

    //share
    public static final String setting_share_gps = "shareGPS";
    public static final String setting_share_contacts = "shareContact";
    public static final String setting_share_accounts = "shareAccounts";
    //public static final String setting_share_calendar = "shareCalendar";
    //public static final String setting_share_sms = "shareSms";
    public static final String setting_share_app = "shareAppInfo";
    public static final String setting_share_netstats = "shareNetStats";
    public static final String setting_share_display = "shareDisplay";
    public static final String setting_share_activity = "shareActivity";


    //contain all default_setting key
    public static final ArrayList<String> setting_keys =
            new ArrayList<String>() {{
                add(setting_read_gps);
                add(setting_read_contacts);
                add(setting_read_accounts);
                //add(setting_read_calendar);
                //add(setting_read_sms);
                add(setting_read_app);
                add(setting_read_netstats);
                add(setting_read_display);
                add(setting_read_activity);
                add(setting_time_read_gps);
                add(setting_time_read_contacts);
                add(setting_time_read_accounts);
                //add(setting_time_read_calendar);
                //add(setting_time_read_sms);
                add(setting_time_read_app);
                add(setting_time_read_netstats);
                add(setting_time_read_activity);
                add(setting_share_gps);
                add(setting_share_contacts);
                add(setting_share_accounts);
                //add(setting_share_calendar);
                //add(setting_share_sms);
                add(setting_share_app);
                add(setting_share_netstats);
                add(setting_share_display);
                add(setting_share_activity);
            }};

    //contain only key used for the permission
    public static final ArrayList<String> setting_permission_keys =
            new ArrayList<String>() {{
                add(setting_read_gps);
                add(setting_read_contacts);
                add(setting_read_accounts);
                //add(setting_read_calendar);
                //add(setting_read_sms);
                add(setting_read_app);
                add(setting_read_netstats);
                add(setting_read_display);
                add(setting_read_activity);
            }};

    //contain only key used for set the time of the reading
    public static final ArrayList<String> setting_time_permission_keys =
            new ArrayList<String>() {{
                add(setting_time_read_gps);
                add(setting_time_read_contacts);
                add(setting_time_read_accounts);
                //add(setting_time_read_calendar);
                //add(setting_time_read_sms);
                add(setting_time_read_app);
                add(setting_time_read_netstats);
                add(setting_time_read_activity);
            }};

    //contain only key used for set the share of the data
    public static final ArrayList<String> setting_share_permission_keys =
            new ArrayList<String>() {{
                add(setting_share_gps);
                add(setting_share_contacts);
                add(setting_share_accounts);
                //add(setting_share_calendar);
                //add(setting_share_sms);
                add(setting_share_app);
                add(setting_share_netstats);
                add(setting_share_display);
                add(setting_share_activity);
            }};


    public static final HashMap<String, String> default_setting = new HashMap<String, String>(){{
        put(setting_read_gps, no_record);
        put(setting_read_contacts, no_record);
        put(setting_read_accounts, no_record);
        //put(setting_read_calendar, no_record);
        //put(setting_read_sms, no_record);
        put(setting_read_app, no_record);
        put(setting_read_netstats, no_record);
        put(setting_read_display, no_record);
        put(setting_read_activity, no_record);
        put(setting_time_read_gps, "120000");
        put(setting_time_read_contacts, "3600000");
        put(setting_time_read_accounts, "3600000");
        //put(setting_time_read_calendar, "3600000");
        //put(setting_time_read_sms, "3600000");
        put(setting_time_read_app, "86400000");
        put(setting_time_read_netstats, "3600000");
        put(setting_time_read_activity, "120000");
        put(setting_share_gps, string_false);
        put(setting_share_contacts, string_false);
        put(setting_share_accounts, string_false);
        //put(setting_share_calendar, string_false);
        //put(setting_share_sms, string_false);
        put(setting_share_app, string_false);
        put(setting_share_netstats, string_false);
        put(setting_share_display, string_false);
        put(setting_share_activity, string_false);
    }};


    //mapping between interval setting key and time read
    public static final HashMap<String, String> mappingSettingTimeRead = new HashMap<String, String>(){{
        put(setting_read_gps, setting_time_read_gps);
        put(setting_read_contacts, setting_time_read_contacts);
        put(setting_read_accounts, setting_time_read_accounts);
        //put(setting_read_calendar, setting_time_read_calendar);
        //put(setting_read_sms, setting_time_read_sms);
        put(setting_read_app, setting_time_read_app);
        put(setting_read_netstats, setting_time_read_netstats);
        put(setting_read_display, "");
        put(setting_read_activity, setting_time_read_activity);
    }};

    //mapping between interval setting key and time read
    public static final HashMap<String, String> mappingSettingShare = new HashMap<String, String>(){{
        put(setting_read_gps, setting_share_gps);
        put(setting_read_contacts, setting_share_contacts);
        put(setting_read_accounts, setting_share_accounts);
        //put(setting_read_calendar, setting_share_calendar);
        //put(setting_read_sms, setting_share_sms);
        put(setting_read_app, setting_share_app);
        put(setting_read_netstats, setting_share_netstats);
        put(setting_read_display, setting_share_display);
        put(setting_read_activity, setting_share_activity);
    }};

    //mapping between setting key and time for take data
    public static final HashMap<String, ArrayList<Integer>> mappingSettingAdmissibleValue = new HashMap<String, ArrayList<Integer>>(){{
        put(setting_read_gps, new ArrayList<Integer>(){{
                                                        add(1);
                                                        add(2);
                                                        add(3);
                                                        add(4);
                                                        add(5);
                                                        add(6);
                                                        add(10);
                                                        add(12);
                                                        add(15);
                                                        add(20);
                                                        add(30);
                                                        add(60);

                                                    }});
        put(setting_read_contacts, new ArrayList<Integer>(){{
                                                        add(1);
                                                        add(2);
                                                        add(3);
                                                        add(4);
                                                        add(6);
                                                        add(8);
                                                        add(12);
                                                        add(24);
                                                    }});
        put(setting_read_accounts, new ArrayList<Integer>(){{
                                                        add(1);
                                                        add(2);
                                                        add(3);
                                                        add(4);
                                                        add(6);
                                                        add(8);
                                                        add(12);
                                                        add(24);
                                                    }});
        //put(setting_read_calendar, new ArrayList<Integer>(){{}});
        //put(setting_read_sms, new ArrayList<Integer>(){{}});
        put(setting_read_app, new ArrayList<Integer>(){{}});
        put(setting_read_netstats, new ArrayList<Integer>(){{
                                                        add(1);
                                                        add(2);
                                                        add(3);
                                                        add(4);
                                                        add(6);
                                                        add(8);
                                                        add(12);
                                                        add(24);
                                                    }});
        put(setting_read_display, new ArrayList<Integer>(){{}});
        put(setting_read_activity, new ArrayList<Integer>(){{
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
            add(6);
            add(10);
            add(12);
            add(15);
            add(20);
            add(30);
            add(60);
        }});
    }};

    //mapping between setting key and time type
    public static final HashMap<String, String> mappingSettingTimeTimeType = new HashMap<String, String>(){{
        put(setting_read_gps, type_minute);
        put(setting_read_contacts, type_hour);
        put(setting_read_accounts, type_hour);
        //put(setting_read_calendar, type_hour);
        //put(setting_read_sms, type_hour);
        put(setting_read_app, type_hour);
        put(setting_read_netstats, type_hour);
        put(setting_read_display, type_minute);
        put(setting_read_activity, type_minute);
    }};



    //preference to storage the last data timestamp send
    public static final String last_gps_send = "last_gps_send";
    public static final String last_contacts_send = "last_contacts_send";
    public static final String last_accounts_send = "last_accounts_send";
    public static final String last_sms_send = "last_sms_send";
    public static final String last_app_send = "last_app_send";
    public static final String last_netstats_send = "last_netstats_send";
    public static final String last_display_send = "last_display_send";
    public static final String last_activity_send = "last_activity_send";


    public static final String type_minute = "minute";
    public static final String type_hour = "hour";
    public static final String type_day = "day";




        /**/


}
