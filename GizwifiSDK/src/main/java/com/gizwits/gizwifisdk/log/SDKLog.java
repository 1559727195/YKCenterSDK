package com.gizwits.gizwifisdk.log;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.enumration.GizLogPrintLevel;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class SDKLog {

   private static SDKLog mInstance = new SDKLog();
   public static boolean isSDCard = false;
   public static String fileCachePath = "";
   private static String domain = null;
   private static final String apiKey = "openapi";
   private static final String portKey = "port";
   private static final String sslPortKey = "sslPort";
   private static final String apiPRC = "api.gizwits.com";
   private static final String apiEastUS = "usapi.gizwits.com";
   private static final String apiEurope = "euapi.gizwits.com";
   private static final int port = 80;
   private static final int sslPort = 443;


   public static void setEncryptLog(boolean encrypt) {
      cLogSetEncrypt(encrypt);
   }

   public static boolean getEncryptLog() {
      return cLogGetEncrypt();
   }

   public static boolean getLogIsInit() {
      return cLogGetLogIsInit();
   }

   private static native void cLogSetEncrypt(boolean var0);

   private static native boolean cLogGetEncrypt();

   private static native boolean cLogGetLogIsInit();

   private static native void cLogInit(String var0, int var1, int var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, int var10);

   private static native int cLogProvision(String var0);

   private static native void cUserFeedback(String var0, String var1, boolean var2);

   private static native void cSetUploadLogSwitch(boolean var0, boolean var1);

   private static native void cLogAPI(String var0);

   private static native void cLogBiz(String var0);

   private static native void cLogError(String var0);

   private static native void cLogDebug(String var0);

   private static native String cGetTimeStr();

   private static String getSysLogHead(String level) {
      StackTraceElement[] stacks = (new Throwable()).getStackTrace();
      if(null != stacks && stacks.length >= 3) {
         String content = "[SYS][" + level + "][" + cGetTimeStr() + "][" + stacks[2].getFileName() + ":" + stacks[2].getLineNumber() + " " + stacks[2].getMethodName() + "]";
         return content;
      } else {
         return null;
      }
   }

   private static String getSysLogHeadListener(String level) {
      StackTraceElement[] stacks = (new Throwable()).getStackTrace();
      if(null != stacks && stacks.length >= 3) {
         String content = "[SYS][" + level + "][" + cGetTimeStr() + "][" + stacks[3].getFileName() + ":" + stacks[3].getLineNumber() + " " + stacks[3].getMethodName() + "]";
         return content;
      } else {
         return null;
      }
   }

   public static String getFileCachePath(Context context) {
      if(null == context) {
         return fileCachePath;
      } else {
         if(Environment.getExternalStorageState().equals("mounted")) {
            fileCachePath = context.getExternalFilesDir(null).getAbsolutePath() + "/GizWifiSDK/" + context.getPackageName();
            boolean canAccessSDCard = true;
            File file = new File(fileCachePath);
            if(file.exists()) {
               if(!file.canRead() || !file.canWrite()) {
                  canAccessSDCard = false;
                  e("The directory " + fileCachePath + " can\'t readable or writeable, pleasure make sure the application has the WRITE_EXTERNAL_STORAGE permission!");
               }
            } else if(!file.mkdirs()) {
               canAccessSDCard = false;
               e("Create directory " + fileCachePath + " failed, pleasure make sure the application has the WRITE_EXTERNAL_STORAGE permission!");
            }

            if(!canAccessSDCard) {
               if(context.getExternalFilesDir((String)null) == null) {
                  fileCachePath = context.getFilesDir().getAbsolutePath();
               } else {
                  fileCachePath = context.getExternalFilesDir((String)null).getAbsolutePath();
               }
            }

            isSDCard = canAccessSDCard;
         } else {
            if(context.getExternalFilesDir((String)null) == null) {
               fileCachePath = context.getFilesDir().getAbsolutePath();
            } else {
               fileCachePath = context.getExternalFilesDir((String)null).getAbsolutePath();
            }

            isSDCard = false;
         }

         return fileCachePath;
      }
   }

   private static ConcurrentHashMap parseServiceInfo(String domain, String domainKey, String port1Key, String port2Key) {
      ConcurrentHashMap domainInfo = new ConcurrentHashMap();
      if(!TextUtils.isEmpty(domain)) {
         if(domain.contains(":")) {
            domainInfo.put(domainKey, domain.split(":")[0]);
            String port = domain.split(":")[1];
            if(!TextUtils.isEmpty(port)) {
               if(port.contains("&")) {
                  String port1 = port.split("&")[0];
                  if(port1 != null && TextUtils.isDigitsOnly(port1)) {
                     domainInfo.put(port1Key, Integer.valueOf(Integer.parseInt(port1)));
                  }

                  String port2 = port.split("&")[1];
                  if(port2 != null && TextUtils.isDigitsOnly(port2)) {
                     domainInfo.put(port2Key, Integer.valueOf(Integer.parseInt(port2)));
                  }
               } else if(TextUtils.isDigitsOnly(port)) {
                  domainInfo.put(port1Key, Integer.valueOf(Integer.parseInt(port)));
               }
            }
         } else {
            domainInfo.put(domainKey, domain);
         }
      }

      Object info = domainInfo.get(domainKey);
      if(info == null || TextUtils.isEmpty((String)info)) {
         domainInfo.put(port1Key, "api.gizwits.com");
      }

      info = domainInfo.get(port1Key);
      if(info == null || ((Integer)info).intValue() == 0) {
         domainInfo.put(port1Key, Integer.valueOf(80));
      }

      info = domainInfo.get(port2Key);
      if(info == null || ((Integer)info).intValue() == 0) {
         domainInfo.put(port2Key, Integer.valueOf(443));
      }

      return domainInfo;
   }

   public static void init(Context context, ConcurrentHashMap domainInfo, String appID, String verSDK, GizLogPrintLevel printLevel) {
      if(null == context) {
         e("Invalid Parameter, context is null");
      } else if(null == appID) {
         e("Invalid Parameter, appID is null");
      } else if(null == verSDK) {
         e("Invalid Parameter, verSDK is null");
      } else if(null == printLevel) {
         e("Invalid Parameter, printLevel is null");
      } else {
         boolean openSslPort = false;
         boolean openPort = false;
         String logDirStr = getFileCachePath(context);
         String apiDomain = domainInfo.containsKey("openapi")?(String)domainInfo.get("openapi"):"";
         ConcurrentHashMap apiInfo = parseServiceInfo(apiDomain, "openapi", "port", "sslPort");
         if(null != apiInfo) {
            domain = (String)apiInfo.get("openapi");
         }

         if(domain == null || !domain.equals("api.gizwits.com") && !domain.equals("usapi.gizwits.com") && !domain.equals("euapi.gizwits.com")) {
            domain = "api.gizwits.com";
         }

         int openPort1 = Integer.parseInt(String.valueOf(apiInfo.get("port")));
         int openSslPort1 = Integer.parseInt(String.valueOf(apiInfo.get("sslPort")));
         cLogInit(domain, openSslPort1, openPort1, appID, Secure.getString(context.getContentResolver(), "android_id"), Build.MODEL, "Android", VERSION.RELEASE, verSDK, logDirStr, printLevel.ordinal());
      }
   }

   public static int provision(String uid, String token) {
      return domain == null?0:cLogProvision(token);
   }

   public static void setUploadLogSwitch(boolean syslog, boolean bizlog) {
      cSetUploadLogSwitch(syslog, bizlog);
   }

   public static void userFeedback(String contactInfo, String feedbackInfo, boolean sendLog) {
      String contactInfo_jni = contactInfo == null?"":contactInfo;
      String feedbackInfo_jni = feedbackInfo == null?"":feedbackInfo;
      cUserFeedback(contactInfo_jni, feedbackInfo_jni, sendLog);
   }

   public static void a(String message) {
      if(null != message) {
         message = getSysLogHead("DEBUG") + "[" + message + "]";
         cLogAPI(message);
      }
   }

   public static void b(String bussiCode, String result, String message) {
      if(null != bussiCode && null != result && null != message) {
         message = "[BIZ][" + cGetTimeStr() + "][" + bussiCode + "][" + result + "][{" + message + "}]";
         cLogBiz(message);
      }
   }

   public static void c(String message) {
      if(null != message) {
         message = getSysLogHead("CRASH") + "[" + message + "]";
         cLogError(message);
      }
   }

   public static void d(String message) {
      if(null != message) {
         message = getSysLogHead("DEBUG") + "[" + message + "]";
         cLogDebug(message);
      }
   }

   public static void e(String message) {
      if(null != message) {
         message = getSysLogHead("ERROR") + "[" + message + "]";
         cLogError(message);
      }
   }

   static {
      System.loadLibrary("SDKLog");
   }
}
