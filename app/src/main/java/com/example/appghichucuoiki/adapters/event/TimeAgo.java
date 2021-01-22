package com.example.appghichucuoiki.adapters.event;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {

    public String getTimeAgo(long duration) {
        Date now = new Date();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - duration);
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - duration);
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - duration);

        if(seconds < 60){
            return "vừa xong";
        } else if (minutes == 1) {
            return "1 phút trước";
        } else if (minutes > 1 && minutes < 60) {
            return minutes + " phút trước";
        } else if (hours == 1) {
            return "1 giờ trước";
        } else if (hours > 1 && hours < 24) {
            return hours + " giờ trước";
        } else if (days == 1) {
            return "1 ngày trước";
        } else {
            return days + " ngày trước";
        }

    }

}
