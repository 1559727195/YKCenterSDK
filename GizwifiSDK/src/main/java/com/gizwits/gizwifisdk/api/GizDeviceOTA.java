//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.gizwits.gizwifisdk.enumration.GizOTAFirmwareType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceOTAListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceOTA {
    private static final int MSG_RECV = 5;
    private static GizDeviceOTAListener mListener;
    static Handler han = new Handler(Looper.getMainLooper()) {
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

                        if (GizDeviceOTA.mListener != null) {
                            GizDeviceOTA.didSetListener(cmd, obj, GizDeviceOTA.mListener, sn);
                        }
                    } catch (NumberFormatException var7) {
                        var7.printStackTrace();
                    } catch (JSONException var8) {
                        var8.printStackTrace();
                    }
                default:
            }
        }
    };
    static Handler timeHan = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            int cmd = (Integer)msg.obj;
            switch(cmd) {
                case 1202:
                    GizDeviceOTA.timeHan.removeMessages(msg.what);
                    ConcurrentHashMap<String, String> map = new ConcurrentHashMap();
                    GizDeviceOTA.OnDidCheckDeviceUpdate(GizDeviceOTA.checkUpdateDevice, GizWifiErrorCode.valueOf(8308), map, map);
                    break;
                case 1204:
                    GizDeviceOTA.timeHan.removeMessages(msg.what);
                    GizDeviceOTA.OnDidUpgradeDevice(GizDeviceOTA.upDateDevice, GizWifiErrorCode.valueOf(8308), (GizOTAFirmwareType)null);
            }

        }
    };
    private static GizWifiDevice upDateDevice;
    private static GizWifiDevice checkUpdateDevice;

    public GizDeviceOTA() {
    }

    private static void sendMes2Demo(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    public static void setListener(GizDeviceOTAListener Listener) {
        SDKLog.a("Start => , Listener: " + (Listener == null ? "null" : Listener));
        mListener = Listener;
        SDKLog.a("End <= ");
    }

    public static void checkDeviceUpdate(String uid, String token, GizWifiDevice device) {
        SDKLog.a("Start => uid: " + uid + ", token: " + Utils.dataMasking(token));
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap();
        if (!Constant.ishandshake) {
            OnDidCheckDeviceUpdate(device, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, map, map);
            SDKLog.a("End <= ");
        } else if (device == null) {
            OnDidCheckDeviceUpdate(device, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, map, map);
            SDKLog.a("End <= ");
        } else {
            SDKLog.d("deviceid :" + Utils.dataMasking(device.getDid()));
            checkUpdateDevice = device;
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1201);
                obj.put("sn", sn);
                obj.put("uid", uid);
                obj.put("token", token);
                obj.put("did", device.getDid());
            } catch (JSONException var7) {
                SDKLog.e(var7.toString());
                var7.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(31000, 1202, sn);
            SDKLog.a("End <= ");
        }
    }

    public static void upgradeDevice(String uid, String token, GizWifiDevice device, GizOTAFirmwareType firmwareType) {
        SDKLog.a("Start => uid: " + uid + ", token: " + Utils.dataMasking(token) + ", device: " + (device == null ? "null" : device.simpleInfoMasking()) + ", firmwareType: " + (firmwareType == null ? "null" : firmwareType.name()));
        if (!Constant.ishandshake) {
            OnDidUpgradeDevice(device, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, firmwareType);
            SDKLog.a("End <= ");
        } else if (device != null && firmwareType != null) {
            upDateDevice = device;
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1203);
                obj.put("sn", sn);
                obj.put("uid", uid);
                obj.put("token", token);
                obj.put("did", device.getDid());
                obj.put("firmwareType", firmwareType.ordinal());
            } catch (JSONException var7) {
                SDKLog.e(var7.toString());
                var7.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(31000, 1204, sn);
            SDKLog.a("End <= ");
        } else {
            OnDidUpgradeDevice(device, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, firmwareType);
            SDKLog.a("End <= ");
        }
    }

    private static void didSetListener(int cmd, JSONObject obj, GizDeviceOTAListener mListener, int sn) throws JSONException {
        String did;
        GizWifiDevice device;
        switch(cmd) {
            case 1202:
                int errocode = obj.getInt("errorCode");
                timeHan.removeMessages(sn);
                did = "";
                if (obj.has("did")) {
                    did = obj.getString("did");
                }

                ConcurrentHashMap<String, String> latestVersion = new ConcurrentHashMap();
                if (obj.has("wifiVersion")) {
                    JSONObject object = obj.getJSONObject("wifiVersion");
                    String mcu;
                    if (object.has("latest")) {
                        mcu = object.getString("latest");
                        latestVersion.put("latest", mcu);
                    }

                    if (object.has("current")) {
                        mcu = object.getString("current");
                        latestVersion.put("current", mcu);
                    }
                }

                ConcurrentHashMap<String, String> currentVersion = new ConcurrentHashMap();
                if (obj.has("mcuVersion")) {
                    JSONObject object = obj.getJSONObject("mcuVersion");
                    String mcu;
                    if (object.has("latest")) {
                        mcu = object.getString("latest");
                        currentVersion.put("latest", mcu);
                    }

                    if (object.has("current")) {
                        mcu = object.getString("current");
                        currentVersion.put("current", mcu);
                    }
                }

                device = findDeviceByDid(did);
                OnDidCheckDeviceUpdate(device, GizWifiErrorCode.valueOf(errocode), latestVersion, currentVersion);
                break;
            case 1204:
                timeHan.removeMessages(sn);
                int err = obj.getInt("errorCode");
                did = null;
                if (obj.has("did")) {
                    did = obj.getString("did");
                }

                int type = -1;
                if (obj.has("firmwareType")) {
                    type = obj.getInt("firmwareType");
                }

                device = findDeviceByDid(did);
                OnDidUpgradeDevice(device, GizWifiErrorCode.valueOf(err), GizOTAFirmwareType.valueOf(type));
                break;
            case 2007:
                did = null;
                if (obj.has("did")) {
                    did = obj.getString("did");
                }

                ConcurrentHashMap<String, String> mlatestVersion = new ConcurrentHashMap();
                if (obj.has("wifiVersion")) {
                    JSONObject object = obj.getJSONObject("wifiVersion");
                    String mcu;
                    if (object.has("latest")) {
                        mcu = object.getString("latest");
                        mlatestVersion.put("latest", mcu);
                    }

                    if (object.has("current")) {
                        mcu = object.getString("current");
                        mlatestVersion.put("current", mcu);
                    }
                }

                ConcurrentHashMap<String, String> mcurrentVersion = new ConcurrentHashMap();
                if (obj.has("mcuVersion")) {
                    JSONObject object = obj.getJSONObject("mcuVersion");
                    String mcu;
                    if (object.has("latest")) {
                        mcu = object.getString("latest");
                        mcurrentVersion.put("latest", mcu);
                    }

                    if (object.has("current")) {
                        mcu = object.getString("current");
                        mcurrentVersion.put("current", mcu);
                    }
                }

                device = findDeviceByDid(did);
                OnDidNotifyDeviceUpdate(device, mlatestVersion, mcurrentVersion);
                break;
            case 2009:
                did = null;
                if (obj.has("did")) {
                    did = obj.getString("did");
                }

                int upgradeStatus = -1;
                if (obj.has("upgradeStatus")) {
                    upgradeStatus = obj.getInt("upgradeStatus");
                }

                int mytyep = -1;
                if (obj.has("firmwareType")) {
                    mytyep = obj.getInt("firmwareType");
                }

                device = findDeviceByDid(did);
                OnDidNotifyDeviceUpgradeStatus(device, GizOTAFirmwareType.valueOf(mytyep), GizWifiErrorCode.valueOf(upgradeStatus));
        }

    }

    private static GizWifiDevice findDeviceByDid(String did) {
        GizWifiDevice device = null;
        List<GizWifiDevice> deviceList = SDKEventManager.getInstance().getTotalDeviceList();
        synchronized(deviceList) {
            Iterator var4 = deviceList.iterator();

            while(var4.hasNext()) {
                GizWifiDevice gizWifiDevice = (GizWifiDevice)var4.next();
                if (gizWifiDevice.getDid().equals(did)) {
                    device = gizWifiDevice;
                    break;
                }
            }

            return device;
        }
    }

    private static void OnDidNotifyDeviceUpgradeStatus(GizWifiDevice device, GizOTAFirmwareType firmwareType, GizWifiErrorCode upgradeStatus) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, device: " + (device == null ? "null" : device.simpleInfoMasking()) + "firmwareType: " + firmwareType.name() + "upgradeStatus: " + upgradeStatus.name());
        if (mListener != null) {
            mListener.didNotifyDeviceUpgradeStatus(device, firmwareType, upgradeStatus);
            SDKLog.d("Callback end");
        }

    }

    private static void OnDidNotifyDeviceUpdate(GizWifiDevice device, ConcurrentHashMap<String, String> wifiVersion, ConcurrentHashMap<String, String> mcuVersion) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, device: " + (device == null ? "null" : device.simpleInfoMasking()) + "wifiVersion: " + wifiVersion + "mcuVersion: " + mcuVersion);
        if (mListener != null) {
            mListener.didNotifyDeviceUpdate(device, wifiVersion, mcuVersion);
            SDKLog.d("Callback end");
        }

    }

    private static void OnDidUpgradeDevice(GizWifiDevice device, GizWifiErrorCode result, GizOTAFirmwareType firmwareType) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", device: " + (device == null ? "null" : device.simpleInfoMasking()) + ", firmwareType: " + firmwareType);
        if (mListener != null) {
            mListener.didUpgradeDevice(device, result, firmwareType);
            SDKLog.d("Callback end");
        }

    }

    private static void OnDidCheckDeviceUpdate(GizWifiDevice device, GizWifiErrorCode result, ConcurrentHashMap<String, String> wifiVersion, ConcurrentHashMap<String, String> mcuVersion) {
        SDKLog.d("Ready to callback, listener: listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", device: " + (device == null ? "null" : device.simpleInfoMasking()) + "wifiVersion: " + wifiVersion + "mcuVersion: " + mcuVersion);
        if (mListener != null) {
            mListener.didCheckDeviceUpdate(device, result, wifiVersion, mcuVersion);
            SDKLog.d("Callback end");
        }

    }

    private static void makeTimer(int timeout, int cmd, int sn) {
        Message mes = Message.obtain();
        mes.what = sn;
        mes.obj = cmd;
        timeHan.sendMessageDelayed(mes, (long)timeout);
    }
}
