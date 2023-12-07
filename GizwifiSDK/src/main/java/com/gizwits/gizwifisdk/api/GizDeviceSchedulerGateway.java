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
import com.gizwits.gizwifisdk.enumration.GizScheduleWeekday;
import com.gizwits.gizwifisdk.enumration.GizSchedulerType;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceSchedulerGatewayListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceSchedulerGateway extends GizDeviceSchedulerSuper implements Parcelable {
    private static List<ConcurrentHashMap<String, Integer>> messageQueue = new ArrayList();
    private List<GizDeviceSchedulerTask> taskList;
    private static final int MSG_RECV = 5;
    private GizDeviceSchedulerGatewayListener mListener;
    private String name;
    private int delayTime;
    Handler timeoutHandler;
    Handler messageHandler;
    public static final Creator<GizDeviceSchedulerGateway> CREATOR = new Creator<GizDeviceSchedulerGateway>() {
        public GizDeviceSchedulerGateway createFromParcel(Parcel source) {
            GizWifiDevice device = (GizWifiDevice)source.readParcelable(GizWifiDevice.class.getClassLoader());
            if (device == null) {
                int readInt = source.readInt();
                GizDeviceSchedulerGateway way = null;
                String time;
                String name;
                boolean b;
                switch(readInt) {
                    case 0:
                        int delayTime = source.readInt();
                        time = source.readString();
                        way = new GizDeviceSchedulerGateway(delayTime, time);
                        break;
                    case 1:
                        String date = source.readString();
                        time = source.readString();
                        name = source.readString();
                        b = source.readInt() == 1;
                        way = new GizDeviceSchedulerGateway(date, time, b, name);
                        break;
                    case 2:
                        time = source.readString();
                        name = source.readString();
                        b = source.readInt() == 1;
                        List<GizScheduleWeekday> weekdays = new ArrayList();
                        source.readList(weekdays, GizScheduleWeekday.class.getClassLoader());
                        way = new GizDeviceSchedulerGateway(time, weekdays, b, name);
                        break;
                    case 3:
                        time = source.readString();
                        name = source.readString();
                        b = source.readInt() == 1;
                        List<Integer> monthDays = new ArrayList();
                        source.readList(monthDays, Integer.class.getClassLoader());
                        way = new GizDeviceSchedulerGateway(time, monthDays, name, b);
                }

                return way;
            } else {
                String schedulerid = source.readString();
                ConcurrentHashMap<GizWifiDevice, List<GizDeviceSchedulerSuper>> schedulerList = GizDeviceSchedulerCenter.getSchedulerList();
                List<GizDeviceSchedulerSuper> list = (List)schedulerList.get(device);
                GizDeviceSchedulerGateway scd = null;
                if (list != null) {
                    Iterator var7 = list.iterator();

                    while(var7.hasNext()) {
                        GizDeviceSchedulerSuper gizDeviceScheduler = (GizDeviceSchedulerSuper)var7.next();
                        if (gizDeviceScheduler.getSchedulerID().equals(schedulerid)) {
                            scd = (GizDeviceSchedulerGateway)gizDeviceScheduler;
                        }
                    }
                }

                return scd;
            }
        }

        public GizDeviceSchedulerGateway[] newArray(int size) {
            return null;
        }
    };

    public String toString() {
        String log = "GizDeviceScheduler [" + super.toString();
        return log + ", name=" + this.name + ", delayTime=" + this.delayTime + ", taskList=" + this.taskList + ", mListener=" + this.mListener + "]";
    }

    protected String simpleInfoMasking() {
        return "GizDeviceScheduler [" + super.infoMasking() + ", listener=" + this.mListener + "]";
    }

    protected String infoMasking() {
        String info = this.simpleInfoMasking();
        return info + "->[enabled= " + this.getEnabled() + ", name=" + this.name + ", taskList=" + listMasking(this.taskList) + "]";
    }

    protected static String listMasking(List<GizDeviceSchedulerTask> list) {
        String masking = "{size= " + (list == null ? "0" : list.size()) + ", ";
        if (list != null) {
            for(Iterator var2 = list.iterator(); var2.hasNext(); masking = masking + ", ") {
                GizDeviceSchedulerTask object = (GizDeviceSchedulerTask)var2.next();
                masking = masking + "[" + object.infoMasking() + "]";
            }
        }

        return masking.substring(0, masking.length() - 2) + "}";
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDelayTime() {
        return this.delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public List<GizDeviceSchedulerTask> getTaskList() {
        return this.taskList;
    }

    protected void setTaskList(List<GizDeviceSchedulerTask> taskList) {
        this.taskList = taskList;
    }

    public GizDeviceSchedulerGateway(int delayTime, String name) {
        this.timeoutHandler = new NamelessClass_2(Looper.getMainLooper());
        this.messageHandler = new NamelessClass_1(Looper.getMainLooper());
        this.setIsValid(true);
        this.setSchedulerType(GizSchedulerType.GizSchedulerDelay);
        this.setDelayTime(delayTime);
        if (name != null) {
            this.setName(name);
        }

        if (this.taskList == null) {
            this.taskList = new ArrayList();
        }

    }

    public GizDeviceSchedulerGateway(String date, String time, boolean enabled, String name) {
        this.timeoutHandler = new NamelessClass_2(Looper.getMainLooper());
        this.messageHandler = new NamelessClass_1(Looper.getMainLooper());
        this.setIsValid(true);
        this.setSchedulerType(GizSchedulerType.GizSchedulerOneTime);
        this.setDate(date);
        this.setTime(time);
        this.setEnabled(enabled);
        if (name != null) {
            this.setName(name);
        }

        if (this.taskList == null) {
            this.taskList = new ArrayList();
        }

    }

    public GizDeviceSchedulerGateway(String time, List<GizScheduleWeekday> weekDays, boolean enabled, String name) {
        this.timeoutHandler = new NamelessClass_2(Looper.getMainLooper());
        this.messageHandler = new NamelessClass_1(Looper.getMainLooper());
        this.setIsValid(true);
        this.setSchedulerType(GizSchedulerType.GizSchedulerWeekRepeat);
        this.setTime(time);
        this.setWeekdays(weekDays);
        this.setEnabled(enabled);
        if (name != null) {
            this.setName(name);
        }

        if (this.taskList == null) {
            this.taskList = new ArrayList();
        }

    }

    class NamelessClass_2 extends Handler {
        NamelessClass_2(Looper x0) {
            super(x0);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GizDeviceSchedulerGateway.this.handleTimeoutMessage(msg);
        }
    }

    class NamelessClass_1 extends Handler {
        NamelessClass_1(Looper x0) {
            super(x0);
        }

        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 5:
                    try {
                        String jsonStr = (String)msg.obj;
                        JSONObject obj = new JSONObject(jsonStr);
                        int cmd = obj.has("cmd") ? Integer.parseInt(obj.getString("cmd")) : 0;
                        int sn = obj.has("sn") ? Integer.parseInt(obj.getString("sn")) : 0;
                        String did = obj.has("did") ? obj.getString("did") : "";
                        String mac = obj.has("mac") ? obj.getString("mac") : "";
                        String productKey = obj.has("productKey") ? obj.getString("productKey") : "";
                        if (!GizDeviceSchedulerGateway.this.schedulerOwner.equals(mac, productKey, did)) {
                            SDKLog.d("<mac: " + mac + ", productKey: " + productKey + ", did: " + did + ">is not the owner");
                        }

                        if (cmd > 2000) {
                            sn = 0;
                        }

                        GizDeviceSchedulerGateway.this.handleReceiveMessage(cmd, obj, sn);
                    } catch (NumberFormatException var9) {
                        var9.printStackTrace();
                    } catch (JSONException var10) {
                        var10.printStackTrace();
                    }
                default:
            }
        }
    }

    public GizDeviceSchedulerGateway(String time, List<Integer> monthDays, String name, boolean enabled) {
        this.timeoutHandler = new NamelessClass_2(Looper.getMainLooper());
        this.messageHandler = new NamelessClass_1(Looper.getMainLooper());
        this.setIsValid(true);
        this.setSchedulerType(GizSchedulerType.GizSchedulerDayRepeat);
        this.setTime(time);
        this.setMonthDays(monthDays);
        this.setEnabled(enabled);
        if (name != null) {
            this.setName(name);
        }

        if (this.taskList == null) {
            this.taskList = new ArrayList();
        }

    }

    public void setListener(GizDeviceSchedulerGatewayListener listener) {
        this.mListener = listener;
    }

    public void editSchedulerTasks(List<GizDeviceSchedulerTask> tasks) {
        SDKLog.a("Start => ");
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            this.OnDidUpdateSchedulerTasks(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.a("End <= ");
        } else if (this.schedulerOwner == null) {
            list = new ArrayList();
            this.OnDidUpdateSchedulerTasks(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1251);
                obj.put("sn", sn);
                obj.put("mac", this.schedulerOwner.getMacAddress());
                obj.put("did", this.schedulerOwner.getDid());
                obj.put("productKey", this.schedulerOwner.getProductKey());
                obj.put("schedulerID", this.schedulerID);
                String task = Utils.schedulerTasksToString(tasks);
                if (!TextUtils.isEmpty(task)) {
                    JSONArray ay = new JSONArray(task);
                    obj.put("tasks", ay);
                }
            } catch (JSONException var6) {
                var6.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (this.schedulerOwner.isLAN) {
                this.addMessage(this.timeoutHandler, 9000, 1252, sn, 0);
            } else {
                this.addMessage(this.timeoutHandler, 20000, 1252, sn, 0);
            }

            SDKLog.a("End <= ");
        }
    }

    public void editSchedulerInfo(GizSchedulerType type) {
        SDKLog.a("Start => , type: " + type);
        if (!Constant.ishandshake) {
            this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
            SDKLog.a("End <= ");
        } else if (this.schedulerOwner != null && !TextUtils.isEmpty(this.schedulerID) && type != null) {
            JSONArray days_json = null;
            String repeat_json = null;
            String time_json = null;
            String date_json = null;
            int type_json = 0;
            String time_local = this.time;
            String date_local = this.date;
            SDKLog.d("current scheduler info: " + this.infoMasking());
            if (type == GizSchedulerType.GizSchedulerDelay) {
                type_json = 0;
            } else if (type == GizSchedulerType.GizSchedulerOneTime) {
                label194: {
                    if (!TextUtils.isEmpty(time_local) && !TextUtils.isEmpty(date_local)) {
                        ConcurrentHashMap<String, String> utcDateTime = this.getUTCDateTimeByLocal(date_local, time_local);
                        time_json = (String)utcDateTime.get("time");
                        date_json = (String)utcDateTime.get("date");
                        if (!TextUtils.isEmpty(time_json) && !TextUtils.isEmpty(date_json)) {
                            type_json = 1;
                            repeat_json = "none";
                            break label194;
                        }

                        SDKLog.d("time_json: " + time_json + ", date_json: " + date_json);
                        this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
                        SDKLog.a("End <= ");
                        return;
                    }

                    SDKLog.d("time_local: " + time_local + ", date_local: " + date_local);
                    this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
                    SDKLog.a("End <= ");
                    return;
                }
            } else {
                List weekdays_local;
                ConcurrentHashMap utcDateTime;
                int diffDay;
                ArrayList monthDays_utc;
                Iterator var13;
                if (type == GizSchedulerType.GizSchedulerWeekRepeat) {
                    weekdays_local = this.weekdays;
                    if (TextUtils.isEmpty(time_local) || weekdays_local == null || weekdays_local.size() == 0) {
                        SDKLog.d("time_local: " + time_local + ", weekdays_local: " + weekdays_local);
                        this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
                        SDKLog.a("End <= ");
                        return;
                    }

                    utcDateTime = this.getUTCDateTimeByLocal(date_local, time_local);
                    time_json = (String)utcDateTime.get("time");
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
                            var13 = weekdays_local.iterator();

                            while(var13.hasNext()) {
                                gizScheduleWeekday = (GizScheduleWeekday)var13.next();
                                switch(gizScheduleWeekday.ordinal()) {
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
                            var13 = weekdays_local.iterator();

                            while(var13.hasNext()) {
                                gizScheduleWeekday = (GizScheduleWeekday)var13.next();
                                switch(gizScheduleWeekday.ordinal()) {
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

                    weekdays_local = this.monthDays;
                    if (TextUtils.isEmpty(time_local) || weekdays_local == null || weekdays_local.size() == 0) {
                        SDKLog.d("time_local: " + time_local + ", monthDays_local: " + weekdays_local);
                        this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
                        SDKLog.d("End <= ");
                        return;
                    }

                    utcDateTime = this.getUTCDateTimeByLocal(date_local, time_local);
                    time_json = (String)utcDateTime.get("time");
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
                    var13 = weekdays_local.iterator();

                    while(var13.hasNext()) {
                        int day = (Integer)var13.next();
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

                    for(int i = 0; i < monthDays_utc.size(); ++i) {
                        days_json.put(monthDays_utc.get(i));
                    }
                }
            }

            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1099);
                obj.put("sn", sn);
                obj.put("mac", this.schedulerOwner.getMacAddress());
                obj.put("productKey", this.schedulerOwner.getProductKey());
                obj.put("did", this.schedulerOwner.getDid());
                obj.put("schedulerID", this.getSchedulerID());
                JSONObject scheduler = new JSONObject();
                scheduler.put("schedulerType", type_json);
                scheduler.put("enabled", this.getEnabled());
                scheduler.put("delay", this.delayTime);
                scheduler.put("date", date_json);
                scheduler.put("time", time_json);
                scheduler.put("repeat", repeat_json);
                scheduler.put("days", days_json);
                scheduler.put("schedulerName", this.name);
                obj.put("scheduler", scheduler);
            } catch (JSONException var16) {
                SDKLog.e(var16.toString());
                var16.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (this.schedulerOwner.isLAN) {
                this.addMessage(this.timeoutHandler, 9000, 1100, sn, 0);
            } else {
                this.addMessage(this.timeoutHandler, 20000, 1100, sn, 0);
            }

            SDKLog.a("End <= ");
        } else {
            SDKLog.d("schedulerOwner: " + this.schedulerOwner + ", type: " + type);
            this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
            SDKLog.a("End <= ");
        }
    }

    public void updateSchedulerEnableStatus() {
        SDKLog.a("Start => ");
        if (!Constant.ishandshake) {
            this.OnDidUpdateSceneStatus(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, false);
            SDKLog.a("End <= ");
        } else if (this.schedulerOwner == null) {
            this.OnDidUpdateSceneStatus(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, false);
            SDKLog.a("End <= ");
        } else {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1257);
                obj.put("sn", sn);
                obj.put("mac", this.schedulerOwner.getMacAddress());
                obj.put("did", this.schedulerOwner.getDid());
                obj.put("schedulerID", this.getSchedulerID());
                obj.put("productKey", this.schedulerOwner.getProductKey());
            } catch (JSONException var4) {
                var4.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (this.schedulerOwner.isLAN) {
                this.addMessage(this.timeoutHandler, 9000, 1258, sn, 0);
            } else {
                this.addMessage(this.timeoutHandler, 20000, 1258, sn, 0);
            }

            SDKLog.a("End <= ");
        }
    }

    public void enableScheduler(boolean enable, int sn) {
        SDKLog.a("Start => ");
        if (!Constant.ishandshake) {
            this.OnDidEnableScheduler(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, sn);
            SDKLog.a("End <= ");
        } else if (this.schedulerOwner == null) {
            this.OnDidEnableScheduler(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, sn);
            SDKLog.a("End <= ");
        } else {
            int sdkSn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                this.setEnabled(enable);
                obj.put("cmd", 1255);
                obj.put("sn", sdkSn);
                obj.put("mac", this.schedulerOwner.getMacAddress());
                obj.put("productKey", this.schedulerOwner.getProductKey());
                obj.put("did", this.schedulerOwner.getDid());
                obj.put("schedulerID", this.getSchedulerID());
                obj.put("enabled", this.getEnabled());
            } catch (JSONException var6) {
                var6.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (this.schedulerOwner.isLAN) {
                this.addMessage(this.timeoutHandler, 9000, 1256, sn, 0);
            } else {
                this.addMessage(this.timeoutHandler, 20000, 1256, sn, 0);
            }

            SDKLog.a("End <= ");
        }
    }

    public void updateSchedulerTasks() {
        SDKLog.a("Start => ");
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            this.OnDidUpdateSchedulerTasks(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.a("End <= ");
        } else if (this.schedulerOwner == null) {
            list = new ArrayList();
            this.OnDidUpdateSchedulerTasks(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1253);
                obj.put("sn", sn);
                obj.put("mac", this.schedulerOwner.getMacAddress());
                obj.put("did", this.schedulerOwner.getDid());
                obj.put("schedulerID", this.schedulerID);
            } catch (JSONException var4) {
                var4.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (this.schedulerOwner.isLAN) {
                this.addMessage(this.timeoutHandler, 9000, 1254, sn, 0);
            } else {
                this.addMessage(this.timeoutHandler, 20000, 1254, sn, 0);
            }

            SDKLog.a("End <= ");
        }
    }

    private void OnDidUpdateSchedulerInfo(GizDeviceSchedulerGateway scheduler, GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", scheduler: " + scheduler.infoMasking());
        if (this.mListener != null) {
            this.mListener.didUpdateSchedulerInfo(scheduler, result);
            SDKLog.d("Callback end");
        }

    }

    private void OnDidUpdateSchedulerTasks(GizDeviceSchedulerGateway scheduler, GizWifiErrorCode result, List<GizDeviceSchedulerTask> taskList) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", taskList: " + listMasking(taskList));
        if (this.mListener != null) {
            this.mListener.didUpdateSchedulerTasks(scheduler, result, taskList);
            SDKLog.d("Callback end");
        }

    }

    private void OnDidEnableScheduler(GizDeviceSchedulerGateway scheduler, GizWifiErrorCode result, int sn) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, scheduler" + scheduler.infoMasking() + "result: " + result.name() + ", sn: " + sn);
        if (this.mListener != null) {
            this.mListener.didEnableScheduler(scheduler, result, sn);
            SDKLog.d("Callback end");
        }

    }

    private void OnDidUpdateSceneStatus(GizDeviceSchedulerGateway scheduler, GizWifiErrorCode result, boolean enableStatus) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", enableStatus: " + enableStatus);
        if (this.mListener != null) {
            this.mListener.didUpdateSceneStatus(scheduler, result, enableStatus);
            SDKLog.d("Callback end");
        }

    }

    private static void sendMessageToDaemon(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    protected void handleTimeoutMessage(Message msg) {
        int sdk_sn = msg.what;
        int cmd = (Integer)msg.obj;
        ArrayList list;
        switch(cmd) {
            case 1100:
                this.removeMessage(this.timeoutHandler, cmd, sdk_sn);
                this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT);
                break;
            case 1252:
                this.removeMessage(this.timeoutHandler, cmd, sdk_sn);
                list = new ArrayList();
                this.OnDidUpdateSchedulerTasks(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1254:
                this.removeMessage(this.timeoutHandler, cmd, sdk_sn);
                list = new ArrayList();
                this.OnDidUpdateSchedulerTasks(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1256:
                int app_sn = this.removeMessage(this.timeoutHandler, cmd, sdk_sn);
                this.OnDidEnableScheduler(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, app_sn);
                break;
            case 1258:
                this.removeMessage(this.timeoutHandler, cmd, sdk_sn);
                this.OnDidUpdateSceneStatus(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, this.getEnabled());
        }

    }

    protected void handleReceiveMessage(int cmd, JSONObject obj, int sn) throws JSONException {
        int appSn;
        switch(cmd) {
            case 1100:
                appSn = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (appSn != 0) {
                    this.removeMessage(this.timeoutHandler, cmd, sn);
                    this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.valueOf(appSn));
                }
                break;
            case 1252:
                appSn = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (appSn != 0) {
                    this.removeMessage(this.timeoutHandler, cmd, sn);
                    this.OnDidUpdateSchedulerTasks(this, GizWifiErrorCode.valueOf(appSn), this.taskList);
                }
                break;
            case 1254:
                appSn = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                this.removeMessage(this.timeoutHandler, cmd, sn);
                if (obj.has("tasks")) {
                    JSONArray jsonArray = obj.getJSONArray("tasks");
                    this.saveSchedulerTasks(this.schedulerOwner, jsonArray);
                }

                this.OnDidUpdateSchedulerTasks(this, GizWifiErrorCode.valueOf(appSn), this.taskList);
                break;
            case 1256:
                appSn = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                 appSn = this.removeMessage(this.timeoutHandler, cmd, sn);
                if (appSn != GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                    this.setEnabled(!this.getEnabled());
                }

                this.OnDidEnableScheduler(this, GizWifiErrorCode.valueOf(appSn), appSn);
                break;
            case 1258:
                appSn = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                this.removeMessage(this.timeoutHandler, cmd, sn);
                if (obj.has("enabled")) {
                    this.setEnabled(obj.getBoolean("enabled"));
                }

                this.OnDidUpdateSceneStatus(this, GizWifiErrorCode.valueOf(appSn), this.getEnabled());
                break;
            case 2027:
                this.removeMessage(this.timeoutHandler, cmd, 0);
                if (obj.has("tasks")) {
                    JSONArray jsonArray = obj.getJSONArray("tasks");
                    this.saveSchedulerTasks(this.schedulerOwner, jsonArray);
                }

                this.OnDidUpdateSchedulerTasks(this, GizWifiErrorCode.GIZ_SDK_SUCCESS, this.taskList);
                break;
            case 2028:
                appSn = this.removeMessage(this.timeoutHandler, cmd, 0);
                if (obj.has("enabled")) {
                    this.setEnabled(obj.getBoolean("enabled"));
                }

                this.OnDidEnableScheduler(this, GizWifiErrorCode.GIZ_SDK_SUCCESS, appSn);
                break;
            case 2033:
                this.removeMessage(this.timeoutHandler, cmd, 0);
                this.saveSchedulerInfo(obj);
                this.OnDidUpdateSchedulerInfo(this, GizWifiErrorCode.GIZ_SDK_SUCCESS);
        }

    }

    private void addMessage(Handler handle, int timeout, int cmd, int sdk_sn, int app_sn) {
        Message msg = handle.obtainMessage();
        msg.what = sdk_sn;
        msg.obj = cmd;
        handle.sendMessageDelayed(msg, (long)timeout);
        this.addSnQueue(messageQueue, cmd, sdk_sn, app_sn);
        SDKLog.d("add the message: <cmd: " + cmd + ", sn: " + sdk_sn + ", app_sn: " + app_sn + ">");
    }

    private int removeMessage(Handler handle, int cmd, int sdk_sn) {
        Iterator var4;
        ConcurrentHashMap sn;
        if (cmd == 2028) {
            var4 = messageQueue.iterator();

            while(var4.hasNext()) {
                sn = (ConcurrentHashMap)var4.next();
                if ((Integer)sn.get("cmd") == 1256) {
                    cmd = 1256;
                    sdk_sn = (Integer)sn.get("sdkSn");
                    break;
                }
            }
        } else if (cmd == 2027) {
            var4 = messageQueue.iterator();

            while(var4.hasNext()) {
                sn = (ConcurrentHashMap)var4.next();
                if ((Integer)sn.get("cmd") == 1252) {
                    cmd = 1252;
                    sdk_sn = (Integer)sn.get("sdkSn");
                    break;
                }
            }
        } else if (cmd == 2033) {
            var4 = messageQueue.iterator();

            while(var4.hasNext()) {
                sn = (ConcurrentHashMap)var4.next();
                if ((Integer)sn.get("cmd") == 1100) {
                    cmd = 1100;
                    sdk_sn = (Integer)sn.get("sdkSn");
                    break;
                }
            }
        }

        Message msg = handle.hasMessages(sdk_sn) ? handle.obtainMessage(sdk_sn) : null;
        if (sdk_sn != 0 && msg != null) {
            handle.removeMessages(sdk_sn);
            SDKLog.d("removed the message: <cmd: " + cmd + ", sn: " + sdk_sn + ">");
        } else {
            SDKLog.d("did not remove, can not find message: <cmd: " + cmd + ", sn: " + sdk_sn + ">");
        }

        return this.removeSn(messageQueue, cmd, sdk_sn);
    }

    void addSnQueue(List<ConcurrentHashMap<String, Integer>> queue, int cmd, int sdkSn, int appSn) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap();
        map.put("cmd", cmd);
        map.put("appSn", appSn);
        map.put("sdkSn", sdkSn);
        queue.add(map);
    }

    int removeSn(List<ConcurrentHashMap<String, Integer>> queue, int cmd, int sn) {
        int appSn = 0;
        ConcurrentHashMap<String, Integer> snToRemove = null;
        Iterator var6 = queue.iterator();

        while(var6.hasNext()) {
            ConcurrentHashMap<String, Integer> snqueue = (ConcurrentHashMap)var6.next();
            if (sn == (Integer)snqueue.get("sdkSn")) {
                snToRemove = snqueue;
                break;
            }
        }

        if (snToRemove != null) {
            appSn = (Integer)snToRemove.get("appSn");
            queue.remove(snToRemove);
        }

        return appSn;
    }

    protected void saveSchedulerInfo(JSONObject obj) {
        if (obj != null) {
            try {
                JSONObject schedulerInfoJson = obj.has("schedulerInfo") ? obj.getJSONObject("schedulerInfo") : null;
                if (schedulerInfoJson == null) {
                    SDKLog.d("There is no schedulerInfo, cann't saved it!");
                    return;
                }

                int schedulerType_json = schedulerInfoJson.has("schedulerType") ? schedulerInfoJson.getInt("schedulerType") : -1;
                String schedulerName_json = schedulerInfoJson.has("schedulerName") ? schedulerInfoJson.getString("schedulerName") : "";
                boolean enabled_json = schedulerInfoJson.has("enabled") ? schedulerInfoJson.getBoolean("enabled") : false;
                int delay_json = schedulerInfoJson.has("delay") ? schedulerInfoJson.getInt("delay") : -1;
                String date_json = schedulerInfoJson.has("date") ? schedulerInfoJson.getString("date") : "";
                String time_json = schedulerInfoJson.has("time") ? schedulerInfoJson.getString("time") : "";
                String repeat_json = schedulerInfoJson.has("repeat") ? schedulerInfoJson.getString("repeat") : null;
                JSONArray days_json = schedulerInfoJson.has("days") ? schedulerInfoJson.getJSONArray("days") : null;
                if (schedulerType_json == -1) {
                    SDKLog.d("There is no schedulerType, cann't saved it!");
                    return;
                }

                this.setName(schedulerName_json);
                this.setEnabled(enabled_json);
                if (schedulerType_json == 0) {
                    this.setSchedulerType(GizSchedulerType.GizSchedulerDelay);
                    this.setDelayTime(delay_json);
                } else {
                    ConcurrentHashMap dateTime;
                    if (schedulerType_json == 1) {
                        this.setSchedulerType(GizSchedulerType.GizSchedulerOneTime);
                        dateTime = this.getLocalDateTimeByUTC(date_json, time_json);
                        this.setDate((String)dateTime.get("date"));
                        this.setTime((String)dateTime.get("time"));
                    } else {
                        int diffDay;
                        if (schedulerType_json == 2) {
                            this.setSchedulerType(GizSchedulerType.GizSchedulerWeekRepeat);
                            if (repeat_json != null) {
                                dateTime = this.getLocalDateTimeByUTC(date_json, time_json);
                                this.setTime((String)dateTime.get("time"));
                                diffDay = this.getDiffDayByLocalAndUTC(this.getTime());
                                List<GizScheduleWeekday> repeatWeekDays = Utils.getRepeatType(repeat_json);
                                List<GizScheduleWeekday> localWeekDays = new ArrayList();
                                Iterator var15;
                                GizScheduleWeekday gizScheduleWeekday;
                                if (diffDay == 1) {
                                    var15 = repeatWeekDays.iterator();

                                    while(var15.hasNext()) {
                                        gizScheduleWeekday = (GizScheduleWeekday)var15.next();
                                        switch(gizScheduleWeekday.ordinal()) {
                                            case 0:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleMonday);
                                                break;
                                            case 1:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleTuesday);
                                                break;
                                            case 2:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleWednesday);
                                                break;
                                            case 3:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleThursday);
                                                break;
                                            case 4:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleFriday);
                                                break;
                                            case 5:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleSaturday);
                                                break;
                                            case 6:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleSunday);
                                        }
                                    }

                                    this.setWeekdays(localWeekDays);
                                } else if (diffDay == -1) {
                                    var15 = repeatWeekDays.iterator();

                                    while(var15.hasNext()) {
                                        gizScheduleWeekday = (GizScheduleWeekday)var15.next();
                                        switch(gizScheduleWeekday.ordinal()) {
                                            case 0:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleSaturday);
                                                break;
                                            case 1:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleSunday);
                                                break;
                                            case 2:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleMonday);
                                                break;
                                            case 3:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleTuesday);
                                                break;
                                            case 4:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleWednesday);
                                                break;
                                            case 5:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleThursday);
                                                break;
                                            case 6:
                                                localWeekDays.add(GizScheduleWeekday.GizScheduleFriday);
                                        }
                                    }

                                    this.setWeekdays(localWeekDays);
                                } else {
                                    this.setWeekdays(repeatWeekDays);
                                }
                            } else {
                                SDKLog.d("There is no week days to repeat, cann't saved it!");
                            }
                        } else if (schedulerType_json == 3) {
                            this.setSchedulerType(GizSchedulerType.GizSchedulerDayRepeat);
                            if (days_json != null) {
                                dateTime = this.getLocalDateTimeByUTC(date_json, time_json);
                                this.setTime((String)dateTime.get("time"));
                                diffDay = this.getDiffDayByLocalAndUTC(this.time);
                                List<Integer> monthDays = new ArrayList();

                                for(int j = 0; j < days_json.length(); ++j) {
                                    int num = days_json.getInt(j) + diffDay;
                                    if (num >= 1 && num <= 31) {
                                        monthDays.add(num);
                                    } else if (0 == num) {
                                        monthDays.add(31);
                                    } else if (32 == num) {
                                        monthDays.add(1);
                                    }
                                }

                                this.setMonthDays(monthDays);
                            } else {
                                SDKLog.d("There is no month days to repeat, cann't saved it!");
                            }
                        } else {
                            SDKLog.d("SchedulerType is illegal, can not saved it!");
                        }
                    }
                }
            } catch (JSONException var17) {
                var17.printStackTrace();
            }

        }
    }

    protected boolean saveSchedulerTasks(GizWifiDevice owner, JSONArray jsonArray) {
        if (jsonArray == null) {
            SDKLog.d("Tasks json is null, can not synchronous");
            return false;
        } else if (owner != null && owner.getProductType() == GizWifiDeviceType.GizDeviceCenterControl) {
            if (owner.getMacAddress().equals(this.schedulerOwner.getMacAddress()) && owner.getDid().equals(this.schedulerOwner.getDid()) && owner.getProductKey().equals(this.schedulerOwner.getProductKey())) {
                boolean update = false;
                GizWifiCentralControlDevice schedulerOwner_json = (GizWifiCentralControlDevice)owner;
                ArrayList tempList = new ArrayList();

                try {
                    for(int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject mob = (JSONObject)jsonArray.get(i);
                        int schedulerTaskType = -1;
                        if (mob.has("schedulerTaskType")) {
                            schedulerTaskType = mob.getInt("schedulerTaskType");
                        }

                        GizWifiDevice device_json = null;
                        String groupID;
                        String sceneID;
                        if (mob.has("device")) {
                            JSONObject mdevice = mob.getJSONObject("device");
                            groupID = mdevice.getString("mac");
                            sceneID = mdevice.getString("did");
                            String productKey = mdevice.getString("productKey");
                            device_json = SDKEventManager.getInstance().findDeviceInTotalDeviceList(sceneID, groupID, productKey);
                        }

                        GizDeviceGroup group_json = null;
                        GizDeviceGroup data_json;
                        if (mob.has("groupID")) {
                            groupID = mob.getString("groupID");
                            List<GizDeviceGroup> list = GizDeviceGroupCenter.getTotalGroupListByOwner(schedulerOwner_json);
                            Iterator var23 = list.iterator();

                            while(var23.hasNext()) {
                                data_json = (GizDeviceGroup)var23.next();
                                if (groupID.equals(data_json.getGroupID())) {
                                    group_json = data_json;
                                    break;
                                }
                            }
                        }

                        GizDeviceScene scene_json = null;
                        if (mob.has("sceneID")) {
                            sceneID = mob.getString("sceneID");
                            List<GizDeviceScene> list = GizDeviceSceneCenter.getTotalSceneListByOwner(schedulerOwner_json);
                            Iterator var26 = list.iterator();

                            while(var26.hasNext()) {
                                GizDeviceScene gizDeviceScene = (GizDeviceScene)var26.next();
                                if (sceneID.equals(gizDeviceScene.getSceneID())) {
                                    scene_json = gizDeviceScene;
                                    break;
                                }
                            }
                        }

                        boolean sceneStatus = false;
                        if (mob.has("sceneStatus")) {
                            sceneStatus = mob.getBoolean("sceneStatus");
                        }

                        GizDeviceSchedulerTask item = null;
                        ConcurrentHashMap<String, Object> data_jsons = null;
                        switch(schedulerTaskType) {
                            case 0:
                                if (device_json == null) {
                                    SDKLog.d("schedulerTask device is null, ignore it");
                                } else {
                                    data_json = null;
                                    List<ConcurrentHashMap<String, Object>> maps_v4a1_v4a2 = device_json.parseDeviceStatusForKey(mob, "attrStatus");
                                    ConcurrentHashMap<String, Object> map_v4a2 = (ConcurrentHashMap)maps_v4a1_v4a2.get(1);
                                    data_jsons = (ConcurrentHashMap)map_v4a2.get("data");
                                    if (data_jsons != null && data_jsons.size() != 0) {
                                        item = new GizDeviceSchedulerTask(device_json, data_jsons);
                                        break;
                                    }

                                    SDKLog.d("schedulerTask data is empty, ignore it");
                                }
                                break;
                            case 1:
                                if (group_json == null) {
                                    SDKLog.d("schedulerTask group is null, ignore it");
                                } else {
                                    GizWifiDevice deviceByProductKey = SDKEventManager.getInstance().findDeviceByProductKeyInTotalDeviceList(group_json.getGroupType());
                                    if (deviceByProductKey != null) {
                                        List<ConcurrentHashMap<String, Object>> maps_v4a1_v4a2 = deviceByProductKey.parseDeviceStatusForKey(mob, "attrStatus");
                                        ConcurrentHashMap<String, Object> map_v4a2 = (ConcurrentHashMap)maps_v4a1_v4a2.get(1);
                                        data_jsons = (ConcurrentHashMap)map_v4a2.get("data");
                                    }

                                    if (data_jsons != null && data_jsons.size() != 0) {
                                        item = new GizDeviceSchedulerTask(group_json, data_jsons);
                                        break;
                                    }

                                    SDKLog.d("schedulerTask data is empty, ignore it");
                                }
                                break;
                            case 2:
                                item = new GizDeviceSchedulerTask(scene_json, sceneStatus);
                        }

                        if (item != null) {
                            tempList.add(item);
                        }
                    }

                    SDKLog.d("schedulerID: " + this.getSchedulerID() + ", tasks: " + listMasking(this.getTaskList()));
                } catch (JSONException var18) {
                    var18.printStackTrace();
                    SDKLog.e(var18.toString());
                }

                if (this.taskList.size() != tempList.size()) {
                    update = true;
                }

                this.taskList.clear();
                this.taskList.addAll(tempList);
                if (update) {
                    SDKLog.d("pomia =====> update: " + update);
                    this.OnDidUpdateSchedulerTasks(this, GizWifiErrorCode.GIZ_SDK_SUCCESS, this.taskList);
                } else {
                    SDKLog.d("pomia =====> update: " + update + "do not callback");
                }

                return update;
            } else {
                SDKLog.d("owner is not current schedulerOwner, ignore it");
                return false;
            }
        } else {
            SDKLog.d("owner is null, or not GizDeviceCenterControl, ignore it");
            return false;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeParcelable(this.schedulerOwner, 1);
        if (this.schedulerOwner == null) {
            dest.writeInt(this.schedulerType.ordinal());
            switch(this.schedulerType.ordinal()) {
                case 0:
                    dest.writeInt(this.delayTime);
                    dest.writeString(this.getName());
                    break;
                case 1:
                    dest.writeString(this.getDate());
                    dest.writeString(this.getTime());
                    dest.writeString(this.getName());
                    dest.writeInt(this.getEnabled() ? 1 : 0);
                    break;
                case 2:
                    dest.writeString(this.getTime());
                    dest.writeString(this.getName());
                    dest.writeInt(this.getEnabled() ? 1 : 0);
                    dest.writeList(this.getWeekdays());
                    break;
                case 3:
                    dest.writeString(this.getTime());
                    dest.writeString(this.getName());
                    dest.writeInt(this.getEnabled() ? 1 : 0);
                    dest.writeList(this.getMonthDays());
            }
        } else {
            dest.writeString(this.getSchedulerID());
        }

    }

}
