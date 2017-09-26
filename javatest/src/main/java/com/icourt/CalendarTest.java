package com.icourt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sun.rmi.runtime.Log;

/**
 * Created by zhaodanyang on 2017/9/26.
 */

public class CalendarTest {

    public static void main(String[] args) throws Exception {

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01"));
            while (cal.getTimeInMillis() < System.currentTimeMillis()) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
                System.out.print("\n--时间--:" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cal.getTimeInMillis()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
