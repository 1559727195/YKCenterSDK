//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceType;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageHandler {
    public static final int MAX_BUF_LENGTH = 1048576;
    public static final int MSG_CONNETING = 1;
    public static final int MSG_CONNETED = 2;
    public static final int MSG_CONNECT_FAILED = 3;
    public static final int MSG_SEND = 4;
    public static final int MSG_RECV = 5;
    public static final int MSG_MESH_UPDATE = 6;
    public static final int MSG_MESH_WRITE = 7;
    private static final MessageHandler mInstance = new MessageHandler();
    private static Selector mSelector;
    protected SocketChannel mSocketChannel;
    private boolean checkhasmessage = true;
    private static Handler mMessageParentHandler;
    public static MessageHandler.MessageChildHandler mMessageChildHandler;
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            try {
                if (null == MessageHandler.mSelector) {
                    SDKLog.d("mSelector is null, get selector open");
                    MessageHandler.mSelector = Selector.open();
                }
            } catch (IOException var6) {
                SDKLog.e("Selector.open() error " + var6.toString());
                MessageHandler.mMessageParentHandler.sendEmptyMessage(3);
            }

            try {
                if (null == MessageHandler.this.mSocketChannel || MessageHandler.this.mSocketChannel != null && !MessageHandler.this.mSocketChannel.isOpen()) {
                    SDKLog.d("mSocketChannel: " + MessageHandler.this.mSocketChannel + ", mSocketChannel.isOpen(): " + (MessageHandler.this.mSocketChannel != null ? MessageHandler.this.mSocketChannel.isOpen() : false) + ", get socket channel open");
                    MessageHandler.this.mSocketChannel = SocketChannel.open();
                    SDKLog.d("mSocketChannel.isOpen() = " + MessageHandler.this.mSocketChannel.isOpen());
                    MessageHandler.this.mSocketChannel.socket().setTcpNoDelay(true);
                }
            } catch (IOException var8) {
                SDKLog.e("SocketChannel.open() error " + var8.toString());
                MessageHandler.mMessageParentHandler.sendEmptyMessage(3);
            }

            try {
                if (MessageHandler.this.mSocketChannel != null && MessageHandler.this.mSocketChannel.isOpen() && !MessageHandler.this.mSocketChannel.isConnected()) {
                    InetAddress address = InetAddress.getLocalHost();
                    SDKLog.d("Local Host Name：" + address.getHostName() + ", Local IP：" + address.getHostAddress());
                    boolean connect = MessageHandler.this.mSocketChannel.connect(new InetSocketAddress(address, 20017));
                    if (connect) {
                        SDKLog.d("connect to GizWifiSDKDaemon success: " + MessageHandler.this.mSocketChannel.hashCode());
                        MessageHandler.this.checkhasmessage = true;
                        MessageHandler.this.mSocketChannel.configureBlocking(false);
                        MessageHandler.this.mSocketChannel.register(MessageHandler.mSelector, 1);
                        MessageHandler.mMessageParentHandler.sendEmptyMessage(2);
                    } else {
                        SDKLog.d("connect to GizWifiSDKDaemon failed: " + MessageHandler.this.mSocketChannel.hashCode());
                        MessageHandler.mMessageParentHandler.sendEmptyMessage(3);
                    }
                }

                while(MessageHandler.this.checkhasmessage) {
                    int readyChannels = MessageHandler.mSelector.select(10L);
                    if (0 == readyChannels) {
                        break;
                    }

                    Set<SelectionKey> selectedKeys = MessageHandler.mSelector.selectedKeys();

                    for(Iterator keyIterator = selectedKeys.iterator(); keyIterator.hasNext(); keyIterator.remove()) {
                        SelectionKey key = (SelectionKey)keyIterator.next();
                        if (key.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel)key.channel();
                            MessageHandler.this.recvJsonStrFromGizWifiSDKDaemon(socketChannel);
                        }
                    }
                }
            } catch (IOException var7) {
                SDKLog.e("SocketChannel or Selector error " + var7.toString());
                MessageHandler.mMessageParentHandler.sendEmptyMessage(3);
            }

            MessageHandler.mMessageChildHandler.postDelayed(MessageHandler.this.mRunnable, 100L);
        }
    };

    public MessageHandler() {
    }

    public static MessageHandler getSingleInstance() {
        return mInstance;
    }

    public Handler getHandler() {
        return mMessageParentHandler;
    }

    public void setHandler(Handler handler) {
        if (mMessageParentHandler == null) {
            mMessageParentHandler = handler;
            HandlerThread connectDaemonThread = new HandlerThread("ConnectDaemonThread");
            connectDaemonThread.start();
            if (mMessageChildHandler == null) {
                mMessageChildHandler = new MessageHandler.MessageChildHandler(connectDaemonThread.getLooper());
                mMessageChildHandler.post(this.mRunnable);
                this.checkhasmessage = true;
            }

        }
    }

    private String jsonMasking(Object obj) {
        String jsonMasking = "";
        JSONObject object = null;

        try {
            if (obj instanceof String) {
                object = new JSONObject((String)obj);
            } else if (obj instanceof JSONObject) {
                object = (JSONObject)obj;
            } else {
                jsonMasking = jsonMasking + obj.toString();
            }

            if (object != null && object instanceof JSONObject) {
                String cmd = object.has("cmd") ? object.getString("cmd") : "";
                if (!cmd.equals("1001") && !cmd.equals("1002")) {
                    jsonMasking = "{";
                    jsonMasking = jsonMasking + "cmd: " + cmd + ", sn: " + (object.has("sn") ? object.getString("sn") : "") + (object.has("mac") ? ", mac: " + object.getString("mac") : "") + (object.has("did") ? ", did: " + Utils.dataMasking(object.getString("did")) : "") + (object.has("productKey") ? ", productKey: " + object.getString("productKey") : "") + (object.has("errorCode") ? ", errorCode: " + object.getString("errorCode") : "");
                    jsonMasking = jsonMasking + "}";
                } else {
                    if (object.has("appid")) {
                        object.put("appid", Utils.dataMasking(object.getString("appid")));
                    }

                    if (object.has("appSecret")) {
                        object.put("appSecret", Utils.dataMasking(object.getString("appSecret")));
                    }

                    if (object.has("token")) {
                        object.put("token", Utils.dataMasking(object.getString("token")));
                    }

                    JSONArray array2;
                    int i;
                    JSONObject o;
                    if (object.has("specialProductKeys")) {
                        array2 = object.getJSONArray("specialProductKeys");

                        for(i = 0; i < array2.length(); ++i) {
                            o = array2.getJSONObject(i);
                            if (o.has("productSecret")) {
                                o.put("productSecret", Utils.dataMasking(o.getString("productSecret")));
                            }
                        }
                    }

                    if (object.has("devices")) {
                        array2 = object.getJSONArray("devices");

                        for(i = 0; i < array2.length(); ++i) {
                            o = array2.getJSONObject(i);
                            if (o.has("did")) {
                                o.put("did", Utils.dataMasking(o.getString("did")));
                            }
                        }
                    }

                    jsonMasking = jsonMasking + object.toString();
                }
            }
        } catch (JSONException var8) {
            var8.printStackTrace();
            jsonMasking = jsonMasking + var8.toString();
        }

        return jsonMasking;
    }

    public void setDeviceHandler(Handler handler) {
    }

    public void send(String jsonStr) {
        Message newMessage = new Message();
        if (mMessageChildHandler == null) {
            SDKLog.e("can not send json to daemon, mMessageChildHandler is null");
        } else {
            newMessage.setTarget(mMessageChildHandler);
            newMessage.what = 4;
            newMessage.obj = jsonStr;
            newMessage.sendToTarget();
        }
    }

    private void sendJsonStrToGizWifiSDKDaemon(String jsonStr) {
        try {
            ByteBuffer sendBuf = ByteBuffer.allocate(4 + jsonStr.getBytes("UTF-8").length);
            if (sendBuf != null) {
                int length = jsonStr.getBytes("UTF-8").length;
                sendBuf.putInt(length);
                byte[] bytes = jsonStr.getBytes("UTF-8");
                sendBuf.put(bytes);
                sendBuf.flip();

                try {
                    if (this.mSocketChannel != null) {
                        this.mSocketChannel.write(sendBuf);
                    } else {
                        SDKLog.d("can not send json to daemon, mSocketChannel is null");
                    }
                } catch (IOException var6) {
                    var6.printStackTrace();
                    SDKLog.d(var6.getMessage());
                }
            } else {
                SDKLog.d("can not send json to daemon, sendBuf allocate failed");
            }
        } catch (UnsupportedEncodingException var7) {
            var7.printStackTrace();
            SDKLog.d(var7.getMessage());
        }

    }

    private void recvJsonStrFromGizWifiSDKDaemon(SocketChannel socketChannel) {
        ByteBuffer recvLengthBuf = ByteBuffer.allocate(4);

        try {
            int len = socketChannel.read(recvLengthBuf);
            if (len <= 0) {
                SDKLog.e("read return " + len + ", so close the connection");

                try {
                    socketChannel.close();
                } catch (Exception var21) {
                    SDKLog.e("mSocketChannel  is not close:" + var21.toString());
                    var21.printStackTrace();
                }

                this.checkhasmessage = false;
                return;
            }

            recvLengthBuf.flip();
            len = recvLengthBuf.getInt();
            recvLengthBuf.clear();
            SDKLog.d("receive length: " + len);
            ByteBuffer tmpBuf;
            if (len > 1048576) {
                SDKLog.e("read return " + len + " is over the MAX_BUF_LENGTH, so read it out");
                tmpBuf = ByteBuffer.allocate(1048576);

                while((len = socketChannel.read(tmpBuf)) > 0) {
                    tmpBuf.clear();
                }

                SDKLog.e("read return " + len + " is over the MAX_BUF_LENGTH, so close the socket");
            } else {
                tmpBuf = ByteBuffer.allocate(len);
                int read = socketChannel.read(tmpBuf);
                int length = len - read;
                if (length != 0) {
                    SDKLog.d("recv length:" + read + "is not len: " + len + "so return");
                    return;
                }

                tmpBuf.flip();
                String jsonStr = new String(tmpBuf.array(), "utf-8");
                tmpBuf.clear();
                Message newMessage = Message.obtain();

                try {
                    JSONObject obj = new JSONObject(jsonStr);
                    int cmd = Integer.parseInt(obj.getString("cmd"));
                    if (cmd != 1030 && cmd != 1032 && cmd != 1034 && cmd != 1036 && cmd != 1038 && cmd != 1040 && cmd != 2006 && cmd != 1042 && cmd != 2003 && cmd != 1018 && cmd != 2014) {
                        String mac;
                        String did;
                        String productKey;
                        if (cmd != 1020 && cmd != 1022 && cmd != 1024 && cmd != 2002) {
                            if (cmd != 1202 && cmd != 1204 && cmd != 2007 && cmd != 2009) {
                                if (cmd != 1152 && cmd != 1154 && cmd != 1156 && cmd != 1158 && cmd != 1160 && cmd != 1162 && cmd != 1164 && cmd != 1166 && cmd != 1168 && cmd != 1170 && cmd != 1172 && cmd != 1174) {
                                    if (cmd != 1302 && cmd != 1306 && cmd != 1308 && cmd != 2021 && cmd != 1318) {
                                        int sn;
                                        String jointActionID;
                                        GizWifiDevice myOwnerDevice;
                                        List list;
                                        Iterator var29;
                                        if (cmd != 1310 && cmd != 1312 && cmd != 1314 && cmd != 1316 && cmd != 1304 && cmd != 2022 && cmd != 2031) {
                                            if (cmd != 1332 && cmd != 1336 && cmd != 1338 && cmd != 2023 && cmd != 1348) {
                                                if (cmd != 1340 && cmd != 1342 && cmd != 1344 && cmd != 1346 && cmd != 1334 && cmd != 2032 && cmd != 2024 && cmd != 2025) {
                                                    if (cmd != 1092 && cmd != 1094 && cmd != 1096 && cmd != 1098 && cmd != 1260 && cmd != 2026) {
                                                        if (cmd != 1100 && cmd != 1252 && cmd != 1254 && cmd != 1256 && cmd != 1258 && cmd != 2028 && cmd != 2027 && cmd != 2033) {
                                                            if (cmd != 1362 && cmd != 1364 && cmd != 1366 && cmd != 1368 && cmd != 2034) {
                                                                if (cmd != 1370 && cmd != 1372 && cmd != 1374 && cmd != 1376 && cmd != 2035 && cmd != 2036 && cmd != 2037) {
                                                                    SDKLog.d("find cmd: " + cmd);
                                                                    if (cmd == 1002) {
                                                                        Constant.ishandshake = true;
                                                                    }

                                                                    newMessage.setTarget(mMessageParentHandler);
                                                                } else {
                                                                    sn = obj.has("sn") ? (Integer)obj.get("sn") : 0;
                                                                    mac = obj.has("mac") ? (String)obj.get("mac") : "";
                                                                    did = obj.has("did") ? (String)obj.get("did") : "";
                                                                    productKey = obj.has("productKey") ? (String)obj.get("productKey") : "";
                                                                    jointActionID = obj.has("jointActionID") ? (String)obj.get("jointActionID") : "";
                                                                    myOwnerDevice = this.getMyOwnerDevice(mac, productKey, did);
                                                                    list = GizDeviceJointActionCenter.getAllJointActionListByOwner(myOwnerDevice);
                                                                    var29 = list.iterator();

                                                                    while(var29.hasNext()) {
                                                                        GizDeviceJointAction jointAction = (GizDeviceJointAction)var29.next();
                                                                        if (jointAction.getJointActionID().equals(jointActionID)) {
                                                                            SDKLog.d("handle message<cmd=" + cmd + ", sn=" + sn + "> to jointAction: " + jointActionID);
                                                                            newMessage.setTarget(jointAction.messageHandler);
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                newMessage.setTarget(GizDeviceJointActionCenter.messageHandler);
                                                            }
                                                        } else {
                                                            sn = obj.has("sn") ? (Integer)obj.get("sn") : 0;
                                                            mac = obj.has("mac") ? (String)obj.get("mac") : "";
                                                            did = obj.has("did") ? (String)obj.get("did") : "";
                                                            productKey = obj.has("productKey") ? (String)obj.get("productKey") : "";
                                                            jointActionID = obj.has("schedulerID") ? (String)obj.get("schedulerID") : "";
                                                            myOwnerDevice = this.getMyOwnerDevice(mac, productKey, did);
                                                            list = GizDeviceSchedulerCenter.getAllSchedulerListByDevice(myOwnerDevice);
                                                            var29 = list.iterator();

                                                            while(var29.hasNext()) {
                                                                GizDeviceSchedulerSuper scheduler = (GizDeviceSchedulerSuper)var29.next();
                                                                if (scheduler.getSchedulerID().equals(jointActionID)) {
                                                                    if (scheduler instanceof GizDeviceScheduler) {
                                                                        GizDeviceScheduler schedulerCloud = (GizDeviceScheduler)scheduler;
                                                                        SDKLog.d("handle message<cmd=" + cmd + ", sn=" + sn + "> to schedulerCloud: " + schedulerCloud.getSchedulerID());
                                                                        newMessage.setTarget(schedulerCloud.messageHandler);
                                                                    } else if (scheduler instanceof GizDeviceSchedulerGateway) {
                                                                        GizDeviceSchedulerGateway schedulerGatway = (GizDeviceSchedulerGateway)scheduler;
                                                                        SDKLog.d("handle message<cmd=" + cmd + ", sn=" + sn + "> to schedulerGateway: " + schedulerGatway.getSchedulerID());
                                                                        newMessage.setTarget(schedulerGatway.messageHandler);
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        newMessage.setTarget(GizDeviceSchedulerCenter.messageHandler);
                                                    }
                                                } else {
                                                    sn = obj.has("sn") ? (Integer)obj.get("sn") : 0;
                                                    mac = obj.has("mac") ? (String)obj.get("mac") : "";
                                                    did = obj.has("did") ? (String)obj.get("did") : "";
                                                    productKey = obj.has("productKey") ? (String)obj.get("productKey") : "";
                                                    jointActionID = obj.has("sceneID") ? (String)obj.get("sceneID") : "";
                                                    myOwnerDevice = this.getMyOwnerDevice(mac, productKey, did);
                                                    list = GizDeviceSceneCenter.getTotalSceneListByOwner(myOwnerDevice);
                                                    var29 = list.iterator();

                                                    while(var29.hasNext()) {
                                                        GizDeviceScene scene = (GizDeviceScene)var29.next();
                                                        if (scene.getSceneID().equals(jointActionID)) {
                                                            SDKLog.d("handle message<cmd=" + cmd + ", sn=" + sn + "> to scene: " + jointActionID);
                                                            newMessage.setTarget(scene.messageHandler);
                                                            break;
                                                        }
                                                    }
                                                }
                                            } else {
                                                newMessage.setTarget(GizDeviceSceneCenter.messageHandler);
                                            }
                                        } else {
                                            sn = obj.has("sn") ? (Integer)obj.get("sn") : 0;
                                            mac = obj.has("mac") ? (String)obj.get("mac") : "";
                                            did = obj.has("did") ? (String)obj.get("did") : "";
                                            productKey = obj.has("productKey") ? (String)obj.get("productKey") : "";
                                            jointActionID = obj.has("groupID") ? (String)obj.get("groupID") : "";
                                            myOwnerDevice = this.getMyOwnerDevice(mac, productKey, did);
                                            list = GizDeviceGroupCenter.getTotalGroupListByOwner(myOwnerDevice);
                                            var29 = list.iterator();

                                            while(var29.hasNext()) {
                                                GizDeviceGroup group = (GizDeviceGroup)var29.next();
                                                if (group.getGroupID().equals(jointActionID)) {
                                                    SDKLog.d("handle message<cmd=" + cmd + ", sn=" + sn + "> to group: " + jointActionID);
                                                    newMessage.setTarget(group.messageHandler);
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        newMessage.setTarget(GizDeviceGroupCenter.messageHandler);
                                    }
                                } else {
                                    newMessage.setTarget(GizDeviceSharing.DeviceSharingHan);
                                }
                            } else {
                                newMessage.setTarget(GizDeviceOTA.han);
                            }
                        } else if (obj.has("centralDevice")) {
                            JSONObject deviceJson = obj.getJSONObject("centralDevice");
                            mac = deviceJson != null ? (deviceJson.has("mac") ? deviceJson.getString("mac") : "") : "";
                            did = deviceJson != null ? (deviceJson.has("did") ? deviceJson.getString("did") : "") : "";
                            productKey = deviceJson != null ? (deviceJson.has("productKey") ? deviceJson.getString("productKey") : "") : "";
                            int sn = obj.has("sn") ? (Integer)obj.get("sn") : 0;
                            if (sn != 0) {
                                Handler NormalHandler = getCentralHandlerBySN(sn);
                                if (NormalHandler != null) {
                                    newMessage.setTarget(NormalHandler);
                                }
                            } else {
                                List<GizWifiDevice> deviceList = SDKEventManager.getInstance().getTotalDeviceList();
                                GizWifiDevice device = SDKEventManager.getInstance().findDeviceInDeviceList(mac, did, productKey, deviceList);
                                if (device != null && device.getProductType() == GizWifiDeviceType.GizDeviceCenterControl) {
                                    GizWifiCentralControlDevice ccdevice = (GizWifiCentralControlDevice)device;
                                    SDKLog.d("handle message<cmd=" + cmd + ", sn=" + sn + "> to device: " + ccdevice.getMacAddress());
                                    Handler centralHandler = ccdevice.getCentralHandler();
                                    newMessage.setTarget(centralHandler);
                                }
                            }
                        }
                    } else {
                        setTargetHandler(newMessage, obj);
                    }
                } catch (JSONException var22) {
                    SDKLog.e(var22.toString());
                    var22.printStackTrace();
                }

                if (newMessage.getTarget() != null) {
                    newMessage.what = 5;
                    newMessage.obj = jsonStr;
                    newMessage.sendToTarget();
                }
            }
        } catch (Exception var23) {
            var23.printStackTrace();
            SDKLog.d("exception: " + var23.toString());
        }

    }

    protected static void setTargetHandler(Message newMessage, JSONObject obj) throws JSONException {
        int cmd = Integer.parseInt(obj.getString("cmd"));
        if (obj.has("sn") && cmd < 2000) {
            int sn = Integer.parseInt(obj.getString("sn"));
            Handler NormalHandler = getHandlerBySN(sn);
            if (NormalHandler != null) {
                newMessage.setTarget(NormalHandler);
            } else {
                SDKLog.d("not find device, cmd :" + cmd + "   sn  " + sn);
                SDKLog.d("device list :" + SDKEventManager.listMasking(SDKEventManager.getInstance().getDeviceListByProductKeys()));
            }
        } else {
            String mac = obj.getString("mac");
            String did = obj.getString("did");
            String productKey = obj.getString("productKey");
            List<GizWifiDevice> deviceList = SDKEventManager.getInstance().getDeviceList();
            Iterator var7 = deviceList.iterator();

            while(var7.hasNext()) {
                GizWifiDevice gizWifiDevice = (GizWifiDevice)var7.next();
                if (gizWifiDevice.getMacAddress().equals(mac) && gizWifiDevice.getDid().equals(did) && gizWifiDevice.getProductKey().equals(productKey)) {
                    newMessage.setTarget(gizWifiDevice.getHandler());
                }
            }
        }

    }

    protected static Handler getHandlerBySN(int sn) {
        List<GizWifiDevice> AlldeviceList = SDKEventManager.getInstance().getDeviceList();
        Iterator var2 = AlldeviceList.iterator();

        GizWifiDevice gizWifiDevice;
        boolean has;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            gizWifiDevice = (GizWifiDevice)var2.next();
            has = gizWifiDevice.getTimerHandler().hasMessages(sn);
            if (gizWifiDevice.getProductType() == GizWifiDeviceType.GizDeviceCenterControl && gizWifiDevice instanceof GizWifiCentralControlDevice) {
                GizWifiCentralControlDevice device = (GizWifiCentralControlDevice)gizWifiDevice;
                Handler centralTimerHandler = device.getCentralTimerHandler();
                boolean hasMessages = centralTimerHandler.hasMessages(sn);
                if (hasMessages) {
                    return centralTimerHandler;
                }
            }
        } while(!has);

        return gizWifiDevice.getHandler();
    }

    protected static Handler getCentralHandlerBySN(int sn) {
        List<GizWifiDevice> AlldeviceList = SDKEventManager.getInstance().getTotalDeviceList();
        synchronized(AlldeviceList) {
            Iterator var3 = AlldeviceList.iterator();

            boolean has;
            GizWifiCentralControlDevice central;
            do {
                if (!var3.hasNext()) {
                    return null;
                }

                GizWifiDevice gizWifiDevice = (GizWifiDevice)var3.next();
                has = false;
                central = null;
                if (gizWifiDevice.getProductType() == GizWifiDeviceType.GizDeviceCenterControl) {
                    central = (GizWifiCentralControlDevice)gizWifiDevice;
                    if (central != null && central.getCentralTimerHandler() != null) {
                        has = central.getCentralTimerHandler().hasMessages(sn);
                    }
                }
            } while(!has);

            return central.getCentralHandler();
        }
    }

    GizWifiDevice getMyOwnerDevice(String mac, String pk, String did) {
        List<GizWifiDevice> deviceList = GizWifiSDK.sharedInstance().getDeviceList();
        GizWifiDevice device = null;

        for(int i = 0; i < deviceList.size(); ++i) {
            GizWifiDevice gizWifiDevice = (GizWifiDevice)deviceList.get(i);
            if (!TextUtils.isEmpty(mac) && !TextUtils.isEmpty(pk) && gizWifiDevice.getMacAddress().equals(mac) && gizWifiDevice.getDid().equals(did) && gizWifiDevice.getProductKey().equals(pk)) {
                device = gizWifiDevice;
            }
        }

        return device;
    }

    public class MessageChildHandler extends Handler {
        public MessageChildHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 4:
                    String jsonStr = (String)msg.obj;
                    MessageHandler.this.sendJsonStrToGizWifiSDKDaemon(jsonStr);
                default:
            }
        }
    }
}
