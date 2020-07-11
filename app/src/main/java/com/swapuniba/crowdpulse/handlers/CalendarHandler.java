package com.swapuniba.crowdpulse.handlers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.swapuniba.crowdpulse.business_object.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Cancellabile
 */
public class CalendarHandler {

    /*
    public static ArrayList<String> nameOfEvent = new ArrayList<String>();
    public static ArrayList<String> startDates = new ArrayList<String>();
    public static ArrayList<String> endDates = new ArrayList<String>();
    public static ArrayList<String> descriptions = new ArrayList<String>();


    public static ArrayList<String> readCalendarEvent(Context context) {
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[] { "calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation" }, null,
                        null, null);
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        for (int i = 0; i < CNames.length; i++) {

            nameOfEvent.add(cursor.getString(1));
            startDates.add(getDate(Long.parseLong(cursor.getString(3))));
            endDates.add(getDate(Long.parseLong(cursor.getString(4))));
            descriptions.add(cursor.getString(2));
            CNames[i] = cursor.getString(1);
            cursor.moveToNext();

        }
        return nameOfEvent;
    }
    */

    public static ArrayList<Event> readCalendarEvents(Context context) {

        ArrayList<Event> events = new ArrayList<Event>();
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[] { "calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation" }, null,
                        null, null);
        cursor.moveToFirst();

        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        for (int i = 0; i < CNames.length; i++) {
            Event event = new Event();

            event.calendarId = cursor.getString(0);
            event.nameOfEvent = cursor.getString(1);
            event.startDates = getDate(Long.parseLong(cursor.getString(3)));
            event.endDates = getDate(Long.parseLong(cursor.getString(4)));
            event.descriptions = cursor.getString(2);
            event.eventLocation = cursor.getString(5);
            CNames[i] = cursor.getString(1);
            cursor.moveToNext();

            events.add(event);

        }

        return events;
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}