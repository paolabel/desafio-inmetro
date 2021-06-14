package com.example.apidesafio.service;

import java.util.Calendar;
import java.util.Date;

public class DateHandler {

    static final int DATE_FORMAT_LENGHT = 10;
    static final int TIME_FORMAT_LENGHT = 8;

    private static int[] getDateArray(String dateString) {
        // dateString deve estar no formato "DD/MM/YYYY"
        if(dateString.length() != DATE_FORMAT_LENGHT) {
            Exception exception = new Exception("Formato de data inválido");
            System.out.println(exception.getMessage());
            return null;
        }
        String[] dateStringArray = dateString.split("/");
        int year = Integer.parseInt(dateStringArray[2]);
        int month = Integer.parseInt(dateStringArray[1]);
        int day = Integer.parseInt(dateStringArray[0]);

        int[] dateArray = new int[]{year, month, day};
        return dateArray;
    }

    private static int[] getTimeArray(String timeString) {
        // timeString deve estar no formato "HH:MM:SS"
        if(timeString.length() != TIME_FORMAT_LENGHT) {
            Exception exception = new Exception("Formato de tempo inválido");
            System.out.println(exception.getMessage());
            return null;
        }
        String[] timeStringArray = timeString.split(":");
        int hour = Integer.parseInt(timeStringArray[0]);
        int min = Integer.parseInt(timeStringArray[1]);
        int sec = Integer.parseInt(timeStringArray[2]);

        int[] timeArray = new int[]{hour, min, sec};
        return timeArray;
    }

    public static Date getDate(String dateString) {
        int[] dateArray = getDateArray(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateArray[0], dateArray[1], dateArray[2]);
        Date date = calendar.getTime();
        return date;
    }

    public static Date getDateTime(String dateString, String timeString) {

        Calendar calendar = Calendar.getInstance();

        int[] dateArray = getDateArray(dateString);
        int[] timeArray = getTimeArray(timeString);

        calendar.set(dateArray[0], dateArray[1], dateArray[2], timeArray[0], timeArray[1], timeArray[2]);
        Date dateTime = calendar.getTime();
        
        return dateTime;
    }

    public static Long getCurrentMilliseconds() {
        Date now = Calendar.getInstance().getTime();
        Long now_ms = now.getTime();
        return now_ms;
    }

    public static Long getMilliseconds(String dateString, String timeString) {
        Date date = getDateTime(dateString, timeString);
        Long date_ms = date.getTime();
        return date_ms;
    }

}
