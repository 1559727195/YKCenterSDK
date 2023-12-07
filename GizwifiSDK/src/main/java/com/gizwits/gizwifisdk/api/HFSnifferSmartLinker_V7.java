//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.content.Context;
import com.gizwits.gizwifisdk.log.SDKLog;
import com.hiflying.smartlink.OnSmartLinkListener;
import com.hiflying.smartlink.v7.MulticastSmartLinker;

public class HFSnifferSmartLinker_V7 {
    private static final String TAG = "HFSnifferSmartLinkerManager";
    private static HFSnifferSmartLinker_V7 mHFSnifferSmartLinker = new HFSnifferSmartLinker_V7() {
    };
    private boolean mIsRunning = false;

    public HFSnifferSmartLinker_V7() {
    }

    public static HFSnifferSmartLinker_V7 sharedInstance() {
        return mHFSnifferSmartLinker;
    }

    public boolean isRunning() {
        return this.mIsRunning;
    }

    public void start(String ssid, String password, byte[] dataAppend, int timeout, Context context, boolean isV8) {
        if (!this.mIsRunning) {
            SDKLog.d("=====> Start HF config: ssid = " + ssid + ", password = " + Utils.dataMasking(password));
            if (dataAppend == null) {
                dataAppend = new byte[0];
            }

            String dataString = "";

            for(int i = 0; i < dataAppend.length; ++i) {
                dataString = dataString + dataAppend[i] + " ";
            }

            SDKLog.d("=====> Start HF config: dataAppend = " + dataString);

            try {
                MulticastSmartLinker.getInstance().setmMixSmartLink8(isV8);
                MulticastSmartLinker.getInstance().start(context, password, dataAppend, new String[]{ssid});
            } catch (Exception var9) {
                var9.printStackTrace();
            }

            this.mIsRunning = true;
        }
    }

    public void stop() {
        if (this.mIsRunning) {
            SDKLog.d("=====> Stop HF config");
            MulticastSmartLinker.getInstance().setOnSmartLinkListener((OnSmartLinkListener)null);
            MulticastSmartLinker.getInstance().stop();
            this.mIsRunning = false;
        }
    }
}
