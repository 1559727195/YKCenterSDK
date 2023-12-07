//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.gizwits.gizwifisdk.enumration.GizSceneItemType;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceSceneCenterListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceSceneCenter {
    private static ConcurrentHashMap<GizWifiDevice, List<GizDeviceScene>> sceneListMap = new ConcurrentHashMap();
    private static List<Integer> messageQueue = new ArrayList();
    private static final int MSG_RECV = 5;
    private static GizDeviceSceneCenterListener mListener;
    private static GizWifiDevice addSceneRequestOwner = null;
    private static GizWifiDevice removeSceneRequestOwner = null;
    private static GizWifiDevice updateScenesRequestOwner = null;
    protected static Handler timeoutHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GizDeviceSceneCenter.handleTimeoutMessage(msg);
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

                        GizDeviceSceneCenter.handleReceiveMessage(cmd, obj, sn, deviceOwner);
                    } catch (NumberFormatException var10) {
                        var10.printStackTrace();
                    } catch (JSONException var11) {
                        var11.printStackTrace();
                    }
                default:
            }
        }
    };

    public GizDeviceSceneCenter() {
    }

    protected static String listMasking(List<GizDeviceScene> list) {
        String masking = "{size= " + (list == null ? "0" : list.size()) + ", ";
        if (list != null) {
            for(Iterator var2 = list.iterator(); var2.hasNext(); masking = masking + ", ") {
                GizDeviceScene object = (GizDeviceScene)var2.next();
                masking = masking + "[" + object.infoMasking() + "]";
            }
        }

        return masking.substring(0, masking.length() - 2) + "}";
    }

    private static String mapMasking(ConcurrentHashMap<GizWifiDevice, List<GizDeviceScene>> map) {
        String masking = "{size= " + (map == null ? "0" : map.size()) + ", ";
        if (map != null) {
            for(Iterator var2 = map.keySet().iterator(); var2.hasNext(); masking = masking + ", ") {
                GizWifiDevice key = (GizWifiDevice)var2.next();
                List<GizDeviceScene> list = (List)map.get(key);
                masking = "{" + key.simpleInfoMasking() + ": " + listMasking(list) + "}";
            }
        }

        return masking.substring(0, masking.length() - 2) + "}";
    }

    private static ConcurrentHashMap<GizWifiDevice, List<GizDeviceScene>> getSceneListMap() {
        return sceneListMap;
    }

    protected static List<GizDeviceScene> getTotalSceneListByOwner(GizWifiDevice owner) {
        List<GizDeviceScene> list = new ArrayList();
        boolean isHasOwner = false;
        Iterator var3 = getSceneListMap().keySet().iterator();

        while(var3.hasNext()) {
            GizWifiDevice key = (GizWifiDevice)var3.next();
            if (owner != null && key.getMacAddress().equals(owner.getMacAddress()) && key.getDid().equals(owner.getDid()) && key.getProductKey().equals(owner.getProductKey())) {
                isHasOwner = true;
                break;
            }
        }

        if (isHasOwner) {
            list = (List)getSceneListMap().get(owner);
            var3 = ((List)list).iterator();

            while(var3.hasNext()) {
                GizDeviceScene item = (GizDeviceScene)var3.next();
                item.setSceneItemList((List)null);
            }
        }

        return (List)list;
    }

    private static ConcurrentHashMap<GizWifiDevice, List<GizDeviceScene>> getValidSceneListMap() {
        ConcurrentHashMap<GizWifiDevice, List<GizDeviceScene>> mapList = new ConcurrentHashMap();
        Iterator var1 = getSceneListMap().keySet().iterator();

        while(var1.hasNext()) {
            GizWifiDevice key = (GizWifiDevice)var1.next();
            List<GizDeviceScene> sceneList = new ArrayList();
            Iterator var4 = ((List)getSceneListMap().get(key)).iterator();

            while(var4.hasNext()) {
                GizDeviceScene scene = (GizDeviceScene)var4.next();
                if (scene.getIsValid()) {
                    sceneList.add(scene);
                }
            }

            if (sceneList.size() > 0) {
                mapList.put(key, sceneList);
            }
        }

        return mapList;
    }

    protected static List<GizDeviceScene> getValidSceneListByOwner(GizWifiDevice owner) {
        List<GizDeviceScene> list = new ArrayList();
        ConcurrentHashMap<GizWifiDevice, List<GizDeviceScene>> maplist = getValidSceneListMap();
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
            var4 = ((List)list).iterator();

            while(var4.hasNext()) {
                GizDeviceScene item = (GizDeviceScene)var4.next();
                item.setSceneItemList((List)null);
            }
        }

        return (List)list;
    }

    protected static GizDeviceScene getSceneByOwner(GizWifiDevice owner, String sceneID) {
        List<GizDeviceScene> list = getTotalSceneListByOwner(owner);
        GizDeviceScene deviceScene = null;
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            GizDeviceScene scene = (GizDeviceScene)var4.next();
            if (scene.getSceneID().equals(sceneID)) {
                deviceScene = scene;
                break;
            }
        }

        return deviceScene;
    }

    public static List<GizDeviceScene> getSceneListGateway(GizWifiDevice sceneOwner) {
        SDKLog.a("Start => sceneOwner: " + (sceneOwner == null ? "" : sceneOwner.simpleInfoMasking()));
        List<GizDeviceScene> list = getValidSceneListByOwner(sceneOwner);
        SDKLog.d("-----> cache scene list: " + listMasking(list));
        SDKLog.a("End <= ");
        return list;
    }

    public static void setListener(GizDeviceSceneCenterListener listener) {
        SDKLog.a("Start => listener: " + (listener == null ? "null" : listener));
        mListener = listener;
        SDKLog.a("End <= ");
    }

    public static void addScene(GizWifiDevice sceneOwner, String sceneName, List<GizDeviceSceneItem> sceneItems) {
        SDKLog.d("Start => sceneOwner: " + (sceneOwner == null ? "null" : sceneOwner.simpleInfoMasking()) + ", sceneName: " + sceneName + ", sceneItems: " + (sceneItems == null ? "null" : sceneItems.size()));
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            OnDidUpdateScenes(sceneOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.d("End <= ");
        } else if (sceneOwner == null) {
            list = new ArrayList();
            OnDidUpdateScenes(sceneOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.d("End <= ");
        } else if (sceneOwner.getProductType() != GizWifiDeviceType.GizDeviceCenterControl) {
            list = new ArrayList();
            OnDidUpdateScenes(sceneOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else {
            addSceneRequestOwner = sceneOwner;
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1331);
                obj.put("sn", sn);
                obj.put("mac", sceneOwner.getMacAddress());
                obj.put("did", sceneOwner.getDid());
                obj.put("productKey", sceneOwner.getProductKey());
                obj.put("sceneName", sceneName);
                JSONArray sceneItem = getAddSceneJsonArray(sceneOwner, sceneItems);
                obj.put("sceneItems", sceneItem);
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (sceneOwner.isLAN) {
                makeTimer(9000, 1332, sn);
            } else {
                makeTimer(20000, 1332, sn);
            }

            SDKLog.d("End <= ");
        }
    }

    public static void removeScene(GizWifiDevice sceneOwner, GizDeviceScene scene) {
        SDKLog.a("Start => sceneOwner: " + (sceneOwner == null ? "null" : sceneOwner.simpleInfoMasking()) + ", scene: " + (scene == null ? "null" : scene.simpleInfoMasking()));
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            OnDidUpdateScenes(sceneOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.a("End <= ");
        } else if (sceneOwner == null) {
            list = new ArrayList();
            OnDidUpdateScenes(sceneOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else if (sceneOwner.getProductType() != GizWifiDeviceType.GizDeviceCenterControl) {
            list = new ArrayList();
            OnDidUpdateScenes(sceneOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else if (scene == null) {
            list = new ArrayList();
            OnDidUpdateScenes(sceneOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else {
            removeSceneRequestOwner = sceneOwner;
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1335);
                obj.put("sn", sn);
                obj.put("mac", sceneOwner.getMacAddress());
                obj.put("did", sceneOwner.getDid());
                obj.put("productKey", sceneOwner.getProductKey());
                obj.put("sceneID", scene.getSceneID());
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (sceneOwner.isLAN) {
                makeTimer(9000, 1336, sn);
            } else {
                makeTimer(20000, 1336, sn);
            }

            SDKLog.a("End <= ");
        }
    }

    public static void updateScenes(GizWifiDevice sceneOwner) {
        SDKLog.a("Start => sceneOwner: " + (sceneOwner == null ? "null" : sceneOwner.simpleInfoMasking()));
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            OnDidUpdateScenes(sceneOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.d("End <= ");
        } else if (sceneOwner == null) {
            list = new ArrayList();
            OnDidUpdateScenes(sceneOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.d("End <= ");
        } else if (sceneOwner.getProductType() != GizWifiDeviceType.GizDeviceCenterControl) {
            list = new ArrayList();
            OnDidUpdateScenes(sceneOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else {
            updateScenesRequestOwner = sceneOwner;
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1337);
                obj.put("sn", sn);
                obj.put("mac", sceneOwner.getMacAddress());
                obj.put("did", sceneOwner.getDid());
                obj.put("productKey", sceneOwner.getProductKey());
            } catch (JSONException var4) {
                SDKLog.e(var4.toString());
                var4.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (sceneOwner.isLAN) {
                makeTimer(9000, 1338, sn);
            } else {
                makeTimer(20000, 1338, sn);
            }

            SDKLog.a("End <= ");
        }
    }

    private static void OnDidUpdateScenes(GizWifiDevice sceneOwner, GizWifiErrorCode result, List<GizDeviceScene> sceneList) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", sceneOwner: " + (sceneOwner == null ? "null" : sceneOwner.simpleInfoMasking()) + ", sceneList: " + listMasking(sceneList));
        if (mListener != null) {
            mListener.didUpdateScenes(sceneOwner, result, sceneList);
        }

        SDKLog.d("Callback end");
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

    private static void handleTimeoutMessage(Message msg) {
        int cmd = (Integer)msg.obj;
        List<GizDeviceScene> list = new ArrayList();
        switch(cmd) {
            case 1332:
                timeoutHandler.removeMessages(msg.what);
                OnDidUpdateScenes(addSceneRequestOwner, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1336:
                timeoutHandler.removeMessages(msg.what);
                OnDidUpdateScenes(removeSceneRequestOwner, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1338:
                timeoutHandler.removeMessages(msg.what);
                OnDidUpdateScenes(updateScenesRequestOwner, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
        }

    }

    private static void handleReceiveMessage(int cmd, JSONObject obj, int sn, GizWifiDevice deviceOwner) throws JSONException {
        int errorCode;
        JSONArray scenes;
        List<GizDeviceScene>  list;
        String sceneID = null;
        switch(cmd) {
            case 1332:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (errorCode != 0) {
                    timeoutHandler.removeMessages(sn);
                    list = new ArrayList();
                    OnDidUpdateScenes(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    messageQueue.add(sn);
                }
                break;
            case 1336:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                 sceneID = obj.has("sceneID") ? obj.getString("sceneID") : "";
                if (errorCode != 0) {
                    timeoutHandler.removeMessages(sn);
                    list = new ArrayList();
                    OnDidUpdateScenes(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    messageQueue.add(sn);
                    Iterator var20 = getValidSceneListByOwner(deviceOwner).iterator();

                    GizDeviceScene scene;
                    do {
                        if (!var20.hasNext()) {
                            return;
                        }

                        scene = (GizDeviceScene)var20.next();
                    } while(!scene.getSceneID().equals(sceneID));

                    scene.setIsValid(false);
                }
                break;
            case 1338:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                scenes = obj.has("scenes") ? obj.getJSONArray("scenes") : null;
                timeoutHandler.removeMessages(sn);
                if (errorCode != 0) {
                    list = new ArrayList();
                    OnDidUpdateScenes(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    saveSceneList(deviceOwner, scenes);
                    getAllSceneItemsJson(deviceOwner.getMacAddress(), deviceOwner.getDid(), deviceOwner.getProductKey());
                    OnDidUpdateScenes(deviceOwner, GizWifiErrorCode.valueOf(errorCode), getValidSceneListByOwner(deviceOwner));
                }
                break;
            case 1348:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                scenes = obj.has("scenes") ? obj.getJSONArray("scenes") : null;
                if (errorCode == 0 && scenes != null) {
                    for(int i = 0; i < scenes.length(); ++i) {
                        JSONObject jsonOb = (JSONObject)scenes.get(i);
                        if (jsonOb.has("sceneID")) {
                            sceneID = jsonOb.getString("sceneID");
                        }

                        JSONArray sceneItems = null;
                        if (deviceOwner != null && jsonOb.has("sceneItems")) {
                            sceneItems = jsonOb.getJSONArray("sceneItems");
                        }

                        Iterator var10 = getValidSceneListByOwner(deviceOwner).iterator();

                        while(var10.hasNext()) {
                            GizDeviceScene object = (GizDeviceScene)var10.next();
                            if (object.getSceneID().equals(sceneID)) {
                                boolean update = object.saveSceneItems(deviceOwner, sceneItems);
                                if (update) {
                                    object.OnDidUpdateSceneItems(object, GizWifiErrorCode.GIZ_SDK_SUCCESS, object.getSceneItemList());
                                }
                                break;
                            }
                        }
                    }

                    return;
                } else {
                    SDKLog.d("cmd 1348: " + GizWifiErrorCode.valueOf(errorCode));
                    break;
                }
            case 2023:
                scenes = obj.has("scenes") ? obj.getJSONArray("scenes") : null;
                Iterator var5 = messageQueue.iterator();

                while(var5.hasNext()) {
                    Integer waitsn = (Integer)var5.next();
                    if (timeoutHandler.hasMessages(waitsn)) {
                        timeoutHandler.removeMessages(waitsn);
                    }
                }

                saveSceneList(deviceOwner, scenes);
                getAllSceneItemsJson(deviceOwner.getMacAddress(), deviceOwner.getDid(), deviceOwner.getProductKey());
                OnDidUpdateScenes(deviceOwner, GizWifiErrorCode.GIZ_SDK_SUCCESS, getValidSceneListByOwner(deviceOwner));
        }

    }

    private static void getAllSceneItemsJson(String mac, String did, String pk) {
        int sn = Utils.getSn();
        JSONObject obj = new JSONObject();

        try {
            obj.put("cmd", 1347);
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

    private static void saveSceneList(GizWifiDevice owner, JSONArray jsonArray) throws JSONException {
        if (owner != null && jsonArray != null) {
            List<GizDeviceScene> newList = new ArrayList();
            List<GizDeviceScene> cacheList = new ArrayList();
            boolean isHasOwner = false;
            Iterator var5 = sceneListMap.keySet().iterator();

            while(var5.hasNext()) {
                GizWifiDevice key = (GizWifiDevice)var5.next();
                if (owner != null && key.getMacAddress().equals(owner.getMacAddress()) && key.getDid().equals(owner.getDid()) && key.getProductKey().equals(owner.getProductKey())) {
                    isHasOwner = true;
                    cacheList = (List)sceneListMap.get(key);
                    break;
                }
            }

            var5 = ((List)cacheList).iterator();

            while(var5.hasNext()) {
                GizDeviceScene object = (GizDeviceScene)var5.next();
                object.setIsValid(false);
            }

            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonOb = (JSONObject)jsonArray.get(i);
                String sceneID = null;
                if (jsonOb.has("sceneID")) {
                    sceneID = jsonOb.getString("sceneID");
                }

                String sceneName = null;
                if (jsonOb.has("sceneName")) {
                    sceneName = jsonOb.getString("sceneName");
                }

                if (!isHasOwner) {
                    GizDeviceScene object = new GizDeviceScene();
                    object.setSceneID(sceneID);
                    object.setSceneName(sceneName);
                    object.setSceneOwner(owner);
                    object.setIsValid(true);
                    newList.add(object);
                } else {
                    boolean isHasOb = false;
                    Iterator var10 = ((List)cacheList).iterator();

                    while(var10.hasNext()) {
                        GizDeviceScene object = (GizDeviceScene)var10.next();
                        if (sceneID.equals(object.getSceneID())) {
                            object.setSceneName(sceneName);
                            object.setSceneOwner(owner);
                            object.setIsValid(true);
                            object.setSceneItemList((List)null);
                            isHasOb = true;
                            break;
                        }
                    }

                    if (!isHasOb) {
                        GizDeviceScene object = new GizDeviceScene();
                        object.setSceneID(sceneID);
                        object.setSceneName(sceneName);
                        object.setSceneOwner(owner);
                        object.setIsValid(true);
                        ((List)cacheList).add(object);
                    }
                }
            }

            if (!isHasOwner && newList.size() > 0) {
                sceneListMap.put(owner, newList);
            }

        }
    }

    private static JSONArray getAddSceneJsonArray(GizWifiDevice owner, List<GizDeviceSceneItem> sceneItems) {
        if (owner != null && sceneItems != null) {
            if (owner.getProductType() != GizWifiDeviceType.GizDeviceCenterControl) {
                SDKLog.d("sceneOwner is not " + GizWifiDeviceType.GizDeviceCenterControl + ", ignore it");
                return null;
            } else {
                GizWifiCentralControlDevice deviceOwner = (GizWifiCentralControlDevice)owner;
                List<GizWifiDevice> subDevices = deviceOwner.getSubDeviceList();
                List<GizDeviceGroup> groups = GizDeviceGroupCenter.getGroupListGateway(deviceOwner);
                JSONArray sceneItems_json = new JSONArray();

                try {
                    Iterator var6 = sceneItems.iterator();

                    while(true) {
                        while(var6.hasNext()) {
                            GizDeviceSceneItem sceneItem = (GizDeviceSceneItem)var6.next();
                            int sceneItemType = sceneItem.getSceneItemType().ordinal();
                            int delay = sceneItem.getDelayTime();
                            GizWifiDevice device = sceneItem.getDevice();
                            GizDeviceGroup group = sceneItem.getGroup();
                            ConcurrentHashMap<String, Object> data = sceneItem.getData();
                            if (sceneItem.getSceneItemType() == GizSceneItemType.GizSceneItemDelay) {
                                if (delay <= 0) {
                                    SDKLog.d("sceneItem delay " + delay + " is invalid, ignore it");
                                } else {
                                    JSONObject sceneItem_json = new JSONObject();
                                    sceneItem_json.put("sceneItemType", sceneItemType);
                                    sceneItem_json.put("delay", delay);
                                    sceneItems_json.put(sceneItem_json);
                                }
                            } else {
                                boolean has;
                                Iterator var14;
                                JSONObject sceneItem_json;
                                JSONObject data_json;
                                if (sceneItem.getSceneItemType() == GizSceneItemType.GizSceneItemDevice) {
                                    if (data != null && data.size() != 0) {
                                        if (device == null) {
                                            SDKLog.d("sceneItem device is null, ignore it");
                                        } else {
                                            has = false;
                                            var14 = subDevices.iterator();

                                            while(var14.hasNext()) {
                                                GizWifiDevice object = (GizWifiDevice)var14.next();
                                                if (object.getMacAddress().equals(device.getMacAddress()) && object.getProductKey().equals(device.getProductKey()) && object.getDid().equals(device.getDid())) {
                                                    has = true;
                                                    break;
                                                }
                                            }

                                            if (!has) {
                                                SDKLog.d("sceneItem device is not in subDevice list, ignore it");
                                            } else {
                                                sceneItem_json = new JSONObject();
                                                sceneItem_json.put("sceneItemType", sceneItemType);
                                                data_json = new JSONObject();
                                                data_json.put("mac", device.getMacAddress());
                                                data_json.put("productKey", device.getProductKey());
                                                data_json.put("did", device.getDid());
                                                sceneItem_json.put("device", data_json);
                                                 data_json = new JSONObject();
                                                Iterator iter = data.entrySet().iterator();

                                                while(iter.hasNext()) {
                                                    Entry<String, Object> entry = (Entry)iter.next();
                                                    String key = (String)entry.getKey();
                                                    Object value = entry.getValue();
                                                    if (value instanceof byte[]) {
                                                        byte[] byts = (byte[])((byte[])value);
                                                        data_json.put(key, GizWifiBinary.encode(byts));
                                                    } else {
                                                        data_json.put(key, value);
                                                    }
                                                }

                                                sceneItem_json.put("data", data_json);
                                                sceneItems_json.put(sceneItem_json);
                                            }
                                        }
                                    } else {
                                        SDKLog.d("sceneItem data is empty, ignore it");
                                    }
                                } else if (sceneItem.getSceneItemType() == GizSceneItemType.GizSceneItemGroup) {
                                    if (data != null && data.size() != 0) {
                                        if (group == null) {
                                            SDKLog.d("sceneItem group is null, ignore it");
                                        } else {
                                            has = false;
                                            var14 = groups.iterator();

                                            while(var14.hasNext()) {
                                                GizDeviceGroup object = (GizDeviceGroup)var14.next();
                                                if (object.getGroupID().equals(group.getGroupID())) {
                                                    has = true;
                                                    break;
                                                }
                                            }

                                            if (!has) {
                                                SDKLog.d("sceneItem group is not in group list, ignore it");
                                            } else {
                                                sceneItem_json = new JSONObject();
                                                sceneItem_json.put("sceneItemType", sceneItemType);
                                                sceneItem_json.put("groupID", group.getGroupID());
                                                data_json = new JSONObject();
                                                Iterator iter = data.entrySet().iterator();

                                                while(iter.hasNext()) {
                                                    Entry<String, Object> entry = (Entry)iter.next();
                                                    String key = (String)entry.getKey();
                                                    Object value = entry.getValue();
                                                    if (value instanceof byte[]) {
                                                        byte[] byts = (byte[])((byte[])value);
                                                        data_json.put(key, GizWifiBinary.encode(byts));
                                                    } else {
                                                        data_json.put(key, value);
                                                    }
                                                }

                                                sceneItem_json.put("data", data_json);
                                                sceneItems_json.put(sceneItem_json);
                                            }
                                        }
                                    } else {
                                        SDKLog.d("sceneItem data is empty, ignore it");
                                    }
                                }
                            }
                        }

                        return sceneItems_json;
                    }
                } catch (JSONException var22) {
                    var22.printStackTrace();
                    return sceneItems_json;
                }
            }
        } else {
            SDKLog.d("owner: " + owner + ", sceneItems:" + sceneItems);
            return null;
        }
    }
}
