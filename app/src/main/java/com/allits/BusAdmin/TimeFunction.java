package com.allits.BusAdmin;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Stefan on 19.06.2015.
 */
public class TimeFunction {
    public static long getTimeDiff(String time, String ktime){

        String[] timeparts = time.split(":");
        String[] ktimeparts = ktime.split(":");

        int sHourTime = Integer.parseInt(timeparts[0]);
        int sMinTime = Integer.parseInt(timeparts[1]);
        int sMinKtime = Integer.parseInt(ktimeparts[1]);

        int newHour;
        int newMin;

        if(sMinTime+sMinKtime>60) {
            newHour = sHourTime+1;
            newMin = (sMinTime+sMinKtime)-60;
        }else{
            newHour = sHourTime;
            newMin = sMinKtime+sMinTime;
        }

        Log.d("Time", newHour + ":" + newMin);

        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY, newHour);
        c1.set(Calendar.MINUTE, newMin);
        Calendar c2 = Calendar.getInstance();

        Date x = c1.getTime();
        Date y = c2.getTime();

        long diff = x.getTime() - y.getTime();
        long diffMin = diff / (60*1000);

        return diffMin;
    }
}
