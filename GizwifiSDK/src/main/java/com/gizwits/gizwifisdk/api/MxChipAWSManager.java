//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.content.Context;
import com.gizwits.gizwifisdk.log.SDKLog;
import io.fogcloud.sdk.easylink.api.EasyLink;
import io.fogcloud.sdk.easylink.api.EasylinkP2P;
import io.fogcloud.sdk.easylink.helper.EasyLinkCallBack;
import io.fogcloud.sdk.easylink.helper.EasyLinkParams;

public class MxChipAWSManager {
    private static MxChipAWSManager mMxChipAWSManager = new MxChipAWSManager();
    private EasyLink elink;
    boolean isRunning = false;
    private EasylinkP2P elp2p;

    protected static MxChipAWSManager sharedInstance() {
        return mMxChipAWSManager;
    }

    private MxChipAWSManager() {
    }

    protected void start(String ssid, String key, int timeout, Context context) {
        if (!this.isRunning()) {
            this.elink = new EasyLink(context);
            this.elp2p = new EasylinkP2P(context);
            SDKLog.d("=====> Start MxCHIPAWS config: ssid = " + ssid + ", key = " + Utils.dataMasking(key));
            this.isRunning = true;
            EasyLinkParams easylinkPara = new EasyLinkParams();
            easylinkPara.ssid = ssid;
            easylinkPara.password = key;
            easylinkPara.runSecond = timeout * 1000;
            easylinkPara.sleeptime = 20;
            easylinkPara.extraData = "xu sin";
            SDKLog.d("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm  ::::" + this.elink.getSSID());
            this.elp2p.startEasyLink(easylinkPara, new EasyLinkCallBack() {
                public void onSuccess(int code, String message) {
                    SDKLog.d("MXCHIPAWS is config success    code :" + code + "     message :" + message);
                }

                public void onFailure(int code, String message) {
                    SDKLog.d("MXCHIPAWS is config failed    code :" + code + "     message :" + message);
                }
            });
        }
    }

    protected void stop() {
        if (this.isRunning()) {
            SDKLog.d("=====> Stop MXCHIPAWS config");
            if (this.elp2p != null) {
                this.elp2p.stopEasyLink(new EasyLinkCallBack() {
                    public void onSuccess(int code, String message) {
                        SDKLog.d("=====> Stop MXCHIPAWS config   success    code :" + code + "   message :" + message);
                    }

                    public void onFailure(int code, String message) {
                        SDKLog.d("=====> Stop MXCHIPAWS config   failed    code :" + code + "   message :" + message);
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
