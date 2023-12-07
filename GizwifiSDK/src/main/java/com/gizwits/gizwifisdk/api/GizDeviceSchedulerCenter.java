//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.enumration.GizScheduleRepeatType;
import com.gizwits.gizwifisdk.enumration.GizScheduleWeekday;
import com.gizwits.gizwifisdk.enumration.GizSchedulerType;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceSchedulerCenterListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceSchedulerCenter {
    protected static ConcurrentHashMap<GizWifiDevice, List<GizDeviceSchedulerSuper>> schedulerListMap = new ConcurrentHashMap();
    protected static List<GizDeviceSchedulerSuper> myschedulerList = new ArrayList();
    private static List<Integer> messageQueue = new ArrayList();
    private static GizDeviceSchedulerCenterListener mListener;
    private static final int MSG_RECV = 5;
    private static GizWifiDevice createSchedulerRequestOwner = null;
    private static GizWifiDevice deleteSchedulerRequestOwner = null;
    private static GizWifiDevice updateSchedulersRequestOwner = null;
    private static GizWifiDevice editSchedulerRequestOwner = null;
    private static Handler timeoutHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            int cmd = (Integer)msg.obj;
            GizDeviceSchedulerCenter.handleTimeoutMessage(cmd, msg);
        }
    };
    protected static Handler messageHandler = new Handler(Looper.getMainLooper()) {
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
                        GizWifiDevice deviceOwner = SDKEventManager.getInstance().findDeviceInTotalDeviceList(did, mac, productKey);
                        if (deviceOwner == null) {
                            SDKLog.d("owner<mac: " + mac + ", productKey: " + productKey + ", did: " + did + ">is null");
                        }

                        if (cmd > 2000) {
                            sn = 0;
                        }

                        GizDeviceSchedulerCenter.handleReceiveMessage(cmd, obj, sn, deviceOwner);
                    } catch (NumberFormatException var10) {
                        var10.printStackTrace();
                    } catch (JSONException var11) {
                        var11.printStackTrace();
                    }
                default:
            }
        }
    };

    public GizDeviceSchedulerCenter() {
    }

    protected static String listMasking(List<?> list) {
        String masking = "{size= " + (list == null ? "0" : list.size()) + ", ";
        if (list != null) {
            for(int i = 0; i < list.size(); ++i) {
                Object objT = list.get(i);
                if (objT instanceof GizDeviceScheduler) {
                    GizDeviceScheduler object = (GizDeviceScheduler)objT;
                    masking = masking + "[" + object.infoMasking() + "]";
                    masking = masking + ", ";
                } else if (objT instanceof GizDeviceSchedulerGateway) {
                    GizDeviceSchedulerGateway object = (GizDeviceSchedulerGateway)objT;
                    masking = masking + "[" + object.infoMasking() + "]";
                    masking = masking + ", ";
                }
            }
        }

        return masking.substring(0, masking.length() - 2) + "}";
    }

    protected static ConcurrentHashMap<GizWifiDevice, List<GizDeviceSchedulerSuper>> getSchedulerList() {
        return schedulerListMap;
    }

    protected static List<GizDeviceSchedulerSuper> getAllSchedulerListByDevice(GizWifiDevice schedulerOwner) {
        List<GizDeviceSchedulerSuper> schedulerCache = new ArrayList();
        Iterator var2 = schedulerListMap.keySet().iterator();

        while(var2.hasNext()) {
            GizWifiDevice key = (GizWifiDevice)var2.next();
            if (schedulerOwner != null && key.getMacAddress().equals(schedulerOwner.getMacAddress()) && key.getDid().equals(schedulerOwner.getDid()) && key.getProductKey().equals(schedulerOwner.getProductKey())) {
                List<GizDeviceSchedulerSuper> maplist = (List)schedulerListMap.get(key);
                if (maplist != null && maplist.size() > 0) {
                    schedulerCache.addAll(maplist);
                }
                break;
            }
        }

        return schedulerCache;
    }

    protected static List<GizDeviceSchedulerGateway> getAllSchedulerListGateway(GizWifiDevice schedulerOwner) {
        List<GizDeviceSchedulerGateway> schedulerCache = new ArrayList();
        Iterator var2 = schedulerListMap.keySet().iterator();

        while(var2.hasNext()) {
            GizWifiDevice key = (GizWifiDevice)var2.next();
            if (schedulerOwner != null && schedulerOwner.getProductType() == GizWifiDeviceType.GizDeviceCenterControl && key.getMacAddress().equals(schedulerOwner.getMacAddress()) && key.getDid().equals(schedulerOwner.getDid()) && key.getProductKey().equals(schedulerOwner.getProductKey())) {
                List<GizDeviceSchedulerSuper> maplist = (List)schedulerListMap.get(key);
                if (maplist != null) {
                    List<GizDeviceSchedulerGateway> list = new ArrayList();
                    GizDeviceSchedulerGateway[] schedulers = maplist.toArray(new GizDeviceSchedulerGateway[list.size()]);
                    list = Arrays.asList(schedulers);
                    Iterator var7 = list.iterator();

                    while(var7.hasNext()) {
                        GizDeviceSchedulerGateway scheduler = (GizDeviceSchedulerGateway)var7.next();
                        schedulerCache.add(scheduler);
                    }
                }
                break;
            }
        }

        return schedulerCache;
    }

    protected static List<GizDeviceScheduler> getAllSchedulerListCloud(GizWifiDevice schedulerOwner) {
        List<GizDeviceScheduler> schedulerCache = new ArrayList();
        Iterator var2 = schedulerListMap.keySet().iterator();

        while(var2.hasNext()) {
            GizWifiDevice key = (GizWifiDevice)var2.next();
            if (schedulerOwner != null && schedulerOwner.getProductType() != GizWifiDeviceType.GizDeviceCenterControl && key.getMacAddress().equals(schedulerOwner.getMacAddress()) && key.getDid().equals(schedulerOwner.getDid()) && key.getProductKey().equals(schedulerOwner.getProductKey())) {
                List<GizDeviceSchedulerSuper> maplist = (List)schedulerListMap.get(key);
                if (maplist != null) {
                    List<GizDeviceScheduler> list = new ArrayList();
                    GizDeviceScheduler[] schedulers = (GizDeviceScheduler[])maplist.toArray(new GizDeviceScheduler[list.size()]);
                    list = Arrays.asList(schedulers);
                    Iterator var7 = list.iterator();

                    while(var7.hasNext()) {
                        GizDeviceScheduler scheduler = (GizDeviceScheduler)var7.next();
                        schedulerCache.add(scheduler);
                    }
                }
                break;
            }
        }

        return schedulerCache;
    }

    protected static List<GizDeviceSchedulerSuper> getAllValidSchedulerListByDevice(GizWifiDevice schedulerOwner) {
        List<GizDeviceSchedulerSuper> schedulerCache = new ArrayList();
        Iterator var2 = schedulerListMap.keySet().iterator();

        while(var2.hasNext()) {
            GizWifiDevice key = (GizWifiDevice)var2.next();
            if (schedulerOwner != null && key.getMacAddress().equals(schedulerOwner.getMacAddress()) && key.getDid().equals(schedulerOwner.getDid()) && key.getProductKey().equals(schedulerOwner.getProductKey())) {
                List<GizDeviceSchedulerSuper> maplist = (List)schedulerListMap.get(key);
                if (maplist != null) {
                    Iterator var5 = maplist.iterator();

                    while(var5.hasNext()) {
                        GizDeviceSchedulerSuper scheduler = (GizDeviceSchedulerSuper)var5.next();
                        if (scheduler.isValid) {
                            schedulerCache.add(scheduler);
                        }
                    }
                }
                break;
            }
        }

        return schedulerCache;
    }

    protected static GizDeviceSchedulerGateway getSchedulerGatewayByOwner(GizWifiDevice owner, String schedulerID) {
        List<GizDeviceSchedulerGateway> list = getAllSchedulerListGateway(owner);
        GizDeviceSchedulerGateway deviceScheduler = null;
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            GizDeviceSchedulerGateway scheduler = (GizDeviceSchedulerGateway)var4.next();
            if (scheduler.getSchedulerID().equals(schedulerID)) {
                deviceScheduler = scheduler;
                break;
            }
        }

        return deviceScheduler;
    }

    public static List<GizDeviceScheduler> getSchedulerListCloud(GizWifiDevice schedulerOwner) {
        SDKLog.a("Start => schedulerOwner: " + (schedulerOwner == null ? "" : schedulerOwner.simpleInfoMasking()));
        List<GizDeviceScheduler> schedulerCache = new ArrayList();
        List<GizDeviceScheduler> allSchedulerList = getAllSchedulerListCloud(schedulerOwner);
        Iterator var3 = allSchedulerList.iterator();

        while(var3.hasNext()) {
            GizDeviceScheduler scheduler = (GizDeviceScheduler)var3.next();
            if (scheduler.isValid) {
                schedulerCache.add(scheduler);
            }
        }

        SDKLog.a("End <= ");
        return schedulerCache;
    }

    public static List<GizDeviceSchedulerGateway> getSchedulerListGateway(GizWifiDevice schedulerOwner) {
        SDKLog.a("Start => schedulerOwner: " + (schedulerOwner == null ? "null" : schedulerOwner.simpleInfoMasking()));
        List<GizDeviceSchedulerGateway> schedulerCache = new ArrayList();
        List<GizDeviceSchedulerGateway> allSchedulerList = getAllSchedulerListGateway(schedulerOwner);
        Iterator var3 = allSchedulerList.iterator();

        while(var3.hasNext()) {
            GizDeviceSchedulerGateway scheduler = (GizDeviceSchedulerGateway)var3.next();
            if (scheduler.isValid) {
                schedulerCache.add(scheduler);
            }
        }

        SDKLog.a("End <= ");
        return schedulerCache;
    }

    public static void setListener(GizDeviceSchedulerCenterListener listener) {
        mListener = listener;
    }

    /** @deprecated */
    public static void createScheduler(String uid, String token, GizWifiDevice schedulerOwner, GizDeviceScheduler schedulerInfo) {
        SDKLog.a("Start => <deprecated> uid: " + uid + ", token: " + Utils.dataMasking(token) + ", schedulerOwner: " + (schedulerOwner == null ? "null" : schedulerOwner.moreSimpleInfoMasking()) + ", schedulerInfo: " + (schedulerInfo == null ? "null" : schedulerInfo.infoMasking()));
        ArrayList schedulerList;
        if (!Constant.ishandshake) {
            schedulerList = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, schedulerList);
            SDKLog.a("End <= ");
        } else if (schedulerInfo != null && schedulerOwner != null && !TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1091);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("did", schedulerOwner.getDid());
                obj.put("mac", schedulerOwner.getMacAddress());
                obj.put("productKey", schedulerOwner.getProductKey());
                String date = schedulerInfo.getDate();
                String time = schedulerInfo.getTime();
                if (TextUtils.isEmpty(time)) {
                    schedulerList = new ArrayList();
                    OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
                    SDKLog.a("End <= ");
                    return;
                }

                JSONObject scheduler = new JSONObject();
                String utc = null;
                String mydate = null;
                String mytime = null;
                if (time.contains(":")) {
                    String[] split = time.split(":");
                    String s = split[0];
                    if (s.equals("24")) {
                        time = "0:" + split[1];
                    }
                }

                if (!TextUtils.isEmpty(date)) {
                    utc = DateUtil.getUtc(date + " " + time, "yyyy-MM-dd HH:mm");
                    if (TextUtils.isEmpty(utc)) {
                        schedulerList = new ArrayList();
                        OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
                        SDKLog.a("End <= ");
                        return;
                    }

                    mydate = utc.substring(0, 11).trim();
                    mytime = utc.substring(11, utc.length()).trim();
                } else {
                    utc = DateUtil.getUtc(time, "HH:mm");
                    if (TextUtils.isEmpty(utc)) {
                        schedulerList = new ArrayList();
                        OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
                        SDKLog.a("End <= ");
                        return;
                    }

                    mytime = utc.trim();
                }

                scheduler.put("date", mydate);
                scheduler.put("time", mytime);
                long gettimeDiff = DateUtil.gettimeDiff(time, "00:00:00");
                int addDay = 0;
                Calendar calendar = new GregorianCalendar();
                int offset2 = calendar.getTimeZone().getOffset(System.currentTimeMillis()) / 1000;
                if (gettimeDiff - (long)offset2 < 0L) {
                    addDay = -1;
                } else if (gettimeDiff - (long)offset2 >= 0L && gettimeDiff - (long)offset2 <= 86400L) {
                    addDay = 0;
                } else {
                    addDay = 1;
                }

                GizSchedulerType repeatType = GizSchedulerType.GizSchedulerOneTime;
                if (schedulerInfo.getMonthDays() != null && schedulerInfo.getMonthDays().size() > 0) {
                    repeatType = GizSchedulerType.GizSchedulerDayRepeat;
                }

                if (schedulerInfo.getWeekdays() != null && schedulerInfo.getWeekdays().size() > 0) {
                    repeatType = GizSchedulerType.GizSchedulerWeekRepeat;
                }

                if (schedulerInfo.getWeekdays() != null && schedulerInfo.getWeekdays().size() > 0 && schedulerInfo.getMonthDays() != null && schedulerInfo.getMonthDays().size() > 0) {
                    repeatType = GizSchedulerType.GizSchedulerWeekRepeat;
                }

                if (repeatType == null) {
                    scheduler.put("repeat", "none");
                    scheduler.put("schedulerType", GizSchedulerType.GizSchedulerOneTime.ordinal());
                } else if (repeatType == GizSchedulerType.GizSchedulerOneTime) {
                    scheduler.put("repeat", "none");
                    scheduler.put("schedulerType", repeatType.ordinal());
                } else {
                    List weekdays;
                    ArrayList mylist;
                    Iterator var20;
                    if (repeatType == GizSchedulerType.GizSchedulerDayRepeat) {
                        scheduler.put("repeat", "day");
                        weekdays = schedulerInfo.getMonthDays();
                        if (weekdays != null) {
                            mylist = new ArrayList();
                            var20 = weekdays.iterator();

                            while(true) {
                                int integer;
                                if (!var20.hasNext()) {
                                    JSONArray array = new JSONArray();

                                    for(integer = 0; integer < mylist.size(); ++integer) {
                                        array.put(mylist.get(integer));
                                    }

                                    scheduler.put("days", array);
                                    scheduler.put("schedulerType", repeatType.ordinal());
                                    break;
                                }

                                integer = (Integer)var20.next();
                                if (1 <= integer + addDay && integer + addDay <= 31) {
                                    int myint = integer + addDay;
                                    if (myint > 0 && myint < 32 && !mylist.contains(myint) && (addDay != -1 || myint != 31)) {
                                        mylist.add(myint);
                                    }
                                }

                                byte myin;
                                if (integer + addDay == 0 && addDay == -1) {
                                    myin = 31;
                                    if (myin > 0 && myin < 32 && !mylist.contains(Integer.valueOf(myin))) {
                                        mylist.add(Integer.valueOf(myin));
                                    }
                                }

                                if (integer + addDay == 32 && addDay == 1) {
                                    myin = 1;
                                    if (myin > 0 && myin < 32 && !mylist.contains(Integer.valueOf(myin))) {
                                        mylist.add(Integer.valueOf(myin));
                                    }
                                }
                            }
                        }
                    } else if (repeatType == GizSchedulerType.GizSchedulerWeekRepeat) {
                        weekdays = schedulerInfo.getWeekdays();
                        mylist = new ArrayList();
                        GizScheduleWeekday gizScheduleWeekday;
                        if (addDay == 1) {
                            var20 = weekdays.iterator();

                            while(var20.hasNext()) {
                                gizScheduleWeekday = (GizScheduleWeekday)var20.next();
                                switch(gizScheduleWeekday.ordinal()) {
                                    case 0:
                                        mylist.add(GizScheduleWeekday.GizScheduleMonday);
                                        break;
                                    case 1:
                                        mylist.add(GizScheduleWeekday.GizScheduleTuesday);
                                        break;
                                    case 2:
                                        mylist.add(GizScheduleWeekday.GizScheduleWednesday);
                                        break;
                                    case 3:
                                        mylist.add(GizScheduleWeekday.GizScheduleThursday);
                                        break;
                                    case 4:
                                        mylist.add(GizScheduleWeekday.GizScheduleFriday);
                                        break;
                                    case 5:
                                        mylist.add(GizScheduleWeekday.GizScheduleSaturday);
                                        break;
                                    case 6:
                                        mylist.add(GizScheduleWeekday.GizScheduleSunday);
                                }
                            }

                            scheduler.put("repeat", Utils.listToString(mylist));
                            scheduler.put("schedulerType", repeatType.ordinal());
                        } else if (addDay != -1) {
                            scheduler.put("repeat", Utils.listToString(schedulerInfo.getWeekdays()));
                            scheduler.put("schedulerType", repeatType.ordinal());
                        } else {
                            var20 = weekdays.iterator();

                            while(var20.hasNext()) {
                                gizScheduleWeekday = (GizScheduleWeekday)var20.next();
                                switch(gizScheduleWeekday.ordinal()) {
                                    case 0:
                                        mylist.add(GizScheduleWeekday.GizScheduleSaturday);
                                        break;
                                    case 1:
                                        mylist.add(GizScheduleWeekday.GizScheduleSunday);
                                        break;
                                    case 2:
                                        mylist.add(GizScheduleWeekday.GizScheduleMonday);
                                        break;
                                    case 3:
                                        mylist.add(GizScheduleWeekday.GizScheduleTuesday);
                                        break;
                                    case 4:
                                        mylist.add(GizScheduleWeekday.GizScheduleWednesday);
                                        break;
                                    case 5:
                                        mylist.add(GizScheduleWeekday.GizScheduleThursday);
                                        break;
                                    case 6:
                                        mylist.add(GizScheduleWeekday.GizScheduleFriday);
                                }
                            }

                            scheduler.put("repeat", Utils.listToString(mylist));
                            scheduler.put("schedulerType", repeatType.ordinal());
                        }
                    }
                }

                scheduler.put("remark", schedulerInfo.getRemark());
                scheduler.put("start_date", schedulerInfo.getStartDate());
                scheduler.put("end_date", schedulerInfo.getEndDate());
                scheduler.put("enabled", schedulerInfo.getEnabled());
                scheduler.put("attrs", Utils.getTaskJsonObject(schedulerInfo.getAttrs()));
                obj.put("scheduler", scheduler);
            } catch (JSONException var23) {
                SDKLog.e(var23.toString());
                var23.printStackTrace();
            }

            sendMessageToDaemon(obj);
            makeTimer(31000, 1092, sn);
            SDKLog.a("End <= ");
        } else {
            schedulerList = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
            SDKLog.a("End <= ");
        }
    }

    public static void createScheduler(String uid, String token, GizWifiDevice schedulerOwner, GizDeviceSchedulerSuper scheduler, List<GizDeviceSchedulerTask> schedulerTasks) {
        SDKLog.a("Start => uid: " + uid + ", token: " + Utils.dataMasking(token) + ", schedulerOwner: " + (schedulerOwner == null ? "null" : schedulerOwner.moreSimpleInfoMasking()) + ", scheduler: " + (scheduler == null ? "null" : scheduler.infoMasking()) + ", schedulerTasks: " + (schedulerTasks == null ? "null" : schedulerTasks.size()));
        ArrayList schedulerList;
        if (!Constant.ishandshake) {
            schedulerList = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, schedulerList);
            SDKLog.a("End <= ");
        } else if (scheduler != null && schedulerOwner != null) {
            if (!(scheduler instanceof GizDeviceScheduler) || !TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
                int sn = Utils.getSn();

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("cmd", 1091);
                    obj.put("sn", sn);
                    obj.put("token", token);
                    obj.put("did", schedulerOwner.getDid());
                    obj.put("mac", schedulerOwner.getMacAddress());
                    obj.put("productKey", schedulerOwner.getProductKey());
                    boolean isToSetTime = true;
                    if (scheduler instanceof GizDeviceSchedulerGateway) {
                        GizDeviceSchedulerGateway way = (GizDeviceSchedulerGateway)scheduler;
                        int ordinal = way.getSchedulerType().ordinal();
                        if (ordinal == 0) {
                            isToSetTime = false;
                        }
                    }

                    JSONObject myscheduler = new JSONObject();
                    String time;
                    if (isToSetTime) {
                        String date = scheduler.getDate();
                        time = scheduler.getTime();
                        if (TextUtils.isEmpty(time)) {
                           schedulerList = new ArrayList();
                            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
                            SDKLog.a("End <= ");
                            return;
                        }

                        String utc = null;
                        String mydate = null;
                        String mytime = null;
                        if (time.contains(":")) {
                            String[] split = time.split(":");
                            String s = split[0];
                            if (s.equals("24")) {
                                time = "0:" + split[1];
                            }
                        }

                        if (!TextUtils.isEmpty(date)) {
                            utc = DateUtil.getUtc(date + " " + time, "yyyy-MM-dd HH:mm");
                            if (TextUtils.isEmpty(utc)) {
                                schedulerList = new ArrayList();
                                OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
                                SDKLog.a("End <= ");
                                return;
                            }

                            mydate = utc.substring(0, 11).trim();
                            mytime = utc.substring(11, utc.length()).trim();
                        } else {
                            utc = DateUtil.getUtc(time, "HH:mm");
                            if (TextUtils.isEmpty(utc)) {
                                schedulerList = new ArrayList();
                                OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
                                SDKLog.a("End <= ");
                                return;
                            }

                            mytime = utc.trim();
                        }

                        myscheduler.put("date", mydate);
                        myscheduler.put("time", mytime);
                        long gettimeDiff = DateUtil.gettimeDiff(time, "00:00:00");
                        int addDay = 0;
                        Calendar calendar = new GregorianCalendar();
                        int offset2 = calendar.getTimeZone().getOffset(System.currentTimeMillis()) / 1000;
                        if (gettimeDiff - (long)offset2 < 0L) {
                            addDay = -1;
                        } else if (gettimeDiff - (long)offset2 >= 0L && gettimeDiff - (long)offset2 <= 86400L) {
                            addDay = 0;
                        } else {
                            addDay = 1;
                        }

                        GizSchedulerType repeatType = GizSchedulerType.GizSchedulerOneTime;
                        if (scheduler.getMonthDays() != null && scheduler.getMonthDays().size() > 0) {
                            repeatType = GizSchedulerType.GizSchedulerDayRepeat;
                        }

                        if (scheduler.getWeekdays() != null && scheduler.getWeekdays().size() > 0) {
                            repeatType = GizSchedulerType.GizSchedulerWeekRepeat;
                        }

                        if (scheduler.getWeekdays() != null && scheduler.getWeekdays().size() > 0 && scheduler.getMonthDays() != null && scheduler.getMonthDays().size() > 0) {
                            repeatType = GizSchedulerType.GizSchedulerWeekRepeat;
                        }

                        if (repeatType == null) {
                            myscheduler.put("repeat", "none");
                        } else if (repeatType == GizSchedulerType.GizSchedulerOneTime) {
                            myscheduler.put("repeat", "none");
                        } else {
                            List weekdays;
                            ArrayList mylist;
                            Iterator var22;
                            if (repeatType == GizSchedulerType.GizSchedulerDayRepeat) {
                                myscheduler.put("repeat", "day");
                                weekdays = scheduler.getMonthDays();
                                if (weekdays != null) {
                                    mylist = new ArrayList();
                                    var22 = weekdays.iterator();

                                    while(true) {
                                        int integer;
                                        if (!var22.hasNext()) {
                                            JSONArray array = new JSONArray();

                                            for(integer = 0; integer < mylist.size(); ++integer) {
                                                array.put(mylist.get(integer));
                                            }

                                            myscheduler.put("days", array);
                                            break;
                                        }

                                        integer = (Integer)var22.next();
                                        if (1 <= integer + addDay && integer + addDay <= 31) {
                                            int myint = integer + addDay;
                                            if (myint > 0 && myint < 32 && !mylist.contains(myint) && (addDay != -1 || myint != 31)) {
                                                mylist.add(myint);
                                            }
                                        }

                                        byte myin;
                                        if (integer + addDay == 0 && addDay == -1) {
                                            myin = 31;
                                            if (myin > 0 && myin < 32 && !mylist.contains(Integer.valueOf(myin))) {
                                                mylist.add(Integer.valueOf(myin));
                                            }
                                        }

                                        if (integer + addDay == 32 && addDay == 1) {
                                            myin = 1;
                                            if (myin > 0 && myin < 32 && !mylist.contains(Integer.valueOf(myin))) {
                                                mylist.add(Integer.valueOf(myin));
                                            }
                                        }
                                    }
                                }
                            } else if (repeatType == GizSchedulerType.GizSchedulerWeekRepeat) {
                                weekdays = scheduler.getWeekdays();
                                mylist = new ArrayList();
                                GizScheduleWeekday gizScheduleWeekday;
                                if (addDay == 1) {
                                    var22 = weekdays.iterator();

                                    while(var22.hasNext()) {
                                        gizScheduleWeekday = (GizScheduleWeekday)var22.next();
                                        switch(gizScheduleWeekday.ordinal()) {
                                            case 0:
                                                mylist.add(GizScheduleWeekday.GizScheduleMonday);
                                                break;
                                            case 1:
                                                mylist.add(GizScheduleWeekday.GizScheduleTuesday);
                                                break;
                                            case 2:
                                                mylist.add(GizScheduleWeekday.GizScheduleWednesday);
                                                break;
                                            case 3:
                                                mylist.add(GizScheduleWeekday.GizScheduleThursday);
                                                break;
                                            case 4:
                                                mylist.add(GizScheduleWeekday.GizScheduleFriday);
                                                break;
                                            case 5:
                                                mylist.add(GizScheduleWeekday.GizScheduleSaturday);
                                                break;
                                            case 6:
                                                mylist.add(GizScheduleWeekday.GizScheduleSunday);
                                        }
                                    }

                                    myscheduler.put("repeat", Utils.listToString(mylist));
                                } else if (addDay != -1) {
                                    myscheduler.put("repeat", Utils.listToString(scheduler.getWeekdays()));
                                } else {
                                    var22 = weekdays.iterator();

                                    while(var22.hasNext()) {
                                        gizScheduleWeekday = (GizScheduleWeekday)var22.next();
                                        switch(gizScheduleWeekday.ordinal()) {
                                            case 0:
                                                mylist.add(GizScheduleWeekday.GizScheduleSaturday);
                                                break;
                                            case 1:
                                                mylist.add(GizScheduleWeekday.GizScheduleSunday);
                                                break;
                                            case 2:
                                                mylist.add(GizScheduleWeekday.GizScheduleMonday);
                                                break;
                                            case 3:
                                                mylist.add(GizScheduleWeekday.GizScheduleTuesday);
                                                break;
                                            case 4:
                                                mylist.add(GizScheduleWeekday.GizScheduleWednesday);
                                                break;
                                            case 5:
                                                mylist.add(GizScheduleWeekday.GizScheduleThursday);
                                                break;
                                            case 6:
                                                mylist.add(GizScheduleWeekday.GizScheduleFriday);
                                        }
                                    }

                                    myscheduler.put("repeat", Utils.listToString(mylist));
                                }
                            }
                        }
                    }

                    if (scheduler instanceof GizDeviceScheduler) {
                        GizDeviceScheduler duler = (GizDeviceScheduler)scheduler;
                        if (duler.getSchedulerType() == null) {
                           schedulerList = new ArrayList();
                            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
                            SDKLog.a("End <= ");
                            return;
                        }

                        myscheduler.put("remark", duler.getRemark());
                        myscheduler.put("start_date", duler.getStartDate());
                        myscheduler.put("end_date", duler.getEndDate());
                        myscheduler.put("enabled", duler.getEnabled());
                        myscheduler.put("attrs", Utils.getTaskJsonObject(duler.getAttrs()));
                        myscheduler.put("schedulerType", duler.getSchedulerType().ordinal());
                    } else if (scheduler instanceof GizDeviceSchedulerGateway) {
                        GizDeviceSchedulerGateway duler = (GizDeviceSchedulerGateway)scheduler;
                        myscheduler.put("schedulerName", duler.getName());
                        myscheduler.put("delay", duler.getDelayTime());
                        myscheduler.put("schedulerType", duler.getSchedulerType().ordinal());
                        time = Utils.schedulerTasksToString(schedulerTasks);
                        if (!TextUtils.isEmpty(time)) {
                            JSONArray ay = new JSONArray(time);
                            myscheduler.put("tasks", ay);
                        }
                    }

                    obj.put("scheduler", myscheduler);
                    sendMessageToDaemon(obj);
                    if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
                        makeTimer(31000, 1092, sn);
                    } else if (schedulerOwner.isLAN) {
                        makeTimer(9000, 1092, sn);
                    } else {
                        makeTimer(20000, 1092, sn);
                    }
                } catch (JSONException var25) {
                    var25.printStackTrace();
                }

                SDKLog.a("End <= ");
            } else {
                schedulerList = new ArrayList();
                OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
                SDKLog.a("End <= ");
            }
        } else {
            schedulerList = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
            SDKLog.a("End <= ");
        }
    }

    /** @deprecated */
    public static void deleteScheduler(String uid, String token, GizWifiDevice schedulerOwner, String sid) {
        SDKLog.a("Start => <deprecated> uid: " + uid + ", token: " + Utils.dataMasking(token) + ", schedulerOwner: " + (schedulerOwner == null ? "null" : schedulerOwner.moreSimpleInfoMasking()) + ", sid: " + sid);
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.a("End <= ");
        } else if (schedulerOwner != null && !TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(sid)) {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1095);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("mac", schedulerOwner.getMacAddress());
                obj.put("did", schedulerOwner.getDid());
                obj.put("schedulerID", sid);
                obj.put("productKey", schedulerOwner.getProductKey());
            } catch (JSONException var7) {
                SDKLog.e(var7.toString());
                var7.printStackTrace();
            }

            sendMessageToDaemon(obj);
            makeTimer(31000, 1096, sn);
            SDKLog.a("End <= ");
        } else {
            list = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        }
    }

    public static void deleteScheduler(String uid, String token, GizWifiDevice schedulerOwner, GizDeviceSchedulerSuper scheduler) {
        SDKLog.a("Start => uid: " + uid + ", token: " + Utils.dataMasking(token) + ", schedulerOwner: " + (schedulerOwner == null ? "null" : schedulerOwner.moreSimpleInfoMasking()) + ", scheduler: " + (scheduler == null ? "null" : scheduler.getSchedulerID()));
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.a("End <= ");
        } else if (schedulerOwner != null && scheduler != null && !TextUtils.isEmpty(scheduler.getSchedulerID())) {
            if (!(scheduler instanceof GizDeviceScheduler) || !TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1095);
                    obj.put("sn", sn);
                    obj.put("token", token);
                    obj.put("mac", schedulerOwner.getMacAddress());
                    obj.put("did", schedulerOwner.getDid());
                    obj.put("productKey", schedulerOwner.getProductKey());
                    obj.put("schedulerID", scheduler.getSchedulerID());
                } catch (JSONException var7) {
                    SDKLog.e(var7.toString());
                    var7.printStackTrace();
                }

                sendMessageToDaemon(obj);
                if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
                    makeTimer(31000, 1096, sn);
                } else if (schedulerOwner.isLAN) {
                    makeTimer(9000, 1096, sn);
                } else {
                    makeTimer(20000, 1096, sn);
                }

                SDKLog.a("End <= ");
            } else {
                list = new ArrayList();
                OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
                SDKLog.a("End <= ");
            }
        } else {
            list = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        }
    }

    public static void updateSchedulers(String uid, String token, GizWifiDevice schedulerOwner) {
        SDKLog.a("Start => uid: " + uid + ", token: " + Utils.dataMasking(token) + ", schedulerOwner: " + (schedulerOwner == null ? "null" : schedulerOwner.moreSimpleInfoMasking()));
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.a("End <= ");
        } else if (schedulerOwner == null) {
            list = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else {
            if (!TextUtils.isEmpty(uid) && TextUtils.isEmpty(token) || TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
                list = new ArrayList();
                OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
                SDKLog.a("End <= ");
            }

            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1093);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("mac", schedulerOwner.getMacAddress());
                obj.put("did", schedulerOwner.getDid());
                obj.put("productKey", schedulerOwner.getProductKey());
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
                makeTimer(31000, 1094, sn);
            } else if (schedulerOwner.isLAN) {
                makeTimer(9000, 1094, sn);
            } else {
                makeTimer(20000, 1094, sn);
            }

            SDKLog.a("End <= ");
        }
    }

    /** @deprecated */
    public static void editScheduler(String uid, String token, GizWifiDevice schedulerOwner, GizDeviceScheduler schedulerInfo) {
        SDKLog.d("Start => ");
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.d("End <= ");
        } else if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token) && schedulerOwner != null && schedulerInfo != null && !TextUtils.isEmpty(schedulerInfo.getSchedulerID())) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1099);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("mac", schedulerOwner.getMacAddress());
                obj.put("did", schedulerOwner.getDid());
                obj.put("productKey", schedulerOwner.getProductKey());
                obj.put("schedulerID", schedulerInfo.getSchedulerID());
                String date = schedulerInfo.getDate();
                String time = schedulerInfo.getTime();
                if (TextUtils.isEmpty(time)) {
                    List<GizDeviceSchedulerSuper> schedulerList = new ArrayList();
                    OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
                    SDKLog.d("End <= ");
                    return;
                }

                JSONObject scheduler = new JSONObject();
                String utc = null;
                String mydate = null;
                String mytime = null;
                if (time.contains(":")) {
                    String[] split = time.split(":");
                    String s = split[0];
                    if (s.equals("24")) {
                        time = "0:" + split[1];
                    }
                }

                ArrayList schedulerList;
                if (!TextUtils.isEmpty(date)) {
                    utc = DateUtil.getUtc(date + " " + time, "yyyy-MM-dd HH:mm");
                    if (TextUtils.isEmpty(utc)) {
                        schedulerList = new ArrayList();
                        OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
                        SDKLog.d("End <= ");
                        return;
                    }

                    mydate = utc.substring(0, 11).trim();
                    mytime = utc.substring(11, utc.length()).trim();
                } else {
                    utc = DateUtil.getUtc(time, "HH:mm");
                    if (TextUtils.isEmpty(utc)) {
                        schedulerList = new ArrayList();
                        OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, schedulerList);
                        SDKLog.d("End <= ");
                        return;
                    }

                    mytime = utc;
                }

                scheduler.put("date", mydate);
                scheduler.put("time", mytime);
                long gettimeDiff = DateUtil.gettimeDiff(time, "00:00:00");
                int addDay = 0;
                Calendar calendar = new GregorianCalendar();
                int offset2 = calendar.getTimeZone().getOffset(System.currentTimeMillis()) / 1000;
                if (gettimeDiff - (long)offset2 < 0L) {
                    addDay = -1;
                } else if (gettimeDiff - (long)offset2 >= 0L && gettimeDiff - (long)offset2 <= 86400L) {
                    addDay = 0;
                } else {
                    addDay = 1;
                }

                GizScheduleRepeatType repeatType = GizScheduleRepeatType.GizScheduleNone;
                if (schedulerInfo.getMonthDays() != null && schedulerInfo.getMonthDays().size() > 0) {
                    repeatType = GizScheduleRepeatType.GizScheduleDay;
                }

                if (schedulerInfo.getWeekdays() != null && schedulerInfo.getWeekdays().size() > 0) {
                    repeatType = GizScheduleRepeatType.GizScheduleWeek;
                }

                if (schedulerInfo.getWeekdays() != null && schedulerInfo.getWeekdays().size() > 0 && schedulerInfo.getMonthDays() != null && schedulerInfo.getMonthDays().size() > 0) {
                    repeatType = GizScheduleRepeatType.GizScheduleWeek;
                }

                if (repeatType == null) {
                    scheduler.put("repeat", "none");
                } else if (repeatType == GizScheduleRepeatType.GizScheduleNone) {
                    scheduler.put("repeat", "none");
                } else {
                    List weekdays;
                    ArrayList mylist;
                    Iterator var20;
                    if (repeatType == GizScheduleRepeatType.GizScheduleDay) {
                        scheduler.put("repeat", "day");
                        weekdays = schedulerInfo.getMonthDays();
                        if (weekdays != null) {
                            mylist = new ArrayList();
                            var20 = weekdays.iterator();

                            while(true) {
                                int integer;
                                if (!var20.hasNext()) {
                                    JSONArray array = new JSONArray();

                                    for(integer = 0; integer < mylist.size(); ++integer) {
                                        array.put(mylist.get(integer));
                                    }

                                    scheduler.put("days", array);
                                    break;
                                }

                                integer = (Integer)var20.next();
                                if (1 <= integer + addDay && integer + addDay <= 31) {
                                    int myint = integer + addDay;
                                    if (myint > 0 && myint < 32 && !mylist.contains(myint) && (addDay != -1 || myint != 31)) {
                                        mylist.add(myint);
                                    }
                                }

                                byte er;
                                if (integer + addDay == 0 && addDay == -1) {
                                    er = 31;
                                    if (er > 0 && er < 32 && !mylist.contains(Integer.valueOf(er))) {
                                        mylist.add(Integer.valueOf(er));
                                    }
                                }

                                if (integer + addDay == 32 && addDay == 1) {
                                    er = 1;
                                    if (er > 0 && er < 32 && !mylist.contains(Integer.valueOf(er))) {
                                        mylist.add(Integer.valueOf(er));
                                    }
                                }
                            }
                        }
                    } else if (repeatType == GizScheduleRepeatType.GizScheduleWeek) {
                        weekdays = schedulerInfo.getWeekdays();
                        mylist = new ArrayList();
                        GizScheduleWeekday gizScheduleWeekday;
                        if (addDay == 1) {
                            var20 = weekdays.iterator();

                            while(var20.hasNext()) {
                                gizScheduleWeekday = (GizScheduleWeekday)var20.next();
                                switch(gizScheduleWeekday.ordinal()) {
                                    case 0:
                                        mylist.add(GizScheduleWeekday.GizScheduleMonday);
                                        break;
                                    case 1:
                                        mylist.add(GizScheduleWeekday.GizScheduleTuesday);
                                        break;
                                    case 2:
                                        mylist.add(GizScheduleWeekday.GizScheduleWednesday);
                                        break;
                                    case 3:
                                        mylist.add(GizScheduleWeekday.GizScheduleThursday);
                                        break;
                                    case 4:
                                        mylist.add(GizScheduleWeekday.GizScheduleFriday);
                                        break;
                                    case 5:
                                        mylist.add(GizScheduleWeekday.GizScheduleSaturday);
                                        break;
                                    case 6:
                                        mylist.add(GizScheduleWeekday.GizScheduleSunday);
                                }
                            }

                            scheduler.put("repeat", Utils.listToString(mylist));
                        } else if (addDay != -1) {
                            scheduler.put("repeat", Utils.listToString(schedulerInfo.getWeekdays()));
                        } else {
                            var20 = weekdays.iterator();

                            while(var20.hasNext()) {
                                gizScheduleWeekday = (GizScheduleWeekday)var20.next();
                                switch(gizScheduleWeekday.ordinal()) {
                                    case 0:
                                        mylist.add(GizScheduleWeekday.GizScheduleSaturday);
                                        break;
                                    case 1:
                                        mylist.add(GizScheduleWeekday.GizScheduleSunday);
                                        break;
                                    case 2:
                                        mylist.add(GizScheduleWeekday.GizScheduleMonday);
                                        break;
                                    case 3:
                                        mylist.add(GizScheduleWeekday.GizScheduleTuesday);
                                        break;
                                    case 4:
                                        mylist.add(GizScheduleWeekday.GizScheduleWednesday);
                                        break;
                                    case 5:
                                        mylist.add(GizScheduleWeekday.GizScheduleThursday);
                                        break;
                                    case 6:
                                        mylist.add(GizScheduleWeekday.GizScheduleFriday);
                                }
                            }

                            scheduler.put("repeat", Utils.listToString(mylist));
                        }
                    }
                }

                scheduler.put("remark", schedulerInfo.getRemark());
                scheduler.put("start_date", schedulerInfo.getStartDate());
                scheduler.put("end_date", schedulerInfo.getEndDate());
                scheduler.put("enabled", schedulerInfo.getEnabled());
                scheduler.put("attrs", Utils.getTaskJsonObject(schedulerInfo.getAttrs()));
                obj.put("scheduler", scheduler);
            } catch (JSONException var23) {
                SDKLog.e(var23.toString());
                var23.printStackTrace();
            }

            sendMessageToDaemon(obj);
            makeTimer(31000, 1100, sn);
            SDKLog.d("End <= ");
        } else {
            list = new ArrayList();
            OnDidUpdateSchedulers(schedulerOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.d("End <= ");
        }
    }

    protected static void OnDidUpdateSchedulers(GizWifiDevice schedulerOwner, GizWifiErrorCode result, List<GizDeviceSchedulerSuper> schedulerList) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", schedulerList: " + listMasking(schedulerList));
        if (mListener != null) {
            mListener.didUpdateSchedulers(result, schedulerOwner, compatibleSchedulerList(schedulerList));
            mListener.didUpdateSchedulers(schedulerOwner, result, schedulerList);
            SDKLog.d("Callback end");
        }

    }

    private static void sendMessageToDaemon(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    private static void makeTimer(int timeout, int cmd, int sn) {
        Message mes = Message.obtain();
        mes.what = sn;
        mes.obj = cmd;
        timeoutHandler.sendMessageDelayed(mes, (long)timeout);
    }

    private static void handleTimeoutMessage(int cmd, Message msg) {
        ArrayList list;
        switch(cmd) {
            case 1092:
                timeoutHandler.removeMessages(msg.what);
                list = new ArrayList();
                OnDidUpdateSchedulers(createSchedulerRequestOwner, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
            case 1093:
            case 1095:
            case 1097:
            case 1098:
            case 1099:
            default:
                break;
            case 1094:
                timeoutHandler.removeMessages(msg.what);
                list = new ArrayList();
                OnDidUpdateSchedulers(updateSchedulersRequestOwner, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1096:
                timeoutHandler.removeMessages(msg.what);
                list = new ArrayList();
                OnDidUpdateSchedulers(deleteSchedulerRequestOwner, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1100:
                timeoutHandler.removeMessages(msg.what);
                list = new ArrayList();
                OnDidUpdateSchedulers(editSchedulerRequestOwner, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
        }

    }

    private static void handleReceiveMessage(int cmd, JSONObject obj, int sn, GizWifiDevice deviceOwner) throws JSONException {
        String schedulerID;
        int errorCode;
        ArrayList list;
        switch(cmd) {
            case 1092:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (errorCode != 0) {
                    timeoutHandler.removeMessages(sn);
                    list = new ArrayList();
                    OnDidUpdateSchedulers(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    messageQueue.add(sn);
                }
                break;
            case 1094:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                timeoutHandler.removeMessages(sn);
                myschedulerList = saveSchedulerListByJson(obj);
                getAllSchedulerTasksJson(deviceOwner.getMacAddress(), deviceOwner.getDid(), deviceOwner.getProductKey());
                OnDidUpdateSchedulers(deviceOwner, GizWifiErrorCode.valueOf(errorCode), getAllValidSchedulerListByDevice(deviceOwner));
                break;
            case 1096:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                schedulerID = obj.has("schedulerID") ? obj.getString("schedulerID") : "";
                if (errorCode != 0) {
                    timeoutHandler.removeMessages(sn);
                     list = new ArrayList();
                    OnDidUpdateSchedulers(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
                    break;
                } else {
                    messageQueue.add(sn);

                    for(int i = 0; i < myschedulerList.size(); ++i) {
                        GizDeviceSchedulerSuper gizDeviceScheduler = (GizDeviceSchedulerSuper)myschedulerList.get(i);
                        if (!TextUtils.isEmpty(schedulerID) && schedulerID.equals(gizDeviceScheduler.getSchedulerID())) {
                            gizDeviceScheduler.setIsValid(false);
                            return;
                        }
                    }

                    return;
                }
            case 1100:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (errorCode != 0) {
                    timeoutHandler.removeMessages(sn);
                    list = new ArrayList();
                    OnDidUpdateSchedulers(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    messageQueue.add(sn);
                }
                break;
            case 1260:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (errorCode == 0) {
                    List<GizDeviceSchedulerSuper> schedulers = deviceOwner != null ? (List)schedulerListMap.get(deviceOwner) : null;
                    if (schedulers != null && schedulers.size() > 0) {
                        boolean notify = saveAllSchedulerTasksByJson(deviceOwner, schedulers, obj);
                        if (notify) {
                            OnDidUpdateSchedulers(deviceOwner, GizWifiErrorCode.valueOf(0), getAllValidSchedulerListByDevice(deviceOwner));
                        }
                    } else {
                        SDKLog.d("Device's scheduler list is empty, can not save json tasks!");
                    }
                } else {
                    SDKLog.d("Can not get all scheduler tasks, errorCode: " + GizWifiErrorCode.valueOf(errorCode));
                }
                break;
            case 2026:
                String did = obj.has("did") ? obj.getString("did") : "";
                schedulerID = obj.has("mac") ? obj.getString("mac") : "";
                String productKey = obj.has("productKey") ? obj.getString("productKey") : "";
                GizWifiDevice myOwnerDevice = SDKEventManager.getInstance().findDeviceInTotalDeviceList(did, schedulerID, productKey);
                Iterator var8 = messageQueue.iterator();

                while(var8.hasNext()) {
                    Integer waitsn = (Integer)var8.next();
                    if (timeoutHandler.hasMessages(waitsn)) {
                        timeoutHandler.removeMessages(waitsn);
                    }
                }

                myschedulerList = saveSchedulerListByJson(obj);
                getAllSchedulerTasksJson(schedulerID, did, productKey);
                OnDidUpdateSchedulers(myOwnerDevice, GizWifiErrorCode.GIZ_SDK_SUCCESS, getAllValidSchedulerListByDevice(myOwnerDevice));
        }

    }

    private static List<GizDeviceScheduler> compatibleSchedulerList(List<GizDeviceSchedulerSuper> list) {
        List<GizDeviceScheduler> mylist = new ArrayList();
        if (list != null) {
            Iterator var2 = list.iterator();

            while(var2.hasNext()) {
                GizDeviceSchedulerSuper gizDeviceScheduler = (GizDeviceSchedulerSuper)var2.next();
                if (gizDeviceScheduler instanceof GizDeviceScheduler) {
                    GizDeviceScheduler sd = (GizDeviceScheduler)gizDeviceScheduler;
                    if (sd.getIsValid()) {
                        mylist.add(sd);
                    }
                }
            }
        }

        return mylist;
    }

    private static void getAllSchedulerTasksJson(String mac, String did, String pk) {
        int sn = Utils.getSn();
        JSONObject obj = new JSONObject();

        try {
            obj.put("cmd", 1259);
            obj.put("sn", sn);
            obj.put("mac", mac);
            obj.put("did", did);
            obj.put("productKey", pk);
        } catch (JSONException var6) {
            SDKLog.e(var6.toString());
            var6.printStackTrace();
        }

        sendMessageToDaemon(obj);
    }

    private static boolean saveAllSchedulerTasksByJson(GizWifiDevice owner, List<GizDeviceSchedulerSuper> schedulers, JSONObject obj) {
        boolean update = false;
        if (owner != null && obj != null) {
            try {
                if (obj.has("schedulers")) {
                    JSONArray schedulersJson = obj.getJSONArray("schedulers");

                    for(int i = 0; i < schedulersJson.length(); ++i) {
                        String schedulerID = null;
                        JSONObject schedulerJson = (JSONObject)schedulersJson.get(i);
                        if (schedulerJson.has("schedulerID")) {
                            schedulerID = schedulerJson.getString("schedulerID");
                        }

                        if (TextUtils.isEmpty(schedulerID)) {
                            SDKLog.d("SchedulerID is empty, ignore it!");
                        } else {
                            GizDeviceSchedulerGateway schedulerGateway = null;
                            Iterator var9 = schedulers.iterator();

                            while(var9.hasNext()) {
                                GizDeviceSchedulerSuper scheduler = (GizDeviceSchedulerSuper)var9.next();
                                if (scheduler.getSchedulerID().equals(schedulerID)) {
                                    if (scheduler instanceof GizDeviceSchedulerGateway) {
                                        JSONArray tasksJson = schedulerJson.getJSONArray("tasks");
                                        schedulerGateway = (GizDeviceSchedulerGateway)scheduler;
                                        update = schedulerGateway.saveSchedulerTasks(owner, tasksJson);
                                    } else {
                                        SDKLog.d("Scheduler is not GizDeviceSchedulerGateway, ignore it!");
                                    }
                                    break;
                                }
                            }

                            if (schedulerGateway == null) {
                                SDKLog.d("Did not find the matched schedulerID " + schedulerID + ", ignored it");
                            }
                        }
                    }
                }
            } catch (JSONException var12) {
                var12.printStackTrace();
            }

            return update;
        } else {
            SDKLog.e("owner: " + owner + ", obj: " + obj);
            return false;
        }
    }

    protected static List<GizDeviceSchedulerSuper> saveSchedulerListByJson(JSONObject obj) throws JSONException {
        if (obj == null) {
            SDKLog.d("json is invalid, obj: " + obj);
            return new ArrayList();
        } else {
            String did = obj.has("did") ? obj.getString("did") : "";
            String mac = obj.has("mac") ? obj.getString("mac") : "";
            String productKey = obj.has("productKey") ? obj.getString("productKey") : "";
            GizWifiDevice myOwnerDevice = SDKEventManager.getInstance().findDeviceInTotalDeviceList(did, mac, productKey);
            if (myOwnerDevice == null) {
                SDKLog.d("json device is invalid, mac: mac, did: " + did + ", productKey: " + productKey);
                return new ArrayList();
            } else {
                List<GizDeviceSchedulerSuper> schedulers = (List)schedulerListMap.get(myOwnerDevice);
                JSONArray schedulersJson = obj.has("schedulers") ? obj.getJSONArray("schedulers") : null;
                if (schedulersJson == null) {
                    if (schedulers != null) {
                        SDKLog.d("schedulers from json is null, bug gateway scheduler is not empty!");
                        return (List)schedulers;
                    } else {
                        SDKLog.d("schedulers from json is null, and gateway scheduler is empty!");
                        return new ArrayList();
                    }
                } else {
                    if (schedulers == null) {
                        schedulers = new ArrayList();
                        schedulerListMap.put(myOwnerDevice, schedulers);
                    }

                    Iterator var7 = ((List)schedulers).iterator();

                    while(var7.hasNext()) {
                        GizDeviceSchedulerSuper gizDeviceScheduler = (GizDeviceSchedulerSuper)var7.next();
                        gizDeviceScheduler.setIsValid(false);
                    }

                    for(int i = 0; i < schedulersJson.length(); ++i) {
                        JSONObject schedulerJson = schedulersJson.getJSONObject(i);
                        String schedulerID = schedulerJson.has("schedulerID") ? schedulerJson.getString("schedulerID") : null;
                        String schedulerName = schedulerJson.has("schedulerName") ? schedulerJson.getString("schedulerName") : null;
                        String time = schedulerJson.has("time") ? schedulerJson.getString("time") : null;
                        String date = schedulerJson.has("date") ? schedulerJson.getString("date") : null;
                        String remark = schedulerJson.has("remark") ? schedulerJson.getString("remark") : null;
                        String start_date = schedulerJson.has("start_date") ? schedulerJson.getString("start_date") : null;
                        String end_date = schedulerJson.has("end_date") ? schedulerJson.getString("end_date") : null;
                        String created_at = schedulerJson.has("created_at") ? schedulerJson.getString("created_at") : null;
                        if (created_at != null) {
                            created_at = DateUtil.utc2Local(created_at, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
                        }

                        boolean hasIsFromCloud = schedulerJson.has("isFromCloud");
                        boolean isFromCloud = hasIsFromCloud ? schedulerJson.getBoolean("isFromCloud") : false;
                        boolean hasEnabled = schedulerJson.has("enabled");
                        boolean enabled = hasEnabled ? schedulerJson.getBoolean("enabled") : true;
                        int delay = schedulerJson.has("delay") ? schedulerJson.getInt("delay") : -1;
                        int schedulerType = schedulerJson.has("schedulerType") ? schedulerJson.getInt("schedulerType") : -1;
                        GizSchedulerType schedulerType_sdk = null;
                        switch(schedulerType) {
                            case 0:
                                schedulerType_sdk = GizSchedulerType.GizSchedulerDelay;
                                break;
                            case 1:
                                schedulerType_sdk = GizSchedulerType.GizSchedulerOneTime;
                                break;
                            case 2:
                                schedulerType_sdk = GizSchedulerType.GizSchedulerWeekRepeat;
                                break;
                            case 3:
                                schedulerType_sdk = GizSchedulerType.GizSchedulerDayRepeat;
                        }

                        String time_phone = "";
                        if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(time)) {
                            String utc2Local = DateUtil.getMyUtc(date + " " + time, "yyyy-MM-dd HH:mm");
                            date = utc2Local.substring(0, 11).trim();
                        } else if (!TextUtils.isEmpty(time)) {
                            time_phone = DateUtil.getMyUtc(time, "HH:mm");
                        }

                        if (!TextUtils.isEmpty(time)) {
                            time_phone = DateUtil.getMyUtc(time, "HH:mm");
                        }

                        long gettimeDiff = DateUtil.gettimeDiff(time_phone, "00:00:00");
                        int addDay = 0;
                        Calendar calendar = new GregorianCalendar();
                        int offset2 = calendar.getTimeZone().getOffset(System.currentTimeMillis()) / 1000;
                        if (gettimeDiff - (long)offset2 < 0L) {
                            addDay = 1;
                        } else if (gettimeDiff - (long)offset2 >= 0L && gettimeDiff - (long)offset2 <= 86400L) {
                            addDay = 0;
                        } else {
                            addDay = -1;
                        }

                        List<Integer> monthDays = null;
                        List<GizScheduleWeekday> weekDays = null;
                        String repeat = schedulerJson.has("repeat") ? schedulerJson.getString("repeat") : null;
                        if (repeat != null && !"none".equals(repeat)) {
                            if (!"day".equals(repeat)) {
                                List<GizScheduleWeekday> repeatDays = Utils.getRepeatType(repeat);
                                if (repeatDays.size() > 0) {
                                    weekDays = new ArrayList();
                                    Iterator var45;
                                    GizScheduleWeekday gizScheduleWeekday;
                                    if (addDay == 1) {
                                        var45 = repeatDays.iterator();

                                        while(var45.hasNext()) {
                                            gizScheduleWeekday = (GizScheduleWeekday)var45.next();
                                            switch(gizScheduleWeekday.ordinal()) {
                                                case 0:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleMonday);
                                                    break;
                                                case 1:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleTuesday);
                                                    break;
                                                case 2:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleWednesday);
                                                    break;
                                                case 3:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleThursday);
                                                    break;
                                                case 4:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleFriday);
                                                    break;
                                                case 5:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleSaturday);
                                                    break;
                                                case 6:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleSunday);
                                            }
                                        }
                                    } else if (addDay == -1) {
                                        var45 = repeatDays.iterator();

                                        while(var45.hasNext()) {
                                            gizScheduleWeekday = (GizScheduleWeekday)var45.next();
                                            switch(gizScheduleWeekday.ordinal()) {
                                                case 0:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleSaturday);
                                                    break;
                                                case 1:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleSunday);
                                                    break;
                                                case 2:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleMonday);
                                                    break;
                                                case 3:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleTuesday);
                                                    break;
                                                case 4:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleWednesday);
                                                    break;
                                                case 5:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleThursday);
                                                    break;
                                                case 6:
                                                    ((List)weekDays).add(GizScheduleWeekday.GizScheduleFriday);
                                            }
                                        }
                                    } else {
                                        weekDays = repeatDays;
                                    }
                                }
                            } else {
                                JSONArray daysJson = schedulerJson.has("days") ? schedulerJson.getJSONArray("days") : null;
                                if (daysJson != null && daysJson.length() > 0) {
                                    monthDays = new ArrayList();

                                    for(int j = 0; j < daysJson.length(); ++j) {
                                        int num = daysJson.getInt(j) + addDay;
                                        if (1 <= num && num <= 31) {
                                            monthDays.add(num);
                                        } else if (0 == num) {
                                            monthDays.add(31);
                                        } else if (32 == num) {
                                            monthDays.add(1);
                                        }
                                    }
                                }
                            }
                        }

                        ConcurrentHashMap<String, Object> attrs = null;
                        Iterator keys;
                        if (schedulerJson.has("attrs")) {
                            JSONObject mymapjson = schedulerJson.getJSONObject("attrs");
                            keys = mymapjson.keys();
                            attrs = new ConcurrentHashMap();

                            while(keys.hasNext()) {
                                String key = (String)keys.next();
                                Object value = mymapjson.get(key);
                                attrs.put(key, value);
                            }
                        }

                        boolean isHas = false;
                        keys = ((List)schedulers).iterator();

                        label471:
                        while(keys.hasNext()) {
                            GizDeviceSchedulerSuper gizDeviceScheduler = (GizDeviceSchedulerSuper)keys.next();
                            if (gizDeviceScheduler.getSchedulerID().equals(schedulerID)) {
                                isHas = true;
                                gizDeviceScheduler.setIsValid(true);
                                GizSchedulerType myschedulerType;
                                if (hasIsFromCloud && isFromCloud && gizDeviceScheduler instanceof GizDeviceScheduler) {
                                    GizDeviceScheduler scheduler = (GizDeviceScheduler)gizDeviceScheduler;
                                    if (schedulerType_sdk != null) {
                                        scheduler.setSchedulerType(schedulerType_sdk);
                                    }

                                    myschedulerType = scheduler.getSchedulerType();
                                    switch(myschedulerType.ordinal()) {
                                        case 1:
                                            if (attrs != null) {
                                                scheduler.setAttrs(attrs);
                                            }

                                            if (date != null) {
                                                scheduler.setDate(date);
                                            }

                                            if (time_phone != null) {
                                                scheduler.setTime(time_phone);
                                            }

                                            if (hasEnabled) {
                                                scheduler.setEnabled(enabled);
                                            }

                                            if (remark != null) {
                                                scheduler.setRemark(remark);
                                            }

                                            if (start_date != null) {
                                                scheduler.setStartDate(start_date);
                                            }

                                            if (end_date != null) {
                                                scheduler.setEndDate(end_date);
                                            }
                                            break label471;
                                        case 2:
                                            if (attrs != null) {
                                                scheduler.setAttrs(attrs);
                                            }

                                            if (time_phone != null) {
                                                scheduler.setTime(time_phone);
                                            }

                                            if (weekDays != null) {
                                                scheduler.setWeekdays((List)weekDays);
                                            }

                                            if (hasEnabled) {
                                                scheduler.setEnabled(enabled);
                                            }

                                            if (remark != null) {
                                                scheduler.setRemark(remark);
                                            }

                                            if (start_date != null) {
                                                scheduler.setStartDate(start_date);
                                            }

                                            if (end_date != null) {
                                                scheduler.setEndDate(end_date);
                                            }
                                            break label471;
                                        case 3:
                                            if (attrs != null) {
                                                scheduler.setAttrs(attrs);
                                            }

                                            if (time_phone != null) {
                                                scheduler.setTime(time_phone);
                                            }

                                            if (monthDays != null) {
                                                scheduler.setMonthDays(monthDays);
                                            }

                                            if (hasEnabled) {
                                                scheduler.setEnabled(enabled);
                                            }

                                            if (remark != null) {
                                                scheduler.setRemark(remark);
                                            }

                                            if (start_date != null) {
                                                scheduler.setStartDate(start_date);
                                            }

                                            if (end_date != null) {
                                                scheduler.setEndDate(end_date);
                                            }
                                        default:
                                            break label471;
                                    }
                                }

                                if (hasIsFromCloud && !isFromCloud && gizDeviceScheduler instanceof GizDeviceSchedulerGateway) {
                                    GizDeviceSchedulerGateway schedulerGateway = (GizDeviceSchedulerGateway)gizDeviceScheduler;
                                    if (schedulerType_sdk != null) {
                                        schedulerGateway.setSchedulerType(schedulerType_sdk);
                                    }

                                    myschedulerType = schedulerGateway.getSchedulerType();
                                    switch(myschedulerType.ordinal()) {
                                        case 0:
                                            if (delay != -1) {
                                                schedulerGateway.setDelayTime(delay);
                                            }

                                            if (schedulerName != null) {
                                                schedulerGateway.setName(schedulerName);
                                            }
                                            break label471;
                                        case 1:
                                            if (date != null) {
                                                schedulerGateway.setDate(date);
                                            }

                                            if (time_phone != null) {
                                                schedulerGateway.setTime(time_phone);
                                            }

                                            if (hasEnabled) {
                                                schedulerGateway.setEnabled(enabled);
                                            }

                                            if (schedulerName != null) {
                                                schedulerGateway.setName(schedulerName);
                                            }
                                            break label471;
                                        case 2:
                                            if (weekDays != null) {
                                                schedulerGateway.setWeekdays((List)weekDays);
                                            }

                                            if (time_phone != null) {
                                                schedulerGateway.setTime(time_phone);
                                            }

                                            if (hasEnabled) {
                                                schedulerGateway.setEnabled(enabled);
                                            }

                                            if (schedulerName != null) {
                                                schedulerGateway.setName(schedulerName);
                                            }
                                            break label471;
                                        case 3:
                                            if (monthDays != null) {
                                                schedulerGateway.setMonthDays(monthDays);
                                            }

                                            if (time_phone != null) {
                                                schedulerGateway.setTime(time_phone);
                                            }

                                            if (hasEnabled) {
                                                schedulerGateway.setEnabled(enabled);
                                            }

                                            if (schedulerName != null) {
                                                schedulerGateway.setName(schedulerName);
                                            }
                                    }
                                }
                                break;
                            }
                        }

                        if (!isHas) {
                            if (hasIsFromCloud && isFromCloud && myOwnerDevice.getProductType() != GizWifiDeviceType.GizDeviceCenterControl) {
                                GizDeviceScheduler scheduler;
                                switch(schedulerType) {
                                    case 1:
                                        scheduler = new GizDeviceScheduler(attrs, date, time_phone, enabled, remark);
                                        scheduler.setStartDate(start_date);
                                        scheduler.setEndDate(end_date);
                                        scheduler.setSchedulerOwner(myOwnerDevice);
                                        scheduler.setSchedulerID(schedulerID);
                                        ((List)schedulers).add(scheduler);
                                        break;
                                    case 2:
                                        scheduler = new GizDeviceScheduler(attrs, time_phone, (List)weekDays, enabled, remark);
                                        scheduler.setStartDate(start_date);
                                        scheduler.setEndDate(end_date);
                                        scheduler.setSchedulerID(schedulerID);
                                        scheduler.setSchedulerOwner(myOwnerDevice);
                                        ((List)schedulers).add(scheduler);
                                        break;
                                    case 3:
                                        scheduler = new GizDeviceScheduler(time_phone, attrs, monthDays, enabled, remark);
                                        scheduler.setStartDate(start_date);
                                        scheduler.setEndDate(end_date);
                                        scheduler.setSchedulerID(schedulerID);
                                        scheduler.setSchedulerOwner(myOwnerDevice);
                                        ((List)schedulers).add(scheduler);
                                }
                            } else if (hasIsFromCloud && !isFromCloud && myOwnerDevice.getProductType() == GizWifiDeviceType.GizDeviceCenterControl) {
                                GizDeviceSchedulerGateway scheduler;
                                switch(schedulerType) {
                                    case 0:
                                        scheduler = new GizDeviceSchedulerGateway(delay, schedulerName);
                                        scheduler.setSchedulerID(schedulerID);
                                        scheduler.setSchedulerOwner(myOwnerDevice);
                                        ((List)schedulers).add(scheduler);
                                        break;
                                    case 1:
                                        scheduler = new GizDeviceSchedulerGateway(date, time_phone, enabled, schedulerName);
                                        scheduler.setSchedulerID(schedulerID);
                                        scheduler.setSchedulerOwner(myOwnerDevice);
                                        ((List)schedulers).add(scheduler);
                                        break;
                                    case 2:
                                        scheduler = new GizDeviceSchedulerGateway(time_phone, (List)weekDays, enabled, schedulerName);
                                        scheduler.setSchedulerID(schedulerID);
                                        scheduler.setSchedulerOwner(myOwnerDevice);
                                        ((List)schedulers).add(scheduler);
                                        break;
                                    case 3:
                                        scheduler = new GizDeviceSchedulerGateway(time_phone, monthDays, schedulerName, enabled);
                                        scheduler.setSchedulerID(schedulerID);
                                        scheduler.setSchedulerOwner(myOwnerDevice);
                                        ((List)schedulers).add(scheduler);
                                }
                            } else {
                                SDKLog.d("can not save the scheduler, hasIsFromCloud=" + hasIsFromCloud + ", isFromCloud=" + isFromCloud + ", " + myOwnerDevice.moreSimpleInfoMasking() + ", type is " + myOwnerDevice.getProductType().name());
                            }
                        }
                    }

                    return (List)schedulerListMap.get(myOwnerDevice);
                }
            }
        }
    }
}
