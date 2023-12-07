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
import com.gizwits.gizwifisdk.api.GizJsProtocol.GetResult;
import com.gizwits.gizwifisdk.enumration.GizDeviceNetType;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingUserRole;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.enumration.XPGWifiDeviceType;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizWifiDevice implements Parcelable {
    protected GizWifiDeviceListener mListener;
    private String macAddress;
    private String did;
    private String ipAddress;
    protected boolean isLAN;
    private GizWifiDeviceNetStatus netStatus;
    private GizWifiDeviceType productType;
    private GizDeviceNetType netType;
    private String productKey;
    private String productKeyAdapter;
    private String productName;
    private boolean subscribed;
    private boolean isBind;
    private boolean hasProductDefine;
    private boolean isDisabled;
    private String remark;
    private String alias;
    private String productUI = null;
    private String productUIAdapter = null;
    private GizDeviceSharingUserRole sharingRole;
    private String deviceModuleFirmwareVer;
    private String deviceMcuFirmwareVer;
    private String deviceModuleHardVer;
    private String deviceMcuHardVer;
    private GizWifiDevice rootDevice;
    private ConcurrentHashMap<String, Object> attrStatus;
    private boolean isLowPower;
    private boolean isDormant;
    private int stateLastTimestamp;
    private int sleepDuration;
    private static final int MSG_RECV = 5;
    private static final int MSG_MESH_UPDATE = 6;
    private static final int MSG_MESH_WRITE = 7;
    private boolean oldIsOnline;
    private boolean oldIsConnected;
    private boolean loginning;
    private boolean isJsProtocol;
    private boolean autoGetDeviceStatus;
    private int logintimeout = 0;
    private int notify_status_sn = 0;
    private static List<ConcurrentHashMap<String, Integer>> app_sn_queue = new ArrayList();
    private static List<Integer> test_sn = new ArrayList();
    private String subdid;
    Handler hand = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (GizWifiDevice.this.loginning) {
                String macdid = (String)msg.obj;
                String mac = macdid.split(":")[0];
                String did = macdid.split(":")[1];
                List<GizWifiDevice> allDeviceList = SDKEventManager.getInstance().getDeviceListByProductKeys();
                Iterator var6 = allDeviceList.iterator();

                while(var6.hasNext()) {
                    GizWifiDevice gizWifiDevice = (GizWifiDevice)var6.next();
                    if (gizWifiDevice.getMacAddress().equals(mac) && gizWifiDevice.getDid().equals(did) && gizWifiDevice.getLoginning()) {
                        GizWifiDevice.this.onDidLogin(gizWifiDevice, 8308);
                        SDKLog.d("didlogin end==>" + DateUtil.getCurrentTime());
                        SDKLog.d("Device_Loging_start_client   :" + GizWifiDevice.this.logintimeout);
                    }
                }
            }

        }
    };
    private GizWifiDevice.MessageErrorHandler handler;
    /** @deprecated */
    public static String XPGWifiDeviceHardwareWifiHardVerKey = "wifiHardVer";
    /** @deprecated */
    public static String XPGWifiDeviceHardwareWifiSoftVerKey = "wifiSoftVer";
    /** @deprecated */
    public static String XPGWifiDeviceHardwareMCUHardVerKey = "mcuHardVer";
    /** @deprecated */
    public static String XPGWifiDeviceHardwareMCUSoftVerKey = "mcuSoftVer";
    /** @deprecated */
    public static String XPGWifiDeviceHardwareFirmwareIdKey = "firmwareId";
    /** @deprecated */
    public static String XPGWifiDeviceHardwareFirmwareVerKey = "firmwareVer";
    /** @deprecated */
    public static String XPGWifiDeviceHardwareProductKey = "productKey";
    private Handler han;
    private Parcel dest;
    private int flags;
    public static final Creator<GizWifiDevice> CREATOR = new Creator<GizWifiDevice>() {
        public GizWifiDevice createFromParcel(Parcel source) {
            GizWifiDevice device = new GizWifiDevice();
            device.macAddress = source.readString();
            device.did = source.readString();
            device.ipAddress = source.readString();
            device.productKey = source.readString();
            device.productName = source.readString();
            device.remark = source.readString();
            device.alias = source.readString();
            device.productUI = source.readString();
            device.isLAN = source.readByte() != 0;
            device.subscribed = source.readByte() != 0;
            device.isBind = source.readByte() != 0;
            device.hasProductDefine = source.readByte() != 0;
            device.isDisabled = source.readByte() != 0;
            device.netStatus = (GizWifiDeviceNetStatus)source.readSerializable();
            device.productType = (GizWifiDeviceType)source.readSerializable();
            GizWifiDevice deviceByMacAndDid = SDKEventManager.getInstance().getDeviceInDeviceListByProductKeys(device.macAddress, device.productKey, device.did);
            return deviceByMacAndDid;
        }

        public GizWifiDevice[] newArray(int size) {
            return null;
        }
    };
    private static String statustime;
    private static String writetime;

    public String toString() {
        return "GizWifiDevice [macAddress=" + this.macAddress + ", did=" + Utils.dataMasking(this.did) + ", productKey=" + this.productKey + ", ipAddress=" + this.ipAddress + ", isLAN=" + this.isLAN + ", netStatus=" + this.netStatus + ", netType=" + this.netType + ", isBind=" + this.isBind + ", subscribed=" + this.subscribed + ", productType=" + this.productType + ", productName=" + this.productName + ", hasProductDefine=" + this.hasProductDefine + ", remark=" + this.remark + ", alias=" + this.alias + ", sharingRole=" + this.sharingRole + ", listener=" + this.mListener + ", isLowPower=" + this.isLowPower + ", sleepDuration=" + this.sleepDuration + ", stateLastTimestamp=" + this.stateLastTimestamp + "]";
    }

    protected String moreSimpleInfoMasking() {
        return "GizWifiDevice [macAddress=" + this.macAddress + ", did=" + Utils.dataMasking(this.did) + ", productKey=" + this.productKey + "]";
    }

    protected String simpleInfoMasking() {
        String info = this.moreSimpleInfoMasking();
        return info + "->[ipAddress=" + this.ipAddress + ", isLAN=" + this.isLAN + ", isBind=" + this.isBind + ", subscribed=" + this.subscribed + ", productType=" + this.productType + ", productName=" + this.productName + ", rootDevice=" + this.rootDevice + ", listener=" + this.mListener + "]";
    }

    protected String infoMasking() {
        String info = this.simpleInfoMasking();
        return info + "->[remark=" + this.remark + ", alias=" + this.alias + ", hasProductDefine=" + this.hasProductDefine + ", productUI=" + (this.productUI == null ? "null" : "******") + ", sharingRole=" + this.sharingRole + ", deviceModuleFirmwareVer=" + this.deviceModuleFirmwareVer + ", deviceMcuFirmwareVer=" + this.deviceMcuFirmwareVer + "]";
    }

    protected boolean equals(String mac, String productKey, String did) {
        boolean isEqual = false;
        if (mac.equals(this.macAddress) && productKey.equals(this.productKey) && did.equals(this.did)) {
            isEqual = true;
        }

        return isEqual;
    }

    void addAppSn(List<ConcurrentHashMap<String, Integer>> snQueue, int appSn, int sdkSn) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap();
        map.put("appSn", appSn);
        map.put("sdkSn", sdkSn);
        snQueue.add(map);
    }

    int removeAppSn(List<ConcurrentHashMap<String, Integer>> snQueue, int ackSn) {
        int callbackSn = 0;
        ConcurrentHashMap<String, Integer> snToRemove = null;
        Iterator var5 = app_sn_queue.iterator();

        while(var5.hasNext()) {
            ConcurrentHashMap<String, Integer> snqueue = (ConcurrentHashMap)var5.next();
            Integer appSn = (Integer)snqueue.get("appSn");
            Integer sdkSn = (Integer)snqueue.get("sdkSn");
            if (ackSn == sdkSn) {
                callbackSn = appSn;
                snToRemove = snqueue;
                break;
            }
        }

        if (snToRemove != null) {
            app_sn_queue.remove(snToRemove);
        }

        return callbackSn;
    }

    public String getDeviceModuleFirmwareVer() {
        return this.deviceModuleFirmwareVer;
    }

    protected void setDeviceModuleFirmwareVer(String deviceModuleFirmwareVer) {
        this.deviceModuleFirmwareVer = deviceModuleFirmwareVer;
    }

    public String getDeviceModuleHardVer() {
        return this.deviceModuleHardVer;
    }

    protected void setDeviceModuleHardVer(String deviceModuleHardVer) {
        this.deviceModuleHardVer = deviceModuleHardVer;
    }

    public String getDeviceMcuFirmwareVer() {
        return this.deviceMcuFirmwareVer;
    }

    protected void setDeviceMcuFirmwareVer(String deviceMcuFirmwareVer) {
        this.deviceMcuFirmwareVer = deviceMcuFirmwareVer;
    }

    public String getDeviceMcuHardVer() {
        return this.deviceMcuHardVer;
    }

    protected void setDeviceMcuHardVer(String deviceMcuHardVer) {
        this.deviceMcuHardVer = deviceMcuHardVer;
    }

    public boolean isAutoGetDeviceStatus() {
        return this.autoGetDeviceStatus;
    }

    public void setAutoGetDeviceStatus(boolean autoGetDeviceStatus) {
        this.autoGetDeviceStatus = autoGetDeviceStatus;
    }

    public GizWifiDevice getRootDevice() {
        return this.rootDevice;
    }

    protected void setRootDevice(GizWifiDevice device) {
        this.rootDevice = device;
    }

    protected String getSubdid() {
        return this.subdid;
    }

    protected void setSubdid(String subdid) {
        this.subdid = subdid;
    }

    public GizDeviceSharingUserRole getSharingRole() {
        return this.sharingRole;
    }

    public ConcurrentHashMap<String, Object> getAttrStatus() {
        return this.attrStatus;
    }

    protected void setAttrStatus(ConcurrentHashMap<String, Object> attrStatus) {
        this.attrStatus = attrStatus;
    }

    protected void setSharingRole(GizDeviceSharingUserRole sharingRole) {
        this.sharingRole = sharingRole;
    }

    public void setJsProtocol(boolean isJsProtocol) {
        this.isJsProtocol = isJsProtocol;
    }

    public boolean isJsProtocol() {
        return this.isJsProtocol;
    }

    protected GizWifiDevice() {
        this.makeHandler();
        this.createHandler();
        this.subscribed = false;
        this.oldIsOnline = false;
        this.oldIsConnected = false;
        this.loginning = false;
        this.isJsProtocol = false;
    }

    protected void onDidUpdateProduct(GizWifiErrorCode result, String productKey, String productUI) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", productKey is " + (productKey == null ? "null" : productKey) + ", productUI: " + (productUI == null ? "null" : "******"));
        if (this.mListener != null) {
            this.mListener.didUpdateProduct(result, this, productUI);
            SDKLog.d("Callback end");
        }

    }

    private void createHandler() {
        this.handler = new GizWifiDevice.MessageErrorHandler(Looper.getMainLooper(), this);
        SDKLog.d("==========================================>" + this.handler);
    }

    Handler getHandler() {
        return this.han;
    }

    Handler getTimerHandler() {
        return this.handler;
    }

    protected void onDidSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", device: " + this.simpleInfoMasking() + ", isSubscribed: " + isSubscribed);
        if (this.mListener != null) {
            this.mListener.didSetSubscribe(result, this, this.subscribed);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidReceiveApp2DevData(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> status_v4a1, ConcurrentHashMap<String, Object> status_v4a2, ConcurrentHashMap<String, Object> adapterStatus, int sn) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", sn: " + sn + ", device: " + this.simpleInfoMasking() + "v4a2Status: " + (status_v4a2 == null ? "null" : status_v4a2) + "adapterStatus: " + (adapterStatus == null ? "null" : adapterStatus));
        SDKLog.d("didreceiveData===========>v4a1: " + status_v4a1);
        SDKLog.d("didreceiveData===========>v4a2: " + status_v4a2 + ", adapterStatus: " + adapterStatus);
        if (this.mListener != null) {
            this.mListener.didReceiveAppToDevAttrStatus(result, this, status_v4a2, adapterStatus, sn);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidReceiveData(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> status_v4a1, ConcurrentHashMap<String, Object> status_v4a2, ConcurrentHashMap<String, Object> adapterStatus, int sn) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", sn: " + sn + ", device: " + this.simpleInfoMasking() + "v4a2Status: " + (status_v4a2 == null ? "null" : status_v4a2) + "adapterStatus: " + (adapterStatus == null ? "null" : adapterStatus));
        SDKLog.d("didreceiveData===========>v4a1: " + status_v4a1);
        SDKLog.d("didreceiveData===========>v4a2: " + status_v4a2 + ", adapterStatus: " + adapterStatus);
        if (this.mListener != null) {
            this.mListener.didReceiveAttrStatus(result, this, status_v4a2, adapterStatus, sn);
            this.mListener.didReceiveData(result, this, status_v4a2, sn);
            this.mListener.didReceiveData(this, status_v4a1, Utils.changeErrorCode(result.getResult()));
            SDKLog.d("Callback end");
        }

    }

    protected void onDidGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, String> hardwareInfo_v4a1, ConcurrentHashMap<String, String> hardwareInfo_v4a2) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", device: " + this.simpleInfoMasking() + "dataMap: " + (hardwareInfo_v4a2 == null ? "null" : hardwareInfo_v4a2));
        if (this.mListener != null) {
            this.mListener.didGetHardwareInfo(result, this, hardwareInfo_v4a2);
            this.mListener.didQueryHardwareInfo(this, Utils.changeErrorCode(result.getResult()), hardwareInfo_v4a1);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidExitProductionTesting(GizWifiErrorCode result, GizWifiDevice device) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", device: " + this.simpleInfoMasking());
        if (this.mListener != null) {
            this.mListener.didExitProductionTesting(result, this);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", device: " + this.simpleInfoMasking());
        if (this.mListener != null) {
            this.mListener.didSetCustomInfo(result, this);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, , device: " + this.simpleInfoMasking() + "netStatus: " + netStatus.name());
        if (this.mListener != null) {
            this.mListener.didUpdateNetStatus(this, netStatus);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidDeviceOnline(GizWifiDevice device, boolean onlineStatus) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, , device: " + this.simpleInfoMasking() + "onlineStatus: " + onlineStatus);
        if (this.mListener != null) {
            this.mListener.didDeviceOnline(this, onlineStatus);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidDisconnected(GizWifiDevice device) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, , device: " + this.simpleInfoMasking() + "isConnected(): " + this.isConnected());
        if (this.mListener != null) {
            this.mListener.didDisconnected(this, 0);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidLogin(GizWifiDevice device, int result) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result + ", device: " + this.simpleInfoMasking());
        this.hand.removeMessages(this.logintimeout);
        SDKLog.d("didlogin end==>" + DateUtil.getCurrentTime());
        SDKLog.d("device isLogin====>" + this.loginning);
        SDKLog.d("Device_Loging_remove   :" + this.logintimeout);
        this.setLoginning(false);
        if (this.mListener != null) {
            this.mListener.didLogin(this, result);
            SDKLog.d("Callback end");
        }

    }

    private void makeHandler() {
        this.han = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 5:
                        String jsonStr = (String)msg.obj;

                        try {
                            JSONObject obj = new JSONObject(jsonStr);
                            int cmd = Integer.parseInt(obj.getString("cmd"));
                            int sn;
                            if (cmd <= 2000 && obj.has("sn")) {
                                sn = Integer.parseInt(obj.getString("sn"));
                            } else {
                                sn = cmd;
                            }

                            GizWifiDevice.this.didSetListener(cmd, obj, GizWifiDevice.this.mListener, sn);
                        } catch (NumberFormatException var19) {
                            var19.printStackTrace();
                        } catch (JSONException var20) {
                            var20.printStackTrace();
                        }
                        break;
                    case 6:
                        int loginSn = (Integer)msg.obj;
                        if (GizWifiDevice.this.isHandler(loginSn)) {
                            GizWifiDevice.this.handler.removeMessages(loginSn);
                            GizWifiDevice.this.setNetStatus(GizWifiDeviceNetStatus.GizDeviceControlled);
                            GizWifiDevice.this.onDidUpdateNetStatus(GizWifiDevice.this, GizWifiDeviceNetStatus.GizDeviceControlled);
                            SDKLog.b("device_subScribe", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), "" + Utils.changeString("subScribed") + ": " + GizWifiDevice.this.isSubscribed() + ", " + Utils.changeString("mac") + ": " + Utils.changeString(GizWifiDevice.this.getMacAddress()) + ", " + Utils.changeString("did") + ": " + Utils.changeString(Utils.dataMasking(GizWifiDevice.this.did)) + ", " + Utils.changeString("product_key") + ": " + Utils.changeString(GizWifiDevice.this.getProductKey()) + ", " + Utils.changeString("is_lan") + ": " + GizWifiDevice.this.isLAN + ", " + DateUtil.getLogString(SDKEventManager.mContext));
                        }
                        break;
                    case 7:
                        JSONObject callback = (JSONObject)msg.obj;
                        int writeSn = callback.optInt("sn", 0);
                        int errorcode = callback.optInt("errorCode", 0);
                        if (GizWifiDevice.this.isHandler(writeSn)) {
                            GizWifiDevice.this.handler.removeMessages(writeSn);
                            int appSn = GizWifiDevice.this.removeAppSn(GizWifiDevice.app_sn_queue, writeSn);

                            try {
                                String end = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
                                long diff = DateUtil.getDiff(GizWifiDevice.writetime, end);
                                SDKLog.b("device_write_end", GizWifiErrorCode.valueOf(errorcode).name(), Utils.changeString("elapsed_time") + ":" + diff);
                                ConcurrentHashMap<String, Object> maps_v4a1 = new ConcurrentHashMap();
                                ConcurrentHashMap<String, Object> maps_v4a2 = new ConcurrentHashMap();
                                new ArrayList();
                                ConcurrentHashMap<String, Object> maps_adapter = new ConcurrentHashMap();
                                GizWifiDevice.this.onDidReceiveData(GizWifiErrorCode.valueOf(errorcode), GizWifiDevice.this, maps_v4a1, maps_v4a2, maps_adapter, appSn);
                            } catch (NumberFormatException var18) {
                                var18.printStackTrace();
                            }
                        }
                }

            }
        };
    }

    private void didSetListener(int cmd, JSONObject obj, GizWifiDeviceListener mListener, int sn) {
        List<ConcurrentHashMap<String, Object>> maps;
        ConcurrentHashMap hardwareInfo_v4a1;
        ConcurrentHashMap maps_v4a1;
        List adapterMaps;
        ConcurrentHashMap<String, Object> maps_adapter;
        List deviceList;
        GizWifiDevice device_netstatus_notify;
        int error;
        String productUI;
        String productKey;
        String product_key;
        switch(cmd) {
            case 1018:
                if (this.isHandler(sn)) {
                    this.handler.removeMessages(sn);

                    try {
                        error = Integer.parseInt(obj.getString("errorCode"));
                        productUI = obj.has("productUI") ? obj.getString("productUI") : null;
                        productKey = obj.has("productKey") ? obj.getString("productKey") : null;
                        product_key = "";
                        adapterMaps = SDKEventManager.getInstance().getDeviceListByProductKeys();
                        Iterator var42 = adapterMaps.iterator();

                        while(var42.hasNext()) {
                            device_netstatus_notify = (GizWifiDevice)var42.next();
                            if (device_netstatus_notify.getProductKey().equals(product_key)) {
                                this.setProductUI(productUI);
                            }

                            if (device_netstatus_notify.getProductKeyAdapter().equals(product_key)) {
                                this.setProductUIAdapter(productUI);
                            }
                        }

                        this.onDidUpdateProduct(GizWifiErrorCode.valueOf(error), productKey, productUI);
                    } catch (JSONException var30) {
                        var30.printStackTrace();
                    }
                }
                break;
            case 1030:
                if (this.isHandler(sn)) {
                    this.handler.removeMessages(sn);

                    try {
                        error = Integer.parseInt(obj.getString("errorCode"));
                        if (error == 0) {
                            this.subscribed = true;
                        }

                        this.onDidSetSubscribe(GizWifiErrorCode.valueOf(error), this, this.subscribed);
                        SDKLog.b("device_subScribe", Utils.changeToGizErrorCode(error).name(), "" + Utils.changeString("subScribed") + ": " + this.isSubscribed() + ", " + Utils.changeString("mac") + ": " + Utils.changeString(this.getMacAddress()) + ", " + Utils.changeString("did") + ": " + Utils.changeString(Utils.dataMasking(this.did)) + ", " + Utils.changeString("product_key") + ": " + Utils.changeString(this.getProductKey()) + ", " + Utils.changeString("is_lan") + ": " + this.isLAN + ", " + DateUtil.getLogString(SDKEventManager.mContext));
                    } catch (JSONException var27) {
                        var27.printStackTrace();
                    }
                }
                break;
            case 1032:
                if (this.isHandler(sn)) {
                    this.handler.removeMessages(sn);

                    try {
                        error = Integer.parseInt(obj.getString("errorCode"));
                        if (error == 0) {
                            this.subscribed = false;
                        }

                        this.onDidSetSubscribe(GizWifiErrorCode.valueOf(error), this, this.subscribed);
                    } catch (JSONException var24) {
                        var24.printStackTrace();
                    }
                }
                break;
            case 1034:
                if (this.isHandler(sn)) {
                    this.notify_status_sn = sn;

                    try {
                        error = Integer.parseInt(obj.getString("errorCode"));
                       maps = this.parseDeviceStatusForKey(obj, "attrStatus");
                        maps_v4a1 = (ConcurrentHashMap)maps.get(0);
                        ConcurrentHashMap<String, Object> maps_v4a2 = (ConcurrentHashMap)maps.get(1);
                        adapterMaps = this.parseDeviceStatusForKey(obj, "adapterAttrStatus");
                        maps_adapter = (ConcurrentHashMap)adapterMaps.get(1);
                        if (error != 0 || maps_v4a1.size() == 0 && maps_v4a2.size() == 0) {
                            if (error != 0) {
                                this.onDidReceiveData(GizWifiErrorCode.valueOf(error), this, maps_v4a1, maps_v4a2, maps_adapter, 0);
                                this.handler.removeMessages(sn);
                            }
                        } else {
                            this.onDidReceiveData(GizWifiErrorCode.valueOf(error), this, maps_v4a1, maps_v4a2, maps_adapter, 0);
                            this.handler.removeMessages(sn);
                        }

                        String end = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
                        long diff = DateUtil.getDiff(end, statustime);
                        SDKLog.b("get_device_status_end", GizWifiErrorCode.valueOf(error).name(), Utils.changeString("elapsed_time") + ":" + diff);
                    } catch (NumberFormatException var28) {
                        var28.printStackTrace();
                    } catch (JSONException var29) {
                        var29.printStackTrace();
                    }
                }
                break;
            case 1036:
                if (this.isHandler(sn)) {
                    this.handler.removeMessages(sn);
                    error = this.removeAppSn(app_sn_queue, sn);

                    try {
                        int errorcode = Integer.parseInt(obj.getString("errorCode"));
                        productKey = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
                        long diff = DateUtil.getDiff(writetime, productKey);
                        SDKLog.b("device_write_end", GizWifiErrorCode.valueOf(errorcode).name(), Utils.changeString("elapsed_time") + ":" + diff);
                        deviceList = this.parseDeviceStatusForKey(obj, "attrStatus");
                        maps_v4a1 = (ConcurrentHashMap)deviceList.get(0);
                        ConcurrentHashMap<String, Object> maps_v4a2 = (ConcurrentHashMap)deviceList.get(1);
                       adapterMaps = this.parseDeviceStatusForKey(obj, "adapterAttrStatus");
                        maps_adapter = (ConcurrentHashMap)adapterMaps.get(1);
                        this.onDidReceiveData(GizWifiErrorCode.valueOf(errorcode), this, maps_v4a1, maps_v4a2, maps_adapter, error);
                    } catch (NumberFormatException var22) {
                        var22.printStackTrace();
                    } catch (JSONException var23) {
                        var23.printStackTrace();
                    }
                }
                break;
            case 1038:
                if (this.isHandler(sn)) {
                    this.handler.removeMessages(sn);

                    try {
                        error = Integer.parseInt(obj.getString("errorCode"));
                        hardwareInfo_v4a1 = null;
                        maps_v4a1 = null;
                        if (error == 0) {
                            hardwareInfo_v4a1 = this.parseHardwareInfo(obj, true);
                            maps_v4a1 = this.parseHardwareInfo(obj, false);
                        }

                        this.onDidGetHardwareInfo(GizWifiErrorCode.valueOf(error), this, hardwareInfo_v4a1, maps_v4a1);
                    } catch (NumberFormatException var20) {
                        var20.printStackTrace();
                    } catch (JSONException var21) {
                        var21.printStackTrace();
                    }
                }
                break;
            case 1040:
                if (this.isHandler(sn)) {
                    this.handler.removeMessages(sn);

                    try {
                        error = Integer.parseInt(obj.getString("errorCode"));
                        this.onDidExitProductionTesting(GizWifiErrorCode.valueOf(error), this);
                    } catch (NumberFormatException var18) {
                        var18.printStackTrace();
                    } catch (JSONException var19) {
                        var19.printStackTrace();
                    }
                }
                break;
            case 1042:
                if (this.isHandler(sn)) {
                    this.handler.removeMessages(sn);

                    try {
                        error = Integer.parseInt(obj.getString("errorCode"));
                        if (obj.has("remark")) {
                            productUI = obj.getString("remark");
                            this.setRemark(productUI);
                        }

                        if (obj.has("alias")) {
                            productUI = obj.getString("alias");
                            this.setAlias(productUI);
                        }

                        this.onDidSetCustomInfo(GizWifiErrorCode.valueOf(error), this);
                    } catch (NumberFormatException var25) {
                        var25.printStackTrace();
                    } catch (JSONException var26) {
                        var26.printStackTrace();
                    }
                }
                break;
            case 2003:
                try {
                    String mac_notify = obj.getString("mac");
                    productUI = obj.getString("did");
                    productKey = obj.getString("productKey");
                    product_key = obj.getString("netStatus");
                    GizWifiDeviceNetStatus status = GizWifiDeviceNetStatus.GizDeviceUnavailable;
                    if (product_key.equalsIgnoreCase("offline")) {
                        status = GizWifiDeviceNetStatus.GizDeviceOffline;
                    } else if (product_key.equalsIgnoreCase("online")) {
                        status = GizWifiDeviceNetStatus.GizDeviceOnline;
                    } else if (product_key.equalsIgnoreCase("controlled")) {
                        status = GizWifiDeviceNetStatus.GizDeviceControlled;
                    }

                    deviceList = SDKEventManager.getInstance().getTotalDeviceList();
                    device_netstatus_notify = SDKEventManager.getInstance().findDeviceInDeviceList(mac_notify, productUI, productKey, deviceList);
                    if (device_netstatus_notify != null) {
                        device_netstatus_notify.setNetStatus(status);
                        device_netstatus_notify.onDidUpdateNetStatus(device_netstatus_notify, status);
                        if (device_netstatus_notify.getOldIsOnline() != device_netstatus_notify.isOnline()) {
                            device_netstatus_notify.setOldIsOnline(device_netstatus_notify.isOnline());
                            device_netstatus_notify.onDidDeviceOnline(device_netstatus_notify, device_netstatus_notify.isOnline());
                        }

                        if (!device_netstatus_notify.isConnected() && device_netstatus_notify.getOldIsConnected() != device_netstatus_notify.isConnected()) {
                            device_netstatus_notify.setOldIsConnected(device_netstatus_notify.isConnected());
                            device_netstatus_notify.onDidDisconnected(device_netstatus_notify);
                        }

                        if (device_netstatus_notify.isConnected() && device_netstatus_notify.getLoginning()) {
                            device_netstatus_notify.onDidLogin(device_netstatus_notify, GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult());
                            device_netstatus_notify.setOldIsConnected(device_netstatus_notify.isConnected());
                        }
                    }
                } catch (JSONException var17) {
                    var17.printStackTrace();
                }
                break;
            case 2006:
                try {
                    if (this.isHandler(this.notify_status_sn)) {
                        this.handler.removeMessages(this.notify_status_sn);
                    }

                    maps = this.parseDeviceStatusForKey(obj, "attrStatus");
                    hardwareInfo_v4a1 = (ConcurrentHashMap)maps.get(0);
                    maps_v4a1 = (ConcurrentHashMap)maps.get(1);
                    adapterMaps = this.parseDeviceStatusForKey(obj, "adapterAttrStatus");
                    maps_adapter = (ConcurrentHashMap)adapterMaps.get(1);
                    this.onDidReceiveData(GizWifiErrorCode.GIZ_SDK_SUCCESS, this, hardwareInfo_v4a1, maps_v4a1, maps_adapter, 0);
                } catch (JSONException var15) {
                    var15.printStackTrace();
                }
                break;
            case 2014:
                try {
                    if (this.isHandler(this.notify_status_sn)) {
                        this.handler.removeMessages(this.notify_status_sn);
                    }

                    maps = this.parseDeviceStatusForKey(obj, "attrStatus");
                    hardwareInfo_v4a1 = (ConcurrentHashMap)maps.get(0);
                    maps_v4a1 = (ConcurrentHashMap)maps.get(1);
                    adapterMaps = this.parseDeviceStatusForKey(obj, "adapterAttrStatus");
                    maps_adapter = (ConcurrentHashMap)adapterMaps.get(1);
                    this.onDidReceiveApp2DevData(GizWifiErrorCode.GIZ_SDK_SUCCESS, this, hardwareInfo_v4a1, maps_v4a1, maps_adapter, 0);
                } catch (JSONException var16) {
                    var16.printStackTrace();
                }
        }

    }

    private ConcurrentHashMap<String, String> parseHardwareInfo(JSONObject obj, boolean isCompitable) throws JSONException {
        ConcurrentHashMap<String, String> map_v4a1 = new ConcurrentHashMap();
        ConcurrentHashMap<String, String> map_v4a2 = new ConcurrentHashMap();
        String wifiHardVersion = obj.getString("wifiHardVersion");
        String wifiSoftVersion = obj.getString("wifiSoftVersion");
        String mcuHardVersion = obj.getString("mcuHardVersion");
        String mcuSoftVersion = obj.getString("mcuSoftVersion");
        String wifiFirmwareId = obj.getString("wifiFirmwareId");
        String wifiFirmwareVer = obj.getString("wifiFirmwareVer");
        String productKey = obj.getString("productKey");
        map_v4a1.put("wifiHardVer", wifiHardVersion);
        map_v4a1.put("wifiSoftVer", wifiSoftVersion);
        map_v4a1.put("mcuHardVer", mcuHardVersion);
        map_v4a1.put("mcuSoftVer", mcuSoftVersion);
        map_v4a1.put("firmwareId", wifiFirmwareId);
        map_v4a1.put("firmwareVer", wifiFirmwareVer);
        map_v4a1.put("productKey", productKey);
        map_v4a2.put("wifiHardVersion", wifiHardVersion);
        map_v4a2.put("wifiSoftVersion", wifiSoftVersion);
        map_v4a2.put("mcuHardVersion", mcuHardVersion);
        map_v4a2.put("mcuSoftVersion", mcuSoftVersion);
        map_v4a2.put("wifiFirmwareId", wifiFirmwareId);
        map_v4a2.put("wifiFirmwareVer", wifiFirmwareVer);
        map_v4a2.put("productKey", productKey);
        return isCompitable ? map_v4a1 : map_v4a2;
    }

    protected List<ConcurrentHashMap<String, Object>> parseDeviceStatusForKey(JSONObject obj, String key) throws JSONException {
        List<ConcurrentHashMap<String, Object>> map_v4a1_v4a2 = new ArrayList();
        ConcurrentHashMap<String, Object> map_v4a1 = new ConcurrentHashMap();
        ConcurrentHashMap<String, Object> map_v4a2 = new ConcurrentHashMap();
        JSONObject attrstatus_jsonobj = null;
        if (obj != null && obj.has(key)) {
            attrstatus_jsonobj = obj.getJSONObject(key);
        }

        if (attrstatus_jsonobj == null) {
            map_v4a1_v4a2.add(map_v4a1);
            map_v4a1_v4a2.add(map_v4a2);
            return map_v4a1_v4a2;
        } else {
            JSONObject status_jsonobj = null;
            JSONObject extdata_jsonobj = null;
            JSONObject enumData_jsonobj = null;
            JSONObject faults_jsonobj = null;
            JSONObject alerts_jsonobj = null;
            String binaryString = null;
            if (attrstatus_jsonobj.has("data")) {
                status_jsonobj = attrstatus_jsonobj.getJSONObject("data");
            }

            if (attrstatus_jsonobj.has("enumData")) {
                enumData_jsonobj = attrstatus_jsonobj.getJSONObject("enumData");
            }

            if (attrstatus_jsonobj.has("extData")) {
                extdata_jsonobj = attrstatus_jsonobj.getJSONObject("extData");
            }

            if (attrstatus_jsonobj.has("binary")) {
                binaryString = attrstatus_jsonobj.getString("binary");
            }

            if (attrstatus_jsonobj.has("faults")) {
                faults_jsonobj = attrstatus_jsonobj.getJSONObject("faults");
            }

            if (attrstatus_jsonobj.has("alerts")) {
                alerts_jsonobj = attrstatus_jsonobj.getJSONObject("alerts");
            }

            JSONObject entity0 = new JSONObject();
            JSONObject json_v4a1 = new JSONObject();
            ConcurrentHashMap<String, Object> json_v4a2 = new ConcurrentHashMap();
            JSONObject params;
            Iterator bin_params;
            String param;
            if (status_jsonobj != null) {
                params = new JSONObject(status_jsonobj.toString());
                bin_params = params.keys();

                while(bin_params.hasNext()) {
                    param = bin_params.next().toString();
                    Object value = params.get(param);
                    if (value != null) {
                        if (value instanceof Boolean) {
                            boolean istrue = (Boolean)value;
                            if (istrue) {
                                json_v4a1.put(param, "1");
                            } else {
                                json_v4a1.put(param, "0");
                            }
                        } else {
                            json_v4a1.put(param, value + "");
                        }

                        if (value instanceof String) {
                            param = (String)value;
                            if (param.contains(".")) {
                                Double db = Double.parseDouble(param);
                                json_v4a2.put(param, db);
                            } else {
                                json_v4a2.put(param, Integer.parseInt(param));
                            }
                        } else {
                            json_v4a2.put(param, value);
                        }
                    }
                }
            }

            if (extdata_jsonobj != null) {
                params = new JSONObject(extdata_jsonobj.toString());
                bin_params = extdata_jsonobj.keys();

                while(bin_params.hasNext()) {
                    param = bin_params.next().toString();
                    String value = params.getString(param);
                    if (!TextUtils.isEmpty(value)) {
                        try {
                            byte[] decode = GizWifiBinary.decode(value);
                            json_v4a2.put(param, decode);
                            json_v4a1.put(param, value.toString());
                        } catch (UnsupportedEncodingException var26) {
                            var26.printStackTrace();
                        }
                    }
                }
            }

            if (enumData_jsonobj != null) {
                params = new JSONObject(enumData_jsonobj.toString());
                bin_params = enumData_jsonobj.keys();

                while(bin_params.hasNext()) {
                    param = bin_params.next().toString();
                    JSONObject value = params.getJSONObject(param);
                    if (value != null) {
                        param = value.getString("displayName");
                        int b = value.getInt("value");
                        json_v4a1.put(param, b + "");
                        json_v4a2.put(param, b);
                    }
                }
            }

            entity0.put("entity0", json_v4a1);
            entity0.put("cmd", 4);
            entity0.put("version", 4);
            map_v4a1.put("data", entity0.toString());
            map_v4a2.put("data", json_v4a2);
            if (!TextUtils.isEmpty(binaryString)) {
                try {
                    map_v4a1.put("binary", binaryString);
                    map_v4a2.put("binary", GizWifiBinary.decode(binaryString));
                } catch (UnsupportedEncodingException var25) {
                    var25.printStackTrace();
                }
            }

            String name;
            Object b;
            boolean b_value;
            JSONObject v1json;
            ConcurrentHashMap v2json;
            JSONObject value;
            if (faults_jsonobj != null) {
                params = new JSONObject(faults_jsonobj.toString());
                bin_params = faults_jsonobj.keys();
                v1json = new JSONObject();
                v2json = new ConcurrentHashMap();

                while(bin_params.hasNext()) {
                    param = bin_params.next().toString();
                    value = params.getJSONObject(param);
                    if (value != null) {
                        name = value.getString("displayName");
                        b = value.get("value");
                        if (b instanceof Boolean) {
                            b_value = (Boolean)b;
                            if (b_value) {
                                v1json.put(name, "1");
                            } else {
                                v1json.put(name, "0");
                            }
                        } else {
                            v1json.put(name, b + "");
                        }

                        v2json.put(param, b);
                    }
                }

                map_v4a1.put("faults", v1json.toString());
                map_v4a2.put("faults", v2json);
            }

            if (alerts_jsonobj != null) {
                params = new JSONObject(alerts_jsonobj.toString());
                bin_params = alerts_jsonobj.keys();
                v1json = new JSONObject();
                v2json = new ConcurrentHashMap();

                while(bin_params.hasNext()) {
                    param = bin_params.next().toString();
                    value = params.getJSONObject(param);
                    if (value != null) {
                        name = value.getString("displayName");
                        b = value.get("value");
                        if (b instanceof Boolean) {
                            b_value = (Boolean)b;
                            if (b_value) {
                                v1json.put(name, "1");
                            } else {
                                v1json.put(name, "0");
                            }
                        } else {
                            v1json.put(name, b + "");
                        }

                        v2json.put(param, b);
                    }
                }

                map_v4a1.put("alters", v1json.toString());
                map_v4a2.put("alerts", v2json);
            }

            this.setAttrStatus(map_v4a2);
            map_v4a1_v4a2.add(map_v4a1);
            map_v4a1_v4a2.add(map_v4a2);
            return map_v4a1_v4a2;
        }
    }

    protected void syncDeviceInfoFromJson(JSONObject jsonObject) throws JSONException {
        String mac = jsonObject.has("mac") ? jsonObject.getString("mac") : "";
        String did = jsonObject.has("did") ? jsonObject.getString("did") : "";
        String ip = jsonObject.has("ip") ? jsonObject.getString("ip") : "";
        String productKey = jsonObject.has("productKey") ? jsonObject.getString("productKey") : "";
        String productKeyAdapter = jsonObject.has("productKeyAdapter") ? jsonObject.getString("productKeyAdapter") : "";
        String productName = jsonObject.has("productName") ? jsonObject.getString("productName") : "";
        String remark = jsonObject.has("remark") ? jsonObject.getString("remark") : "";
        String alias = jsonObject.has("alias") ? jsonObject.getString("alias") : "";
        String deviceModuleFirmwareVer = jsonObject.has("deviceModuleFirmwareVer") ? jsonObject.getString("deviceModuleFirmwareVer") : "";
        String deviceMcuFirmwareVer = jsonObject.has("deviceMcuFirmwareVer") ? jsonObject.getString("deviceMcuFirmwareVer") : "";
        String deviceModuleHardVer = jsonObject.has("deviceModuleHardVer") ? jsonObject.getString("deviceModuleHardVer") : "";
        String deviceMcuHardVer = jsonObject.has("deviceMcuHardVer") ? jsonObject.getString("deviceMcuHardVer") : "";
        boolean isLocal = jsonObject.has("isLocal") ? jsonObject.getBoolean("isLocal") : true;
        boolean isBind = jsonObject.has("isBind") ? jsonObject.getBoolean("isBind") : false;
        boolean isDisabled = jsonObject.has("isDisabled") ? jsonObject.getBoolean("isDisabled") : false;
        boolean isProductDefined = jsonObject.has("isProductDefined") ? jsonObject.getBoolean("isProductDefined") : false;
        boolean isLowPower = jsonObject.has("isLowPower") ? jsonObject.getBoolean("isLowPower") : false;
        boolean isDormant = jsonObject.has("isDormant") ? jsonObject.getBoolean("isDormant") : false;
        int stateLastTimestamp = jsonObject.has("stateLastTimestamp") ? jsonObject.getInt("stateLastTimestamp") : 0;
        int sleepDuration = jsonObject.has("sleepDuration") ? jsonObject.getInt("sleepDuration") : 0;
        GizDeviceSharingUserRole role = GizDeviceSharingUserRole.GizDeviceSharingNormal;
        if (jsonObject.has("role")) {
            if (jsonObject.getString("role").equalsIgnoreCase("special")) {
                role = GizDeviceSharingUserRole.GizDeviceSharingSpecial;
            } else if (jsonObject.getString("role").equalsIgnoreCase("owner")) {
                role = GizDeviceSharingUserRole.GizDeviceSharingOwner;
            } else if (jsonObject.getString("role").equalsIgnoreCase("guest")) {
                role = GizDeviceSharingUserRole.GizDeviceSharingGuest;
            }
        }

        GizWifiDeviceNetStatus netStatus = GizWifiDeviceNetStatus.GizDeviceOffline;
        if (jsonObject.has("netStatus")) {
            if (jsonObject.getString("netStatus").equalsIgnoreCase("online")) {
                netStatus = GizWifiDeviceNetStatus.GizDeviceOnline;
            } else if (jsonObject.getString("netStatus").equalsIgnoreCase("controlled")) {
                netStatus = GizWifiDeviceNetStatus.GizDeviceControlled;
            }
        }

        GizDeviceNetType netType = GizDeviceNetType.GizDeviceNetWifi;
        if (jsonObject.has("netType")) {
            if (jsonObject.getInt("netType") == 1) {
                netType = GizDeviceNetType.GizDeviceNetNB;
            } else if (jsonObject.getInt("netType") == 2) {
                netType = GizDeviceNetType.GizDeviceNetBT;
            } else if (jsonObject.getInt("netType") == 3) {
                netType = GizDeviceNetType.GizDeviceNetBLE;
            } else {
                netType = GizDeviceNetType.GizDeviceNetWifi;
            }
        }

        GizWifiDeviceType type = GizWifiDeviceType.GizDeviceNormal;
        if (jsonObject.has("type")) {
            if (jsonObject.getString("type").equalsIgnoreCase("centralControl")) {
                type = GizWifiDeviceType.GizDeviceCenterControl;
            } else if (jsonObject.getString("type").equalsIgnoreCase("sub")) {
                type = GizWifiDeviceType.GizDeviceSub;
            }
        }

        this.setMacAddress(mac);
        this.setDid(did);
        this.setIpAddress(ip);
        this.setProductKey(productKey);
        this.setProductKeyAdapter(productKeyAdapter);
        this.setProductName(productName);
        this.setRemark(remark);
        this.setAlias(alias);
        this.setIsBind(isBind);
        this.setLAN(isLocal);
        this.setDisabled(isDisabled);
        this.setHasProductDefine(isProductDefined);
        this.setNetStatus(netStatus);
        this.setNetType(netType);
        this.setProductType(type);
        this.setSharingRole(role);
        this.setDeviceMcuFirmwareVer(deviceMcuFirmwareVer);
        this.setDeviceModuleFirmwareVer(deviceModuleFirmwareVer);
        this.setDeviceMcuHardVer(deviceMcuHardVer);
        this.setDeviceModuleHardVer(deviceModuleHardVer);
        this.setLowPower(isLowPower);
        this.setDormant(isDormant);
        this.setSleepDuration(sleepDuration);
        this.setStateLastTimestamp(stateLastTimestamp);
        JSONObject gatewayJsonObj = jsonObject.has("rootDevice") ? jsonObject.getJSONObject("rootDevice") : null;
        String api;
        String profile;
        String productAdapter;
        if (gatewayJsonObj != null) {
            api = gatewayJsonObj.has("mac") ? gatewayJsonObj.getString("mac") : "";
            profile = gatewayJsonObj.has("productKey") ? gatewayJsonObj.getString("productKey") : "";
            productAdapter = gatewayJsonObj.has("did") ? gatewayJsonObj.getString("did") : "";
            List<GizWifiDevice> deviceList = SDKEventManager.getInstance().getTotalDeviceList();
            GizWifiDevice gateway = SDKEventManager.getInstance().findDeviceInDeviceList(api, productAdapter, profile, deviceList);
            if (gateway != null) {
                this.setRootDevice(gateway);
            }
        } else {
            this.setRootDevice((GizWifiDevice)null);
        }

        api = (String)SDKEventManager.domainInfo.get("openapi");
        api = api.contains(":") ? api.split(":")[0] : api;
        File profiles;
        File file;
        JSONObject ui;
        String myui;
        if (isProductDefined) {
            profiles = null;
            if (SDKLog.isSDCard) {
                profiles = new File(Constant.productFilePath, api);
            } else {
                profiles = new File(SDKLog.fileCachePath + "/productFile/", api);
            }

            productAdapter = "";
            if (TextUtils.isEmpty(this.getProductUI())) {
                file = new File(profiles, productKey + ".json");
                productAdapter = Utils.readFileContentStr(file.toString());
            }

            if (!TextUtils.isEmpty(productAdapter)) {
                ui = new JSONObject(productAdapter);
                if (ui.has("ui")) {
                    myui = ui.getString("ui");
                    this.setProductUI(myui);
                }
            }
        }

        if (!TextUtils.isEmpty(productKeyAdapter)) {
            profiles = null;
            if (SDKLog.isSDCard) {
                profiles = new File(Constant.productFilePath, api);
            } else {
                profiles = new File(SDKLog.fileCachePath + "/productFile/", api);
            }

            productAdapter = "";
            if (TextUtils.isEmpty(this.getProductUIAdapter())) {
                file = new File(profiles, productKeyAdapter + ".json");
                productAdapter = Utils.readFileContentStr(file.toString());
            }

            if (!TextUtils.isEmpty(productAdapter)) {
                ui = new JSONObject(productAdapter);
                if (ui.has("ui")) {
                    myui = ui.getString("ui");
                    this.setProductUIAdapter(myui);
                }
            }
        }

    }

    protected boolean isEqualToJsonObj(JSONObject jsonObj) throws JSONException {
        String mac = jsonObj.has("mac") ? jsonObj.getString("mac") : "";
        String did = jsonObj.has("did") ? jsonObj.getString("did") : "";
        String productKey = jsonObj.has("productKey") ? jsonObj.getString("productKey") : "";
        return this.macAddress.equals(mac) && this.productKey.equals(productKey) && this.did.equals(did);
    }

    protected String getProductSecretByProductInfo() {
        String productSecret = null;
        if (SDKEventManager.getProductInfo() != null) {
            Iterator var2 = SDKEventManager.getProductInfo().iterator();

            while(var2.hasNext()) {
                ConcurrentHashMap<String, Object> productInfo = (ConcurrentHashMap)var2.next();
                if (productInfo.get("productKey").equals(this.productKey)) {
                    productSecret = (String)productInfo.get("productSecret");
                    break;
                }
            }
        }

        return productSecret;
    }

    public void setListener(GizWifiDeviceListener Listener) {
        SDKLog.a("Start => listener: " + Listener);
        this.mListener = Listener;
    }

    public GizWifiDeviceListener getListener() {
        return this.mListener;
    }

    /** @deprecated */
    public void setSubscribe(boolean subscribe) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", subsribe: " + subscribe);
        if (this.subscribed && this.hasProductDefine && TextUtils.isEmpty(this.productUI)) {
            this.updateProduct(this.productKey);
        }

        if (this.subscribed && !TextUtils.isEmpty(this.productKeyAdapter)) {
            this.updateProduct(this.productKeyAdapter);
        }

        this.setAutoGetDeviceStatus(true);
        JSONObject obj = new JSONObject();
        int sn = Utils.getSn();

        try {
            obj.put("sn", sn);
            obj.put("did", this.getDid());
            obj.put("mac", this.getMacAddress());
            obj.put("productKey", this.getProductKey());
            obj.put("autoGetDeviceStatus", true);
            if (subscribe) {
                obj.put("cmd", 1029);
                obj.put("productSecret", this.getProductSecretByProductInfo());
                this.sendMes2Demo(obj);
                this.makeTimer(4000, 1030, sn);
            } else {
                obj.put("cmd", 1031);
                this.sendMes2Demo(obj);
                this.makeTimer(4000, 1032, sn);
            }

            if (GizMeshLocalControlCenter.sharedInstance().isHasFindDevice(this.getMacAddress())) {
                GizMeshLocalControlCenter.sharedInstance().setSubscribe(this.getMacAddress(), sn, this.subscribed);
            }
        } catch (JSONException var5) {
            SDKLog.e(var5.toString());
            var5.printStackTrace();
        }

        SDKLog.a("End <= ");
    }

    public void setSubscribe(String productSecret, boolean subscribed) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", subsribe: " + subscribed + ", productSecret :" + Utils.dataMasking(productSecret));
        if (subscribed && this.hasProductDefine && TextUtils.isEmpty(this.productUI)) {
            this.updateProduct(this.productKey);
        }

        if (subscribed && !TextUtils.isEmpty(this.productKeyAdapter)) {
            this.updateProduct(this.productKeyAdapter);
        }

        this.setAutoGetDeviceStatus(true);
        JSONObject obj = new JSONObject();
        int sn = Utils.getSn();

        try {
            obj.put("sn", sn);
            obj.put("did", this.getDid());
            obj.put("mac", this.getMacAddress());
            obj.put("productKey", this.getProductKey());
            obj.put("autoGetDeviceStatus", true);
            if (subscribed) {
                obj.put("cmd", 1029);
                this.makeTimer(4000, 1030, sn);
                String ps = this.getProductSecretByProductInfo();
                if (ps == null) {
                    obj.put("productSecret", productSecret);
                } else {
                    obj.put("productSecret", ps);
                }

                this.sendMes2Demo(obj);
            } else {
                obj.put("cmd", 1031);
                this.sendMes2Demo(obj);
                this.makeTimer(4000, 1032, sn);
            }

            if (GizMeshLocalControlCenter.sharedInstance().isHasFindDevice(this.getMacAddress())) {
                GizMeshLocalControlCenter.sharedInstance().setSubscribe(this.getMacAddress(), sn, subscribed);
            }
        } catch (JSONException var6) {
            SDKLog.e(var6.toString());
            var6.printStackTrace();
        }

        SDKLog.a("End <= ");
    }

    public void setSubscribe(boolean subscribed, boolean autoGetDeviceStatus) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", subsribe: " + subscribed + ", autoGetDeviceStatus :" + autoGetDeviceStatus);
        if (subscribed && this.hasProductDefine && TextUtils.isEmpty(this.productUI)) {
            this.updateProduct(this.productKey);
        }

        if (subscribed && !TextUtils.isEmpty(this.productKeyAdapter)) {
            this.updateProduct(this.productKeyAdapter);
        }

        this.setAutoGetDeviceStatus(autoGetDeviceStatus);
        JSONObject obj = new JSONObject();
        int sn = Utils.getSn();

        try {
            obj.put("sn", sn);
            obj.put("did", this.getDid());
            obj.put("mac", this.getMacAddress());
            obj.put("productKey", this.getProductKey());
            obj.put("autoGetDeviceStatus", autoGetDeviceStatus);
            if (subscribed) {
                obj.put("cmd", 1029);
                this.sendMes2Demo(obj);
                this.makeTimer(4000, 1030, sn);
            } else {
                obj.put("cmd", 1031);
                this.sendMes2Demo(obj);
                this.makeTimer(4000, 1032, sn);
            }

            if (GizMeshLocalControlCenter.sharedInstance().isHasFindDevice(this.getMacAddress())) {
                GizMeshLocalControlCenter.sharedInstance().setSubscribe(this.getMacAddress(), sn, subscribed);
            }
        } catch (JSONException var6) {
            SDKLog.e(var6.toString());
            var6.printStackTrace();
        }

        SDKLog.a("End <= ");
    }

    public void getHardwareInfo() {
        SDKLog.a("Start => this: " + this.simpleInfoMasking());
        int sn = Utils.getSn();
        JSONObject obj = new JSONObject();

        try {
            obj.put("cmd", 1037);
            obj.put("sn", sn);
            obj.put("did", this.getDid());
            obj.put("mac", this.getMacAddress());
            obj.put("productKey", this.getProductKey());
        } catch (JSONException var4) {
            SDKLog.e(var4.toString());
            var4.printStackTrace();
        }

        this.sendMes2Demo(obj);
        this.makeTimer(31000, 1038, sn);
        SDKLog.a("End <= ");
    }

    public void exitProductionTesting() {
        SDKLog.a("Start => this: " + this.simpleInfoMasking());
        int sn = Utils.getSn();
        JSONObject obj = new JSONObject();

        try {
            obj.put("cmd", 1039);
            obj.put("sn", sn);
            obj.put("did", this.getDid());
            obj.put("mac", this.getMacAddress());
            obj.put("produtKey", this.getProductKey());
        } catch (JSONException var4) {
            SDKLog.e(var4.toString());
            var4.printStackTrace();
        }

        this.sendMes2Demo(obj);
        this.makeTimer(31000, 1040, sn);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void getDeviceStatus() {
        SDKLog.d("Start => this: " + this.simpleInfoMasking());
        GizWifiErrorCode result = GizWifiErrorCode.GIZ_SDK_SUCCESS;
        SDKLog.b("get_device_status_start", result.name(), Utils.changeString("mac") + ":" + Utils.changeString(TextUtils.isEmpty(this.getMacAddress()) ? "null" : this.getMacAddress()) + ", " + Utils.changeString("did") + " : " + Utils.changeString(TextUtils.isEmpty(this.getDid()) ? "null" : Utils.dataMasking(this.did)) + ", " + Utils.changeString("mac") + ":" + this.isLAN + Utils.changeString("bind_type") + " : " + Utils.changeString("UserSharing") + DateUtil.getLogString(SDKEventManager.mContext));
        statustime = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
        int sn = Utils.getSn();
        JSONObject obj = new JSONObject();

        try {
            obj.put("cmd", 1033);
            obj.put("sn", sn);
            obj.put("did", this.getDid());
            obj.put("mac", this.getMacAddress());
            obj.put("productKey", this.getProductKey());
        } catch (JSONException var5) {
            SDKLog.e(var5.toString());
            var5.printStackTrace();
        }

        this.sendMes2Demo(obj);
        if (this.isLAN) {
            this.makeTimer(4000, 1034, sn);
        } else {
            this.makeTimer(20000, 1034, sn);
        }

        SDKLog.a("End <= ");
    }

    public void getDeviceStatus(List<String> attrs) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", attrs: " + (attrs == null ? "null" : attrs));
        GizWifiErrorCode result = GizWifiErrorCode.GIZ_SDK_SUCCESS;
        SDKLog.b("get_device_status_start", result.name(), Utils.changeString("mac") + ":" + Utils.changeString(TextUtils.isEmpty(this.getMacAddress()) ? "null" : this.getMacAddress()) + ", " + Utils.changeString("did") + " : " + Utils.changeString(TextUtils.isEmpty(this.did) ? "null" : Utils.dataMasking(this.did) + ", " + Utils.changeString("mac") + ":" + this.isLAN + Utils.changeString("bind_type") + " : " + Utils.changeString("UserSharing") + DateUtil.getLogString(SDKEventManager.mContext)));
        statustime = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
        int sn = Utils.getSn();
        JSONObject obj = new JSONObject();
        JSONArray attr = null;
        if (attrs != null) {
            attr = new JSONArray();
            Iterator var6 = attrs.iterator();

            while(var6.hasNext()) {
                String string = (String)var6.next();
                attr.put(string);
            }
        }

        try {
            obj.put("cmd", 1033);
            obj.put("sn", sn);
            obj.put("did", this.getDid());
            obj.put("mac", this.macAddress);
            obj.put("productKey", this.getProductKey());
            obj.put("attrs", attr);
        } catch (JSONException var8) {
            SDKLog.e(var8.toString());
            var8.printStackTrace();
        }

        this.sendMes2Demo(obj);
        if (this.isLAN) {
            this.makeTimer(4000, 1034, sn);
        } else {
            this.makeTimer(20000, 1034, sn);
        }

        SDKLog.a("End <= ");
    }

    public void write(ConcurrentHashMap<String, Object> data, int sn) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", sn: " + sn + ", data: " + data);
        GizWifiErrorCode result = GizWifiErrorCode.GIZ_SDK_SUCCESS;
        SDKLog.b("device_write_start", result.name(), Utils.changeString("mac") + ":" + Utils.changeString(TextUtils.isEmpty(this.getMacAddress()) ? "null" : this.getMacAddress()) + ", " + Utils.changeString("did") + " : " + Utils.changeString(TextUtils.isEmpty(this.did) ? "null" : Utils.dataMasking(this.did)) + ", " + Utils.changeString("mac") + ":" + this.isLAN + Utils.changeString("bind_type") + " : " + Utils.changeString("UserSharing") + DateUtil.getLogString(SDKEventManager.mContext) + ", " + Utils.changeString("data") + ":" + data);
        writetime = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
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
            } catch (JSONException var11) {
                var11.printStackTrace();
            }
        }

        int sn1 = Utils.getSn();
        this.addAppSn(app_sn_queue, sn, sn1);
        boolean isRepeat = false;
        test_sn.add(sn1);

        for(int i = 1; i < test_sn.size(); ++i) {
            if ((Integer)test_sn.get(i) <= (Integer)test_sn.get(i - 1)) {
                isRepeat = true;
            }
        }

        SDKLog.e("mac:" + this.macAddress + "  isRepeat:" + isRepeat);
        JSONObject obj = new JSONObject();

        try {
            obj.put("cmd", 1035);
            obj.put("sn", sn1);
            obj.put("did", this.getDid());
            obj.put("mac", this.getMacAddress());
            obj.put("productKey", this.getProductKey());
            obj.put("data", mobj);
        } catch (JSONException var10) {
            SDKLog.e(var10.toString());
            var10.printStackTrace();
        }

        if (GizMeshLocalControlCenter.sharedInstance().isHasFindDevice(this.getMacAddress())) {
            if (!this.isSubscribed()) {
                this.onDidReceiveData(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_DEVICE_NOT_SUBSCRIBED.getResult()), this, (ConcurrentHashMap)null, (ConcurrentHashMap)null, (ConcurrentHashMap)null, sn);
                return;
            }

            if (!GizJsProtocol.sharedInstance().isHasDataPoint(this.getProductKey())) {
                this.onDidReceiveData(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_UNFIND_PRODUCT_CONFIG_FILE.getResult()), this, (ConcurrentHashMap)null, (ConcurrentHashMap)null, (ConcurrentHashMap)null, sn);
                return;
            }

            if (!GizJsProtocol.sharedInstance().isHasProtocolJs(this.getProductKey())) {
                this.onDidReceiveData(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_UNFIND_PRODUCT_SCRIPT_FILE.getResult()), this, (ConcurrentHashMap)null, (ConcurrentHashMap)null, (ConcurrentHashMap)null, sn);
                return;
            }

            this.makeTimer(4000, 1036, sn1);
            GizMeshLocalControlCenter.sharedInstance().write(this.getProductKey(), this.getMacAddress(), sn1, mobj);
        } else {
            if (this.isJsProtocol()) {
                this.write4Js(mobj, obj, sn, sn1);
                return;
            }

            this.sendMes2Demo(obj);
            if (this.isLowPower) {
                return;
            }

            if (this.isLAN) {
                this.makeTimer(4000, 1036, sn1);
            } else {
                this.makeTimer(20000, 1036, sn1);
            }
        }

        SDKLog.a("End <= ");
    }

    private void write4Js(JSONObject mobj, final JSONObject obj, int sn, int sn1) {
        if (!GizJsProtocol.sharedInstance().isHasDataPoint(this.getProductKey())) {
            this.onDidReceiveData(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_UNFIND_PRODUCT_CONFIG_FILE.getResult()), this, (ConcurrentHashMap)null, (ConcurrentHashMap)null, (ConcurrentHashMap)null, sn);
        } else if (!GizJsProtocol.sharedInstance().isHasProtocolJs(this.getProductKey())) {
            this.onDidReceiveData(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_UNFIND_PRODUCT_SCRIPT_FILE.getResult()), this, (ConcurrentHashMap)null, (ConcurrentHashMap)null, (ConcurrentHashMap)null, sn);
        } else {
            GizJsProtocol.sharedInstance().encodeProtocol(this.getProductKey(), mobj, ((GizLiteGWSubDevice)this).getMeshId(), new GetResult() {
                public void encode(byte[] cmd) {
                    if (cmd != null) {
                        JSONObject encodeCmd = new JSONObject();

                        try {
                            encodeCmd.put("binary", GizWifiBinary.encode(cmd));
                            obj.put("data", encodeCmd);
                        } catch (JSONException var4) {
                            var4.printStackTrace();
                        }

                        SDKLog.e(obj.toString());
                        GizWifiDevice.this.sendMes2Demo(obj);
                    }
                }

                public void decode(String decode) {
                }
            });
            if (this.isLAN) {
                this.makeTimer(4000, 1036, sn1);
            } else {
                this.makeTimer(20000, 1036, sn1);
            }

        }
    }

    /** @deprecated */
    public void write(String jsonData) {
        SDKLog.a("Start => <deprecated>this: " + this.simpleInfoMasking() + ", jsonData: " + jsonData);
        int sn = Utils.getSn();
        JSONObject myobj = new JSONObject();

        try {
            myobj.put("cmd", 1035);
            myobj.put("sn", sn);
            myobj.put("did", this.getDid());
            myobj.put("mac", this.getMacAddress());
            myobj.put("productKey", this.getProductKey());
            String productUI2 = this.getProductUI();
            JSONObject obj = new JSONObject(jsonData);
            if (jsonData.contains("cmd") && obj.getString("cmd").equalsIgnoreCase("2")) {
                this.getDeviceStatus();
            } else if (jsonData.contains("cmd") && obj.getString("cmd").equalsIgnoreCase("1")) {
                JSONObject jsonObject = obj.getJSONObject("entity0");
                myobj.put("data", jsonObject);
            } else {
                myobj.put("data", obj);
            }
        } catch (JSONException var7) {
            var7.printStackTrace();
        }

        this.sendMes2Demo(myobj);
        if (this.isLAN) {
            this.makeTimer(4000, 1036, sn);
        } else {
            this.makeTimer(20000, 1036, sn);
        }

        SDKLog.a("End <= <deprecated>");
    }

    public void setCustomInfo(String remark, String alias) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", remark: " + remark + ", alias: " + alias);
        if (remark == null && alias == null) {
            this.onDidSetCustomInfo(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.getResult()), (GizWifiDevice)null);
            SDKLog.a("End <= ");
        } else {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1041);
                obj.put("sn", sn);
                obj.put("did", this.getDid());
                obj.put("mac", this.getMacAddress());
                obj.put("productKey", this.getProductKey());
                obj.put("remark", remark);
                obj.put("alias", alias);
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            this.sendMes2Demo(obj);
            this.makeTimer(31000, 1042, sn);
            SDKLog.a("End <= ");
        }
    }

    /** @deprecated */
    public void disconnect() {
        SDKLog.a("Start => <deprecated>this: " + this.simpleInfoMasking());
        this.setSubscribe(false);
        this.onDidDisconnected(this);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void login(String uid, String token) {
        SDKLog.a("Start => <deprecated>this: " + this.simpleInfoMasking() + ", uid: " + uid + ", token: " + token);
        if (this.isConnected()) {
            this.onDidLogin(this, Utils.changeErrorCode(GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()));
        } else {
            if (this.loginning) {
                SDKLog.d("device is loginning");
                return;
            }

            this.loginning = true;
        }

        int sn = Utils.getSn();
        this.logintimeout = sn;
        Message obtain = Message.obtain();
        String send = this.macAddress + ":" + this.did;
        obtain.obj = send;
        obtain.what = this.logintimeout;
        SDKLog.d("didlogin start==>" + DateUtil.getCurrentTime());
        if (this.isLAN()) {
            this.hand.sendMessageDelayed(obtain, 10000L);
        } else {
            this.hand.sendMessageDelayed(obtain, 30000L);
        }

        SDKLog.d("Device_Loging   :" + this.logintimeout);
        this.setSubscribe(true);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public boolean isBind(String uid) {
        return this.isBind();
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    protected void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDid() {
        return this.did;
    }

    protected void setDid(String did) {
        this.did = did;
    }

    public String getIPAddress() {
        return this.ipAddress;
    }

    protected void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getProductKey() {
        return this.productKey;
    }

    protected void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getProductKeyAdapter() {
        return this.productKeyAdapter;
    }

    protected void setProductKeyAdapter(String productKeyAdapter) {
        this.productKeyAdapter = productKeyAdapter;
    }

    public String getProductName() {
        return this.productName;
    }

    protected void setProductName(String productName) {
        this.productName = productName;
    }

    public boolean isLAN() {
        return this.isLAN;
    }

    protected void setLAN(boolean isLAN) {
        this.isLAN = isLAN;
    }

    public boolean isDisabled() {
        return this.isDisabled;
    }

    protected void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public boolean isSubscribed() {
        return this.subscribed;
    }

    protected void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public boolean isBind() {
        return this.isBind;
    }

    protected void setIsBind(boolean isBind) {
        this.isBind = isBind;
    }

    public boolean isProductDefined() {
        return this.hasProductDefine;
    }

    protected void setHasProductDefine(boolean hasProductDefine) {
        this.hasProductDefine = hasProductDefine;
    }

    public GizWifiDeviceType getProductType() {
        return this.productType;
    }

    /** @deprecated */
    public XPGWifiDeviceType type() {
        return this.productType == GizWifiDeviceType.GizDeviceCenterControl ? XPGWifiDeviceType.XPGWifiDeviceTypeCenterControl : XPGWifiDeviceType.XPGWifiDeviceTypeNormal;
    }

    protected void setProductType(GizWifiDeviceType productType) {
        this.productType = productType;
    }

    public GizDeviceNetType getNetType() {
        return this.netType;
    }

    protected void setNetType(GizDeviceNetType netType) {
        this.netType = netType;
    }

    public GizWifiDeviceNetStatus getNetStatus() {
        return this.netStatus;
    }

    protected void setNetStatus(GizWifiDeviceNetStatus netStatus) {
        this.netStatus = netStatus;
    }

    /** @deprecated */
    public boolean isOnline() {
        return this.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled || this.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOnline;
    }

    protected boolean getOldIsOnline() {
        return this.oldIsOnline;
    }

    protected void setOldIsOnline(boolean online) {
        this.oldIsOnline = online;
    }

    /** @deprecated */
    public boolean isConnected() {
        return this.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled;
    }

    protected boolean getOldIsConnected() {
        return this.oldIsConnected;
    }

    protected void setOldIsConnected(boolean connected) {
        this.oldIsConnected = connected;
    }

    protected boolean getLoginning() {
        return this.loginning;
    }

    protected void setLoginning(boolean loginning) {
        this.loginning = loginning;
    }

    public String getRemark() {
        return this.remark;
    }

    protected void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isLowPower() {
        return this.isLowPower;
    }

    public void setLowPower(boolean isLowPower) {
        this.isLowPower = isLowPower;
    }

    public boolean isDormant() {
        return this.isDormant;
    }

    public void setDormant(boolean isDormant) {
        this.isDormant = isDormant;
    }

    public int getStateLastTimestamp() {
        return this.stateLastTimestamp;
    }

    public void setStateLastTimestamp(int stateLastTimestamp) {
        this.stateLastTimestamp = stateLastTimestamp;
    }

    public int getSleepDuration() {
        return this.sleepDuration;
    }

    public void setSleepDuration(int sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    public String getAlias() {
        return this.alias;
    }

    protected void setAlias(String alias) {
        this.alias = alias;
    }

    public String getProductUI() {
        return this.productUI;
    }

    protected void setProductUI(String productUI) {
        this.productUI = productUI;
    }

    public String getProductUIAdapter() {
        return this.productUIAdapter;
    }

    protected void setProductUIAdapter(String productUIAdapter) {
        this.productUIAdapter = productUIAdapter;
    }

    private void sendMes2Demo(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    private void makeTimer(int timeout, int cmd, int sn) {
        Message mes = Message.obtain();
        mes.what = sn;
        mes.obj = cmd;
        this.handler.sendMessageDelayed(mes, (long)timeout);
    }

    private boolean isHandler(int sn) {
        boolean hasMessages = this.handler.hasMessages(sn);
        return hasMessages;
    }

    /** @deprecated */
    public String getPasscode() {
        return "";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        this.dest = dest;
        this.flags = flags;
        dest.writeString(this.macAddress);
        dest.writeString(this.did);
        dest.writeString(this.ipAddress);
        dest.writeString(this.productKey);
        dest.writeString(this.productName);
        dest.writeString(this.remark);
        dest.writeString(this.alias);
        dest.writeString(this.productUI);
        dest.writeByte((byte)(this.isLAN ? 1 : 0));
        dest.writeByte((byte)(this.subscribed ? 1 : 0));
        dest.writeByte((byte)(this.isBind ? 1 : 0));
        dest.writeByte((byte)(this.hasProductDefine ? 1 : 0));
        dest.writeByte((byte)(this.isDisabled ? 1 : 0));
        dest.writeSerializable(this.netStatus);
        dest.writeSerializable(this.productType);
    }

    public void updateProduct(String productKey) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking());
        JSONObject obj = new JSONObject();
        int sn = Utils.getSn();

        try {
            obj.put("cmd", 1017);
            obj.put("sn", sn);
            obj.put("productKey", productKey);
        } catch (JSONException var5) {
            SDKLog.e(var5.toString());
            var5.printStackTrace();
        }

        this.sendMes2Demo(obj);
        this.makeTimer(31000, 1018, sn);
        SDKLog.a("End <= ");
    }

    class MessageErrorHandler extends Handler {
        GizWifiDevice object;
        ConcurrentHashMap<String, Object> maps = new ConcurrentHashMap();
        String end;
        long diff;

        public MessageErrorHandler(Looper looper, GizWifiDevice device) {
            super(looper);
            this.object = device;
        }

        public void handleMessage(Message msg) {
            int cmd = (Integer)msg.obj;
            int sn = Integer.valueOf(msg.what);
            SDKLog.d("timeout cmd: " + cmd + ", sn: " + sn + ", device: " + this.object);
            switch(cmd) {
                case 1018:
                    GizWifiDevice.this.handler.removeMessages(msg.what);
                    GizWifiDevice.this.onDidUpdateProduct(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, (String)null, (String)null);
                case 1019:
                case 1020:
                case 1021:
                case 1022:
                case 1023:
                case 1024:
                case 1025:
                case 1026:
                case 1027:
                case 1028:
                case 1029:
                case 1031:
                case 1033:
                case 1035:
                case 1037:
                case 1039:
                case 1041:
                default:
                    break;
                case 1030:
                    GizWifiDevice.this.handler.removeMessages(msg.what);
                    GizWifiDevice.this.onDidSetSubscribe(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), this.object, GizWifiDevice.this.subscribed);
                    break;
                case 1032:
                    GizWifiDevice.this.handler.removeMessages(msg.what);
                    GizWifiDevice.this.onDidSetSubscribe(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), this.object, GizWifiDevice.this.subscribed);
                    break;
                case 1034:
                    GizWifiDevice.this.handler.removeMessages(msg.what);
                    GizWifiDevice.this.onDidReceiveData(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), this.object, this.maps, this.maps, this.maps, 0);
                    this.end = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
                    this.diff = DateUtil.getDiff(GizWifiDevice.statustime, this.end);
                    SDKLog.b("get_device_status_end", GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name(), Utils.changeString("elapsed_time") + ":" + this.diff);
                    break;
                case 1036:
                    GizWifiDevice.this.handler.removeMessages(msg.what);
                    GizWifiDevice var10001 = this.object;
                    int appSn = this.object.removeAppSn(GizWifiDevice.app_sn_queue, sn);
                    GizWifiDevice.this.onDidReceiveData(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), this.object, this.maps, this.maps, this.maps, appSn);
                    this.end = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
                    this.diff = DateUtil.getDiff(this.end, GizWifiDevice.writetime);
                    SDKLog.b("device_write_end", GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name(), Utils.changeString("elapsed_time") + ":" + this.diff);
                    break;
                case 1038:
                    GizWifiDevice.this.handler.removeMessages(msg.what);
                    GizWifiDevice.this.onDidGetHardwareInfo(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), this.object, (ConcurrentHashMap)null, (ConcurrentHashMap)null);
                    break;
                case 1040:
                    GizWifiDevice.this.handler.removeMessages(msg.what);
                    GizWifiDevice.this.onDidExitProductionTesting(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), this.object);
                    break;
                case 1042:
                    GizWifiDevice.this.handler.removeMessages(msg.what);
                    GizWifiDevice.this.onDidSetCustomInfo(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), this.object);
            }

        }
    }
}
