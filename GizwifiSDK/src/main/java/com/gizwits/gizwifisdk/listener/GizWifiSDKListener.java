//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.listener;

import com.gizwits.gizwifisdk.api.GizDeviceScheduler;
import com.gizwits.gizwifisdk.api.GizUserInfo;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiGroup;
import com.gizwits.gizwifisdk.api.GizWifiSSID;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizScheduleStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GizWifiSDKListener {
   public GizWifiSDKListener() {
   }

   public void didNotifyEvent(GizEventType eventType, Object eventSource, GizWifiErrorCode eventID, String eventMessage) {
   }

   public void didSetDeviceOnboarding(GizWifiErrorCode result, GizWifiDevice device) {
   }

   public void didDeviceSafetyRegister(List<ConcurrentHashMap<String, Object>> successDevices, List<ConcurrentHashMap<String, Object>> failedDevices) {
   }

   public void didDeviceSafetyUnbind(List<ConcurrentHashMap<String, Object>> failedDevices) {
   }

   /** @deprecated */
   public void didSetDeviceOnboarding(GizWifiErrorCode result, String mac, String did, String productKey) {
   }

   /** @deprecated */
   public void didSetDeviceWifi(int error, GizWifiDevice device) {
   }

   public void didGetSSIDList(GizWifiErrorCode result, List<GizWifiSSID> ssidInfoList) {
   }

   /** @deprecated */
   public void didGetSSIDList(int error, List<GizWifiSSID> ssidInfoList) {
   }

   public void didDiscoveredMeshDevices(GizWifiErrorCode result, List<ConcurrentHashMap<String, String>> mesDeviceList) {
   }

   public void didChangeDeviceMesh(GizWifiErrorCode result, ConcurrentHashMap<String, String> mesDevice) {
   }

   public void didAddDevicesToGroup(GizWifiErrorCode result, List<GizWifiDevice> mesDevice) {
   }

   public void didDeleteDevicesFromGroup(GizWifiErrorCode result, List<GizWifiDevice> gisWifiDevices) {
   }

   public void didUpdateGroupsWithDevice(GizWifiErrorCode result, List<Integer> groupIDList, GizWifiDevice gizWifiDevice) {
   }

   public void didRestoreDeviceFactorySetting(GizWifiErrorCode result, String mac) {
   }

   public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
   }

   /** @deprecated */
   public void didDiscovered(int error, List<GizWifiDevice> devicesList) {
   }

   public void didUpdateProduct(GizWifiErrorCode result, String productKey, String productUI) {
   }

   /** @deprecated */
   public void didUpdateProduct(int error, String productKey) {
   }

   public void didBindDevice(GizWifiErrorCode result, String did) {
   }

   /** @deprecated */
   public void didBindDevice(int error, String errorMessage, String did) {
   }

   public void didUnbindDevice(GizWifiErrorCode result, String did) {
   }

   /** @deprecated */
   public void didUnbindDevice(int error, String errorMessage, String did) {
   }

   public void didGetCaptchaCode(GizWifiErrorCode result, String token, String captchaId, String captchaURL) {
   }

   /** @deprecated */
   public void didGetCaptchaCode(int result, String errorMessage, String token, String captchaId, String captchaURL) {
   }

   public void didRequestSendPhoneSMSCode(GizWifiErrorCode result, String token) {
   }

   /** @deprecated */
   public void didRequestSendPhoneSMSCode(int result, String errorMessage) {
   }

   public void didVerifyPhoneSMSCode(GizWifiErrorCode result) {
   }

   /** @deprecated */
   public void didVerifyPhoneSMSCode(int result, String errorMessage) {
   }

   public void didRegisterUser(GizWifiErrorCode result, String uid, String token) {
   }

   /** @deprecated */
   public void didRegisterUser(int error, String errorMessage, String uid, String token) {
   }

   public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
   }

   /** @deprecated */
   public void didUserLogin(int error, String errorMessage, String uid, String token) {
   }

   public void didChangeUserPassword(GizWifiErrorCode result) {
   }

   /** @deprecated */
   public void didChangeUserPassword(int error, String errorMessage) {
   }

   public void didChangeUserInfo(GizWifiErrorCode result) {
   }

   /** @deprecated */
   public void didChangeUserInfo(int error, String errorMessage) {
   }

   public void didTransAnonymousUser(GizWifiErrorCode result) {
   }

   /** @deprecated */
   public void didTransUser(int error, String errorMessage) {
   }

   public void didUserLogout(GizWifiErrorCode result) {
   }

   /** @deprecated */
   public void didGetGroups(GizWifiErrorCode result, List<GizWifiGroup> groupList) {
   }

   /** @deprecated */
   public void didGetGroups(int error, List<GizWifiGroup> groupList) {
   }

   public void didGetCurrentCloudService(GizWifiErrorCode result, ConcurrentHashMap<String, String> cloudServiceInfo) {
   }

   /** @deprecated */
   public void didGetCurrentCloudService(int result, String errorMessage, ConcurrentHashMap<String, String> cloudServiceInfo) {
   }

   public void didGetUserInfo(GizWifiErrorCode result, GizUserInfo userInfo) {
   }

   /** @deprecated */
   public void didGetUserInfo(int error, String errorMessage, GizUserInfo userInfo) {
   }

   public void didDisableLAN(GizWifiErrorCode result) {
   }

   public void didChannelIDBind(GizWifiErrorCode result) {
   }

   public void didChannelIDUnBind(GizWifiErrorCode result) {
   }

   /** @deprecated */
   public void didCreateScheduler(GizWifiErrorCode result, String sid) {
   }

   /** @deprecated */
   public void didDeleteScheduler(GizWifiErrorCode result) {
   }

   /** @deprecated */
   public void didGetSchedulers(GizWifiErrorCode result, List<GizDeviceScheduler> scheduleTaskList) {
   }

   public void didGetSchedulerStatus(GizWifiErrorCode result, String sid, String datetime, GizScheduleStatus status, ConcurrentHashMap<String, Boolean> statusDetail) {
   }

   public void didGetDevicesToSetServerInfo(GizWifiErrorCode result, List<ConcurrentHashMap<String, String>> devices) {
   }

   public void didSetDeviceServerInfo(GizWifiErrorCode result, String mac) {
   }

   public void didReceiveDeviceLog(GizWifiErrorCode result, String mac, int timestamp, int logSN, String log) {
   }

   public void didGetMapTab(GizWifiErrorCode result) {
   }
}
