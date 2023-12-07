//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.content.Context;
import com.gizwits.gizwifisdk.log.SDKLog;
import com.mxchip.api.EasyLink;
import com.mxchip.helper.EasyLinkCallBack;
import com.mxchip.helper.EasyLinkParams;

public class MxChipMicoManager {
    private String TAG = "MxChipMicoManager";
    private static MxChipMicoManager mMxChipMicoManager = new MxChipMicoManager();
    private EasyLink elink;
    boolean isRunning = false;

    protected static MxChipMicoManager sharedInstance(Context context) {
        return mMxChipMicoManager;
    }

    private MxChipMicoManager() {
    }

    protected void start(String ssid, String key, int timeout, Context context) {
        if (!this.isRunning()) {
            this.elink = new EasyLink(context);
            SDKLog.d("=====> Start MXCHIP3 config: ssid = " + ssid + ", key = " + Utils.dataMasking(key));
            this.isRunning = true;
            EasyLinkParams easylinkPara = new EasyLinkParams();
            easylinkPara.ssid = ssid;
            easylinkPara.password = key;
            easylinkPara.runSecond = timeout * 1000;
            easylinkPara.sleeptime = 20;
            easylinkPara.extraData = "xu sin";
            SDKLog.d("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm  ::::" + this.elink.getSSID());
            this.elink.startEasyLink(easylinkPara, new EasyLinkCallBack() {
                public void onSuccess(int code, String message) {
                    SDKLog.d("MXCHIP3 is config success    code :" + code + "     message :" + message);
                }

                public void onFailure(int code, String message) {
                    SDKLog.d("MXCHIP3 is config failed    code :" + code + "     message :" + message);
                }
            });
        }
    }

    protected void stop() {
        if (this.isRunning()) {
            SDKLog.d("=====> Stop MXCHIP3 config");
            if (this.elink != null) {
                this.elink.stopEasyLink(new EasyLinkCallBack() {
                    public void onSuccess(int code, String message) {
                        SDKLog.d("=====> Stop MXCHIP3 config   success    code :" + code + "   message :" + message);
                    }

                    public void onFailure(int code, String message) {
                        SDKLog.d("=====> Stop MXCHIP3 config   failed    code :" + code + "   message :" + message);
                    }
                });
            }

            this.isRunning = false;
        }
    }

    protected boolean isRunning() {
        return this.isRunning;
    }
}
