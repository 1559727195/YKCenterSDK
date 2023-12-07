//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

public class MyCrashHandler implements UncaughtExceptionHandler {
    private static MyCrashHandler INSTANCE;
    private Context context;
    String TAG = "MyCrashHandler";
    private UncaughtExceptionHandler defalutHandler;

    private MyCrashHandler() {
    }

    public static synchronized MyCrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MyCrashHandler();
        }

        return INSTANCE;
    }

    public void init(Context context) {
        this.context = context;
        this.defalutHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread arg0, Throwable arg1) {
        SDKLog.e("Catch a crash..............");
        String errorInfo = this.getErrorInfo(arg1);
        SDKLog.e("捕获的错误信息" + errorInfo + "   获取手机的硬件信息" + this.getMobileInfo() + "  获取手机的版本信息 " + this.getVersionInfo());
        SDKLog.c("捕获的错误信息" + errorInfo + "   获取手机的硬件信息" + this.getMobileInfo() + "  获取手机的版本信息 " + this.getVersionInfo());
        String threadName = arg0.getName();
        SDKLog.e(threadName);
        boolean res = this.handleException(arg1);
        if (!res && this.defalutHandler != null) {
            this.defalutHandler.uncaughtException(arg0, arg1);
        } else {
            try {
                Thread.sleep(3000L);
            } catch (Exception var7) {
            }

            Process.killProcess(Process.myPid());
            System.exit(0);
        }

    }

    private String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        String error = writer.toString();
        return error;
    }

    private String getMobileInfo() {
        StringBuffer sb = new StringBuffer();

        try {
            Field[] fields = Build.class.getDeclaredFields();
            Field[] var3 = fields;
            int var4 = fields.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Field field = var3[var5];
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get((Object)null).toString();
                sb.append(name + "=" + value);
                sb.append("\n");
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return sb.toString();
    }

    private String getVersionInfo() {
        try {
            PackageManager pm = this.context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(this.context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception var3) {
            var3.printStackTrace();
            return "unknow version";
        }
    }

    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        } else {
            (new Thread() {
                public void run() {
                    Looper.prepare();
                    ex.printStackTrace();
                    String err = "[" + ex.getMessage() + "]";
                    Looper.loop();
                }
            }).start();
            SDKLog.c(ex.getMessage());
            SDKLog.b("crash", "", "" + Utils.changeString("app_name") + ": " + Utils.changeString(Utils.getApplicationName(this.context)) + ", " + Utils.changeString("app_version") + ": " + Utils.changeString(Utils.getVersion(this.context)) + ", " + Utils.changeString("trace_function") + ": " + Utils.changeString(ex.getMessage()));
            return true;
        }
    }
}
