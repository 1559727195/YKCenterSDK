//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.text.TextUtils;
import com.gizwits.gizwifisdk.enumration.GizScheduleWeekday;
import com.gizwits.gizwifisdk.enumration.GizSchedulerType;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GizDeviceSchedulerSuper {
    protected String schedulerID;
    protected GizWifiDevice schedulerOwner;
    protected String date;
    protected String time;
    protected List<GizScheduleWeekday> weekdays;
    protected List<Integer> monthDays;
    protected GizSchedulerType schedulerType;
    private boolean enabled = true;
    protected boolean isValid;

    public GizDeviceSchedulerSuper() {
    }

    public String toString() {
        return "GizDeviceScheduler [schedulerID=" + this.schedulerID + ", schedulerOwner=" + this.schedulerOwner.moreSimpleInfoMasking() + ", isValid=" + this.isValid + ", schedulerType=" + this.schedulerType + ", enabled: " + this.enabled + ", date=" + this.date + ", time: " + this.time + ", weekdays=" + this.weekdays + ", monthDays=" + this.monthDays;
    }

    protected String infoMasking() {
        return "schedulerID: " + this.schedulerID + ", isValid=" + this.isValid + ", schedulerType: " + (this.schedulerType == null ? "null" : this.schedulerType.name()) + ", date: " + this.date + ", time: " + this.time + ", weekdays: " + this.weekdays + ", monthDays: " + this.monthDays + ", schedulerOwner: " + (this.schedulerOwner == null ? "null" : this.schedulerOwner.moreSimpleInfoMasking());
    }

    public String getSchedulerID() {
        return this.schedulerID;
    }

    protected void setSchedulerID(String schedulerID) {
        this.schedulerID = schedulerID;
    }

    public GizWifiDevice getSchedulerOwner() {
        return this.schedulerOwner;
    }

    protected void setSchedulerOwner(GizWifiDevice schedulerOwner) {
        this.schedulerOwner = schedulerOwner;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<GizScheduleWeekday> getWeekdays() {
        return this.weekdays;
    }

    public void setWeekdays(List<GizScheduleWeekday> weekdays) {
        this.weekdays = weekdays;
    }

    public List<Integer> getMonthDays() {
        return this.monthDays;
    }

    public void setMonthDays(List<Integer> monthDays) {
        this.monthDays = monthDays;
    }

    protected boolean getIsValid() {
        return this.isValid;
    }

    protected void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public GizSchedulerType getSchedulerType() {
        return this.schedulerType;
    }

    protected void setSchedulerType(GizSchedulerType schedulerType) {
        this.schedulerType = schedulerType;
    }

    protected int getDiffDayByLocalAndUTC(String localTime) {
        int addDay = 0;
        long gettimeDiff = DateUtil.gettimeDiff(localTime, "00:00:00");
        Calendar calendar = new GregorianCalendar();
        int offset2 = calendar.getTimeZone().getOffset(System.currentTimeMillis()) / 1000;
        long diff = gettimeDiff - (long)offset2;
        if (diff < 0L) {
            addDay = 1;
        } else if (diff >= 0L && diff <= 86400L) {
            addDay = 0;
        } else {
            addDay = -1;
        }

        return addDay;
    }

    protected ConcurrentHashMap<String, String> getLocalDateTimeByUTC(String date, String time) {
        ConcurrentHashMap<String, String> localDateTime = new ConcurrentHashMap();
        String dateTime;
        if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(time)) {
            dateTime = DateUtil.getMyUtc(date + " " + time, "yyyy-MM-dd HH:mm");
            if (dateTime != null) {
                String[] split = dateTime.split(" ");
                if (split.length > 0) {
                    localDateTime.put("date", split[0]);
                    if (split.length > 1) {
                        localDateTime.put("time", split[1]);
                    }
                }
            }
        } else if (!TextUtils.isEmpty(time)) {
            dateTime = DateUtil.getMyUtc(time, "HH:mm");
            localDateTime.put("time", dateTime);
        }

        return localDateTime;
    }

    protected ConcurrentHashMap<String, String> getUTCDateTimeByLocal(String date, String time) {
        ConcurrentHashMap<String, String> utcDateTime = new ConcurrentHashMap();
        if (!TextUtils.isEmpty(time)) {
            if (time.contains(":")) {
                String[] split = time.split(":");
                if (split.length > 0) {
                    String s = split[0];
                    time = s.equals("24") ? "0" : time;
                    if (split.length >= 2) {
                        time = time + ":" + split[1];
                    }
                }
            }

            String dateTime;
            if (TextUtils.isEmpty(date)) {
                dateTime = DateUtil.getUtc(time, "HH:mm");
                utcDateTime.put("time", dateTime);
            } else {
                dateTime = DateUtil.getUtc(date + " " + time, "yyyy-MM-dd HH:mm");
                if (dateTime != null) {
                    String[] split = dateTime.split(" ");
                    if (split.length >= 2) {
                        utcDateTime.put("date", split[0]);
                        utcDateTime.put("time", split[1]);
                    }
                }
            }
        }

        return utcDateTime;
    }
}
