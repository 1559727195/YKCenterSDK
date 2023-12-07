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
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceGroupListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceGroup implements Parcelable {
    private static List<ConcurrentHashMap<String, Integer>> messageQueue = new ArrayList();
    private List<GizWifiDevice> groupDeviceList = new ArrayList();
    private static final int MSG_RECV = 5;
    private GizDeviceGroupListener mListener;
    private GizWifiDevice groupOwner;
    private String groupID;
    private String groupType;
    private String groupName;
    private boolean isValid = true;
    protected Handler timeoutHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GizDeviceGroup.this.handleTimeoutMessage(msg);
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
                        if (!GizDeviceGroup.this.groupOwner.equals(mac, productKey, did)) {
                            SDKLog.d("<mac: " + mac + ", productKey: " + productKey + ", did: " + did + ">is not the owner");
                        }

                        if (cmd > 2000) {
                            sn = 0;
                        }

                        GizDeviceGroup.this.handleReceiveMessage(cmd, obj, sn);
                    } catch (NumberFormatException var9) {
                        var9.printStackTrace();
                    } catch (JSONException var10) {
                        var10.printStackTrace();
                    }
                default:
            }
        }
    };
    public static final Creator<GizDeviceGroup> CREATOR = new Creator<GizDeviceGroup>() {
        public GizDeviceGroup createFromParcel(Parcel source) {
            GizDeviceGroup group = new GizDeviceGroup();
            group.groupID = source.readString();
            group.groupOwner = (GizWifiDevice)source.readParcelable(GizWifiDevice.class.getClassLoader());
            List<GizDeviceGroup> list = GizDeviceGroupCenter.getTotalGroupListByOwner(group.groupOwner);
            GizDeviceGroup gp = null;
            Iterator var5 = list.iterator();

            while(var5.hasNext()) {
                GizDeviceGroup gizDeviceGroup = (GizDeviceGroup)var5.next();
                if (gizDeviceGroup != null && gizDeviceGroup.getGroupID().equals(group.groupID)) {
                    gp = gizDeviceGroup;
                    break;
                }
            }

            return gp;
        }

        public GizDeviceGroup[] newArray(int size) {
            return null;
        }
    };

    public String toString() {
        return "GizDeviceGroup [groupID=" + this.groupID + ", groupOwner=" + this.groupOwner + ", groupType=" + this.groupType + ", groupName=" + this.groupName + ", groupDeviceList=" + this.groupDeviceList + ", mListener=" + this.mListener + "]";
    }

    protected String simpleInfoMasking() {
        return "GizDeviceGroup [groupID=" + this.groupID + ", isValid=" + this.isValid + ", groupOwner=" + (this.groupOwner == null ? "null" : this.groupOwner.moreSimpleInfoMasking()) + "]";
    }

    protected String infoMasking() {
        String info = this.simpleInfoMasking() + ", listener=" + this.mListener + " ";
        return info + "->[groupType=" + this.groupType + ", groupName=" + this.groupName + ", groupDeviceList=" + SDKEventManager.listMasking(this.groupDeviceList) + "]";
    }

    protected GizDeviceGroup() {
    }

    public String getGroupID() {
        return this.groupID;
    }

    protected void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public GizWifiDevice getGroupOwner() {
        return this.groupOwner;
    }

    protected void setGroupOwner(GizWifiDevice groupOwner) {
        this.groupOwner = groupOwner;
    }

    public String getGroupType() {
        return this.groupType;
    }

    protected void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupName() {
        return this.groupName;
    }

    protected void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<GizWifiDevice> getGroupDeviceList() {
        SDKLog.a("Start => ");
        SDKLog.d("cache group device list: " + SDKEventManager.listMasking(this.groupDeviceList));
        SDKLog.a("End <= ");
        return this.groupDeviceList;
    }

    protected boolean getIsValid() {
        return this.isValid;
    }

    protected void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    protected void setGroupDeviceList(List<GizWifiDevice> groupDeviceList) {
        this.groupDeviceList = groupDeviceList;
    }

    public void setListener(GizDeviceGroupListener listener) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + "listener: " + (listener == null ? "null" : listener));
        this.mListener = listener;
        SDKLog.a("End <= ");
    }

    public void editGroupInfo(String groupName) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", groupName: " + groupName);
        if (!Constant.ishandshake) {
            this.OnDidUpdateGroupInfo(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
            SDKLog.a("End <= ");
        } else if (this.groupOwner == null) {
            this.OnDidUpdateGroupInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
            SDKLog.a("End <= ");
        } else if (groupName == null) {
            this.OnDidUpdateGroupInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
            SDKLog.a("End <= ");
        } else {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1303);
                obj.put("sn", sn);
                obj.put("mac", this.groupOwner.getMacAddress());
                obj.put("did", this.groupOwner.getDid());
                obj.put("productKey", this.groupOwner.getProductKey());
                obj.put("groupID", this.groupID);
                obj.put("groupName", groupName);
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (this.groupOwner.isLAN) {
                this.makeTimer(this.timeoutHandler, 9000, 1304, sn, 0);
            } else {
                this.makeTimer(this.timeoutHandler, 20000, 1304, sn, 0);
            }

            SDKLog.a("End <= ");
        }
    }

    public void addGroupDevice(List<GizWifiDevice> groupDevices) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", groupDevices: " + SDKEventManager.listMasking(groupDevices));
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.a("End <= ");
        } else if (this.groupOwner == null) {
            this.OnDidUpdateGroupInfo(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID);
            SDKLog.a("End <= ");
        } else if (groupDevices != null && groupDevices.size() != 0) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1309);
                obj.put("sn", sn);
                obj.put("mac", this.groupOwner.getMacAddress());
                obj.put("did", this.groupOwner.getDid());
                obj.put("productKey", this.groupOwner.getProductKey());
                obj.put("groupID", this.groupID);
                if (groupDevices != null && groupDevices.size() > 0) {
                    JSONArray array = new JSONArray();
                    Iterator var5 = groupDevices.iterator();

                    while(var5.hasNext()) {
                        GizWifiDevice device = (GizWifiDevice)var5.next();
                        if (device != null) {
                            JSONObject ob = new JSONObject();
                            ob.put("mac", device.getMacAddress());
                            ob.put("did", device.getDid());
                            ob.put("productKey", device.getProductKey());
                            array.put(ob);
                        }
                    }

                    obj.put("groupDevices", array);
                }
            } catch (JSONException var8) {
                SDKLog.e(var8.toString());
                var8.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (this.groupOwner.isLAN) {
                this.makeTimer(this.timeoutHandler, 9000, 1310, sn, 0);
            } else {
                this.makeTimer(this.timeoutHandler, 20000, 1310, sn, 0);
            }

            SDKLog.a("End <= ");
        } else {
            list = new ArrayList();
            this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        }
    }

    public void removeGroupDevice(List<GizWifiDevice> groupDevices) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", groupDevices: " + SDKEventManager.listMasking(groupDevices));
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.a("End <= ");
        } else if (this.groupOwner == null) {
            list = new ArrayList();
            this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else if (groupDevices != null && groupDevices.size() != 0) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1311);
                obj.put("sn", sn);
                obj.put("mac", this.groupOwner.getMacAddress());
                obj.put("did", this.groupOwner.getDid());
                obj.put("productKey", this.groupOwner.getProductKey());
                obj.put("groupID", this.groupID);
                if (groupDevices != null && groupDevices.size() > 0) {
                    JSONArray array = new JSONArray();
                    Iterator var5 = groupDevices.iterator();

                    while(var5.hasNext()) {
                        GizWifiDevice device = (GizWifiDevice)var5.next();
                        if (device != null) {
                            JSONObject ob = new JSONObject();
                            ob.put("mac", device.getMacAddress());
                            ob.put("did", device.getDid());
                            ob.put("productKey", device.getProductKey());
                            array.put(ob);
                        }
                    }

                    obj.put("groupDevices", array);
                }
            } catch (JSONException var8) {
                SDKLog.e(var8.toString());
                var8.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (this.groupOwner.isLAN) {
                this.makeTimer(this.timeoutHandler, 9000, 1312, sn, 0);
            } else {
                this.makeTimer(this.timeoutHandler, 20000, 1312, sn, 0);
            }

            SDKLog.a("End <= ");
        } else {
            list = new ArrayList();
            this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        }
    }

    public void updateGroupDevices() {
        SDKLog.a("Start => this: " + this.simpleInfoMasking());
        ArrayList list;
        if (!Constant.ishandshake) {
            list = new ArrayList();
            this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.a("End <= ");
        } else if (this.groupOwner == null) {
            list = new ArrayList();
            this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1313);
                obj.put("sn", sn);
                obj.put("mac", this.groupOwner.getMacAddress());
                obj.put("did", this.groupOwner.getDid());
                obj.put("productKey", this.groupOwner.getProductKey());
                obj.put("groupID", this.getGroupID());
            } catch (JSONException var4) {
                SDKLog.e(var4.toString());
                var4.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (this.groupOwner.isLAN) {
                this.makeTimer(this.timeoutHandler, 9000, 1314, sn, 0);
            } else {
                this.makeTimer(this.timeoutHandler, 20000, 1314, sn, 0);
            }

            SDKLog.a("End <= ");
        }
    }

    public void write(ConcurrentHashMap<String, Object> data, int sn) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", data: " + (data == null ? "null" : data) + ", sn: " + sn);
        if (!Constant.ishandshake) {
            this.OnDidWrite(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, sn);
            SDKLog.a("End <= ");
        } else if (this.groupOwner == null) {
            List<GizWifiDevice> list = new ArrayList();
            this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
            SDKLog.a("End <= ");
        } else if (data == null) {
            this.OnDidWrite(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, sn);
            SDKLog.a("End <= ");
        } else {
            Iterator iter = data.entrySet().iterator();
            JSONObject mobj = new JSONObject();

            while(iter.hasNext()) {
                try {
                    Entry<String, Object> entry = (Entry)iter.next();
                    String key = (String)entry.getKey();
                    Object val = entry.getValue();
                    if (val instanceof byte[]) {
                        byte[] byts = (byte[])((byte[])val);
                        mobj.put(key, GizWifiBinary.encode(byts));
                    } else {
                        mobj.put(key, val);
                    }
                } catch (JSONException var10) {
                    var10.printStackTrace();
                }
            }

            int sdk_sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1315);
                obj.put("sn", sdk_sn);
                obj.put("mac", this.groupOwner.getMacAddress());
                obj.put("did", this.groupOwner.getDid());
                obj.put("productKey", this.groupOwner.getProductKey());
                obj.put("groupID", this.groupID);
                obj.put("data", mobj);
            } catch (JSONException var9) {
                SDKLog.e(var9.toString());
                var9.printStackTrace();
            }

            sendMessageToDaemon(obj);
            if (this.groupOwner.isLAN) {
                this.makeTimer(this.timeoutHandler, 9000, 1316, sdk_sn, 0);
            } else {
                this.makeTimer(this.timeoutHandler, 20000, 1316, sdk_sn, 0);
            }

            SDKLog.a("End <= ");
        }
    }

    private void OnDidUpdateGroupInfo(GizDeviceGroup group, GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", group: " + (group == null ? "null" : group.simpleInfoMasking()));
        if (this.mListener != null) {
            this.mListener.didUpdateGroupInfo(group, result);
            SDKLog.d("Callback end");
        }

    }

    protected void OnDidUpdateGroupDevices(GizDeviceGroup group, GizWifiErrorCode result, List<GizWifiDevice> groupDeviceList) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", group: " + (group == null ? "null" : group.simpleInfoMasking()) + ", groupDeviceList: " + SDKEventManager.listMasking(groupDeviceList));
        if (this.mListener != null) {
            this.mListener.didUpdateGroupDevices(group, result, groupDeviceList);
            SDKLog.d("Callback end");
        }

    }

    private void OnDidWrite(GizDeviceGroup group, GizWifiErrorCode result, int sn) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", group: " + (group == null ? "null" : group.simpleInfoMasking()) + ", sn: " + sn);
        if (this.mListener != null) {
            this.mListener.didWrite(this, result, sn);
            SDKLog.d("Callback end");
        }

    }

    private static void sendMessageToDaemon(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    protected void handleTimeoutMessage(Message msg) {
        int sn = msg.what;
        int cmd = (Integer)msg.obj;
        List<GizWifiDevice> list = new ArrayList();
        switch(cmd) {
            case 1304:
                this.removeMessage(this.timeoutHandler, cmd, sn);
                this.OnDidUpdateGroupInfo(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT);
            case 1305:
            case 1306:
            case 1307:
            case 1308:
            case 1309:
            case 1311:
            case 1313:
            case 1315:
            default:
                break;
            case 1310:
                this.removeMessage(this.timeoutHandler, cmd, sn);
                this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1312:
                this.removeMessage(this.timeoutHandler, cmd, sn);
                this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1314:
                this.removeMessage(this.timeoutHandler, cmd, sn);
                this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
                break;
            case 1316:
                int appsn = this.removeMessage(this.timeoutHandler, cmd, sn);
                this.OnDidWrite(this, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, appsn);
        }

    }

    protected void handleReceiveMessage(int cmd, JSONObject obj, int sn) throws JSONException {
        int errorCode;
        List<GizWifiDevice> list;
        JSONArray groupDevices;
        switch(cmd) {
            case 1304:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (errorCode != 0) {
                    this.removeMessage(this.timeoutHandler, cmd, sn);
                    this.OnDidUpdateGroupInfo(this, GizWifiErrorCode.valueOf(errorCode));
                }
                break;
            case 1310:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (errorCode != 0) {
                    this.removeMessage(this.timeoutHandler, cmd, sn);
                    list = new ArrayList();
                    this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.valueOf(errorCode), list);
                }
                break;
            case 1312:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (errorCode != 0) {
                    this.removeMessage(this.timeoutHandler, cmd, sn);
                    list = new ArrayList();
                    this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.valueOf(errorCode), list);
                }
                break;
            case 1314:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                groupDevices = obj.has("groupDevices") ? obj.getJSONArray("groupDevices") : null;
                this.removeMessage(this.timeoutHandler, cmd, sn);
                if (errorCode != 0) {
                    list = new ArrayList();
                    this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    this.saveGroupDeviceList(groupDevices);
                    this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.valueOf(errorCode), this.groupDeviceList);
                }
                break;
            case 1316:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                int appsn = this.removeMessage(this.timeoutHandler, cmd, sn);
                this.OnDidWrite(this, GizWifiErrorCode.valueOf(errorCode), appsn);
                break;
            case 2022:
                groupDevices = obj.has("groupDevices") ? obj.getJSONArray("groupDevices") : null;
                this.removeMessage(this.timeoutHandler, cmd, sn);
                this.saveGroupDeviceList(groupDevices);
                this.OnDidUpdateGroupDevices(this, GizWifiErrorCode.GIZ_SDK_SUCCESS, this.groupDeviceList);
                break;
            case 2031:
                if (obj.has("groupName")) {
                    this.groupName = obj.getString("groupName");
                }

                this.removeMessage(this.timeoutHandler, cmd, sn);
                this.OnDidUpdateGroupInfo(this, GizWifiErrorCode.GIZ_SDK_SUCCESS);
        }

    }

    private void makeTimer(Handler handle, int timeout, int cmd, int sn, int appsn) {
        Message mes = handle.obtainMessage();
        mes.what = sn;
        mes.obj = cmd;
        handle.sendMessageDelayed(mes, (long)timeout);
        this.addSn(messageQueue, cmd, sn, appsn);
    }

    private int removeMessage(Handler handle, int cmd, int sdk_sn) {
        int appSn_snMap = 0;
        for(int i = 0; i < messageQueue.size(); ++i) {
            ConcurrentHashMap<String, Integer> snMap = (ConcurrentHashMap)messageQueue.get(i);
            int cmd_snMap;
            int sn_snMap;
            if (cmd != 2022 && cmd != 2031) {
                cmd_snMap = cmd;
                sn_snMap = sdk_sn;
            } else {
                cmd_snMap = (Integer)snMap.get("cmd");
                sn_snMap = (Integer)snMap.get("sdkSn");
                SDKLog.d("the notify cmd: " + cmd + ", the related sn: <cmd=" + cmd_snMap + ", sn= " + sn_snMap + ">");
            }

            Message msg = handle.hasMessages(sn_snMap) ? handle.obtainMessage(sn_snMap) : null;
            if (sn_snMap != 0 && msg != null) {
                handle.removeMessages(sn_snMap);
                SDKLog.d("removed the sn message: <cmd= " + cmd_snMap + ", sn= " + sn_snMap + ">");
            } else {
                SDKLog.d("did not remove the sn message, can not find <cmd= " + cmd_snMap + ", sn= " + sn_snMap + ">");
            }

            appSn_snMap = this.removeSn(messageQueue, cmd_snMap, sn_snMap);
            if (cmd != 2022 && cmd != 2031) {
                break;
            }
        }

        return appSn_snMap;
    }

    void addSn(List<ConcurrentHashMap<String, Integer>> queue, int cmd, int sdkSn, int appSn) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap();
        map.put("cmd", cmd);
        map.put("sdkSn", sdkSn);
        map.put("appSn", appSn);
        queue.add(map);
        SDKLog.d("add snQueue<cmd: " + cmd + ", sdkSn: " + sdkSn + ", appSn: " + appSn + ">");
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
            SDKLog.d("remove the sn: <cmd=" + cmd + ", sn= " + sn + ">");
            appSn = (Integer)snToRemove.get("appSn");
            queue.remove(snToRemove);
        } else {
            SDKLog.d("did not remove the sn, can not find <cmd= " + cmd + ", sn= " + sn + ">");
        }

        return appSn;
    }

    protected boolean saveGroupDeviceList(JSONArray jsonArray) throws JSONException {
        boolean update = false;
        if (jsonArray == null) {
            return update;
        } else {
            List<GizWifiDevice> tempList = new ArrayList();

            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonOb = (JSONObject)jsonArray.get(i);
                String mac = jsonOb.has("mac") ? jsonOb.getString("mac") : "";
                String did = jsonOb.has("did") ? jsonOb.getString("did") : "";
                String productKey = jsonOb.has("productKey") ? jsonOb.getString("productKey") : "";
                GizWifiDevice myOwnerDevice = SDKEventManager.getInstance().getDeviceInDeviceListByProductKeys(mac, productKey, did);
                if (myOwnerDevice != null) {
                    tempList.add(myOwnerDevice);
                }

                boolean isHasOb = false;
                Iterator var11 = this.groupDeviceList.iterator();

                while(var11.hasNext()) {
                    GizWifiDevice device = (GizWifiDevice)var11.next();
                    if (device.getMacAddress().equals(mac) && device.getDid().equals(did) && device.getProductKey().equals(productKey)) {
                        isHasOb = true;
                        break;
                    }
                }

                if (!isHasOb) {
                    update = true;
                }
            }

            if (tempList.size() < this.groupDeviceList.size()) {
                update = true;
            }

            this.groupDeviceList.clear();
            this.groupDeviceList.addAll(tempList);
            return update;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(this.groupID);
        dest.writeParcelable(this.groupOwner, 1);
    }
}
