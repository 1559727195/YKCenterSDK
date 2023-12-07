//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.enumration.GizConditionOperator;
import com.gizwits.gizwifisdk.enumration.GizJointActionRulerEventType;
import com.gizwits.gizwifisdk.enumration.GizLogicalOperator;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceJointActionCenterListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceJointActionCenter {
    private static ConcurrentHashMap<GizWifiDevice, List<GizDeviceJointAction>> jointActionListMap = new ConcurrentHashMap();
    private static List<Integer> messageWaitingList = new ArrayList();
    private static GizDeviceJointActionCenterListener mListener;
    private static final int MSG_RECV = 5;
    private static GizWifiDevice addJointActionDeviceOwnerRequest = null;
    private static GizWifiDevice removeJointActionDeviceOwnerRequest = null;
    private static GizWifiDevice updateJointActionDeviceOwnerRequest = null;
    protected static Handler timeoutHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GizDeviceJointActionCenter.handleTimeoutMessage(msg);
        }
    };
    protected static Handler messageHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 5:
                    String jsonStr = (String)msg.obj;

                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        int cmd = Integer.parseInt(obj.getString("cmd"));
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

                        GizDeviceJointActionCenter.handleReceiveMessage(cmd, obj, sn, deviceOwner);
                    } catch (NumberFormatException var10) {
                        var10.printStackTrace();
                    } catch (JSONException var11) {
                        var11.printStackTrace();
                    }
                default:
            }
        }
    };

    public GizDeviceJointActionCenter() {
    }

    protected static String listMasking(List<GizDeviceJointAction> list) {
        String masking = "{size= " + (list == null ? "0" : list.size()) + ", ";
        if (list != null) {
            for(Iterator var2 = list.iterator(); var2.hasNext(); masking = masking + ", ") {
                GizDeviceJointAction object = (GizDeviceJointAction)var2.next();
                masking = masking + "[" + object.infoMasking() + "]";
            }
        }

        return masking.substring(0, masking.length() - 2) + "}";
    }

    protected static ConcurrentHashMap<GizWifiDevice, List<GizDeviceJointAction>> getJointActionListMap() {
        return getValidJointActionListMap();
    }

    public static List<GizDeviceJointAction> getJointActionListGateway(GizWifiDevice jointActionOwner) {
        return getValidJointActionListByOwner(jointActionOwner);
    }

    public static void setListener(GizDeviceJointActionCenterListener listener) {
        mListener = listener;
    }

    public static void addJointAction(GizWifiDevice jointActionOwner, String jointActionName, boolean enabled, GizDeviceJointActionRule jointActionRule) {
        SDKLog.a("Start => , jointActionOwner: " + (jointActionOwner == null ? "null" : jointActionOwner.simpleInfoMasking()) + ", jointActionName: " + jointActionName + "，enabled: " + enabled + "，jointActionRule: " + jointActionRule);
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            OnDidUpdateJointActions(jointActionOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.a("End <= ");
        } else if (jointActionOwner == null) {
            list = new ArrayList();
            OnDidUpdateJointActions(jointActionOwner, GizWifiErrorCode.GIZ_SDK_PARAM_NO_DEVICE_OWNER, list);
            SDKLog.a("End <= ");
        } else if (jointActionOwner.getProductType() != GizWifiDeviceType.GizDeviceCenterControl) {
            list = new ArrayList();
            OnDidUpdateJointActions(jointActionOwner, GizWifiErrorCode.GIZ_SDK_PARAM_GATEWAY_OWNER_REQUIRED, list);
            SDKLog.a("End <= ");
        } else if (jointActionRule != null && jointActionRule.getConditionCombType() == null) {
            list = new ArrayList();
            OnDidUpdateJointActions(jointActionOwner, GizWifiErrorCode.GIZ_SDK_JOINT_ACTION_CONDITION_COMBI_INVALID, list);
            SDKLog.a("End <= ");
        } else {
            addJointActionDeviceOwnerRequest = jointActionOwner;
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1361);
                obj.put("sn", sn);
                obj.put("mac", jointActionOwner.getMacAddress());
                obj.put("did", jointActionOwner.getDid());
                obj.put("productKey", jointActionOwner.getProductKey());
                obj.put("jointActionName", jointActionName);
                obj.put("enabled", enabled);
                JSONObject jointActionRuleJsonObj = getJointActionRuleJsonObject(jointActionRule);
                if (jointActionRuleJsonObj != null) {
                    obj.put("jointActionRule", jointActionRuleJsonObj);
                }
            } catch (JSONException var7) {
                SDKLog.e(var7.toString());
                var7.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (jointActionOwner.isLAN) {
                makeTimer(9000, 1362, sn);
            } else {
                makeTimer(20000, 1362, sn);
            }

            SDKLog.a("End <= ");
        }
    }

    public static void removeJointAction(GizWifiDevice jointActionOwner, GizDeviceJointAction jointAction) {
        SDKLog.a("Start => , jointActionOwner: " + (jointActionOwner == null ? "null" : jointActionOwner.simpleInfoMasking()) + ", jointAction: " + jointAction);
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            OnDidUpdateJointActions(jointActionOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.d("End <= ");
        } else if (jointActionOwner == null) {
            list = new ArrayList();
            OnDidUpdateJointActions(jointActionOwner, GizWifiErrorCode.GIZ_SDK_PARAM_NO_DEVICE_OWNER, list);
            SDKLog.d("End <= ");
        } else if (jointAction == null) {
            list = new ArrayList();
            OnDidUpdateJointActions(jointActionOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.d("End <= ");
        } else {
            removeJointActionDeviceOwnerRequest = jointActionOwner;
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1363);
                obj.put("sn", sn);
                obj.put("mac", jointActionOwner.getMacAddress());
                obj.put("did", jointActionOwner.getDid());
                obj.put("productKey", jointActionOwner.getProductKey());
                obj.put("jointActionID", jointAction.getJointActionID());
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (jointActionOwner.isLAN) {
                makeTimer(9000, 1364, sn);
            } else {
                makeTimer(20000, 1364, sn);
            }

            SDKLog.d("End <= ");
        }
    }

    public static void updateJointActions(GizWifiDevice jointActionOwner) {
        SDKLog.a("Start => , jointActionOwner: " + (jointActionOwner == null ? "null" : jointActionOwner.simpleInfoMasking()));
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            OnDidUpdateJointActions(jointActionOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.d("End <= ");
        } else if (jointActionOwner == null) {
            list = new ArrayList();
            OnDidUpdateJointActions(jointActionOwner, GizWifiErrorCode.GIZ_SDK_PARAM_NO_DEVICE_OWNER, list);
            SDKLog.d("End <= ");
        } else {
            updateJointActionDeviceOwnerRequest = jointActionOwner;
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1365);
                obj.put("sn", sn);
                obj.put("mac", jointActionOwner.getMacAddress());
                obj.put("did", jointActionOwner.getDid());
                obj.put("productKey", jointActionOwner.getProductKey());
            } catch (JSONException var4) {
                SDKLog.e(var4.toString());
                var4.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (jointActionOwner.isLAN) {
                makeTimer(9000, 1366, sn);
            } else {
                makeTimer(20000, 1366, sn);
            }

            SDKLog.d("End <= ");
        }
    }

    private static void OnDidUpdateJointActions(GizWifiDevice jointActionOwner, GizWifiErrorCode result, List<GizDeviceJointAction> jointActionList) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", jointActionOwner: " + (jointActionOwner == null ? "null" : jointActionOwner.moreSimpleInfoMasking()) + ", jointActionList: " + listMasking(jointActionList));
        if (mListener != null) {
            mListener.didUpdateJointActions(jointActionOwner, result, jointActionList);
            SDKLog.d("Callback end");
        }

    }

    private static void sendMessageToDaemon(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    private static void makeTimer(int timeout, int cmd, int sn) {
        Message message = Message.obtain();
        message.what = sn;
        message.obj = cmd;
        timeoutHandler.sendMessageDelayed(message, (long)timeout);
    }

    protected static void handleTimeoutMessage(Message msg) {
        int cmd = (Integer)msg.obj;
        SDKLog.d("timeout message<cmd: " + cmd + ", sn: " + msg.what + ">, timer handler: " + timeoutHandler.hasMessages(msg.what));
        ArrayList list;
        switch(cmd) {
            case 1362:
                timeoutHandler.removeMessages(msg.what);
                list = new ArrayList();
                OnDidUpdateJointActions(addJointActionDeviceOwnerRequest, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
            case 1363:
            case 1365:
            default:
                break;
            case 1364:
                timeoutHandler.removeMessages(msg.what);
                list = new ArrayList();
                OnDidUpdateJointActions(removeJointActionDeviceOwnerRequest, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1366:
                timeoutHandler.removeMessages(msg.what);
                list = new ArrayList();
                OnDidUpdateJointActions(updateJointActionDeviceOwnerRequest, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
        }

    }

    protected static void handleReceiveMessage(int cmd, JSONObject obj, int sn, GizWifiDevice deviceOwner) throws JSONException {
        int errorCode;
        ArrayList list;
        String jointActionID;
        switch(cmd) {
            case 1362:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                jointActionID = obj.has("jointActionID") ? obj.getString("jointActionID") : "";
                if (errorCode != GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                    timeoutHandler.removeMessages(sn);
                    list = new ArrayList();
                    OnDidUpdateJointActions(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    messageWaitingList.add(sn);
                    SDKLog.d("receive the added jointActionID: " + jointActionID + ", but need to wait its notify");
                }
                break;
            case 1364:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                jointActionID = obj.has("jointActionID") ? obj.getString("jointActionID") : "";
                if (errorCode != GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                    timeoutHandler.removeMessages(sn);
                    list = new ArrayList();
                    OnDidUpdateJointActions(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    messageWaitingList.add(sn);
                    SDKLog.d("receive the removed jointActionID: " + jointActionID + ", but need to wait its notify");
                }
                break;
            case 1366:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                timeoutHandler.removeMessages(sn);
                if (errorCode != GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                list = new ArrayList();
                    OnDidUpdateJointActions(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    if (deviceOwner != null && obj.has("jointActions")) {
                        JSONArray jsonArray = obj.getJSONArray("jointActions");
                        updateJointActionListCache(deviceOwner, jsonArray);
                    }

                    queryAllJointActionDetails(deviceOwner);
                    OnDidUpdateJointActions(deviceOwner, GizWifiErrorCode.valueOf(errorCode), getValidJointActionListByOwner(deviceOwner));
                }
                break;
            case 1368:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (errorCode != GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                    SDKLog.d("jointActionRules details query failed");
                } else {
                    boolean update = false;
                    JSONArray jointActionArrayJson = obj.has("jointActions") ? obj.getJSONArray("jointActions") : null;
                    if (jointActionArrayJson != null) {
                        for(int i = 0; i < jointActionArrayJson.length(); ++i) {
                            JSONObject jsonObj = (JSONObject)jointActionArrayJson.get(i);
                            jointActionID = jsonObj.has("jointActionID") ? jsonObj.getString("jointActionID") : "";
                            JSONObject jointActionRuleJson = jsonObj.has("jointActionRule") ? jsonObj.getJSONObject("jointActionRule") : null;
                            Iterator var11 = getValidJointActionListByOwner(deviceOwner).iterator();

                            while(var11.hasNext()) {
                                GizDeviceJointAction object = (GizDeviceJointAction)var11.next();
                                if (object.getJointActionID().equals(jointActionID)) {
                                    update = object.updateJointActionRule(jointActionRuleJson);
                                    break;
                                }
                            }
                        }
                    }

                    if (update) {
                        OnDidUpdateJointActions(deviceOwner, GizWifiErrorCode.GIZ_SDK_SUCCESS, (List)getJointActionListMap().get(deviceOwner));
                    }
                }
                break;
            case 2034:
                Iterator var4 = messageWaitingList.iterator();

                while(var4.hasNext()) {
                    Integer waitsn = (Integer)var4.next();
                    if (timeoutHandler.hasMessages(waitsn)) {
                        timeoutHandler.removeMessages(waitsn);
                    }
                }

                if (deviceOwner != null && obj.has("jointActions")) {
                    JSONArray jsonArray = obj.getJSONArray("jointActions");
                    updateJointActionListCache(deviceOwner, jsonArray);
                }

                queryAllJointActionDetails(deviceOwner);
                OnDidUpdateJointActions(deviceOwner, GizWifiErrorCode.GIZ_SDK_SUCCESS, getValidJointActionListByOwner(deviceOwner));
        }

    }

    private static void queryAllJointActionDetails(GizWifiDevice deviceOwner) {
        if (deviceOwner != null) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1367);
                obj.put("sn", sn);
                obj.put("mac", deviceOwner.getMacAddress());
                obj.put("did", deviceOwner.getDid());
                obj.put("productKey", deviceOwner.getProductKey());
            } catch (JSONException var4) {
                SDKLog.e(var4.toString());
                var4.printStackTrace();
                return;
            }

            sendMessageToDaemon(obj);
        }
    }

    protected static List<GizDeviceJointAction> getAllJointActionListByOwner(GizWifiDevice owner) {
        List<GizDeviceJointAction> list = new ArrayList();
        if (owner == null) {
            return list;
        } else {
            Iterator var2 = jointActionListMap.keySet().iterator();

            while(var2.hasNext()) {
                GizWifiDevice key = (GizWifiDevice)var2.next();
                if (owner != null && key.getMacAddress().equals(owner.getMacAddress()) && key.getDid().equals(owner.getDid()) && key.getProductKey().equals(owner.getProductKey())) {
                    List<GizDeviceJointAction> maplist = (List)jointActionListMap.get(key);
                    if (maplist != null && maplist.size() > 0) {
                        list.addAll(maplist);
                    }
                    break;
                }
            }

            return list;
        }
    }

    private static ConcurrentHashMap<GizWifiDevice, List<GizDeviceJointAction>> getValidJointActionListMap() {
        SDKLog.d("read cache:");
        ConcurrentHashMap<GizWifiDevice, List<GizDeviceJointAction>> mapList = new ConcurrentHashMap();
        Iterator var1 = jointActionListMap.keySet().iterator();

        while(var1.hasNext()) {
            GizWifiDevice key = (GizWifiDevice)var1.next();
            SDKLog.d("cache joint action's owner: " + key.moreSimpleInfoMasking());
            List<GizDeviceJointAction> jointActionList = new ArrayList();
            Iterator var4 = ((List)jointActionListMap.get(key)).iterator();

            while(var4.hasNext()) {
                GizDeviceJointAction jointAction = (GizDeviceJointAction)var4.next();
                if (jointAction.getIsValid()) {
                    SDKLog.d("cache joint action: " + jointAction.simpleInfoMasking());
                    jointActionList.add(jointAction);
                }
            }

            if (jointActionList.size() > 0) {
                mapList.put(key, jointActionList);
            }
        }

        return mapList;
    }

    protected static List<GizDeviceJointAction> getValidJointActionListByOwner(GizWifiDevice owner) {
        List<GizDeviceJointAction> list = new ArrayList();
        ConcurrentHashMap<GizWifiDevice, List<GizDeviceJointAction>> maplist = getValidJointActionListMap();
        if (owner == null) {
            return (List)list;
        } else {
            boolean isHasOwner = false;
            Iterator var4 = maplist.keySet().iterator();

            while(var4.hasNext()) {
                GizWifiDevice key = (GizWifiDevice)var4.next();
                if (owner != null && key.getMacAddress().equals(owner.getMacAddress()) && key.getDid().equals(owner.getDid()) && key.getProductKey().equals(owner.getProductKey())) {
                    isHasOwner = true;
                    break;
                }
            }

            if (isHasOwner) {
                list = (List)maplist.get(owner);
            }

            return (List)list;
        }
    }

    private static boolean updateJointActionListCache(GizWifiDevice deviceOwner, JSONArray jsonArray) throws JSONException {
        boolean update = false;
        if (deviceOwner != null && jsonArray != null) {
            List<GizDeviceJointAction> newlist = new ArrayList();
            List<GizDeviceJointAction> cachelist = new ArrayList();
            boolean isHasOwner = false;
            Iterator var6 = jointActionListMap.keySet().iterator();

            while(var6.hasNext()) {
                GizWifiDevice key = (GizWifiDevice)var6.next();
                if (key.getMacAddress().equals(deviceOwner.getMacAddress()) && key.getDid().equals(deviceOwner.getDid()) && key.getProductKey().equals(deviceOwner.getProductKey())) {
                    cachelist = (List)jointActionListMap.get(key);
                    isHasOwner = true;
                    break;
                }
            }

            var6 = ((List)cachelist).iterator();

            while(var6.hasNext()) {
                GizDeviceJointAction jointAction = (GizDeviceJointAction)var6.next();
                jointAction.setIsValid(false);
            }

            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                String jointActionID = jsonObj.has("jointActionID") ? jsonObj.getString("jointActionID") : "";
                if (TextUtils.isEmpty(jointActionID)) {
                    SDKLog.d("jointActionID in json is empty, skip");
                } else {
                    boolean hasEnabled = jsonObj.has("enabled");
                    boolean enabled = jsonObj.has("enabled") ? jsonObj.getBoolean("enabled") : false;
                    String jointActionName = jsonObj.has("jointActionName") ? jsonObj.getString("jointActionName") : null;
                    if (!isHasOwner) {
                        SDKLog.d("no owner, add new joint action, jointActionID: " + jointActionID);
                        GizDeviceJointAction jointAction = new GizDeviceJointAction(deviceOwner, jointActionID, jointActionName, enabled);
                        newlist.add(jointAction);
                        update = true;
                    } else {
                        boolean isHasOb = false;
                        Iterator var13 = ((List)cachelist).iterator();

                        while(var13.hasNext()) {
                            GizDeviceJointAction jointAction = (GizDeviceJointAction)var13.next();
                            if (jointAction.getJointActionID().equals(jointActionID)) {
                                SDKLog.d("has owner, get cache joint action, jointActionID: " + jointActionID + ", isvalid: " + jointAction.getIsValid());
                                if (jointActionName != null) {
                                    if (!jointActionName.equals(jointAction.getJointActionName())) {
                                        update = true;
                                    }

                                    jointAction.setJointActionName(jointActionName);
                                }

                                if (hasEnabled) {
                                    if (enabled != jointAction.getEnabled()) {
                                        update = true;
                                    }

                                    jointAction.setEnabled(enabled);
                                }

                                jointAction.setJointActionOwner(deviceOwner);
                                jointAction.setIsValid(true);
                                isHasOb = true;
                                break;
                            }
                        }

                        if (!isHasOb) {
                            SDKLog.d("has owner, add new joint action, jointActionID: " + jointActionID);
                            GizDeviceJointAction jointAction = new GizDeviceJointAction(deviceOwner, jointActionID, jointActionName, enabled);
                            ((List)cachelist).add(jointAction);
                            update = true;
                        }
                    }
                }
            }

            if (!isHasOwner && newlist.size() > 0) {
                jointActionListMap.put(deviceOwner, newlist);
            }

            return update;
        } else {
            SDKLog.d("deviceOwner or jsonArray is null, skip");
            return false;
        }
    }

    protected static JSONObject getJointActionRuleJsonObject(GizDeviceJointActionRule jointActionRule) throws JSONException {
        if (jointActionRule == null) {
            return null;
        } else {
            JSONObject jsonObj = new JSONObject();
            GizLogicalOperator conditionCombType = jointActionRule.getConditionCombType();
            if (conditionCombType != null) {
                jsonObj.put("conditionCombType", conditionCombType.ordinal());
            }

            List<GizDeviceJointActionRuleCondition> conditionList = jointActionRule.getConditionList();
            ConcurrentHashMap data;
            JSONObject mob;
            Iterator var12;
            String key;
            if (conditionList != null && conditionList.size() > 0) {
                JSONArray conditionArrayJson = new JSONArray();

                JSONObject conditionJson;
                for(Iterator var5 = conditionList.iterator(); var5.hasNext(); conditionArrayJson.put(conditionJson)) {
                    GizDeviceJointActionRuleCondition ruleCondition = (GizDeviceJointActionRuleCondition)var5.next();
                    conditionJson = new JSONObject();
                    GizWifiDevice device = ruleCondition.getDevice();
                    if (device != null) {
                        JSONObject dev = new JSONObject();
                        dev.put("mac", device.getMacAddress());
                        dev.put("did", device.getDid());
                        dev.put("productKey", device.getProductKey());
                        conditionJson.put("device", dev);
                    }

                    GizConditionOperator conditionOperator = ruleCondition.getConditionOperator();
                    if (conditionOperator != null) {
                        conditionJson.put("conditionOperator", conditionOperator.ordinal());
                    }

                    data = ruleCondition.getData();
                    if (data != null && data.size() > 0) {
                        mob = new JSONObject();
                        var12 = data.keySet().iterator();

                        while(var12.hasNext()) {
                            key = (String)var12.next();
                            mob.put(key, data.get(key));
                        }

                        conditionJson.put("data", mob);
                    }
                }

                jsonObj.put("conditions", conditionArrayJson);
            }

            List<GizDeviceJointActionRuleResultEvent> resultEventList = jointActionRule.getResultEventList();
            if (resultEventList != null && resultEventList.size() > 0) {
                JSONArray eventArrayJson = new JSONArray();

                JSONObject resultEventJson;
                for(Iterator var17 = resultEventList.iterator(); var17.hasNext(); eventArrayJson.put(resultEventJson)) {
                    GizDeviceJointActionRuleResultEvent mRuleResultEvent = (GizDeviceJointActionRuleResultEvent)var17.next();
                    resultEventJson = new JSONObject();
                    GizJointActionRulerEventType resultEventType = mRuleResultEvent.getResultEventType();
                    if (resultEventType != null) {
                        resultEventJson.put("resultEventType", resultEventType.ordinal());
                    }

                    boolean enabled;
                    switch(resultEventType.ordinal()) {
                        case 0:
                            GizWifiDevice device = mRuleResultEvent.getDevice();
                            if (device != null) {
                                mob = new JSONObject();
                                mob.put("mac", device.getMacAddress());
                                mob.put("did", device.getDid());
                                mob.put("productKey", device.getProductKey());
                                resultEventJson.put("device", mob);
                            }

                             data = mRuleResultEvent.getData();
                            if (data == null || data.size() <= 0) {
                                break;
                            }

                             mob = new JSONObject();
                            Iterator var29 = data.keySet().iterator();

                            while(var29.hasNext()) {
                               key = (String)var29.next();
                                mob.put(key, data.get(key));
                            }

                            resultEventJson.put("data", mob);
                            break;
                        case 1:
                            data = mRuleResultEvent.getData();
                            if (data != null && data.size() > 0) {
                                mob = new JSONObject();
                                var12 = data.keySet().iterator();

                                while(var12.hasNext()) {
                                    key = (String)var12.next();
                                    mob.put(key, data.get(key));
                                }

                                resultEventJson.put("data", mob);
                            }

                            GizDeviceGroup group = mRuleResultEvent.getGroup();
                            if (group != null) {
                                resultEventJson.put("groupID", group.getGroupID());
                            }
                            break;
                        case 2:
                            enabled = mRuleResultEvent.getEnabled();
                            resultEventJson.put("enabled", enabled);
                            GizDeviceScene scene = mRuleResultEvent.getScene();
                            if (scene != null) {
                                resultEventJson.put("sceneID", scene.getSceneID());
                            }
                            break;
                        case 3:
                            enabled = mRuleResultEvent.getEnabled();
                            resultEventJson.put("enabled", enabled);
                            GizDeviceSchedulerSuper scheduler = mRuleResultEvent.getScheduler();
                            if (scheduler != null) {
                                resultEventJson.put("schedulerID", scheduler.getSchedulerID());
                            }
                    }
                }

                jsonObj.put("resultEvents", eventArrayJson);
            }

            return jsonObj;
        }
    }
}
