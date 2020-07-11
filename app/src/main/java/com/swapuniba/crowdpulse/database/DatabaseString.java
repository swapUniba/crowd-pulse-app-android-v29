package com.swapuniba.crowdpulse.database;


/**
 * Created by fabio on 20/04/16.
 */
public class DatabaseString
{

    public static final String db_name = "crowdpulse";

    //table GPS
    public static final String tab_gps = "gps";

    public static final String gps_timestamp = "detection_time";
    public static final String gps_latitude = "latitude";
    public static final String gps_longitude = "longitude";
    public static final String gps_speed  = "speed";
    public static final String gps_accuracy = "accuracy";
    public static final String gps_send  = "send";


    //table NetStats
    public static final String tab_netstats = "netstats";

    public static final String netstats_networkType = "networkType";
    public static final String netstats_timestamp = "detection_time";
    public static final String netstats_rxBytes = "rxBytes";
    public static final String netstats_txBytes = "txBytes";
    public static final String netstats_send = "send";


    //table AppInfo
    public static final String tab_appinfo = "appinfo";

    public static final String appinfo_packageName = "packageName";
    public static final String appinfo_timestamp = "detection_time";
    public static final String appinfo_category = "category";
    public static final String appinfo_foregroundTime = "foregroundTime";
    public static final String appinfo_send = "send";


    //table Display
    public static final String tab_display = "display";

    public static final String display_timestamp = "detection_time";
    public static final String display_state = "state";
    public static final String display_send = "send";


    //table Account
    public static final String tab_account = "account";

    public static final String account_timestamp = "detection_time";
    public static final String account_userAccountName = "userAccountName";
    public static final String account_packageName = "packageName";
    public static final String account_send = "send";


    //table Contact
    public static final String tab_contact= "contact";

    public static final String contact_timestamp = "detection_time";
    public static final String contact_contactId = "contactId";
    public static final String contact_contactName = "contactName";
    public static final String contact_contactedTimes = "contactedTimes";
    public static final String contact_starred = "starred";
    public static final String contact_contactPhoneNumbers = "contactPhoneNumbers";
    public static final String contact_send = "send";


    //table Activity
    public static final String tab_activity= "activity";

    public static final String activity_timestamp = "detection_time";
    public static final String activity_inVehicle = "inVehicle";
    public static final String activity_onBicycle = "onBicycle";
    public static final String activity_onFoot = "onFoot";
    public static final String activity_running = "running";
    public static final String activity_still = "still";
    public static final String activity_tilting = "tilting";
    public static final String activity_walking = "walking";
    public static final String activity_unknown = "unknown";
    public static final String activity_send = "send";










}