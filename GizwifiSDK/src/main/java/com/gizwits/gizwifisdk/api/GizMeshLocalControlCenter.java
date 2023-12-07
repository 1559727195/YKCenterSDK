//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
import com.gizwits.gizwifisdk.api.GizJsProtocol.GetResult;
import com.gizwits.gizwifisdk.enumration.GizLocalMeshType;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.log.SDKLog;
import com.telink.crypto.AES;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class GizMeshLocalControlCenter {
    private static final String TAG = "GizMeshLocalControlCenter";
    private static final int MSG_CONNECT_DEVICE = 0;
    private static final int MSG_LOGIN_DEVICE = 1;
    private static final int MSG_LOGIN_SUCCESS = 2;
    private static final int MSG_TIME_OUT = 3;
    private Context mContext;
    private List<GizWifiDevice> localMeshDeviceList = new ArrayList();
    private List<BluetoothDevice> scanBTDeviceList = new ArrayList();
    private List<ConcurrentHashMap<String, String>> meshInfoList = new ArrayList();
    String scanBTmacs = "";
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCharacteristic loginGattCharacteristic;
    private BluetoothGattCharacteristic commandGattCharacteristic;
    private BluetoothGattCharacteristic notifyGattCharacteristic;
    private String pairUUID = null;
    private String commandUUID = null;
    private String notifyUUID = null;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice connectDevice;
    private final byte[] loginRandm = new byte[8];
    private byte[] sessionKey;
    private byte[] nonce;
    LeScanCallback mLeScanCallback = new LeScanCallback() {
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            boolean isLocalDevice = false;

            int length;
            for(length = 0; length < GizMeshLocalControlCenter.this.localMeshDeviceList.size(); ++length) {
                if (((GizWifiDevice)GizMeshLocalControlCenter.this.localMeshDeviceList.get(length)).getMacAddress().equals(device.getAddress().replace(":", ""))) {
                    isLocalDevice = true;
                }
            }

            if (isLocalDevice) {
                length = scanRecord.length;
                int packetPosition = 0;
                byte[] name = null;
                int rspData = 0;

                while(true) {
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
                                        if (meshNamestr.equals(GizMeshLocalControlCenter.this.meshName)) {
                                            ConcurrentHashMap<String, String> meshMap = new ConcurrentHashMap();
                                            meshMap.put("mac", device.getAddress().replace(":", ""));
                                            meshMap.put("meshID", String.format("%02x", meshAddress));
                                            meshMap.put("advData", Utils.bytesToHexString(scanRecord, ""));
                                            Message message = new Message();
                                            message.what = 6;
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("status", GizMeshLocalControlCenter.this.connectDevice != null && GizMeshLocalControlCenter.this.currentLoginMacs.contains(device.getAddress().replace(":", "")) ? GizWifiDeviceNetStatus.GizDeviceControlled.ordinal() : GizWifiDeviceNetStatus.GizDeviceOnline.ordinal());
                                            bundle.putString("mac", device.getAddress().replace(":", ""));
                                            message.setData(bundle);
                                            MessageHandler.getSingleInstance().getHandler().sendMessage(message);
                                            boolean isHasDevice = false;
                                            if (!GizMeshLocalControlCenter.this.scanBTmacs.contains(device.getAddress())) {
                                                GizMeshLocalControlCenter.this.scanBTmacs = GizMeshLocalControlCenter.this.scanBTmacs + device.getAddress() + ",";
                                            }

                                            for(int i = 0; i < GizMeshLocalControlCenter.this.scanBTDeviceList.size(); ++i) {
                                                if (device.getAddress().equals(((BluetoothDevice)GizMeshLocalControlCenter.this.scanBTDeviceList.get(i)).getAddress())) {
                                                    isHasDevice = true;
                                                }
                                            }

                                            if (!isHasDevice) {
                                                GizMeshLocalControlCenter.this.meshInfoList.add(meshMap);
                                                GizMeshLocalControlCenter.this.scanBTDeviceList.add(device);
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
        }
    };
    private String meshName;
    private String meshPwd;
    private boolean isSearchMeshDevice;
    private boolean isDeviceLogin;
    private List<String> currentLoginMacs = new ArrayList();
    private GizMeshLocalControlCenter.SearchMeshDeviceThread searchMeshDeviceThread;
    private boolean isBanAction;
    private Handler receiveDataHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            GizMeshLocalControlCenter.this.receiveData(msg.getData().getString("meshId"), msg.getData().getByteArray("data"));
        }
    };
    private Handler subscribeHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    GizMeshLocalControlCenter.this.connectDevice();
                    break;
                case 1:
                    GizMeshLocalControlCenter.this.loginDevice();
                    break;
                case 2:
                    GizMeshLocalControlCenter.this.subscribeCallBack(true);
                    (new Handler()).postDelayed(new Runnable() {
                        public void run() {
                            GizMeshLocalControlCenter.this.notifyGattCharacteristic.setValue(new byte[]{1});
                            GizMeshLocalControlCenter.this.notifyGattCharacteristic.setWriteType(2);
                            GizMeshLocalControlCenter.this.mBluetoothGatt.writeCharacteristic(GizMeshLocalControlCenter.this.notifyGattCharacteristic);
                        }
                    }, 1000L);
                    break;
                case 3:
                    this.removeMessages(1);
                    GizMeshLocalControlCenter.this.isDeviceLogin = false;
                    GizMeshLocalControlCenter.this.connectDevice = null;
                    GizMeshLocalControlCenter.this.currentLoginMacs.clear();
            }

        }
    };
    private static final GizMeshLocalControlCenter mInstance = new GizMeshLocalControlCenter();
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == 2) {
                SDKLog.e("Connected to GATT server.");
                SDKLog.e("Attempting to start service discovery:" + GizMeshLocalControlCenter.this.mBluetoothGatt.discoverServices());
            } else if (newState == 0) {
                GizMeshLocalControlCenter.this.subscribeHandler.removeMessages(3);
                SDKLog.e("disconnected to GATT server.");
                GizMeshLocalControlCenter.this.mBluetoothGatt.close();
                GizMeshLocalControlCenter.this.connectDevice = null;
                GizMeshLocalControlCenter.this.sessionKey = null;
                GizMeshLocalControlCenter.this.mBluetoothAdapter = null;
                GizMeshLocalControlCenter.this.mBluetoothGatt = null;
                GizMeshLocalControlCenter.this.isDeviceLogin = false;
                GizMeshLocalControlCenter.this.subscribeCallBack(false);
            }

        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> gattServices = gatt.getServices();
            if (gattServices != null) {
                Iterator var4 = gattServices.iterator();

                while(var4.hasNext()) {
                    BluetoothGattService gattService = (BluetoothGattService)var4.next();
                    int type = gattService.getType();
                    SDKLog.e("find BluetoothGattService uuid:" + gattService.getUuid().toString());
                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                    Iterator var8 = gattCharacteristics.iterator();

                    while(var8.hasNext()) {
                        BluetoothGattCharacteristic gattCharacteristic = (BluetoothGattCharacteristic)var8.next();
                        int permission = gattCharacteristic.getPermissions();
                        int property = gattCharacteristic.getProperties();
                        byte[] data = gattCharacteristic.getValue();
                        if (gattCharacteristic.getUuid().toString().equals(GizMeshLocalControlCenter.this.pairUUID.toLowerCase())) {
                            GizMeshLocalControlCenter.this.loginGattCharacteristic = gattCharacteristic;
                            SDKLog.e("find pairUUID:" + gattCharacteristic.getUuid().toString());
                            GizMeshLocalControlCenter.this.subscribeHandler.sendEmptyMessage(1);
                        }

                        if (gattCharacteristic.getUuid().toString().equals(GizMeshLocalControlCenter.this.commandUUID.toLowerCase())) {
                            GizMeshLocalControlCenter.this.commandGattCharacteristic = gattCharacteristic;
                            SDKLog.e("find commandUUID:" + gattCharacteristic.getUuid().toString());
                        }

                        if (gattCharacteristic.getUuid().toString().equals(GizMeshLocalControlCenter.this.notifyUUID.toLowerCase())) {
                            GizMeshLocalControlCenter.this.notifyGattCharacteristic = gattCharacteristic;
                            SDKLog.e("find notifyUUID:" + gattCharacteristic.getUuid().toString());
                            GizMeshLocalControlCenter.this.mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                        }
                    }
                }

            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0) {
                byte[] data = characteristic.getValue();
                if (data[0] == 14) {
                    return;
                }

                byte[] sk = new byte[16];
                byte[] rands = new byte[8];
                System.arraycopy(data, 1, sk, 0, 16);
                System.arraycopy(data, 1, rands, 0, 8);

                try {
                    GizMeshLocalControlCenter.this.sessionKey = GizMeshLocalControlCenter.this.getSessionKey(Utils.stringToBytes(GizMeshLocalControlCenter.this.meshName, 16), Utils.stringToBytes(GizMeshLocalControlCenter.this.meshPwd, 16), GizMeshLocalControlCenter.this.loginRandm, rands, sk);
                } catch (Exception var8) {
                    return;
                }

                if (GizMeshLocalControlCenter.this.sessionKey != null) {
                    GizMeshLocalControlCenter.this.subscribeHandler.removeMessages(3);
                    GizMeshLocalControlCenter.this.subscribeHandler.sendEmptyMessage(2);
                }
            }

        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            SDKLog.e("onCharacteristicChanged " + characteristic.getUuid().toString() + " " + Utils.bytesToHexString(characteristic.getValue(), " "));
            if (characteristic.getUuid().toString().equals(GizMeshLocalControlCenter.this.notifyGattCharacteristic.getUuid().toString())) {
                byte[] data = characteristic.getValue();
                byte[] nonce = GizMeshLocalControlCenter.this.getSecIVS(GizMeshLocalControlCenter.this.getByteByMacAddress(gatt.getDevice().getAddress()));
                System.arraycopy(data, 0, nonce, 3, 5);
                byte[] result = AES.decrypt(GizMeshLocalControlCenter.this.sessionKey, nonce, data);
                SDKLog.e("receiveData:" + Utils.bytesToHexString(result, " "));
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putByteArray("data", result);
                bundle.putString("meshId", String.format("%02x", result[10]));
                message.setData(bundle);
                message.what = 0;
                GizMeshLocalControlCenter.this.receiveDataHandler.sendMessage(message);
            }

        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            SDKLog.e("onCharacteristicWrite " + characteristic.getUuid().toString() + " " + Utils.bytesToHexString(characteristic.getValue(), " "));
            if (characteristic.getUuid().toString().equals(GizMeshLocalControlCenter.this.loginGattCharacteristic.getUuid().toString())) {
                if (GizMeshLocalControlCenter.this.sessionKey == null || GizMeshLocalControlCenter.this.sessionKey.length == 0) {
                    SDKLog.e("login mesh device success");
                    GizMeshLocalControlCenter.this.mBluetoothGatt.readCharacteristic(GizMeshLocalControlCenter.this.loginGattCharacteristic);
                }
            } else if (characteristic.getUuid().toString().equals(GizMeshLocalControlCenter.this.commandGattCharacteristic.getUuid().toString())) {
            }

        }
    };

    public GizMeshLocalControlCenter() {
    }

    public void setMeshInfo(Context context, String meshName, String meshPwd, String pairUUID, String commandUUID, String notifyUUID) {
        this.mContext = context;
        this.meshName = meshName;
        this.meshPwd = meshPwd;
        this.pairUUID = pairUUID;
        this.commandUUID = commandUUID;
        this.notifyUUID = notifyUUID;
    }

    public static synchronized GizMeshLocalControlCenter sharedInstance() {
        return mInstance;
    }

    public void stopAllAction() {
        this.isBanAction = true;
        if (this.mBluetoothGatt != null && this.mBluetoothGatt.connect()) {
            this.mBluetoothGatt.disconnect();
        } else {
            this.subscribeHandler.removeMessages(3);
            this.connectDevice = null;
            this.sessionKey = null;
            this.mBluetoothAdapter = null;
            this.mBluetoothGatt = null;
            this.isDeviceLogin = false;
        }

        this.isSearchMeshDevice = false;
        this.searchMeshDeviceThread = null;

        for(int i = 0; i < this.scanBTDeviceList.size(); ++i) {
            Message message = new Message();
            message.what = 6;
            Bundle bundle = new Bundle();
            bundle.putInt("status", GizWifiDeviceNetStatus.GizDeviceOffline.ordinal());
            bundle.putString("mac", ((BluetoothDevice)this.scanBTDeviceList.get(i)).getAddress().replace(":", ""));
            message.setData(bundle);
            MessageHandler.getSingleInstance().getHandler().sendMessage(message);
        }

        this.localMeshDeviceList.clear();
        this.scanBTDeviceList.clear();
    }

    public void resetAction() {
        this.isBanAction = false;
    }

    public synchronized void setLocalMeshDeviceList(List<ConcurrentHashMap<String, Object>> productInfos, List<GizWifiDevice> allDevice) {
        if (!this.isBanAction) {
            this.localMeshDeviceList.clear();
            String meshProductKeys = "";
            String meshGatewayProductKeys = "";
            if (productInfos != null && allDevice != null && !TextUtils.isEmpty(this.meshName)) {
                int i;
                for(i = 0; i < allDevice.size(); ++i) {
                    if (this.isHasFindDevice(((GizWifiDevice)allDevice.get(i)).getMacAddress())) {
                        Message message = new Message();
                        message.what = 6;
                        Bundle bundle = new Bundle();
                        bundle.putInt("status", this.connectDevice != null && ((GizWifiDevice)allDevice.get(i)).isSubscribed() ? GizWifiDeviceNetStatus.GizDeviceControlled.ordinal() : GizWifiDeviceNetStatus.GizDeviceOnline.ordinal());
                        bundle.putString("mac", ((GizWifiDevice)allDevice.get(i)).getMacAddress());
                        message.setData(bundle);
                        MessageHandler.getSingleInstance().getHandler().sendMessage(message);
                    }
                }

                for(i = 0; i < productInfos.size(); ++i) {
                    ConcurrentHashMap<String, Object> productInfo = (ConcurrentHashMap)productInfos.get(i);
                    SDKLog.e("localMeshDevice:" + productInfo.toString() + " " + allDevice.size());
                    if (productInfo.get("localMeshType") != null) {
                        if ((Integer)productInfo.get("localMeshType") == GizLocalMeshType.GizLocalMeshSub.ordinal()) {
                            meshProductKeys = meshProductKeys + "," + productInfo.get("productKey");
                        }

                        if ((Integer)productInfo.get("localMeshType") == GizLocalMeshType.GizLocalMeshGateway.ordinal()) {
                            meshGatewayProductKeys = meshGatewayProductKeys + "," + productInfo.get("productKey");
                        }
                    }
                }

                String offlineRootDeviceDids = "";

                GizWifiDevice gizWifiDevice;
                for(i = 0; i < allDevice.size(); ++i) {
                    gizWifiDevice = (GizWifiDevice)allDevice.get(i);
                    if (meshGatewayProductKeys.contains(gizWifiDevice.getProductKey()) && gizWifiDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOffline) {
                        offlineRootDeviceDids = offlineRootDeviceDids + "," + gizWifiDevice.getDid();
                    }
                }

                for(i = 0; i < allDevice.size(); ++i) {
                    gizWifiDevice = (GizWifiDevice)allDevice.get(i);
                    SDKLog.e("localMeshDevice:" + gizWifiDevice.toString() + " " + meshProductKeys + " " + offlineRootDeviceDids);
                    if (meshProductKeys.contains(gizWifiDevice.getProductKey()) && (gizWifiDevice.getRootDevice() == null || offlineRootDeviceDids.contains(gizWifiDevice.getRootDevice().getDid()))) {
                        SDKLog.e("localMeshDevice:" + gizWifiDevice.getMacAddress());
                        this.localMeshDeviceList.add(gizWifiDevice);
                    }
                }

                if (this.localMeshDeviceList.size() != 0) {
                    if (!this.isSearchMeshDevice) {
                        this.isSearchMeshDevice = true;
                        this.searchMeshDeviceThread = new GizMeshLocalControlCenter.SearchMeshDeviceThread();
                        this.searchMeshDeviceThread.start();
                    }
                } else {
                    this.isSearchMeshDevice = false;
                    this.searchMeshDeviceThread = null;
                    this.stopAllAction();
                    this.isBanAction = false;
                }

            }
        }
    }

    public void checkDeviceAutoConnect() {
        for(int i = 0; i < this.localMeshDeviceList.size(); ++i) {
            if (((GizWifiDevice)this.localMeshDeviceList.get(i)).isSubscribed() && this.connectDevice == null && this.scanBTmacs.replace(":", "").contains(((GizWifiDevice)this.localMeshDeviceList.get(i)).getMacAddress())) {
                this.setSubscribe(((GizWifiDevice)this.localMeshDeviceList.get(i)).getMacAddress(), -1, true);
                break;
            }
        }

    }

    private synchronized void checkDeviceOffline() {
        if (!this.isDeviceLogin) {
            List<BluetoothDevice> removeList = new ArrayList();

            for(int i = 0; i < this.scanBTDeviceList.size(); ++i) {
                if ((this.connectDevice == null || !this.connectDevice.getAddress().equals(((BluetoothDevice)this.scanBTDeviceList.get(i)).getAddress())) && !this.scanBTmacs.contains(((BluetoothDevice)this.scanBTDeviceList.get(i)).getAddress())) {
                    removeList.add(this.scanBTDeviceList.get(i));
                    Message message = new Message();
                    message.what = 6;
                    Bundle bundle = new Bundle();
                    bundle.putInt("status", GizWifiDeviceNetStatus.GizDeviceOffline.ordinal());
                    bundle.putString("mac", ((BluetoothDevice)this.scanBTDeviceList.get(i)).getAddress().replace(":", ""));
                    message.setData(bundle);
                    MessageHandler.getSingleInstance().getHandler().sendMessage(message);
                }
            }

            this.scanBTDeviceList.removeAll(removeList);
            this.scanBTmacs = "";
        }
    }

    private void stopSearchMeshDevice() {
        if (this.mBluetoothAdapter != null) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        }

    }

    private void searchMeshDevice() {
        if (this.mBluetoothAdapter == null) {
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        Log.e("GizMeshLocalControlCenter", "startSearchMeshDevice");
        if (VERSION.SDK_INT >= 18) {
            this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
        }

    }

    public void setSubscribe(String mac, int sn, boolean isSubscribe) {
        if (isSubscribe) {
            if (!this.currentLoginMacs.contains(mac)) {
                this.currentLoginMacs.add(mac);
            }

            if (this.isDeviceLogin) {
                return;
            }

            if (this.sessionKey != null) {
                this.subscribeCallBack(true);
                return;
            }

            for(int i = 0; i < this.scanBTDeviceList.size(); ++i) {
                if (mac.equals(((BluetoothDevice)this.scanBTDeviceList.get(i)).getAddress().replace(":", ""))) {
                    this.connectDevice = (BluetoothDevice)this.scanBTDeviceList.get(i);
                }
            }

            this.subscribeHandler.sendEmptyMessage(0);
            this.subscribeHandler.sendEmptyMessageDelayed(3, 10000L);
        } else {
            if (this.currentLoginMacs.contains(mac)) {
                this.currentLoginMacs.remove(mac);
            }

            if (this.currentLoginMacs.size() == 0 && this.mBluetoothGatt != null) {
                this.mBluetoothGatt.disconnect();
            }

            Message message = new Message();
            message.what = 6;
            Bundle bundle = new Bundle();
            bundle.putInt("status", GizWifiDeviceNetStatus.GizDeviceOnline.ordinal());
            bundle.putString("mac", mac);
            message.setData(bundle);
            MessageHandler.getSingleInstance().getHandler().sendMessage(message);
            this.subscribeHandler.removeMessages(3);
        }

    }

    private void subscribeCallBack(boolean isControl) {
        this.isDeviceLogin = false;

        for(int i = 0; i < this.currentLoginMacs.size(); ++i) {
            Message message = new Message();
            message.what = 6;
            Bundle bundle = new Bundle();
            bundle.putInt("status", isControl ? GizWifiDeviceNetStatus.GizDeviceControlled.ordinal() : GizWifiDeviceNetStatus.GizDeviceOnline.ordinal());
            bundle.putString("mac", (String)this.currentLoginMacs.get(i));
            message.setData(bundle);
            MessageHandler.getSingleInstance().getHandler().sendMessage(message);
        }

    }

    public boolean isHasFindDevice(String mac) {
        for(int i = 0; i < this.scanBTDeviceList.size(); ++i) {
            if (mac.equals(((BluetoothDevice)this.scanBTDeviceList.get(i)).getAddress().replace(":", ""))) {
                return true;
            }
        }

        return false;
    }

    private void connectDevice() {
        this.isDeviceLogin = true;
        SDKLog.e("start connect bt device");
        this.mBluetoothGatt = this.connectDevice.connectGatt(this.mContext, false, this.mGattCallback);
    }

    protected void loginDevice() {
        byte[] meshName = new byte[16];
        byte[] password = new byte[16];
        byte[] commandData = new byte[17];
        meshName = Utils.stringToBytes(this.meshName, 16);
        password = Utils.stringToBytes(this.meshPwd, 16);
        byte[] plaintext = new byte[16];

        for(int i = 0; i < 16; ++i) {
            plaintext[i] = (byte)(meshName[i] ^ password[i]);
        }

        byte[] sk = new byte[16];
        (new SecureRandom()).nextBytes(this.loginRandm);
        byte[] randm = this.loginRandm;
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

    private byte[] getSessionKey(byte[] meshName, byte[] password, byte[] randm, byte[] rands, byte[] sk) throws Exception {
        byte[] key = new byte[16];
        System.arraycopy(rands, 0, key, 0, rands.length);
        byte[] plaintext = new byte[16];

        for(int i = 0; i < 16; ++i) {
            plaintext[i] = (byte)(meshName[i] ^ password[i]);
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

    public void write(String pk, String mac, final int writeSn, JSONObject cmdData) {
        ConcurrentHashMap<String, String> meshInfo = null;

        for(int i = 0; i < this.meshInfoList.size(); ++i) {
            if (((String)((ConcurrentHashMap)this.meshInfoList.get(i)).get("mac")).equals(mac)) {
                meshInfo = (ConcurrentHashMap)this.meshInfoList.get(i);
            }
        }

        if (this.sessionKey != null && meshInfo != null) {
            SDKLog.e("meshid:" + (String)meshInfo.get("meshID"));
            GizJsProtocol.sharedInstance().encodeProtocol(pk, cmdData, (String)meshInfo.get("meshID"), new GetResult() {
                public void encode(byte[] cmd) {
                    GizMeshLocalControlCenter.this.sendBleCommand(cmd, writeSn);
                }

                public void decode(String decode) {
                }
            });
        } else {
            this.writeCallBack(GizWifiErrorCode.GIZ_SDK_DEVICE_NOT_READY, writeSn);
        }
    }

    public void sendBleCommand(byte[] cmdData, int writeSn) {
        byte[] macAddress = this.getByteByMacAddress(this.connectDevice.getAddress().replace(":", ""));
        this.nonce = this.getSecIVM(macAddress, cmdData);
        byte[] data = AES.encrypt(this.sessionKey, this.nonce, cmdData);
        this.commandGattCharacteristic.setValue(data);
        this.commandGattCharacteristic.setWriteType(2);
        this.mBluetoothGatt.writeCharacteristic(this.commandGattCharacteristic);
        this.mBluetoothGatt.setCharacteristicNotification(this.notifyGattCharacteristic, true);
        this.writeCallBack(GizWifiErrorCode.GIZ_SDK_SUCCESS, writeSn);
    }

    public void writeCallBack(GizWifiErrorCode errorCode, int sn) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("sn", sn);
            jsonObject.put("errorCode", errorCode.getResult());
        } catch (JSONException var6) {
            var6.printStackTrace();
        }

        Message message = new Message();
        message.what = 7;
        message.obj = jsonObject;
        MessageHandler.getSingleInstance();
        Handler handler = MessageHandler.getHandlerBySN(sn);
        if (handler != null) {
            handler.sendMessage(message);
        }

    }

    public GizWifiDevice getDeviceByMac(String mac) {
        for(int i = 0; i < this.localMeshDeviceList.size(); ++i) {
            if (((GizWifiDevice)this.localMeshDeviceList.get(i)).getMacAddress().equals(mac)) {
                return (GizWifiDevice)this.localMeshDeviceList.get(i);
            }
        }

        return null;
    }

    public GizWifiDevice getDeviceByMeshId(String meshId) {
        for(int i = 0; i < this.localMeshDeviceList.size(); ++i) {
            if (this.localMeshDeviceList.get(i) instanceof GizLiteGWSubDevice && ((GizLiteGWSubDevice)this.localMeshDeviceList.get(i)).getMeshId().equals(meshId)) {
                SDKLog.e("meshId:" + ((GizLiteGWSubDevice)this.localMeshDeviceList.get(i)).getMeshId());
                return (GizWifiDevice)this.localMeshDeviceList.get(i);
            }
        }

        return null;
    }

    @SuppressLint({"NewApi"})
    public void receiveData(String meshId, byte[] data) {
        final GizWifiDevice gizWifiDevice = this.getDeviceByMeshId(meshId);
        if (gizWifiDevice != null && gizWifiDevice.isSubscribed()) {
            String pk = gizWifiDevice.getProductKey();
            String base64data = GizWifiBinary.encode(data);
            GizJsProtocol.sharedInstance().decodeProtocol(pk, base64data, new GetResult() {
                public void encode(byte[] cmd) {
                }

                public void decode(String decode) {
                    if (decode != null) {
                        decode = decode.substring(1, decode.length() - 1).replace("\\", "");
                        GizMeshLocalControlCenter.this.receiveDataCallBack(gizWifiDevice, decode);
                    }
                }
            });
        }
    }

    public void receiveDataCallBack(GizWifiDevice gizWifiDevice, String value) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("cmd", 2006);
            jsonObject.put("sn", 0);
            jsonObject.put("errorCode", 0);
            JSONObject attrStatus = new JSONObject(value);
            jsonObject.put("attrStatus", attrStatus);
        } catch (JSONException var5) {
            var5.printStackTrace();
        }

        Message message = new Message();
        message.setTarget(gizWifiDevice.getHandler());
        message.what = 5;
        message.obj = jsonObject.toString();
        message.sendToTarget();
    }

    private byte[] getSecIVM(byte[] macAddress, byte[] cmdData) {
        byte[] ivm = new byte[8];
        System.arraycopy(macAddress, 0, ivm, 0, macAddress.length);
        ivm[4] = 1;
        ivm[5] = (byte)(cmdData[0] & 255);
        ivm[6] = (byte)(cmdData[1] & 255);
        ivm[7] = (byte)(cmdData[2] & 255);
        return ivm;
    }

    private byte[] getSecIVM(byte[] meshAddress, int sn) {
        byte[] ivm = new byte[8];
        System.arraycopy(meshAddress, 0, ivm, 0, meshAddress.length);
        ivm[4] = 1;
        ivm[5] = (byte)(sn & 255);
        ivm[6] = (byte)(sn >> 8 & 255);
        ivm[7] = (byte)(sn >> 16 & 255);
        return ivm;
    }

    private byte[] getSecIVS(byte[] macAddress) {
        byte[] ivs = new byte[8];
        ivs[0] = macAddress[0];
        ivs[1] = macAddress[1];
        ivs[2] = macAddress[2];
        return ivs;
    }

    public byte[] getByteByMacAddress(String macAddressStr) {
        if (macAddressStr.contains(":")) {
            String[] strArray = macAddressStr.split(":");
            int length = strArray.length;
            byte[] macAddress = new byte[length];

            for(int i = 0; i < length; ++i) {
                macAddress[i] = (byte)(Integer.parseInt(strArray[i], 16) & 255);
            }

            Utils.reverse(macAddress, 0, length - 1);
            return macAddress;
        } else {
            int length = macAddressStr.length() / 2;
            byte[] macAddress = new byte[length];

            for(int i = 0; i < length; ++i) {
                macAddress[i] = (byte)Integer.parseInt(macAddressStr.substring(i * 2, (i + 1) * 2), 16);
            }

            Utils.reverse(macAddress, 0, length - 1);
            return macAddress;
        }
    }

    private class SearchMeshDeviceThread extends Thread {
        private SearchMeshDeviceThread() {
        }

        public void run() {
            super.run();

            while(GizMeshLocalControlCenter.this.isSearchMeshDevice) {
                try {
                    GizMeshLocalControlCenter.this.searchMeshDevice();
                    Thread.sleep(5000L);
                    GizMeshLocalControlCenter.this.stopSearchMeshDevice();
                    GizMeshLocalControlCenter.this.checkDeviceAutoConnect();
                    GizMeshLocalControlCenter.this.checkDeviceOffline();
                    Thread.sleep(5000L);
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }
            }

        }
    }
}
