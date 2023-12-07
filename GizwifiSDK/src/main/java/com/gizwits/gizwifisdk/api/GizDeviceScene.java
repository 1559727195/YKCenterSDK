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
import com.gizwits.gizwifisdk.enumration.GizDeviceSceneStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceSceneListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceScene implements Parcelable {
    private static List<ConcurrentHashMap<String, Integer>> messageQueue = new ArrayList();
    private List<GizDeviceSceneItem> sceneItemList = new CopyOnWriteArrayList();
    private static final int MSG_RECV = 5;
    private GizDeviceSceneListener mListener;
    private GizWifiDevice sceneOwner;
    private String sceneID;
    private String sceneName;
    private boolean isValid = true;
    protected Handler timeoutHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GizDeviceScene.this.handleTimeoutMessage(msg);
        }
    };
    Handler messageHandler = new Handler(Looper.getMainLooper()) {
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
                        if (!GizDeviceScene.this.sceneOwner.equals(mac, productKey, did)) {
                            SDKLog.d("<mac: " + mac + ", productKey: " + productKey + ", did: " + did + ">is not the owner");
                        }

                        if (cmd > 2000) {
                            sn = 0;
                        }

                        GizDeviceScene.this.handleReceiveMessage(cmd, obj, sn);
                    } catch (NumberFormatException var9) {
                        var9.printStackTrace();
                    } catch (JSONException var10) {
                        var10.printStackTrace();
                    }
                default:
            }
        }
    };
    public static final Creator<GizDeviceScene> CREATOR = new Creator<GizDeviceScene>() {
        public GizDeviceScene createFromParcel(Parcel source) {
            GizDeviceScene group = new GizDeviceScene();
            group.sceneID = source.readString();
            group.sceneOwner = (GizWifiDevice)source.readParcelable(GizWifiDevice.class.getClassLoader());
            List<GizDeviceScene> list = GizDeviceSceneCenter.getValidSceneListByOwner(group.sceneOwner);
            GizDeviceScene gp = null;
            Iterator var5 = list.iterator();

            while(var5.hasNext()) {
                GizDeviceScene gizDeviceGroup = (GizDeviceScene)var5.next();
                if (gizDeviceGroup != null && gizDeviceGroup.getSceneID().equals(group.sceneID)) {
                    gp = gizDeviceGroup;
                    break;
                }
            }

            return gp;
        }

        public GizDeviceScene[] newArray(int size) {
            return null;
        }
    };

    public GizDeviceScene() {
    }

    public String toString() {
        return "GizDeviceScene [sceneID=" + this.sceneID + ", sceneOwner=" + this.sceneOwner + ", sceneName=" + this.sceneName + ", sceneItemList=" + this.sceneItemList + ", mListener=" + this.mListener + "]";
    }

    protected String simpleInfoMasking() {
        return "GizDeviceScene [sceneID=" + this.sceneID + ", isValid=" + this.isValid + ", sceneOwner=" + (this.sceneOwner == null ? "sceneOwner" : this.sceneOwner.moreSimpleInfoMasking()) + "]";
    }

    protected String infoMasking() {
        String info = this.simpleInfoMasking() + ", listener=" + this.mListener + " ";
        return info + "->[sceneItemList=" + listMasking(this.sceneItemList) + "]";
    }

    protected static String listMasking(List<GizDeviceSceneItem> list) {
        String masking = "{size= " + (list == null ? "0" : list.size()) + ", ";
        if (list != null) {
            for(Iterator var2 = list.iterator(); var2.hasNext(); masking = masking + ", ") {
                GizDeviceSceneItem object = (GizDeviceSceneItem)var2.next();
                masking = masking + "[" + object.infoMasking() + "]";
            }
        }

        return masking.substring(0, masking.length() - 2) + "}";
    }

    public String getSceneID() {
        return this.sceneID;
    }

    protected void setSceneID(String sceneID) {
        this.sceneID = sceneID;
    }

    public GizWifiDevice getSceneOwner() {
        return this.sceneOwner;
    }

    protected void setSceneOwner(GizWifiDevice sceneOwner) {
        this.sceneOwner = sceneOwner;
    }

    public String getSceneName() {
        return this.sceneName;
    }

    protected void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public List<GizDeviceSceneItem> getSceneItemList() {
        SDKLog.a("Start => ");
        SDKLog.d("cache itemlist: " + listMasking(this.sceneItemList));
        SDKLog.a("End <= ");
        return this.sceneItemList;
    }

    protected void setSceneItemList(List<GizDeviceSceneItem> itemList) {
        List<GizDeviceSceneItem> mysceneItemList = new ArrayList();
        if (itemList == null) {
            Iterator var3 = this.sceneItemList.iterator();

            while(var3.hasNext()) {
                GizDeviceSceneItem gizDeviceSceneItem = (GizDeviceSceneItem)var3.next();
                switch(gizDeviceSceneItem.getSceneItemType()) {
                    case GizSceneItemDevice:
                        if (gizDeviceSceneItem.getDevice().getNetStatus() != GizWifiDeviceNetStatus.GizDeviceUnavailable) {
                            mysceneItemList.add(gizDeviceSceneItem);
                        }
                        break;
                    case GizSceneItemGroup:
                        if (gizDeviceSceneItem.getGroup().getIsValid()) {
                            mysceneItemList.add(gizDeviceSceneItem);
                        }
                        break;
                    case GizSceneItemDelay:
                        mysceneItemList.add(gizDeviceSceneItem);
                }
            }
        } else {
            mysceneItemList.addAll(itemList);
        }

        for(int i = 0; i < this.sceneItemList.size(); ++i) {
            this.sceneItemList.remove(i--);
        }

        this.sceneItemList.addAll(mysceneItemList);
    }

    protected boolean getIsValid() {
        return this.isValid;
    }

    protected void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public void setListener(GizDeviceSceneListener listener) {
        SDKLog.a("Start => this: " + this.infoMasking() + "listener: " + (listener == null ? "null" : listener));
        this.mListener = listener;
        SDKLog.a("End <= ");
    }

    public void editSceneInfo(String sceneName) {
        SDKLog.d("Start => this: " + this.infoMasking() + ", sceneName: " + sceneName);
        if (!Constant.ishandshake) {
            this.OnDidUpdateSceneInfo(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
            SDKLog.d("End <= ");
        } else if (this.sceneOwner == null) {
            this.OnDidUpdateSceneInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
            SDKLog.a("End <= ");
        } else if (sceneName == null) {
            this.OnDidUpdateSceneInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
            SDKLog.d("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1333);
                obj.put("sn", sn);
                obj.put("mac", this.sceneOwner.getMacAddress());
                obj.put("did", this.sceneOwner.getDid());
                obj.put("productKey", this.sceneOwner.getProductKey());
                obj.put("sceneID", this.sceneID);
                obj.put("sceneName", sceneName);
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            this.sendMes2Demo(obj);
            if (this.sceneOwner.isLAN) {
                this.addMessage(this.timeoutHandler, 9000, 1334, sn, 0);
            } else {
                this.addMessage(this.timeoutHandler, 20000, 1334, sn, 0);
            }

            SDKLog.d("End <= ");
        }
    }

    public void editSceneItems(List<GizDeviceSceneItem> sceneItems) {
        SDKLog.d("Start => this: " + this.infoMasking() + ", sceneItems: " + (sceneItems == null ? "null" : sceneItems.size()));
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            this.OnDidUpdateSceneItems(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.d("End <= ");
        } else if (this.sceneOwner == null) {
            list = new ArrayList();
            this.OnDidUpdateSceneItems(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else if (sceneItems != null && sceneItems.size() != 0) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1339);
                obj.put("sn", sn);
                obj.put("mac", this.sceneOwner.getMacAddress());
                obj.put("did", this.sceneOwner.getDid());
                obj.put("productKey", this.sceneOwner.getProductKey());
                obj.put("sceneID", this.sceneID);
                if (sceneItems != null) {
                    JSONArray sceneItem = this.getAddSceneJsonArray(sceneItems);
                    obj.put("sceneItems", sceneItem);
                }
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            this.sendMes2Demo(obj);
            if (this.sceneOwner.isLAN) {
                this.addMessage(this.timeoutHandler, 9000, 1340, sn, 0);
            } else {
                this.addMessage(this.timeoutHandler, 20000, 1340, sn, 0);
            }

            SDKLog.d("End <= ");
        } else {
            list = new ArrayList();
            this.OnDidUpdateSceneItems(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.d("End <= ");
        }
    }

    public void updateSceneItems() {
        SDKLog.d("Start => this: " + this.infoMasking());
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            this.OnDidUpdateSceneItems(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.d("End <= ");
        } else if (this.sceneOwner == null) {
            list = new ArrayList();
            this.OnDidUpdateSceneItems(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1341);
                obj.put("sn", sn);
                obj.put("mac", this.sceneOwner.getMacAddress());
                obj.put("did", this.sceneOwner.getDid());
                obj.put("productKey", this.sceneOwner.getProductKey());
                obj.put("sceneID", this.sceneID);
            } catch (JSONException var4) {
                SDKLog.e(var4.toString());
                var4.printStackTrace();
            }

            this.sendMes2Demo(obj);
            if (this.sceneOwner.isLAN) {
                this.addMessage(this.timeoutHandler, 9000, 1342, sn, 0);
            } else {
                this.addMessage(this.timeoutHandler, 20000, 1342, sn, 0);
            }

            SDKLog.d("End <= ");
        }
    }

    public void executeScene(boolean startup, int sn) {
        SDKLog.d("Start => this: " + this.infoMasking() + ", startup: " + startup + ", sn: " + sn);
        if (!Constant.ishandshake) {
            this.OnDidExecuteScene(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, sn);
            SDKLog.d("End <= ");
        } else if (this.sceneOwner == null) {
            this.OnDidExecuteScene(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, sn);
            SDKLog.a("End <= ");
        } else {
            int sdksn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1343);
                obj.put("sn", sdksn);
                obj.put("mac", this.sceneOwner.getMacAddress());
                obj.put("did", this.sceneOwner.getDid());
                obj.put("productKey", this.sceneOwner.getProductKey());
                obj.put("sceneID", this.sceneID);
                obj.put("sceneStatus", startup);
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            this.sendMes2Demo(obj);
            if (this.sceneOwner.isLAN) {
                this.addMessage(this.timeoutHandler, 9000, 1344, sdksn, sn);
            } else {
                this.addMessage(this.timeoutHandler, 20000, 1344, sdksn, sn);
            }

            SDKLog.d("End <= ");
        }
    }

    public void updateSceneStatus() {
        SDKLog.d("Start => this: " + this.infoMasking());
        if (!Constant.ishandshake) {
            this.OnDidUpdateSceneStatus(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, (GizDeviceSceneStatus)null);
            SDKLog.d("End <= ");
        } else if (this.sceneOwner == null) {
            this.OnDidUpdateSceneStatus(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (GizDeviceSceneStatus)null);
            SDKLog.a("End <= ");
        } else {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1345);
                obj.put("sn", sn);
                obj.put("mac", this.sceneOwner.getMacAddress());
                obj.put("did", this.sceneOwner.getDid());
                obj.put("productKey", this.sceneOwner.getProductKey());
                obj.put("sceneID", this.sceneID);
            } catch (JSONException var4) {
                SDKLog.e(var4.toString());
                var4.printStackTrace();
            }

            this.sendMes2Demo(obj);
            if (this.sceneOwner.isLAN) {
                this.addMessage(this.timeoutHandler, 9000, 1346, sn, 0);
            } else {
                this.addMessage(this.timeoutHandler, 20000, 1346, sn, 0);
            }

            SDKLog.d("End <= ");
        }
    }

    protected void OnDidUpdateSceneItems(GizDeviceScene scene, GizWifiErrorCode result, List<GizDeviceSceneItem> sceneItemList) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", scene: " + (scene == null ? "null" : scene.infoMasking()) + ", sceneItemList: " + (sceneItemList == null ? "null" : sceneItemList.size()));
        if (this.mListener != null) {
            this.mListener.didUpdateSceneItems(scene, result, sceneItemList);
            SDKLog.d("Callback end");
        }

    }

    private void OnDidUpdateSceneStatus(GizDeviceScene scene, GizWifiErrorCode result, GizDeviceSceneStatus status) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", scene: " + (scene == null ? "null" : scene.infoMasking()) + ", status: " + (status == null ? "null" : status.name()));
        if (this.mListener != null) {
            this.mListener.didUpdateSceneStatus(scene, result, status);
            SDKLog.d("Callback end");
        }

    }

    private void OnDidUpdateSceneInfo(GizDeviceScene scene, GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", scene: " + (scene == null ? "null" : scene.infoMasking()));
        if (this.mListener != null) {
            this.mListener.didUpdateSceneInfo(scene, result);
            SDKLog.d("Callback end");
        }

    }

    private void OnDidExecuteScene(GizDeviceScene scene, GizWifiErrorCode result, int sn) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", scene: " + (scene == null ? "null" : scene.infoMasking()) + ", sn: " + sn);
        if (this.mListener != null) {
            this.mListener.didExecuteScene(scene, result, sn);
            SDKLog.d("Callback end");
        }

    }

    private void sendMes2Demo(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    protected void handleTimeoutMessage(Message msg) {
        int sdk_sn = msg.what;
        int cmd = (Integer)msg.obj;
        List<GizDeviceSceneItem> list = new ArrayList();
        switch(cmd) {
            case 1334:
                this.removeMessage(this.timeoutHandler, cmd, sdk_sn);
                this.OnDidUpdateSceneInfo(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT);
            case 1335:
            case 1336:
            case 1337:
            case 1338:
            case 1339:
            case 1341:
            case 1343:
            case 1345:
            default:
                break;
            case 1340:
                this.removeMessage(this.timeoutHandler, cmd, sdk_sn);
                this.OnDidUpdateSceneItems(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1342:
                this.removeMessage(this.timeoutHandler, cmd, sdk_sn);
                this.OnDidUpdateSceneItems(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1344:
                int app_sn = this.removeMessage(this.timeoutHandler, cmd, sdk_sn);
                this.OnDidExecuteScene(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, app_sn);
                break;
            case 1346:
                this.removeMessage(this.timeoutHandler, cmd, sdk_sn);
                this.OnDidUpdateSceneStatus(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, (GizDeviceSceneStatus)null);
        }

    }

    protected void handleReceiveMessage(int cmd, JSONObject obj, int sn) throws JSONException {
        String sceneName;
        int errorCode;
        int sceneStatus;
        switch(cmd) {
            case 1334:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (errorCode != 0) {
                    this.removeMessage(this.timeoutHandler, cmd, sn);
                    this.OnDidUpdateSceneInfo(this, GizWifiErrorCode.valueOf(errorCode));
                }
                break;
            case 1340:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (errorCode != 0) {
                    this.removeMessage(this.timeoutHandler, cmd, sn);
                    List<GizDeviceSceneItem> list = new ArrayList();
                    this.OnDidUpdateSceneItems(this, GizWifiErrorCode.valueOf(errorCode), list);
                }
                break;
            case 1342:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                JSONArray sceneItems = obj.has("sceneItems") ? obj.getJSONArray("sceneItems") : null;
                this.removeMessage(this.timeoutHandler, cmd, sn);
                if (errorCode != 0) {
                    List<GizDeviceSceneItem> list = new ArrayList();
                    this.OnDidUpdateSceneItems(this, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    this.saveSceneItems(this.sceneOwner, sceneItems);
                    this.OnDidUpdateSceneItems(this, GizWifiErrorCode.valueOf(errorCode), this.sceneItemList);
                }
                break;
            case 1344:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                sceneStatus = this.removeMessage(this.timeoutHandler, cmd, sn);
                this.OnDidExecuteScene(this, GizWifiErrorCode.valueOf(errorCode), sceneStatus);
                break;
            case 1346:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                sceneStatus = obj.has("sceneStatus") ? obj.getInt("sceneStatus") : -1;
                this.removeMessage(this.timeoutHandler, cmd, sn);
                this.OnDidUpdateSceneStatus(this, GizWifiErrorCode.valueOf(errorCode), GizDeviceSceneStatus.valueOf(sceneStatus));
                break;
            case 2024:
                sceneName = obj.has("did") ? obj.getString("did") : "";
                String mac = obj.has("mac") ? obj.getString("mac") : "";
                String productKey = obj.has("productKey") ? obj.getString("productKey") : "";
                GizWifiDevice myOwnerDevice = SDKEventManager.getInstance().findDeviceInTotalDeviceList(sceneName, mac, productKey);
                this.removeMessage(this.timeoutHandler, cmd, 0);
                if (obj.has("sceneItems")) {
                    JSONArray jsonArray = obj.getJSONArray("sceneItems");
                    this.saveSceneItems(myOwnerDevice, jsonArray);
                }

                this.OnDidUpdateSceneItems(this, GizWifiErrorCode.GIZ_SDK_SUCCESS, this.sceneItemList);
                break;
            case 2025:
                errorCode = obj.has("sceneStatus") ? obj.getInt("sceneStatus") : -1;
                this.removeMessage(this.timeoutHandler, cmd, 0);
                this.OnDidUpdateSceneStatus(this, GizWifiErrorCode.GIZ_SDK_SUCCESS, GizDeviceSceneStatus.valueOf(errorCode));
                break;
            case 2032:
                sceneName = obj.has("sceneName") ? obj.getString("sceneName") : null;
                this.removeMessage(this.timeoutHandler, cmd, 0);
                if (sceneName != null) {
                    this.setSceneName(sceneName);
                }

                this.OnDidUpdateSceneInfo(this, GizWifiErrorCode.GIZ_SDK_SUCCESS);
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
        if (cmd == 2025) {
            var4 = messageQueue.iterator();

            while(var4.hasNext()) {
                sn = (ConcurrentHashMap)var4.next();
                if ((Integer)sn.get("cmd") == 1344) {
                    cmd = 1344;
                    sdk_sn = (Integer)sn.get("sdkSn");
                    break;
                }
            }
        } else if (cmd == 2024) {
            var4 = messageQueue.iterator();

            while(var4.hasNext()) {
                sn = (ConcurrentHashMap)var4.next();
                if ((Integer)sn.get("cmd") == 1340) {
                    cmd = 1340;
                    sdk_sn = (Integer)sn.get("sdkSn");
                    break;
                }
            }
        } else if (cmd == 2032) {
            var4 = messageQueue.iterator();

            while(var4.hasNext()) {
                sn = (ConcurrentHashMap)var4.next();
                if ((Integer)sn.get("cmd") == 1334) {
                    cmd = 1334;
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

    private JSONArray getAddSceneJsonArray(List<GizDeviceSceneItem> sceneItems) {
        if (sceneItems == null) {
            return null;
        } else {
            JSONArray myjson = new JSONArray();

            try {
                Iterator var3 = sceneItems.iterator();

                while(true) {
                    while(true) {
                        GizDeviceSceneItem gizDeviceSceneItem;
                        do {
                            if (!var3.hasNext()) {
                                return myjson;
                            }

                            gizDeviceSceneItem = (GizDeviceSceneItem)var3.next();
                        } while(gizDeviceSceneItem == null);

                        int delayTime = gizDeviceSceneItem.getDelayTime();
                        int ordinal = gizDeviceSceneItem.getSceneItemType().ordinal();
                        JSONObject myjsonobj;
                        if (delayTime == 0) {
                            myjsonobj = new JSONObject();
                            ConcurrentHashMap<String, Object> data = gizDeviceSceneItem.getData();
                            if (data != null) {
                                Iterator<Entry<String, Object>> iter = data.entrySet().iterator();
                                JSONObject mobj = new JSONObject();

                                while(iter.hasNext()) {
                                    Entry<String, Object> entry = (Entry)iter.next();
                                    String key = (String)entry.getKey();
                                    Object val = entry.getValue();
                                    if (val instanceof byte[]) {
                                        byte[] byts = (byte[])((byte[])val);
                                        mobj.put(key, GizWifiBinary.encode(byts));
                                    } else {
                                        mobj.put(key, val);
                                    }
                                }

                                myjsonobj.put("data", mobj);
                            }

                            GizWifiDevice device = gizDeviceSceneItem.getDevice();
                            String groupID = gizDeviceSceneItem.getGroup() == null ? null : gizDeviceSceneItem.getGroup().getGroupID();
                            if (!TextUtils.isEmpty(groupID)) {
                                myjsonobj.put("groupID", groupID);
                            }

                            if (device != null) {
                                myjsonobj.put("groupID", (Object)null);
                                JSONObject ob = new JSONObject();
                                ob.put("mac", device.getMacAddress());
                                ob.put("did", device.getDid());
                                ob.put("productKey", device.getProductKey());
                                myjsonobj.put("device", ob);
                            }

                            myjsonobj.put("sceneItemType", ordinal);
                            myjson.put(myjsonobj);
                        } else {
                            myjsonobj = new JSONObject();
                            myjsonobj.put("delay", delayTime);
                            myjsonobj.put("sceneItemType", ordinal);
                            myjson.put(myjsonobj);
                        }
                    }
                }
            } catch (JSONException var15) {
                var15.printStackTrace();
                return null;
            }
        }
    }

    protected boolean saveSceneItems(GizWifiDevice owner, JSONArray jsonList) throws JSONException {
        if (jsonList == null) {
            SDKLog.d("jsonList is null, ignore it");
            return false;
        } else if (owner != null && owner.getProductType() == GizWifiDeviceType.GizDeviceCenterControl) {
            if (owner.getMacAddress().equals(this.sceneOwner.getMacAddress()) && owner.getDid().equals(this.sceneOwner.getDid()) && owner.getProductKey().equals(this.sceneOwner.getProductKey())) {
                boolean update = false;
                GizWifiCentralControlDevice sceneOwner = (GizWifiCentralControlDevice)owner;
                List<GizDeviceSceneItem> tempList = new ArrayList();

                for(int i = 0; i < jsonList.length(); ++i) {
                    JSONObject jsonOb = (JSONObject)jsonList.get(i);
                    int sceneItemType = jsonOb.getInt("sceneItemType");
                    int delay = jsonOb.has("delay") ? jsonOb.getInt("delay") : -1;
                    GizWifiDevice device = null;
                    String groupID;
                    if (jsonOb.has("device")) {
                        JSONObject device_json = (JSONObject)jsonOb.get("device");
                        groupID = device_json.has("mac") ? device_json.getString("mac") : null;
                        String did = device_json.has("did") ? device_json.getString("did") : null;
                        String productKey = device_json.has("productKey") ? device_json.getString("productKey") : null;
                        device = SDKEventManager.getInstance().findDeviceInTotalDeviceList(did, groupID, productKey);
                    }

                    GizDeviceGroup group = null;
                    List maps_v4a1_v4a2;
                    if (jsonOb.has("groupID")) {
                        groupID = jsonOb.getString("groupID");
                        maps_v4a1_v4a2 = GizDeviceGroupCenter.getTotalGroupListByOwner(sceneOwner);
                        Iterator var20 = maps_v4a1_v4a2.iterator();

                        while(var20.hasNext()) {
                            GizDeviceGroup gizDeviceGroup = (GizDeviceGroup)var20.next();
                            if (gizDeviceGroup.getGroupID().equals(groupID)) {
                                group = gizDeviceGroup;
                                break;
                            }
                        }
                    }

                    GizDeviceSceneItem item = null;
                    switch(sceneItemType) {
                        case 0:
                            if (device == null) {
                                SDKLog.d("sceneItem device is null, ignore it");
                            } else {
                                maps_v4a1_v4a2 = device != null ? device.parseDeviceStatusForKey(jsonOb, "attrStatus") : null;
                                ConcurrentHashMap<String, Object> status = maps_v4a1_v4a2 != null ? (ConcurrentHashMap)((ConcurrentHashMap)maps_v4a1_v4a2.get(1)).get("data") : null;
                                if (status != null && status.size() != 0) {
                                    item = new GizDeviceSceneItem(device, status);
                                    break;
                                }

                                SDKLog.d("sceneItem data is empty, ignore it");
                            }
                            break;
                        case 1:
                            if (group == null) {
                                SDKLog.d("sceneItem group is null, ignore it");
                            } else {
                                GizWifiDevice deviceByProductKey = SDKEventManager.getInstance().findDeviceByProductKeyInTotalDeviceList(group.getGroupType());
                               maps_v4a1_v4a2 = deviceByProductKey != null ? deviceByProductKey.parseDeviceStatusForKey(jsonOb, "attrStatus") : null;
                                ConcurrentHashMap<String, Object> status = maps_v4a1_v4a2 != null ? (ConcurrentHashMap)((ConcurrentHashMap)maps_v4a1_v4a2.get(1)).get("data") : null;
                                if (status != null && status.size() != 0) {
                                    item = new GizDeviceSceneItem(group, status);
                                    break;
                                }

                                SDKLog.d("sceneItem data is empty, ignore it");
                            }
                            break;
                        case 2:
                            if (delay != -1) {
                                item = new GizDeviceSceneItem(delay);
                            }
                    }

                    if (item != null) {
                        tempList.add(item);
                    }
                }

                if (this.sceneItemList.size() != tempList.size()) {
                    update = true;
                }

                this.sceneItemList.clear();
                this.sceneItemList.addAll(tempList);
                return update;
            } else {
                SDKLog.d("owner is not current sceneOwner, ignore it");
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
        dest.writeString(this.sceneID);
        dest.writeParcelable(this.sceneOwner, 1);
    }
}
