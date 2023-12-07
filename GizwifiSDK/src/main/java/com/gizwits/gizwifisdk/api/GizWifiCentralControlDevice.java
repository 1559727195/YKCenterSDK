//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiCentralControlDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizWifiCentralControlDevice extends GizWifiDevice {
    private static final long serialVersionUID = 6930684980935100518L;
    private static final String TAG = "GizWifiCentralControlDevice";
    protected static final int MSG_RECVE = 5;
    private GizWifiCentralControlDeviceListener mListener;
    private List<GizWifiSubDevice> subDeviceList;
    private List<GizWifiDevice> mysubDeviceList;
    private List<Integer> timeout_sn;
    private static List<ConcurrentHashMap<String, Integer>> messageQueue = new ArrayList();
    public GizWifiCentralControlDevice.MessageErrorHandler timecouthandler;
    private Handler centralHandler;

    protected GizWifiCentralControlDevice(String macAddress, String ipAddress, boolean isLAN, GizWifiDeviceNetStatus netStatus) {
        this.mListener = (GizWifiCentralControlDeviceListener)super.mListener;
        this.subDeviceList = new ArrayList();
        this.mysubDeviceList = new ArrayList();
        this.timeout_sn = new ArrayList();
        this.setMacAddress(macAddress);
        this.setIpAddress(ipAddress);
        this.setLAN(isLAN);
        this.setNetStatus(netStatus);
    }

    private void createCentralHandler() {
        this.timecouthandler = new GizWifiCentralControlDevice.MessageErrorHandler(Looper.getMainLooper());
    }

    protected GizWifiCentralControlDevice() {
        this.mListener = (GizWifiCentralControlDeviceListener)super.mListener;
        this.subDeviceList = new ArrayList();
        this.mysubDeviceList = new ArrayList();
        this.timeout_sn = new ArrayList();
        this.makeCentralHandler();
        this.createCentralHandler();
    }

    public List<GizWifiDevice> getSubDeviceList() {
        return this.mysubDeviceList;
    }

    Handler getCentralHandler() {
        return this.centralHandler;
    }

    Handler getCentralTimerHandler() {
        return this.timecouthandler;
    }

    public void setListener(GizWifiDeviceListener listener) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", listener: " + listener);
        super.setListener(listener);

        try {
            if (listener instanceof GizWifiCentralControlDeviceListener) {
                this.mListener = (GizWifiCentralControlDeviceListener)listener;
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        SDKLog.a("End <= ");
    }

    protected void onSubDeviceDidDiscovered(GizWifiErrorCode result, GizWifiCentralControlDevice device, List<GizWifiSubDevice> subDeviceList) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", device: " + this.simpleInfoMasking());
        if (this.mListener != null) {
            this.mListener.didDiscovered(result, this, subDeviceList);
            this.mListener.didDiscovered(Utils.changeErrorCode(result.getResult()), subDeviceList);
            SDKLog.d("Callback end");
        }

    }

    protected void OnDidUpdateSubDevices(GizWifiCentralControlDevice device, GizWifiErrorCode result, List<GizWifiDevice> subDeviceList) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", device: " + this.simpleInfoMasking() + ", subDeviceList: " + SDKEventManager.listMasking(subDeviceList));
        if (this.mListener != null) {
            this.mListener.didUpdateSubDevices(device, result, subDeviceList);
            SDKLog.d("Callback end");
        }

    }

    /** @deprecated */
    public void getSubDevices() {
        SDKLog.a("Start => <deprecated> device: " + this.simpleInfoMasking());
        GizWifiErrorCode result = GizWifiErrorCode.GIZ_SDK_UNSUPPORTED_API;
        this.onSubDeviceDidDiscovered(result, this, (List)null);
        SDKLog.a("End <= <deprecated>");
    }

    public void updateSubDevices() {
        SDKLog.a("Start => this: " + this.simpleInfoMasking());
        int sn = Utils.getSn();
        JSONObject obj = new JSONObject();

        try {
            obj.put("cmd", 1019);
            obj.put("sn", sn);
            obj.put("mac", this.getMacAddress());
            obj.put("did", this.getDid());
            obj.put("productKey", this.getProductKey());
        } catch (JSONException var4) {
            SDKLog.d(var4.toString());
            var4.printStackTrace();
        }

        this.sendMes2Demo(obj);
        if (this.isLAN) {
            this.makeTimer(this.timecouthandler, 4000, 1020, sn);
        } else {
            this.makeTimer(this.timecouthandler, 20000, 1020, sn);
        }

        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void addSubDevice() {
        SDKLog.a("Start => <deprecated> device: " + this.simpleInfoMasking());
        this.addSubDevice((List)null);
        SDKLog.a("End <= <deprecated>");
    }

    public void addSubDevice(List<String> deviceMacs) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", deviceMacs: " + deviceMacs);
        int sn = Utils.getSn();
        JSONObject obj = new JSONObject();

        try {
            obj.put("cmd", 1021);
            obj.put("sn", sn);
            obj.put("mac", this.getMacAddress());
            obj.put("did", this.getDid());
            obj.put("productKey", this.getProductKey());
            if (deviceMacs != null && deviceMacs.size() > 0) {
                JSONArray maclist = new JSONArray();
                Iterator var5 = deviceMacs.iterator();

                while(var5.hasNext()) {
                    String string = (String)var5.next();
                    maclist.put(string);
                }

                obj.put("subDeviceMacs", maclist);
            }
        } catch (JSONException var7) {
            SDKLog.e(var7.toString());
            var7.printStackTrace();
        }

        this.sendMes2Demo(obj);
        if (this.isLAN) {
            this.makeTimer(this.timecouthandler, 4000, 1022, sn);
        } else {
            this.makeTimer(this.timecouthandler, 20000, 1022, sn);
        }

        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void deleteSubDevice(String deviceID) {
        SDKLog.a("Start => <deprecated>, this: " + this.simpleInfoMasking() + ", deviceID:" + Utils.dataMasking(deviceID));
        GizWifiErrorCode result = GizWifiErrorCode.GIZ_SDK_UNSUPPORTED_API;
        this.onSubDeviceDidDiscovered(result, this, this.subDeviceList);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void deleteSubDevice(GizWifiDevice device) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", device: " + (device == null ? "null" : device.simpleInfoMasking()));
        ArrayList devices;
        if (device == null) {
            devices = new ArrayList();
            this.OnDidUpdateSubDevices(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, devices);
        } else {
            devices = new ArrayList();
            devices.add(device);
            this.deleteSubDeviceList(devices, true);
            SDKLog.a("End <= ");
        }
    }

    public void deleteSubDevices(List<GizWifiDevice> deviceList) {
        SDKLog.a("Start => this: " + this.simpleInfoMasking() + ", device: " + (deviceList == null ? "null" : SDKEventManager.listMasking(deviceList)));
        if (deviceList != null && deviceList.size() != 0) {
            this.deleteSubDeviceList(deviceList, false);
            SDKLog.a("End <= ");
        } else {
            List<GizWifiDevice> list = new ArrayList();
            this.OnDidUpdateSubDevices(this, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
        }
    }

    private void deleteSubDeviceList(List<GizWifiDevice> deviceList, boolean isOld) {
        int sn = Utils.getSn();
        JSONObject obj = new JSONObject();

        try {
            obj.put("cmd", 1023);
            obj.put("sn", sn);
            obj.put("mac", this.getMacAddress());
            obj.put("did", this.getDid());
            obj.put("productKey", this.getProductKey());
            obj.put("isOld", isOld);
            JSONArray array = new JSONArray();
            Iterator var6 = deviceList.iterator();

            while(var6.hasNext()) {
                GizWifiDevice device = (GizWifiDevice)var6.next();
                JSONObject myob = new JSONObject();
                myob.put("mac", device.getMacAddress());
                myob.put("did", device.getDid());
                myob.put("productKey", device.getProductKey());
                array.put(myob);
            }

            obj.put("subDevices", array);
        } catch (JSONException var9) {
            SDKLog.e(var9.toString());
            var9.printStackTrace();
        }

        this.sendMes2Demo(obj);
        if (this.isLAN) {
            this.makeTimer(this.timecouthandler, 9000, 1024, sn);
        } else {
            this.makeTimer(this.timecouthandler, 20000, 1024, sn);
        }

    }

    protected void setSubDeviceList(List<GizWifiSubDevice> subDeviceList) {
        this.subDeviceList = subDeviceList;
    }

    private void sendMes2Demo(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    private void makeCentralHandler() {
        this.centralHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 5:
                        String jsonStr = (String)msg.obj;

                        try {
                            JSONObject obj = new JSONObject(jsonStr);
                            int cmd = Integer.parseInt(obj.getString("cmd"));
                            int sn = -99999;
                            if (cmd > 2000) {
                                sn = 0;
                            } else {
                                sn = Integer.parseInt(obj.getString("sn"));
                            }

                            GizWifiCentralControlDevice.this.didSetListener(cmd, obj, sn, GizWifiCentralControlDevice.this.mListener);
                        } catch (JSONException var6) {
                            var6.printStackTrace();
                        }

                        SDKLog.d("recv jsonStr from GizWifiSDKDaemon in child thread, jsonStr = " + (jsonStr == null ? "null" : jsonStr.length()));
                    default:
                }
            }
        };
    }

    private void didSetListener(int cmd, JSONObject obj, int sn, GizWifiCentralControlDeviceListener listener) throws JSONException {
        JSONArray subDevices_json;
        int i;
        JSONObject ob;
        int errorCode;
        ArrayList list;
        switch(cmd) {
            case 1020:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                subDevices_json = obj.has("subDevices") ? obj.getJSONArray("subDevices") : null;
                this.removeMessageQueue(this.timecouthandler, cmd, sn);
                if (errorCode != GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                     list = new ArrayList();
                    this.OnDidUpdateSubDevices(this, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    this.mysubDeviceList.clear();
                    if (subDevices_json != null && subDevices_json.length() > 0) {
                        for(i = 0; i < subDevices_json.length(); ++i) {
                            ob = (JSONObject)subDevices_json.get(i);
                            this.getMyDeviceByCentralDevice(ob, (JSONObject)null);
                        }

                        Collections.sort(this.mysubDeviceList, new Comparator<GizWifiDevice>() {
                            public int compare(GizWifiDevice arg0, GizWifiDevice arg1) {
                                int ordinal = arg0.getNetStatus().ordinal();
                                int ordinal1 = arg1.getNetStatus().ordinal();
                                return ordinal1 - ordinal;
                            }
                        });
                    }

                    this.OnDidUpdateSubDevices(this, GizWifiErrorCode.GIZ_SDK_SUCCESS, this.mysubDeviceList);
                }
                break;
            case 1022:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                this.removeMessageQueue(this.timecouthandler, cmd, sn);
                if (errorCode != GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                    list = new ArrayList();
                    this.OnDidUpdateSubDevices(this, GizWifiErrorCode.valueOf(errorCode), list);
                } else {
                    this.OnDidUpdateSubDevices(this, GizWifiErrorCode.valueOf(errorCode), this.mysubDeviceList);
                }

                this.onSubDeviceDidDiscovered(GizWifiErrorCode.valueOf(errorCode), this, (List)null);
                break;
            case 1024:
                errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
                if (errorCode != GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                    this.removeMessageQueue(this.timecouthandler, cmd, sn);
                    list = new ArrayList();
                    this.OnDidUpdateSubDevices(this, GizWifiErrorCode.valueOf(errorCode), list);
                }
                break;
            case 2002:
                JSONObject ccDevice_json = obj.has("centralDevice") ? obj.getJSONObject("centralDevice") : null;
                subDevices_json = obj.has("subDevices") ? obj.getJSONArray("subDevices") : null;
                this.removeMessageQueue(this.timecouthandler, cmd, sn);
                if (ccDevice_json != null && subDevices_json != null) {
                    this.mysubDeviceList.clear();

                    for(i = 0; i < subDevices_json.length(); ++i) {
                        ob = (JSONObject)subDevices_json.get(i);
                        this.getMyDeviceByCentralDevice(ob, ccDevice_json);
                    }

                    Collections.sort(this.mysubDeviceList, new Comparator<GizWifiDevice>() {
                        public int compare(GizWifiDevice arg0, GizWifiDevice arg1) {
                            int ordinal = arg0.getNetStatus().ordinal();
                            int ordinal1 = arg1.getNetStatus().ordinal();
                            return ordinal1 - ordinal;
                        }
                    });
                    this.OnDidUpdateSubDevices(this, GizWifiErrorCode.GIZ_SDK_SUCCESS, this.mysubDeviceList);
                } else {
                    SDKLog.d("Ignored notify message data: no centralDevice or subDevices");
                }
        }

    }

    private void addMessageQueue(int cmd, int sn) {
        ConcurrentHashMap<String, Integer> message = new ConcurrentHashMap();
        message.put("cmd", cmd);
        message.put("sdkSn", sn);
        messageQueue.add(message);
        SDKLog.d("add the message: <cmd: " + cmd + ", sn: " + sn + ">");
    }

    private void removeMessageQueue(Handler handler, int cmd, int sn) {
        Message msg;
        if (cmd == 2002) {
            for(int i = 0; i < messageQueue.size(); ++i) {
                ConcurrentHashMap<String, Integer> message = (ConcurrentHashMap)messageQueue.get(i);
                if ((Integer)message.get("cmd") == 1024) {
                    int expectSn = (Integer)message.get("sdkSn");
                    SDKLog.d("find expect message to remove by 2002: <cmd=1024, sn= " + expectSn + ">");
                    messageQueue.remove(i--);
                    msg = handler.hasMessages(expectSn) ? handler.obtainMessage(expectSn) : null;
                    if (msg != null) {
                        handler.removeMessages(expectSn);
                        SDKLog.d("remove the handler message: <cmd=1024, sn= " + expectSn + ">");
                    } else {
                        SDKLog.d("no handler message to remove: <cmd=1024, sn= " + expectSn + ">");
                    }
                }
            }

        } else {
            ConcurrentHashMap<String, Integer> expectMessage = null;

            for(int i = 0; i < messageQueue.size(); ++i) {
                ConcurrentHashMap<String, Integer> message = (ConcurrentHashMap)messageQueue.get(i);
                if ((Integer)message.get("cmd") == cmd && (Integer)message.get("sdkSn") == sn) {
                    SDKLog.d("find expect message to remove: <cmd=" + cmd + ", sn= " + sn + ">");
                    expectMessage = message;
                    msg = handler.hasMessages(sn) ? handler.obtainMessage(sn) : null;
                    if (msg != null) {
                        SDKLog.d("remove the handler message: <cmd=" + cmd + ", sn= " + sn + ">");
                        handler.removeMessages(sn);
                    } else {
                        SDKLog.d("no handler message to remove: <cmd=" + cmd + ", sn= " + sn + ">");
                    }
                    break;
                }
            }

            if (expectMessage != null) {
                SDKLog.d("remove the messageQueue message: <cmd=" + cmd + ", sn= " + sn + ">");
                messageQueue.remove(expectMessage);
            } else {
                SDKLog.d("no messageQueue message to remove: <cmd=" + cmd + ", sn= " + sn + ">");
            }

        }
    }

    private void makeTimer(Handler handler, int timeout, int cmd, int sn) {
        Message mes = handler.obtainMessage();
        mes.what = sn;
        mes.obj = cmd;
        handler.sendMessageDelayed(mes, (long)timeout);
        this.addMessageQueue(cmd, sn);
    }

    private void mytimeoutmethod(List<GizWifiDevice> list) {
        this.OnDidUpdateSubDevices(this, GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), list);
    }

    private boolean isHandler(int sn) {
        boolean hasMessages = this.timecouthandler.hasMessages(sn);
        return hasMessages;
    }

    private void getMyDeviceByCentralDevice(JSONObject ob, JSONObject rootOb) {
        GizWifiDevice root = null;
        List<GizWifiDevice> deviceList = SDKEventManager.getInstance().getTotalDeviceList();
        synchronized(deviceList) {
            try {
                Iterator var6;
                GizWifiDevice gizWifiDevice;
                if (rootOb == null) {
                    root = this;
                } else {
                    var6 = deviceList.iterator();

                    while(var6.hasNext()) {
                        gizWifiDevice = (GizWifiDevice)var6.next();
                        if (gizWifiDevice.getMacAddress().equals(rootOb.getString("mac")) && gizWifiDevice.getDid().equals(rootOb.getString("did")) && gizWifiDevice.getProductKey().equals(rootOb.getString("productKey")) && gizWifiDevice.getProductType() == GizWifiDeviceType.GizDeviceCenterControl) {
                            root = gizWifiDevice;
                            break;
                        }
                    }
                }

                var6 = deviceList.iterator();

                while(var6.hasNext()) {
                    gizWifiDevice = (GizWifiDevice)var6.next();
                    if (gizWifiDevice.getMacAddress().equals(ob.getString("mac")) && gizWifiDevice.getDid().equals(ob.getString("did")) && gizWifiDevice.getProductKey().equals(ob.getString("productKey"))) {
                        gizWifiDevice.setRootDevice((GizWifiDevice)root);
                        String name = ob.has("productName") ? ob.getString("productName") : gizWifiDevice.getProductName();
                        gizWifiDevice.setProductName(name);
                        this.mysubDeviceList.add(gizWifiDevice);
                        break;
                    }

                    gizWifiDevice.setRootDevice((GizWifiDevice)null);
                }
            } catch (JSONException var10) {
                var10.printStackTrace();
            }

        }
    }

    class MessageErrorHandler extends Handler {
        public MessageErrorHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int cmd = (Integer)msg.obj;
            SDKLog.d("timeout cmd: " + cmd + ", sn: " + msg.what);
            List<GizWifiDevice> list = new ArrayList();
            switch(cmd) {
                case 1020:
                    GizWifiCentralControlDevice.this.removeMessageQueue(GizWifiCentralControlDevice.this.timecouthandler, (Integer)msg.obj, msg.what);
                    GizWifiCentralControlDevice.this.onSubDeviceDidDiscovered(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiCentralControlDevice.this, (List)null);
                    GizWifiCentralControlDevice.this.mytimeoutmethod(list);
                case 1021:
                case 1023:
                default:
                    break;
                case 1022:
                    GizWifiCentralControlDevice.this.removeMessageQueue(GizWifiCentralControlDevice.this.timecouthandler, (Integer)msg.obj, msg.what);
                    GizWifiCentralControlDevice.this.onSubDeviceDidDiscovered(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiCentralControlDevice.this, (List)null);
                    GizWifiCentralControlDevice.this.mytimeoutmethod(list);
                    break;
                case 1024:
                    GizWifiCentralControlDevice.this.removeMessageQueue(GizWifiCentralControlDevice.this.timecouthandler, (Integer)msg.obj, msg.what);
                    GizWifiCentralControlDevice.this.onSubDeviceDidDiscovered(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiCentralControlDevice.this, (List)null);
                    GizWifiCentralControlDevice.this.mytimeoutmethod(list);
            }

        }
    }
}
