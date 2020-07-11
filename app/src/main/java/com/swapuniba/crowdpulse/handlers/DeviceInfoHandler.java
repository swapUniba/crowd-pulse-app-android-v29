package com.swapuniba.crowdpulse.handlers;


import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.swapuniba.crowdpulse.business_object.DeviceInfo;

import java.util.ArrayList;

public class DeviceInfoHandler {

    public static DeviceInfo readDeviceInfo(Context context) {

        DeviceInfo deviceInfo = new DeviceInfo();
        TelephonyManager telemamanger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        deviceInfo.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceInfo.brand = Build.BRAND;
        //deviceInfo.device = Build.DEVICE;
        //deviceInfo.hardware = Build.HARDWARE;
        //deviceInfo.id = Build.ID;
        deviceInfo.model = Build.MODEL;

        //deviceInfo.product = Build.PRODUCT;
        //deviceInfo.networkType = Build.TYPE;
        deviceInfo.sdk = Build.VERSION.SDK_INT + "";

        deviceInfo.phoneNumbers = new ArrayList<String>();
        //deviceInfo.phoneNumbers.add(telemamanger.getLine1Number());
        //TODO GET PHONE NUMBER OR REQUIRE IT TO USER
        //deviceInfo.phoneNumbers.add("+393333333333");
        //deviceInfo.operator = telemamanger.getSimOperatorName();


        return deviceInfo;
    }


}