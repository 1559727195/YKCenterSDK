//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.GizWifiDaemon;
import com.gizwits.gizwifisdk.log.SDKLog;
import org.json.JSONException;
import org.json.JSONObject;

public class NetStatusReceiver extends BroadcastReceiver {
    private boolean internetReachable;
    private boolean netFree;
    private boolean netDisable;
    private boolean background;
    private Context mContext;
    static final String Net2G = "2G";
    static final String Net3G = "3G";
    static final String Net4G = "4G";
    static final String WIFI = "WIFI";
    static final String FreeWIFI = "LAN";
    static final String PublicWIFI = "WLAN";
    static final String CMCC = "CMCC";
    static final String ChinaNet = "ChinaNet";
    static final String ChinaUnicom = "ChinaUnicom";
    private NetStatusReceiver.BaiDuHandler pingHandler;

    public NetStatusReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        SDKLog.d("NetStatusReceiver============>  ");

        try {
            this.background = false;
            this.netDisable = false;
            this.netFree = true;
            this.internetReachable = true;
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();
            obj.put("cmd", 1005);
            obj.put("sn", sn);
            boolean isBackgroud = Utils.isApkBackground(context);
            Constant.isBackGround = isBackgroud;
            obj.put("background", isBackgroud);
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService("connectivity");
            SoftApConfig.getInstance().stopConfig();
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info == null || !manager.getBackgroundDataSetting()) {
                this.netDisable = true;
                obj.put("netDisable", this.netDisable);
                this.sendMessage2Deamon(obj);
                Constant.netdisable = true;
                return;
            }

            Constant.netdisable = false;
            int netType = info.getType();
            int netSubtype = info.getSubtype();
            if (netType == 1) {
                String wifiSSID = Utils.getWifiSSID(context);
                if (TextUtils.isEmpty(Constant.wifissid)) {
                    Constant.wifissid = wifiSSID;
                }

                if (!Constant.wifissid.equalsIgnoreCase(Utils.getWifiSSID(context))) {
                    Constant.wifissid = Utils.getWifiSSID(context);
                    this.netDisable = true;
                    obj.put("netDisable", this.netDisable);
                    this.sendMessage2Deamon(obj);
                }

                this.PingBaiDu();
            } else if (netType == 0) {
                this.netDisable = true;
                obj.put("netDisable", this.netDisable);
                this.sendMessage2Deamon(obj);
                this.netDisable = false;
                Constant.wifissid = "unknow";
                obj.put("netDisable", this.netDisable);
                obj.put("netFree", false);
                obj.put("internetReachable", true);
                this.sendMessage2Deamon(obj);
            }
        } catch (JSONException var11) {
            var11.printStackTrace();
        }

    }

    private void sendMessage2Deamon(JSONObject obj) {
        boolean isBackgroud = false;
        isBackgroud = obj.optBoolean("background");
        GizWifiDaemon.updateBackgroundToDaemon(isBackgroud);
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    private void PingBaiDu() {
        HandlerThread connectDaemonThread = new HandlerThread("ping144thread");
        if (this.pingHandler == null) {
            connectDaemonThread.start();
            this.pingHandler = new NetStatusReceiver.BaiDuHandler(connectDaemonThread.getLooper());
        }

        this.pingHandler.sendEmptyMessage(11111);
    }

    class BaiDuHandler extends Handler {
        public BaiDuHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            try {
                int sn = Utils.getSn();
                JSONObject obj = new JSONObject();
                boolean isBackgroud = Utils.isApkBackground(NetStatusReceiver.this.mContext);
                Constant.isBackGround = isBackgroud;
                boolean netFree = Utils.isNetFree(NetStatusReceiver.this.mContext);
                obj.put("cmd", 1005);
                obj.put("sn", sn);
                obj.put("netDisable", false);
                obj.put("background", isBackgroud);
                obj.put("netFree", netFree);
                obj.put("internetReachable", true);
                GizWifiDaemon.updateBackgroundToDaemon(isBackgroud);
                MessageHandler.getSingleInstance().send(obj.toString());
            } catch (JSONException var6) {
                var6.printStackTrace();
            }

        }
    }
}
