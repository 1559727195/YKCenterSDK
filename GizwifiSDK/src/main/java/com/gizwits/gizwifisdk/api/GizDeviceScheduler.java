//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

import com.gizwits.gizwifisdk.enumration.GizScheduleRepeatRule;
import com.gizwits.gizwifisdk.enumration.GizScheduleWeekday;
import com.gizwits.gizwifisdk.enumration.GizSchedulerType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceSchedulerListener;
import com.gizwits.gizwifisdk.log.SDKLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceScheduler extends GizDeviceSchedulerSuper implements Parcelable {
    private GizDeviceSchedulerListener mListener;
    private static final int MSG_RECV = 5;
    private String remark;
    private String startDate;
    private String endDate;
    private String createdDateTime;
    private ConcurrentHashMap<String, Object> attrs;
    private GizScheduleRepeatRule repeatRule;
    private int repeatCount;
    private boolean enabled = true;
    Handler timeoutHandler;
    Handler messageHandler;
    public static final Creator<GizDeviceScheduler> CREATOR = new Creator<GizDeviceScheduler>() {
        public GizDeviceScheduler createFromParcel(Parcel source) {
            GizWifiDevice device = (GizWifiDevice) source.readParcelable(GizWifiDevice.class.getClassLoader());
            if (device == null) {
                GizDeviceScheduler group = new GizDeviceScheduler();
                group.schedulerID = source.readString();
                group.createdDateTime = source.readString();
                group.date = source.readString();
                group.time = source.readString();
                group.remark = source.readString();
                group.startDate = source.readString();
                group.endDate = source.readString();
                group.setEnabled(source.readInt() == 1);
                HashMap<String, Object> map = source.readHashMap(HashMap.class.getClassLoader());
                ConcurrentHashMap<String, Object> hash = new ConcurrentHashMap();
                Iterator var12 = map.keySet().iterator();

                while (var12.hasNext()) {
                    String key = (String) var12.next();
                    hash.put(key, map.get(key));
                }
                group.attrs = hash;
                List<Integer> monthDays = new ArrayList();
                source.readList(monthDays, Integer.class.getClassLoader());
                group.monthDays = monthDays;
                List<GizScheduleWeekday> weekdays = new ArrayList();
                source.readList(weekdays, GizScheduleWeekday.class.getClassLoader());
                group.weekdays = weekdays;
                group.schedulerType = GizSchedulerType.valueOf(source.readInt());
                group.schedulerOwner = device;
                return group;
            } else {
                String schedulerid = source.readString();
                ConcurrentHashMap<GizWifiDevice, List<GizDeviceSchedulerSuper>> schedulerList = GizDeviceSchedulerCenter.getSchedulerList();
                List<GizDeviceSchedulerSuper> list = (List) schedulerList.get(device);
                GizDeviceScheduler scd = null;
                if (list != null) {
                    Iterator var7 = list.iterator();
                    while (var7.hasNext()) {
                        GizDeviceSchedulerSuper gizDeviceScheduler = (GizDeviceSchedulerSuper) var7.next();
                        if (gizDeviceScheduler.getSchedulerID().equals(schedulerid)) {
                            scd = (GizDeviceScheduler) gizDeviceScheduler;
                        }
                    }
                }
                return scd;
            }
        }

        public GizDeviceScheduler[] newArray(int size) {
            return null;
        }
    };

    public String toString() {
        String log = "GizDeviceScheduler [" + super.toString();
        return log + ", createdDateTime=" + this.createdDateTime + ", remark=" + this.remark + ", startDate=" + this.startDate + ", endDate=" + this.endDate + ", attrs=" + this.attrs + ", mListener=" + this.mListener + "]";
    }

    protected String infoMasking() {
        String superMasking = super.infoMasking();
        String thisMasking = "->[enabled: " + this.enabled + "remark: " + this.remark + ", startDate: " + this.startDate + ", endDate: " + this.endDate + ", attrs: " + this.attrs + ", listener:" + this.mListener + "]";
        return superMasking + thisMasking;
    }

    /**
     * @deprecated
     */
    public GizDeviceScheduler() {
        this.timeoutHandler = new NamelessClass_2(Looper.getMainLooper());
        this.messageHandler = new NamelessClass_1(Looper.getMainLooper());
    }

    public void setListener(GizDeviceSchedulerListener listener) {
        this.mListener = listener;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreatedDateTime() {
        return this.createdDateTime;
    }

    protected void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public ConcurrentHashMap<String, Object> getAttrs() {
        return this.attrs;
    }

    public void setAttrs(ConcurrentHashMap<String, Object> attrs) {
        this.attrs = attrs;
    }

    /**
     * @deprecated
     */
    public GizScheduleRepeatRule getRepeatRule() {
        return this.repeatRule;
    }

    public GizDeviceScheduler(ConcurrentHashMap<String, Object> attrs, String date, String time, boolean enabled, String remark) {
        this.timeoutHandler = new NamelessClass_2(Looper.getMainLooper());
        this.messageHandler = new NamelessClass_1(Looper.getMainLooper());
        this.setSchedulerType(GizSchedulerType.GizSchedulerOneTime);
        this.setIsValid(true);
        this.setAttrs(attrs);
        this.setDate(date);
        this.setTime(time);
        this.setEnabled(enabled);
        this.setRemark(remark);
    }

    public GizDeviceScheduler(ConcurrentHashMap<String, Object> attrs, String time, List<GizScheduleWeekday> weekDays, boolean enabled, String remark) {
        this.timeoutHandler = new NamelessClass_2(Looper.getMainLooper());
        this.messageHandler = new NamelessClass_1(Looper.getMainLooper());
        this.setSchedulerType(GizSchedulerType.GizSchedulerWeekRepeat);
        this.setIsValid(true);
        this.setAttrs(attrs);
        this.setTime(time);
        this.setWeekdays(weekDays);
        this.setEnabled(enabled);
        this.setRemark(remark);
    }

    class NamelessClass_2 extends Handler {
        NamelessClass_2(Looper x0) {
            super(x0);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GizDeviceScheduler.this.handleTimeoutMessage(msg);
        }
    }

    class NamelessClass_1 extends Handler {
        NamelessClass_1(Looper x0) {
            super(x0);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 5:
                    try {
                        String jsonStr = (String) msg.obj;
                        JSONObject obj = new JSONObject(jsonStr);
                        int cmd = obj.has("cmd") ? Integer.parseInt(obj.getString("cmd")) : 0;
                        int sn = obj.has("sn") ? Integer.parseInt(obj.getString("sn")) : 0;
                        if (cmd > 2000) {
                            sn = 0;
                        }

                        GizDeviceScheduler.this.handleReceiveMessage(cmd, obj, sn);
                    } catch (NumberFormatException var6) {
                        var6.printStackTrace();
                    } catch (JSONException var7) {
                        var7.printStackTrace();
                    }
                default:
            }
        }
    }


    public GizDeviceScheduler(String time, ConcurrentHashMap<String, Object> attrs, List<Integer> monthDays, boolean enabled, String remark) {
        this.timeoutHandler = new NamelessClass_2(Looper.getMainLooper());
        this.messageHandler = new NamelessClass_1(Looper.getMainLooper());
        this.setSchedulerType(GizSchedulerType.GizSchedulerDayRepeat);
        this.setIsValid(true);
        this.setTime(time);
        this.setAttrs(attrs);
        this.setMonthDays(monthDays);
        this.setEnabled(enabled);
        this.setRemark(remark);
    }

    /**
     * @deprecated
     */
    public void setRepeatRule(GizScheduleRepeatRule repeatRule) {
        this.repeatRule = repeatRule;
    }

    /**
     * @deprecated
     */
    public int getRepeatCount() {
        return this.repeatCount;
    }

    /**
     * @deprecated
     */
    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public void editSchedulerInfo(String uid, String token, GizSchedulerType type) {
        SDKLog.a("Start => uid: " + uid + ", token: " + Utils.dataMasking(token) + ", type: " + type);
        if (!Constant.ishandshake) {
            this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
            SDKLog.a("End <= ");
        } else if (this.schedulerOwner != null && !TextUtils.isEmpty(this.schedulerID) && !TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token) && type != null && type != GizSchedulerType.GizSchedulerDelay) {
            JSONArray days_json = null;
            String repeat_json = null;
            String time_json = null;
            String date_json = null;
            int type_json = 0;
            String time_local = this.getTime();
            String date_local = this.getDate();
            boolean enabled_json = this.getEnabled();
            ConcurrentHashMap<String, Object> attrs = this.getAttrs();
            SDKLog.d("current scheduler info: " + this.infoMasking());
            if (type == GizSchedulerType.GizSchedulerOneTime) {
                if (TextUtils.isEmpty(time_local) || TextUtils.isEmpty(date_local) || attrs == null || attrs.size() == 0) {
                    SDKLog.d("time_local: " + time_local + ", date_local: " + date_local + ", attrs: " + attrs);
                    this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
                    SDKLog.a("End <= ");
                    return;
                }

                ConcurrentHashMap<String, String> utcDateTime = this.getUTCDateTimeByLocal(date_local, time_local);
                time_json = (String) utcDateTime.get("time");
                date_json = (String) utcDateTime.get("date");
                if (TextUtils.isEmpty(time_json) || TextUtils.isEmpty(date_json)) {
                    SDKLog.d("time_json: " + time_json + ", date_json: " + date_json);
                    this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
                    SDKLog.a("End <= ");
                    return;
                }

                type_json = 1;
                repeat_json = "none";
            } else {
                List weekdays_local;
                ConcurrentHashMap utcDateTime;
                int diffDay;
                ArrayList monthDays_utc;
                Iterator var17;
                if (type == GizSchedulerType.GizSchedulerWeekRepeat) {
                    weekdays_local = this.getWeekdays();
                    if (TextUtils.isEmpty(time_local) || weekdays_local == null || weekdays_local.size() == 0 || attrs == null || attrs.size() == 0) {
                        SDKLog.d("time_local: " + time_local + ", weekdays_local: " + weekdays_local + ", attrs: " + attrs);
                        this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
                        SDKLog.a("End <= ");
                        return;
                    }

                    utcDateTime = this.getUTCDateTimeByLocal(date_local, time_local);
                    time_json = (String) utcDateTime.get("time");
                    if (TextUtils.isEmpty(time_json)) {
                        SDKLog.d("time_json: " + time_json);
                        this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
                        SDKLog.a("End <= ");
                        return;
                    }

                    type_json = 2;
                    diffDay = this.getDiffDayByLocalAndUTC(time_local);
                    monthDays_utc = new ArrayList();
                    if (diffDay == 0) {
                        repeat_json = Utils.listToString(weekdays_local);
                    } else {
                        GizScheduleWeekday gizScheduleWeekday;
                        if (diffDay == 1) {
                            var17 = weekdays_local.iterator();

                            while (var17.hasNext()) {
                                gizScheduleWeekday = (GizScheduleWeekday) var17.next();
                                switch (gizScheduleWeekday.ordinal()) {
                                    case 0:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleMonday);
                                        break;
                                    case 1:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleTuesday);
                                        break;
                                    case 2:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleWednesday);
                                        break;
                                    case 3:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleThursday);
                                        break;
                                    case 4:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleFriday);
                                        break;
                                    case 5:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleSaturday);
                                        break;
                                    case 6:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleSunday);
                                }
                            }

                            repeat_json = Utils.listToString(monthDays_utc);
                        } else if (diffDay == -1) {
                            var17 = weekdays_local.iterator();

                            while (var17.hasNext()) {
                                gizScheduleWeekday = (GizScheduleWeekday) var17.next();
                                switch (gizScheduleWeekday.ordinal()) {
                                    case 0:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleSaturday);
                                        break;
                                    case 1:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleSunday);
                                        break;
                                    case 2:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleMonday);
                                        break;
                                    case 3:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleTuesday);
                                        break;
                                    case 4:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleWednesday);
                                        break;
                                    case 5:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleThursday);
                                        break;
                                    case 6:
                                        monthDays_utc.add(GizScheduleWeekday.GizScheduleFriday);
                                }
                            }

                            repeat_json = Utils.listToString(monthDays_utc);
                        }
                    }
                } else {
                    if (type != GizSchedulerType.GizSchedulerDayRepeat) {
                        SDKLog.d("type is illegal: " + type);
                        this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
                        SDKLog.d("End <= ");
                        return;
                    }

                    weekdays_local = this.getMonthDays();
                    if (TextUtils.isEmpty(time_local) || weekdays_local == null || weekdays_local.size() == 0 || attrs == null || attrs.size() == 0) {
                        SDKLog.d("time_local: " + time_local + ", monthDays_local: " + weekdays_local + ", attrs: " + attrs);
                        this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
                        SDKLog.d("End <= ");
                        return;
                    }

                    utcDateTime = this.getUTCDateTimeByLocal(date_local, time_local);
                    time_json = (String) utcDateTime.get("time");
                    if (TextUtils.isEmpty(time_json)) {
                        SDKLog.d("time_json: " + time_json);
                        this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
                        SDKLog.a("End <= ");
                        return;
                    }

                    type_json = 3;
                    repeat_json = "day";
                    diffDay = this.getDiffDayByLocalAndUTC(time_local);
                    monthDays_utc = new ArrayList();
                    var17 = weekdays_local.iterator();

                    while (var17.hasNext()) {
                        int day = (Integer) var17.next();
                        if (1 <= day + diffDay && day + diffDay <= 31) {
                            int myint = day + diffDay;
                            if (myint > 0 && myint < 32 && !monthDays_utc.contains(myint) && (diffDay != -1 || myint != 31)) {
                                monthDays_utc.add(myint);
                            }
                        }

                        byte er;
                        if (day + diffDay == 0 && diffDay == -1) {
                            er = 31;
                            if (er > 0 && er < 32 && !monthDays_utc.contains(Integer.valueOf(er))) {
                                monthDays_utc.add(Integer.valueOf(er));
                            }
                        }

                        if (day + diffDay == 32 && diffDay == 1) {
                            er = 1;
                            if (er > 0 && er < 32 && !monthDays_utc.contains(Integer.valueOf(er))) {
                                monthDays_utc.add(Integer.valueOf(er));
                            }
                        }
                    }

                    days_json = new JSONArray();

                    for (int i = 0; i < monthDays_utc.size(); ++i) {
                        days_json.put(monthDays_utc.get(i));
                    }
                }
            }

            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1099);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("mac", this.schedulerOwner.getMacAddress());
                obj.put("productKey", this.schedulerOwner.getProductKey());
                obj.put("did", this.schedulerOwner.getDid());
                obj.put("schedulerID", this.getSchedulerID());
                JSONObject scheduler = new JSONObject();
                scheduler.put("schedulerType", type_json);
                scheduler.put("enabled", enabled_json);
                scheduler.put("date", date_json);
                scheduler.put("time", time_json);
                scheduler.put("repeat", repeat_json);
                scheduler.put("days", days_json);
                scheduler.put("remark", this.remark);
                scheduler.put("start_date", this.startDate);
                scheduler.put("end_date", this.endDate);
                scheduler.put("attrs", Utils.getTaskJsonObject(attrs));
                obj.put("scheduler", scheduler);
            } catch (JSONException var20) {
                SDKLog.e(var20.toString());
                var20.printStackTrace();
            }

            sendMes2Demo(obj);
            this.makeTimer(31000, 1100, sn);
            SDKLog.a("End <= ");
        } else {
            SDKLog.d("schedulerOwner: " + this.schedulerOwner + ", schedulerID: " + this.schedulerID);
            this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
            SDKLog.a("End <= ");
        }
    }

    private void OnDidUpdateSchedulerInfo(GizDeviceScheduler scheduler, GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + scheduler.infoMasking());
        if (this.mListener != null) {
            this.mListener.didUpdateSchedulerInfo(scheduler, result);
            SDKLog.d("Callback end");
        }

    }

    private static void sendMes2Demo(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    private void makeTimer(int timeout, int cmd, int sn) {
        Message mes = Message.obtain();
        mes.what = sn;
        mes.obj = cmd;
        this.timeoutHandler.sendMessageDelayed(mes, (long) timeout);
    }

    protected void handleTimeoutMessage(Message msg) {
        int cmd = (Integer) msg.obj;
        switch (cmd) {
            case 1100:
                this.timeoutHandler.removeMessages(msg.what);
                this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT);
            default:
        }
    }

    protected void handleReceiveMessage(int cmd, JSONObject obj, int sn) {
        try {
            switch (cmd) {
                case 1100:
                    int errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                    if (this.timeoutHandler.hasMessages(sn)) {
                        this.timeoutHandler.removeMessages(sn);
                    }

                    this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.valueOf(errorCode));
            }
        } catch (JSONException var5) {
            var5.printStackTrace();
            SDKLog.e(var5.toString());
        }

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeParcelable(this.schedulerOwner, 1);
        if (this.schedulerOwner == null) {
            dest.writeString(this.getSchedulerID());
            dest.writeString(this.getCreatedDateTime());
            dest.writeString(this.getDate());
            dest.writeString(this.getTime());
            dest.writeString(this.getRemark());
            dest.writeString(this.getStartDate());
            dest.writeString(this.getEndDate());
            dest.writeInt(this.getEnabled() ? 1 : 0);
            HashMap<String, Object> map = new HashMap();
            if (this.getAttrs() != null) {
                Iterator var4 = this.getAttrs().keySet().iterator();

                while (var4.hasNext()) {
                    String key = (String) var4.next();
                    map.put(key, this.getAttrs().get(key));
                }
            }

            dest.writeMap(map);
            dest.writeList(this.getMonthDays());
            dest.writeList(this.getWeekdays());
            if (this.getSchedulerType() != null) {
                dest.writeInt(this.getSchedulerType().ordinal());
            } else {
                dest.writeInt(0);
            }
        } else {
            dest.writeString(this.getSchedulerID());
        }

    }
}
