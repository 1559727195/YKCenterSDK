//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hiflying.smartlink;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.hiflying.commons.log.HFLog;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSmartLinker implements ISmartLinker {
    private static final int MSG_SMART_LINK_NEW_DEVICE = 1;
    private static final int MSG_SMART_LINK_COMPLETED = 2;
    /** @deprecated */
    @Deprecated
    public static int MAX_DURATION_RECEIVE_SMART_CONFIG = 60000;
    /** @deprecated */
    @Deprecated
    public static int MAX_DURATION_WAIT_MORE_DEVICE = 10000;
    public static int PORT_RECEIVE_SMART_CONFIG = 49999;
    protected boolean mIsSmartLinking;
    protected OnSmartLinkListener mOnSmartLinkListener;
    protected DatagramSocket mSmartConfigSocket;
    private HashSet<String> mDeviceMacs = new HashSet();
    protected int mTimeoutPeriod = 60000;
    protected boolean mIsTimeout;
    protected Context mContext;
    private int mWaitMoreDevicePeriod;
    private Handler mHander;

    protected AbstractSmartLinker() {
        this.mWaitMoreDevicePeriod = MAX_DURATION_WAIT_MORE_DEVICE;
        this.mHander = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what) {
                case 1:
                    if (AbstractSmartLinker.this.mOnSmartLinkListener != null) {
                        AbstractSmartLinker.this.mOnSmartLinkListener.onLinked((SmartLinkedModule)msg.obj);
                    }
                    break;
                case 2:
                    if (AbstractSmartLinker.this.mOnSmartLinkListener != null) {
                        if (AbstractSmartLinker.this.mDeviceMacs.isEmpty()) {
                            AbstractSmartLinker.this.mOnSmartLinkListener.onTimeOut();
                        } else {
                            AbstractSmartLinker.this.mOnSmartLinkListener.onCompleted();
                        }
                    }
                }

            }
        };
    }

    protected void initSmartConfigSocket() throws SocketException {
        this.mSmartConfigSocket = new DatagramSocket(PORT_RECEIVE_SMART_CONFIG);
        this.mSmartConfigSocket.setSoTimeout(1200);
    }

    protected void closeDestroySmartConfigSocket() {
        if (this.mSmartConfigSocket != null) {
            this.mSmartConfigSocket.close();
            this.mSmartConfigSocket.disconnect();
            this.mSmartConfigSocket = null;
        }

    }

    protected DatagramSocket getSmartConfigSocket() {
        return this.mSmartConfigSocket;
    }

    private Runnable[] createSenderRunnables(final CountDownLatch latch, final Runnable[] runnables) {
        Runnable[] _runnnables = null;
        if (runnables != null) {
            _runnnables = new Runnable[runnables.length];

            for(int i = 0; i < runnables.length; ++i) {
                int finalI = i;
                _runnnables[i] = new Runnable() {
                    public void run() {
                        runnables[finalI].run();
                        latch.countDown();
                    }
                };
            }
        }

        return _runnnables;
    }

    private Runnable createReceiverRunnable(final CountDownLatch latch) {
        return new Runnable() {
            public void run() {
                Runnable receiveRunnable = AbstractSmartLinker.this.setupReceiveAction();
                receiveRunnable.run();
                latch.countDown();
            }
        };
    }

    protected Runnable setupReceiveAction() {
        return new Runnable() {
            public void run() {
                AbstractSmartLinker.this.mDeviceMacs.clear();
                byte[] buffer = new byte[1024];
                byte[] datas = null;
                DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
                long startTime = System.currentTimeMillis();
                long findDeivceTime = 9223372036854775807L;

                while(AbstractSmartLinker.this.mIsSmartLinking) {
                    long currentTime = System.currentTimeMillis();
                    if (!AbstractSmartLinker.this.mIsSmartLinking || currentTime - startTime > (long)AbstractSmartLinker.this.mTimeoutPeriod || currentTime - findDeivceTime > (long)AbstractSmartLinker.this.mWaitMoreDevicePeriod) {
                        break;
                    }

                    try {
                        AbstractSmartLinker.this.mSmartConfigSocket.receive(pack);
                        byte[] datasx = new byte[pack.getLength()];
                        System.arraycopy(buffer, 0, datasx, 0, datasx.length);
                        if (datasx.length > 12) {
                            boolean ignore = true;

                            for(int i = 0; i < datasx.length; ++i) {
                                ignore = datasx[i] == 5;
                                if (!ignore) {
                                    break;
                                }
                            }

                            if (!ignore) {
                                StringBuffer sb = new StringBuffer();

                                for(int ix = 0; ix < datasx.length; ++ix) {
                                    sb.append((char)datasx[ix]);
                                }

                                String result = sb.toString().trim();
                                if (result.startsWith("smart_config")) {
                                    HFLog.d(AbstractSmartLinker.this, "Received: " + result);
                                    result = result.replace("smart_config", "").trim();
                                    if (result.length() != 0 && !AbstractSmartLinker.this.mDeviceMacs.contains(result)) {
                                        AbstractSmartLinker.this.mDeviceMacs.add(result);
                                        SmartLinkedModule module = new SmartLinkedModule();
                                        module.setId(result);
                                        module.setMac(result);
                                        module.setIp(pack.getAddress().getHostAddress());
                                        AbstractSmartLinker.this.mHander.sendMessage(AbstractSmartLinker.this.mHander.obtainMessage(1, module));
                                        if (findDeivceTime == 9223372036854775807L) {
                                            findDeivceTime = System.currentTimeMillis();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException var14) {
                        HFLog.v(AbstractSmartLinker.this, "smartLinkSocket.receive(pack) timeout");
                    }
                }

                AbstractSmartLinker.this.mIsSmartLinking = false;
            }
        };
    }

    protected abstract Runnable[] setupSendAction(String var1, byte[] var2, String... var3) throws Exception;

    public void setOnSmartLinkListener(OnSmartLinkListener listener) {
        this.mOnSmartLinkListener = listener;
    }

    public void start(Context context, final String password, final byte[] dataAppend, final String... ssid) throws Exception {
        if (this.mIsSmartLinking) {
            HFLog.w(this, "SmartLink is already linking, do not start it again!");
        } else {
            HFLog.d(this, "Smart Link started!");
            this.mIsSmartLinking = true;
            this.initSmartConfigSocket();
            this.mContext = context;
            (new Thread(new Runnable() {
                public void run() {
                    Runnable[] senderActions = null;

                    try {
                        senderActions = AbstractSmartLinker.this.setupSendAction(password, dataAppend, ssid);
                    } catch (Exception var12) {
                        var12.printStackTrace();
                    }

                    int length = 1;
                    if (senderActions != null) {
                        length += senderActions.length;
                    }

                    CountDownLatch latch = new CountDownLatch(length);
                    ExecutorService threadPool = Executors.newFixedThreadPool(length);
                    Runnable receiverRunnable = AbstractSmartLinker.this.createReceiverRunnable(latch);
                    AbstractSmartLinker.this.mIsTimeout = false;
                    if (senderActions != null) {
                        Runnable[] senderRunnables = AbstractSmartLinker.this.createSenderRunnables(latch, senderActions);
                        if (senderRunnables != null) {
                            Runnable[] var7 = senderRunnables;
                            int var8 = senderRunnables.length;

                            for(int var9 = 0; var9 < var8; ++var9) {
                                Runnable runnable = var7[var9];
                                threadPool.execute(runnable);
                            }
                        }
                    }

                    threadPool.execute(receiverRunnable);

                    try {
                        AbstractSmartLinker.this.mIsTimeout = !latch.await((long)AbstractSmartLinker.this.mTimeoutPeriod, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException var11) {
                        var11.printStackTrace();
                    }

                    AbstractSmartLinker.this.mIsSmartLinking = false;
                    threadPool.shutdownNow();
                    AbstractSmartLinker.this.closeDestroySmartConfigSocket();
                    AbstractSmartLinker.this.mHander.sendEmptyMessage(2);
                    HFLog.d(AbstractSmartLinker.this, "Smart Link finished!");
                }
            })).start();
        }
    }

    public void stop() {
        this.mIsSmartLinking = false;
        this.closeDestroySmartConfigSocket();
    }

    public boolean isSmartLinking() {
        return this.mIsSmartLinking;
    }

    public void setTimeoutPeriod(int timeoutPeriod) {
        if (timeoutPeriod > 0) {
            this.mTimeoutPeriod = timeoutPeriod;
        }

    }

    public void setWaitMoreDevicePeriod(int period) {
        this.mWaitMoreDevicePeriod = period;
    }
}
