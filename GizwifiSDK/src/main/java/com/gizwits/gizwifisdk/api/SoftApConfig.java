//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.NetworkRequest.Builder;
import android.os.Build.VERSION;
import android.util.Log;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SoftApConfig {
    private static SoftApConfig mSocketClient = null;
    ConnectivityManager connectivityManager;
    private DatagramSocket socket = null;
    private InetAddress serverAddress = null;
    private SoftApConfig.SocketThread mSocketThread;
    private boolean isStart = false;
    private String pwd;
    private String ssid;

    private SoftApConfig() {
    }

    public static SoftApConfig getInstance() {
        if (mSocketClient == null) {
            Class var0 = SoftApConfig.class;
            synchronized(SoftApConfig.class) {
                mSocketClient = new SoftApConfig();
            }
        }

        return mSocketClient;
    }

    public void startConfig(Context mContext, String ssid, String pwd) {
        this.pwd = pwd;
        this.ssid = ssid;
        this.connectivityManager = (ConnectivityManager)mContext.getSystemService("connectivity");
        if (VERSION.SDK_INT >= 21) {
            try {
                Builder builder = new Builder();
                builder.addCapability(12);
                builder.addTransportType(1);
                this.connectivityManager.requestNetwork(builder.build(), new NetworkCallback() {
                    public void onAvailable(Network network) {
                        Log.e("netId", network.toString() + " " + 23);
                        if (VERSION.SDK_INT >= 23) {
                            Log.e("netId", "bindProcess");
                            SoftApConfig.this.connectivityManager.bindProcessToNetwork(network);
                        } else {
                            Log.e("netId", "setProcess");
                            ConnectivityManager var10000 = SoftApConfig.this.connectivityManager;
                            ConnectivityManager.setProcessDefaultNetwork(network);
                        }

                        try {
                            SoftApConfig.this.socket = new DatagramSocket(12345);
                            SoftApConfig.this.serverAddress = InetAddress.getByName("10.10.100.254");
                        } catch (Exception var3) {
                            var3.printStackTrace();
                        }

                        if (!SoftApConfig.this.isStart) {
                            SoftApConfig.this.isStart = true;
                            SoftApConfig.this.mSocketThread = SoftApConfig.this.new SocketThread();
                            SoftApConfig.this.mSocketThread.start();
                        }

                        SoftApConfig.this.connectivityManager.unregisterNetworkCallback(this);
                    }
                });
            } catch (Exception var6) {
            }
        }

    }

    public void stopConfig() {
        this.isStart = false;
        this.mSocketThread = null;
        if (this.connectivityManager != null) {
            if (VERSION.SDK_INT >= 23) {
                this.connectivityManager.bindProcessToNetwork((Network)null);
            } else {
                ConnectivityManager var10000 = this.connectivityManager;
                ConnectivityManager.setProcessDefaultNetwork((Network)null);
            }
        }

        if (this.socket != null) {
            this.socket.close();
        }

    }

    private byte[] getSendData() {
        byte[] pwdByte = this.pwd.getBytes();
        byte[] ssidByte = this.ssid.getBytes();
        byte[] data = new byte[pwdByte.length + ssidByte.length + 12];
        int offset = 0;
        offset = offset + 1;
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

    class SocketThread extends Thread {
        SocketThread() {
        }

        public void run() {
            super.run();

            while(SoftApConfig.this.isStart) {
                try {
                    byte[] data = SoftApConfig.this.getSendData();
                    Log.e("配网指令", Utils.bytesToHexString(data, " "));
                    DatagramPacket packet = new DatagramPacket(data, data.length, SoftApConfig.this.serverAddress, 12414);
                    SoftApConfig.this.socket.send(packet);
                } catch (Exception var4) {
                    var4.printStackTrace();
                }

                try {
                    sleep(5000L);
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }
            }

        }
    }
}
