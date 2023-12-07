//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.listener;

import android.graphics.Bitmap;
import com.gizwits.gizwifisdk.api.GizDeviceSharingInfo;
import com.gizwits.gizwifisdk.api.GizMessage;
import com.gizwits.gizwifisdk.api.GizUserInfo;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import java.util.List;

public class GizDeviceSharingListener {
    public GizDeviceSharingListener() {
    }

    public void didGetBindingUsers(GizWifiErrorCode result, String deviceID, List<GizUserInfo> bindUsers) {
    }

    public void didUnbindUser(GizWifiErrorCode result, String deviceID, String guestUID) {
    }

    public void didSharingOwnerTransfer(GizWifiErrorCode result, String deviceID, String guestUID) {
    }

    public void didGetDeviceSharingInfos(GizWifiErrorCode result, String deviceID, List<GizDeviceSharingInfo> deviceSharingInfos) {
    }

    public void didSharingDevice(GizWifiErrorCode result, String deviceID, int sharingID, Bitmap QRCodeImage) {
    }

    public void didRevokeDeviceSharing(GizWifiErrorCode result, int sharingID) {
    }

    public void didAcceptDeviceSharing(GizWifiErrorCode result, int sharingID) {
    }

    public void didCheckDeviceSharingInfoByQRCode(GizWifiErrorCode result, String userName, String productName, String deviceAlias, String expiredAt) {
    }

    public void didAcceptDeviceSharingByQRCode(GizWifiErrorCode result) {
    }

    public void didModifySharingInfo(GizWifiErrorCode result, int sharingID) {
    }

    public void didQueryMessageList(GizWifiErrorCode result, List<GizMessage> messageList) {
    }

    public void didMarkMessageStatus(GizWifiErrorCode result, String messageID) {
    }
}
