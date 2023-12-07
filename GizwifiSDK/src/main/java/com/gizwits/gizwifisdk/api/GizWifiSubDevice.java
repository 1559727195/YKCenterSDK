//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;
import org.json.JSONObject;

/** @deprecated */
public class GizWifiSubDevice extends GizWifiDevice implements Parcelable {
    private static final long serialVersionUID = 5198208292322700232L;
    protected static final int MSG_RECVE = 5;
    private String subDid;
    private String subProductKey;
    private String subProductName;
    private String macAddress;
    private String did;
    private String productUI;
    protected GizWifiDeviceListener mListener;
    public static final Creator<GizWifiSubDevice> CREATOR = new Creator<GizWifiSubDevice>() {
        public GizWifiSubDevice createFromParcel(Parcel source) {
            GizWifiSubDevice subdevice = new GizWifiSubDevice();
            subdevice.subDid = source.readString();
            subdevice.subProductKey = source.readString();
            subdevice.subProductName = source.readString();
            subdevice.macAddress = source.readString();
            subdevice.did = source.readString();
            subdevice.productUI = source.readString();
            return null;
        }

        public GizWifiSubDevice[] newArray(int size) {
            return new GizWifiSubDevice[size];
        }
    };
    private GizWifiSubDevice.MessageErrorHandler handler;
    private Handler subHandler;

    public String getProductUI() {
        return this.productUI;
    }

    protected void setProductUI(String productUI) {
        this.productUI = productUI;
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

    public void setDid(String did) {
        this.did = did;
    }

    protected GizWifiSubDevice() {
        this.createSubHandler();
        this.createSubMainHandler();
    }

    Handler getSubHandler() {
        return this.subHandler;
    }

    Handler getSubTimerHandler() {
        return this.handler;
    }

    public void setListener(GizWifiDeviceListener Listener) {
        super.setListener(Listener);
    }

    private void createSubMainHandler() {
        this.subHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 5:
                        String jsonStr = (String)msg.obj;

                        try {
                            JSONObject obj = new JSONObject(jsonStr);
                            int cmd = Integer.parseInt(obj.getString("cmd"));
                            int sn = -99999;
                            if (cmd > 2000) {
                                sn = cmd;
                            } else {
                                sn = Integer.parseInt(obj.getString("sn"));
                            }

                            GizWifiSubDevice.this.didSetListener(cmd, obj, sn);
                        } catch (JSONException var6) {
                            var6.printStackTrace();
                        }
                    default:
                }
            }
        };
    }

    private void didSetListener(int cmd, JSONObject obj, int sn) throws JSONException {
        ConcurrentHashMap maps_v4a1;
        int errorcode;
        List maps;
        ConcurrentHashMap maps_v4a2;
        switch(cmd) {
            case 1034:
                if (this.isHandler(sn)) {
                    this.handler.removeMessages(sn);

                    try {
                        errorcode = Integer.parseInt(obj.getString("errorCode"));
                        maps = this.parseDeviceStatusForKey(obj, "attrStatus");
                        maps_v4a1 = (ConcurrentHashMap)maps.get(0);
                        maps_v4a2 = (ConcurrentHashMap)maps.get(1);
                        if (maps_v4a1.size() != 0 && errorcode == 0) {
                            this.onDidReceiveData(GizWifiErrorCode.valueOf(errorcode), this, maps_v4a1, maps_v4a2, (ConcurrentHashMap)null, sn);
                        } else if (errorcode != 0) {
                            this.onDidReceiveData(GizWifiErrorCode.valueOf(errorcode), this, maps_v4a1, maps_v4a2, (ConcurrentHashMap)null, sn);
                        }
                    } catch (NumberFormatException var20) {
                        var20.printStackTrace();
                    } catch (JSONException var21) {
                        var21.printStackTrace();
                    }
                }
                break;
            case 1036:
                if (this.isHandler(sn)) {
                    this.handler.removeMessages(sn);

                    try {
                        errorcode = Integer.parseInt(obj.getString("errorCode"));
                        maps = this.parseDeviceStatusForKey(obj, "attrStatus");
                        maps_v4a1 = (ConcurrentHashMap)maps.get(0);
                        maps_v4a2 = (ConcurrentHashMap)maps.get(1);
                        this.onDidReceiveData(GizWifiErrorCode.valueOf(errorcode), this, maps_v4a1, maps_v4a2, (ConcurrentHashMap)null, Constant.sn);
                    } catch (NumberFormatException var16) {
                        var16.printStackTrace();
                    } catch (JSONException var17) {
                        var17.printStackTrace();
                    }
                }
                break;
            case 2003:
                try {
                    String mac_notify = obj.getString("mac");
                    String did_notify = obj.getString("did");
                    String netStatus_notify = obj.getString("netStatus");
                    List<GizWifiDevice> listall = SDKEventManager.getInstance().getDeviceListByProductKeys();
                    GizWifiDeviceNetStatus status = GizWifiDeviceNetStatus.GizDeviceUnavailable;
                    if (netStatus_notify.equalsIgnoreCase("offline")) {
                        status = GizWifiDeviceNetStatus.GizDeviceOffline;
                    } else if (netStatus_notify.equalsIgnoreCase("online")) {
                        status = GizWifiDeviceNetStatus.GizDeviceOnline;
                    } else if (netStatus_notify.equalsIgnoreCase("controlled")) {
                        status = GizWifiDeviceNetStatus.GizDeviceControlled;
                    }

                    GizWifiDevice device_list = null;
                    GizWifiDevice device_netstatus_notify = null;

                    label95:
                    for(int i = 0; i < listall.size(); ++i) {
                        device_list = (GizWifiDevice)listall.get(i);
                        if (device_list.getMacAddress().equals(mac_notify) && device_list.getDid().equals(did_notify)) {
                            if (device_list.getProductType() == GizWifiDeviceType.GizDeviceCenterControl && obj.has("subdid")) {
                                GizWifiCentralControlDevice device = (GizWifiCentralControlDevice)device_list;
                                List<GizWifiSubDevice> subDeviceList = new ArrayList();
                                Iterator var14 = subDeviceList.iterator();

                                while(true) {
                                    if (!var14.hasNext()) {
                                        break label95;
                                    }

                                    GizWifiSubDevice gizWifiSubDevice = (GizWifiSubDevice)var14.next();
                                    if (gizWifiSubDevice.getSubDid().equals(obj.getString("subdid"))) {
                                        device_netstatus_notify = gizWifiSubDevice;
                                    }
                                }
                            }

                            device_netstatus_notify = device_list;
                            break;
                        }
                    }

                    if (device_netstatus_notify != null) {
                        ((GizWifiDevice)device_netstatus_notify).setNetStatus(status);
                        ((GizWifiDevice)device_netstatus_notify).onDidUpdateNetStatus((GizWifiDevice)device_netstatus_notify, status);
                        if (((GizWifiDevice)device_netstatus_notify).getOldIsOnline() != ((GizWifiDevice)device_netstatus_notify).isOnline()) {
                            ((GizWifiDevice)device_netstatus_notify).setOldIsOnline(((GizWifiDevice)device_netstatus_notify).isOnline());
                            ((GizWifiDevice)device_netstatus_notify).onDidDeviceOnline((GizWifiDevice)device_netstatus_notify, ((GizWifiDevice)device_netstatus_notify).isOnline());
                        }

                        if (!((GizWifiDevice)device_netstatus_notify).isConnected() && ((GizWifiDevice)device_netstatus_notify).getOldIsConnected() != ((GizWifiDevice)device_netstatus_notify).isConnected()) {
                            ((GizWifiDevice)device_netstatus_notify).setOldIsConnected(((GizWifiDevice)device_netstatus_notify).isConnected());
                            ((GizWifiDevice)device_netstatus_notify).onDidDisconnected((GizWifiDevice)device_netstatus_notify);
                        }

                        if (((GizWifiDevice)device_netstatus_notify).isConnected() && ((GizWifiDevice)device_netstatus_notify).getLoginning()) {
                            ((GizWifiDevice)device_netstatus_notify).onDidLogin((GizWifiDevice)device_netstatus_notify, GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult());
                            ((GizWifiDevice)device_netstatus_notify).setOldIsConnected(((GizWifiDevice)device_netstatus_notify).isConnected());
                        }
                    } else {
                        SDKLog.d("Cann't deal with the netStatus notify: , the device is strange, mac: " + mac_notify + ", did: " + Utils.dataMasking(did_notify));
                    }
                } catch (JSONException var19) {
                    var19.printStackTrace();
                }
                break;
            case 2006:
                try {
                     maps = this.parseDeviceStatusForKey(obj, "attrSatus");
                    maps_v4a1 = (ConcurrentHashMap)maps.get(0);
                    maps_v4a1 = (ConcurrentHashMap)maps.get(1);
                    this.onDidReceiveData(GizWifiErrorCode.GIZ_SDK_SUCCESS, this, maps_v4a1, maps_v4a1, (ConcurrentHashMap)null, sn);
                } catch (JSONException var18) {
                    var18.printStackTrace();
                }
        }

    }

    private List<ConcurrentHashMap<String, Object>> parseSubDeviceStatus(JSONObject obj) throws JSONException {
        List<ConcurrentHashMap<String, Object>> map_v4a1_v4a2 = new ArrayList();
        ConcurrentHashMap<String, Object> map_v4a1 = new ConcurrentHashMap();
        ConcurrentHashMap<String, Object> map_v4a2 = new ConcurrentHashMap();
        JSONObject attrstatus_jsonobj = null;
        if (obj.has("attrStatus")) {
            attrstatus_jsonobj = obj.getJSONObject("attrStatus");
        }

        if (attrstatus_jsonobj == null) {
            map_v4a1_v4a2.add(map_v4a1);
            map_v4a1_v4a2.add(map_v4a2);
            return map_v4a1_v4a2;
        } else {
            JSONObject status_jsonobj = null;
            JSONObject extdata_jsonobj = null;
            JSONObject enumData_jsonobj = null;
            String binary_jsonobj = null;
            JSONObject faults_jsonobj = null;
            JSONObject alerts_jsonobj = null;
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
                binary_jsonobj = attrstatus_jsonobj.getString("binary");
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

                Object value;
                for(bin_params = params.keys(); bin_params.hasNext(); json_v4a2.put(param, value)) {
                    param = bin_params.next().toString();
                    value = params.get(param);
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
                }
            }

            if (extdata_jsonobj != null) {
                params = new JSONObject(extdata_jsonobj.toString());
                bin_params = extdata_jsonobj.keys();

                while(bin_params.hasNext()) {
                    param = bin_params.next().toString();
                    String value = params.getString(param);

                    try {
                        byte[] decode = GizWifiBinary.decode(value);
                        json_v4a2.put(param, decode);
                        json_v4a1.put(param, value.toString());
                    } catch (UnsupportedEncodingException var24) {
                        var24.printStackTrace();
                    }
                }
            }

            if (enumData_jsonobj != null) {
                params = new JSONObject(enumData_jsonobj.toString());
                bin_params = enumData_jsonobj.keys();

                while(bin_params.hasNext()) {
                    param = bin_params.next().toString();
                    JSONObject value = params.getJSONObject(param);
                    param = value.getString("displayName");
                    int b = value.getInt("value");
                    json_v4a1.put(param, b + "");
                    json_v4a2.put(param, b);
                }
            }

            entity0.put("entity0", json_v4a1);
            entity0.put("cmd", 4);
            entity0.put("version", 4);
            map_v4a1.put("data", entity0.toString());
            map_v4a2.put("data", json_v4a2);
            if (binary_jsonobj != null) {
                try {
                    map_v4a1.put("binary", GizWifiBinary.decode(binary_jsonobj.toString()));
                    map_v4a2.put("binary", GizWifiBinary.decode(binary_jsonobj.toString()));
                } catch (UnsupportedEncodingException var23) {
                    var23.printStackTrace();
                }
            }

            String name;
            boolean b;
            JSONObject v1json;
            ConcurrentHashMap v2json;
            JSONObject value;
            if (faults_jsonobj != null) {
                params = new JSONObject(faults_jsonobj.toString());
                bin_params = faults_jsonobj.keys();
                v1json = new JSONObject();

                for(v2json = new ConcurrentHashMap(); bin_params.hasNext(); v2json.put(param, b)) {
                    param = bin_params.next().toString();
                    value = params.getJSONObject(param);
                    name = value.getString("displayName");
                    b = value.getBoolean("value");
                    if (b) {
                        v1json.put(name, 1);
                    } else {
                        v1json.put(name, 0);
                    }
                }

                map_v4a1.put("faults", v1json.toString());
                map_v4a2.put("faults", v2json);
            }

            if (alerts_jsonobj != null) {
                params = new JSONObject(alerts_jsonobj.toString());
                bin_params = alerts_jsonobj.keys();
                v1json = new JSONObject();

                for(v2json = new ConcurrentHashMap(); bin_params.hasNext(); v2json.put(param, b)) {
                    param = bin_params.next().toString();
                    value = params.getJSONObject(param);
                    name = value.getString("displayName");
                    b = value.getBoolean("value");
                    if (b) {
                        v1json.put(name, 1);
                    } else {
                        v1json.put(name, 0);
                    }
                }

                map_v4a1.put("alters", v1json.toString());
                map_v4a2.put("alerts", v2json);
            }

            map_v4a1_v4a2.add(map_v4a1);
            map_v4a1_v4a2.add(map_v4a2);
            return map_v4a1_v4a2;
        }
    }

    private void createSubHandler() {
        HandlerThread connectDaemonThread = new HandlerThread("subhandler");
        if (this.handler == null) {
            connectDaemonThread.start();
            this.handler = new GizWifiSubDevice.MessageErrorHandler(connectDaemonThread.getLooper());
        }

    }

    public void write(ConcurrentHashMap<String, Object> data, int sn) {
        SDKLog.d("Start => , did: " + Utils.dataMasking(this.did) + ", sdid: " + this.subDid);
        Constant.sn = sn;
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

        int sn1 = Utils.getSn();
        JSONObject obj = new JSONObject();

        try {
            obj.put("cmd", 1035);
            obj.put("sn", sn1);
            obj.put("mac", this.getMacAddress());
            obj.put("did", this.getDid());
            obj.put("subdid", this.getSubDid());
            obj.put("data", mobj);
        } catch (JSONException var9) {
            SDKLog.e(var9.toString());
            var9.printStackTrace();
        }

        this.sendMes2Demo(obj);
        if (this.isLAN) {
            this.makeTimer(4000, 1036, sn1);
        } else {
            this.makeTimer(20000, 1036, sn1);
        }

        SDKLog.d("End <= ");
    }

    private void sendMes2Demo(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    /** @deprecated */
    public void write(String jsonData) {
        SDKLog.d("Start => , did: " + Utils.dataMasking(this.did) + ", sdid: " + this.subDid);
        int sn = Utils.getSn();
        JSONObject myobj = new JSONObject();

        try {
            myobj.put("cmd", 1035);
            myobj.put("sn", sn);
            myobj.put("mac", this.macAddress);
            myobj.put("subdid", this.getSubDid());
            myobj.put("did", this.did);
            JSONObject obj = new JSONObject(jsonData);
            if (jsonData.contains("cmd") && obj.getString("cmd").equalsIgnoreCase("2")) {
                this.getDeviceStatus();
            } else if (jsonData.contains("cmd") && obj.getString("cmd").equalsIgnoreCase("1")) {
                JSONObject jsonObject = obj.getJSONObject("entity0");
                myobj.put("data", jsonObject);
            } else {
                myobj.put("data", obj);
            }
        } catch (JSONException var6) {
            var6.printStackTrace();
        }

        this.sendMes2Demo(myobj);
        if (this.isLAN) {
            this.makeTimer(4000, 1036, sn);
        } else {
            this.makeTimer(20000, 1036, sn);
        }

        SDKLog.d("End <= ");
    }

    public String getSubDid() {
        return this.subDid;
    }

    protected void setSubDid(String subDid) {
        this.subDid = subDid;
    }

    public String getSubProductKey() {
        return this.subProductKey;
    }

    protected void setSubProductKey(String subProductKey) {
        this.subProductKey = subProductKey;
    }

    public String getSubProductName() {
        return this.subProductName;
    }

    protected void setSubProductName(String subProductName) {
        this.subProductName = subProductName;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getSubDid());
        dest.writeString(this.getSubProductKey());
        dest.writeString(this.getSubProductName());
        dest.writeString(this.getMacAddress());
        dest.writeString(this.getDid());
        dest.writeString(this.getProductUI());
    }

    public void getDeviceStatus() {
        SDKLog.d("Start => mac: " + this.macAddress + ", did: " + Utils.dataMasking(this.did) + ", isLan: " + this.isLAN());
        int sn = Utils.getSn();
        JSONObject obj = new JSONObject();

        try {
            obj.put("cmd", 1033);
            obj.put("sn", sn);
            obj.put("did", this.getDid());
            obj.put("subdid", this.getSubDid());
            obj.put("mac", this.macAddress);
            String devicedid = this.getDid();
            if (!TextUtils.isEmpty(devicedid)) {
                obj.put("did", devicedid);
            }
        } catch (JSONException var4) {
            SDKLog.e(var4.toString());
            var4.printStackTrace();
        }

        this.sendMes2Demo(obj);
        if (this.isLAN) {
            this.makeTimer(4000, 1034, sn);
        } else {
            this.makeTimer(20000, 1034, sn);
        }

        SDKLog.d("End <= ");
    }

    boolean isEqualToJsonObj(JSONObject jsonObj, String did) throws JSONException {
        return TextUtils.equals(this.did, did) && TextUtils.equals(this.subDid, jsonObj.getString("subdid"));
    }

    protected void syncDeviceInfoFromJson(JSONObject jsonObject) throws JSONException {
        String subDidJson = jsonObject.getString("subdid");
        String subProductKeyJson = jsonObject.getString("subProductKey");
        String subProductNameJson = jsonObject.getString("subProductName");
        this.setSubDid(subDidJson);
        this.setSubProductKey(subProductKeyJson);
        this.setSubProductName(subProductNameJson);
        String productUI = null;
        if (!TextUtils.isEmpty(subProductKeyJson) && SDKLog.isSDCard) {
            String api = (String)SDKEventManager.domainInfo.get("openapi");
            api = api.contains(":") ? api.split(":")[0] : api;
            File profile = new File(Constant.productFilePath, api);
            File file = new File(profile, subProductKeyJson + ".json");
            String productJson = Utils.readFileContentStr(file.toString());
            if (!TextUtils.isEmpty(productJson)) {
                JSONObject bj = new JSONObject(productJson);
                if (bj.has("ui")) {
                    productUI = bj.getString("ui");
                }
            }
        }

        this.setProductUI(productUI);
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

    class MessageErrorHandler extends Handler {
        public MessageErrorHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int cmd = (Integer)msg.obj;
            SDKLog.d("timeout cmd: " + cmd + ", sn: " + msg.what);
            switch(cmd) {
                case 1034:
                    GizWifiSubDevice.this.handler.removeMessages(msg.what);
                    GizWifiSubDevice.this.onDidReceiveData(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), (GizWifiDevice)null, (ConcurrentHashMap)null, (ConcurrentHashMap)null, (ConcurrentHashMap)null, 22);
                    break;
                case 1036:
                    GizWifiSubDevice.this.handler.removeMessages(msg.what);
                    ConcurrentHashMap<String, Object> maps = new ConcurrentHashMap();
                    GizWifiSubDevice.this.onDidReceiveData(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), (GizWifiDevice)null, maps, maps, maps, Constant.sn);
            }

        }
    }
}
