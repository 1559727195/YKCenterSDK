//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Build.VERSION;
import android.provider.MediaStore.Files;
import android.provider.Settings.Secure;
import android.text.TextUtils;

import com.gizwits.gizwifisdk.GizWifiDaemon;
import com.gizwits.gizwifisdk.enumration.GizAdapterType;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizLocalMeshType;
import com.gizwits.gizwifisdk.enumration.GizLogPrintLevel;
import com.gizwits.gizwifisdk.enumration.GizMeshVendor;
import com.gizwits.gizwifisdk.enumration.GizPushType;
import com.gizwits.gizwifisdk.enumration.GizScheduleStatus;
import com.gizwits.gizwifisdk.enumration.GizThirdAccountType;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;
import com.gizwits.gizwifisdk.enumration.GizUserGenderType;
import com.gizwits.gizwifisdk.enumration.GizWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.enumration.GizWifiGAgentType;
import com.gizwits.gizwifisdk.enumration.XPGUserGenderType;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import com.telink.crypto.AES;
import com.telink.crypto.Manufacture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class SDKEventManager {
    private static final SDKEventManager mInstance = new SDKEventManager() {
    };
    private static GizWifiSDKListener mListener;
    protected static Context mContext;
    private static List<GizWifiDevice> deviceList = new Vector();
    private static List<String> specialProductKeys = new ArrayList();
    private static List<ConcurrentHashMap<String, Object>> productInfo = null;
    private static String softAPHotspotPrefix = "XPG-GAgent-";
    private static String appId;
    private static String appSecret;
    private static boolean autoSetDeviceDomain = false;
    protected static JSONObject cityJson = null;
    private static AtomicBoolean isStartDaemon = new AtomicBoolean(false);
    protected static ConcurrentHashMap<String, String> domainInfo = new ConcurrentHashMap();
    private static GizLogPrintLevel logLevel;
    private static long onboarding_end;
    private static long onboarding_start;
    private static long user_login_start;
    private static long user_register_start;
    private List<Integer> onboardingSn = new ArrayList();
    private static List<GizWifiGAgentType> gagentTypes;
    private static GizWifiConfigureMode configureMode;
    private static boolean is5GWifi;
    private static String onboardingSsid;
    private static String originalKey;
    private static int onboardingTimeout;
    private static boolean isDisableLan;
    private static String userMeshName;
    private static String userPassword;
    private static String serviceUUID;
    private static String pairUUID;
    private static String commandUUID;
    private static String notifyUUID;
    private byte[] meshLTK = null;
    private static GizMeshVendor gizMeshVendor;
    private String loginMeshName = null;
    private String loginPassword = null;
    private int newMeshID = -1;
    private ConcurrentHashMap<String, String> currentMeshInfo = null;
    private List<ConcurrentHashMap<String, String>> meshDeviceList = null;
    private final byte[] loginRandm = new byte[8];
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCharacteristic loginGattCharacteristic;
    private BluetoothGattCharacteristic commandGattCharacteristic;
    private BluetoothGattCharacteristic notifyGattCharacteristic;
    private BluetoothGatt mBluetoothGatt;
    private int addGroupID;
    private int deleteGroupID;
    private byte[] sessionKey;
    private byte[] currentGroupMac;
    private List<ConcurrentHashMap<String, String>> addGroupMacList;
    private List<GizWifiDevice> addGroupMacSuccessList;
    private List<ConcurrentHashMap<String, String>> deleteGroupMacList;
    private ConcurrentHashMap<String, String> updateMac;
    private List<GizWifiDevice> deleteGroupMacSuccessList;
    private static int MeshAction;
    private static int CurrentMsg;
    private Random random = new SecureRandom();
    private static final int MSG_SEARCH = 0;
    private static final int MSG_CONNECT_MESHDEVICE = 1;
    private static final int MSG_LOGIN_MESHDEVICE = 2;
    private static final int MSG_RESET_MESHDEVICE = 3;
    private static final int MSG_ADD_GROUP = 4;
    private static final int MSG_RESET_MESHID = 5;
    private static final int MSG_RESET_MESHID_CALLBACK = 6;
    private static final int MSG_RESTORE_FACTORY = 7;
    private static final int MSG_RESTORE_FACTORY_CALLBACK = 8;
    private static final int MSG_DELETE_GROUP = 9;
    private static final int MSG_GET_MESH_GROUPID = 10;
    private static final int MSG_GET_MESH_GROUPID_CALLBACK = 11;
    private static final int MSG_TIMEOUT = 12;
    private static final int MSG_LOGIN_FAIL = 13;
    private static final int MSG_CONNECT_FAIL = 14;
    private boolean isRN = false;
    private static final int MSG_DEVICE_CALLBACK = 7;
    private Handler resetMeshDeviceHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            if (msg.what > SDKEventManager.CurrentMsg || SDKEventManager.CurrentMsg == 4 || SDKEventManager.CurrentMsg == 9) {
                SDKEventManager.CurrentMsg = msg.what;
                switch (msg.what) {
                    case 1:
                        SDKEventManager.this.connectDevice();
                        break;
                    case 2:
                        SDKEventManager.this.loginDevice();
                        break;
                    case 3:
                        SDKEventManager.this.resetMeshName();
                        break;
                    case 4:
                        int position = msg.getData().getInt("position");
                        SDKEventManager.this.resetMeshGroupID(position, false);
                        break;
                    case 5:
                        SDKEventManager.this.resetMeshID();
                        break;
                    case 6:
                        ConcurrentHashMap<String, String> meshDevice = new ConcurrentHashMap();
                        meshDevice.put("mac", SDKEventManager.this.currentMeshInfo.get("mac"));
                        meshDevice.put("meshID", SDKEventManager.this.newMeshID + "");
                        SDKEventManager.this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_SUCCESS, meshDevice);
                        break;
                    case 7:
                        SDKEventManager.this.restoreFactory();
                        break;
                    case 8:
                        SDKEventManager.this.onDidRestoreDeviceFactorySetting(GizWifiErrorCode.GIZ_SDK_SUCCESS, (String) SDKEventManager.this.currentMeshInfo.get("mac"));
                        break;
                    case 9:
                        int deleteposition = msg.getData().getInt("position");
                        SDKEventManager.this.resetMeshGroupID(deleteposition, true);
                        break;
                    case 10:
                        SDKEventManager.this.getGroupID();
                        break;
                    case 11:
                        List<Integer> ids = (List) msg.obj;
                        GizWifiDevice gizWifiDevice = null;
                        String mac = (String) SDKEventManager.this.updateMac.get("mac");

                        for (int i = 0; i < SDKEventManager.deviceList.size(); ++i) {
                            if (((GizWifiDevice) SDKEventManager.deviceList.get(i)).getMacAddress().equals(mac)) {
                                gizWifiDevice = (GizWifiDevice) SDKEventManager.deviceList.get(i);
                            }
                        }

                        SDKEventManager.this.onDidUpdateGroupWithDevice(GizWifiErrorCode.GIZ_SDK_SUCCESS, ids, gizWifiDevice);
                        break;
                    case 12:
                    case 13:
                    case 14:
                        GizWifiErrorCode gizWifiErrorCode = GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT;
                        if (msg.what == 13) {
                            gizWifiErrorCode = GizWifiErrorCode.GIZ_SDK_BLE_LOGIN_FAILED;
                        } else if (msg.what == 14) {
                            gizWifiErrorCode = GizWifiErrorCode.GIZ_SDK_BLE_DEVICE_CONNECT_FAILED;
                        }

                        if (SDKEventManager.MeshAction == 3) {
                            SDKEventManager.this.onDidChangeDeviceMesh(gizWifiErrorCode, (ConcurrentHashMap) null);
                        } else if (SDKEventManager.MeshAction == 4) {
                            SDKEventManager.this.onDidAddDevicesToGroup(gizWifiErrorCode, (List) null);
                        } else if (SDKEventManager.MeshAction == 9) {
                            SDKEventManager.this.onDidDeleteDevicesFromGroup(gizWifiErrorCode, (List) null);
                        } else if (SDKEventManager.MeshAction == 7) {
                            SDKEventManager.this.onDidRestoreDeviceFactorySetting(gizWifiErrorCode, (String) null);
                        } else if (SDKEventManager.MeshAction == 10) {
                            SDKEventManager.this.onDidUpdateGroupWithDevice(gizWifiErrorCode, (List) null, (GizWifiDevice) null);
                        }

                        if (SDKEventManager.this.mBluetoothGatt != null) {
                            SDKEventManager.this.mBluetoothGatt.disconnect();
                        }
                }

            }
        }
    };
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == 2) {
                SDKLog.e("Connected to GATT server.");
                SDKLog.e("Attempting to start service discovery:" + SDKEventManager.this.mBluetoothGatt.discoverServices());
            } else if (newState == 0) {
                if (SDKEventManager.CurrentMsg != -1) {
                    SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessage(14);
                }

                SDKLog.e("Disconnected to GATT server.");
            }

        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> gattServices = gatt.getServices();
            if (gattServices != null) {
                Iterator var4 = gattServices.iterator();

                while (var4.hasNext()) {
                    BluetoothGattService gattService = (BluetoothGattService) var4.next();
                    int type = gattService.getType();
                    SDKLog.e("find BluetoothGattService uuid:" + gattService.getUuid().toString());
                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                    Iterator var8 = gattCharacteristics.iterator();

                    while (var8.hasNext()) {
                        BluetoothGattCharacteristic gattCharacteristic = (BluetoothGattCharacteristic) var8.next();
                        int permission = gattCharacteristic.getPermissions();
                        int property = gattCharacteristic.getProperties();
                        byte[] data = gattCharacteristic.getValue();
                        if (gattCharacteristic.getUuid().toString().equals(SDKEventManager.pairUUID.toLowerCase())) {
                            SDKEventManager.this.loginGattCharacteristic = gattCharacteristic;
                            SDKLog.e("find pairUUID:" + gattCharacteristic.getUuid().toString());
                            SDKEventManager.this.mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                            SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessage(2);
                        }

                        if (gattCharacteristic.getUuid().toString().equals(SDKEventManager.commandUUID.toLowerCase())) {
                            SDKEventManager.this.commandGattCharacteristic = gattCharacteristic;
                            SDKLog.e("find commandUUID:" + gattCharacteristic.getUuid().toString());
                            SDKEventManager.this.mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                        }

                        if (gattCharacteristic.getUuid().toString().equals(SDKEventManager.notifyUUID.toLowerCase())) {
                            SDKEventManager.this.notifyGattCharacteristic = gattCharacteristic;
                            SDKLog.e("find notifyUUID:" + gattCharacteristic.getUuid().toString());
                        }
                    }
                }

            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0) {
                SDKLog.e("onCharacteristicRead:" + characteristic.getUuid().toString() + " with reslut:" + Utils.bytesToHexString(characteristic.getValue(), " ") + " sessionKey:" + (SDKEventManager.this.sessionKey == null));
                byte[] data = characteristic.getValue();
                if (data[0] == 14) {
                    SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessage(13);
                    return;
                }

                byte[] sk = new byte[16];
                byte[] rands = new byte[8];
                System.arraycopy(data, 1, sk, 0, 16);
                System.arraycopy(data, 1, rands, 0, 8);

                try {
                    if (SDKEventManager.MeshAction == 3) {
                        SDKEventManager.this.sessionKey = SDKEventManager.this.getSessionKey(Utils.stringToBytes(SDKEventManager.this.loginMeshName, 16), Utils.stringToBytes(SDKEventManager.this.loginPassword, 16), SDKEventManager.this.loginRandm, rands, sk);
                    } else {
                        SDKEventManager.this.sessionKey = SDKEventManager.this.getSessionKey(Utils.stringToBytes(SDKEventManager.userMeshName, 16), Utils.stringToBytes(SDKEventManager.userPassword, 16), SDKEventManager.this.loginRandm, rands, sk);
                    }
                } catch (Exception var9) {
                    return;
                }

                if (SDKEventManager.this.sessionKey == null) {
                    SDKLog.e("login mesh device fail");
                    SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessage(13);
                    return;
                }

                SDKLog.e("login mesh device success");
                Message msg = new Message();
                msg.what = SDKEventManager.MeshAction;
                Bundle bundle = new Bundle();
                bundle.putInt("position", 0);
                msg.setData(bundle);
                SDKEventManager.this.resetMeshDeviceHandler.sendMessage(msg);
            }

        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            SDKLog.e("onCharacteristicChanged:" + characteristic.getUuid().toString() + " with reslut:" + Utils.bytesToHexString(characteristic.getValue(), " ") + " sessionKey:" + (SDKEventManager.this.sessionKey == null));
            if (characteristic.getUuid().toString().equals(SDKEventManager.this.notifyGattCharacteristic.getUuid().toString()) && (SDKEventManager.MeshAction == 4 || SDKEventManager.MeshAction == 9 || SDKEventManager.MeshAction == 10)) {
                byte[] data = characteristic.getValue();
                byte[] nonce = SDKEventManager.this.getSecIVS(SDKEventManager.this.currentGroupMac);
                System.arraycopy(data, 0, nonce, 3, 5);
                byte[] result = AES.decrypt(SDKEventManager.this.sessionKey, nonce, data);
                GizWifiDevice deleteSuccessDevice;
                if (SDKEventManager.MeshAction == 4) {
                    SDKLog.e("set groupID callback:" + SDKEventManager.this.addGroupID + " with reslut:" + Utils.bytesToHexString(result, " "));
                    deleteSuccessDevice = SDKEventManager.this.hasChangeGroupSuccess(result);
                    if (deleteSuccessDevice != null) {
                        SDKEventManager.this.addGroupMacSuccessList.add(deleteSuccessDevice);
                    }
                }

                if (SDKEventManager.MeshAction == 9) {
                    SDKLog.e("delete groupID callback:" + SDKEventManager.this.deleteGroupID + " with reslut:" + Utils.bytesToHexString(result, " "));
                    deleteSuccessDevice = SDKEventManager.this.hasDeleteGroupSuccess(result);
                    if (deleteSuccessDevice != null) {
                        SDKEventManager.this.deleteGroupMacSuccessList.add(deleteSuccessDevice);
                    }
                }

                if (SDKEventManager.MeshAction == 10) {
                    SDKLog.e("get groupID callback:" + SDKEventManager.this.deleteGroupID + " with reslut:" + Utils.bytesToHexString(result, " "));
                    List<Integer> groupIds = new ArrayList();

                    for (int i = 10; i < result.length; ++i) {
                        int id = result[i] & 255;
                        if (id != 255) {
                            groupIds.add(result[i] & 255);
                        }
                    }

                    Message msg = new Message();
                    msg.what = 11;
                    msg.obj = groupIds;
                    SDKEventManager.this.resetMeshDeviceHandler.sendMessage(msg);
                }
            }

        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            SDKLog.e("onCharacteristicWrite:" + characteristic.getUuid().toString() + " with reslut:" + Utils.bytesToHexString(characteristic.getValue(), " ") + " sessionKey:" + (SDKEventManager.this.sessionKey == null));
            if (characteristic.getUuid().toString().equals(SDKEventManager.this.loginGattCharacteristic.getUuid().toString())) {
                if (SDKEventManager.this.sessionKey == null || SDKEventManager.this.sessionKey.length == 0) {
                    SDKEventManager.this.mBluetoothGatt.readCharacteristic(SDKEventManager.this.loginGattCharacteristic);
                }
            } else if (characteristic.getUuid().toString().equals(SDKEventManager.this.commandGattCharacteristic.getUuid().toString())) {
                if (SDKEventManager.MeshAction == 3) {
                    SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessage(6);
                } else if (SDKEventManager.MeshAction == 7) {
                    SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessage(8);
                }
            }

        }
    };
    public static SDKEventManager.SyncDeviceHandler syncDeviceHandler;
    public static SDKEventManager.MessageErrorHandler handler;
    private static long user_register_end;
    private static long user_login_end;
    private final Handler nHandler = new Handler(Looper.getMainLooper()) {
        String jsonStr;
        private boolean isStartedDaemon;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                case 3:
                case 4:
                default:
                    break;
                case 2:
                    SDKLog.d("MSG_CONNETED: connect GizWifiSDKDaemon success in child thread");
                    long start_daemon_end = System.currentTimeMillis();
                    SDKLog.d("rundaemon  :" + (start_daemon_end - SDKEventManager.this.start_daemon));
                    Constant.isconnected = true;
                    SDKEventManager.this.makehandshake();
                    SDKEventManager.getInstance().setIsStartedDaemon(true);
                    SDKEventManager.registBroadcasereciver();
                    break;
                case 5:
                    this.jsonStr = (String) msg.obj;

                    try {
                        JSONObject obj = new JSONObject(this.jsonStr);
                        int cmd = Integer.parseInt(obj.getString("cmd"));
                        int sn = -1;
                        if (cmd > 2000) {
                            sn = 0;
                        } else {
                            sn = Integer.parseInt(obj.getString("sn"));
                        }

                        GizWifiSDKListener listener = SDKEventManager.getInstance().getListener();
                        this.didSetListener(cmd, obj, sn, listener);
                    } catch (JSONException var9) {
                        var9.printStackTrace();
                    }
                case 6:
                    int status = msg.getData().getInt("status");
                    String mac = msg.getData().getString("mac");
                    SDKEventManager.this.updateMeshDeviceStatus(mac, GizWifiDeviceNetStatus.valueOf(status));
                    break;
                case 7:
                    List<GizWifiDevice> list = SDKEventManager.this.getDeviceListByProductKeys();
                    SDKEventManager.onDidDiscovered(GizWifiErrorCode.GIZ_SDK_SUCCESS, list);
                    GizMeshLocalControlCenter.sharedInstance().setLocalMeshDeviceList(SDKEventManager.productInfo, list);
            }

        }

        private void didSetListener(int cmd, JSONObject obj, int sn, GizWifiSDKListener listener) throws JSONException {
            String errorCodeJson = "errorCode";
            int errorCodeValue = GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult();
            String errorMessageValue = null;
            String tzCity;
            String openapi;
            int ix;
            String did;
            String macx;
            String didx;
            Iterator var19;
            int outime;
            String type;
            String mac_event_notify;
            String did_event_notify;
            String version;
            JSONArray failedDevices;
            List<ConcurrentHashMap<String, Object>> successmap;
            String site;
            String productKey;
            String errorMessage;
            ConcurrentHashMap itemx;
            String errorMessagex;
            String eventType;
            String eventSource;
            String uid_get_user_info;
            String name_get_user_info;
            String gender_get_user_info;
            int ixxxxxx;
            switch (cmd) {
                case 1002:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        if (errorCodeValue != GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                            SDKEventManager.onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.valueOf(errorCodeValue), GizWifiErrorCode.valueOf(errorCodeValue).name());
                            Constant.ishandshake = false;
                        } else {
                            version = obj.getString("daemonVersion");
                            tzCity = obj.getString("productFilePath");
                            Constant.productFilePath = tzCity;
                            Constant.DaemonVersion = version;
                            Constant.ishandshake = true;
                            SDKEventManager.this.sendMessage2Deamon(Utils.getJsonString());
                            SDKEventManager.onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_START_SUCCESS.getResult()), GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_START_SUCCESS.getResult()).name());
                        }
                    }
                    break;
                case 1004:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        if (errorCodeValue != 0) {
                            SDKEventManager.onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.valueOf(errorCodeValue), GizWifiErrorCode.valueOf(errorCodeValue).name());
                        }
                    }
                case 1006:
                default:
                    break;
                case 1008:
                    if (SDKEventManager.isHandler(sn)) {
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        SDKEventManager.handler.removeMessages(sn);
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                            SDKEventManager.isDisableLan = obj.has("disableLan") ? obj.getBoolean("disableLan") : false;
                        }

                        SDKEventManager.onDidDisableLAN(GizWifiErrorCode.valueOf(errorCodeValue));
                    }
                    break;
                case 1010:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeValue = obj.has("errorCode") ? obj.getInt("errorCode") : -1;
                        if (errorCodeValue == 0) {
                            SDKEventManager.this.onDidSetDeviceOnboarding(GizWifiErrorCode.GIZ_SDK_ONBOARDING_STOPPED, (GizWifiDevice) null);
                            var19 = SDKEventManager.this.onboardingSn.iterator();

                            while (var19.hasNext()) {
                                outime = (Integer) var19.next();
                                SDKEventManager.handler.removeMessages(outime);
                            }

                            SDKEventManager.this.onboardingSn.clear();
                        }
                    }
                    break;
                case 1012:
                    if (SDKEventManager.isHandler(sn)) {
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        if (errorCodeValue != 0) {
                            SDKEventManager.handler.removeMessages(sn);
                            SDKEventManager.this.onDidSetDeviceOnboarding(GizWifiErrorCode.valueOf(errorCodeValue), (GizWifiDevice) null);
                        } else {
                            SDKEventManager.this.cutNet();
                        }
                    }
                    break;
                case 1014:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        List<GizWifiSSID> ssidList = null;
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                            ssidList = new ArrayList();
                            JSONArray ssidlistJson = obj.getJSONArray("ssidlist");

                            for (int ixx = 0; ixx < ssidlistJson.length(); ++ixx) {
                                JSONObject json = ssidlistJson.getJSONObject(ixx);
                                GizWifiSSID ssid = new GizWifiSSID(json.getString("ssid"), Integer.parseInt(json.getString("rssi")));
                                ssidList.add(ssid);
                            }
                        }

                        SDKEventManager.onDidGetSSIDList(GizWifiErrorCode.valueOf(errorCodeValue), ssidList);
                    }
                    break;
                case 1016:
                    SDKLog.d("cmd: " + cmd + "sn: " + sn);
                    if (SDKEventManager.isHandler(sn)) {
                        SDKLog.d("cmd: " + cmd + "sn: " + sn);
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessageValue = "";
                        if (obj.has("errorMessage")) {
                            errorMessageValue = obj.getString("errorMessage");
                        }

                        SDKEventManager.syncDeviceListFromDaemon(obj);
                        List<GizWifiDevice> list = SDKEventManager.this.getDeviceListByProductKeys();
                        GizMeshLocalControlCenter.sharedInstance().setLocalMeshDeviceList(SDKEventManager.productInfo, list);
                        SDKEventManager.onDidDiscovered(GizWifiErrorCode.valueOf(errorCodeValue), list);
                    }
                    break;
                case 1018:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessagex = null;
                        uid_get_user_info = null;
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                            errorMessagex = obj.getString("productKey");
                            uid_get_user_info = obj.getString("productUI");
                            Utils.isExistedProductKeyInDeviceList(errorMessagex, SDKEventManager.this.getDeviceListByProductKeys(), uid_get_user_info, true);
                        }

                        SDKEventManager.onDidUpdateProduct(GizWifiErrorCode.valueOf(errorCodeValue), errorMessagex, uid_get_user_info);
                    }
                    break;
                case 1026:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessagex = null;
                        if (obj.has("did")) {
                            errorMessagex = obj.getString("did");
                        }

                        uid_get_user_info = null;
                        if (obj.has("errorMessage")) {
                            uid_get_user_info = obj.getString("errorMessage");
                        }

                        SDKEventManager.onDidBindDevice(Utils.changeErrorCode(errorCodeValue), uid_get_user_info, errorMessagex);
                    }
                    break;
                case 1028:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessagex = null;
                        if (obj.has("did")) {
                            errorMessagex = obj.getString("did");
                        }

                        uid_get_user_info = null;
                        if (obj.has("errorMessage")) {
                            uid_get_user_info = obj.getString("errorMessage");
                        }

                        SDKEventManager.onDidUnbindDevice(GizWifiErrorCode.valueOf(errorCodeValue), uid_get_user_info, errorMessagex);
                    }
                    break;
                case 1044:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        mac_event_notify = null;
                        if (obj.has("did")) {
                            mac_event_notify = obj.getString("did");
                        }

                        SDKEventManager.onDidBindRemoteDevice(GizWifiErrorCode.valueOf(errorCodeValue), mac_event_notify);
                    }
                    break;
                case 1046:
                    if (SDKEventManager.isHandler(sn)) {
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        SDKEventManager.handler.removeMessages(sn);
                        SDKEventManager.onDidBindRemoteDevice(GizWifiErrorCode.valueOf(errorCodeValue), (String) null);
                    }
                    break;
                case 1052:
                case 1078:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessageValue = "";
                        errorMessagex = null;
                        uid_get_user_info = null;
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                            errorMessagex = obj.getString("uid");
                            uid_get_user_info = obj.getString("token");
                            Constant.uid = errorMessagex;
                            Constant.token = uid_get_user_info;
                            SDKLog.provision(errorMessagex, uid_get_user_info);
                        } else if (obj.has("errorMessage")) {
                            errorMessageValue = obj.getString("errorMessage");
                        }

                        SDKEventManager.onDidUserLogin(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue, errorMessagex, uid_get_user_info);
                    }
                    break;
                case 1054:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessageValue = "";
                        errorMessagex = obj.has("uid") ? obj.getString("uid") : null;
                        uid_get_user_info = obj.has("token") ? obj.getString("token") : null;
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult() && obj.has("errorMessage")) {
                            errorMessageValue = obj.getString("errorMessage");
                        }

                        SDKEventManager.onDidRegisterUser(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue, errorMessagex, uid_get_user_info);
                    } else {
                        SDKLog.d("failed to find sn: " + sn);
                    }
                    break;
                case 1056:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessageValue = "";
                        if (obj.has("errorMessage")) {
                            errorMessageValue = obj.getString("errorMessage");
                        }

                        SDKEventManager.onDidChangeUserPassword(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue);
                    }
                    break;
                case 1058:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessageValue = "";
                        if (obj.has("errorMessage")) {
                            errorMessageValue = obj.getString("errorMessage");
                        }

                        SDKEventManager.onDidChangeUserPassword(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue);
                    }
                    break;
                case 1060:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        if (obj.has("username")) {
                            errorCodeJson = obj.getJSONObject("username").getString("errorCode");
                            errorCodeValue = Integer.parseInt(errorCodeJson);
                            errorMessageValue = "";
                            if (obj.getJSONObject("username").toString().contains("errorMessage")) {
                                errorMessageValue = obj.getJSONObject("username").getString("errorMessage");
                            }

                            SDKEventManager.onDidChangeUserInfo(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue);
                        }

                        if (obj.has("additionalInfo")) {
                            errorCodeJson = obj.getJSONObject("additionalInfo").getString("errorCode");
                            errorCodeValue = Integer.parseInt(errorCodeJson);
                            errorMessageValue = "";
                            if (obj.getJSONObject("additionalInfo").toString().contains("errorMessage")) {
                                errorMessageValue = obj.getString("errorMessage");
                            }

                            SDKEventManager.onDidChangeUserInfo(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue);
                        }
                    }
                    break;
                case 1062:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessageValue = "";
                        if (obj.has("errorMessage")) {
                            errorMessageValue = obj.getString("errorMessage");
                        }

                        SDKEventManager.onDidTransAnonymousUser(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue);
                    }
                    break;
                case 1064:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessageValue = "";
                        errorMessagex = null;
                        uid_get_user_info = null;
                        type = null;
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                            errorMessagex = obj.getString("token");
                            uid_get_user_info = obj.getString("captchaId");
                            type = obj.getString("captchaUrl");
                            if (obj.has("errorMessage")) {
                                errorMessageValue = obj.getString("errorMessage");
                            }
                        }

                        SDKEventManager.onDidGetCaptchaCode(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue, errorMessagex, uid_get_user_info, type);
                    }
                    break;
                case 1066:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessageValue = "";
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult() && obj.has("errorMessage")) {
                            errorMessageValue = obj.getString("errorMessage");
                        }

                        SDKEventManager.onDidRequestSendPhoneSMSCode(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue, (String) null);
                    }
                    break;
                case 1068:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessageValue = "";
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult() && obj.has("errorMessage")) {
                            errorMessageValue = obj.getString("errorMessage");
                        }

                        SDKEventManager.onDidVerifyPhoneSMSCode(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue);
                    }
                    break;
                case 1070:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessageValue = "";
                        if (obj.has("errorMessage")) {
                            errorMessageValue = obj.getString("errorMessage");
                        }

                        GizUserInfo userInfo = null;
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                            userInfo = new GizUserInfo();
                            uid_get_user_info = obj.getString("uid");
                            userInfo.setUid(uid_get_user_info);
                            type = obj.getString("username");
                            userInfo.setUsername(type);
                            eventType = obj.getString("email");
                            userInfo.setEmail(eventType);
                            eventSource = obj.getString("phone");
                            userInfo.setPhone(eventSource);
                            name_get_user_info = obj.getString("name");
                            userInfo.setName(name_get_user_info);
                            gender_get_user_info = obj.getString("gender");
                            if (gender_get_user_info.contains("M")) {
                                userInfo.setUserGender(GizUserGenderType.GizUserGenderMale);
                                userInfo.setGender(XPGUserGenderType.Male);
                            } else if (gender_get_user_info.contains("F")) {
                                userInfo.setUserGender(GizUserGenderType.GizUserGenderFemale);
                                userInfo.setGender(XPGUserGenderType.Female);
                            } else {
                                userInfo.setUserGender(GizUserGenderType.GizUserGenderUnknown);
                                userInfo.setGender(XPGUserGenderType.Unknown);
                            }

                            mac_event_notify = obj.getString("birthday");
                            userInfo.setBirthday(mac_event_notify);
                            did_event_notify = obj.getString("address");
                            userInfo.setAddress(did_event_notify);
                            String lang_get_user_info = obj.getString("lang");
                            userInfo.setLang(lang_get_user_info);
                            String remark_get_user_info = obj.getString("remark");
                            userInfo.setRemark(remark_get_user_info);
                            boolean is_anonymous = obj.getBoolean("is_anonymous");
                            userInfo.setAnonymous(is_anonymous);
                        }

                        SDKEventManager.onDidGetUserInfo(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue, userInfo);
                    }
                    break;
                case 1072:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        errorMessageValue = "";
                        did = null;
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                            did = obj.getString("token");
                            if (obj.has("errorMessage")) {
                                errorMessageValue = obj.getString("errorMessage");
                            }
                        }

                        SDKEventManager.onDidRequestSendPhoneSMSCode(GizWifiErrorCode.valueOf(errorCodeValue), errorMessageValue, did);
                    }
                    break;
                case 1074:
                    if (SDKEventManager.isHandler(sn)) {
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        SDKEventManager.handler.removeMessages(sn);
                        SDKEventManager.onDidDidChannelIDBind(GizWifiErrorCode.valueOf(errorCodeValue));
                    }
                    break;
                case 1076:
                    if (SDKEventManager.isHandler(sn)) {
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        SDKEventManager.handler.removeMessages(sn);
                        SDKEventManager.onDidDidChannelIDUnBind(GizWifiErrorCode.valueOf(errorCodeValue));
                    }
                    break;
                case 1080:
                    if (SDKEventManager.isHandler(sn)) {
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        SDKEventManager.handler.removeMessages(sn);
                        SDKEventManager.onDidUserLogout(GizWifiErrorCode.valueOf(errorCodeValue));
                    }
                    break;
                case 1102:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        itemx = null;
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                            uid_get_user_info = obj.getString("openAPIDomain");
                            type = obj.getString("openAPIPort");
                            eventType = obj.getString("siteDomain");
                            eventSource = obj.getString("sitePort");
                            SDKEventManager.domainInfo.put("openapi", uid_get_user_info + ":" + type);
                            SDKEventManager.domainInfo.put("site", eventType + ":" + eventSource);
                            SDKLog.init(SDKEventManager.mContext, SDKEventManager.domainInfo, SDKEventManager.appId, SDKEventManager.this.getVersion(), GizLogPrintLevel.GizLogPrintAll);
                            itemx = new ConcurrentHashMap();
                            itemx.put("openAPIDomain", uid_get_user_info);
                            itemx.put("openAPIPort", type);
                            itemx.put("siteDomain", eventType);
                            itemx.put("sitePort", eventSource);
                        }

                        SDKEventManager.onDidGetCurrentCloudService(GizWifiErrorCode.valueOf(errorCodeValue), itemx);
                    }
                    break;
                case 1104:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        itemx = null;
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                            uid_get_user_info = obj.getString("openAPIDomain");
                            type = obj.getString("openAPIPort");
                            eventType = obj.getString("siteDomain");
                            eventSource = obj.getString("sitePort");
                            name_get_user_info = obj.getString("pushDomain");
                            gender_get_user_info = obj.getString("pushPort");
                            SDKEventManager.domainInfo.put("openapi", uid_get_user_info);
                            SDKEventManager.domainInfo.put("site", eventType);
                            SDKEventManager.domainInfo.put("push", name_get_user_info);
                            itemx = new ConcurrentHashMap();
                            itemx.put("openAPIDomain", uid_get_user_info);
                            itemx.put("openAPIPort", type);
                            itemx.put("siteDomain", eventType);
                            itemx.put("sitePort", eventSource);
                            itemx.put("pushDomain", name_get_user_info);
                            itemx.put("pushPort", gender_get_user_info);
                        }

                        SDKEventManager.onDidGetCurrentCloudService(GizWifiErrorCode.valueOf(errorCodeValue), itemx);
                    }
                    break;
                case 1106:
                    errorCodeJson = obj.getString("errorCode");
                    errorCodeValue = Integer.parseInt(errorCodeJson);
                    if (errorCodeValue == 0) {
                        tzCity = obj.getString("tzCity");
                        openapi = obj.getString("openapi");
                        site = obj.getString("site");
                        String push = obj.getString("push");
                        if (SDKEventManager.cityJson != null && SDKEventManager.cityJson.has(tzCity)) {
                            JSONObject cityJsonObj = (JSONObject) SDKEventManager.cityJson.get(tzCity);
                            productKey = cityJsonObj.getString("push");
                            did = cityJsonObj.getString("api");
                            macx = cityJsonObj.getString("site");
                            if (!productKey.equals(push) || !did.equals(openapi) || !macx.equals(site)) {
                                JSONObject writeObj = new JSONObject();
                                writeObj.put("push", push);
                                writeObj.put("api", openapi);
                                writeObj.put("site", site);
                                SDKEventManager.cityJson.put(tzCity, writeObj);
                            }
                        }
                    }
                    break;
                case 1108:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        List<ConcurrentHashMap<String, String>> devices = new ArrayList();
                        if (obj.has("devices")) {
                            JSONArray jsonArray = obj.getJSONArray("devices");

                            for (ixxxxxx = 0; ixxxxxx < jsonArray.length(); ++ixxxxxx) {
                                JSONObject deviceToServer = (JSONObject) jsonArray.get(ixxxxxx);
                                ConcurrentHashMap<String, String> map = new ConcurrentHashMap();
                                String domain;
                                if (deviceToServer.has("mac")) {
                                    domain = deviceToServer.getString("mac");
                                    map.put("mac", domain);
                                }

                                if (deviceToServer.has("productKey")) {
                                    domain = deviceToServer.getString("productKey");
                                    map.put("productKey", domain);
                                }

                                if (deviceToServer.has("domain")) {
                                    domain = deviceToServer.getString("domain");
                                    map.put("domain", domain);
                                }

                                devices.add(map);
                            }
                        }

                        SDKEventManager.OnDidGetDevicesToSetServerInfo(GizWifiErrorCode.valueOf(errorCodeValue), devices);
                    }
                    break;
                case 1110:
                    if (SDKEventManager.isHandler(sn)) {
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        if (errorCodeValue != 0) {
                            SDKEventManager.handler.removeMessages(sn);
                            if (SDKEventManager.this.snlist.contains(sn)) {
                                int removenum = -1;

                                for (int ixxxxx = 0; ixxxxx < SDKEventManager.this.snlist.size(); ++ixxxxx) {
                                    Integer num = (Integer) SDKEventManager.this.snlist.get(ixxxxx);
                                    if (num == sn) {
                                        removenum = ixxxxx;
                                    }
                                }

                                if (removenum != -1) {
                                    SDKEventManager.this.snlist.remove(removenum);
                                }
                            }

                            mac_event_notify = SDKEventManager.this.getMac(sn);
                            SDKEventManager.this.OnDidSetDeviceServerInfo(GizWifiErrorCode.valueOf(errorCodeValue), mac_event_notify);
                        }
                    }
                    break;
                case 1122:
                    SDKLog.d("cmd: " + cmd + "sn: " + sn);
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        failedDevices = obj.has("successDevices") ? obj.getJSONArray("successDevices") : null;
                        JSONArray failedDevicesx = obj.has("failedDevices") ? obj.getJSONArray("failedDevices") : null;
                        successmap = null;
                        List<ConcurrentHashMap<String, Object>> failedmap = null;
                        List<ConcurrentHashMap<String, Object>> success = new ArrayList();
                        List<ConcurrentHashMap<String, Object>> failed = new ArrayList();
                        int i;
                        JSONObject failedObj;
                        if (failedDevices != null) {
                            for (i = 0; i < failedDevices.length(); ++i) {
                                failedObj = (JSONObject) failedDevices.get(i);
                                macx = failedObj.has("mac") ? failedObj.getString("mac") : null;
                                errorMessage = failedObj.has("productKey") ? failedObj.getString("productKey") : null;
                                didx = failedObj.has("did") ? failedObj.getString("did") : null;
                                itemx = new ConcurrentHashMap();
                                if (macx != null) {
                                    itemx.put("mac", macx);
                                }

                                if (errorMessage != null) {
                                    itemx.put("productKey", errorMessage);
                                }

                                if (didx != null) {
                                    itemx.put("did", didx);
                                }

                                success.add(itemx);
                            }
                        }

                        if (success.size() > 0) {
                            successmap = success;
                        }

                        if (failedDevicesx != null) {
                            for (i = 0; i < failedDevicesx.length(); ++i) {
                                failedObj = (JSONObject) failedDevicesx.get(i);
                                macx = failedObj.has("mac") ? failedObj.getString("mac") : null;
                                errorMessage = failedObj.has("productKey") ? failedObj.getString("productKey") : null;
                                int errorCodex = failedObj.has("errorCode") ? failedObj.getInt("errorCode") : 0;
                                errorMessagex = failedObj.has("errorMessage") ? failedObj.getString("errorMessage") : null;
                                ConcurrentHashMap<String, Object> itemxx = new ConcurrentHashMap();
                                if (macx != null) {
                                    itemxx.put("mac", macx);
                                }

                                if (errorMessage != null) {
                                    itemxx.put("productKey", errorMessage);
                                }

                                itemxx.put("errorCode", errorCodex);
                                if (errorMessagex != null) {
                                    itemxx.put("errorMessage", errorMessagex);
                                }

                                failed.add(itemxx);
                            }
                        }

                        if (failed.size() > 0) {
                            failedmap = failed;
                        }

                        SDKEventManager.onDidDeviceSafetyRegister(successmap, failedmap);
                    }
                    break;
                case 1124:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKEventManager.handler.removeMessages(sn);
                        failedDevices = obj.has("failedDevices") ? obj.getJSONArray("failedDevices") : null;
                        List<ConcurrentHashMap<String, Object>> failedx = new ArrayList();
                        List<ConcurrentHashMap<String, Object>> failedmap = null;
                        successmap = null;
                        if (failedDevices != null) {
                            for (ix = 0; ix < failedDevices.length(); ++ix) {
                                JSONObject failedObjx = (JSONObject) failedDevices.get(ix);
                                String mac = failedObjx.has("mac") ? failedObjx.getString("mac") : null;
                                productKey = failedObjx.has("productKey") ? failedObjx.getString("productKey") : null;
                                did = failedObjx.has("did") ? failedObjx.getString("mac") : null;
                                int errorCode = failedObjx.has("errorCode") ? failedObjx.getInt("errorCode") : 0;
                                errorMessage = failedObjx.has("errorMessage") ? failedObjx.getString("errorMessage") : null;
                                ConcurrentHashMap<String, Object> item = new ConcurrentHashMap();
                                if (mac != null) {
                                    item.put("mac", mac);
                                }

                                if (productKey != null) {
                                    item.put("productKey", productKey);
                                }

                                if (did != null) {
                                    item.put("did", did);
                                }

                                item.put("errorCode", errorCode);
                                if (errorMessage != null) {
                                    item.put("errorMessage", errorMessage);
                                }

                                failedx.add(item);
                            }
                        }

                        if (failedx.size() > 0) {
                            failedmap = failedx;
                        }

                        SDKEventManager.onDidDeviceSafetyUnbind(failedmap);
                    }
                    break;
                case 1392:
                    errorCodeJson = obj.getString("errorCode");
                    errorCodeValue = Integer.parseInt(errorCodeJson);
                    if (GizWifiErrorCode.valueOf(errorCodeValue) != GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                        SDKEventManager.onDidReceiveDeviceLog(GizWifiErrorCode.valueOf(errorCodeValue), (String) null, 0, 0, (String) null);
                    }
                    break;
                case 1396:
                    if (SDKEventManager.isHandler(sn)) {
                        SDKLog.e("js:" + obj.getString("productKey"));
                        errorCodeJson = obj.getString("errorCode");
                        errorCodeValue = Integer.parseInt(errorCodeJson);
                        SDKEventManager.handler.removeMessages(sn);
                        if (errorCodeValue == GizWifiErrorCode.GIZ_SDK_SUCCESS.getResult()) {
                            tzCity = obj.getString("productKey");
                            openapi = obj.getString("javaScriptFile");
                            site = obj.getString("productFile");
                            GizJsProtocol.sharedInstance().setProtocolJsAndDataPoints(SDKEventManager.mContext, tzCity, openapi, site);
                        }
                    }
                    break;
                case 1398:
                    errorCodeJson = obj.getString("errorCode");
                    errorCodeValue = Integer.parseInt(errorCodeJson);
                    SDKEventManager.onDidGetMapTab(GizWifiErrorCode.valueOf(errorCodeValue));
                    break;
                case 2001:
                    SDKEventManager.this.syncDevice(obj);
                    break;
                case 2004:
                    type = obj.getString("productKey");
                    if (SDKLog.isSDCard) {
                        eventType = obj.getString("productFilePath");
                        eventSource = Utils.readFileContentStr(eventType);
                        if (!TextUtils.isEmpty(eventSource)) {
                            JSONObject ui = new JSONObject(eventSource);
                            if (ui.has("ui")) {
                                eventSource = ui.getString("ui");
                            } else {
                                eventSource = "";
                            }
                        }

                        Utils.isExistedProductKeyInDeviceList(type, SDKEventManager.this.getDeviceListByProductKeys(), eventSource, true);
                    } else {
                        List<GizWifiDevice> deviceListByProductKeys = SDKEventManager.this.getDeviceListByProductKeys();
                        Iterator var72 = deviceListByProductKeys.iterator();

                        GizWifiDevice gizWifiDevice2;
                        do {
                            if (!var72.hasNext()) {
                                return;
                            }

                            gizWifiDevice2 = (GizWifiDevice) var72.next();
                        } while (!gizWifiDevice2.getProductKey().equals(type) || TextUtils.isEmpty(gizWifiDevice2.getProductUI()));

                        gizWifiDevice2.updateProduct(gizWifiDevice2.getProductKey());
                        gizWifiDevice2.updateProduct(gizWifiDevice2.getProductKeyAdapter());
                    }
                    break;
                case 2005:
                    did = obj.getString("mac");
                    macx = obj.getString("did");
                    boolean issubScribed = obj.getBoolean("subScribed");
                    didx = obj.getString("productKey");
                    var19 = SDKEventManager.this.getDeviceListByProductKeys().iterator();

                    while (var19.hasNext()) {
                        GizWifiDevice gizWifiDevice = (GizWifiDevice) var19.next();
                        if (gizWifiDevice.getMacAddress().equals(did) && gizWifiDevice.getDid().equals(macx) && gizWifiDevice.getProductKey().equals(didx)) {
                            gizWifiDevice.setSubscribed(issubScribed);
                        }
                    }

                    return;
                case 2008:
                    eventType = obj.getString("eventType");
                    eventSource = obj.getString("eventSource");
                    int eventID = obj.getInt("eventID");
                    GizEventType typex = null;
                    if (eventType.equalsIgnoreCase("SDK")) {
                        typex = GizEventType.GizEventSDK;
                        if (eventID == GizWifiErrorCode.GIZ_SDK_UDP_PORT_BIND_FAILED.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_DAEMON_EXCEPTION.getResult()) {
                            SDKEventManager.onDidNotifyEvent(typex, (Object) null, GizWifiErrorCode.valueOf(eventID), GizWifiErrorCode.valueOf(eventID).name());
                        }
                    } else if (eventType.equalsIgnoreCase("device")) {
                        typex = GizEventType.GizEventDevice;
                        mac_event_notify = "";
                        did_event_notify = "";
                        String[] deviceInfos = eventSource.split(Pattern.quote("+"));

                        for (int ixxx = 0; ixxx < deviceInfos.length; ++ixxx) {
                            if (ixxx == 0) {
                                mac_event_notify = deviceInfos[ixxx];
                            }

                            if (ixxx == 1) {
                                did_event_notify = deviceInfos[ixxx];
                            }
                        }

                        GizWifiDevice device_event_notify = null;
                        List<GizWifiDevice> deviceListByProductKeysx = SDKEventManager.this.getDeviceListByProductKeys();

                        for (int ixxxx = 0; ixxxx < deviceListByProductKeysx.size(); ++ixxxx) {
                            GizWifiDevice mDeviceToFind = (GizWifiDevice) deviceListByProductKeysx.get(ixxxx);
                            if (mDeviceToFind.getMacAddress().equalsIgnoreCase(mac_event_notify) && mDeviceToFind.getDid().equals(did_event_notify)) {
                                device_event_notify = mDeviceToFind;
                                break;
                            }
                        }

                        if (device_event_notify != null && device_event_notify.isLAN() && eventID == GizWifiErrorCode.GIZ_SDK_CONNECTION_CLOSED.getResult()) {
                            SDKEventManager.onDidNotifyEvent(typex, device_event_notify, GizWifiErrorCode.valueOf(eventID), GizWifiErrorCode.valueOf(eventID).name());
                            device_event_notify.onDidDisconnected(device_event_notify);
                        }

                        if (device_event_notify != null && device_event_notify.isLAN() && device_event_notify.getLoginning() && (eventID == GizWifiErrorCode.GIZ_SDK_SET_SOCKET_NON_BLOCK_FAILED.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_CONNECTION_TIMEOUT.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_CONNECTION_REFUSED.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_CONNECTION_ERROR.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_DEVICE_LOGIN_VERIFY_FAILED.getResult())) {
                            device_event_notify.setLoginning(false);
                            device_event_notify.onDidLogin(device_event_notify, Utils.changeErrorCode(eventID));
                        }
                    } else if (eventType.equalsIgnoreCase("m2m")) {
                        typex = GizEventType.GizEventM2MService;
                        mac_event_notify = null;
                        List<GizWifiDevice> deviceListByProductKeysxx = SDKEventManager.this.getDeviceListByProductKeys();

                        for (ixxxxxx = 0; ixxxxxx < deviceListByProductKeysxx.size(); ++ixxxxxx) {
                            GizWifiDevice device_m2m = (GizWifiDevice) deviceListByProductKeysxx.get(ixxxxxx);
                            if (!device_m2m.isLAN() && device_m2m.isConnected() && (eventID == GizWifiErrorCode.GIZ_SDK_DNS_FAILED.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_SET_SOCKET_NON_BLOCK_FAILED.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_CONNECTION_TIMEOUT.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_CONNECTION_REFUSED.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_CONNECTION_ERROR.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_CONNECTION_CLOSED.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_SSL_HANDSHAKE_FAILED.getResult())) {
                                device_m2m.onDidDisconnected(device_m2m);
                            }

                            if (!device_m2m.isLAN() && device_m2m.getLoginning() && device_m2m.getIPAddress().equalsIgnoreCase(eventSource) && (eventID == GizWifiErrorCode.GIZ_SDK_DNS_FAILED.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_SET_SOCKET_NON_BLOCK_FAILED.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_CONNECTION_TIMEOUT.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_CONNECTION_REFUSED.getResult() || eventID == GizWifiErrorCode.GIZ_SDK_CONNECTION_ERROR.getResult())) {
                                device_m2m.setLoginning(false);
                                device_m2m.onDidLogin(device_m2m, Utils.changeErrorCode(eventID));
                            }
                        }

                        return;
                    } else if (!eventType.equalsIgnoreCase("openapi") && !eventType.equalsIgnoreCase("configServer") && eventType.equalsIgnoreCase("token")) {
                        typex = GizEventType.GizEventToken;
                        SDKEventManager.onDidNotifyEvent(typex, eventSource, GizWifiErrorCode.valueOf(eventID), GizWifiErrorCode.valueOf(eventID).name());
                    }
                    break;
                case 2010:
                    if (SDKEventManager.configureMode != GizWifiConfigureMode.GizWifiAirLinkMulti && SDKEventManager.configureMode != GizWifiConfigureMode.GizWifiBleLinkMulti) {
                        if (SDKEventManager.this.onboardingSn.size() > 0) {
                            var19 = SDKEventManager.this.onboardingSn.iterator();

                            while (var19.hasNext()) {
                                outime = (Integer) var19.next();
                                SDKEventManager.handler.removeMessages(outime);
                            }
                        }

                        SDKEventManager.this.onboardingSn.clear();
                    }

                    boolean hasDevice = false;
                    GizWifiDevice existedDevice = null;
                    synchronized (SDKEventManager.deviceList) {
                        Iterator var22 = SDKEventManager.deviceList.iterator();

                        while (var22.hasNext()) {
                            GizWifiDevice mDevice = (GizWifiDevice) var22.next();
                            if (mDevice.isEqualToJsonObj(obj)) {
                                existedDevice = mDevice;
                                hasDevice = true;
                                break;
                            }
                        }
                    }

                    if (!hasDevice) {
                        type = "normal";
                        if (obj.has("type")) {
                            type = obj.getString("type");
                        }

                        if (type.equalsIgnoreCase("centralControl")) {
                            existedDevice = new GizWifiCentralControlDevice();
                        } else {
                            existedDevice = new GizWifiDevice();
                        }

                        ((GizWifiDevice) existedDevice).syncDeviceInfoFromJson(obj);
                        ((GizWifiDevice) existedDevice).setOldIsOnline(((GizWifiDevice) existedDevice).isOnline());
                        ((GizWifiDevice) existedDevice).setOldIsConnected(((GizWifiDevice) existedDevice).isConnected());
                        if (!SDKEventManager.isDisableLan) {
                            SDKEventManager.deviceList.add(existedDevice);
                        }
                    } else if (!SDKEventManager.isDisableLan) {
                        ((GizWifiDevice) existedDevice).syncDeviceInfoFromJson(obj);
                    }

                    SDKEventManager.this.onDidSetDeviceOnboarding(GizWifiErrorCode.GIZ_SDK_SUCCESS, (GizWifiDevice) existedDevice);
                    break;
                case 2011:
                    if (SDKEventManager.this.snlist != null && SDKEventManager.this.snlist.size() > 0) {
                        Iterator var8 = SDKEventManager.this.snlist.iterator();

                        while (var8.hasNext()) {
                            int mysn = (Integer) var8.next();
                            SDKEventManager.handler.removeMessages(mysn);
                        }

                        SDKEventManager.this.snlist.clear();
                    }

                    errorCodeValue = obj.getInt("errorCode");
                    version = obj.getString("mac");
                    SDKEventManager.this.OnDidSetDeviceServerInfo(GizWifiErrorCode.valueOf(errorCodeValue), version);
                    break;
                case 2012:
                    boolean syslog = obj.has("upload_syslog") ? obj.getBoolean("upload_syslog") : false;
                    boolean bizlog = obj.has("upload_bizlog") ? obj.getBoolean("upload_bizlog") : false;
                    SDKLog.setUploadLogSwitch(syslog, bizlog);
                    break;
                case 2013:
                    errorCodeJson = obj.getString("errorCode");
                    errorCodeValue = Integer.parseInt(errorCodeJson);
                    tzCity = obj.optString("mac");
                    openapi = obj.optString("log");
                    ix = obj.optInt("logSN");
                    int timeStemp = obj.optInt("timestamp");
                    SDKEventManager.onDidReceiveDeviceLog(GizWifiErrorCode.valueOf(errorCodeValue), tzCity, timeStemp, ix, openapi);
            }

        }
    };
    private SDKEventManager.BaiDuHandler pingHandler;
    private long start_daemon;
    private long ps_start;
    private List<Integer> snlist;
    private List<ConcurrentHashMap<Integer, String>> macandsnlist = new ArrayList();
    private static String remotetimestart;
    private static String unbindtime;
    private static String getboundtime;
    private static String getmeshtime;
    private static String onboardingstarttime;

    SDKEventManager() {
    }

    public void setRN(boolean isRN) {
        this.isRN = isRN;
    }

    protected static String listMasking(List<GizWifiDevice> list) {
        String masking = "{size= " + (list == null ? "0" : list.size()) + ", ";
        if (list != null) {
            for (Iterator var2 = list.iterator(); var2.hasNext(); masking = masking + ", ") {
                GizWifiDevice object = (GizWifiDevice) var2.next();
                masking = masking + "[" + object.infoMasking() + "]";
            }
        }

        return masking.substring(0, masking.length() - 2) + "}";
    }

    protected static SDKEventManager getInstance() {
        return mInstance;
    }

    private static boolean isHandler(int sn) {
        boolean hasMessages = handler.hasMessages(sn);
        return hasMessages;
    }

    protected GizWifiSDKListener getListener() {
        return mListener;
    }

    protected void setListener(GizWifiSDKListener listener) {
        mListener = listener;
    }

    protected Context getContext() {
        return mContext;
    }

    protected static List<ConcurrentHashMap<String, Object>> getProductInfo() {
        return productInfo;
    }

    protected boolean getIsStartedDaemon() {
        return isStartDaemon.get();
    }

    protected void setIsStartedDaemon(boolean StartDaemon) {
        isStartDaemon.set(StartDaemon);
    }

    protected void setDeviceList(List<GizWifiDevice> deviceList) {
        SDKEventManager.deviceList = deviceList;
    }

    protected List<GizWifiDevice> getTotalDeviceList() {
        return deviceList;
    }

    protected GizWifiDevice findDeviceByProductKeyInTotalDeviceList(String productKey) {
        GizWifiDevice device = null;
        synchronized (deviceList) {
            Iterator var4 = deviceList.iterator();

            while (var4.hasNext()) {
                GizWifiDevice object = (GizWifiDevice) var4.next();
                if (object.getProductKey().equals(productKey)) {
                    device = object;
                    break;
                }
            }

            return device;
        }
    }

    protected GizWifiDevice findDeviceInTotalDeviceList(String did, String mac, String productKey) {
        GizWifiDevice device = null;
        synchronized (deviceList) {
            Iterator var6 = deviceList.iterator();

            while (var6.hasNext()) {
                GizWifiDevice object = (GizWifiDevice) var6.next();
                if (object.getMacAddress().equals(mac) && object.getDid().equals(did) && object.getProductKey().equals(productKey)) {
                    device = object;
                    break;
                }
            }

            return device;
        }
    }

    protected GizWifiDevice findDeviceInDeviceList(String mac, String did, String productKey, List<GizWifiDevice> deviceList) {
        GizWifiDevice device = null;
        synchronized (deviceList) {
            Iterator var7 = deviceList.iterator();

            while (var7.hasNext()) {
                GizWifiDevice object = (GizWifiDevice) var7.next();
                if (object.getMacAddress().equals(mac) && object.getDid().equals(did) && object.getProductKey().equals(productKey)) {
                    device = object;
                    break;
                }
            }

            return device;
        }
    }

    protected List<GizWifiDevice> getDeviceList() {
        List<GizWifiDevice> list = this.getDeviceListByProductKeys();
        return list;
    }

    protected GizWifiDevice getDeviceInDeviceListByProductKeys(String mac, String productKey, String did) {
        List<GizWifiDevice> deviceListByProductKeys = this.getDeviceListByProductKeys();
        Iterator var5 = deviceListByProductKeys.iterator();

        GizWifiDevice gizWifiDevice;
        do {
            if (!var5.hasNext()) {
                return null;
            }

            gizWifiDevice = (GizWifiDevice) var5.next();
        } while (!gizWifiDevice.getDid().equals(did) || !gizWifiDevice.getMacAddress().equals(mac) || !gizWifiDevice.getProductKey().equals(productKey));

        return gizWifiDevice;
    }

    protected List<GizWifiDevice> getDeviceListByLAN() {
        List<GizWifiDevice> deviceListByLAN = new ArrayList();
        List<GizWifiDevice> deviceListByProductKeys = this.getDeviceListByProductKeys();
        Iterator var3 = deviceListByProductKeys.iterator();

        while (var3.hasNext()) {
            GizWifiDevice mDevice = (GizWifiDevice) var3.next();
            if (mDevice.isLAN()) {
                deviceListByLAN.add(mDevice);
            }
        }

        return deviceListByLAN;
    }

    protected List<GizWifiDevice> getDeviceListByDisable() {
        List<GizWifiDevice> deviceListByProductKeys = this.getDeviceListByProductKeys();
        List<GizWifiDevice> deviceListByDisable = new ArrayList();
        Iterator var3 = deviceListByProductKeys.iterator();

        while (var3.hasNext()) {
            GizWifiDevice gizWifiDevice = (GizWifiDevice) var3.next();
            if (!gizWifiDevice.isLAN) {
                deviceListByDisable.add(gizWifiDevice);
            }
        }

        return deviceListByDisable;
    }

    protected List<GizWifiDevice> getDeviceListByProductKeys() {
        List<GizWifiDevice> deviceListByProductKeys = new ArrayList();
        List<String> productKeys = null;
        if (productInfo == null) {
            productKeys = specialProductKeys;
        } else {
            productKeys = new ArrayList();
            Iterator var3 = productInfo.iterator();

            while (var3.hasNext()) {
                ConcurrentHashMap<String, Object> info = (ConcurrentHashMap) var3.next();
                String pk = (String) info.get("productKey");
                ((List) productKeys).add(pk);
            }
        }

        this.getJSProtocolByDeviceList();
        synchronized (deviceList) {
            Iterator var15;
            GizWifiDevice gizWifiDevice;
            if (((List) productKeys).size() != 0) {
                var15 = deviceList.iterator();

                while (var15.hasNext()) {
                    gizWifiDevice = (GizWifiDevice) var15.next();
                    Iterator var17 = ((List) productKeys).iterator();

                    while (var17.hasNext()) {
                        String productKey = (String) var17.next();
                        if (gizWifiDevice.getNetStatus() != GizWifiDeviceNetStatus.GizDeviceUnavailable && gizWifiDevice.getProductKey().equals(productKey)) {
                            deviceListByProductKeys.add(gizWifiDevice);
                        }
                    }
                }

                Collections.sort(deviceListByProductKeys, new Comparator<GizWifiDevice>() {
                    public int compare(GizWifiDevice arg0, GizWifiDevice arg1) {
                        int ordinal = arg0.getNetStatus().ordinal();
                        int ordinal1 = arg1.getNetStatus().ordinal();
                        return ordinal1 - ordinal;
                    }
                });
                return deviceListByProductKeys;
            } else {
                var15 = deviceList.iterator();

                while (var15.hasNext()) {
                    gizWifiDevice = (GizWifiDevice) var15.next();
                    if (gizWifiDevice.getNetStatus() != GizWifiDeviceNetStatus.GizDeviceUnavailable) {
                        deviceListByProductKeys.add(gizWifiDevice);
                    }
                }

                Collections.sort(deviceListByProductKeys, new Comparator<GizWifiDevice>() {
                    public int compare(GizWifiDevice arg0, GizWifiDevice arg1) {
                        int ordinal = arg0.getNetStatus().ordinal();
                        int ordinal1 = arg1.getNetStatus().ordinal();
                        return ordinal1 - ordinal;
                    }
                });
                var15 = deviceListByProductKeys.iterator();

                while (var15.hasNext()) {
                    gizWifiDevice = (GizWifiDevice) var15.next();

                    try {
                        if (TextUtils.isEmpty(gizWifiDevice.getProductUI())) {
                            String api = (String) domainInfo.get("openapi");
                            api = api.contains(":") ? api.split(":")[0] : api;
                            File profile = new File(Constant.productFilePath, api);
                            File file = new File(profile, gizWifiDevice.getProductKey() + ".json");
                            String productUI = Utils.readFileContentStr(file.toString());
                            if (!TextUtils.isEmpty(productUI)) {
                                JSONObject ui = new JSONObject(productUI);
                                if (ui.has("ui")) {
                                    String myui = ui.getString("ui");
                                    gizWifiDevice.setProductUI(myui);
                                } else {
                                    gizWifiDevice.setProductUI("");
                                }
                            }
                        }
                    } catch (JSONException var13) {
                        var13.printStackTrace();
                    }
                }

                return deviceListByProductKeys;
            }
        }
    }

    public void getJSProtocolByDeviceList() {
        if (productInfo != null) {
            for (int i = 0; i < productInfo.size(); ++i) {
                ConcurrentHashMap<String, Object> info = (ConcurrentHashMap) productInfo.get(i);
                if (info.get("localMeshType") != null && info.get("localMeshType").equals(GizLocalMeshType.GizLocalMeshSub.ordinal()) || info.get("adapterType") != null && info.get("adapterType").equals(GizAdapterType.GizAdapterDataPointScript.ordinal())) {
                    String pk = (String) info.get("productKey");
                    if (!GizJsProtocol.sharedInstance().isHasProtocolJs(pk)) {
                        this.getJSProtocol(pk);
                    }
                }
            }

        }
    }

    protected static void syncDeviceListFromDaemon(JSONObject obj) throws JSONException {
        Iterator var1 = deviceList.iterator();

        while (var1.hasNext()) {
            GizWifiDevice mDevice = (GizWifiDevice) var1.next();
            mDevice.setNetStatus(GizWifiDeviceNetStatus.GizDeviceUnavailable);
        }

        JSONArray devicesFromJson = obj.getJSONArray("devices");

        for (int i = 0; i < devicesFromJson.length(); ++i) {
            JSONObject jsonObj = devicesFromJson.getJSONObject(i);
            GizWifiDevice existedDevice = null;
            Iterator var5 = deviceList.iterator();

            while (var5.hasNext()) {
                GizWifiDevice mDevice = (GizWifiDevice) var5.next();
                if (mDevice.isEqualToJsonObj(jsonObj)) {
                    existedDevice = mDevice;
                    break;
                }
            }

            if (existedDevice != null) {
                existedDevice.syncDeviceInfoFromJson(jsonObj);
                if (existedDevice instanceof GizLiteGWSubDevice) {
                    GizLiteGWSubDevice btDevice = (GizLiteGWSubDevice) existedDevice;
                    btDevice.syncBtInfoFromJson(jsonObj);
                } else {
                    SDKLog.d("the device is not bt or ble");
                }
            } else {
                String type = jsonObj.has("type") ? jsonObj.getString("type") : "normal";
                int netType = jsonObj.has("netType") ? jsonObj.getInt("netType") : 0;
                if (2 != netType && 3 != netType) {
                    if (type.equalsIgnoreCase("centralControl")) {
                        existedDevice = new GizWifiCentralControlDevice();
                    } else {
                        existedDevice = new GizWifiDevice();
                    }
                } else {
                    GizLiteGWSubDevice btDevice = new GizLiteGWSubDevice();
                    btDevice.syncBtInfoFromJson(jsonObj);
                    existedDevice = btDevice;
                }

                ((GizWifiDevice) existedDevice).syncDeviceInfoFromJson(jsonObj);
                if (productInfo != null) {
                    for (int j = 0; j < productInfo.size(); ++j) {
                        ConcurrentHashMap<String, Object> info = (ConcurrentHashMap) productInfo.get(j);
                        if (info.get("localMeshType") != null && info.get("localMeshType").equals(GizLocalMeshType.GizLocalMeshSub.ordinal()) || info.get("adapterType") != null && info.get("adapterType").equals(GizAdapterType.GizAdapterDataPointScript.ordinal())) {
                            String pk = (String) info.get("productKey");
                            if (pk.equals(((GizWifiDevice) existedDevice).getProductKey())) {
                                ((GizWifiDevice) existedDevice).setJsProtocol(true);
                            }
                        }
                    }
                }

                ((GizWifiDevice) existedDevice).setOldIsOnline(((GizWifiDevice) existedDevice).isOnline());
                ((GizWifiDevice) existedDevice).setOldIsConnected(((GizWifiDevice) existedDevice).isConnected());
                deviceList.add(existedDevice);
            }
        }

    }

    protected static List<ConcurrentHashMap<String, Object>> saveProductInfo(List<ConcurrentHashMap<String, String>> productInfo) {
        if (SDKEventManager.productInfo == null) {
            SDKEventManager.productInfo = new ArrayList();
        } else {
            SDKEventManager.productInfo.clear();
        }

        List<ConcurrentHashMap<String, Object>> maskProductInfo = new ArrayList();
        if (productInfo != null && productInfo.size() > 0) {
            Iterator var2 = productInfo.iterator();

            while (var2.hasNext()) {
                ConcurrentHashMap<String, String> pkInfo = (ConcurrentHashMap) var2.next();
                Object pkObj = pkInfo.containsKey("productKey") ? (String) pkInfo.get("productKey") : null;
                String pk = pkObj instanceof String ? (String) pkObj : null;
                Object psObj = pkInfo.containsKey("productSecret") ? (String) pkInfo.get("productSecret") : null;
                String ps = psObj instanceof String ? (String) psObj : null;
                String adapterTypeStr = pkInfo.containsKey("usingAdapter") ? (String) pkInfo.get("usingAdapter") : "";
                GizAdapterType adapterType = GizAdapterType.GizAdapterNon;
                if (!adapterTypeStr.equals(GizAdapterType.GizAdapterDataPointMap.name()) && !adapterTypeStr.equals("1")) {
                    if (!adapterTypeStr.equals(GizAdapterType.GizAdapterDataPointFunc.name()) && !adapterTypeStr.equals("2")) {
                        if (adapterTypeStr.equals(GizAdapterType.GizAdapterDataPointScript.name()) || adapterTypeStr.equals("3")) {
                            adapterType = GizAdapterType.GizAdapterDataPointScript;
                        }
                    } else {
                        adapterType = GizAdapterType.GizAdapterDataPointFunc;
                    }
                } else {
                    adapterType = GizAdapterType.GizAdapterDataPointMap;
                }

                String localMeshTypeStr = pkInfo.containsKey("localMeshType") ? (String) pkInfo.get("localMeshType") : "";
                GizLocalMeshType localMeshType = GizLocalMeshType.GizLocalMeshUnSupport;
                if (!localMeshTypeStr.equals(GizLocalMeshType.GizLocalMeshSub.name()) && !localMeshTypeStr.equals("1")) {
                    if (localMeshTypeStr.equals(GizLocalMeshType.GizLocalMeshGateway.name()) || localMeshTypeStr.equals("2")) {
                        localMeshType = GizLocalMeshType.GizLocalMeshGateway;
                    }
                } else {
                    localMeshType = GizLocalMeshType.GizLocalMeshSub;
                }

                if (pk != null && pk.length() == 32 && ps != null && ps.length() == 32) {
                    ConcurrentHashMap<String, Object> info = new ConcurrentHashMap();
                    info.put("productKey", pk);
                    info.put("productSecret", ps);
                    info.put("adapterType", adapterType.ordinal());
                    info.put("localMeshType", localMeshType.ordinal());
                    SDKEventManager.productInfo.add(info);
                } else {
                    SDKLog.d("throw the invalid pkInfo: <pk=" + pk + ", ps=" + ps + ", usingAdapter=" + adapterType + ">");
                }

                String psMasking = ps != null ? Utils.dataMasking(ps) : null;
                ConcurrentHashMap<String, Object> infoMasking = new ConcurrentHashMap();
                infoMasking.put("productKey", pk);
                infoMasking.put("productSecret", psMasking);
                infoMasking.put("adapterType", adapterType);
                infoMasking.put("localMeshType", localMeshType.ordinal());
                maskProductInfo.add(infoMasking);
            }
        }

        SDKEventManager.productInfo = Utils.removeDuplicateMap(SDKEventManager.productInfo);
        return maskProductInfo;
    }

    protected static ConcurrentHashMap<String, Object> getProductInfoByProductKey(String productKey) {
        ConcurrentHashMap<String, Object> productInfo = null;
        Iterator var2 = SDKEventManager.productInfo.iterator();

        while (var2.hasNext()) {
            ConcurrentHashMap<String, Object> pkInfo = (ConcurrentHashMap) var2.next();
            Object pkObj = pkInfo.containsKey("productKey") ? (String) pkInfo.get("productKey") : null;
            String pk = pkObj instanceof String ? (String) pkObj : null;
            if (pk != null && pk.equals(productKey)) {
                productInfo = pkInfo;
                break;
            }
        }

        return productInfo;
    }

    protected void cutNet() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService("wifi");
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String newSsid = wifiInfo.getSSID();
        if (configureMode == GizWifiConfigureMode.GizWifiSoftAP) {
            WifiAutoConnectManager manager = new WifiAutoConnectManager(wifiManager);
            SDKLog.d("softap ssid :" + onboardingSsid + ", softap password :" + Utils.dataMasking(originalKey));
            manager.connect(onboardingSsid, originalKey, WifiAutoConnectManager.getCipherType(mContext, onboardingSsid));
        }

    }

    public String getPhoneID() {
        return Secure.getString(mContext.getContentResolver(), "android_id");
    }

    protected boolean deamonstart() {
        Runtime runtime = Runtime.getRuntime();

        try {
            Process process = runtime.exec("ps");
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader buffer = new BufferedReader(inputStreamReader);
            String line = null;

            String psCmdStr;
            for (psCmdStr = null; (line = buffer.readLine()) != null; psCmdStr = psCmdStr + line) {
            }

            SDKLog.d("get current process list: \n" + psCmdStr);
            if (psCmdStr != null && psCmdStr.contains("GizWifiSDKDaemon")) {
                SDKLog.d("GizWifiSDKDaemon exist!");
                this.setIsStartedDaemon(true);
                return true;
            } else {
                SDKLog.d("GizWifiSDKDaemon not exist!");
                this.setIsStartedDaemon(false);
                return false;
            }
        } catch (IOException var7) {
            var7.printStackTrace();
            return false;
        }
    }

    private byte[] fDataAppend(String domain) {
        SDKLog.d("pomia -----> get domain: " + domain);
        byte[] byte_domain = new byte[1];
        byte[] byte_timezoneCode = new byte[3];
        if ("api.gizwits.com".equals(domain)) {
            SDKLog.d("pomia -----> domain: " + domain + " and " + "api.gizwits.com" + " is matched.");
            byte_domain[0] = 48;
        } else if ("usapi.gizwits.com".equals(domain)) {
            SDKLog.d("pomia -----> domain: " + domain + " and " + "usapi.gizwits.com" + " is matched.");
            byte_domain[0] = 49;
        } else if ("euapi.gizwits.com".equals(domain)) {
            SDKLog.d("pomia -----> domain: " + domain + " and " + "euapi.gizwits.com" + " is matched.");
            byte_domain[0] = 50;
        } else {
            SDKLog.d("pomia -----> domain: " + domain + " does not match any of the three gizwits domains");
            byte_domain[0] = 0;
        }

        Calendar calendar = new GregorianCalendar();
        double diffHours = (double) calendar.getTimeZone().getOffset(System.currentTimeMillis()) / 1000.0D / 3600.0D;
        int fHours = (int) Math.floor(Math.abs(diffHours));
        int mHours = (int) Math.floor((Math.abs(diffHours) - (double) fHours) * 100.0D);
        byte[] byte_diffHours = new byte[2];
        fHours = diffHours < 0.0D ? fHours * -1 : fHours;
        if (fHours >= 0 && fHours <= 9) {
            byte_diffHours[0] = (byte) (fHours + 48);
        } else if (fHours >= 10 && fHours <= 15) {
            byte_diffHours[0] = (byte) (fHours + 55);
        } else if (fHours < 0 && fHours >= -15) {
            byte_diffHours[0] = (byte) (fHours * -1 + 96);
        }

        if (mHours == 25) {
            byte_diffHours[1] = 49;
        } else if (mHours == 50) {
            byte_diffHours[1] = 50;
        } else if (mHours == 75) {
            byte_diffHours[1] = 51;
        } else {
            byte_diffHours[1] = 48;
        }

        byte[] byte_pre_split = new byte[]{0};
        byte[] byte_split = new byte[]{27};
        byte[] byte_append = new byte[byte_pre_split.length + 3 * byte_split.length + byte_domain.length + byte_diffHours.length + byte_timezoneCode.length];
        SDKLog.d("pomia -----> byte_domain[0]: " + byte_domain[0]);
        if (byte_domain[0] == 0) {
            return new byte[0];
        } else {
            int pos = 0;
            byte_append[pos] = byte_pre_split[0];
            pos = pos + byte_pre_split.length;
            byte_append[pos] = byte_split[0];
            pos += byte_split.length;
            System.arraycopy(byte_domain, 0, byte_append, pos, byte_domain.length);
            pos += byte_domain.length;
            byte_append[pos] = byte_split[0];
            pos += byte_split.length;
            System.arraycopy(byte_diffHours, 0, byte_append, pos, byte_diffHours.length);
            pos += byte_diffHours.length;
            JSONObject timeZoneJson = Utils.readJsonFileInJar(mContext, "timezoneName.json");
            String defaultZone = TimeZone.getDefault().getID();
            Iterator it = timeZoneJson.keys();
            String vol = "";
            String key = null;

            while (it.hasNext()) {
                key = it.next().toString();

                try {
                    vol = timeZoneJson.getString(key);
                } catch (Exception var20) {
                }

                if (defaultZone.equals(vol)) {
                    byte_timezoneCode = key.getBytes();
                    break;
                }
            }

            byte_append[pos] = byte_split[0];
            pos += byte_split.length;
            System.arraycopy(byte_timezoneCode, 0, byte_append, pos, byte_timezoneCode.length);
            return byte_append;
        }
    }

    private String appendDomain2Onboarding(String password, String domain) {
        SDKLog.d("domain info transfer start");
        byte[] dataAppend = this.fDataAppend(domain);
        SDKLog.d("domain info transfer end");
        if (dataAppend.length == 0) {
            SDKLog.d("dataAppend = null");
            return password;
        } else {
            byte[] byte_password = password.getBytes();
            byte[] byte_all = new byte[byte_password.length + dataAppend.length];
            System.arraycopy(byte_password, 0, byte_all, 0, byte_password.length);
            System.arraycopy(dataAppend, 0, byte_all, byte_password.length, dataAppend.length);
            SDKLog.d("domain bytes copy finished");
            String password_domain = new String(byte_all);
            String allToString = Utils.dataMasking(password);
            byte[] bs = password_domain.getBytes();

            for (int i = byte_password.length; i < bs.length; ++i) {
                allToString = allToString + " " + bs[i];
            }

            SDKLog.d("config key and dataAppend = " + allToString);
            return password_domain;
        }
    }

    protected static void onDidNotifyEvent(GizEventType eventType, Object eventSource, GizWifiErrorCode eventID, String eventMessage) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, eventType: " + eventType.name() + ", eventSource: " + (eventSource == null ? "null" : eventSource) + ", eventID: " + eventID + ", eventMessage: " + eventMessage);
        if (mListener != null) {
            mListener.didNotifyEvent(eventType, eventSource, eventID, eventMessage);
            SDKLog.d("Callback end");
        }

    }

    public void onDidSetDeviceOnboarding(GizWifiErrorCode result, GizWifiDevice device) {
        SoftApConfig.getInstance().stopConfig();
        String mac = device == null ? null : device.getMacAddress();
        String did = device == null ? null : device.getDid();
        String productKey = device == null ? null : device.getProductKey();
        if (configureMode != null) {
            onboarding_end = System.currentTimeMillis();
            float elapsed_time = (float) (onboarding_end - onboarding_start) / 1000.0F;
            String end = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
            DateUtil.getDiff(end, onboardingstarttime);
            JSONArray gagentTypesBiz = new JSONArray();

            for (int i = 0; i < gagentTypes.size(); ++i) {
                try {
                    GizWifiGAgentType type = (GizWifiGAgentType) gagentTypes.get(i);
                    gagentTypesBiz.put(i, type.name());
                } catch (JSONException var11) {
                    var11.printStackTrace();
                }
            }

            SDKLog.b("onboarding_end", result.name(), "" + Utils.changeString("elapsed_time") + ": " + elapsed_time + ", " + Utils.changeString("gagent_type") + ": " + gagentTypesBiz + ", " + Utils.changeString("config_mode") + ": " + Utils.changeString(configureMode.name()));
        }

        if (result != GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING) {
            if (configureMode == GizWifiConfigureMode.GizWifiAirLink || configureMode == GizWifiConfigureMode.GizWifiAirLinkMulti && result != GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                stopAirlink(gagentTypes);
            }

            if (configureMode == GizWifiConfigureMode.GizWifiBleLink || configureMode == GizWifiConfigureMode.GizWifiBleLinkMulti) {
                if (result == GizWifiErrorCode.GIZ_SDK_BLE_UNFIND_DEVICE_PERIPHERAL || result == GizWifiErrorCode.GIZ_SDK_BLE_BLUETOOTH_FUNCTION_NOT_TURNED_ON) {
                    this.sendStopDeviceOnboarding();
                }

                if (result != GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    BleSoftApConfig.getInstance(mContext).stopConfig();
                }
            }
        }

        if (!TextUtils.isEmpty(onboardingSsid) && mContext != null && !onboardingSsid.equals(Utils.getWifiSSID(mContext))) {
            SDKLog.d("=====> connected ssid : " + Utils.getWifiSSID(mContext) + ", onboarding ssid :" + onboardingSsid);
        }

        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", device: " + (device == null ? "null" : device.simpleInfoMasking()));
        if (result != GizWifiErrorCode.GIZ_SDK_SUCCESS && result != GizWifiErrorCode.GIZ_SDK_ONBOARDING_STOPPED && result != GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING) {
            SDKLog.userFeedback((String) null, "SDK Report:  device onboarding failed", true);
        }

        if (mListener != null) {
            mListener.didSetDeviceOnboarding(result, device);
            mListener.didSetDeviceOnboarding(result, mac, did, productKey);
            mListener.didSetDeviceWifi(Utils.changeErrorCode(result.getResult()), device);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidGetSSIDList(GizWifiErrorCode result, List<GizWifiSSID> ssidInfoList) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", ssidInfoList: " + ssidInfoList);
        if (mListener != null) {
            mListener.didGetSSIDList(result, ssidInfoList);
            mListener.didGetSSIDList(Utils.changeErrorCode(result.getResult()), ssidInfoList);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidReceiveDeviceLog(GizWifiErrorCode result, String mac, int timeStamp, int logSN, String log) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", deviceLog: " + log);
        if (mListener != null) {
            mListener.didReceiveDeviceLog(result, mac, timeStamp, logSN, log);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidGetMapTab(GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didGetMapTab(result);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidDeviceSafetyRegister(List<ConcurrentHashMap<String, Object>> successDevices, List<ConcurrentHashMap<String, Object>> failedDevices) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, successDevices: " + successDevices + ", failedDevices: " + failedDevices);
        if (mListener != null) {
            mListener.didDeviceSafetyRegister(successDevices, failedDevices);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidDeviceSafetyUnbind(List<ConcurrentHashMap<String, Object>> failedDevices) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, failedDevices: " + failedDevices);
        if (mListener != null) {
            mListener.didDeviceSafetyUnbind(failedDevices);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidDiscoveredMeshDevices(GizWifiErrorCode result, List<ConcurrentHashMap<String, String>> meshDeviceList) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", meshDeviceList: " + meshDeviceList.size());
        if (mListener != null) {
            mListener.didDiscoveredMeshDevices(result, meshDeviceList);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidChangeDeviceMesh(GizWifiErrorCode result, ConcurrentHashMap<String, String> meshDevice) {
        if (result != GizWifiErrorCode.GIZ_SDK_BLE_HAS_CONFLICT_OPERATION_IS_ONGOING) {
            this.resetAllMeshStatus();
        }

        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", meshDevice: " + (meshDevice == null ? "" : meshDevice.toString()));
        if (mListener != null) {
            mListener.didChangeDeviceMesh(result, meshDevice);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidAddDevicesToGroup(GizWifiErrorCode result, List<GizWifiDevice> meshDevice) {
        if (result != GizWifiErrorCode.GIZ_SDK_BLE_HAS_CONFLICT_OPERATION_IS_ONGOING) {
            this.resetAllMeshStatus();
        }

        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", meshDevice: " + (meshDevice == null ? "" : meshDevice.toString()));
        if (mListener != null) {
            mListener.didAddDevicesToGroup(result, meshDevice);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidDeleteDevicesFromGroup(GizWifiErrorCode result, List<GizWifiDevice> meshDevice) {
        if (result != GizWifiErrorCode.GIZ_SDK_BLE_HAS_CONFLICT_OPERATION_IS_ONGOING) {
            this.resetAllMeshStatus();
        }

        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", meshDevice: " + (meshDevice == null ? "" : meshDevice.toString()));
        if (mListener != null) {
            mListener.didDeleteDevicesFromGroup(result, meshDevice);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidUpdateGroupWithDevice(GizWifiErrorCode result, List<Integer> ids, GizWifiDevice gizWifiDevice) {
        if (result != GizWifiErrorCode.GIZ_SDK_BLE_HAS_CONFLICT_OPERATION_IS_ONGOING) {
            this.resetAllMeshStatus();
        }

        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", meshDevice: " + (gizWifiDevice == null ? "" : gizWifiDevice.toString()));
        if (mListener != null) {
            mListener.didUpdateGroupsWithDevice(result, ids, gizWifiDevice);
            SDKLog.d("Callback end");
        }

    }

    protected void onDidRestoreDeviceFactorySetting(GizWifiErrorCode result, String mac) {
        if (result != GizWifiErrorCode.GIZ_SDK_BLE_HAS_CONFLICT_OPERATION_IS_ONGOING) {
            this.resetAllMeshStatus();
        }

        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", meshDevice: " + mac);
        if (mListener != null) {
            mListener.didRestoreDeviceFactorySetting(result, mac);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", deviceList: " + listMasking(deviceList));
        if (mListener != null) {
            if (result != GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN) {
                String end = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
                if (!TextUtils.isEmpty(getboundtime)) {
                    long diff = DateUtil.getDiff(end, getboundtime);
                    SDKLog.b("get_bound_devices_end", result.name(), Utils.changeString("elapsed_time") + ":" + diff);
                }
            }

            mListener.didDiscovered(result, deviceList);
            mListener.didDiscovered(Utils.changeErrorCode(result.getResult()), deviceList);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidUpdateProduct(GizWifiErrorCode result, String productKey, String productUI) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", productKey is " + (productKey == null ? "null" : productKey) + ", productUI is " + (productUI == null ? "null" : "not null"));
        if (mListener != null) {
            mListener.didUpdateProduct(result, productKey, productUI);
            mListener.didUpdateProduct(Utils.changeErrorCode(result.getResult()), productKey);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidBindRemoteDevice(GizWifiErrorCode result, String did) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", did: " + Utils.dataMasking(did));
        if (mListener != null) {
            if (result != GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN) {
                String end = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
                long diff = DateUtil.getDiff(remotetimestart, end);
                SDKLog.b("device_bind_end", result.name(), Utils.changeString("did") + ":" + Utils.changeString(Utils.dataMasking(did)) + " " + Utils.changeString("elapsed_time") + ":" + diff);
            }

            mListener.didBindDevice(result, did);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidBindDevice(int error, String errorMessage, String did) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, error: " + error + ", errorMessage: " + errorMessage + ", did: " + Utils.dataMasking(did));
        if (mListener != null) {
            mListener.didBindDevice(error, errorMessage, did);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidUnbindDevice(GizWifiErrorCode result, String resultMessage, String did) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", did: " + Utils.dataMasking(did));
        if (mListener != null) {
            String errorMessage;
            if (result != GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN) {
                errorMessage = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
                long diff = DateUtil.getDiff(unbindtime, errorMessage);
                SDKLog.b("device_unbind_end", result.name(), Utils.changeString("elapsed_time") + ":" + diff);
            }

            mListener.didUnbindDevice(result, did);
            errorMessage = resultMessage;
            if (TextUtils.isEmpty(resultMessage)) {
                errorMessage = result.name();
            }

            mListener.didUnbindDevice(Utils.changeErrorCode(result.getResult()), errorMessage, did);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidGetGroups(GizWifiErrorCode result, List<GizWifiGroup> groupList) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didGetGroups(result, groupList);
            mListener.didGetGroups(Utils.changeErrorCode(result.getResult()), groupList);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidGetCaptchaCode(GizWifiErrorCode result, String resultMessage, String token, String captchaId, String captchaURL) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", token: " + Utils.dataMasking(token) + ", captchaId: " + captchaId + ", captchaURL: " + Utils.dataMasking(captchaURL));
        if (mListener != null) {
            mListener.didGetCaptchaCode(result, token, captchaId, captchaURL);
            mListener.didGetCaptchaCode(Utils.changeErrorCode(result.getResult()), resultMessage, token, captchaId, captchaURL);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidRequestSendPhoneSMSCode(GizWifiErrorCode result, String resultMessage, String token) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", token: " + Utils.dataMasking(token));
        if (mListener != null) {
            mListener.didRequestSendPhoneSMSCode(result, token);
            mListener.didRequestSendPhoneSMSCode(Utils.changeErrorCode(result.getResult()), resultMessage);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidVerifyPhoneSMSCode(GizWifiErrorCode result, String resultMessage) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didVerifyPhoneSMSCode(result);
            mListener.didVerifyPhoneSMSCode(Utils.changeErrorCode(result.getResult()), resultMessage);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidRegisterUser(GizWifiErrorCode result, String resultMessage, String uid, String token) {
        user_register_end = System.currentTimeMillis();
        float elapsed_time = (float) (user_register_end - user_register_start) / 1000.0F;
        SDKLog.b("user_register_end", result.name(), Utils.changeString("uid") + " :" + Utils.changeString(TextUtils.isEmpty(uid) ? "null" : uid) + "" + Utils.changeString("elapsed_time") + ": " + elapsed_time + "");
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", uid: " + uid + ", token: " + Utils.dataMasking(token));
        if (mListener != null) {
            mListener.didRegisterUser(result, uid, token);
            mListener.didRegisterUser(Utils.changeErrorCode(result.getResult()), resultMessage, uid, token);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidUserLogin(GizWifiErrorCode result, String resultMessage, String uid, String token) {
        user_login_end = System.currentTimeMillis();
        float elapsed_time = (float) (user_login_end - user_login_start) / 1000.0F;
        SDKLog.b("user_login_end", result.name(), Utils.changeString("uid") + ": " + Utils.changeString(TextUtils.isEmpty(uid) ? "null" : uid) + "," + Utils.changeString("elapsed_time") + ": " + elapsed_time + "");
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", uid: " + uid + ", token: " + Utils.dataMasking(token));
        if (mListener != null) {
            mListener.didUserLogin(result, uid, token);
            mListener.didUserLogin(Utils.changeErrorCode(result.getResult()), resultMessage, uid, token);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidChangeUserPassword(GizWifiErrorCode result, String resultMessage) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didChangeUserPassword(result);
            mListener.didChangeUserPassword(Utils.changeErrorCode(result.getResult()), resultMessage);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidDisableLAN(GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didDisableLAN(result);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidDidChannelIDBind(GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didChannelIDBind(result);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidDidCreateScheduler(GizWifiErrorCode result, String did) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", did: " + Utils.dataMasking(did));
        if (mListener != null) {
            mListener.didCreateScheduler(result, did);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidGetScheduleTasks(GizWifiErrorCode result, List<GizDeviceScheduler> scheduleTaskList) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", scheduleTaskList: " + (scheduleTaskList == null ? "null" : scheduleTaskList.size()));
        if (mListener != null) {
            mListener.didGetSchedulers(result, scheduleTaskList);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidDeleteScheduler(GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didDeleteScheduler(result);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidGetSchedulerStatus(GizWifiErrorCode result, String sid, String datetime, GizScheduleStatus status, ConcurrentHashMap<String, Boolean> statusDetail) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didGetSchedulerStatus(result, sid, datetime, status, statusDetail);
            SDKLog.d("Callback end");
        }

    }

    protected static void OnDidGetDevicesToSetServerInfo(GizWifiErrorCode result, List<ConcurrentHashMap<String, String>> devices) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", devices: " + devices.size());
        if (mListener != null) {
            mListener.didGetDevicesToSetServerInfo(result, devices);
            SDKLog.d("Callback end");
        }

    }

    public void OnDidSetDeviceServerInfo(GizWifiErrorCode result, String mac) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didSetDeviceServerInfo(result, mac);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidDidChannelIDUnBind(GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didChannelIDUnBind(result);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidChangeUserInfo(GizWifiErrorCode result, String resultMessage) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didChangeUserInfo(result);
            mListener.didChangeUserInfo(Utils.changeErrorCode(result.getResult()), resultMessage);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidGetUserInfo(GizWifiErrorCode result, String resultMessage, GizUserInfo userInfo) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", userInfo: " + (userInfo == null ? "null" : userInfo.infoMasking()));
        if (mListener != null) {
            mListener.didGetUserInfo(result, userInfo);
            mListener.didGetUserInfo(Utils.changeErrorCode(result.getResult()), resultMessage, userInfo);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidTransAnonymousUser(GizWifiErrorCode result, String resultMessage) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didTransAnonymousUser(result);
            mListener.didTransUser(Utils.changeErrorCode(result.getResult()), resultMessage);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidGetCurrentCloudService(GizWifiErrorCode result, ConcurrentHashMap<String, String> cloudServiceInfo) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", cloudServiceInfo: " + (cloudServiceInfo == null ? "null" : cloudServiceInfo));
        if (mListener != null) {
            mListener.didGetCurrentCloudService(result, cloudServiceInfo);
            mListener.didGetCurrentCloudService(Utils.changeErrorCode(result.getResult()), result.name(), cloudServiceInfo);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidUserLogout(GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, error: " + result);
        if (mListener != null) {
            mListener.didUserLogout(result);
            SDKLog.d("Callback end");
        }

    }

    protected void startDaemon(Context context, String appID) {
        if (context == null) {
            onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.GIZ_SDK_APK_CONTEXT_IS_NULL, GizWifiErrorCode.GIZ_SDK_APK_CONTEXT_IS_NULL.name());
        } else if (appID != null && appID.length() == 32) {
            this.startDaemonII(context, appID, (String) null, (ConcurrentHashMap) null, false);
        } else {
            onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.GIZ_SDK_APPID_INVALID, GizWifiErrorCode.GIZ_SDK_APPID_INVALID.name());
        }
    }

    protected void startDaemon(Context context, String appID, List<String> productKeys, ConcurrentHashMap<String, String> cloudServiceInfo, boolean autoSetDeviceDomain) {
        if (context == null) {
            onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.GIZ_SDK_APK_CONTEXT_IS_NULL, GizWifiErrorCode.GIZ_SDK_APK_CONTEXT_IS_NULL.name());
        } else if (appID != null && appID.length() == 32) {
            if (productKeys != null && productKeys.size() > 0) {
                new ArrayList();
                List<String> list = Utils.removeInvalidLength(productKeys, 32);
                if (list.size() > 0) {
                    specialProductKeys = Utils.removeDuplicateString(list);
                }
            }

            this.startDaemonII(context, appID, (String) null, cloudServiceInfo, autoSetDeviceDomain);
        } else {
            onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.GIZ_SDK_APPID_INVALID, GizWifiErrorCode.GIZ_SDK_APPID_INVALID.name());
        }
    }

    protected void startDaemon(Context context, String appID, String appSecret, List<String> productInfo, ConcurrentHashMap<String, String> cloudServiceInfo, boolean autoSetDeviceDomain) {
        if (context == null) {
            onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.GIZ_SDK_APK_CONTEXT_IS_NULL, GizWifiErrorCode.GIZ_SDK_APK_CONTEXT_IS_NULL.name());
        } else if (appID != null && appID.length() == 32) {
            if (appSecret != null && appSecret.length() == 32) {
                if (productInfo != null && productInfo.size() > 0) {
                    new ArrayList();
                    List<String> list = Utils.removeInvalidLength(productInfo, 32);
                    if (list.size() > 0) {
                        specialProductKeys = Utils.removeDuplicateString(list);
                    }
                }

                this.startDaemonII(context, appID, appSecret, cloudServiceInfo, autoSetDeviceDomain);
            } else {
                onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.GIZ_SDK_APP_SECRET_INVALID, GizWifiErrorCode.GIZ_SDK_APP_SECRET_INVALID.name());
            }
        } else {
            onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.GIZ_SDK_APPID_INVALID, GizWifiErrorCode.GIZ_SDK_APPID_INVALID.name());
        }
    }

    protected void startDaemon(Context context, String appId, String appSecret, ConcurrentHashMap<String, String> cloudServiceInfo, boolean autoSetDeviceDomain) {
        if (appId != null && appId.length() == 32) {
            if (appSecret != null && appSecret.length() == 32) {
                this.startDaemonII(context, appId, appSecret, cloudServiceInfo, autoSetDeviceDomain);
            } else {
                onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.GIZ_SDK_APP_SECRET_INVALID, GizWifiErrorCode.GIZ_SDK_APP_SECRET_INVALID.name());
            }
        } else {
            onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.GIZ_SDK_APPID_INVALID, GizWifiErrorCode.GIZ_SDK_APPID_INVALID.name());
        }
    }

    protected void startDaemonII(Context context, String appID, String appSecret, ConcurrentHashMap<String, String> cloudServiceInfo, boolean autoSetDeviceDomain) {
        mContext = context;
        appId = appID;
        SDKEventManager.appSecret = appSecret;
        SDKEventManager.autoSetDeviceDomain = autoSetDeviceDomain;
        String file_dir = context.getFilesDir().getAbsolutePath();
        MessageHandler.getSingleInstance().setHandler(this.nHandler);
        this.copyDaemon(context, file_dir);
        this.ps_start = System.currentTimeMillis();
        long ps_end = System.currentTimeMillis();
        List<String> needPermissions = Utils.needApkPermissions(context);
        Iterator var10 = needPermissions.iterator();

        String permission;
        while (var10.hasNext()) {
            permission = (String) var10.next();
            SDKLog.e("The apk permission is not set: " + permission);
        }

        if (needPermissions != null && needPermissions.size() > 0) {
            var10 = needPermissions.iterator();

            while (var10.hasNext()) {
                permission = (String) var10.next();
                SDKLog.e("The apk permission is not set: " + permission + " GizWifiSDK start failed");
            }

            onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.GIZ_SDK_APK_PERMISSION_NOT_SET, GizWifiErrorCode.GIZ_SDK_APK_PERMISSION_NOT_SET.name());
        } else {
            SDKLog.d("ps_time  :" + (ps_end - this.ps_start));
            SDKLog.b("startSDK", "GIZ_SDK_SUCCESS", "" + Utils.changeString("app_name") + ": " + Utils.changeString(Utils.getApplicationName(context)) + ", " + Utils.changeString("app_version") + ": " + Utils.changeString(Utils.getVersion(context)) + "," + Utils.changeString("time_stamp") + ": " + (double) System.nanoTime() / 1000000.0D + DateUtil.getLogString(mContext));
            MyCrashHandler.getInstance().init(context.getApplicationContext());
            if (!Constant.isconnected) {
                this.runDaemon(context);
            }

            MessageHandler.getSingleInstance().setHandler(this.nHandler);
            if (Constant.isconnected) {
                this.makehandshake();
            }

            this.PingBaiDu();
        }
    }

    public void updateMeshDeviceStatus(String mac, GizWifiDeviceNetStatus status) {
        if (mac != null) {
            List<GizWifiDevice> deviceList = this.getDeviceList();

            for (int i = 0; i < deviceList.size(); ++i) {
                GizWifiDevice gizWifiDevice = (GizWifiDevice) deviceList.get(i);
                if (gizWifiDevice.getMacAddress().equals(mac) && gizWifiDevice.getNetStatus() != status) {
                    SDKLog.e("meshdevice" + mac + " " + status.name());
                    gizWifiDevice.setNetStatus(status);
                    gizWifiDevice.onDidUpdateNetStatus((GizWifiDevice) deviceList.get(i), status);
                    onDidDiscovered(GizWifiErrorCode.GIZ_SDK_SUCCESS, deviceList);
                }
            }

        }
    }

    private void PingBaiDu() {
        HandlerThread connectDaemonThread = new HandlerThread("pingthread");
        if (this.pingHandler == null) {
            connectDaemonThread.start();
            this.pingHandler = new SDKEventManager.BaiDuHandler(connectDaemonThread.getLooper());
            this.pingHandler.sendEmptyMessage(11111);
        }

    }

    private void makehandshake() {
        JSONObject obj = new JSONObject();
        int sn = Utils.getSn();

        try {
            obj.put("cmd", 1001);
            obj.put("sn", sn);
            obj.put("clientVersion", "16.20080310");
            if (Constant.DaemonVersion != null && Constant.DaemonVersion.contains(".a")) {
                obj.put("daemonVersion", Constant.DaemonVersion.substring(0, Constant.DaemonVersion.length() - 2));
            } else {
                obj.put("daemonVersion", Constant.DaemonVersion);
            }

            obj.put("appid", appId);
            obj.put("appSecret", appSecret);
            if (mContext != null) {
                obj.put("appPackageName", mContext.getPackageName());
            }

            if (!TextUtils.isEmpty(Constant.uid)) {
                obj.put("uid", Constant.uid);
            }

            if (!TextUtils.isEmpty(Constant.token)) {
                obj.put("token", Constant.token);
            }

            JSONArray productInfoArray = new JSONArray();
            Iterator var4;
            JSONObject ob;
            if (productInfo != null) {
                var4 = productInfo.iterator();

                while (var4.hasNext()) {
                    ConcurrentHashMap<String, Object> info = (ConcurrentHashMap) var4.next();
                    ob = new JSONObject();
                    ob.put("productKey", info.get("productKey"));
                    ob.put("productSecret", info.get("productSecret"));
                    ob.put("usingAdapter", info.get("adapterType"));
                    productInfoArray.put(ob);
                }
            } else if (specialProductKeys != null && specialProductKeys.size() > 0) {
                var4 = specialProductKeys.iterator();

                while (var4.hasNext()) {
                    String pk = (String) var4.next();
                    ob = new JSONObject();
                    ob.put("productKey", pk);
                    productInfoArray.put(ob);
                }
            }

            obj.put("specialProductKeys", productInfoArray);
            obj.put("autoSetDeviceDomain", autoSetDeviceDomain);
            if (autoSetDeviceDomain) {
                Calendar calendar = new GregorianCalendar();
                int offset2 = calendar.getTimeZone().getOffset(System.currentTimeMillis()) / 1000;
                obj.put("zone", offset2);
            }

            JSONArray array = new JSONArray();
            Iterator var13 = this.getDeviceListByProductKeys().iterator();

            while (var13.hasNext()) {
                GizWifiDevice gizWifiDevice = (GizWifiDevice) var13.next();
                JSONObject jsonlist = new JSONObject();
                if (gizWifiDevice.isSubscribed()) {
                    jsonlist.put("mac", gizWifiDevice.getMacAddress());
                    jsonlist.put("did", gizWifiDevice.getDid());
                    jsonlist.put("productKey", gizWifiDevice.getProductKey());
                    jsonlist.put("autoGetDeviceStatus", gizWifiDevice.isAutoGetDeviceStatus());
                    array.put(jsonlist);
                }
            }

            if (array.length() != 0) {
                obj.put("devices", array);
            }

            JSONObject cloudServiceInfo = new JSONObject();
            cloudServiceInfo.put("openapi", domainInfo.get("openapi"));
            cloudServiceInfo.put("site", domainInfo.get("site"));
            cloudServiceInfo.put("push", domainInfo.get("push"));
            obj.put("cloudServiceInfo", cloudServiceInfo);
        } catch (Exception var8) {
            SDKLog.e("StartDaemon_fail :" + var8);
            var8.printStackTrace();
        }

        MessageHandler.getSingleInstance().send(obj.toString());
        this.makeTimer(4000, 1002, sn);
    }

    private static void registBroadcasereciver() {
        NetStatusReceiver broadcase = new NetStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mContext.getApplicationContext().registerReceiver(broadcase, filter);
    }

    protected void sendMessage2Deamon(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    private void copyDaemon(Context context, String filesPath) {
        try {
            boolean x86 = Utils.isX86();
            new File(filesPath);
            URL url = SDKEventManager.class.getResource("files/default");
            JarURLConnection jarCon = (JarURLConnection) url.openConnection();
            jarCon.setUseCaches(false);
            jarCon.connect();
            JarFile jarFile = jarCon.getJarFile();
            Enumeration entrys = jarFile.entries();

            while (true) {
                File file;
                JarEntry fileEntry;
                String curFileName;
                String curUrlStr;
                do {
                    while (true) {
                        do {
                            if (!entrys.hasMoreElements()) {
                                jarFile.close();
                                return;
                            }

                            fileEntry = (JarEntry) entrys.nextElement();
                        } while (!fileEntry.getName().contains("com/gizwits/gizwifisdk/api/files/"));

                        curFileName = fileEntry.getName().substring(fileEntry.getName().lastIndexOf("/"));
                        curUrlStr = url.toString().substring(0, url.toString().lastIndexOf("/")) + curFileName;
                        if (!curFileName.contains("GizWifiSDKDaemon")) {
                            break;
                        }

                        Constant.DaemonVersion = curFileName.split("-")[1];
                        if (x86 && curFileName.contains("x86")) {
                            Constant.daemonname = curFileName;
                            break;
                        }

                        if (!x86 && !curFileName.contains("x86")) {
                            Constant.daemonname = curFileName;
                            break;
                        }
                    }

                    file = new File(filesPath + curFileName);
                    if (curFileName.contains("tz_domain.json")) {
                        String str = "";
                        URL curUrl = new URL(curUrlStr);
                        InputStream inStream = curUrl.openConnection().getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                        StringBuffer sb = new StringBuffer();

                        while ((str = reader.readLine()) != null) {
                            sb.append(str).append("\n");
                        }

                        try {
                            JSONObject obj = new JSONObject(sb.toString());
                            cityJson = obj;
                        } catch (JSONException var18) {
                            var18.printStackTrace();
                        }
                    }
                } while (file.exists());

                URL curUrl = new URL(curUrlStr);
                InputStream inStream = curUrl.openConnection().getInputStream();
                FileOutputStream outStream = context.openFileOutput(curFileName.replace("/", ""), 0);
                int nread = 0;
                byte[] buffer = new byte[1024];
                while ((nread = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, nread);
                }

                SDKLog.d("copy File " + fileEntry.getName() + " Success, Size = " + fileEntry.getSize());
                inStream.close();
                outStream.close();
            }
        } catch (IOException var19) {
            SDKLog.e("loadFiles catch Exception: " + var19.toString());
        }
    }

    private void runDaemon(Context context) {
        String filesPath = context.getFilesDir().getPath();
        String fileCachePath = SDKLog.getFileCachePath(context);
        String phoneModel = Build.MODEL;
        String phoneVersion = VERSION.RELEASE;
        boolean background = Utils.isApkBackground(context);
        String exe = filesPath + Constant.daemonname;
        String argvs = exe + " -phoneOS Android";
        argvs = argvs + " -phoneModel " + phoneModel;
        argvs = argvs + " -phoneID " + this.getPhoneID();
        argvs = argvs + " -phoneOSVersion " + phoneVersion;
        argvs = argvs + " -fileCachePath " + fileCachePath;
        argvs = argvs + " -background " + background;
        argvs = argvs + " -encryptLog " + SDKLog.getEncryptLog();
        argvs = argvs + " -clientVersion 16.20080310";
        SDKLog.d("Run into startDaemon " + exe);
        GizWifiDaemon.initSDK(this.getPhoneID(), phoneModel, "Android", phoneVersion, fileCachePath);
    }

    protected String getVersion() {
        if (Constant.DaemonVersion.equals("00.00160427.a")) {
            Utils.findDaemonInJar(mContext);
        }

        String version = "2." + "16.20080310".substring(0, 2) + "." + Constant.DaemonVersion.substring(0, 2) + ".1";
        String daemonVersion = Constant.DaemonVersion.substring(7);
        return version + daemonVersion;
    }

    protected void getUserInfo(String token) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        GizWifiErrorCode result = GizWifiErrorCode.GIZ_SDK_SUCCESS;
        if (isStartedDaemon && Constant.ishandshake) {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1069);
                obj.put("sn", sn);
                obj.put("token", token);
            } catch (JSONException var7) {
                SDKLog.e(var7.toString());
                var7.printStackTrace();
            }

            this.sendMessage2Deamon(obj);
            this.makeTimer(31000, 1070, sn);
        } else {
            result = GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN;
            onDidGetUserInfo(result, result.name(), (GizUserInfo) null);
        }
    }

    protected void getJSProtocol(String pk) {
        if (!Constant.ishandshake) {
            SDKLog.e("Daemon is not start, can't set daemon loglevel");
        } else {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1395);
                obj.put("sn", sn);
                obj.put("productKey", pk);
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            this.sendMessage2Deamon(obj);
            this.makeTimer(4000, 1396, sn);
        }
    }

    protected void setLogLevel(GizLogPrintLevel logLevel, boolean encryptLog) {
        SDKEventManager.logLevel = logLevel;
        if (!Constant.ishandshake) {
            SDKLog.e("Daemon is not start, can't set daemon loglevel");
        } else {
            int sn = Utils.getSn();
            JSONObject obj = new JSONObject();

            try {
                obj.put("cmd", 1003);
                obj.put("sn", sn);
                obj.put("logPrintLevel", logLevel.ordinal());
                obj.put("logPath", SDKLog.getFileCachePath(mContext));
                if (encryptLog) {
                    obj.put("encryptLog", true);
                    SDKLog.setEncryptLog(true);
                } else {
                    obj.put("encryptLog", false);
                    SDKLog.setEncryptLog(false);
                    SDKLog.init(mContext, domainInfo, appId, this.getVersion(), GizLogPrintLevel.GizLogPrintAll);
                }
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            this.sendMessage2Deamon(obj);
            this.makeTimer(4000, 1004, sn);
        }
    }

    private void sendConfigInfoToDaemon(String ssid, String appendKey, String softAPSSIDPrefix, int timeout, List<GizWifiGAgentType> types, GizWifiConfigureMode mode, boolean bind) {
        String configMode = "softap";
        if (mode == GizWifiConfigureMode.GizWifiAirLink) {
            configMode = "smartlink";
        } else if (mode == GizWifiConfigureMode.GizWifiAirLinkMulti) {
            configMode = "smartlinkMulti";
        } else if (mode == GizWifiConfigureMode.GizWifiBleLink) {
            configMode = "bleLink";
        } else if (mode == GizWifiConfigureMode.GizWifiBleLinkMulti) {
            configMode = "bleLinkMulti";
        }

        try {
            String type_json = null;
            String key_json = "";
            JSONArray types_json = new JSONArray();
            boolean hasAppend = false;

            for (int i = 0; i < gagentTypes.size(); ++i) {
                GizWifiGAgentType gtype = (GizWifiGAgentType) gagentTypes.get(i);
                if (gtype == GizWifiGAgentType.GizGAgentMXCHIP) {
                    type_json = "MXChip";
                }

                if (gtype == GizWifiGAgentType.GizGAgentHF || gtype == GizWifiGAgentType.GizGAgentHFV8 || gtype == GizWifiGAgentType.GizGAgentESP || gtype == GizWifiGAgentType.GizGAgentESPBroadcast || gtype == GizWifiGAgentType.GizGAgentBL || gtype == GizWifiGAgentType.GizGAgentFlyLink) {
                    hasAppend = true;
                }

                types_json.put(i, gtype.ordinal());
            }

            if (!originalKey.equals(appendKey) && hasAppend) {
                key_json = appendKey;
            } else {
                key_json = originalKey;
            }

            JSONObject js = new JSONObject();
            int sn = Utils.getSn();
            this.onboardingSn.add(sn);
            js.put("cmd", 1011);
            js.put("sn", sn);
            js.put("ssid", ssid);
            byte[] bytesKey = key_json.getBytes();
            js.put("password", GizWifiBinary.encode(bytesKey));
            js.put("configMode", configMode);
            js.put("gagentType", type_json);
            js.put("gagentTypes", types_json);
            js.put("timeout", timeout);
            js.put("bind", bind);
            this.makeTimer((timeout + 1) * 1000, 1012, sn);
            this.sendMessage2Deamon(js);
            if (configureMode == GizWifiConfigureMode.GizWifiBleLink || configureMode == GizWifiConfigureMode.GizWifiBleLinkMulti) {
                BleSoftApConfig.getInstance(mContext).startConfigDevice(softAPSSIDPrefix, sn, ssid, key_json, configureMode);
            }

            if (configureMode == GizWifiConfigureMode.GizWifiSoftAP) {
                SoftApConfig.getInstance().startConfig(mContext, ssid, key_json);
            }
        } catch (JSONException var16) {
            var16.printStackTrace();
        }

    }

    private void startAirlink(String ssid, String usingKey, int timeout, List<GizWifiGAgentType> gagentTypes) {
        Iterator var5 = gagentTypes.iterator();

        while (true) {
            while (var5.hasNext()) {
                GizWifiGAgentType type = (GizWifiGAgentType) var5.next();
                byte[] dataAppend;
                if (GizWifiGAgentType.GizGAgentHF == type) {
                    if (originalKey.equals(usingKey)) {
                        HFSnifferSmartLinker_V7.sharedInstance().start(ssid, originalKey, (byte[]) null, timeout, mContext, false);
                    } else {
                        dataAppend = this.fDataAppend((String) domainInfo.get("openapi"));
                        HFSnifferSmartLinker_V7.sharedInstance().start(ssid, originalKey, dataAppend, timeout, mContext, false);
                    }
                } else if (GizWifiGAgentType.GizGAgentHFV8 == type) {
                    if (originalKey.equals(usingKey)) {
                        HFSnifferSmartLinker_V7.sharedInstance().start(ssid, originalKey, (byte[]) null, timeout, mContext, true);
                    } else {
                        dataAppend = this.fDataAppend((String) domainInfo.get("openapi"));
                        HFSnifferSmartLinker_V7.sharedInstance().start(ssid, originalKey, dataAppend, timeout, mContext, true);
                    }
                } else if (GizWifiGAgentType.GizGAgentRTK != type) {
                    if (GizWifiGAgentType.GizGAgentWM == type) {
                        WMOneShotConfigManager.sharedInstance().start(ssid, originalKey, timeout, mContext);
                    } else if (GizWifiGAgentType.GizGAgentESP != type && type != null) {
                        if (GizWifiGAgentType.GizGAgentESPBroadcast == type) {
                            if (originalKey.equals(usingKey)) {
                                ESPTouchConfigManager.sharedInstance().start(ssid, originalKey, timeout, mContext, true);
                            } else {
                                ESPTouchConfigManager.sharedInstance().start(ssid, usingKey, timeout, mContext, true);
                            }
                        } else if (GizWifiGAgentType.GizGAgentFSK != type) {
                            if (GizWifiGAgentType.GizGAgentMXCHIP3 == type) {
                                MxChipMicoManager.sharedInstance(mContext).start(ssid, originalKey, timeout, mContext);
                            } else if (GizWifiGAgentType.GizGAgentBL == type) {
                                BLEasyConfigManager.sharedInstance().start(ssid, originalKey, timeout, mContext);
                            } else if (GizWifiGAgentType.GizGAgentAtmelEE == type) {
                                AtmelEESmartConfigManager.sharedInstance().start(ssid, originalKey, timeout, mContext);
                            } else if (GizWifiGAgentType.GizGAgentFlyLink == type) {
                                if (originalKey.equals(usingKey)) {
                                    FlylinkConfigManager.sharedInstance().start(ssid, originalKey, timeout, mContext);
                                } else {
                                    FlylinkConfigManager.sharedInstance().start(ssid, usingKey, timeout, mContext);
                                }
                            } else if (GizWifiGAgentType.GizGAgentMxchipAWS == type) {
                                MxChipAWSManager.sharedInstance().start(ssid, originalKey, timeout, mContext);
                            }
                        }
                    } else if (originalKey.equals(usingKey)) {
                        ESPTouchConfigManager.sharedInstance().start(ssid, originalKey, timeout, mContext, false);
                    } else {
                        ESPTouchConfigManager.sharedInstance().start(ssid, usingKey, timeout, mContext, false);
                    }
                }
            }

            return;
        }
    }

    private static void stopAirlink(List<GizWifiGAgentType> gagentTypes) {
        Iterator var1 = gagentTypes.iterator();

        while (true) {
            while (true) {
                GizWifiGAgentType type;
                label46:
                do {
                    while (var1.hasNext()) {
                        type = (GizWifiGAgentType) var1.next();
                        if (type != GizWifiGAgentType.GizGAgentHF && type != GizWifiGAgentType.GizGAgentHFV8) {
                            continue label46;
                        }

                        HFSnifferSmartLinker_V7.sharedInstance().stop();
                    }

                    return;
                } while (type == GizWifiGAgentType.GizGAgentRTK);

                if (type == GizWifiGAgentType.GizGAgentBL) {
                    BLEasyConfigManager.sharedInstance().stop();
                } else if (type != GizWifiGAgentType.GizGAgentESP && type != GizWifiGAgentType.GizGAgentESPBroadcast) {
                    if (type == GizWifiGAgentType.GizGAgentWM) {
                        WMOneShotConfigManager.sharedInstance().stop();
                    } else if (type == GizWifiGAgentType.GizGAgentMXCHIP3) {
                        MxChipMicoManager.sharedInstance(mContext).stop();
                    } else if (type != GizWifiGAgentType.GizGAgentFSK) {
                        if (type == GizWifiGAgentType.GizGAgentAtmelEE) {
                            AtmelEESmartConfigManager.sharedInstance().stop();
                        } else if (type == GizWifiGAgentType.GizGAgentFlyLink) {
                            FlylinkConfigManager.sharedInstance().stop();
                        } else if (type == GizWifiGAgentType.GizGAgentMxchipAWS) {
                            MxChipAWSManager.sharedInstance().stop();
                        }
                    }
                } else {
                    ESPTouchConfigManager.sharedInstance().stop();
                }
            }
        }
    }

    private boolean isConnectToSoftApSSID(String softapPrefix) {
        String wifiSSID = Utils.getWifiSSID(mContext);
        SDKLog.d("============================> current phone wifi: " + wifiSSID + ", gagent softap hotspot: " + softapPrefix);
        return wifiSSID.length() >= softapPrefix.length() && wifiSSID.substring(0, softapPrefix.length()).equals(softapPrefix);
    }

    protected void stopDeviceOnboarding() {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            int sn = this.sendStopDeviceOnboarding();
            this.makeTimer(4000, 1010, sn);
        } else {
            this.onDidSetDeviceOnboarding(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, (GizWifiDevice) null);
        }
    }

    public int sendStopDeviceOnboarding() {
        JSONObject json = new JSONObject();
        int sn = Utils.getSn();

        try {
            json.put("cmd", 1009);
            json.put("sn", sn);
        } catch (JSONException var4) {
            SDKLog.e(var4.toString());
            var4.printStackTrace();
        }

        this.sendMessage2Deamon(json);
        return sn;
    }

    protected void setDeviceOnboardingDeploy(String ssid, String key, GizWifiConfigureMode mode, String softAPSSIDPrefix, int timeout, List<GizWifiGAgentType> types, boolean bind, boolean appendDomain) {
        configureMode = mode == null ? GizWifiConfigureMode.GizWifiAirLinkMulti : mode;
        gagentTypes = Utils.removeDuplicateGAgentType(types);
        if (gagentTypes.size() == 0) {
            gagentTypes.add(GizWifiGAgentType.GizGAgentESP);
        }

        JSONArray gagentTypesBiz = new JSONArray();

        for (int i = 0; i < gagentTypes.size(); ++i) {
            try {
                GizWifiGAgentType type = (GizWifiGAgentType) gagentTypes.get(i);
                gagentTypesBiz.put(i, type.name());
            } catch (JSONException var13) {
                var13.printStackTrace();
            }
        }

        onboardingstarttime = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
        onboarding_start = System.currentTimeMillis();
        SDKLog.b("onboarding_start", "GIZ_SDK_SUCCESS", "" + Utils.changeString("gagent_type") + ": " + gagentTypesBiz + ", " + Utils.changeString("config_mode") + ": " + configureMode.name() + ", " + Utils.changeString("config_ssid") + ": " + Utils.changeString(ssid == null ? "null" : ssid) + ", " + Utils.changeString("route_encrypt_mode") + ": " + (mContext == null ? Utils.changeString("null") : Utils.changeString(Utils.getCipherType(mContext, ssid) + "")) + ", " + Utils.changeString("route_model") + ": " + (mContext == null ? Utils.changeString("null") : Utils.changeString((new WifiAdmin(mContext)).getBSSID())));
        if (!Constant.ishandshake) {
            this.onDidSetDeviceOnboarding(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, (GizWifiDevice) null);
        } else if (TextUtils.isEmpty(ssid)) {
            this.onDidSetDeviceOnboarding(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (GizWifiDevice) null);
        } else {
            if (configureMode == GizWifiConfigureMode.GizWifiSoftAP) {
                if (TextUtils.isEmpty(softAPSSIDPrefix)) {
                    this.onDidSetDeviceOnboarding(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (GizWifiDevice) null);
                    return;
                }

                if (!this.isConnectToSoftApSSID(softAPSSIDPrefix)) {
                    this.onDidSetDeviceOnboarding(GizWifiErrorCode.GIZ_SDK_PHONE_NOT_CONNECT_TO_SOFTAP_SSID, (GizWifiDevice) null);
                    return;
                }
            }

            if ((configureMode == GizWifiConfigureMode.GizWifiBleLink || configureMode == GizWifiConfigureMode.GizWifiBleLinkMulti) && TextUtils.isEmpty(softAPSSIDPrefix)) {
                this.onDidSetDeviceOnboarding(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (GizWifiDevice) null);
            } else {
                String usingKey;
                try {
                    usingKey = new String(ssid.getBytes("UTF-8"), "UTF-8");
                    onboardingSsid = usingKey;
                } catch (UnsupportedEncodingException var12) {
                    var12.printStackTrace();
                }

                onboardingTimeout = timeout < 30 ? 30 : timeout;
                originalKey = key;
                usingKey = key;
                if (appendDomain) {
                    usingKey = this.appendDomain2Onboarding(key, (String) domainInfo.get("openapi"));
                }

                SDKLog.d("device onboarding ready");
                this.sendConfigInfoToDaemon(onboardingSsid, usingKey, softAPSSIDPrefix, onboardingTimeout, gagentTypes, configureMode, bind);
                if (configureMode == GizWifiConfigureMode.GizWifiAirLink || configureMode == GizWifiConfigureMode.GizWifiAirLinkMulti) {
                    this.startAirlink(onboardingSsid, usingKey, onboardingTimeout, gagentTypes);
                }

                is5GWifi = Utils.is5GWifi(mContext, ssid);
                if (is5GWifi) {
                    onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_ONBOARDING_WIFI_IS_5G.getResult()), GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_ONBOARDING_WIFI_IS_5G.getResult()).name());
                }

            }
        }
    }

    protected void getSSIDList() {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!this.isConnectToSoftApSSID(softAPHotspotPrefix)) {
                onDidGetSSIDList(GizWifiErrorCode.GIZ_SDK_PHONE_NOT_CONNECT_TO_SOFTAP_SSID, (List) null);
            } else {
                JSONObject json = new JSONObject();
                int sn = Utils.getSn();

                try {
                    json.put("cmd", 1013);
                    json.put("sn", sn);
                } catch (JSONException var5) {
                    SDKLog.e(var5.toString());
                }

                this.sendMessage2Deamon(json);
                this.makeTimer(31000, 1014, sn);
            }
        } else {
            onDidGetSSIDList(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, (List) null);
        }
    }

    protected void getMapTab() {
        if (!Constant.ishandshake) {
            onDidGetMapTab(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
        } else {
            JSONObject json = new JSONObject();
            int sn = Utils.getSn();

            try {
                json.put("cmd", 1397);
                json.put("sn", sn);
            } catch (JSONException var4) {
                SDKLog.e(var4.toString());
            }

            this.sendMessage2Deamon(json);
            this.makeTimer(31000, 1398, sn);
        }
    }

    protected void getDeviceLog(String softAPSSIDPrefix) {
        if (!Constant.ishandshake) {
            onDidReceiveDeviceLog(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, (String) null, 0, 0, (String) null);
        } else if (!Utils.isWifiConnect(mContext) || !TextUtils.isEmpty(softAPSSIDPrefix) && !this.isConnectToSoftApSSID(softAPSSIDPrefix)) {
            onDidReceiveDeviceLog(GizWifiErrorCode.GIZ_SDK_PHONE_NOT_CONNECT_TO_SOFTAP_SSID, (String) null, 0, 0, (String) null);
        } else {
            JSONObject json = new JSONObject();
            int sn = Utils.getSn();
            String ip = Utils.getWifiIP(mContext);

            try {
                json.put("cmd", 1391);
                json.put("sn", sn);
                if (ip != null) {
                    json.put("ip", ip);
                }
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
            }

            this.sendMessage2Deamon(json);
            this.makeTimer(31000, 1392, sn);
        }
    }

    protected void deviceSafetyRegister(GizWifiDevice gateway, String productKey, List<ConcurrentHashMap<String, String>> devicesInfo) {
        if (!Constant.ishandshake) {
            SDKLog.d("handshake did not done");
            onDidDeviceSafetyRegister((List) null, (List) null);
        } else if (devicesInfo != null && devicesInfo.size() != 0) {
            JSONObject objSend = new JSONObject();
            int sn = Utils.getSn();

            try {
                objSend.put("cmd", 1121);
                objSend.put("sn", sn);
                if (gateway != null) {
                    objSend.put("gatewayDID", gateway.getDid());
                }

                if (!TextUtils.isEmpty(productKey)) {
                    objSend.put("productKey", productKey);
                }

                JSONArray array = new JSONArray();
                Iterator var7 = devicesInfo.iterator();

                while (var7.hasNext()) {
                    ConcurrentHashMap<String, String> info = (ConcurrentHashMap) var7.next();
                    JSONObject obj = new JSONObject();
                    if (info.containsKey("mac")) {
                        obj.put("mac", info.get("mac"));
                    }

                    if (info.containsKey("meshID")) {
                        obj.put("meshID", info.get("meshID"));
                    }

                    if (info.containsKey("alias")) {
                        obj.put("alias", info.get("alias"));
                    }

                    if (info.containsKey("authCode")) {
                        obj.put("authCode", info.get("authCode"));
                    }

                    if (obj.length() > 0) {
                        array.put(obj);
                    }
                }

                if (array.length() > 0) {
                    objSend.put("devicesInfo", array);
                }
            } catch (JSONException var10) {
                SDKLog.e(var10.toString());
                var10.printStackTrace();
            }

            this.sendMessage2Deamon(objSend);
            this.makeTimer(31000, 1122, sn);
        } else {
            SDKLog.d("devicesInfo is empty");
            onDidDeviceSafetyRegister((List) null, (List) null);
        }
    }

    protected void deviceSafetyUnbind(List<ConcurrentHashMap<String, Object>> devicesInfo) {
        if (!Constant.ishandshake) {
            SDKLog.d("handshake did not done");
            onDidDeviceSafetyUnbind((List) null);
        } else if (devicesInfo != null && devicesInfo.size() != 0) {
            JSONObject objSend = new JSONObject();
            int sn = Utils.getSn();

            try {
                objSend.put("cmd", 1123);
                objSend.put("sn", sn);
                JSONArray array = new JSONArray();
                Iterator var5 = devicesInfo.iterator();

                while (var5.hasNext()) {
                    ConcurrentHashMap<String, Object> info = (ConcurrentHashMap) var5.next();
                    JSONObject obj = new JSONObject();
                    if (info.containsKey("device")) {
                        GizWifiDevice device = (GizWifiDevice) info.get("device");
                        obj.put("mac", device.getMacAddress());
                        obj.put("productKey", device.getProductKey());
                        if (TextUtils.isEmpty(device.getDid())) {
                            obj.put("did", device.getDid());
                        }
                    }

                    if (info.containsKey("authCode")) {
                        obj.put("authCode", info.get("authCode"));
                    }

                    if (obj.length() > 0) {
                        array.put(obj);
                    }
                }

                if (array.length() > 0) {
                    objSend.put("devicesInfo", array);
                }
            } catch (JSONException var9) {
                SDKLog.e(var9.toString());
                var9.printStackTrace();
            }

            this.sendMessage2Deamon(objSend);
            this.makeTimer(31000, 1124, sn);
        } else {
            SDKLog.d("devicesInfo is empty");
            onDidDeviceSafetyUnbind((List) null);
        }
    }

    protected void shareLogFile() {
        if (mContext != null) {
            String srcPath =mContext.getExternalFilesDir(null).getAbsolutePath() + "/GizWifiSDK/" + mContext.getPackageName();
            String zipPath = srcPath + ".zip";
            File zipfile = new File(zipPath);
            File srcFile = new File(srcPath);
            if (zipfile.exists()) {
                zipfile.delete();
            }

            try {
                zipfile.createNewFile();
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipfile));
                ZipFiles(srcFile.getParent() + File.separator, srcFile.getName(), zipOutputStream);
                zipOutputStream.finish();
                zipOutputStream.close();
            } catch (Exception var6) {
                var6.printStackTrace();
            }

            Intent sendIntent = new Intent();
            sendIntent.setAction("android.intent.action.SEND");
            sendIntent.putExtra("android.intent.extra.STREAM", this.getFileContentUri(mContext, zipfile));
            sendIntent.setType("*/*");
            mContext.getApplicationContext().startActivity(Intent.createChooser(sendIntent, "Share to..."));
        }
    }

    private Uri getFileContentUri(Context context, File file) {
        String volumeName = "external";
        String filePath = file.getAbsolutePath();
        String[] projection = new String[]{"_id"};
        Uri uri = null;
        Cursor cursor = context.getContentResolver().query(Files.getContentUri(volumeName), projection, "_data=? ", new String[]{filePath}, (String) null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                uri = Files.getContentUri(volumeName, (long) id);
            }

            cursor.close();
        }

        return uri;
    }

    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam != null && mContext != null) {
            File file = new File(folderString + fileString);
            if (file.isFile()) {
                SDKLog.e("file" + fileString);
                String srcPath = mContext.getExternalFilesDir(null).getAbsolutePath() + "/GizWifiSDK/";
                String zipFile = folderString.replace(srcPath, "");
                ZipEntry zipEntry = new ZipEntry(zipFile + fileString);
                FileInputStream inputStream = new FileInputStream(file);
                zipOutputSteam.putNextEntry(zipEntry);
                byte[] buffer = new byte[4096];

                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    zipOutputSteam.write(buffer, 0, len);
                }

                zipOutputSteam.closeEntry();
            } else {
                String[] fileList = file.list();

                for (int i = 0; i < fileList.length; ++i) {
                    ZipFiles(folderString + fileString + "/", fileList[i], zipOutputSteam);
                }
            }

        }
    }

    protected void searchBTDevice(final String meshName, int timeout) {
        timeout = timeout == 0 ? 4 : timeout;
        this.meshDeviceList = new ArrayList();
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            onDidDiscoveredMeshDevices(GizWifiErrorCode.GIZ_SDK_BLE_BLUETOOTH_FUNCTION_NOT_TURNED_ON, this.meshDeviceList);
        } else {
            SDKLog.d("searchBTDevice_start");
            getmeshtime = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
            if (VERSION.SDK_INT >= 18) {
                final LeScanCallback mLeScanCallback = new LeScanCallback() {
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        SDKLog.d(device.getName() + "-->" + Utils.bytesToHexString(scanRecord, ":"));
                        int length = scanRecord.length;
                        int packetPosition = 0;
                        byte[] name = null;
                        int rspData = 0;

                        while (true) {
                            if (packetPosition < length) {
                                int packetSize = scanRecord[packetPosition];
                                if (packetSize != 0) {
                                    int position = packetPosition + 1;
                                    int type = scanRecord[position] & 255;
                                    ++position;
                                    if (type == 9) {
                                        int packetContentLength = packetSize - 1;
                                        if (packetContentLength > 16 || packetContentLength <= 0) {
                                            return;
                                        }

                                        name = new byte[16];
                                        System.arraycopy(scanRecord, position, name, 0, packetContentLength);
                                    } else if (type == 255) {
                                        ++rspData;
                                        if (rspData == 2) {
                                            int vendorId = (scanRecord[position++] & 255) + ((scanRecord[position++] & 255) << 8);
                                            int meshUUID = (scanRecord[position++] & 255) + ((scanRecord[position++] & 255) << 8);
                                            position += 4;
                                            int productUUID = (scanRecord[position++] & 255) + ((scanRecord[position++] & 255) << 8);
                                            int status = scanRecord[position++] & 255;
                                            int meshAddress = (scanRecord[position++] & 255) + ((scanRecord[position] & 255) << 8);
                                            if (name != null) {
                                                String meshNamestr = (new String(name, Charset.defaultCharset())).trim();
                                                SDKLog.e("meshName:" + meshNamestr);
                                                if (meshName == null || meshName.equals("") || meshNamestr.equals(meshName)) {
                                                    ConcurrentHashMap<String, String> meshMap = new ConcurrentHashMap();
                                                    meshMap.put("mac", device.getAddress().replace(":", ""));
                                                    meshMap.put("meshID", String.format("%02x", meshAddress));
                                                    meshMap.put("advData", Utils.bytesToHexString(scanRecord, ""));
                                                    meshMap.put("meshName", meshNamestr);
                                                    if (!SDKEventManager.this.meshDeviceList.contains(meshMap)) {
                                                        SDKEventManager.this.meshDeviceList.add(meshMap);
                                                        SDKEventManager.onDidDiscoveredMeshDevices(GizWifiErrorCode.GIZ_SDK_SUCCESS, SDKEventManager.this.meshDeviceList);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    packetPosition += packetSize + 1;
                                    continue;
                                }
                            }

                            return;
                        }
                    }
                };
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                (new Handler()).postDelayed(new Runnable() {
                    public void run() {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        SDKEventManager.onDidDiscoveredMeshDevices(GizWifiErrorCode.GIZ_SDK_BLE_SEARCH_DEVICE_STOPPED, SDKEventManager.this.meshDeviceList);
                    }
                }, (long) (timeout * 1000));
            }

        }
    }

    protected void setUserMeshName(String userMeshName, String userPassword, ConcurrentHashMap<String, String> uuidInfo, byte[] meshLTK, GizMeshVendor gizMeshVendor) {
        SDKEventManager.userMeshName = null;
        SDKEventManager.userPassword = null;
        SDKEventManager.serviceUUID = null;
        SDKEventManager.pairUUID = null;
        SDKEventManager.commandUUID = null;
        SDKEventManager.notifyUUID = null;
        this.meshLTK = null;
        SDKEventManager.gizMeshVendor = gizMeshVendor;
        if (userMeshName != null && userPassword != null) {
            if (gizMeshVendor == GizMeshVendor.GizMeshJingxun) {
                if (userMeshName.length() >= 1 && userMeshName.length() <= 16 && userPassword.length() >= 1 && userPassword.length() <= 8) {
                    SDKEventManager.userMeshName = userMeshName;
                    SDKEventManager.userPassword = userPassword;
                }
            } else if (userMeshName.length() >= 1 && userMeshName.length() <= 16 && userPassword.length() >= 1 && userPassword.length() <= 16) {
                SDKEventManager.userMeshName = userMeshName;
                SDKEventManager.userPassword = userPassword;
            }
        }

        if (uuidInfo != null) {
            String serviceUUID = (String) uuidInfo.get("serviceUUID");
            String pairUUID = (String) uuidInfo.get("pairUUID");
            String commandUUID = (String) uuidInfo.get("commandUUID");
            String notifyUUID = (String) uuidInfo.get("notifyUUID");
            if (serviceUUID != null && pairUUID != null && commandUUID != null && notifyUUID != null) {
                SDKEventManager.serviceUUID = serviceUUID;
                SDKEventManager.pairUUID = pairUUID;
                SDKEventManager.commandUUID = commandUUID;
                SDKEventManager.notifyUUID = notifyUUID;
            }
        }

        GizMeshLocalControlCenter.sharedInstance().setMeshInfo(mContext, SDKEventManager.userMeshName, SDKEventManager.userPassword, SDKEventManager.pairUUID, SDKEventManager.commandUUID, SDKEventManager.notifyUUID);
        this.meshLTK = meshLTK;
    }

    protected void changeDeviceMesh(ConcurrentHashMap<String, String> meshDeviceInfo, ConcurrentHashMap<String, String> currentMesh, int newMeshID) {
        if (meshDeviceInfo != null && currentMesh != null && meshDeviceInfo.size() != 0 && currentMesh.size() != 0) {
            if (CurrentMsg != -1) {
                this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_BLE_HAS_CONFLICT_OPERATION_IS_ONGOING, (ConcurrentHashMap) null);
            } else {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_BLE_BLUETOOTH_FUNCTION_NOT_TURNED_ON, (ConcurrentHashMap) null);
                } else {
                    String currentMeshName = (String) currentMesh.get("meshName");
                    String currentPassword = (String) currentMesh.get("password");
                    if (currentMeshName != null && currentPassword != null) {
                        if (gizMeshVendor == GizMeshVendor.GizMeshJingxun) {
                            if (currentMeshName.length() < 1 || currentMeshName.length() > 16 || currentPassword.length() < 1 || currentPassword.length() > 8) {
                                this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (ConcurrentHashMap) null);
                                return;
                            }
                        } else if (currentMeshName.length() < 1 || currentMeshName.length() > 16 || currentPassword.length() < 1 || currentPassword.length() > 16) {
                            this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (ConcurrentHashMap) null);
                            return;
                        }

                        if (userMeshName != null && userPassword != null) {
                            if (serviceUUID != null && pairUUID != null && commandUUID != null && notifyUUID != null) {
                                if (this.meshLTK == null) {
                                    this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_BLE_PARAM_LTK_REQUIRED, (ConcurrentHashMap) null);
                                } else {
                                    GizMeshLocalControlCenter.sharedInstance().stopAllAction();
                                    this.loginMeshName = currentMeshName;
                                    this.loginPassword = currentPassword;
                                    this.currentMeshInfo = meshDeviceInfo;
                                    this.newMeshID = newMeshID;
                                    MeshAction = 3;
                                    CurrentMsg = 0;
                                    if (this.hasBtDevice((String) this.currentMeshInfo.get("mac"), this.loginMeshName, (String) this.currentMeshInfo.get("meshID")) != null) {
                                        SDKLog.e("find bt device and start connect , mac:" + (String) this.currentMeshInfo.get("mac"));
                                        this.resetMeshDeviceHandler.sendEmptyMessage(1);
                                        this.resetMeshDeviceHandler.sendEmptyMessageDelayed(12, 30000L);
                                    } else {
                                        this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_BLE_UNFIND_DEVICE_PERIPHERAL, (ConcurrentHashMap) null);
                                    }

                                }
                            } else {
                                this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_BLE_PARAM_UUID_INFO_REQUIRED, (ConcurrentHashMap) null);
                            }
                        } else {
                            this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_BLE_CURRENT_USER_MESHNAME_OR_PASSWORD_INVALID, (ConcurrentHashMap) null);
                        }
                    } else {
                        this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (ConcurrentHashMap) null);
                    }
                }
            }
        } else {
            this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (ConcurrentHashMap) null);
        }
    }

    protected void addGroupByDevice(int groupID, List<GizWifiDevice> devices) {
        List<String> macs = new ArrayList();
        if (devices != null && devices.size() != 0) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                this.onDidAddDevicesToGroup(GizWifiErrorCode.GIZ_SDK_BLE_BLUETOOTH_FUNCTION_NOT_TURNED_ON, (List) null);
            } else {
                for (int i = 0; i < devices.size(); ++i) {
                    macs.add(((GizWifiDevice) devices.get(i)).getMacAddress());
                }

                this.addGroup(groupID, macs);
            }
        } else {
            this.onDidAddDevicesToGroup(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (List) null);
        }
    }

    protected void deleteGroupByDevice(int groupID, List<GizWifiDevice> devices) {
        List<String> macs = new ArrayList();
        if (devices != null && devices.size() != 0) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                this.onDidDeleteDevicesFromGroup(GizWifiErrorCode.GIZ_SDK_BLE_BLUETOOTH_FUNCTION_NOT_TURNED_ON, (List) null);
            } else {
                for (int i = 0; i < devices.size(); ++i) {
                    macs.add(((GizWifiDevice) devices.get(i)).getMacAddress());
                }

                this.deleteGroup(groupID, macs);
            }
        } else {
            this.onDidDeleteDevicesFromGroup(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (List) null);
        }
    }

    protected void updateGroupWitshDevice(GizWifiDevice device) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            this.onDidUpdateGroupWithDevice(GizWifiErrorCode.GIZ_SDK_BLE_BLUETOOTH_FUNCTION_NOT_TURNED_ON, (List) null, (GizWifiDevice) null);
        } else if (device == null) {
            this.onDidUpdateGroupWithDevice(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (List) null, (GizWifiDevice) null);
        } else {
            this.updateGroup(device);
        }
    }

    protected void restoreDeviceFactory(GizLiteGWSubDevice device) {
        if (CurrentMsg != -1) {
            this.onDidRestoreDeviceFactorySetting(GizWifiErrorCode.GIZ_SDK_BLE_HAS_CONFLICT_OPERATION_IS_ONGOING, (String) null);
        } else {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {
                this.onDidRestoreDeviceFactorySetting(GizWifiErrorCode.GIZ_SDK_BLE_BLUETOOTH_FUNCTION_NOT_TURNED_ON, (String) null);
            } else if (userMeshName != null && userPassword != null) {
                if (serviceUUID != null && pairUUID != null && commandUUID != null && notifyUUID != null) {
                    if (this.meshLTK == null) {
                        this.onDidRestoreDeviceFactorySetting(GizWifiErrorCode.GIZ_SDK_BLE_PARAM_LTK_REQUIRED, (String) null);
                    } else if (device == null) {
                        this.onDidRestoreDeviceFactorySetting(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (String) null);
                    } else {
                        final String mac = device.getMacAddress();
                        MeshAction = 7;
                        this.searchBTDevice(userMeshName, 3000);
                        CurrentMsg = 0;
                        GizMeshLocalControlCenter.sharedInstance().stopAllAction();
                        (new Handler()).postDelayed(new Runnable() {
                            public void run() {
                                ConcurrentHashMap<String, String> meshDevice = SDKEventManager.this.hasBtDevice(mac);
                                if (meshDevice != null) {
                                    SDKEventManager.this.currentMeshInfo = meshDevice;
                                    SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessage(1);
                                    SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessageDelayed(12, 10000L);
                                } else {
                                    SDKEventManager.this.onDidRestoreDeviceFactorySetting(GizWifiErrorCode.GIZ_SDK_BLE_UNFIND_DEVICE_PERIPHERAL, (String) null);
                                }

                            }
                        }, 3000L);
                    }
                } else {
                    this.onDidRestoreDeviceFactorySetting(GizWifiErrorCode.GIZ_SDK_BLE_PARAM_UUID_INFO_REQUIRED, (String) null);
                }
            } else {
                this.onDidRestoreDeviceFactorySetting(GizWifiErrorCode.GIZ_SDK_BLE_CURRENT_USER_MESHNAME_OR_PASSWORD_INVALID, (String) null);
            }
        }
    }

    protected void addGroup(int groupID, final List<String> macs) {
        if (CurrentMsg != -1) {
            this.onDidAddDevicesToGroup(GizWifiErrorCode.GIZ_SDK_BLE_HAS_CONFLICT_OPERATION_IS_ONGOING, (List) null);
        } else if (userMeshName != null && userPassword != null) {
            if (serviceUUID != null && pairUUID != null && commandUUID != null && notifyUUID != null) {
                if (this.meshLTK == null) {
                    this.onDidAddDevicesToGroup(GizWifiErrorCode.GIZ_SDK_BLE_PARAM_LTK_REQUIRED, (List) null);
                } else if (macs != null && macs.size() != 0 && groupID > 0 && groupID < 255) {
                    MeshAction = 4;
                    this.addGroupID = groupID;
                    this.searchBTDevice(userMeshName, 3000);
                    CurrentMsg = 0;
                    GizMeshLocalControlCenter.sharedInstance().stopAllAction();
                    (new Handler()).postDelayed(new Runnable() {
                        public void run() {
                            SDKEventManager.this.addGroupMacList = new ArrayList();

                            for (int i = 0; i < macs.size(); ++i) {
                                ConcurrentHashMap<String, String> meshDevice = SDKEventManager.this.hasBtDevice((String) macs.get(i));
                                if (meshDevice != null) {
                                    SDKEventManager.this.addGroupMacList.add(meshDevice);
                                }
                            }

                            if (SDKEventManager.this.addGroupMacList.size() != 0) {
                                SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessage(1);
                                SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessageDelayed(12, 10000L);
                            } else {
                                SDKEventManager.this.onDidAddDevicesToGroup(GizWifiErrorCode.GIZ_SDK_BLE_UNFIND_DEVICE_PERIPHERAL, (List) null);
                            }

                        }
                    }, 3000L);
                } else {
                    this.onDidAddDevicesToGroup(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (List) null);
                }
            } else {
                this.onDidAddDevicesToGroup(GizWifiErrorCode.GIZ_SDK_BLE_PARAM_UUID_INFO_REQUIRED, (List) null);
            }
        } else {
            this.onDidAddDevicesToGroup(GizWifiErrorCode.GIZ_SDK_BLE_CURRENT_USER_MESHNAME_OR_PASSWORD_INVALID, (List) null);
        }
    }

    protected void updateGroup(final GizWifiDevice gizWifiDevice) {
        if (CurrentMsg != -1) {
            this.onDidUpdateGroupWithDevice(GizWifiErrorCode.GIZ_SDK_BLE_HAS_CONFLICT_OPERATION_IS_ONGOING, (List) null, (GizWifiDevice) null);
        } else if (userMeshName != null && userPassword != null) {
            if (serviceUUID != null && pairUUID != null && commandUUID != null && notifyUUID != null) {
                if (this.meshLTK == null) {
                    this.onDidUpdateGroupWithDevice(GizWifiErrorCode.GIZ_SDK_BLE_PARAM_LTK_REQUIRED, (List) null, (GizWifiDevice) null);
                } else {
                    MeshAction = 10;
                    this.searchBTDevice(userMeshName, 3000);
                    CurrentMsg = 0;
                    GizMeshLocalControlCenter.sharedInstance().stopAllAction();
                    (new Handler()).postDelayed(new Runnable() {
                        public void run() {
                            SDKEventManager.this.updateMac = null;
                            ConcurrentHashMap<String, String> meshDevice = SDKEventManager.this.hasBtDevice(gizWifiDevice.getMacAddress());
                            if (meshDevice != null) {
                                SDKEventManager.this.updateMac = meshDevice;
                            }

                            if (SDKEventManager.this.updateMac != null) {
                                SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessage(1);
                                SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessageDelayed(12, 10000L);
                            } else {
                                SDKEventManager.this.onDidUpdateGroupWithDevice(GizWifiErrorCode.GIZ_SDK_BLE_UNFIND_DEVICE_PERIPHERAL, (List) null, (GizWifiDevice) null);
                            }

                        }
                    }, 3000L);
                }
            } else {
                this.onDidUpdateGroupWithDevice(GizWifiErrorCode.GIZ_SDK_BLE_PARAM_UUID_INFO_REQUIRED, (List) null, (GizWifiDevice) null);
            }
        } else {
            this.onDidUpdateGroupWithDevice(GizWifiErrorCode.GIZ_SDK_BLE_CURRENT_USER_MESHNAME_OR_PASSWORD_INVALID, (List) null, (GizWifiDevice) null);
        }
    }

    public void deleteGroup(int groupID, final List<String> macs) {
        if (CurrentMsg != -1) {
            this.onDidDeleteDevicesFromGroup(GizWifiErrorCode.GIZ_SDK_BLE_HAS_CONFLICT_OPERATION_IS_ONGOING, (List) null);
        } else if (userMeshName != null && userPassword != null) {
            if (serviceUUID != null && pairUUID != null && commandUUID != null && notifyUUID != null) {
                if (this.meshLTK == null) {
                    this.onDidDeleteDevicesFromGroup(GizWifiErrorCode.GIZ_SDK_BLE_PARAM_LTK_REQUIRED, (List) null);
                } else if (macs != null && macs.size() != 0 && groupID > 0 && groupID < 255) {
                    MeshAction = 9;
                    this.deleteGroupID = groupID;
                    this.searchBTDevice(userMeshName, 3000);
                    CurrentMsg = 0;
                    GizMeshLocalControlCenter.sharedInstance().stopAllAction();
                    (new Handler()).postDelayed(new Runnable() {
                        public void run() {
                            SDKEventManager.this.deleteGroupMacList = new ArrayList();

                            for (int i = 0; i < macs.size(); ++i) {
                                ConcurrentHashMap<String, String> meshDevice = SDKEventManager.this.hasBtDevice((String) macs.get(i));
                                if (meshDevice != null) {
                                    SDKEventManager.this.deleteGroupMacList.add(meshDevice);
                                }
                            }

                            if (SDKEventManager.this.deleteGroupMacList.size() != 0) {
                                SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessage(1);
                                SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessageDelayed(12, 10000L);
                            } else {
                                SDKEventManager.this.onDidDeleteDevicesFromGroup(GizWifiErrorCode.GIZ_SDK_BLE_UNFIND_DEVICE_PERIPHERAL, (List) null);
                            }

                        }
                    }, 3000L);
                } else {
                    this.onDidDeleteDevicesFromGroup(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (List) null);
                }
            } else {
                this.onDidDeleteDevicesFromGroup(GizWifiErrorCode.GIZ_SDK_BLE_PARAM_UUID_INFO_REQUIRED, (List) null);
            }
        } else {
            this.onDidDeleteDevicesFromGroup(GizWifiErrorCode.GIZ_SDK_BLE_CURRENT_USER_MESHNAME_OR_PASSWORD_INVALID, (List) null);
        }
    }

    public ConcurrentHashMap<String, String> hasBtDevice(String mac) {
        if (this.meshDeviceList != null) {
            for (int i = 0; i < this.meshDeviceList.size(); ++i) {
                if (((String) ((ConcurrentHashMap) this.meshDeviceList.get(i)).get("mac")).toLowerCase().equals(mac.toLowerCase())) {
                    return (ConcurrentHashMap) this.meshDeviceList.get(i);
                }
            }
        }

        return null;
    }

    public ConcurrentHashMap<String, String> hasBtDevice(String mac, String name, String meshId) {
        if (this.meshDeviceList != null) {
            for (int i = 0; i < this.meshDeviceList.size(); ++i) {
                if (((String) ((ConcurrentHashMap) this.meshDeviceList.get(i)).get("mac")).toLowerCase().equals(mac.toLowerCase()) && ((String) ((ConcurrentHashMap) this.meshDeviceList.get(i)).get("meshName")).equals(name) && ((String) ((ConcurrentHashMap) this.meshDeviceList.get(i)).get("meshID")).equals(meshId)) {
                    return (ConcurrentHashMap) this.meshDeviceList.get(i);
                }
            }
        }

        return null;
    }

    private GizWifiDevice hasDeleteGroupSuccess(byte[] result) {
        String mac = "";
        List<Integer> groupIds = new ArrayList();

        int i;
        for (i = 10; i < result.length; ++i) {
            groupIds.add(result[i] & 255);
        }

        for (i = 0; i < this.deleteGroupMacList.size(); ++i) {
            int meshId = Integer.valueOf((String) ((ConcurrentHashMap) this.deleteGroupMacList.get(i)).get("meshID"), 16);
            if (meshId == (result[3] & 255) && !groupIds.contains(this.deleteGroupID & 255)) {
                mac = (String) ((ConcurrentHashMap) this.deleteGroupMacList.get(i)).get("mac");
            }
        }

        for (i = 0; i < deviceList.size(); ++i) {
            if (((GizWifiDevice) deviceList.get(i)).getMacAddress().equals(mac)) {
                return (GizWifiDevice) deviceList.get(i);
            }
        }

        return null;
    }

    private GizWifiDevice hasChangeGroupSuccess(byte[] result) {
        String mac = "";
        List<Integer> groupIds = new ArrayList();

        int i;
        for (i = 10; i < result.length; ++i) {
            groupIds.add(result[i] & 255);
        }

        for (i = 0; i < this.addGroupMacList.size(); ++i) {
            int meshId = Integer.valueOf((String) ((ConcurrentHashMap) this.addGroupMacList.get(i)).get("meshID"), 16);
            if (meshId == (result[3] & 255) && groupIds.contains(this.addGroupID & 255)) {
                mac = (String) ((ConcurrentHashMap) this.addGroupMacList.get(i)).get("mac");
            }
        }

        for (i = 0; i < deviceList.size(); ++i) {
            if (((GizWifiDevice) deviceList.get(i)).getMacAddress().equals(mac)) {
                return (GizWifiDevice) deviceList.get(i);
            }
        }

        return null;
    }

    private byte[] getSessionKey(byte[] meshName, byte[] password, byte[] randm, byte[] rands, byte[] sk) throws Exception {
        byte[] key = new byte[16];
        System.arraycopy(rands, 0, key, 0, rands.length);
        byte[] plaintext = new byte[16];

        for (int i = 0; i < 16; ++i) {
            plaintext[i] = (byte) (meshName[i] ^ password[i]);
        }

        byte[] encrypted = AES.encrypt(key, plaintext);
        byte[] result = new byte[16];
        System.arraycopy(rands, 0, result, 0, rands.length);
        System.arraycopy(encrypted, 8, result, 8, 8);
        Utils.reverse(result, 8, 15);
        if (!Arrays.equals(result, sk)) {
            return null;
        } else {
            System.arraycopy(randm, 0, key, 0, randm.length);
            System.arraycopy(rands, 0, key, 8, rands.length);
            byte[] sessionKey = AES.encrypt(plaintext, key);
            Utils.reverse(sessionKey, 0, sessionKey.length - 1);
            return sessionKey;
        }
    }

    private byte[] getSecIVS(byte[] macAddress) {
        byte[] ivs = new byte[8];
        ivs[0] = macAddress[0];
        ivs[1] = macAddress[1];
        ivs[2] = macAddress[2];
        return ivs;
    }

    public void connectDevice() {
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) mContext.getSystemService("bluetooth");
            this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        }

        if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            this.mBluetoothGatt.connect();
        } else {
            String mac = "";
            if (MeshAction == 4) {
                mac = (String) ((ConcurrentHashMap) this.addGroupMacList.get(0)).get("mac");
            } else if (MeshAction == 9) {
                mac = (String) ((ConcurrentHashMap) this.deleteGroupMacList.get(0)).get("mac");
            } else if (MeshAction == 10) {
                mac = (String) this.updateMac.get("mac");
            } else if (MeshAction == 3 || MeshAction == 7) {
                mac = (String) this.currentMeshInfo.get("mac");
            }

            if (!mac.contains(":")) {
                String tempMac = "";

                for (int i = 0; i < mac.length() / 2; ++i) {
                    tempMac = tempMac + (i == 0 ? "" : ":") + mac.substring(i * 2, (i + 1) * 2);
                }

                mac = tempMac;
            }

            BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(mac.toUpperCase());
            if (device == null) {
                this.onDidChangeDeviceMesh(GizWifiErrorCode.GIZ_SDK_BLE_UNFIND_DEVICE_PERIPHERAL, (ConcurrentHashMap) null);
            } else {
                SDKLog.e("start connect bt device");
                this.mBluetoothGatt = device.connectGatt(mContext, false, this.mGattCallback);
            }
        }
    }

    protected void loginDevice() {
        byte[] meshName = new byte[16];
        byte[] password = new byte[16];
        byte[] commandData = new byte[17];
        if (MeshAction == 3) {
            meshName = Utils.stringToBytes(this.loginMeshName, 16);
            password = Utils.stringToBytes(this.loginPassword, 16);
        } else {
            meshName = Utils.stringToBytes(userMeshName, 16);
            password = Utils.stringToBytes(userPassword, 16);
        }

        byte[] plaintext = new byte[16];

        for (int i = 0; i < 16; ++i) {
            plaintext[i] = (byte) (meshName[i] ^ password[i]);
        }

        byte[] sk = new byte[16];
        byte[] randm = this.generateRandom(this.loginRandm);
        System.arraycopy(randm, 0, sk, 0, randm.length);

        byte[] encrypted;
        try {
            encrypted = AES.encrypt(sk, plaintext);
        } catch (Exception var9) {
            return;
        }

        commandData[0] = 12;
        System.arraycopy(randm, 0, commandData, 1, randm.length);
        System.arraycopy(encrypted, 8, commandData, 9, 8);
        Utils.reverse(commandData, 9, 16);
        SDKLog.e("start login mesh device");
        this.loginGattCharacteristic.setValue(commandData);
        this.loginGattCharacteristic.setWriteType(2);
        this.mBluetoothGatt.writeCharacteristic(this.loginGattCharacteristic);
    }

    private void resetMeshGroupID(int position, boolean isDelete) {
        int meshAddress = Integer.valueOf(isDelete ? (String) ((ConcurrentHashMap) this.deleteGroupMacList.get(position)).get("meshID") : (String) ((ConcurrentHashMap) this.addGroupMacList.get(position)).get("meshID"), 16);
        byte[] macAddress = this.getByteByMacAddress(isDelete ? (String) ((ConcurrentHashMap) this.deleteGroupMacList.get(position)).get("mac") : (String) ((ConcurrentHashMap) this.addGroupMacList.get(0)).get("mac"));
        this.currentGroupMac = macAddress;
        int sn = (new Random()).nextInt(16777215);
        byte[] nonce = this.getSecIVM(macAddress, sn);
        byte[] command = new byte[20];
        int offset = 0;
        command[offset] = (byte) (sn & 255);
        command[offset++] = (byte) (sn >> 8 & 255);
        command[offset++] = (byte) (sn >> 16 & 255);
        command[offset++] = 0;
        command[offset++] = 0;
        command[offset++] = (byte) (meshAddress & 255);
        command[offset++] = (byte) (meshAddress >> 8 & 255);
        command[offset++] = -41;
        Manufacture manufacture = Manufacture.getDefault();
        int vendorId = manufacture.getVendorId();
        command[offset++] = (byte) (vendorId & 255);
        command[offset++] = (byte) (vendorId >> 8 & 255);
        command[offset++] = (byte) (isDelete ? 0 : 1);
        byte[] params = isDelete ? new byte[]{(byte) (this.deleteGroupID & 255), -128} : new byte[]{(byte) (this.addGroupID & 255), -128};
        System.arraycopy(params, 0, command, offset, params.length);
        if (isDelete) {
            SDKLog.e("meshDevice:" + Utils.bytesToHexString(macAddress, " ") + "delete groupID:" + this.deleteGroupID + " with command:" + Utils.bytesToHexString(command, " "));
        } else {
            SDKLog.e("meshDevice:" + Utils.bytesToHexString(macAddress, " ") + "set groupID:" + this.addGroupID + " with command:" + Utils.bytesToHexString(command, " "));
        }

        byte[] data = AES.encrypt(this.sessionKey, nonce, command);
        this.commandGattCharacteristic.setValue(data);
        this.commandGattCharacteristic.setWriteType(2);
        this.mBluetoothGatt.writeCharacteristic(this.commandGattCharacteristic);
        this.mBluetoothGatt.setCharacteristicNotification(this.notifyGattCharacteristic, true);
        if (position == 0) {
            if (isDelete) {
                this.deleteGroupMacSuccessList = new ArrayList();
                (new Handler()).postDelayed(new Runnable() {
                    public void run() {
                        SDKEventManager.this.onDidDeleteDevicesFromGroup(GizWifiErrorCode.GIZ_SDK_SUCCESS, SDKEventManager.this.deleteGroupMacSuccessList);
                    }
                }, (long) (this.deleteGroupMacList.size() * 500));
            } else {
                this.addGroupMacSuccessList = new ArrayList();
                (new Handler()).postDelayed(new Runnable() {
                    public void run() {
                        SDKEventManager.this.onDidAddDevicesToGroup(GizWifiErrorCode.GIZ_SDK_SUCCESS, SDKEventManager.this.addGroupMacSuccessList);
                    }
                }, (long) (this.addGroupMacList.size() * 500));
            }
        }

        ++position;
        int size = isDelete ? this.deleteGroupMacList.size() : this.addGroupMacList.size();
        if (position < size) {
            Message msg = new Message();
            msg.what = isDelete ? 9 : 4;
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            msg.setData(bundle);
            this.resetMeshDeviceHandler.sendMessageDelayed(msg, 250L);
        }

    }

    private void getGroupID() {
        int meshAddress = Integer.valueOf((String) this.updateMac.get("meshID"), 16);
        byte[] macAddress = this.getByteByMacAddress((String) this.updateMac.get("mac"));
        this.currentGroupMac = macAddress;
        int sn = (new Random()).nextInt(16777215);
        byte[] nonce = this.getSecIVM(macAddress, sn);
        byte[] command = new byte[20];
        int offset = 0;
        command[offset] = (byte) (sn & 255);
        command[offset++] = (byte) (sn >> 8 & 255);
        command[offset++] = (byte) (sn >> 16 & 255);
        command[offset++] = 0;
        command[offset++] = 0;
        command[offset++] = (byte) (meshAddress & 255);
        command[offset++] = (byte) (meshAddress >> 8 & 255);
        command[offset++] = -35;
        Manufacture manufacture = Manufacture.getDefault();
        int vendorId = manufacture.getVendorId();
        command[offset++] = (byte) (vendorId & 255);
        command[offset++] = (byte) (vendorId >> 8 & 255);
        command[offset++] = 16;
        command[offset++] = 1;
        SDKLog.e("meshDevice:" + Utils.bytesToHexString(macAddress, " ") + " with command:" + Utils.bytesToHexString(command, " "));
        byte[] data = AES.encrypt(this.sessionKey, nonce, command);
        this.commandGattCharacteristic.setValue(data);
        this.commandGattCharacteristic.setWriteType(2);
        this.mBluetoothGatt.writeCharacteristic(this.commandGattCharacteristic);
        this.mBluetoothGatt.setCharacteristicNotification(this.notifyGattCharacteristic, true);
    }

    private void resetAllMeshStatus() {
        CurrentMsg = -1;
        this.sessionKey = new byte[0];
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }

        this.resetMeshDeviceHandler.removeMessages(12);
        GizMeshLocalControlCenter.sharedInstance().resetAction();
    }

    private byte[] generateRandom(byte[] randm) {
        this.random.nextBytes(randm);
        return randm;
    }

    private void resetMeshName() {
        byte[] nn;
        byte[] pwd;
        byte[] ltk;
        try {
            nn = AES.encrypt(this.sessionKey, Utils.stringToBytes(userMeshName, 16));
            pwd = AES.encrypt(this.sessionKey, Utils.stringToBytes(userPassword, 16));
            ltk = AES.encrypt(this.sessionKey, this.meshLTK);
            Utils.reverse(nn, 0, nn.length - 1);
            Utils.reverse(pwd, 0, pwd.length - 1);
            Utils.reverse(ltk, 0, ltk.length - 1);
        } catch (Exception var7) {
            return;
        }

        byte[] nnData = new byte[17];
        nnData[0] = 4;
        System.arraycopy(nn, 0, nnData, 1, nn.length);
        final byte[] pwdData = new byte[17];
        pwdData[0] = 5;
        System.arraycopy(pwd, 0, pwdData, 1, pwd.length);
        final byte[] ltkData = new byte[18];
        ltkData[0] = 6;
        ltkData[17] = 1;
        System.arraycopy(ltk, 0, ltkData, 1, ltk.length);
        SDKLog.e("start reset mesh device meshName");
        this.loginGattCharacteristic.setValue(nnData);
        this.loginGattCharacteristic.setWriteType(2);
        this.mBluetoothGatt.writeCharacteristic(this.loginGattCharacteristic);
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                SDKEventManager.this.loginGattCharacteristic.setValue(pwdData);
                SDKEventManager.this.loginGattCharacteristic.setWriteType(2);
                SDKEventManager.this.mBluetoothGatt.writeCharacteristic(SDKEventManager.this.loginGattCharacteristic);
            }
        }, 200L);
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                SDKEventManager.this.loginGattCharacteristic.setValue(ltkData);
                SDKEventManager.this.loginGattCharacteristic.setWriteType(2);
                SDKEventManager.this.mBluetoothGatt.writeCharacteristic(SDKEventManager.this.loginGattCharacteristic);
                SDKEventManager.this.resetMeshDeviceHandler.sendEmptyMessageDelayed(5, 1000L);
            }
        }, 400L);
    }

    public byte[] getByteByMacAddress(String macAddressStr) {
        if (macAddressStr.contains(":")) {
            String[] strArray = macAddressStr.split(":");
            int length = strArray.length;
            byte[] macAddress = new byte[length];

            for (int i = 0; i < length; ++i) {
                macAddress[i] = (byte) (Integer.parseInt(strArray[i], 16) & 255);
            }

            Utils.reverse(macAddress, 0, length - 1);
            return macAddress;
        } else {
            int length = macAddressStr.length() / 2;
            byte[] macAddress = new byte[length];

            for (int i = 0; i < length; ++i) {
                macAddress[i] = (byte) Integer.parseInt(macAddressStr.substring(i * 2, (i + 1) * 2), 16);
            }

            Utils.reverse(macAddress, 0, length - 1);
            return macAddress;
        }
    }

    public void resetMeshID() {
        byte[] macAddress = this.getByteByMacAddress((String) this.currentMeshInfo.get("mac"));
        int sn = (new Random()).nextInt(16777215);
        byte[] nonce = this.getSecIVM(macAddress, sn);
        int meshAddress = Integer.valueOf((String) this.currentMeshInfo.get("meshID"), 16);
        byte[] command = new byte[20];
        int offset = 0;
        command[offset] = (byte) (sn & 255);
        command[offset++] = (byte) (sn >> 8 & 255);
        command[offset++] = (byte) (sn >> 16 & 255);
        command[offset++] = 0;
        command[offset++] = 0;
        command[offset++] = (byte) (meshAddress & 255);
        command[offset++] = (byte) (meshAddress >> 8 & 255);
        command[offset++] = -32;
        Manufacture manufacture = Manufacture.getDefault();
        int vendorId = manufacture.getVendorId();
        command[offset++] = (byte) (vendorId & 255);
        command[offset++] = (byte) (vendorId >> 8 & 255);
        byte[] params = new byte[]{(byte) (this.newMeshID & 255)};
        System.arraycopy(params, 0, command, offset, params.length);
        byte[] data = AES.encrypt(this.sessionKey, nonce, command);
        SDKLog.e("start reset mesh ID " + Utils.bytesToHexString(data, " "));
        this.commandGattCharacteristic.setValue(data);
        this.commandGattCharacteristic.setWriteType(2);
        this.mBluetoothGatt.writeCharacteristic(this.commandGattCharacteristic);
        this.mBluetoothGatt.setCharacteristicNotification(this.commandGattCharacteristic, true);
    }

    public void restoreFactory() {
        byte[] macAddress = this.getByteByMacAddress((String) this.currentMeshInfo.get("mac"));
        int sn = (new Random()).nextInt(16777215);
        byte[] nonce = this.getSecIVM(macAddress, sn);
        int meshAddress = Integer.valueOf((String) this.currentMeshInfo.get("meshID"), 16);
        byte[] command = new byte[20];
        int offset = 0;
        command[offset] = (byte) (sn & 255);
        command[offset++] = (byte) (sn >> 8 & 255);
        command[offset++] = (byte) (sn >> 16 & 255);
        command[offset++] = 0;
        command[offset++] = 0;
        command[offset++] = (byte) (meshAddress & 255);
        command[offset++] = (byte) (meshAddress >> 8 & 255);
        command[offset++] = -29;
        Manufacture manufacture = Manufacture.getDefault();
        int vendorId = manufacture.getVendorId();
        command[offset++] = (byte) (vendorId & 255);
        command[offset++] = (byte) (vendorId >> 8 & 255);
        byte[] data = AES.encrypt(this.sessionKey, nonce, command);
        SDKLog.e("start restore factory" + Utils.bytesToHexString(data, " "));
        this.commandGattCharacteristic.setValue(data);
        this.commandGattCharacteristic.setWriteType(2);
        this.mBluetoothGatt.writeCharacteristic(this.commandGattCharacteristic);
        this.mBluetoothGatt.setCharacteristicNotification(this.commandGattCharacteristic, true);
    }

    private byte[] getSecIVM(byte[] meshAddress, int sn) {
        byte[] ivm = new byte[8];
        System.arraycopy(meshAddress, 0, ivm, 0, meshAddress.length);
        ivm[4] = 1;
        ivm[5] = (byte) (sn & 255);
        ivm[6] = (byte) (sn >> 8 & 255);
        ivm[7] = (byte) (sn >> 16 & 255);
        return ivm;
    }

    protected void getBoundDevices(String uid, String token, List<String> specialProductKey) {
        List<GizWifiDevice> devices = new ArrayList();
        if (!Constant.ishandshake) {
            onDidDiscovered(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, devices);
        } else if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token) && uid.length() == 32 && token.length() == 32) {
            Constant.uid = uid;
            Constant.token = token;
            SDKLog.b("get_bound_devices_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), DateUtil.getLogString(mContext));
            getboundtime = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
            specialProductKeys = Utils.removeDuplicateString(specialProductKey);
            JSONObject objSend = new JSONObject();
            int sn = Utils.getSn();

            try {
                objSend.put("cmd", 1015);
                objSend.put("sn", sn);
                objSend.put("token", token);
                objSend.put("uid", uid);
            } catch (JSONException var8) {
                SDKLog.e(var8.toString());
                var8.printStackTrace();
            }

            this.sendMessage2Deamon(objSend);
            this.makeTimer(31000, 1016, sn);
        } else {
            SDKLog.d("uid: " + uid + ", token: " + token + "is invalid");
            List<GizWifiDevice> list = this.getDeviceListByProductKeys();
            GizMeshLocalControlCenter.sharedInstance().setLocalMeshDeviceList(productInfo, list);
            onDidDiscovered(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, list);
        }
    }

    protected void bindDevice(String uid, String token, String did, String passCode, String remark) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(did)) {
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1025);
                    obj.put("sn", sn);
                    obj.put("token", token);
                    obj.put("did", did);
                    obj.put("uid", uid);
                    obj.put("passcode", passCode);
                    obj.put("remark", remark);
                } catch (JSONException var10) {
                    SDKLog.e(var10.toString());
                    var10.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1026, sn);
            } else {
                onDidBindDevice(Utils.changeErrorCode(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.getResult()), GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null);
            }
        } else {
            onDidBindDevice(Utils.changeErrorCode(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.getResult()), GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name(), (String) null);
        }
    }

    protected void unbindDevice(String uid, String token, String did) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(did)) {
                SDKLog.b("device_bind_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), Utils.changeString("did") + ":" + Utils.changeString(did) + DateUtil.getLogString(mContext));
                unbindtime = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1027);
                    obj.put("sn", sn);
                    obj.put("token", token);
                    obj.put("did", did);
                    obj.put("uid", uid);
                } catch (JSONException var8) {
                    var8.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1028, sn);
            } else {
                onDidUnbindDevice(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null);
            }
        } else {
            onDidUnbindDevice(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name(), (String) null);
        }
    }

    protected void getCaptchaCode(String appSecret) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (TextUtils.isEmpty(appSecret)) {
                onDidGetCaptchaCode(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null, (String) null, (String) null);
            } else {
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1063);
                    obj.put("sn", sn);
                    obj.put("appSecret", appSecret);
                } catch (JSONException var6) {
                    SDKLog.e(var6.toString());
                    var6.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1064, sn);
            }
        } else {
            onDidGetCaptchaCode(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name(), (String) null, (String) null, (String) null);
        }
    }

    protected void requestSendPhoneSMSCode(String token, String captchaId, String captchaCode, String phone) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(captchaId) && !TextUtils.isEmpty(captchaCode) && !TextUtils.isEmpty(phone)) {
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1065);
                    obj.put("sn", sn);
                    obj.put("token", token);
                    obj.put("captchaId", captchaId);
                    obj.put("captchaCode", captchaCode);
                    obj.put("phone", phone);
                    obj.put("lang", Utils.getLanguage(mContext));
                } catch (JSONException var9) {
                    SDKLog.e(var9.toString());
                    var9.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1066, sn);
            } else {
                onDidRequestSendPhoneSMSCode(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null);
            }
        } else {
            onDidRequestSendPhoneSMSCode(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name(), (String) null);
        }
    }

    protected void verifyPhoneSMSCode(String token, String phoneCode, String phone) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(phoneCode) && !TextUtils.isEmpty(phone)) {
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1067);
                    obj.put("sn", sn);
                    obj.put("token", token);
                    obj.put("verifyCode", phoneCode);
                    obj.put("phone", phone);
                } catch (JSONException var8) {
                    SDKLog.e(var8.toString());
                    var8.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1068, sn);
            } else {
                onDidVerifyPhoneSMSCode(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name());
            }
        } else {
            onDidVerifyPhoneSMSCode(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name());
        }
    }

    protected void registerUser(String username, String password, String code, GizUserAccountType accountType) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                user_register_start = System.currentTimeMillis();
                SDKLog.b("user_register_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), "" + Utils.changeString("username") + ": " + Utils.changeString(Utils.dataMasking(username)) + ", " + Utils.changeString("user_type") + ": " + Utils.changeString(accountType == null ? "null" : accountType.name()) + "" + DateUtil.getLogString(mContext));
                String usertype = "";
                if (GizUserAccountType.GizUserEmail == accountType) {
                    usertype = "email";
                } else if (GizUserAccountType.GizUserNormal == accountType) {
                    usertype = "username";
                } else {
                    if (GizUserAccountType.GizUserPhone != accountType) {
                        onDidRegisterUser(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null, (String) null);
                        return;
                    }

                    usertype = "phone";
                    if (TextUtils.isEmpty(code)) {
                        onDidRegisterUser(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null, (String) null);
                        return;
                    }
                }

                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1053);
                    obj.put("sn", sn);
                    obj.put("username", username);
                    obj.put("password", password);
                    obj.put("verifyCode", code);
                    obj.put("userType", usertype);
                    obj.put("lang", Utils.getLanguage(mContext));
                } catch (JSONException var10) {
                    SDKLog.e(var10.toString());
                    var10.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1054, sn);
            } else {
                onDidRegisterUser(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null, (String) null);
            }
        } else {
            onDidRegisterUser(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name(), (String) null, (String) null);
        }
    }

    protected void userLoginAnonymous() {
        user_login_start = System.currentTimeMillis();
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            SDKLog.b("user_login_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), "" + Utils.changeString("username") + ": " + Utils.changeString(Utils.dataMasking(this.getPhoneID())) + ", " + Utils.changeString("user_type") + ": " + Utils.changeString("GizUserAnonymous") + "," + DateUtil.getLogString(mContext));
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1051);
                obj.put("sn", sn);
                obj.put("userType", "anonymous");
                obj.put("username", this.getPhoneID());
                obj.put("lang", Utils.getLanguage(mContext));
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            this.sendMessage2Deamon(obj);
            this.makeTimer(31000, 1052, sn);
        } else {
            onDidUserLogin(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name(), (String) null, (String) null);
        }
    }

    protected void dynamicLogin(String phone, String code) {
        user_login_start = System.currentTimeMillis();
        SDKLog.b("user_login_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), "" + Utils.changeString("username") + ": " + Utils.changeString(Utils.dataMasking(phone)) + ", " + Utils.changeString("user_type") + ": " + Utils.changeString("GizUserDynamic") + "");
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(code)) {
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1077);
                    obj.put("sn", sn);
                    obj.put("phone", phone);
                    obj.put("code", code);
                    obj.put("lang", Utils.getLanguage(mContext));
                } catch (JSONException var7) {
                    SDKLog.e(var7.toString());
                    var7.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1078, sn);
            } else {
                onDidUserLogin(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null, (String) null);
            }
        } else {
            onDidUserLogin(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name(), (String) null, (String) null);
        }
    }

    protected void userLogin(String userName, String password) {
        user_login_start = System.currentTimeMillis();
        SDKLog.b("user_login_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), "" + Utils.changeString("username") + ": " + Utils.changeString(Utils.dataMasking(userName)) + ", " + Utils.changeString("user_type") + ": " + Utils.changeString("GizUserNormal") + "");
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1051);
                    obj.put("sn", sn);
                    obj.put("userType", "username");
                    obj.put("username", userName);
                    obj.put("password", password);
                    obj.put("lang", Utils.getLanguage(mContext));
                } catch (JSONException var7) {
                    SDKLog.e(var7.toString());
                    var7.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1052, sn);
            } else {
                onDidUserLogin(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null, (String) null);
            }
        } else {
            onDidUserLogin(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name(), (String) null, (String) null);
        }
    }

    protected void loginWithThirdAccountType(GizThirdAccountType thirdAccountType, String uid, String token) {
        user_login_start = System.currentTimeMillis();
        SDKLog.b("user_login_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), "" + Utils.changeString("username") + ": " + Utils.changeString(uid) + ", " + Utils.changeString("user_type") + ": " + (thirdAccountType == null ? "null" : Utils.changeString(thirdAccountType.name())) + "");
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
                String type = "";
                if (GizThirdAccountType.GizThirdBAIDU == thirdAccountType) {
                    type = "baidu";
                } else if (GizThirdAccountType.GizThirdQQ == thirdAccountType) {
                    type = "qq";
                } else if (GizThirdAccountType.GizThirdSINA == thirdAccountType) {
                    type = "sina";
                } else if (GizThirdAccountType.GizThirdWeChat == thirdAccountType) {
                    type = "wechat";
                } else if (GizThirdAccountType.GizThirdFacebook == thirdAccountType) {
                    type = "facebook";
                } else {
                    if (GizThirdAccountType.GizThirdTwitter != thirdAccountType) {
                        onDidUserLogin(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null, (String) null);
                        return;
                    }

                    type = "twitter";
                }

                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1051);
                    obj.put("sn", sn);
                    obj.put("userType", "thirdAccount");
                    obj.put("username", uid);
                    obj.put("password", token);
                    obj.put("thirdAccountType", type);
                    obj.put("lang", Utils.getLanguage(mContext));
                } catch (JSONException var9) {
                    SDKLog.e(var9.toString());
                    var9.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1052, sn);
            } else {
                onDidUserLogin(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null, (String) null);
            }
        } else {
            onDidUserLogin(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name(), (String) null, (String) null);
        }
    }

    protected void changeUserPassword(String token, String oldPassword, String newPassword) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword)) {
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1057);
                    obj.put("sn", sn);
                    obj.put("token", token);
                    obj.put("oldPassword", oldPassword);
                    obj.put("newPassword", newPassword);
                } catch (JSONException var8) {
                    SDKLog.e(var8.toString());
                    var8.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1058, sn);
            } else {
                onDidChangeUserPassword(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name());
            }
        } else {
            onDidChangeUserPassword(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name());
        }
    }

    protected void resetUserPassword(String username, String code, String newPassword, GizUserAccountType accountType) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (TextUtils.isEmpty(username)) {
                onDidChangeUserPassword(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name());
            } else {
                String type = "";
                if (GizUserAccountType.GizUserEmail == accountType) {
                    type = "email";
                } else if (GizUserAccountType.GizUserNormal == accountType) {
                    type = "username";
                } else {
                    if (GizUserAccountType.GizUserPhone != accountType) {
                        onDidChangeUserPassword(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name());
                        return;
                    }

                    type = "phone";
                    if (TextUtils.isEmpty(code)) {
                        onDidChangeUserPassword(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name());
                        return;
                    }
                }

                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1055);
                    obj.put("sn", sn);
                    obj.put("userType", type);
                    obj.put("username", username);
                    obj.put("newPassword", newPassword);
                    if (type.equalsIgnoreCase("phone")) {
                        obj.put("verifyCode", code);
                    }
                } catch (JSONException var10) {
                    SDKLog.e(var10.toString());
                    var10.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1056, sn);
            }
        } else {
            onDidChangeUserPassword(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name());
        }
    }

    protected void changeUserInfo(String token, String username, String code, GizUserAccountType accountType, GizUserInfo additionalInfo) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (TextUtils.isEmpty(token)) {
                onDidChangeUserInfo(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name());
            } else {
                String type = "username";
                if (GizUserAccountType.GizUserNormal == accountType) {
                    type = "username";
                } else if (GizUserAccountType.GizUserEmail == accountType) {
                    type = "email";
                } else if (GizUserAccountType.GizUserPhone == accountType) {
                    type = "phone";
                    if (TextUtils.isEmpty(code)) {
                        onDidChangeUserInfo(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name());
                        return;
                    }
                }

                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();
                JSONObject nalInfo = new JSONObject();

                try {
                    if (additionalInfo != null) {
                        if (additionalInfo.getUserGender() != null && additionalInfo.getUserGender().toString().equals("GizUserGenderMale")) {
                            nalInfo.put("gender", "M");
                        } else if (additionalInfo.getUserGender() != null && additionalInfo.getUserGender().toString().equals("GizUserGenderFemale")) {
                            nalInfo.put("gender", "F");
                        } else if (additionalInfo.getGender() != null && additionalInfo.getGender().toString().equals("Female")) {
                            nalInfo.put("gender", "F");
                        } else if (additionalInfo.getGender() != null && additionalInfo.getGender().toString().equals("Male")) {
                            nalInfo.put("gender", "M");
                        }

                        if (additionalInfo.getName() != null) {
                            nalInfo.put("name", additionalInfo.getName());
                        }

                        if (additionalInfo.getBirthday() != null) {
                            nalInfo.put("birthday", additionalInfo.getBirthday());
                        }

                        if (additionalInfo.getAddress() != null) {
                            nalInfo.put("address", additionalInfo.getAddress());
                        }

                        if (additionalInfo.getRemark() != null) {
                            nalInfo.put("remark", additionalInfo.getRemark());
                        }

                        nalInfo.put("lang", Utils.getLanguage(mContext));
                        obj.put("additionalInfo", nalInfo);
                    }

                    obj.put("cmd", 1059);
                    obj.put("sn", sn);
                    obj.put("token", token);
                    if (!TextUtils.isEmpty(type)) {
                        obj.put("userType", type);
                        obj.put("username", username);
                        if (type.equalsIgnoreCase("phone")) {
                            obj.put("verifyCode", code);
                        }
                    }
                } catch (JSONException var12) {
                    SDKLog.e(var12.toString());
                    var12.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1060, sn);
            }
        } else {
            onDidChangeUserInfo(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name());
        }
    }

    protected void transAnonymousUser(String token, String username, String password, String code, GizUserAccountType accountType) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                String type = "";
                if (GizUserAccountType.GizUserNormal == accountType) {
                    type = "username";
                } else if (GizUserAccountType.GizUserEmail == accountType) {
                    type = "email";
                } else {
                    if (GizUserAccountType.GizUserPhone != accountType) {
                        onDidTransAnonymousUser(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name());
                        return;
                    }

                    type = "phone";
                    if (TextUtils.isEmpty(code)) {
                        onDidTransAnonymousUser(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name());
                        return;
                    }
                }

                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1061);
                    obj.put("sn", sn);
                    obj.put("token", token);
                    obj.put("userType", type);
                    obj.put("password", password);
                    obj.put("username", username);
                    if (type.equalsIgnoreCase("phone")) {
                        obj.put("verifyCode", code);
                    }
                } catch (JSONException var11) {
                    SDKLog.e(var11.toString());
                    var11.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1062, sn);
            } else {
                onDidTransAnonymousUser(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name());
            }
        } else {
            onDidTransAnonymousUser(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name());
        }
    }

    protected void setCloudService(String openAPIDomain, int openAPIPort, String siteDomain, int sitePort) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(openAPIDomain) && !TextUtils.isEmpty(openAPIDomain) && openAPIPort > 0 && sitePort > 0) {
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1101);
                    obj.put("sn", sn);
                    obj.put("openAPIDomain", openAPIDomain);
                    obj.put("openAPIPort", openAPIPort);
                    obj.put("siteDomain", siteDomain);
                    obj.put("sitePort", sitePort);
                } catch (JSONException var9) {
                    var9.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1102, sn);
            } else {
                onDidGetCurrentCloudService(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (ConcurrentHashMap) null);
            }
        } else {
            onDidGetCurrentCloudService(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, (ConcurrentHashMap) null);
        }
    }

    protected void getCurrentCloudService() {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        GizWifiErrorCode result = GizWifiErrorCode.GIZ_SDK_SUCCESS;
        if (isStartedDaemon && Constant.ishandshake) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1103);
                obj.put("sn", sn);
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            this.sendMessage2Deamon(obj);
            this.makeTimer(31000, 1104, sn);
        } else {
            result = GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN;
            onDidGetCurrentCloudService(result, (ConcurrentHashMap) null);
        }
    }

    protected void updateDeviceFromServer(String productKey) {
        SDKLog.a("Start => productKey: " + productKey);
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (TextUtils.isEmpty(productKey)) {
                onDidUpdateProduct(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (String) null, (String) null);
            } else {
                GizWifiDevice isExistedInDeviceList = Utils.isExistedProductKeyInDeviceList(productKey, this.getDeviceListByProductKeys(), (String) null, false);
                String ui = null;
                if (isExistedInDeviceList != null) {
                    ui = isExistedInDeviceList.getProductUI();
                }

                if (!TextUtils.isEmpty(ui)) {
                    onDidUpdateProduct(GizWifiErrorCode.GIZ_SDK_SUCCESS, productKey, ui);
                } else {
                    onDidUpdateProduct(GizWifiErrorCode.GIZ_SDK_PRODUCT_IS_DOWNLOADING, productKey, (String) null);
                    JSONObject obj = new JSONObject();
                    int sn = Utils.getSn();

                    try {
                        obj.put("cmd", 1017);
                        obj.put("sn", sn);
                        obj.put("productKey", productKey);
                    } catch (JSONException var8) {
                        SDKLog.e(var8.toString());
                        var8.printStackTrace();
                    }

                    this.sendMessage2Deamon(obj);
                    this.makeTimer(31000, 1018, sn);
                }

            }
        } else {
            onDidUpdateProduct(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, (String) null, (String) null);
        }
    }

    protected void bindRemoteDevice(String uid, String token, String mac, String productKey, String productSecret, boolean beOwner) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(mac) && !TextUtils.isEmpty(productKey) && !TextUtils.isEmpty(productSecret)) {
                SDKLog.b("device_bind_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), Utils.changeString("mac") + ":" + Utils.changeString(mac) + " " + Utils.changeString("productKey") + " : " + Utils.changeString(productKey) + " " + Utils.changeString("bind_type") + " : " + Utils.changeString("PKSecret") + DateUtil.getLogString(mContext));
                remotetimestart = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1043);
                    obj.put("sn", sn);
                    obj.put("token", token);
                    obj.put("uid", uid);
                    obj.put("mac", mac);
                    obj.put("productKey", productKey);
                    obj.put("productSecret", productSecret);
                    obj.put("setOwner", beOwner);
                } catch (JSONException var11) {
                    SDKLog.e(var11.toString());
                    var11.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1044, sn);
            } else {
                onDidBindRemoteDevice(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (String) null);
            }
        } else {
            onDidBindRemoteDevice(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, (String) null);
        }
    }

    protected void requestSendPhoneSMSCode(String appSecret, String phone) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (TextUtils.isEmpty(appSecret) && TextUtils.isEmpty(phone)) {
                onDidRequestSendPhoneSMSCode(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null);
            } else {
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1071);
                    obj.put("sn", sn);
                    obj.put("appSecret", appSecret);
                    obj.put("phone", phone);
                } catch (JSONException var7) {
                    SDKLog.e(var7.toString());
                    var7.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1072, sn);
            }
        } else {
            onDidRequestSendPhoneSMSCode(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name(), (String) null);
        }
    }

    protected void userLogout() {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1079);
                obj.put("sn", sn);
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            this.sendMessage2Deamon(obj);
            this.makeTimer(4000, 1080, sn);
        } else {
            onDidUserLogout(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
        }
    }

    public void disableLAN(boolean disabled) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1007);
                obj.put("sn", sn);
                obj.put("disableLan", disabled);
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            this.sendMessage2Deamon(obj);
            this.makeTimer(31000, 1008, sn);
        } else {
            onDidDisableLAN(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
        }
    }

    public void bindDeviceByQRCode(String uid, String token, String qRContent, boolean beOwner) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            SDKLog.b("device_bind_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), Utils.changeString("uid") + ":" + Utils.changeString(uid) + " " + Utils.changeString("token") + " : " + Utils.changeString(Utils.dataMasking(token)) + " " + Utils.changeString("bind_type") + " : " + Utils.changeString("QRCode") + DateUtil.getLogString(mContext));
            remotetimestart = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1045);
                obj.put("sn", sn);
                obj.put("uid", uid);
                obj.put("token", token);
                obj.put("qrContent", qRContent);
                obj.put("setOwner", beOwner);
            } catch (JSONException var9) {
                SDKLog.e(var9.toString());
                var9.printStackTrace();
            }

            this.sendMessage2Deamon(obj);
            this.makeTimer(31000, 1046, sn);
        } else {
            onDidBindRemoteDevice(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, (String) null);
        }
    }

    public void channelIDBind(String token, String channelID, String alias, GizPushType pushType) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("GizWifiSDK", 0);
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();
            String pushtyle = null;
            if (pushType != null && pushType == GizPushType.GizPushBaiDu) {
                pushtyle = "baidu";
            } else if (pushType != null && pushType == GizPushType.GizPushJiGuang) {
                pushtyle = "jiguang";
            } else if (pushType != null && pushType == GizPushType.GizPushAWS) {
                pushtyle = "aws";
            } else if (pushType != null && pushType == GizPushType.GizPushXinGe) {
                pushtyle = "xinge";
            }

            sharedPreferences.edit().putString("pushType", pushtyle).commit();

            try {
                obj.put("cmd", 1073);
                obj.put("sn", sn);
                obj.put("channelID", channelID);
                obj.put("token", token);
                obj.put("alias", alias);
                obj.put("pushType", pushtyle);
            } catch (JSONException var11) {
                SDKLog.e(var11.toString());
                var11.printStackTrace();
            }

            this.sendMessage2Deamon(obj);
            this.makeTimer(31000, 1074, sn);
        } else {
            onDidDidChannelIDBind(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
        }
    }

    public void channelIDUnBind(String token, String channelID) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("GizWifiSDK", 0);
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1075);
                obj.put("sn", sn);
                obj.put("channelID", channelID);
                obj.put("token", token);
                obj.put("pushType", sharedPreferences.getString("pushType", ""));
            } catch (JSONException var8) {
                SDKLog.e(var8.toString());
                var8.printStackTrace();
            }

            this.sendMessage2Deamon(obj);
            this.makeTimer(31000, 1076, sn);
        } else {
            onDidDidChannelIDUnBind(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
        }
    }

    public void setCloudService(ConcurrentHashMap<String, String> cloudServiceInfo) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (cloudServiceInfo == null) {
                onDidGetCurrentCloudService(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (ConcurrentHashMap) null);
            } else {
                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1101);
                    obj.put("sn", sn);
                    Iterator var5 = cloudServiceInfo.keySet().iterator();

                    while (var5.hasNext()) {
                        String key = (String) var5.next();
                        if ("openAPIDomain".equals(key)) {
                            obj.put("openAPIDomain", cloudServiceInfo.get(key));
                        } else if ("openAPIPort".equals(key)) {
                            obj.put("openAPIPort", Integer.parseInt((String) cloudServiceInfo.get(key)));
                        } else if ("siteDomain".equals(key)) {
                            obj.put("siteDomain", cloudServiceInfo.get(key));
                        } else if ("sitePort".equals(key)) {
                            obj.put("sitePort", Integer.parseInt((String) cloudServiceInfo.get(key)));
                        } else if ("pushDomain".equals(key)) {
                            obj.put("pushDomain", cloudServiceInfo.get(key));
                        } else if ("pushPort".equals(key)) {
                            obj.put("pushPort", Integer.parseInt((String) cloudServiceInfo.get(key)));
                        }
                    }
                } catch (Exception var7) {
                    var7.printStackTrace();
                    onDidGetCurrentCloudService(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, (ConcurrentHashMap) null);
                    return;
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1102, sn);
            }
        } else {
            onDidGetCurrentCloudService(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, (ConcurrentHashMap) null);
        }
    }

    protected void getDevicesToSetServerInfo() {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1107);
                obj.put("sn", sn);
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            this.sendMessage2Deamon(obj);
            this.makeTimer(31000, 1108, sn);
        } else {
            List<ConcurrentHashMap<String, String>> devices = new ArrayList();
            OnDidGetDevicesToSetServerInfo(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, devices);
        }
    }

    protected void setDeviceServerInfo(String domain, String mac) {
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();
            this.sendMessage2Deamon(Utils.getJsonString());

            try {
                obj.put("cmd", 1109);
                obj.put("sn", sn);
                obj.put("domain", domain);
                obj.put("mac", mac);

                try {
                    Calendar calendar = new GregorianCalendar();
                    int offset2 = calendar.getTimeZone().getOffset(System.currentTimeMillis()) / 1000;
                    obj.put("zone", offset2);
                } catch (NumberFormatException var11) {
                    var11.printStackTrace();
                    SDKLog.e(var11.toString());
                }

                TimeZone tz = TimeZone.getDefault();
                String id = tz.getID();
                if (TextUtils.isEmpty(domain) && cityJson != null && cityJson.has(id)) {
                    JSONObject pushObj = (JSONObject) cityJson.get(id);
                    String api = (String) pushObj.get("api");
                    obj.put("domain", api);
                }
            } catch (JSONException var12) {
                SDKLog.e(var12.toString());
                var12.printStackTrace();

                try {
                    obj.put("domain", "api.gizwits.com");
                } catch (JSONException var10) {
                    var10.printStackTrace();
                }
            }

            this.sendMessage2Deamon(obj);
            this.snlist = new ArrayList();
            if (TextUtils.isEmpty(mac)) {
                this.makeTimer(120000, 1110, sn);
                this.snlist.add(sn);
            } else {
                ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap();
                map.put(sn, mac);
                this.macandsnlist.add(map);
                this.makeTimer(4000, 1110, sn);
                this.snlist.add(sn);
            }

        } else {
            this.OnDidSetDeviceServerInfo(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, mac);
        }
    }

    public void loginWithThirdAccountType(GizThirdAccountType thirdAccountType, String uid, String token, String tokenSecret) {
        user_login_start = System.currentTimeMillis();
        SDKLog.b("user_login_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), "" + Utils.changeString("username") + ": " + Utils.changeString(uid) + ", " + Utils.changeString("user_type") + ": " + (thirdAccountType == null ? "null" : Utils.changeString(thirdAccountType.name())) + "");
        boolean isStartedDaemon = getInstance().getIsStartedDaemon();
        if (isStartedDaemon && Constant.ishandshake) {
            if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
                String type = "";
                if (GizThirdAccountType.GizThirdBAIDU == thirdAccountType) {
                    type = "baidu";
                } else if (GizThirdAccountType.GizThirdQQ == thirdAccountType) {
                    type = "qq";
                } else if (GizThirdAccountType.GizThirdSINA == thirdAccountType) {
                    type = "sina";
                } else if (GizThirdAccountType.GizThirdWeChat == thirdAccountType) {
                    type = "wechat";
                } else if (GizThirdAccountType.GizThirdFacebook == thirdAccountType) {
                    type = "facebook";
                } else {
                    if (GizThirdAccountType.GizThirdTwitter != thirdAccountType) {
                        onDidUserLogin(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null, (String) null);
                        return;
                    }

                    type = "twitter";
                }

                JSONObject obj = new JSONObject();
                int sn = Utils.getSn();

                try {
                    obj.put("cmd", 1051);
                    obj.put("sn", sn);
                    obj.put("userType", "thirdAccount");
                    obj.put("username", uid);
                    obj.put("tokenSecret", tokenSecret);
                    obj.put("password", token);
                    obj.put("thirdAccountType", type);
                    obj.put("lang", Utils.getLanguage(mContext));
                } catch (JSONException var10) {
                    SDKLog.e(var10.toString());
                    var10.printStackTrace();
                }

                this.sendMessage2Deamon(obj);
                this.makeTimer(31000, 1052, sn);
            } else {
                onDidUserLogin(GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID.name(), (String) null, (String) null);
            }
        } else {
            onDidUserLogin(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN.name(), (String) null, (String) null);
        }
    }

    private void syncDevice(JSONObject obj) {
        HandlerThread connectDaemonThread = new HandlerThread("syncDevice");
        if (syncDeviceHandler == null) {
            connectDaemonThread.start();
            syncDeviceHandler = new SDKEventManager.SyncDeviceHandler(connectDaemonThread.getLooper());
        }

        Message message = new Message();
        message.what = 0;
        message.obj = obj;
        syncDeviceHandler.sendMessage(message);
    }

    private void makeTimer(int timeout, int cmd, int sn) {
        HandlerThread connectDaemonThread = new HandlerThread("errorthread");
        if (handler == null) {
            connectDaemonThread.start();
            handler = new SDKEventManager.MessageErrorHandler(Looper.getMainLooper());
        }

        Message mes = Message.obtain();
        mes.what = sn;
        mes.obj = cmd;
        handler.sendMessageDelayed(mes, (long) timeout);
    }

    private String getMac(Integer sn) {
        String mymac = "";
        int num = -1;

        for (int i = 0; i < this.macandsnlist.size(); ++i) {
            ConcurrentHashMap<Integer, String> map = (ConcurrentHashMap) this.macandsnlist.get(i);
            if (map.containsKey(sn)) {
                String mac = (String) map.get(sn);
                mymac = mac;
                num = i;
            }
        }

        if (num != -1) {
            this.macandsnlist.remove(num);
        }

        return mymac;
    }

    static {
        logLevel = GizLogPrintLevel.GizLogPrintAll;
        gagentTypes = new ArrayList();
        configureMode = null;
        is5GWifi = false;
        onboardingSsid = "";
        originalKey = "";
        onboardingTimeout = 30;
        isDisableLan = false;
        userMeshName = null;
        userPassword = null;
        serviceUUID = null;
        pairUUID = null;
        commandUUID = null;
        notifyUUID = null;
        gizMeshVendor = GizMeshVendor.GizMeshTelink;
        MeshAction = -1;
        CurrentMsg = -1;
    }

    class BaiDuHandler extends Handler {
        public BaiDuHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            boolean isbackgroud = Utils.isApkBackground(SDKEventManager.mContext);
            JSONObject obj;
            if (!Utils.isWifi(SDKEventManager.mContext) && Utils.isNetAvailable(SDKEventManager.mContext)) {
                try {
                    int sn = Utils.getSn();
                    if (isbackgroud != Constant.isBackGround) {
                        Constant.isBackGround = isbackgroud;
                        obj = new JSONObject();
                        obj.put("cmd", 1005);
                        obj.put("sn", sn);
                        obj.put("netDisable", false);
                        obj.put("netFree", false);
                        obj.put("internetReachable", true);
                        obj.put("background", isbackgroud);
                        GizWifiDaemon.updateBackgroundToDaemon(isbackgroud);
                        MessageHandler.getSingleInstance().send(obj.toString());
                    }
                } catch (JSONException var9) {
                    SDKEventManager.this.pingHandler.sendEmptyMessageDelayed(1111, 10000L);
                    var9.printStackTrace();
                }

                SDKEventManager.this.pingHandler.sendEmptyMessageDelayed(1111, 10000L);
            } else {
                boolean ping = true;
                if (ping != Constant.netping || isbackgroud != Constant.isBackGround) {
                    Constant.isBackGround = isbackgroud;

                    try {
                        obj = new JSONObject();
                        if (ping) {
                            int snx = Utils.getSn();
                            boolean netFree = Utils.isNetFree(SDKEventManager.mContext);
                            obj.put("cmd", 1005);
                            obj.put("sn", snx);
                            obj.put("netDisable", false);
                            obj.put("background", isbackgroud);
                            obj.put("netFree", netFree);
                            obj.put("internetReachable", ping);
                            Constant.netping = ping;
                            GizWifiDaemon.updateBackgroundToDaemon(isbackgroud);
                            MessageHandler.getSingleInstance().send(obj.toString());
                        } else {
                            ConnectivityManager manager = (ConnectivityManager) SDKEventManager.mContext.getSystemService("connectivity");
                            NetworkInfo info = manager.getActiveNetworkInfo();
                            int snxx;
                            if (info == null || !manager.getBackgroundDataSetting()) {
                                snxx = Utils.getSn();
                                Constant.netping = ping;
                                obj.put("cmd", 1005);
                                obj.put("sn", snxx);
                                obj.put("netDisable", true);
                                obj.put("background", isbackgroud);
                                GizWifiDaemon.updateBackgroundToDaemon(isbackgroud);
                                SDKEventManager.this.sendMessage2Deamon(obj);
                                SDKEventManager.this.pingHandler.sendEmptyMessageDelayed(1111, 10000L);
                                return;
                            }

                            snxx = Utils.getSn();
                            boolean netFreex = Utils.isNetFree(SDKEventManager.mContext);
                            obj.put("cmd", 1005);
                            obj.put("sn", snxx);
                            obj.put("netDisable", false);
                            Constant.netping = ping;
                            obj.put("netFree", netFreex);
                            obj.put("internetReachable", ping);
                            obj.put("background", isbackgroud);
                            GizWifiDaemon.updateBackgroundToDaemon(isbackgroud);
                            MessageHandler.getSingleInstance().send(obj.toString());
                        }
                    } catch (JSONException var10) {
                        var10.printStackTrace();
                        SDKEventManager.this.pingHandler.sendEmptyMessageDelayed(1111, 10000L);
                    }
                }

                SDKEventManager.this.pingHandler.sendEmptyMessageDelayed(1111, 10000L);
            }

        }
    }

    class MyAsyncTask extends AsyncTask<Void, Integer, String> {
        Context context;
        String filesPath;

        public MyAsyncTask(Context context, String filesPath) {
            this.context = context;
            this.filesPath = filesPath;
        }

        protected String doInBackground(Void... params) {
            SDKEventManager.this.copyDaemon(SDKEventManager.mContext, this.filesPath);
            return null;
        }
    }

    class MessageErrorHandler extends Handler {
        public MessageErrorHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int cmd = (Integer) msg.obj;
            SDKLog.d("timeout  cmd: " + cmd + "   sn: " + msg.what + " isHasMes :  " + SDKEventManager.isHandler(msg.what));
            switch (cmd) {
                case 1002:
                    SDKEventManager.handler.removeMessages(msg.what);
                    break;
                case 1004:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidNotifyEvent(GizEventType.GizEventSDK, (Object) null, GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()).name());
                    break;
                case 1008:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidDisableLAN(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()));
                    break;
                case 1010:
                    SDKEventManager.handler.removeMessages(msg.what);
                    break;
                case 1012:
                    SDKEventManager.handler.removeMessages(msg.what);
                    if (!TextUtils.isEmpty(SDKEventManager.onboardingSsid) && !SDKEventManager.onboardingSsid.equals(Utils.getWifiSSID(SDKEventManager.mContext))) {
                        SDKEventManager.this.onDidSetDeviceOnboarding(GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_SSID_NOT_MATCHED, (GizWifiDevice) null);
                    } else {
                        SDKEventManager.this.onDidSetDeviceOnboarding(GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_TIMEOUT, (GizWifiDevice) null);
                    }
                    break;
                case 1014:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidGetSSIDList(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), (List) null);
                    break;
                case 1016:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidDiscovered(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, SDKEventManager.this.getDeviceListByProductKeys());
                    break;
                case 1018:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidUpdateProduct(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), (String) null, (String) null);
                    break;
                case 1026:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidBindDevice(GizWifiErrorCode.valueOf(8308).getResult(), GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name(), (String) null);
                    break;
                case 1028:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidUnbindDevice(GizWifiErrorCode.valueOf(8308), GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name(), (String) null);
                    break;
                case 1044:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidBindRemoteDevice(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), (String) null);
                    break;
                case 1046:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidBindRemoteDevice(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), (String) null);
                    break;
                case 1052:
                case 1078:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidUserLogin(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()).name(), (String) null, (String) null);
                    break;
                case 1054:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidRegisterUser(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name(), (String) null, (String) null);
                    break;
                case 1056:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidChangeUserPassword(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name());
                    break;
                case 1058:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidChangeUserPassword(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name());
                    break;
                case 1060:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidChangeUserInfo(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name());
                    break;
                case 1062:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidTransAnonymousUser(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name());
                    break;
                case 1064:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidGetCaptchaCode(GizWifiErrorCode.valueOf(8308), (String) null, (String) null, (String) null, (String) null);
                    break;
                case 1066:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidRequestSendPhoneSMSCode(GizWifiErrorCode.valueOf(8308), (String) null, (String) null);
                    break;
                case 1068:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidVerifyPhoneSMSCode(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name());
                    break;
                case 1070:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidGetUserInfo(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name(), (GizUserInfo) null);
                    break;
                case 1072:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidRequestSendPhoneSMSCode(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.name(), (String) null);
                    break;
                case 1074:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidDidChannelIDBind(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()));
                    break;
                case 1076:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidDidChannelIDUnBind(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()));
                    break;
                case 1080:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidUserLogout(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()));
                    break;
                case 1102:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidGetCurrentCloudService(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), (ConcurrentHashMap) null);
                    break;
                case 1108:
                    SDKEventManager.handler.removeMessages(msg.what);
                    List<ConcurrentHashMap<String, String>> devices = new ArrayList();
                    SDKEventManager.OnDidGetDevicesToSetServerInfo(GizWifiErrorCode.valueOf(8308), devices);
                    break;
                case 1110:
                    SDKEventManager.handler.removeMessages(msg.what);
                    String mac = SDKEventManager.this.getMac(msg.what);
                    SDKEventManager.this.OnDidSetDeviceServerInfo(GizWifiErrorCode.valueOf(8308), mac);
                    break;
                case 1122:
                    SDKEventManager.handler.removeMessages(msg.what);
                    break;
                case 1124:
                    SDKEventManager.handler.removeMessages(msg.what);
                    break;
                case 1254:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidGetScheduleTasks(GizWifiErrorCode.valueOf(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT.getResult()), (List) null);
                    break;
                case 1398:
                    SDKEventManager.handler.removeMessages(msg.what);
                    SDKEventManager.onDidGetMapTab(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT);
            }

        }
    }

    class SyncDeviceHandler extends Handler {
        public SyncDeviceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject obj = (JSONObject) msg.obj;

            try {
                SDKEventManager.syncDeviceListFromDaemon(obj);
                SDKEventManager.this.nHandler.sendEmptyMessage(7);
            } catch (JSONException var4) {
                var4.printStackTrace();
            }

        }
    }
}
