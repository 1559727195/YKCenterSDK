//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.espressif.iot.esptouch.gizwits;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Esptouch1 {
    private static final String TAG = "Esptouch1, ";
    private Context mContext;
    private EsptouchTask mTask;
    private EsptouchTask mTaskNoBroadcast;

    public Esptouch1() {
    }

    private List<ScanResult> scan() {
        WifiManager mWifiManager = (WifiManager)this.mContext.getApplicationContext().getSystemService("wifi");
        mWifiManager.startScan();
        List wifiList = null;
        wifiList = mWifiManager.getScanResults();
        return wifiList != null ? wifiList : Collections.emptyList();
    }

    private WifiInfo getConnectionInfo() {
        WifiManager mWifiManager = (WifiManager)this.mContext.getApplicationContext().getSystemService("wifi");
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return wifiInfo;
    }

    private String getSsid(WifiInfo wifiInfo) {
        String ssid = null;
        if (wifiInfo != null) {
            int len = wifiInfo.getSSID().length();
            if (wifiInfo.getSSID().startsWith("\"") && wifiInfo.getSSID().endsWith("\"")) {
                ssid = wifiInfo.getSSID().substring(1, len - 1);
            } else {
                ssid = wifiInfo.getSSID();
            }
        }

        return ssid;
    }

    private String getBssid(String ssid) {
        WifiInfo apInfo = this.getConnectionInfo();
        String scanResultList;
        if (apInfo != null) {
            scanResultList = this.getSsid(apInfo);
            if (ssid.equals(scanResultList)) {
                return apInfo.getBSSID();
            }
        }

        scanResultList = null;

        for(int retry = 0; retry < 10; ++retry) {
            if (retry > 0) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException var9) {
                    var9.printStackTrace();
                }
            }

            List var9 = this.scan();
            Iterator var6 = var9.iterator();

            while(var6.hasNext()) {
                ScanResult scanResult = (ScanResult)var6.next();
                if (ssid.equals(scanResult.SSID)) {
                    String bssid = scanResult.BSSID;
                    return bssid;
                }
            }
        }

        return null;
    }

    public String getWifiConnectedSsidAscii(String ssid) {
        long timeout = 100L;
        long interval = 20L;
        String ssidAscii = ssid;
        WifiManager wifiManager = (WifiManager)this.mContext.getSystemService("wifi");
        wifiManager.startScan();
        boolean isBreak = false;
        long start = System.currentTimeMillis();

        do {
            try {
                Thread.sleep(20L);
            } catch (InterruptedException var20) {
                isBreak = true;
                break;
            }

            List<ScanResult> scanResults = wifiManager.getScanResults();
            Iterator var12 = scanResults.iterator();

            while(var12.hasNext()) {
                ScanResult scanResult = (ScanResult)var12.next();
                if (scanResult.SSID != null && scanResult.SSID.equals(ssid)) {
                    isBreak = true;

                    try {
                        Field wifiSsidfield = ScanResult.class.getDeclaredField("wifiSsid");
                        wifiSsidfield.setAccessible(true);
                        Class<?> wifiSsidClass = wifiSsidfield.getType();
                        Object wifiSsid = wifiSsidfield.get(scanResult);
                        Method method = wifiSsidClass.getDeclaredMethod("getOctets");
                        byte[] bytes = (byte[])((byte[])method.invoke(wifiSsid));
                        ssidAscii = new String(bytes, "ISO-8859-1");
                    } catch (Exception var19) {
                        var19.printStackTrace();
                    }
                    break;
                }
            }
        } while(System.currentTimeMillis() - start < 100L && !isBreak);

        return ssidAscii;
    }

    public synchronized boolean start(String ssid, String psw, Context context, int timeout, boolean broadcast) {
        if (this.mTask == null && this.mTaskNoBroadcast == null) {
            this.mContext = context;
            String bssid = this.getBssid(ssid);
            if (bssid == null) {
                SDKLog.e("Esptouch1Esptouch1 WARN: can't get bssid by ssid");
                return false;
            } else {
                byte[] ssid_original_byte = ssid.getBytes();
                SDKLog.d("Esptouch1, ssid_original_byte length: " + ssid_original_byte.length);

                for(int i = 0; i < ssid_original_byte.length; ++i) {
                    SDKLog.d("Esptouch1, ssid_original_byte[" + i + "] = " + ssid_original_byte[i]);
                }

                byte[] ssid_ISO88591_byte = ssid.getBytes();
                SDKLog.d("Esptouch1, ssid_ISO88591_byte length: " + ssid_ISO88591_byte.length);

                for(int i = 0; i < ssid_ISO88591_byte.length; ++i) {
                    SDKLog.d("Esptouch1, ssid_ISO88591_byte[" + i + "] = " + ssid_ISO88591_byte[i]);
                }

                this.mTask = new EsptouchTask(ssid, bssid, psw, context);
                if (broadcast) {
                    this.mTask.setPackageBroadcast(true);
                } else {
                    this.mTask.setPackageBroadcast(false);
                }

                (new Thread() {
                    public void run() {
                        Esptouch1.this.mTask.executeForResults(0);
                    }
                }).start();
                return true;
            }
        } else {
            SDKLog.e("Esptouch1Esptouch1 ERROR: task is executing, do nothing else");
            return false;
        }
    }

    public synchronized void stop() {
        if (this.mTask != null) {
            this.mTask.interrupt();
        }

        if (this.mTaskNoBroadcast != null) {
            this.mTaskNoBroadcast.interrupt();
        }

    }
}
