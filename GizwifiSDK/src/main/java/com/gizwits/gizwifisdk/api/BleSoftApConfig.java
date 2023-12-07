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
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import com.gizwits.gizwifisdk.enumration.GizWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class BleSoftApConfig {
    private static final String TAG = "BleSoftApConfig";
    private int currentAction = 0;
    private static final int ACTION_NONE = 0;
    private static final int ACTION_SEARCH = 1;
    private static final int ACTION_CONNECT = 2;
    private static final int ACTION_CONFIG = 3;
    private static final int MSG_SEND_SECOND_COMMAND = 1;
    private List<BluetoothDevice> scanBTDeviceList = new ArrayList();
    private List<String> configSuccessDeviceMacs = new ArrayList();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCharacteristic commandGattCharacteristic;
    private BluetoothGattCharacteristic notifyGattCharacteristic;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice connectDevice;
    private static final String serviceUUID = "abf0";
    private static final String commandUUID = "abf7";
    private static final String notifyUUID = "abf7";
    private int configSN;
    private Context mContext;
    private String pwd;
    private String ssid;
    private String configPrefix;
    LeScanCallback mLeScanCallback = new LeScanCallback() {
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device.getName() != null && device.getName().startsWith(BleSoftApConfig.this.configPrefix)) {
                boolean isHasDevice = false;

                for(int i = 0; i < BleSoftApConfig.this.scanBTDeviceList.size(); ++i) {
                    if (((BluetoothDevice)BleSoftApConfig.this.scanBTDeviceList.get(i)).getAddress().equals(device.getAddress())) {
                        isHasDevice = true;
                    }
                }

                if (!isHasDevice) {
                    BleSoftApConfig.this.scanBTDeviceList.add(device);
                    if (BleSoftApConfig.this.configureMode == GizWifiConfigureMode.GizWifiBleLinkMulti && BleSoftApConfig.this.currentAction == 1) {
                        BleSoftApConfig.this.configNextDevice();
                    }

                    if (BleSoftApConfig.this.configureMode == GizWifiConfigureMode.GizWifiBleLink && BleSoftApConfig.this.currentAction == 1) {
                        BleSoftApConfig.this.connectDevice = device;
                        BleSoftApConfig.this.mBluetoothAdapter.stopLeScan(BleSoftApConfig.this.mLeScanCallback);
                        BleSoftApConfig.this.connectDevice();
                    }
                }

            }
        }
    };
    BleSoftApConfig.ConfigThread configThread;
    BleSoftApConfig.ConnectThread connectThread;
    GizWifiConfigureMode configureMode;
    boolean isChangeMtu;
    Handler sendHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch(msg.what) {
                case 1:
                    byte[] byte2 = (byte[])((byte[])msg.obj);
                    if (BleSoftApConfig.this.commandGattCharacteristic != null && BleSoftApConfig.this.mBluetoothGatt != null) {
                        SDKLog.e("send2:" + Utils.bytesToHexString(byte2, " "));
                        BleSoftApConfig.this.commandGattCharacteristic.setValue(byte2);
                        BleSoftApConfig.this.mBluetoothGatt.writeCharacteristic(BleSoftApConfig.this.commandGattCharacteristic);
                    }
                default:
            }
        }
    };
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == 2) {
                SDKLog.e("Connected to GATT server.");
                SDKLog.e("Attempting to start service discovery:" + BleSoftApConfig.this.mBluetoothGatt.discoverServices());
            } else if (newState == 2) {
                SDKLog.e("BleSoftApConfigdisconnect GATT server");
                if (BleSoftApConfig.this.currentAction == 3) {
                    BleSoftApConfig.this.connectDevice();
                }
            }

        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            SDKLog.e("change MTU:" + mtu + " " + status);
            BleSoftApConfig.this.isChangeMtu = status == 0;
            BleSoftApConfig.this.startConfig();
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid().toString().contains("abf7".toLowerCase())) {
                SDKLog.e("ble send config response:" + Utils.bytesToHexString(characteristic.getValue(), " "));
                BleSoftApConfig.this.configSuccessDeviceMacs.add(BleSoftApConfig.this.connectDevice.getAddress());
                BleSoftApConfig.this.disconnectDevice();
                if (BleSoftApConfig.this.configureMode == GizWifiConfigureMode.GizWifiBleLinkMulti) {
                    BleSoftApConfig.this.configNextDevice();
                }
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
                        if (gattCharacteristic.getUuid().toString().contains("abf7".toLowerCase())) {
                            BleSoftApConfig.this.commandGattCharacteristic = gattCharacteristic;
                            SDKLog.e("find commandUUID:" + gattCharacteristic.getUuid().toString());
                        }

                        if (gattCharacteristic.getUuid().toString().contains("abf7".toLowerCase())) {
                            BleSoftApConfig.this.notifyGattCharacteristic = gattCharacteristic;
                            SDKLog.e("find notifyUUID:" + gattCharacteristic.getUuid().toString());
                            BleSoftApConfig.this.mBluetoothGatt.setCharacteristicNotification(BleSoftApConfig.this.notifyGattCharacteristic, true);
                        }

                        if (BleSoftApConfig.this.commandGattCharacteristic != null && BleSoftApConfig.this.notifyGattCharacteristic != null) {
                            boolean requestMtu = BleSoftApConfig.this.mBluetoothGatt.requestMtu(128);
                            if (!requestMtu) {
                                BleSoftApConfig.this.isChangeMtu = false;
                                BleSoftApConfig.this.startConfig();
                            }
                        }
                    }
                }

            }
        }
    };
    private static BleSoftApConfig mInstance;

    public static synchronized BleSoftApConfig getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BleSoftApConfig(context);
        }

        return mInstance;
    }

    public BleSoftApConfig(Context context) {
    }

    public boolean startScanDevice() {
        this.scanBTDeviceList.clear();
        if (this.mBluetoothAdapter == null) {
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (!this.mBluetoothAdapter.isEnabled()) {
            return false;
        } else {
            SDKLog.e("BleSoftApConfig startSearchMeshDevice");
            if (VERSION.SDK_INT >= 18) {
                this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
            }

            return true;
        }
    }

    public void startConfigDevice(String configPrefix, int sn, String ssid, String pwd, GizWifiConfigureMode configureMode) {
        if (this.currentAction == 0) {
            if (!this.startScanDevice()) {
                this.callBack(GizWifiErrorCode.GIZ_SDK_BLE_BLUETOOTH_FUNCTION_NOT_TURNED_ON);
            } else {
                this.configureMode = configureMode;
                this.configSN = sn;
                this.ssid = ssid;
                this.pwd = pwd;
                this.configPrefix = configPrefix;
                this.configSuccessDeviceMacs.clear();
                this.scanBTDeviceList.clear();
                this.currentAction = 1;
                if (configureMode == GizWifiConfigureMode.GizWifiBleLink) {
                    (new Handler()).postDelayed(new Runnable() {
                        public void run() {
                            if (BleSoftApConfig.this.scanBTDeviceList.size() == 0) {
                                BleSoftApConfig.this.callBack(GizWifiErrorCode.GIZ_SDK_BLE_UNFIND_DEVICE_PERIPHERAL);
                            }

                        }
                    }, 10000L);
                }

            }
        }
    }

    public synchronized void configNextDevice() {
        for(int i = 0; i < this.scanBTDeviceList.size(); ++i) {
            if (!this.configSuccessDeviceMacs.contains(((BluetoothDevice)this.scanBTDeviceList.get(i)).getAddress())) {
                this.connectDevice = (BluetoothDevice)this.scanBTDeviceList.get(i);
                break;
            }
        }

        if (this.connectDevice != null) {
            this.connectDevice();
        }

    }

    private void connectDevice() {
        if (this.currentAction != 2) {
            this.currentAction = 2;
            this.connectThread = new BleSoftApConfig.ConnectThread();
            this.connectThread.start();
        }

        this.commandGattCharacteristic = null;
        this.notifyGattCharacteristic = null;
        SDKLog.e("start connect bt device");
        if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            this.mBluetoothGatt.connect();
        } else {
            this.mBluetoothGatt = this.connectDevice.connectGatt(this.mContext, false, this.mGattCallback);
        }
    }

    private void startConfig() {
        this.currentAction = 3;
        this.connectThread = null;
        this.configThread = new BleSoftApConfig.ConfigThread();
        this.configThread.start();
    }

    private void sendConfig() {
        byte[] senddata = this.getSendData();
        SDKLog.e("ble send config:" + Utils.bytesToHexString(senddata, " "));
        if (this.mBluetoothGatt != null) {
            if (senddata.length <= 20) {
                this.commandGattCharacteristic.setValue(senddata);
                this.mBluetoothGatt.writeCharacteristic(this.commandGattCharacteristic);
            } else {
                byte[] byte1 = new byte[20];
                System.arraycopy(senddata, 0, byte1, 0, 20);
                this.commandGattCharacteristic.setValue(byte1);
                this.mBluetoothGatt.writeCharacteristic(this.commandGattCharacteristic);
                SDKLog.e("send1:" + Utils.bytesToHexString(byte1, " "));
                int byteCount = senddata.length / 20;
                byteCount -= senddata.length % 20 == 0 ? 1 : 0;

                for(int i = 1; i <= byteCount; ++i) {
                    byte[] byte2;
                    if (i == byteCount) {
                        byte2 = new byte[senddata.length - 20 * byteCount];
                    } else {
                        byte2 = new byte[20];
                    }

                    System.arraycopy(senddata, i * 20, byte2, 0, byte2.length);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = byte2;
                    this.sendHandler.sendMessageDelayed(message, (long)(100 * i));
                }
            }

        }
    }

    public void stopConfig() {
        SDKLog.e("stop ble link");
        if (this.mBluetoothAdapter != null) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        }

        this.disconnectDevice();
        this.currentAction = 0;
    }

    public void disconnectDevice() {
        if (this.configureMode == GizWifiConfigureMode.GizWifiBleLinkMulti) {
            this.currentAction = 1;
        } else {
            this.currentAction = 0;
        }

        this.configThread = null;
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
            this.connectDevice = null;
        }

    }

    private byte[] getSendData() {
        byte[] pwdByte = this.pwd.getBytes();
        byte[] ssidByte = this.ssid.getBytes();
        byte[] data = new byte[pwdByte.length + ssidByte.length + 12];
        int offset = 0;
        data[offset] = 0;
        data[offset++] = 0;
        data[offset++] = 0;
        data[offset++] = 3;
        data[offset++] = (byte)(pwdByte.length + ssidByte.length + 7 & 255);
        data[offset++] = 0;
        data[offset++] = 0;
        data[offset++] = 1;
        data[offset++] = 0;
        data[offset++] = (byte)(ssidByte.length & 255);
        System.arraycopy(ssidByte, 0, data, offset, ssidByte.length);
        offset += ssidByte.length;
        data[offset++] = 0;
        data[offset++] = (byte)(pwdByte.length & 255);
        System.arraycopy(pwdByte, 0, data, offset, pwdByte.length);
        return data;
    }

    public void callBack(GizWifiErrorCode gizWifiErrorCode) {
        this.stopConfig();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("errorCode", gizWifiErrorCode.getResult());
            jsonObject.put("sn", this.configSN);
            jsonObject.put("cmd", 1012);
        } catch (JSONException var5) {
            var5.printStackTrace();
        }

        Message message = new Message();
        message.what = 5;
        message.obj = jsonObject.toString();
        Handler handler = MessageHandler.getSingleInstance().getHandler();
        if (handler != null) {
            handler.sendMessage(message);
        }

    }

    class ConnectThread extends Thread {
        ConnectThread() {
        }

        public void run() {
            super.run();

            while(BleSoftApConfig.this.currentAction == 2) {
                try {
                    Thread.sleep(6000L);
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }

                if (BleSoftApConfig.this.currentAction == 2) {
                    BleSoftApConfig.this.connectDevice();
                }
            }

        }
    }

    class ConfigThread extends Thread {
        ConfigThread() {
        }

        public void run() {
            super.run();

            while(BleSoftApConfig.this.currentAction == 3) {
                try {
                    byte[] senddata = BleSoftApConfig.this.getSendData();
                    SDKLog.e("ble send config:" + Utils.bytesToHexString(senddata, " "));
                    if (BleSoftApConfig.this.mBluetoothGatt == null) {
                        return;
                    }

                    if (senddata.length <= 20) {
                        BleSoftApConfig.this.commandGattCharacteristic.setValue(senddata);
                        BleSoftApConfig.this.mBluetoothGatt.writeCharacteristic(BleSoftApConfig.this.commandGattCharacteristic);
                    } else {
                        byte[] byte1 = new byte[20];
                        System.arraycopy(senddata, 0, byte1, 0, 20);
                        BleSoftApConfig.this.commandGattCharacteristic.setValue(byte1);
                        BleSoftApConfig.this.mBluetoothGatt.writeCharacteristic(BleSoftApConfig.this.commandGattCharacteristic);
                        SDKLog.e("send1:" + Utils.bytesToHexString(byte1, " "));
                        int byteCount = senddata.length / 20;
                        byteCount -= senddata.length % 20 == 0 ? 1 : 0;

                        for(int i = 1; i <= byteCount; ++i) {
                            byte[] byte2;
                            if (i == byteCount) {
                                byte2 = new byte[senddata.length - 20 * byteCount];
                            } else {
                                byte2 = new byte[20];
                            }

                            System.arraycopy(senddata, i * 20, byte2, 0, byte2.length);
                            Thread.sleep(100L);
                            if (BleSoftApConfig.this.commandGattCharacteristic != null && BleSoftApConfig.this.mBluetoothGatt != null) {
                                SDKLog.e("send2:" + Utils.bytesToHexString(byte2, " "));
                                BleSoftApConfig.this.commandGattCharacteristic.setValue(byte2);
                                BleSoftApConfig.this.mBluetoothGatt.writeCharacteristic(BleSoftApConfig.this.commandGattCharacteristic);
                            }
                        }
                    }

                    Thread.sleep(500L);
                } catch (InterruptedException var6) {
                    var6.printStackTrace();
                }
            }

        }
    }
}
