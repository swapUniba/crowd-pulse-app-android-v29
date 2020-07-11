package com.swapuniba.crowdpulse.handlers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.swapuniba.crowdpulse.business_object.SMS;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.ArrayList;

public class SMSHandler {

    public static ArrayList<SMS> readSMS(Context context) {

        Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
        Uri allSMS = Uri.parse("content://sms/");

        ArrayList<SMS> messages = new ArrayList<SMS>();

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(allSMS, null, null, null, null);
            if (cursor == null) {
                Utility.printLog("cursor is null. uri: " + mSmsQueryUri);

            }
            for (boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
                SMS sms = new SMS();

                sms.id = cursor.getString(cursor.getColumnIndex("_id"));
                sms.thread_id = cursor.getString(cursor.getColumnIndex("thread_id"));
                sms.address = cursor.getString(cursor.getColumnIndex("address"));
                sms.person = cursor.getString(cursor.getColumnIndex("person"));
                sms.date = cursor.getString(cursor.getColumnIndex("timestamp"));
                sms.date_sent = cursor.getString(cursor.getColumnIndex("date_sent"));
                sms.protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                sms.read = cursor.getString(cursor.getColumnIndex("read"));
                sms.status = cursor.getString(cursor.getColumnIndex("status"));
                sms.type = cursor.getString(cursor.getColumnIndex("networkType"));
                sms.reply_path_present = cursor.getString(cursor.getColumnIndex("reply_path_present"));
                sms.subject = cursor.getString(cursor.getColumnIndex("subject"));
                sms.body = cursor.getString(cursor.getColumnIndex("body"));
                sms.service_center = cursor.getString(cursor.getColumnIndex("service_center"));
                sms.locked = cursor.getString(cursor.getColumnIndex("locked"));
                sms.error_code = cursor.getString(cursor.getColumnIndex("error_code"));
                sms.seen = cursor.getString(cursor.getColumnIndex("seen"));
                sms.semc_message_priority = cursor.getString(cursor.getColumnIndex("semc_message_priority"));
                sms.parent_id = cursor.getString(cursor.getColumnIndex("parent_id"));
                sms.delivery_status = cursor.getString(cursor.getColumnIndex("delivery_status"));
                sms.star_status = cursor.getString(cursor.getColumnIndex("star_status"));
                sms.sequence_time = cursor.getString(cursor.getColumnIndex("sequence_time"));


                Utility.printLog("**************************************************");
                sms.print();
                Utility.printLog("**************************************************");

                messages.add(sms);

            }
        } catch (Exception e) {
            Log.e(Constants.log_tag, e.getMessage());
            messages = null;
        } finally {
            if (cursor != null){
                cursor.close();
            }

        }

        return messages;

    }

}