//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class DateUtil implements Serializable {
    private static final long serialVersionUID = -3098985139095632110L;

    private DateUtil() {
    }

    public static String dateFormat(String sdate) {
        return dateFormat(sdate, "yyyy-MM-dd");
    }

    public static String dateFormat(String sdate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = Date.valueOf(sdate);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static long getIntervalDays(String sd, String ed) {
        return (Date.valueOf(ed).getTime() - Date.valueOf(sd).getTime()) / 86400000L;
    }

    public static int getInterval(String beginMonth, String endMonth) {
        int intBeginYear = Integer.parseInt(beginMonth.substring(0, 4));
        int intBeginMonth = Integer.parseInt(beginMonth.substring(beginMonth.indexOf("-") + 1));
        int intEndYear = Integer.parseInt(endMonth.substring(0, 4));
        int intEndMonth = Integer.parseInt(endMonth.substring(endMonth.indexOf("-") + 1));
        return (intEndYear - intBeginYear) * 12 + (intEndMonth - intBeginMonth) + 1;
    }

    public static java.util.Date getDate(String sDate, String dateFormat) {
        SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
        ParsePosition pos = new ParsePosition(0);
        return fmt.parse(sDate, pos);
    }

    public static String getCurrentYear() {
        return getFormatCurrentTime("yyyy");
    }

    public static String getBeforeYear() {
        String currentYear = getFormatCurrentTime("yyyy");
        int beforeYear = Integer.parseInt(currentYear) - 1;
        return "" + beforeYear;
    }

    public static String getCurrentMonth() {
        return getFormatCurrentTime("MM");
    }

    public static String getCurrentDay() {
        return getFormatCurrentTime("dd");
    }

    public static String getCurrentDate() {
        return getFormatDateTime(new java.util.Date(), "yyyy-MM-dd");
    }

    public static String getFormatDate(java.util.Date date) {
        return getFormatDateTime(date, "yyyy-MM-dd");
    }

    public static String getFormatDate(String format) {
        return getFormatDateTime(new java.util.Date(), format);
    }

    public static String getCurrentTime() {
        return getFormatDateTime(new java.util.Date(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getFormatTime(java.util.Date date) {
        return getFormatDateTime(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getFormatCurrentTime(String format) {
        return getFormatDateTime(new java.util.Date(), format);
    }

    public static String getFormatDateTime(java.util.Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static java.util.Date getDateObj(int year, int month, int day) {
        Calendar c = new GregorianCalendar();
        c.set(year, month - 1, day);
        return c.getTime();
    }

    public static java.util.Date getDateObj(String args, String split) {
        String[] temp = args.split(split);
        int year = new Integer(temp[0]);
        int month = new Integer(temp[1]);
        int day = new Integer(temp[2]);
        return getDateObj(year, month, day);
    }

    public static java.util.Date getDateFromString(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        java.util.Date resDate = null;

        try {
            resDate = sdf.parse(dateStr);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return resDate;
    }

    public static java.util.Date getDateObj() {
        Calendar c = new GregorianCalendar();
        return c.getTime();
    }

    public static int getDaysOfCurMonth() {
        int curyear = new Integer(getCurrentYear());
        int curMonth = new Integer(getCurrentMonth());
        int[] mArray = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (curyear % 400 == 0 || curyear % 100 != 0 && curyear % 4 == 0) {
            mArray[1] = 29;
        }

        return mArray[curMonth - 1];
    }

    public static int getDaysOfCurMonth(String time) {
        String[] timeArray = time.split("-");
        int curyear = new Integer(timeArray[0]);
        int curMonth = new Integer(timeArray[1]);
        int[] mArray = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (curyear % 400 == 0 || curyear % 100 != 0 && curyear % 4 == 0) {
            mArray[1] = 29;
        }

        return curMonth == 12 ? mArray[0] : mArray[curMonth - 1];
    }

    public static int getDayofWeekInMonth(String year, String month, String weekOfMonth, String dayOfWeek) {
        Calendar cal = new GregorianCalendar();
        int y = new Integer(year);
        int m = new Integer(month);
        cal.clear();
        cal.set(y, m - 1, 1);
        cal.set(8, new Integer(weekOfMonth));
        cal.set(7, new Integer(dayOfWeek));
        return cal.get(5);
    }

    public static java.util.Date getDate(int year, int month, int date, int hourOfDay, int minute, int second) {
        Calendar cal = new GregorianCalendar();
        cal.set(year, month, date, hourOfDay, minute, second);
        return cal.getTime();
    }

    public static int getDayOfWeek(String year, String month, String day) {
        Calendar cal = new GregorianCalendar(new Integer(year), new Integer(month) - 1, new Integer(day));
        return cal.get(7);
    }

    public static int getDayOfWeek(String date) {
        String[] temp = null;
        if (date.indexOf("/") > 0) {
            temp = date.split("/");
        }

        if (date.indexOf("-") > 0) {
            temp = date.split("-");
        }

        return getDayOfWeek(temp[0], temp[1], temp[2]);
    }

    public static String getChinaDayOfWeek(String date) {
        String[] weeks = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int week = getDayOfWeek(date);
        return weeks[week - 1];
    }

    public static int getDayOfWeek(java.util.Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(7);
    }

    public static int getWeekOfYear(String year, String month, String day) {
        Calendar cal = new GregorianCalendar();
        cal.clear();
        cal.set(new Integer(year), new Integer(month) - 1, new Integer(day));
        return cal.get(3);
    }

    public static java.util.Date getDateAdd(java.util.Date date, int amount) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(5, amount);
        return cal.getTime();
    }

    public static String getFormatDateAdd(java.util.Date date, int amount, String format) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(5, amount);
        return getFormatDateTime(cal.getTime(), format);
    }

    public static String getFormatCurrentAdd(int amount, String format) {
        java.util.Date d = getDateAdd(new java.util.Date(), amount);
        return getFormatDateTime(d, format);
    }

    public static String getFormatYestoday(String format) {
        return getFormatCurrentAdd(-1, format);
    }

    public static String getYestoday(String sourceDate, String format) {
        return getFormatDateAdd(getDateFromString(sourceDate, format), -1, format);
    }

    public static String getFormatTomorrow(String format) {
        return getFormatCurrentAdd(1, format);
    }

    public static String getFormatDateTommorrow(String sourceDate, String format) {
        return getFormatDateAdd(getDateFromString(sourceDate, format), 1, format);
    }

    public static String getCurrentDateString(String dateFormat) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(cal.getTime());
    }

    /** @deprecated */
    public static String getCurDate() {
        GregorianCalendar gcDate = new GregorianCalendar();
        int year = gcDate.get(1);
        int month = gcDate.get(2) + 1;
        int day = gcDate.get(5);
        int hour = gcDate.get(11);
        int minute = gcDate.get(12);
        int sen = gcDate.get(13);
        String y = (new Integer(year)).toString();
        String m;
        if (month < 10) {
            m = "0" + (new Integer(month)).toString();
        } else {
            m = (new Integer(month)).toString();
        }

        String d;
        if (day < 10) {
            d = "0" + (new Integer(day)).toString();
        } else {
            d = (new Integer(day)).toString();
        }

        String h;
        if (hour < 10) {
            h = "0" + (new Integer(hour)).toString();
        } else {
            h = (new Integer(hour)).toString();
        }

        String n;
        if (minute < 10) {
            n = "0" + (new Integer(minute)).toString();
        } else {
            n = (new Integer(minute)).toString();
        }

        String s;
        if (sen < 10) {
            s = "0" + (new Integer(sen)).toString();
        } else {
            s = (new Integer(sen)).toString();
        }

        return "" + y + m + d + h + n + s;
    }

    public static String getCurTimeByFormat(String format) {
        java.util.Date newdate = new java.util.Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(newdate);
    }

    public static long getDiff(String startTime, String endTime) {
        long diff = 0L;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
                java.util.Date startDate = ft.parse(startTime);
                java.util.Date endDate = ft.parse(endTime);
                diff = startDate.getTime() - endDate.getTime();
                diff /= 1000L;
            }
        } catch (ParseException var7) {
            var7.printStackTrace();
        }

        return diff;
    }

    public static long gettimeDiff(String startTime, String endTime) {
        if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
            long diff = 0L;
            SimpleDateFormat ft = new SimpleDateFormat("HH:mm");

            try {
                java.util.Date startDate = ft.parse(startTime);
                java.util.Date endDate = ft.parse(endTime);
                diff = startDate.getTime() - endDate.getTime();
                diff /= 1000L;
            } catch (ParseException var7) {
                var7.printStackTrace();
                SDKLog.d("catch excepetion: " + var7.getMessage());
            }

            return diff;
        } else {
            return 0L;
        }
    }

    public static String getHour(long second) {
        long hour = second / 60L / 60L;
        long minute = (second - hour * 60L * 60L) / 60L;
        long sec = second - hour * 60L * 60L - minute * 60L;
        return hour + "小时" + minute + "分钟" + sec + "秒";
    }

    public static String getDateTime(long microsecond) {
        return getFormatDateTime(new java.util.Date(microsecond), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateByAddFltHour(float flt) {
        int addMinute = (int)(flt * 60.0F);
        Calendar cal = new GregorianCalendar();
        cal.setTime(new java.util.Date());
        cal.add(12, addMinute);
        return getFormatDateTime(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateByAddHour(String datetime, int minute) {
        String returnTime = null;
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            java.util.Date date = ft.parse(datetime);
            cal.setTime(date);
            cal.add(12, minute);
            returnTime = getFormatDateTime(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException var7) {
            var7.printStackTrace();
        }

        return returnTime;
    }

    public static int getDiffHour(String startTime, String endTime) {
        long diff = 0L;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            java.util.Date startDate = ft.parse(startTime);
            java.util.Date endDate = ft.parse(endTime);
            diff = startDate.getTime() - endDate.getTime();
            diff /= 3600000L;
        } catch (ParseException var7) {
            var7.printStackTrace();
        }

        return (new Long(diff)).intValue();
    }

    public static String getYearSelect(String selectName, String value, int startYear, int endYear) {
        int start = startYear;
        int end = endYear;
        if (startYear > endYear) {
            start = endYear;
            end = startYear;
        }

        StringBuffer sb = new StringBuffer("");
        sb.append("<select name=\"" + selectName + "\">");

        for(int i = start; i <= end; ++i) {
            if (!value.trim().equals("") && i == Integer.parseInt(value)) {
                sb.append("<option value=\"" + i + "\" selected>" + i + "</option>");
            } else {
                sb.append("<option value=\"" + i + "\">" + i + "</option>");
            }
        }

        sb.append("</select>");
        return sb.toString();
    }

    public static String getYearSelect(String selectName, String value, int startYear, int endYear, boolean hasBlank) {
        int start = startYear;
        int end = endYear;
        if (startYear > endYear) {
            start = endYear;
            end = startYear;
        }

        StringBuffer sb = new StringBuffer("");
        sb.append("<select name=\"" + selectName + "\">");
        if (hasBlank) {
            sb.append("<option value=\"\"></option>");
        }

        for(int i = start; i <= end; ++i) {
            if (!value.trim().equals("") && i == Integer.parseInt(value)) {
                sb.append("<option value=\"" + i + "\" selected>" + i + "</option>");
            } else {
                sb.append("<option value=\"" + i + "\">" + i + "</option>");
            }
        }

        sb.append("</select>");
        return sb.toString();
    }

    public static String getYearSelect(String selectName, String value, int startYear, int endYear, boolean hasBlank, String js) {
        int start = startYear;
        int end = endYear;
        if (startYear > endYear) {
            start = endYear;
            end = startYear;
        }

        StringBuffer sb = new StringBuffer("");
        sb.append("<select name=\"" + selectName + "\" " + js + ">");
        if (hasBlank) {
            sb.append("<option value=\"\"></option>");
        }

        for(int i = start; i <= end; ++i) {
            if (!value.trim().equals("") && i == Integer.parseInt(value)) {
                sb.append("<option value=\"" + i + "\" selected>" + i + "</option>");
            } else {
                sb.append("<option value=\"" + i + "\">" + i + "</option>");
            }
        }

        sb.append("</select>");
        return sb.toString();
    }

    public static String getYearSelect(String selectName, String value, int startYear, int endYear, String js) {
        int start = startYear;
        int end = endYear;
        if (startYear > endYear) {
            start = endYear;
            end = startYear;
        }

        StringBuffer sb = new StringBuffer("");
        sb.append("<select name=\"" + selectName + "\" " + js + ">");

        for(int i = start; i <= end; ++i) {
            if (!value.trim().equals("") && i == Integer.parseInt(value)) {
                sb.append("<option value=\"" + i + "\" selected>" + i + "</option>");
            } else {
                sb.append("<option value=\"" + i + "\">" + i + "</option>");
            }
        }

        sb.append("</select>");
        return sb.toString();
    }

    public static String getMonthSelect(String selectName, String value, boolean hasBlank) {
        StringBuffer sb = new StringBuffer("");
        sb.append("<select name=\"" + selectName + "\">");
        if (hasBlank) {
            sb.append("<option value=\"\"></option>");
        }

        for(int i = 1; i <= 12; ++i) {
            if (!value.trim().equals("") && i == Integer.parseInt(value)) {
                sb.append("<option value=\"" + i + "\" selected>" + i + "</option>");
            } else {
                sb.append("<option value=\"" + i + "\">" + i + "</option>");
            }
        }

        sb.append("</select>");
        return sb.toString();
    }

    public static String getMonthSelect(String selectName, String value, boolean hasBlank, String js) {
        StringBuffer sb = new StringBuffer("");
        sb.append("<select name=\"" + selectName + "\" " + js + ">");
        if (hasBlank) {
            sb.append("<option value=\"\"></option>");
        }

        for(int i = 1; i <= 12; ++i) {
            if (!value.trim().equals("") && i == Integer.parseInt(value)) {
                sb.append("<option value=\"" + i + "\" selected>" + i + "</option>");
            } else {
                sb.append("<option value=\"" + i + "\">" + i + "</option>");
            }
        }

        sb.append("</select>");
        return sb.toString();
    }

    public static String getDaySelect(String selectName, String value, boolean hasBlank) {
        StringBuffer sb = new StringBuffer("");
        sb.append("<select name=\"" + selectName + "\">");
        if (hasBlank) {
            sb.append("<option value=\"\"></option>");
        }

        for(int i = 1; i <= 31; ++i) {
            if (!value.trim().equals("") && i == Integer.parseInt(value)) {
                sb.append("<option value=\"" + i + "\" selected>" + i + "</option>");
            } else {
                sb.append("<option value=\"" + i + "\">" + i + "</option>");
            }
        }

        sb.append("</select>");
        return sb.toString();
    }

    public static String getDaySelect(String selectName, String value, boolean hasBlank, String js) {
        StringBuffer sb = new StringBuffer("");
        sb.append("<select name=\"" + selectName + "\" " + js + ">");
        if (hasBlank) {
            sb.append("<option value=\"\"></option>");
        }

        for(int i = 1; i <= 31; ++i) {
            if (!value.trim().equals("") && i == Integer.parseInt(value)) {
                sb.append("<option value=\"" + i + "\" selected>" + i + "</option>");
            } else {
                sb.append("<option value=\"" + i + "\">" + i + "</option>");
            }
        }

        sb.append("</select>");
        return sb.toString();
    }

    public static int countWeekend(String startDate, String endDate) {
        int result = 0;
        java.util.Date sdate = null;
        java.util.Date edate = null;
        sdate = getDateObj(startDate, "/");
        edate = getDateObj(endDate, "/");
        int sumDays = Math.abs(getDiffDays(startDate, endDate));
        int dayOfWeek = 0;

        for(int i = 0; i <= sumDays; ++i) {
            dayOfWeek = getDayOfWeek(getDateAdd(sdate, i));
            if (dayOfWeek == 1 || dayOfWeek == 7) {
                ++result;
            }
        }

        return result;
    }

    public static int getDiffDays(String startDate, String endDate) {
        long diff = 0L;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try {
            java.util.Date sDate = ft.parse(startDate + " 00:00:00");
            java.util.Date eDate = ft.parse(endDate + " 00:00:00");
            diff = eDate.getTime() - sDate.getTime();
            diff /= 86400000L;
        } catch (ParseException var7) {
            var7.printStackTrace();
        }

        return (int)diff;
    }

    public static String[] getArrayDiffDays(String startDate, String endDate) {
        int LEN = 0;
        if (startDate.equals(endDate)) {
            return new String[]{startDate};
        } else {
            java.util.Date sdate = null;
            sdate = getDateObj(startDate, "/");
             LEN = getDiffDays(startDate, endDate);
            String[] dateResult = new String[LEN + 1];
            dateResult[0] = startDate;

            for(int i = 1; i < LEN + 1; ++i) {
                dateResult[i] = getFormatDateTime(getDateAdd(sdate, i), "yyyy/MM/dd");
            }

            return dateResult;
        }
    }

    public static int getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String displayName = tz.getDisplayName();
        int dstSavings = tz.getDSTSavings();
        String id = tz.getID();
        String displayName2 = tz.getDisplayName(false, 0);
        return 1;
    }

    public static String getUtc(String time, String formart) {
        String date = null;
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(15);
        int dstOffset = cal.get(16);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
        java.util.Date stringToDate = stringToDate(time, formart);
        if (stringToDate != null) {
            cal1.setTime(stringToDate);
            cal1.add(14, -(zoneOffset + dstOffset));
            date = dateToString(cal1.getTime(), formart);
        }

        return date;
    }

    public static String getMyUtc(String time, String formart) {
        String date = null;
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(15);
        int dstOffset = cal.get(16);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
        java.util.Date stringToDate = stringToDate(time, formart);
        if (stringToDate != null) {
            cal1.setTime(stringToDate);
            cal1.add(14, zoneOffset + dstOffset);
            date = dateToString(cal1.getTime(), formart);
        }

        return date;
    }

    @SuppressLint({"SimpleDateFormat"})
    public static java.util.Date stringToDate(String str, String formart) {
        DateFormat format = new SimpleDateFormat(formart);
        java.util.Date date = null;

        try {
            date = format.parse(str);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return date;
    }

    @SuppressLint({"SimpleDateFormat"})
    public static String dateToString(java.util.Date date, String type) {
        String str = null;
        SimpleDateFormat format = new SimpleDateFormat(type);
        str = format.format(date);
        return str;
    }

    public static String getLogString(Context context) {
        String net_type = Utils.changeString("net_type");
        String net_name = Utils.changeString("net_name");
        String GSM_standard = Utils.changeString("GSM_standard");
        String zone_city = Utils.changeString("zone_city");
        String s = "";
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService("connectivity");
        NetworkInfo info = manager.getActiveNetworkInfo();
        String wifiSSID;
        if (info != null && manager.getBackgroundDataSetting()) {
            int netType = info.getType();
            if (netType == 1) {
                wifiSSID = Utils.getWifiSSID(context);
                s = net_name + " : " + Utils.changeString(wifiSSID) + "," + net_type + " : " + Utils.changeString("WiFi");
            } else if (netType == 0) {
                TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
                String simOperatorName = tm.getSimOperatorName();
                if (TextUtils.isEmpty(simOperatorName)) {
                    simOperatorName = "MCC";
                }

                s = net_name + " : " + Utils.changeString(simOperatorName) + "," + net_type + " : " + Utils.changeString("GSM");
            }
        } else {
            s = net_name + ": " + Utils.changeString("null") + "," + net_type + ": " + Utils.changeString("null");
        }

        TimeZone tz = TimeZone.getDefault();
        wifiSSID = tz.getID();
        s = s + "," + zone_city + " : " + Utils.changeString(wifiSSID) + "," + GSM_standard + " : " + Utils.changeString("");
        return s;
    }

    public static String utc2Local(String utcTime, String utcTimePatten, String localTimePatten) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        java.util.Date gpsUTCDate = null;

        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException var7) {
            var7.printStackTrace();
        }

        SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }
}
