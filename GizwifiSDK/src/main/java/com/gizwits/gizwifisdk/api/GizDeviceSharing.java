//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingStatus;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingType;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingWay;
import com.gizwits.gizwifisdk.enumration.GizMessageStatus;
import com.gizwits.gizwifisdk.enumration.GizMessageType;
import com.gizwits.gizwifisdk.enumration.GizUserAccountType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceSharingListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceSharing {
    private static GizDeviceSharingListener mListener;
    private static final int MSG_RECV = 5;
    static Handler DeviceSharingHan = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 5:
                    String jsonStr = (String)msg.obj;

                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        int cmd = Integer.parseInt(obj.getString("cmd"));
                        int sn;
                        if (cmd <= 2000 && obj.has("sn")) {
                            sn = Integer.parseInt(obj.getString("sn"));
                        } else {
                            sn = cmd;
                        }

                        if (GizDeviceSharing.mListener != null) {
                            GizDeviceSharing.didSetListener(cmd, obj, GizDeviceSharing.mListener, sn);
                        }
                    } catch (NumberFormatException var7) {
                        var7.printStackTrace();
                    } catch (JSONException var8) {
                        var8.printStackTrace();
                    }
                default:
            }
        }
    };
    static Handler hd = new Handler() {
        public void handleMessage(Message msg) {
            int cmd = (Integer)msg.obj;
            switch(cmd) {
                case 1152:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    List<GizUserInfo> list = new ArrayList();
                    GizDeviceSharing.onDidGetBindingUsers(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, (String)null, list);
                case 1153:
                case 1155:
                case 1157:
                case 1159:
                case 1161:
                case 1163:
                case 1165:
                case 1167:
                case 1169:
                case 1171:
                case 1173:
                default:
                    break;
                case 1154:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    GizDeviceSharing.onDidUnbindGuestUser(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, (String)null, (String)null);
                    break;
                case 1156:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    List<GizDeviceSharingInfo> deviceSharingInfos = new ArrayList();
                    GizDeviceSharing.onDidGetDeviceSharingInfos(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, (String)null, deviceSharingInfos);
                    break;
                case 1158:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    GizDeviceSharing.onDidSharingDevice(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, (String)null, -1, (Bitmap)null);
                    break;
                case 1160:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    GizDeviceSharing.onDidRevokeDeviceSharing(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, -1);
                    break;
                case 1162:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    GizDeviceSharing.onDidAcceptDeviceSharing(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, -1);
                    break;
                case 1164:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    GizDeviceSharing.onDidCheckDeviceSharingInfoByQRCode(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, (String)null, (String)null, (String)null, (String)null);
                    break;
                case 1166:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    GizDeviceSharing.onDidAcceptDeviceSharingByQRCode(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT);
                    break;
                case 1168:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    GizDeviceSharing.onDidModifySharingInfo(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, -1);
                    break;
                case 1170:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    List<GizMessage> messageList = new ArrayList();
                    GizDeviceSharing.onDidQueryMessageList(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, messageList);
                    break;
                case 1172:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    GizDeviceSharing.onDidMarkMessageStatus(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, (String)null);
                    break;
                case 1174:
                    GizDeviceSharing.hd.removeMessages(msg.what);
                    GizDeviceSharing.onDidSharingOwnerTransfer(GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, (String)null, (String)null);
            }

        }
    };
    private static int errorCode;
    private static String deviceID;
    private static String sharingID;
    private static String qRCodeUrl;
    private static String timestart;

    public GizDeviceSharing() {
    }

    protected static String listMasking(List<?> list) {
        String masking = "{size= " + (list == null ? "0" : list.size()) + ", ";
        if (list != null) {
            for(int i = 0; i < list.size(); ++i) {
                masking = masking + "[";
                Object objT = list.get(i);
                if (objT instanceof GizUserInfo) {
                    GizUserInfo object = (GizUserInfo)objT;
                    masking = masking + "[" + object.infoMasking() + "]";
                } else if (objT instanceof GizDeviceSharingInfo) {
                    GizDeviceSharingInfo object = (GizDeviceSharingInfo)objT;
                    masking = masking + "[" + object.infoMasking() + "]";
                } else if (objT instanceof GizMessage) {
                    GizMessage object = (GizMessage)objT;
                    masking = masking + "[" + object.infoMasking() + "]";
                }

                masking = masking + ", ";
            }
        }

        return masking.substring(0, masking.length() - 2) + "}";
    }

    public static void setListener(GizDeviceSharingListener listener) {
        SDKLog.a("Start => listener: " + (listener == null ? "null" : listener));
        mListener = listener;
        SDKLog.a("End <= ");
    }

    private static void sendMes2Demo(JSONObject obj) {
        MessageHandler.getSingleInstance().send(obj.toString());
    }

    public static void getBindingUsers(String token, String deviceID) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "deviceID: " + Utils.dataMasking(deviceID));
        if (!Constant.ishandshake) {
            List<GizUserInfo> list = new ArrayList();
            onDidGetBindingUsers(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, deviceID, list);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1151);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("did", deviceID);
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(20000, 1152, sn);
            SDKLog.a("End <= ");
        }
    }

    public static void unbindUser(String token, String deviceID, String guestUID) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "deviceID: " + Utils.dataMasking(deviceID) + "guestUID: " + guestUID);
        if (!Constant.ishandshake) {
            onDidUnbindGuestUser(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, deviceID, guestUID);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1153);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("did", deviceID);
                obj.put("uid", guestUID);
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(20000, 1154, sn);
            SDKLog.a("End <= ");
        }
    }

    public static void sharingOwnerTransfer(String token, String deviceID, String guestUID) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "deviceID: " + Utils.dataMasking(deviceID) + "guestUID: " + guestUID);
        if (!Constant.ishandshake) {
            onDidSharingOwnerTransfer(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, deviceID, guestUID);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1173);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("did", deviceID);
                obj.put("guestUid", guestUID);
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(20000, 1174, sn);
            SDKLog.a("End <= ");
        }
    }

    public static void getDeviceSharingInfos(String token, GizDeviceSharingType sharingType, String deviceID) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "deviceID: " + Utils.dataMasking(deviceID) + "sharingType: " + (sharingType == null ? "null" : sharingType.name()));
        if (!Constant.ishandshake) {
            List<GizDeviceSharingInfo> list = new ArrayList();
            onDidGetDeviceSharingInfos(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, deviceID, list);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1155);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("did", deviceID);
                obj.put("sharing_type", sharingType.ordinal());
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(20000, 1156, sn);
            SDKLog.a("End <= ");
        }
    }

    public static void sharingDevice(String token, String deviceID, GizDeviceSharingWay sharingWay, String guestUser, GizUserAccountType guestUserType) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "deviceID: " + Utils.dataMasking(deviceID) + "sharingWay: " + (sharingWay == null ? "null" : sharingWay.name()) + "guestUser: " + Utils.dataMasking(guestUser) + "guestUserType: " + (guestUserType == null ? "null" : guestUserType.name()));
        if (Constant.ishandshake && sharingWay != null) {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1157);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("did", deviceID);
                obj.put("type", sharingWay.ordinal());
                if (sharingWay.ordinal() == 0) {
                    SDKLog.b("device_bind_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), Utils.changeString("token") + ":" + Utils.changeString(Utils.dataMasking(token)) + ", " + Utils.changeString("did") + " : " + Utils.changeString(Utils.dataMasking(deviceID)) + ", " + Utils.changeString("bind_type") + " : " + Utils.changeString("UserSharing") + DateUtil.getLogString(SDKEventManager.mContext));
                } else {
                    SDKLog.b("device_bind_start", GizWifiErrorCode.GIZ_SDK_SUCCESS.name(), Utils.changeString("token") + ":" + Utils.changeString(Utils.dataMasking(token)) + ", " + Utils.changeString("did") + " : " + Utils.changeString(Utils.dataMasking(deviceID)) + ", " + Utils.changeString("bind_type") + " : " + Utils.changeString("QRSharing") + DateUtil.getLogString(SDKEventManager.mContext));
                }

                if (guestUserType != null) {
                    switch(guestUserType.ordinal()) {
                        case 0:
                            obj.put("username", guestUser);
                            break;
                        case 1:
                            obj.put("phone", guestUser);
                            break;
                        case 2:
                            obj.put("email", guestUser);
                            break;
                        case 3:
                            obj.put("uid", guestUser);
                    }
                }
            } catch (JSONException var8) {
                SDKLog.e(var8.toString());
                var8.printStackTrace();
            }

            timestart = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
            sendMes2Demo(obj);
            makeTimer(20000, 1158, sn);
            SDKLog.a("End <= ");
        } else {
            onDidSharingDevice(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, deviceID, -1, (Bitmap)null);
            SDKLog.a("End <= ");
        }
    }

    public static void revokeDeviceSharing(String token, int sharingID) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "sharingID: " + sharingID);
        if (!Constant.ishandshake) {
            onDidRevokeDeviceSharing(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, sharingID);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1159);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("id", sharingID);
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(20000, 1160, sn);
            SDKLog.a("End <= ");
        }
    }

    public static void acceptDeviceSharing(String token, int sharingID, boolean accept) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "sharingID: " + sharingID + "accept: " + accept);
        if (!Constant.ishandshake) {
            onDidAcceptDeviceSharing(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, sharingID);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1161);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("id", sharingID);
                if (accept) {
                    obj.put("status", 1);
                } else {
                    obj.put("status", 2);
                }
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(20000, 1162, sn);
            SDKLog.a("End <= ");
        }
    }

    public static void checkDeviceSharingInfoByQRCode(String token, String QRCode) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "QRCode: " + Utils.dataMasking(QRCode));
        if (!Constant.ishandshake) {
            onDidCheckDeviceSharingInfoByQRCode(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, (String)null, (String)null, (String)null, (String)null);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1163);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("code", QRCode);
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(20000, 1164, sn);
            SDKLog.a("End <= ");
        }
    }

    public static void acceptDeviceSharingByQRCode(String token, String QRCode) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "QRCode: " + Utils.dataMasking(QRCode));
        if (!Constant.ishandshake) {
            onDidAcceptDeviceSharingByQRCode(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1165);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("code", QRCode);
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(20000, 1166, sn);
            SDKLog.a("End <= ");
        }
    }

    public static void modifySharingInfo(String token, int sharingID, String sharingAlias) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "sharingID: " + sharingID + "sharingAlias: " + sharingAlias);
        if (!Constant.ishandshake) {
            onDidModifySharingInfo(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, sharingID);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1167);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("id", sharingID);
                obj.put("user_alias", sharingAlias);
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(20000, 1168, sn);
            SDKLog.a("End <= ");
        }
    }

    public static void queryMessageList(String token, GizMessageType messageType) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "messageType: " + (messageType == null ? "null" : messageType.name()));
        if (!Constant.ishandshake) {
            List<GizMessage> list = new ArrayList();
            onDidQueryMessageList(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, list);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1169);
                obj.put("sn", sn);
                obj.put("token", token);
                if (messageType != null) {
                    obj.put("type", messageType.ordinal());
                }
            } catch (JSONException var5) {
                SDKLog.e(var5.toString());
                var5.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(20000, 1170, sn);
            SDKLog.a("End <= ");
        }
    }

    public static void markMessageStatus(String token, String messageID, GizMessageStatus messageStatus) {
        SDKLog.a("Start => token: " + Utils.dataMasking(token) + "messageID: " + messageID + "messageStatus: " + (messageStatus == null ? "null" : messageStatus.name()));
        if (!Constant.ishandshake) {
            onDidMarkMessageStatus(GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, messageID);
            SDKLog.a("End <= ");
        } else {
            JSONObject obj = new JSONObject();
            int sn = Utils.getSn();

            try {
                obj.put("cmd", 1171);
                obj.put("sn", sn);
                obj.put("token", token);
                obj.put("id", messageID);
                if (messageStatus != null) {
                    obj.put("status", messageStatus.ordinal());
                }
            } catch (JSONException var6) {
                SDKLog.e(var6.toString());
                var6.printStackTrace();
            }

            sendMes2Demo(obj);
            makeTimer(20000, 1172, sn);
            SDKLog.a("End <= ");
        }
    }

    protected static void onDidGetBindingUsers(GizWifiErrorCode result, String deviceID, List<GizUserInfo> bindUsers) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + "deviceID: " + Utils.dataMasking(deviceID) + "bindUsers: " + listMasking(bindUsers));
        if (mListener != null) {
            mListener.didGetBindingUsers(result, deviceID, bindUsers);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidUnbindGuestUser(GizWifiErrorCode result, String deviceID, String guestUID) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + "deviceID: " + Utils.dataMasking(deviceID) + "guestUID: " + Utils.dataMasking(guestUID));
        if (mListener != null) {
            mListener.didUnbindUser(result, deviceID, guestUID);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidSharingOwnerTransfer(GizWifiErrorCode result, String deviceID, String guestUID) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + "deviceID: " + Utils.dataMasking(deviceID) + "guestUID: " + Utils.dataMasking(guestUID));
        if (mListener != null) {
            mListener.didSharingOwnerTransfer(result, deviceID, guestUID);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidGetDeviceSharingInfos(GizWifiErrorCode result, String deviceID, List<GizDeviceSharingInfo> deviceSharingInfos) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + "deviceID: " + Utils.dataMasking(deviceID) + "deviceSharingInfos: " + listMasking(deviceSharingInfos));
        if (mListener != null) {
            mListener.didGetDeviceSharingInfos(result, deviceID, deviceSharingInfos);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidSharingDevice(GizWifiErrorCode result, String deviceID, int sharingID, Bitmap QRCodeImage) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + "deviceID: " + Utils.dataMasking(deviceID) + "sharingID: " + sharingID + "QRCodeImage: " + QRCodeImage == null ? "null" : "******");
        if (mListener != null) {
            if (result != GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN) {
                String end = DateUtil.getCurTimeByFormat("yyyy-MM-dd HH:mm:ss");
                long diff = DateUtil.getDiff(timestart, end);
                SDKLog.b("device_bind_end", result.name(), Utils.changeString("did") + ":" + Utils.changeString(TextUtils.isEmpty(deviceID + "") ? "null" : Utils.dataMasking(deviceID) + "") + " " + Utils.changeString("elapsed_time") + ":" + diff);
            }

            mListener.didSharingDevice(result, deviceID, sharingID, QRCodeImage);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidRevokeDeviceSharing(GizWifiErrorCode result, int sharingID) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + "sharingID: " + sharingID);
        if (mListener != null) {
            mListener.didRevokeDeviceSharing(result, sharingID);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidAcceptDeviceSharing(GizWifiErrorCode result, int sharingID) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + "sharingID: " + sharingID);
        if (mListener != null) {
            mListener.didAcceptDeviceSharing(result, sharingID);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidCheckDeviceSharingInfoByQRCode(GizWifiErrorCode result, String userName, String productName, String deviceAlias, String expiredAt) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + "userName: " + Utils.dataMasking(userName) + "productName: " + productName + "deviceAlias: " + deviceAlias);
        if (mListener != null) {
            mListener.didCheckDeviceSharingInfoByQRCode(result, userName, productName, deviceAlias, expiredAt);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidAcceptDeviceSharingByQRCode(GizWifiErrorCode result) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name());
        if (mListener != null) {
            mListener.didAcceptDeviceSharingByQRCode(result);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidModifySharingInfo(GizWifiErrorCode result, int sharingID) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", sharingID: " + sharingID);
        if (mListener != null) {
            mListener.didModifySharingInfo(result, sharingID);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidQueryMessageList(GizWifiErrorCode result, List<GizMessage> messageList) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + ", messageList: " + listMasking(messageList));
        if (mListener != null) {
            mListener.didQueryMessageList(result, messageList);
            SDKLog.d("Callback end");
        }

    }

    protected static void onDidMarkMessageStatus(GizWifiErrorCode result, String messageID) {
        SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
        SDKLog.d("Callback begin, result: " + result.name() + "messageID: " + messageID);
        if (mListener != null) {
            mListener.didMarkMessageStatus(result, messageID);
            SDKLog.d("Callback end");
        }

    }

    private static void didSetListener(int cmd, JSONObject obj, GizDeviceSharingListener mListener2, int sn) throws JSONException {
        String guestUID;
        int i;
        String phone;
        int status;
        int sharingType;
        switch(cmd) {
            case 1152:
                hd.removeMessages(sn);
                errorCode = obj.getInt("errorCode");
                if (obj.has("did")) {
                    deviceID = obj.getString("did");
                }

                List<GizUserInfo> bindUsers = new ArrayList();
                if (obj.has("users")) {
                    JSONArray users = obj.getJSONArray("users");

                    for(i = 0; i < users.length(); ++i) {
                        JSONObject userobj = (JSONObject)users.get(i);
                        GizUserInfo info = new GizUserInfo();
                        if (userobj.has("created_at")) {
                            phone = userobj.getString("created_at");
                            info.setDeviceBindTime(phone);
                        }

                        if (userobj.has("uid")) {
                            phone = userobj.getString("uid");
                            info.setUid(phone);
                        }

                        if (userobj.has("username")) {
                            phone = userobj.getString("username");
                            info.setUsername(phone);
                        }

                        if (userobj.has("email")) {
                            phone = userobj.getString("email");
                            info.setEmail(phone);
                        }

                        if (userobj.has("phone")) {
                            phone = userobj.getString("phone");
                            info.setPhone(phone);
                        }

                        bindUsers.add(info);
                    }
                }

                onDidGetBindingUsers(GizWifiErrorCode.valueOf(errorCode), deviceID, bindUsers);
            case 1153:
            case 1155:
            case 1157:
            case 1159:
            case 1161:
            case 1163:
            case 1165:
            case 1167:
            case 1169:
            case 1171:
            case 1173:
            default:
                break;
            case 1154:
                hd.removeMessages(sn);
                errorCode = obj.getInt("errorCode");
                if (obj.has("did")) {
                    deviceID = obj.getString("did");
                }

                guestUID = null;
                if (obj.has("uid")) {
                    guestUID = obj.getString("uid");
                }

                onDidUnbindGuestUser(GizWifiErrorCode.valueOf(errorCode), deviceID, guestUID);
                break;
            case 1156:
                hd.removeMessages(sn);
                errorCode = obj.getInt("errorCode");
                if (obj.has("did")) {
                    deviceID = obj.getString("did");
                }

                sharingType = 0;
                if (obj.has("sharing_type")) {
                    sharingType = obj.getInt("sharing_type");
                }

                List<GizDeviceSharingInfo> deviceSharingInfos = new ArrayList();
                if (obj.has("objects")) {
                    JSONArray objects = obj.getJSONArray("objects");

                    for(i = 0; i < objects.length(); ++i) {
                        GizDeviceSharingInfo info = new GizDeviceSharingInfo();
                        JSONObject myobj = (JSONObject)objects.get(i);
                        int type;
                        if (myobj.has("id")) {
                            type = myobj.getInt("id");
                            info.setId(type);
                        }

                        if (1 == sharingType) {
                            info.setType(GizDeviceSharingType.GizDeviceSharingToMe);
                        } else {
                            info.setType(GizDeviceSharingType.GizDeviceSharingByMe);
                        }

                        if (myobj.has("type")) {
                            type = myobj.getInt("type");
                            if (type == 0) {
                                info.setWay(GizDeviceSharingWay.GizDeviceSharingByNormal);
                            } else {
                                info.setWay(GizDeviceSharingWay.GizDeviceSharingByQRCode);
                            }
                        }

                        GizUserInfo myinfo = new GizUserInfo();
                        String expired_at;
                        if (myobj.has("uid")) {
                            expired_at = myobj.getString("uid");
                            myinfo.setUid(expired_at);
                        }

                        if (myobj.has("username")) {
                            expired_at = myobj.getString("username");
                            myinfo.setUsername(expired_at);
                        }

                        if (myobj.has("user_alias")) {
                            expired_at = myobj.getString("user_alias");
                            myinfo.setRemark(expired_at);
                        }

                        if (myobj.has("email")) {
                            expired_at = myobj.getString("email");
                            myinfo.setEmail(expired_at);
                        }

                        if (myobj.has("phone")) {
                            expired_at = myobj.getString("phone");
                            myinfo.setPhone(expired_at);
                        }

                        if (myobj.has("did")) {
                            expired_at = myobj.getString("did");
                            info.setDeviceID(expired_at);
                        }

                        if (myobj.has("product_name")) {
                            expired_at = myobj.getString("product_name");
                            info.setProductName(expired_at);
                        }

                        if (myobj.has("dev_alias")) {
                            expired_at = myobj.getString("dev_alias");
                            info.setDeviceAlias(expired_at);
                        }

                        if (myobj.has("status")) {
                            status = myobj.getInt("status");
                            switch(status) {
                                case 0:
                                    info.setStatus(GizDeviceSharingStatus.GizDeviceSharingNotAccepted);
                                    break;
                                case 1:
                                    info.setStatus(GizDeviceSharingStatus.GizDeviceSharingAccepted);
                                    break;
                                case 2:
                                    info.setStatus(GizDeviceSharingStatus.GizDeviceSharingRefused);
                                    break;
                                case 3:
                                    info.setStatus(GizDeviceSharingStatus.GizDeviceSharingCancelled);
                            }
                        }

                        if (myobj.has("created_at")) {
                            expired_at = myobj.getString("created_at");
                            info.setCreatedAt(expired_at);
                        }

                        if (myobj.has("updated_at")) {
                            expired_at = myobj.getString("updated_at");
                            info.setUpdatedAt(expired_at);
                        }

                        if (myobj.has("expired_at")) {
                            expired_at = myobj.getString("expired_at");
                            info.setExpiredAt(expired_at);
                        }

                        info.setUserInfo(myinfo);
                        deviceSharingInfos.add(info);
                    }
                }

                onDidGetDeviceSharingInfos(GizWifiErrorCode.valueOf(errorCode), deviceID, deviceSharingInfos);
                break;
            case 1158:
                hd.removeMessages(sn);
                qRCodeUrl = null;
                deviceID = null;
                sharingID = null;
                sharingType = -1;
                errorCode = obj.getInt("errorCode");
                if (obj.has("did")) {
                    deviceID = obj.getString("did");
                }

                if (obj.has("id")) {
                    sharingType = obj.getInt("id");
                }

                if (obj.has("qr_content")) {
                    qRCodeUrl = obj.getString("qr_content");
                }

                if (qRCodeUrl != null && qRCodeUrl.contains("type=") && qRCodeUrl.contains("code=")) {
                    qRCodeUrl = qRCodeUrl.substring(1, qRCodeUrl.length() - 1);
                    Bitmap createQRImage = Utils.createQRImage(qRCodeUrl, 500, 500);
                    onDidSharingDevice(GizWifiErrorCode.valueOf(errorCode), deviceID, sharingType, createQRImage);
                } else {
                    onDidSharingDevice(GizWifiErrorCode.valueOf(errorCode), deviceID, sharingType, (Bitmap)null);
                }
                break;
            case 1160:
                hd.removeMessages(sn);
                errorCode = obj.getInt("errorCode");
                i = -1;
                if (obj.has("id")) {
                    i = obj.getInt("id");
                }

                onDidRevokeDeviceSharing(GizWifiErrorCode.valueOf(errorCode), i);
                break;
            case 1162:
                hd.removeMessages(sn);
                errorCode = obj.getInt("errorCode");
                int id = -1;
                if (obj.has("id")) {
                    id = obj.getInt("id");
                }

                mListener2.didAcceptDeviceSharing(GizWifiErrorCode.valueOf(errorCode), id);
                break;
            case 1164:
                hd.removeMessages(sn);
                errorCode = obj.getInt("errorCode");
                String owner = null;
                if (obj.has("owner")) {
                    owner = obj.getString("owner");
                }

                phone = null;
                if (obj.has("product_name")) {
                    phone = obj.getString("product_name");
                }

                String deviceAlias = null;
                if (obj.has("dev_alias")) {
                    deviceAlias = obj.getString("dev_alias");
                }

                String expiredAt = null;
                if (obj.has("expired_at")) {
                    expiredAt = obj.getString("expired_at");
                }

                mListener2.didCheckDeviceSharingInfoByQRCode(GizWifiErrorCode.valueOf(errorCode), owner, phone, deviceAlias, expiredAt);
                break;
            case 1166:
                hd.removeMessages(sn);
                errorCode = obj.getInt("errorCode");
                onDidAcceptDeviceSharingByQRCode(GizWifiErrorCode.valueOf(errorCode));
                break;
            case 1168:
                hd.removeMessages(sn);
                errorCode = obj.getInt("errorCode");
                status = -1;
                if (obj.has("id")) {
                    status = obj.getInt("id");
                }

                mListener2.didModifySharingInfo(GizWifiErrorCode.valueOf(errorCode), status);
                break;
            case 1170:
                hd.removeMessages(sn);
                errorCode = obj.getInt("errorCode");
                List<GizMessage> messageList = new ArrayList();
                if (obj.has("objects")) {
                    JSONArray objects = obj.getJSONArray("objects");

                    for(i = 0; i < objects.length(); ++i) {
                        JSONObject myobj = (JSONObject)objects.get(i);
                        GizMessage message = new GizMessage();
                        String content;
                        if (myobj.has("id")) {
                            content = myobj.getString("id");
                            message.setId(content);
                        }

                        if (myobj.has("created_at")) {
                            content = myobj.getString("created_at");
                            message.setCreatedAt(content);
                        }

                        if (myobj.has("updated_at")) {
                            content = myobj.getString("updated_at");
                            message.setUpdatedAt(content);
                        }

                        if (myobj.has("type")) {
                            status = myobj.getInt("type");
                            switch(status) {
                                case 0:
                                    message.setType(GizMessageType.GizMessageSystem);
                                    break;
                                case 1:
                                    message.setType(GizMessageType.GizMessageSharing);
                            }
                        }

                        if (myobj.has("status")) {
                            status = myobj.getInt("status");
                            switch(status) {
                                case 0:
                                    message.setStatus(GizMessageStatus.GizMessageUnread);
                                    break;
                                case 1:
                                    message.setStatus(GizMessageStatus.GizMessageRead);
                                    break;
                                case 2:
                                    message.setStatus(GizMessageStatus.GizMessageDeleted);
                            }
                        }

                        if (myobj.has("content")) {
                            content = myobj.getString("content");
                            message.setContent(content);
                        }

                        messageList.add(message);
                    }
                }

                mListener2.didQueryMessageList(GizWifiErrorCode.valueOf(errorCode), messageList);
                break;
            case 1172:
                errorCode = obj.getInt("errorCode");
                hd.removeMessages(sn);
                String messageID = null;
                if (obj.has("id")) {
                    messageID = obj.getString("id");
                }

                mListener2.didMarkMessageStatus(GizWifiErrorCode.valueOf(errorCode), messageID);
                break;
            case 1174:
                hd.removeMessages(sn);
                errorCode = obj.getInt("errorCode");
                if (obj.has("did")) {
                    deviceID = obj.getString("did");
                }

                guestUID = null;
                if (obj.has("guestUid")) {
                    guestUID = obj.getString("guestUid");
                }

                onDidSharingOwnerTransfer(GizWifiErrorCode.valueOf(errorCode), deviceID, guestUID);
        }

    }

    private static void makeTimer(int timeout, int cmd, int sn) {
        Message mes = Message.obtain();
        mes.what = sn;
        mes.obj = cmd;
        hd.sendMessageDelayed(mes, (long)timeout);
    }
}
