package com.swapuniba.crowdpulse.handlers;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import androidx.core.app.ActivityCompat;

import com.swapuniba.crowdpulse.business_object.AbstractData;
import com.swapuniba.crowdpulse.business_object.Contact;
import com.swapuniba.crowdpulse.config.Constants;
import com.swapuniba.crowdpulse.database.DbManager;
import com.swapuniba.crowdpulse.utility.Utility;

import java.util.ArrayList;

public class ContactHandler {

    public static ArrayList<Contact> readContact(Context context) {

        ArrayList<Contact> contacts = new ArrayList<Contact>();


        ContentResolver cr = context.getContentResolver();
        if(ActivityCompat.checkSelfPermission(context , Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {

                    Contact contact = new Contact();


                    contact.timestamp = System.currentTimeMillis() + "";
                    contact.contactId = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    contact.contactName = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));
                    contact.contactedTimes = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.TIMES_CONTACTED));
                    contact.starred = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.STARRED));
                    //get phone number
                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{contact.contactId}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
                            if (!contact.contactPhoneNumbers.contains(phoneNo)) {
                                contact.contactPhoneNumbers.add(phoneNo);
                            }
                        }
                        pCur.close();
                    }

                    contact.send = false;


                    contacts.add(contact);
                }
            }

        }else{
            Utility.printLog("TAG-M-PERMESSI","Permesso non concesso");
        }
        return contacts;
    }


    /**
     * check if is pass the right time between 2 get
     * @param context
     * @return
     */
    public static Boolean checkTimeBetweenRequest(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return  Long.parseLong(preferences.getString(Constants.last_contacts_send, "0")) < System.currentTimeMillis();

    }

    /**
     * check the next time to register the data
     * @param context
     * @return
     */
    public static void setNetxTime(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Long netxTime = Long.parseLong(preferences.getString(Constants.setting_time_read_contacts, "0")) + System.currentTimeMillis();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.last_contacts_send, netxTime+ "");
        editor.apply();

    }


    public static Boolean saveContact(Contact contact, Context context){
        Boolean done = true;

        DbManager db = new DbManager(context);

        Contact dbContact = db.getContact(contact.timestamp, contact.contactId);

        if (dbContact == null){
            done = db.saveContact(contact);
        }
        else {
            db.updateContact(contact);
        }

        return done;
    }


    public static Boolean saveContactArray(ArrayList<Contact> contactArrayList, Context context){
        Boolean done = true;

        for (Contact contact : contactArrayList){
            saveContact(contact, context);
        }

        return done;

    }

    public static ArrayList<? extends AbstractData> getNotSendContact(Context context){
        DbManager dbManager = new DbManager(context);
        return dbManager.getNotSendContact();
    }



}