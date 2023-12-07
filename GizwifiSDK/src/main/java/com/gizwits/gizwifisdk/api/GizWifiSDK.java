//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.content.Context;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.enumration.GizLogPrintLevel;
import com.gizwits.gizwifisdk.enumration.GizMeshVendor;
import com.gizwits.gizwifisdk.enumration.GizPushType;
import com.gizwits.gizwifisdk.enumration.GizThirdAccountType;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;
import com.gizwits.gizwifisdk.enumration.GizWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.GizWifiGAgentType;
import com.gizwits.gizwifisdk.enumration.XPGUserAccountType;
import com.gizwits.gizwifisdk.enumration.XPGWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.XPGWifiGAgentType;
import com.gizwits.gizwifisdk.enumration.XPGWifiThirdAccountType;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GizWifiSDK {
    private static final GizWifiSDK mInstance = new GizWifiSDK();
    private static SDKEventManager mEventHandler;

    private GizWifiSDK() {
        mEventHandler = SDKEventManager.getInstance();
    }

    public static synchronized GizWifiSDK sharedInstance() {
        return mInstance;
    }

    public void setListener(GizWifiSDKListener listener) {
        SDKLog.a("Start => listener: " + (listener == null ? "null" : listener));
        mEventHandler.setListener(listener);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void startWithAppID(Context context, String appID) {
        SDKEventManager.domainInfo.put("openapi", "api.gizwits.com");
        SDKEventManager.domainInfo.put("site", "api.gizwits.com");
        SDKLog.init(context, SDKEventManager.domainInfo, appID, this.getVersion(), GizLogPrintLevel.GizLogPrintAll);
        SDKLog.a("Start => <deprecated> appID: " + Utils.dataMasking(appID));
        mEventHandler.startDaemon(context, appID);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void startWithAppID(Context context, String appID, List<String> specialProductKeys, ConcurrentHashMap<String, String> cloudServiceInfo) {
        SDKEventManager.cityJson = Utils.readJsonFileInJar(context, "tz_domain.json");
        SDKEventManager.domainInfo = Utils.getDomainInfo(cloudServiceInfo, false, SDKEventManager.cityJson);
        SDKLog.init(context, SDKEventManager.domainInfo, appID, this.getVersion(), GizLogPrintLevel.GizLogPrintAll);
        SDKLog.a("Start => <deprecated> appID: " + Utils.dataMasking(appID) + ", specialProductKeys: " + specialProductKeys + ", cloudServiceInfo: " + cloudServiceInfo);
        mEventHandler.startDaemon(context, appID, specialProductKeys, cloudServiceInfo, false);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void startWithAppID(Context context, String appID, List<String> specialProductKeys, ConcurrentHashMap<String, String> cloudServiceInfo, boolean autoSetDeviceDomain) {
        SDKEventManager.cityJson = Utils.readJsonFileInJar(context, "tz_domain.json");
        SDKEventManager.domainInfo = Utils.getDomainInfo(cloudServiceInfo, true, SDKEventManager.cityJson);
        SDKLog.init(context, SDKEventManager.domainInfo, appID, this.getVersion(), GizLogPrintLevel.GizLogPrintAll);
        SDKLog.a("Start => <deprecated> appID: " + Utils.dataMasking(appID) + ", specialProductKeys: " + specialProductKeys + ", cloudServiceInfo: " + cloudServiceInfo + ", autoSetDeviceDomain: " + autoSetDeviceDomain);
        mEventHandler.startDaemon(context, appID, specialProductKeys, cloudServiceInfo, false);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void startWithAppID(Context context, String appID, String appSecret, List<String> specialProductKeys, ConcurrentHashMap<String, String> cloudServiceInfo, boolean autoSetDeviceDomain) {
        SDKEventManager.cityJson = Utils.readJsonFileInJar(context, "tz_domain.json");
        SDKEventManager.domainInfo = Utils.getDomainInfo(cloudServiceInfo, true, SDKEventManager.cityJson);
        SDKLog.init(context, SDKEventManager.domainInfo, appID, this.getVersion(), GizLogPrintLevel.GizLogPrintAll);
        String appSecretMasking = appSecret != null ? Utils.dataMasking(appSecret) : null;
        SDKLog.a("Start => <deprecated> appID: " + Utils.dataMasking(appID) + ", appSecret: " + appSecretMasking + ", specialProductKeys: " + specialProductKeys + ", cloudServiceInfo: " + cloudServiceInfo + ", autoSetDeviceDomain: " + autoSetDeviceDomain);
        mEventHandler.startDaemon(context, appID, appSecret, specialProductKeys, cloudServiceInfo, false);
        SDKLog.a("End <= <deprecated>");
    }

    public void startWithAppInfo(Context context, ConcurrentHashMap<String, String> appInfo, List<ConcurrentHashMap<String, String>> productInfo, ConcurrentHashMap<String, String> cloudServiceInfo, boolean autoSetDeviceDomain) {
        String appId = appInfo != null ? (appInfo.containsKey("appId") ? (String)appInfo.get("appId") : null) : null;
        String appSecret = appInfo != null ? (appInfo.containsKey("appSecret") ? (String)appInfo.get("appSecret") : null) : null;
        String appSecretMasking = appSecret != null ? Utils.dataMasking(appSecret) : null;
        String appIdMasking = appId != null ? Utils.dataMasking(appId) : null;
        SDKEventManager.cityJson = Utils.readJsonFileInJar(context, "tz_domain.json");
        SDKEventManager.domainInfo = Utils.getDomainInfo(cloudServiceInfo, true, SDKEventManager.cityJson);
        SDKLog.init(context, SDKEventManager.domainInfo, appId, this.getVersion(), GizLogPrintLevel.GizLogPrintAll);
        List<ConcurrentHashMap<String, Object>> maskProductInfo = SDKEventManager.saveProductInfo(productInfo);
        SDKLog.a("Start => appInfo: " + appIdMasking + ", " + appSecretMasking + ", productInfo: " + maskProductInfo + ", cloudServiceInfo: " + cloudServiceInfo + ", autoSetDeviceDomain: " + autoSetDeviceDomain);
        mEventHandler.startDaemon(context, appId, appSecret, cloudServiceInfo, false);
        SDKLog.a("End <= ");
    }

    public void disableLAN(boolean disabled) {
        SDKLog.a("Start => disabled :" + disabled);
        mEventHandler.disableLAN(disabled);
        SDKLog.a("End <= ");
    }

    public String getVersion() {
        return SDKEventManager.getInstance().getVersion();
    }

    public void setLogLevel(GizLogPrintLevel logLevel) {
        SDKLog.a("Start => logLevel: " + (logLevel == null ? "null" : logLevel.name()));
        mEventHandler.setLogLevel(logLevel, false);
        SDKLog.a("End <= ");
    }

    public void encryptLog() {
        SDKLog.setEncryptLog(true);
        SDKLog.a("Start => ");
        mEventHandler.setLogLevel(GizLogPrintLevel.GizLogPrintNone, true);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void setLogLevel(GizLogPrintLevel logLevel, boolean writeSDCard) {
        SDKLog.a("Start => <deprecated> , logLevel: " + (logLevel == null ? "null" : logLevel.name()));
        mEventHandler.setLogLevel(logLevel, false);
        SDKLog.a("End <= <deprecated>");
    }

    public void stopDeviceOnboarding() {
        SDKLog.a("Start => ");
        mEventHandler.stopDeviceOnboarding();
        SDKLog.a("End <= ");
    }

    public void setDeviceOnboardingDeploy(String ssid, String key, GizWifiConfigureMode mode, String softAPSSIDPrefix, int timeout, List<GizWifiGAgentType> types, boolean bind) {
        SDKLog.a("Start => ssid: " + (TextUtils.isEmpty(ssid) ? "null" : ssid) + ", key: " + Utils.dataMasking(key) + ", mode: " + (mode == null ? "null" : mode.name()) + ", softAPSSIDPrefix: " + softAPSSIDPrefix + ", timeout: " + timeout + ", types: " + types + ", bind: " + bind);
        mEventHandler.setDeviceOnboardingDeploy(ssid, key, mode, softAPSSIDPrefix, timeout, types, bind, true);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void setDeviceOnboarding(String ssid, String key, GizWifiConfigureMode mode, String softAPSSIDPrefix, int timeout, List<GizWifiGAgentType> types) {
        SDKLog.a("Start => ssid: " + (TextUtils.isEmpty(ssid) ? "null" : ssid) + ", key: " + Utils.dataMasking(key) + ", mode: " + (mode == null ? "null" : mode.name()) + ", softAPSSIDPrefix: " + softAPSSIDPrefix + ", timeout: " + timeout + ", types: " + types);
        mEventHandler.setDeviceOnboardingDeploy(ssid, key, mode, softAPSSIDPrefix, timeout, types, false, false);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void setDeviceOnboardingByBind(String ssid, String key, GizWifiConfigureMode mode, String softAPSSIDPrefix, int timeout, List<GizWifiGAgentType> types) {
        SDKLog.a("Start => ssid: " + (TextUtils.isEmpty(ssid) ? "null" : ssid) + ", key: " + Utils.dataMasking(key) + ", mode: " + (mode == null ? "null" : mode.name()) + ", softAPSSIDPrefix: " + softAPSSIDPrefix + ", timeout: " + timeout + ", types: " + types);
        mEventHandler.setDeviceOnboardingDeploy(ssid, key, mode, softAPSSIDPrefix, timeout, types, true, false);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void setDeviceWifi(String ssid, String key, XPGWifiConfigureMode mode, String softAPSSIDPrefix, int timeout, List<XPGWifiGAgentType> types) {
        SDKLog.a("Start => <deprecated> ssid: " + (TextUtils.isEmpty(ssid) ? "null" : Utils.dataMasking(key)) + ", mode: " + (mode == null ? "null" : mode.name()) + ", softAPSSIDPrefix: " + softAPSSIDPrefix + ", timeout: " + timeout + ", types: " + types);
        GizWifiConfigureMode mode_v4a2 = GizWifiConfigureMode.GizWifiAirLink;
        if (mode == XPGWifiConfigureMode.XPGWifiConfigureModeSoftAP) {
            mode_v4a2 = GizWifiConfigureMode.GizWifiSoftAP;
        }

        List<GizWifiGAgentType> types_v4a2 = null;
        if (types != null) {
            types_v4a2 = new ArrayList();
            Iterator var9 = types.iterator();

            while(var9.hasNext()) {
                XPGWifiGAgentType type = (XPGWifiGAgentType)var9.next();
                if (type == XPGWifiGAgentType.XPGWifiGAgentTypeMXCHIP) {
                    types_v4a2.add(GizWifiGAgentType.GizGAgentMXCHIP);
                } else if (type == XPGWifiGAgentType.XPGWifiGAgentTypeHF) {
                    types_v4a2.add(GizWifiGAgentType.GizGAgentHF);
                } else if (type == XPGWifiGAgentType.XPGWifiGAgentTypeESP) {
                    types_v4a2.add(GizWifiGAgentType.GizGAgentESP);
                } else if (type == XPGWifiGAgentType.XPGWifiGAgentTypeWM) {
                    types_v4a2.add(GizWifiGAgentType.GizGAgentWM);
                } else if (type == XPGWifiGAgentType.XPGWifiGAgentTypeRTK) {
                    types_v4a2.add(GizWifiGAgentType.GizGAgentRTK);
                } else if (type == XPGWifiGAgentType.XPGWifiGAgentTypeFSK) {
                    types_v4a2.add(GizWifiGAgentType.GizGAgentFSK);
                } else if (type == XPGWifiGAgentType.XPGWifiGAgentTypeBL) {
                    types_v4a2.add(GizWifiGAgentType.GizGAgentBL);
                } else if (type == XPGWifiGAgentType.XPGWifiGAgentTypeQCA) {
                    types_v4a2.add(GizWifiGAgentType.GizGAgentQCA);
                } else if (type == XPGWifiGAgentType.XPGWifiGAgentTypeTI) {
                    types_v4a2.add(GizWifiGAgentType.GizGAgentTI);
                } else if (type == XPGWifiGAgentType.XPGWifiGAgentTypeMXCHIP3) {
                    types_v4a2.add(GizWifiGAgentType.GizGAgentMXCHIP3);
                } else if (type == XPGWifiGAgentType.XPGWifiGAgentTypeHFV8) {
                    types_v4a2.add(GizWifiGAgentType.GizGAgentHFV8);
                }
            }
        }

        this.setDeviceOnboarding(ssid, key, mode_v4a2, softAPSSIDPrefix, timeout, types_v4a2);
        SDKLog.a("End <= <deprecated>");
    }

    public void getSSIDList() {
        SDKLog.a("Start => ");
        mEventHandler.getSSIDList();
        SDKLog.a("End <= ");
    }

    public void searchMeshDevice(String meshName, int timeout) {
        SDKLog.a("Start => meshName: " + meshName);
        mEventHandler.searchBTDevice(meshName, timeout);
        SDKLog.a("End <= ");
    }

    public void setUserMeshName(String meshName, String passwrod, ConcurrentHashMap<String, String> uuidInfo, byte[] meshLTK, GizMeshVendor gizMeshVendor) {
        SDKLog.a("Start => meshName: " + meshName + ",password:" + passwrod + ",meshVendor:" + gizMeshVendor.name());
        mEventHandler.setUserMeshName(meshName, passwrod, uuidInfo, meshLTK, gizMeshVendor);
        SDKLog.a("End <= ");
    }

    public void changeDeviceMesh(ConcurrentHashMap<String, String> meshDeviceInfo, ConcurrentHashMap<String, String> currentMeshInfo, int newMeshID) {
        SDKLog.a("Start => meshDeviceInfo: " + meshDeviceInfo.toString() + ",currentMeshInfo:" + currentMeshInfo.toString() + ",newMeshId:" + newMeshID);
        mEventHandler.changeDeviceMesh(meshDeviceInfo, currentMeshInfo, newMeshID);
        SDKLog.a("End <= ");
    }

    public void addGroup(int groupID, List<GizWifiDevice> devices) {
        SDKLog.a("Start => groupID: " + groupID);
        mEventHandler.addGroupByDevice(groupID, devices);
        SDKLog.a("End <= ");
    }

    public void addGroupByString(int groupID, List<String> macs) {
        SDKLog.a("Start => groupID: " + groupID + ",gizWifiDeviceSize:" + macs.size());
        mEventHandler.addGroup(groupID, macs);
        SDKLog.a("End <= ");
    }

    public void restoreDeviceFactorySetting(GizLiteGWSubDevice gizLiteGWSubDevice) {
        SDKLog.a("Start => restoreMac: ");
        mEventHandler.restoreDeviceFactory(gizLiteGWSubDevice);
        SDKLog.a("End <= ");
    }

    public void deleteDeviceFromGroup(int groupID, List<GizWifiDevice> devices) {
        SDKLog.a("Start => groupID: " + groupID);
        mEventHandler.deleteGroupByDevice(groupID, devices);
        SDKLog.a("End <= ");
    }

    public void updateGroupsWithDevice(GizWifiDevice device) {
        SDKLog.a("Start => ");
        mEventHandler.updateGroupWitshDevice(device);
        SDKLog.a("End <= ");
    }

    public void getDeviceLog(String softAPSSIDPrefix) {
        SDKLog.a("Start => getDeviceLog: " + softAPSSIDPrefix);
        mEventHandler.getDeviceLog(softAPSSIDPrefix);
        SDKLog.a("End <= ");
    }

    public void getMapTab() {
        SDKLog.a("Start => getMapTab: ");
        mEventHandler.getMapTab();
        SDKLog.a("End <= ");
    }

    public void deviceSafetyRegister(GizWifiDevice gateway, String productKey, List<ConcurrentHashMap<String, String>> devicesInfo) {
        SDKLog.a("Start => gateway: " + (gateway == null ? "null" : gateway.moreSimpleInfoMasking()) + ", productKey: " + productKey + ", devicesInfo: " + devicesInfo);
        mEventHandler.deviceSafetyRegister(gateway, productKey, devicesInfo);
        SDKLog.a("End <= ");
    }

    public void deviceSafetyUnbind(List<ConcurrentHashMap<String, Object>> devicesInfo) {
        SDKLog.a("Start => , devicesInfo size: " + (devicesInfo == null ? "0" : devicesInfo.size()));
        mEventHandler.deviceSafetyUnbind(devicesInfo);
        SDKLog.a("End <= ");
    }

    public void getBoundDevices(String uid, String token) {
        SDKLog.a("Start => uid: " + Utils.dataMasking(uid) + ", token: " + Utils.dataMasking(token));
        mEventHandler.getBoundDevices(uid, token, (List)null);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void getBoundDevices(String uid, String token, List<String> specialProductKey) {
        SDKLog.a("Start => <deprecated> uid: " + uid + ", token: " + Utils.dataMasking(token) + ", specialProductKey: " + (specialProductKey == null ? "null" : specialProductKey));
        mEventHandler.getBoundDevices(uid, token, specialProductKey);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void updateDeviceFromServer(String productKey) {
        SDKLog.a("Start => <deprecated>");
        mEventHandler.updateDeviceFromServer(productKey);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void bindDevice(String uid, String token, String did, String passCode, String remark) {
        SDKLog.a("Start => <deprecated>uid: " + uid + ", token: " + Utils.dataMasking(token) + ", did: " + Utils.dataMasking(did) + ", passCode: " + Utils.dataMasking(passCode) + ", remark: " + remark);
        mEventHandler.bindDevice(uid, token, did, passCode, remark);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void bindRemoteDevice(String uid, String token, String mac, String productKey, String productSecret) {
        SDKLog.a("Start => uid: " + uid + ", token: " + Utils.dataMasking(token) + ", mac: " + mac + ", productKey: " + productKey + ", productSecret: " + Utils.dataMasking(productSecret));
        mEventHandler.bindRemoteDevice(uid, token, mac, productKey, productSecret, false);
        SDKLog.a("End <= ");
    }

    public void bindRemoteDevice(String uid, String token, String mac, String productKey, String productSecret, boolean beOwner) {
        SDKLog.a("Start => uid: " + uid + ", token: " + Utils.dataMasking(token) + ", mac: " + mac + ", productKey: " + productKey + ", productSecret: " + Utils.dataMasking(productSecret) + ", beOwner: " + beOwner);
        mEventHandler.bindRemoteDevice(uid, token, mac, productKey, productSecret, beOwner);
        SDKLog.a("End <= ");
    }

    public void bindDeviceByQRCode(String uid, String token, String QRContent, boolean beOwner) {
        SDKLog.a("Start => uid: " + uid + ", token: " + Utils.dataMasking(token) + ", QRContent: " + Utils.dataMasking(QRContent) + "beOwner: " + beOwner);
        mEventHandler.bindDeviceByQRCode(uid, token, QRContent, beOwner);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void unbindDevice(String uid, String token, String did, String passCode) {
        SDKLog.a("Start => <deprecated>uid: " + uid + ", token: " + Utils.dataMasking(token) + ", did: " + Utils.dataMasking(did) + "passCode" + Utils.dataMasking(passCode));
        mEventHandler.unbindDevice(uid, token, did);
        SDKLog.a("End <= <deprecated>");
    }

    public void unbindDevice(String uid, String token, String did) {
        SDKLog.a("Start => uid: " + uid + ", token: " + Utils.dataMasking(token) + ", did: " + Utils.dataMasking(did));
        mEventHandler.unbindDevice(uid, token, did);
        SDKLog.a("End <= ");
    }

    public void getCaptchaCode(String appSecret) {
        SDKLog.a("Start => appSecret: " + Utils.dataMasking(appSecret));
        mEventHandler.getCaptchaCode(appSecret);
        SDKLog.a("End <= ");
    }

    public void requestSendPhoneSMSCode(String token, String captchaId, String captchaCode, String phone) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + ", captchaId: " + Utils.dataMasking(captchaId) + ", captchaCode: " + Utils.dataMasking(captchaCode) + ", phone: " + Utils.dataMasking(phone));
        mEventHandler.requestSendPhoneSMSCode(token, captchaId, captchaCode, phone);
        SDKLog.a("End <= ");
    }

    public void requestSendPhoneSMSCode(String appSecret, String phone) {
        SDKLog.a("Start => appSecret: " + Utils.dataMasking(appSecret) + ", phone: " + Utils.dataMasking(phone));
        mEventHandler.requestSendPhoneSMSCode(appSecret, phone);
        SDKLog.a("End <= ");
    }

    public void verifyPhoneSMSCode(String token, String phoneCode, String phone) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + ", phoneCode: " + Utils.dataMasking(phoneCode) + ", phone: " + Utils.dataMasking(phone));
        mEventHandler.verifyPhoneSMSCode(token, phoneCode, phone);
        SDKLog.a("End <= ");
    }

    public void registerUser(String username, String password, String code, GizUserAccountType accountType) {
        SDKLog.a("Start => username: " + Utils.dataMasking(username) + ", accountType: " + (accountType == null ? "null" : accountType.name()) + ", password :******");
        mEventHandler.registerUser(username, password, code, accountType);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void registerUser(String userName, String password) {
        SDKLog.a("Start => <deprecated>userName: " + Utils.dataMasking(userName) + ", password: ******");
        mEventHandler.registerUser(userName, password, (String)null, GizUserAccountType.GizUserNormal);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void registerUserByPhoneAndCode(String phone, String password, String code) {
        SDKLog.a("Start => <deprecated>phone: " + Utils.dataMasking(phone) + ", password: ******, code: " + Utils.dataMasking(code));
        mEventHandler.registerUser(phone, password, code, GizUserAccountType.GizUserPhone);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void registerUserByEmail(String email, String password) {
        SDKLog.a("Start => <deprecated>email: " + Utils.dataMasking(email) + ", password: ******");
        mEventHandler.registerUser(email, password, (String)null, GizUserAccountType.GizUserEmail);
        SDKLog.a("End <= <deprecated>");
    }

    public void userLoginAnonymous() {
        SDKLog.a("Start => ");
        mEventHandler.userLoginAnonymous();
        SDKLog.a("End <= ");
    }

    public void dynamicLogin(String phone, String code) {
        SDKLog.a("Start => phone: " + Utils.dataMasking(phone) + ", code :******");
        mEventHandler.dynamicLogin(phone, code);
        SDKLog.a("End <= ");
    }

    public void userLogin(String username, String password) {
        SDKLog.a("Start => username: " + Utils.dataMasking(username) + ", password :******");
        mEventHandler.userLogin(username, password);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void userLoginWithUserName(String userName, String password) {
        SDKLog.a("Start => <deprecated>username: " + Utils.dataMasking(userName) + ", password :******");
        mEventHandler.userLogin(userName, password);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void loginWithThirdAccount(GizThirdAccountType thirdAccountType, String uid, String token) {
        SDKLog.a("Start => <deprecated> uid: " + uid + ", token: " + Utils.dataMasking(token) + ", thirdAccountType: " + (thirdAccountType == null ? "null" : thirdAccountType.name()));
        mEventHandler.loginWithThirdAccountType(thirdAccountType, uid, token);
        SDKLog.a("End <= <deprecated>");
    }

    public void loginWithThirdAccount(GizThirdAccountType thirdAccountType, String uid, String token, String tokenSecret) {
        SDKLog.a("Start => uid: " + uid + ", token: " + Utils.dataMasking(token) + ", tokenSecret: " + Utils.dataMasking(tokenSecret) + ", thirdAccountType: " + (thirdAccountType == null ? "null" : thirdAccountType.name()));
        mEventHandler.loginWithThirdAccountType(thirdAccountType, uid, token, tokenSecret);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void loginWithThirdAccountType(XPGWifiThirdAccountType thirdAccountType, String uid, String token) {
        SDKLog.a("Start => <deprecated>");
        GizThirdAccountType type = null;
        switch(thirdAccountType) {
            case XPGWifiThirdAccountTypeBAIDU:
                type = GizThirdAccountType.GizThirdBAIDU;
                break;
            case XPGWifiThirdAccountTypeQQ:
                type = GizThirdAccountType.GizThirdQQ;
                break;
            case XPGWifiThirdAccountTypeSINA:
                type = GizThirdAccountType.GizThirdSINA;
        }

        mEventHandler.loginWithThirdAccountType(type, uid, token);
        SDKLog.a("End <= <deprecated>");
    }

    public void changeUserPassword(String token, String oldPassword, String newPassword) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "oldPassword: " + Utils.dataMasking(oldPassword) + "newPassword: " + Utils.dataMasking(newPassword));
        mEventHandler.changeUserPassword(token, oldPassword, newPassword);
        SDKLog.a("End <= ");
    }

    public void resetPassword(String username, String code, String newPassword, GizUserAccountType accountType) {
        SDKLog.a("Start => username: " + Utils.dataMasking(username) + "code: " + Utils.dataMasking(code) + "accountType: " + (accountType == null ? "null" : accountType.name()) + " newPassWord :" + (newPassword == null ? "null" : "******"));
        mEventHandler.resetUserPassword(username, code, newPassword, accountType);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void changeUserPasswordByCode(String phone, String code, String newPassword) {
        SDKLog.a("Start => <deprecated>" + Utils.dataMasking(phone) + Utils.dataMasking(code) + (newPassword == null ? "null" : "******"));
        mEventHandler.resetUserPassword(phone, code, newPassword, GizUserAccountType.GizUserPhone);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void changeUserPasswordByEmail(String email) {
        SDKLog.a("Start => <deprecated>");
        mEventHandler.resetUserPassword(email, (String)null, "", GizUserAccountType.GizUserEmail);
        SDKLog.a("End <= <deprecated>");
    }

    public void changeUserInfo(String token, String username, String code, GizUserAccountType accountType, GizUserInfo additionalInfo) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + ", username: " + Utils.dataMasking(username) + ", accountType: " + (accountType == null ? "null" : accountType.name()) + ", additionalInfo: " + (additionalInfo == null ? "null" : additionalInfo.infoMasking()));
        mEventHandler.changeUserInfo(token, username, code, accountType, additionalInfo);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void changeUserInfo(String token, String username, String code, XPGUserAccountType accountType, GizUserInfo additionalInfo) {
        SDKLog.a("Start => <deprecated>token: " + Utils.dataMasking(token) + ", username: " + Utils.dataMasking(username) + ", accountType: " + (accountType == null ? "null" : accountType.name()) + ", additionalInfo: " + additionalInfo.infoMasking());
        GizUserAccountType accountType_v4a2 = null;
        switch(accountType) {
            case Email:
                accountType_v4a2 = GizUserAccountType.GizUserEmail;
                break;
            case Normal:
                accountType_v4a2 = GizUserAccountType.GizUserNormal;
                break;
            case Phone:
                accountType_v4a2 = GizUserAccountType.GizUserPhone;
        }

        mEventHandler.changeUserInfo(token, username, code, accountType_v4a2, additionalInfo);
        SDKLog.a("End <= <deprecated>");
    }

    public void transAnonymousUser(String token, String username, String password, String code, GizUserAccountType accountType) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + ", username: " + Utils.dataMasking(username) + ", accountType: " + (accountType == null ? "null" : accountType.name()) + ", password :" + (password == null ? "null" : "******"));
        mEventHandler.transAnonymousUser(token, username, password, code, accountType);
        SDKLog.a("End <= ");
    }

    /** @deprecated */
    public void transAnonymousUserToNormalUser(String token, String userName, String password) {
        SDKLog.a("Start => <deprecated>token: " + Utils.dataMasking(token) + ", userName: " + Utils.dataMasking(userName) + ", password :" + (password == null ? "null" : "******"));
        mEventHandler.transAnonymousUser(token, userName, password, (String)null, GizUserAccountType.GizUserNormal);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void transAnonymousUserToPhoneUser(String token, String userName, String password, String code) {
        SDKLog.a("Start => <deprecated>token: " + Utils.dataMasking(token) + ", userName: " + Utils.dataMasking(userName) + ", password :" + (password == null ? "null" : "******") + ", code: " + Utils.dataMasking(code));
        mEventHandler.transAnonymousUser(token, userName, password, code, GizUserAccountType.GizUserPhone);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void setCloudService(String openAPIDomain, int openAPIPort, String siteDomain, int sitePort) {
        SDKLog.a("Start => <deprecated> openAPIDomain: " + openAPIDomain + "openAPIPort: " + openAPIPort + "siteDomain: " + siteDomain + "sitePort: " + sitePort);
        mEventHandler.setCloudService(openAPIDomain, openAPIPort, siteDomain, sitePort);
        SDKLog.a("End <= <deprecated>");
    }

    /** @deprecated */
    public void setCloudService(ConcurrentHashMap<String, String> cloudServiceInfo) {
        SDKLog.a("Start => <deprecated> cloudServiceInfo: " + cloudServiceInfo);
        mEventHandler.setCloudService(cloudServiceInfo);
        SDKLog.a("End <= <deprecated>");
    }

    public void getCurrentCloudService() {
        SDKLog.a("Start => ");
        mEventHandler.getCurrentCloudService();
        SDKLog.a("End <= ");
    }

    public void getUserInfo(String token) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token));
        mEventHandler.getUserInfo(token);
        SDKLog.a("End <= ");
    }

    public List<GizWifiDevice> getDeviceList() {
        SDKLog.a("Start => ");
        List<GizWifiDevice> list = mEventHandler.getDeviceList();
        SDKLog.d("End <= ");
        return list;
    }

    public List<GizWifiGroup> getGroupList() {
        return null;
    }

    public void userLogout() {
        SDKLog.a("Start => ");
        mEventHandler.userLogout();
        SDKLog.a("End <= ");
    }

    public void userLogout(String uid) {
        SDKLog.a("Start => ");
        mEventHandler.userLogout();
        SDKLog.a("End <= ");
    }

    public void channelIDBind(String token, String channelID, String alias, GizPushType pushType) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "channelID: " + channelID + ", pushType: " + (pushType == null ? "null" : pushType.name()) + ", alias: " + alias);
        mEventHandler.channelIDBind(token, channelID, alias, pushType);
        SDKLog.a("End <= ");
    }

    public void channelIDUnBind(String token, String channelID) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "channelID: " + channelID);
        mEventHandler.channelIDUnBind(token, channelID);
        SDKLog.a("End <= ");
    }

    public void getDevicesToSetServerInfo() {
        SDKLog.a("Start => ");
        mEventHandler.getDevicesToSetServerInfo();
        SDKLog.a("End <= ");
    }

    public void setDeviceServerInfo(String domain, String mac) {
        SDKLog.a("Start => domain: " + domain + ", mac: " + mac);
        mEventHandler.setDeviceServerInfo(domain, mac);
        SDKLog.a("End <= ");
    }

    public void userFeedback(String contactInfo, String feedbackInfo, boolean sendLog) {
        SDKLog.a("Start => contactInfo: " + (contactInfo == null ? "null" : Utils.dataMasking(contactInfo)) + "feedbackInfo: " + feedbackInfo + "sendLog: " + sendLog);
        if (TextUtils.isEmpty(feedbackInfo) && TextUtils.isEmpty(contactInfo)) {
            SDKLog.userFeedback(contactInfo, feedbackInfo, true);
        } else {
            SDKLog.userFeedback(contactInfo, feedbackInfo, sendLog);
        }

        SDKLog.a("End <= ");
    }

    public void shareLogFile() {
        SDKLog.a("Start => ");
        SDKLog.a("End <= ");
    }

    public String getPhoneID() {
        return mEventHandler.getPhoneID();
    }

    public void setRN(boolean isRN) {
        mEventHandler.setRN(isRN);
    }
}
