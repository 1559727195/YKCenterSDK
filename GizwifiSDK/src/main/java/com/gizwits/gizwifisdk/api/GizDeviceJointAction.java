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
import com.gizwits.gizwifisdk.enumration.GizLogicalOperator;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceJointActionListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceJointAction implements Parcelable {
    private String jointActionID;
    private GizWifiDevice jointActionOwner;
    private String jointActionName;
    private boolean enabled;
    private GizDeviceJointActionRule jointActionRule;
    private boolean isValid;
    private GizDeviceJointActionListener mListener;
    private static final int MSG_RECV = 5;
    public static final Creator<GizDeviceJointAction> CREATOR = new Creator<GizDeviceJointAction>() {
        public GizDeviceJointAction createFromParcel(Parcel source) {
            GizDeviceJointAction jointAction = new GizDeviceJointAction(source);
            List<GizDeviceJointAction> list = GizDeviceJointActionCenter.getValidJointActionListByOwner(jointAction.jointActionOwner);
            GizDeviceJointAction gp = null;
            Iterator var5 = list.iterator();

            while(var5.hasNext()) {
                GizDeviceJointAction gizJointAction = (GizDeviceJointAction)var5.next();
                if (gizJointAction != null && gizJointAction.getJointActionID().equals(jointAction.jointActionID)) {
                    gp = gizJointAction;
                    break;
                }
            }

            return gp;
        }

        public GizDeviceJointAction[] newArray(int size) {
            return null;
        }
    };
    protected Handler messageHandler;
    Handler timeHan;

    protected GizDeviceJointAction(GizWifiDevice jointActionOwner, String jointActionID, String jointActionName, boolean enabled) {
        this.messageHandler = new NamelessClass_2(Looper.getMainLooper());
        this.timeHan = new NamelessClass_1();
        this.jointActionOwner = jointActionOwner;
        this.jointActionID = jointActionID;
        this.setJointActionName(jointActionName);
        this.enabled = enabled;
        this.isValid = true;
    }

    protected String simpleInfoMasking() {
        return "GizDeviceJointAction [jointActionID=" + this.jointActionID + ", jointActionName=" + this.jointActionName + ", isValid=" + this.isValid + ", jointActionOwner=" + (this.jointActionOwner == null ? "null" : this.jointActionOwner.moreSimpleInfoMasking()) + "]";
    }

    protected String infoMasking() {
        String info = this.simpleInfoMasking();
        return info + "->[jointActionRule=" + (this.jointActionRule == null ? "null" : this.jointActionRule.infoMasking()) + "]";
    }

    protected boolean getIsValid() {
        return this.isValid;
    }

    protected void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getJointActionID() {
        return this.jointActionID;
    }

    protected void setJointActionID(String jointActionID) {
        this.jointActionID = jointActionID;
    }

    public GizWifiDevice getJointActionOwner() {
        return this.jointActionOwner;
    }

    protected void setJointActionOwner(GizWifiDevice jointActionOwner) {
        this.jointActionOwner = jointActionOwner;
    }

    public String getJointActionName() {
        return this.jointActionName;
    }

    protected void setJointActionName(String jointActionName) {
        if (jointActionName != null) {
            this.jointActionName = jointActionName;
        }

    }

    public boolean getEnabled() {
        return this.enabled;
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public GizDeviceJointActionRule getJointActionRule() {
        return this.jointActionRule;
    }

    protected void setJointActionRule(GizDeviceJointActionRule jointActionRule) {
        this.jointActionRule = jointActionRule;
    }

    protected boolean updateJointActionRule(JSONObject jsonObj) {
        if (jsonObj == null) {
            return false;
        } else {
            if (this.jointActionRule == null) {
                this.jointActionRule = new GizDeviceJointActionRule();
            }

            return this.jointActionRule.updateRuleSettings(this.jointActionOwner, jsonObj);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.jointActionID);
        dest.writeParcelable(this.jointActionOwner, flags);
        dest.writeString(this.jointActionName);
        dest.writeByte((byte)(this.enabled ? 1 : 0));
    }
    class NamelessClass_2 extends Handler {
        private int errorCode;
        private String productKey;
        private String did;
        private String mac;

        NamelessClass_2(Looper x0) {
            super(x0);
        }

        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 5:
                    String jsonStr = (String)msg.obj;

                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        int cmd = Integer.parseInt(obj.getString("cmd"));
                        int sn = obj.has("sn") ? Integer.parseInt(obj.getString("sn")) : 0;
                        this.didSetListener(cmd, obj, GizDeviceJointAction.this.mListener, sn);
                    } catch (NumberFormatException var6) {
                        var6.printStackTrace();
                    } catch (JSONException var7) {
                        var7.printStackTrace();
                    }
                default:
            }
        }

        private void didSetListener(int cmd, JSONObject obj, GizDeviceJointActionListener mListener, int sn) throws JSONException {
            switch(cmd) {
                case 1370:
                    if (GizDeviceJointAction.this.timeHan.hasMessages(sn)) {
                        GizDeviceJointAction.this.timeHan.removeMessages(sn);
                        this.errorCode = obj.getInt("errorCode");
                        if (obj.has("mac")) {
                            this.mac = obj.getString("mac");
                        }

                        if (obj.has("did")) {
                            this.did = obj.getString("did");
                        }

                        if (obj.has("productKey")) {
                            this.productKey = obj.getString("productKey");
                        }

                        if (obj.has("jointActionID")) {
                            GizDeviceJointAction.this.jointActionID = obj.getString("jointActionID");
                        }

                        GizDeviceJointAction.this.jointActionOwner = GizDeviceJointAction.this.findOwnerDevice(this.mac, this.did, this.productKey);
                        if (this.errorCode != 0) {
                            GizDeviceJointAction.this.didupdatejointactioninfo(this.errorCode);
                        }
                    }
                    break;
                case 1372:
                    if (GizDeviceJointAction.this.timeHan.hasMessages(sn)) {
                        GizDeviceJointAction.this.timeHan.removeMessages(sn);
                        this.errorCode = obj.getInt("errorCode");
                        if (obj.has("mac")) {
                            this.mac = obj.getString("mac");
                        }

                        if (obj.has("did")) {
                            this.did = obj.getString("did");
                        }

                        if (obj.has("productKey")) {
                            this.productKey = obj.getString("productKey");
                        }

                        if (obj.has("jointActionID")) {
                            GizDeviceJointAction.this.jointActionID = obj.getString("jointActionID");
                        }

                        GizDeviceJointAction.this.jointActionOwner = GizDeviceJointAction.this.findOwnerDevice(this.mac, this.did, this.productKey);
                        if (this.errorCode != 0) {
                            GizDeviceJointAction.this.didupdatejointactionstatus(this.errorCode);
                        }
                    }
                    break;
                case 1374:
                    if (GizDeviceJointAction.this.timeHan.hasMessages(sn)) {
                        GizDeviceJointAction.this.timeHan.removeMessages(sn);
                        this.errorCode = obj.getInt("errorCode");
                        if (obj.has("mac")) {
                            this.mac = obj.getString("mac");
                        }

                        if (obj.has("did")) {
                            this.did = obj.getString("did");
                        }

                        if (obj.has("productKey")) {
                            this.productKey = obj.getString("productKey");
                        }

                        if (obj.has("jointActionID")) {
                            GizDeviceJointAction.this.jointActionID = obj.getString("jointActionID");
                        }

                        GizDeviceJointAction.this.jointActionOwner = GizDeviceJointAction.this.findOwnerDevice(this.mac, this.did, this.productKey);
                        if (this.errorCode != 0) {
                            GizDeviceJointAction.this.didupdatejointactionrule(this.errorCode);
                        }
                    }
                    break;
                case 1376:
                    if (GizDeviceJointAction.this.timeHan.hasMessages(sn)) {
                        GizDeviceJointAction.this.timeHan.removeMessages(sn);
                        this.errorCode = obj.getInt("errorCode");
                        if (obj.has("mac")) {
                            this.mac = obj.getString("mac");
                        }

                        if (obj.has("did")) {
                            this.did = obj.getString("did");
                        }

                        if (obj.has("productKey")) {
                            this.productKey = obj.getString("productKey");
                        }

                        if (obj.has("jointActionID")) {
                            GizDeviceJointAction.this.jointActionID = obj.getString("jointActionID");
                        }

                        GizDeviceJointAction.this.jointActionOwner = GizDeviceJointAction.this.findOwnerDevice(this.mac, this.did, this.productKey);
                        GizDeviceJointAction.this.updateJointActionRule(obj);
                        GizDeviceJointAction.this.didupdatejointactionrule(this.errorCode);
                    }
                    break;
                case 2035:
                    if (obj.has("mac")) {
                        this.mac = obj.getString("mac");
                    }

                    if (obj.has("did")) {
                        this.did = obj.getString("did");
                    }

                    if (obj.has("productKey")) {
                        this.productKey = obj.getString("productKey");
                    }

                    if (obj.has("jointActionID")) {
                        GizDeviceJointAction.this.jointActionID = obj.getString("jointActionID");
                    }

                    if (obj.has("jointActionName")) {
                        GizDeviceJointAction.this.jointActionName = obj.getString("jointActionName");
                    }

                    GizDeviceJointAction.this.jointActionOwner = GizDeviceJointAction.this.findOwnerDevice(this.mac, this.did, this.productKey);
                    GizDeviceJointAction.this.didupdatejointactioninfo(this.errorCode);
                    break;
                case 2036:
                    if (obj.has("mac")) {
                        this.mac = obj.getString("mac");
                    }

                    if (obj.has("did")) {
                        this.did = obj.getString("did");
                    }

                    if (obj.has("productKey")) {
                        this.productKey = obj.getString("productKey");
                    }

                    if (obj.has("jointActionID")) {
                        GizDeviceJointAction.this.jointActionID = obj.getString("jointActionID");
                    }

                    if (obj.has("enabled")) {
                        GizDeviceJointAction.this.enabled = obj.getBoolean("enabled");
                    }

                    GizDeviceJointAction.this.jointActionOwner = GizDeviceJointAction.this.findOwnerDevice(this.mac, this.did, this.productKey);
                    GizDeviceJointAction.this.didupdatejointactionstatus(this.errorCode);
                    break;
                case 2037:
                    if (obj.has("mac")) {
                        this.mac = obj.getString("mac");
                    }

                    if (obj.has("did")) {
                        this.did = obj.getString("did");
                    }

                    if (obj.has("productKey")) {
                        this.productKey = obj.getString("productKey");
                    }

                    if (obj.has("jointActionID")) {
                        GizDeviceJointAction.this.jointActionID = obj.getString("jointActionID");
                    }

                    GizDeviceJointAction.this.jointActionOwner = GizDeviceJointAction.this.findOwnerDevice(this.mac, this.did, this.productKey);
                    GizDeviceJointAction.this.updateJointActionRule(obj);
                    GizDeviceJointAction.this.didupdatejointactionrule(this.errorCode);
            }

        }
    }
    class NamelessClass_1 extends Handler {
        NamelessClass_1() {
        }

        public void handleMessage(Message msg) {
            int cmd = (Integer)msg.obj;
            switch(cmd) {
                case 1042:
                    GizDeviceJointAction.this.timeHan.removeMessages(msg.what);
                    GizDeviceJointAction.this.didupdatejointactioninfo(8308);
                    break;
                case 1372:
                    GizDeviceJointAction.this.timeHan.removeMessages(msg.what);
                    GizDeviceJointAction.this.didupdatejointactionstatus(8308);
                    break;
                case 1374:
                    GizDeviceJointAction.this.timeHan.removeMessages(msg.what);
                    GizDeviceJointAction.this.didupdatejointactionrule(8308);
                    break;
                case 1376:
                    GizDeviceJointAction.this.timeHan.removeMessages(msg.what);
                    GizDeviceJointAction.this.didupdatejointactionrule(8308);
            }

        }
    }
    protected GizDeviceJointAction(Parcel in) {
        this.messageHandler = new NamelessClass_2(Looper.getMainLooper());
        this.timeHan = new NamelessClass_1();
        this.jointActionID = in.readString();
        this.jointActionOwner = (GizWifiDevice)in.readParcelable(GizWifiDevice.class.getClassLoader());
        this.jointActionName = in.readString();
        this.enabled = in.readByte() != 0;
    }

    protected void onDidUpdateJointActionInfo(GizDeviceJointAction jointAction, GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", jointaction: " + (jointAction == null ? "null" : jointAction.infoMasking()));
        if (this.mListener != null) {
            this.mListener.didUpdateJointActionInfo(jointAction, result);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidUpdateJointActionStatus(GizDeviceJointAction jointAction, GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", jointaction: " + (jointAction == null ? "null" : jointAction.infoMasking()));
        if (this.mListener != null) {
            this.mListener.didUpdateJointActionStatus(jointAction, result);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidUpdateJointActionRule(GizDeviceJointAction jointAction, GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", jointaction: " + (jointAction == null ? "null" : jointAction.infoMasking()));
        if (this.mListener != null) {
            this.mListener.didUpdateJointActionRule(jointAction, result);
            SDKLog.d("Callback end");
        }

    }

    public void setListener(GizDeviceJointActionListener Listener) {
        SDKLog.a("Start => listener: " + Listener);
        this.mListener = Listener;
    }

    public GizDeviceJointActionListener getListener() {
        return this.mListener;
    }

    private void sendMes2Demo(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    private void makeTimer(int timeout, int cmd, int sn) {
        Message mes = Message.obtain();
        mes.what = sn;
        mes.obj = cmd;
        List<GizDeviceJointAction> list = GizDeviceJointActionCenter.getValidJointActionListByOwner(this.jointActionOwner);
        Iterator var6 = list.iterator();

        while(var6.hasNext()) {
            GizDeviceJointAction jointAction = (GizDeviceJointAction)var6.next();
            if (jointAction.getJointActionID().equals(this.jointActionID)) {
                jointAction.timeHan.sendMessageDelayed(mes, (long)timeout);
            }
        }

    }

    public void editJointActionInfo(String jointActionName) {
        SDKLog.a("Start => this: , jointactionname: " + jointActionName);
        if (!Constant.ishandshake) {
            this.onDidUpdateJointActionInfo(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
            SDKLog.d("End <= ");
        } else if (jointActionName == null) {
            this.onDidUpdateJointActionInfo(this, GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.getResult()));
            SDKLog.a("End <= ");
        } else if (this.jointActionOwner == null) {
            this.onDidUpdateJointActionInfo(this, GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.getResult()));
            SDKLog.a("End <= ");
        } else {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1369);
                obj.put("sn", sn);
                obj.put("mac", this.jointActionOwner.getMacAddress());
                obj.put("did", this.jointActionOwner.getDid());
                obj.put("productKey", this.jointActionOwner.getProductKey());
                obj.put("jointActionID", this.jointActionID);
                obj.put("jointActionName", jointActionName);
                obj.put("enabled", this.enabled);
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            this.sendMes2Demo(obj);
            if (this.jointActionOwner.isLAN) {
                this.makeTimer(9000, 1370, sn);
            } else {
                this.makeTimer(20000, 1370, sn);
            }

            SDKLog.a("End <= ");
        }
    }

    public void enableJointAction(boolean enabled) {
        SDKLog.a("Start => this: " + this.infoMasking() + ", enabled: " + enabled);
        if (!Constant.ishandshake) {
            this.onDidUpdateJointActionStatus(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
            SDKLog.d("End <= ");
        } else if (this.jointActionOwner == null) {
            this.onDidUpdateJointActionStatus(this, GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.getResult()));
            SDKLog.a("End <= ");
        } else {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1371);
                obj.put("sn", sn);
                obj.put("mac", this.jointActionOwner.getMacAddress());
                obj.put("did", this.jointActionOwner.getDid());
                obj.put("productKey", this.jointActionOwner.getProductKey());
                obj.put("jointActionID", this.jointActionID);
                obj.put("enabled", enabled);
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            this.sendMes2Demo(obj);
            if (this.jointActionOwner.isLAN) {
                this.makeTimer(9000, 1372, sn);
            } else {
                this.makeTimer(20000, 1372, sn);
            }

            SDKLog.a("End <= ");
        }
    }

    public void editJointActionRule(List<GizDeviceJointActionRuleCondition> conditions, GizLogicalOperator conditionCombType, List<GizDeviceJointActionRuleResultEvent> resultEvents) {
        SDKLog.a("Start => this: " + this.infoMasking() + ", conditions: " + conditions);
        if (!Constant.ishandshake) {
            this.onDidUpdateJointActionRule(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
            SDKLog.d("End <= ");
        } else if (conditions != null && conditions.size() != 0) {
            if (resultEvents != null && resultEvents.size() != 0) {
                if (this.jointActionOwner == null) {
                    this.onDidUpdateJointActionRule(this, GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.getResult()));
                    SDKLog.a("End <= ");
                } else {
                    int sn = Utils.getSn();
                    JSONObject obj = new JSONObject();

                    try {
                        obj.put("cmd", 1373);
                        obj.put("sn", sn);
                        obj.put("mac", this.jointActionOwner.getMacAddress());
                        obj.put("did", this.jointActionOwner.getDid());
                        obj.put("productKey", this.jointActionOwner.getProductKey());
                        obj.put("jointActionID", this.jointActionID);
                        GizDeviceJointActionRule rule = new GizDeviceJointActionRule(conditions, conditionCombType, resultEvents);
                        JSONObject object = GizDeviceJointActionCenter.getJointActionRuleJsonObject(rule);
                        Iterator it = object.keys();

                        while(it.hasNext()) {
                            String key = (String)it.next();
                            obj.put(key, object.get(key));
                        }
                    } catch (JSONException var10) {
                        SDKLog.e(var10.toString());
                        var10.printStackTrace();
                    }

                    this.sendMes2Demo(obj);
                    if (this.jointActionOwner.isLAN) {
                        this.makeTimer(9000, 1374, sn);
                    } else {
                        this.makeTimer(20000, 1374, sn);
                    }

                    SDKLog.a("End <= ");
                }
            } else {
                this.onDidUpdateJointActionRule(this, GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.getResult()));
                SDKLog.a("End <= ");
            }
        } else {
            this.onDidUpdateJointActionRule(this, GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.getResult()));
            SDKLog.a("End <= ");
        }
    }

    public void updateJointActionRule() {
        SDKLog.a("Start => this: " + this.infoMasking());
        if (!Constant.ishandshake) {
            this.onDidUpdateJointActionRule(this, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
            SDKLog.d("End <= ");
        } else if (this.jointActionOwner == null) {
            this.onDidUpdateJointActionRule(this, GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.getResult()));
            SDKLog.a("End <= ");
        } else {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1375);
                obj.put("sn", sn);
                obj.put("mac", this.jointActionOwner.getMacAddress());
                obj.put("did", this.jointActionOwner.getDid());
                obj.put("productKey", this.jointActionOwner.getProductKey());
                obj.put("jointActionID", this.jointActionID);
            } catch (JSONException var4) {
                SDKLog.e(var4.toString());
                var4.printStackTrace();
            }

            this.sendMes2Demo(obj);
            if (this.jointActionOwner.isLAN) {
                this.makeTimer(9000, 1376, sn);
            } else {
                this.makeTimer(20000, 1376, sn);
            }

            SDKLog.a("End <= ");
        }
    }

    private GizWifiDevice findOwnerDevice(String mac2, String did2, String productKey2) {
        if (!TextUtils.isEmpty(mac2) && !TextUtils.isEmpty(productKey2)) {
            GizWifiDevice device = null;
            List<GizWifiDevice> list = SDKEventManager.getInstance().getTotalDeviceList();
            synchronized(list) {
                Iterator var7 = list.iterator();

                while(var7.hasNext()) {
                    GizWifiDevice gizWifiDevice = (GizWifiDevice)var7.next();
                    if (TextUtils.isEmpty(did2)) {
                        if (mac2.equals(gizWifiDevice.getMacAddress()) && productKey2.equals(gizWifiDevice.getProductKey())) {
                            device = gizWifiDevice;
                            break;
                        }
                    } else if (mac2.equals(gizWifiDevice.getMacAddress()) && productKey2.equals(gizWifiDevice.getProductKey()) && did2.equals(gizWifiDevice.getDid())) {
                        device = gizWifiDevice;
                        break;
                    }
                }

                return device;
            }
        } else {
            return null;
        }
    }

    protected void didupdatejointactioninfo(int errorCode) {
        this.onDidUpdateJointActionInfo(this, GizWifiErrorCode.valueOf(errorCode));
    }

    protected void didupdatejointactionstatus(int errorCode) {
        this.onDidUpdateJointActionStatus(this, GizWifiErrorCode.valueOf(errorCode));
    }

    protected void didupdatejointactionrule(int errorCode) {
        this.onDidUpdateJointActionRule(this, GizWifiErrorCode.valueOf(errorCode));
    }
}
