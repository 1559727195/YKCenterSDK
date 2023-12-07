//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.Iterator;
import java.util.List;

public class WifiAutoConnectManager {
    private static final String TAG = WifiAutoConnectManager.class.getSimpleName();
    WifiManager wifiManager;

    public WifiAutoConnectManager(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }

    public void connect(String ssid, String password, WifiAutoConnectManager.WifiCipherType type) {
        Thread thread = new Thread(new WifiAutoConnectManager.ConnectRunnable(ssid, password, type));
        thread.start();
    }

    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = this.wifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            Iterator var3 = existingConfigs.iterator();

            while(var3.hasNext()) {
                WifiConfiguration existingConfig = (WifiConfiguration)var3.next();
                if (existingConfig.SSID != null && existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }

        return null;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password, WifiAutoConnectManager.WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(0);
        }

        if (Type == WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }

            config.allowedAuthAlgorithms.set(0);
            config.allowedAuthAlgorithms.set(1);
            config.allowedKeyManagement.set(0);
            config.wepTxKeyIndex = 0;
        }

        if (Type == WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(0);
            config.allowedGroupCiphers.set(2);
            config.allowedKeyManagement.set(1);
            config.allowedPairwiseCiphers.set(1);
            config.allowedGroupCiphers.set(3);
            config.allowedPairwiseCiphers.set(2);
            config.status = 2;
        }

        return config;
    }

    private boolean openWifi() {
        boolean bRet = true;
        if (!this.wifiManager.isWifiEnabled()) {
            bRet = this.wifiManager.setWifiEnabled(true);
        }

        return bRet;
    }

    private static boolean isHexWepKey(String wepKey) {
        int len = wepKey.length();
        return len != 10 && len != 26 && len != 58 ? false : isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for(int i = key.length() - 1; i >= 0; --i) {
            char c = key.charAt(i);
            if ((c < '0' || c > '9') && (c < 'A' || c > 'F') && (c < 'a' || c > 'f')) {
                return false;
            }
        }

        return true;
    }

    public static WifiAutoConnectManager.WifiCipherType getCipherType(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager)context.getSystemService("wifi");
        List<ScanResult> list = wifiManager.getScanResults();
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            ScanResult scResult = (ScanResult)var4.next();
            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities;
                if (!TextUtils.isEmpty(capabilities)) {
                    if (!capabilities.contains("WPA") && !capabilities.contains("wpa")) {
                        if (!capabilities.contains("WEP") && !capabilities.contains("wep")) {
                            SDKLog.d("no");
                            return WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS;
                        }

                        SDKLog.d("wep");
                        return WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WEP;
                    }

                    SDKLog.d("wpa");
                    return WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA;
                }
            }
        }

        return WifiAutoConnectManager.WifiCipherType.WIFICIPHER_INVALID;
    }

    class ConnectRunnable implements Runnable {
        private String ssid;
        private String password;
        private WifiAutoConnectManager.WifiCipherType type;

        public ConnectRunnable(String ssid, String password, WifiAutoConnectManager.WifiCipherType type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
        }

        public void run() {
            WifiAutoConnectManager.this.openWifi();

            while(WifiAutoConnectManager.this.wifiManager.getWifiState() == 2) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException var6) {
                }
            }

            WifiConfiguration wifiConfig = WifiAutoConnectManager.this.createWifiInfo(this.ssid, this.password, this.type);
            if (wifiConfig == null) {
                SDKLog.d("wifiConfig is null!");
            } else {
                WifiConfiguration tempConfig = WifiAutoConnectManager.this.isExsits(this.ssid);
                if (tempConfig != null) {
                    WifiAutoConnectManager.this.wifiManager.removeNetwork(tempConfig.networkId);
                }

                int netID = WifiAutoConnectManager.this.wifiManager.addNetwork(wifiConfig);
                boolean enabled = WifiAutoConnectManager.this.wifiManager.enableNetwork(netID, true);
                SDKLog.d("enableNetwork status enable=" + enabled);
                boolean connected = WifiAutoConnectManager.this.wifiManager.reconnect();
                SDKLog.d("enableNetwork connected=" + connected);
            }
        }
    }

    public static enum WifiCipherType {
        WIFICIPHER_WEP,
        WIFICIPHER_WPA,
        WIFICIPHER_NOPASS,
        WIFICIPHER_INVALID;

        private WifiCipherType() {
        }
    }
}
