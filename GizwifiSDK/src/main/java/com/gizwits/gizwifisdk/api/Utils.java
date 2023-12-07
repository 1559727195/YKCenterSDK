//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.enumration.GizScheduleWeekday;
import com.gizwits.gizwifisdk.enumration.GizSchedulerTaskType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.enumration.GizWifiGAgentType;
import com.gizwits.gizwifisdk.log.SDKLog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
   private static final String TAG = "GizWifiSDKClient-Utils";
   private static Integer sn = null;

   public Utils() {
   }

   protected static String getMethodName() {
      String clazz = Thread.currentThread().getStackTrace()[1].getClassName();
      String method = Thread.currentThread().getStackTrace()[1].getMethodName();
      System.out.println("class name: " + clazz + " Method Name " + method);
      return method;
   }

   protected static List<String> needApkPermissions(Context context) {
      List<String> needPermissions = new ArrayList();
      ArrayList permissions = new ArrayList();

      try {
         PackageManager pm = context.getPackageManager();
         PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
         String pkgName = pi.packageName;
         PackageInfo pkgInfos = pm.getPackageInfo(pkgName, 4096);
         if (pkgInfos == null) {
            permissions.add("PackageInfo is NULL");
            return permissions;
         }

         String[] sharedPkgList = pkgInfos.requestedPermissions;
         if (sharedPkgList != null) {
            for(int i = 0; i < sharedPkgList.length; ++i) {
               String permName = sharedPkgList[i];
               permissions.add(permName);
            }
         }

         if (!permissions.contains("android.permission.INTERNET")) {
            needPermissions.add("android.permission.INTERNET");
         }

         if (!permissions.contains("android.permission.WRITE_EXTERNAL_STORAGE")) {
            needPermissions.add("android.permission.WRITE_EXTERNAL_STORAGE");
         }

         if (!permissions.contains("android.permission.ACCESS_WIFI_STATE")) {
            needPermissions.add("android.permission.ACCESS_WIFI_STATE");
         }

         if (!permissions.contains("android.permission.ACCESS_NETWORK_STATE")) {
            needPermissions.add("android.permission.ACCESS_NETWORK_STATE");
         }

         if (!permissions.contains("android.permission.CHANGE_WIFI_STATE")) {
            needPermissions.add("android.permission.CHANGE_WIFI_STATE");
         }
      } catch (NameNotFoundException var10) {
         SDKLog.e("Retrieve package name exception " + var10);
      }

      return needPermissions;
   }

   protected static boolean isApkBackground(Context context) {
      boolean isBackground = false;

      try {
         PackageManager pm = context.getPackageManager();
         PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
         String pkgName = pi.packageName;
         ActivityManager activityManager = (ActivityManager)context.getSystemService("activity");
         List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
         if (appProcesses != null) {
            Iterator var7 = appProcesses.iterator();

            while(var7.hasNext()) {
               RunningAppProcessInfo appProcess = (RunningAppProcessInfo)var7.next();
               if (appProcess.processName.equals(pkgName)) {
                  if (appProcess.importance == 400) {
                     isBackground = true;
                  } else {
                     isBackground = false;
                  }
               }
            }
         }
      } catch (NameNotFoundException var9) {
         SDKLog.e("Retrieve package name exception " + var9);
      }

      SDKLog.d("is   background  :" + isBackground);
      return isBackground;
   }

   protected static boolean isWifiDisabled(Context context) {
      boolean isDisabled = false;
      ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService("connectivity");
      if (connectivity != null) {
         NetworkInfo[] infos = connectivity.getAllNetworkInfo();
         if (infos != null) {
            NetworkInfo[] var4 = infos;
            int var5 = infos.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               NetworkInfo ni = var4[var6];
               if (ni.getTypeName().equals("WIFI") && ni.isConnected()) {
                  isDisabled = true;
               }
            }
         }
      }

      return isDisabled;
   }

   protected static boolean isNetFree(Context context) {
      boolean isFree = false;
      ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService("connectivity");
      if (connectivity != null) {
         NetworkInfo info = connectivity.getActiveNetworkInfo();
         if (info != null && info.getState() == State.CONNECTED && info.getType() == 1) {
            WifiManager wifiManager = (WifiManager)context.getSystemService("wifi");
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String wifiSSID = wifiInfo.getSSID();
            if (!TextUtils.isEmpty(wifiSSID) && !wifiSSID.equals("CMCC") && !wifiSSID.equals("ChinaNet") && !wifiSSID.equals("ChinaUnicom")) {
               isFree = true;
            }
         }
      }

      return isFree;
   }

   protected static boolean isInternetReachable(Context context) {
      boolean reachabled = false;

      try {
         String ip = "www.baidu.com";
         Process p = Runtime.getRuntime().exec("ping -c 3 -w 10 " + ip);
         int status = p.waitFor();
         if (status == 0) {
            reachabled = true;
         }
      } catch (Exception var5) {
         SDKLog.e("ping www.baidu.com exception: " + var5);
      }

      return reachabled;
   }

   public String getWifiConnectedBssid(Context mContext) {
      WifiInfo mWifiInfo = this.getConnectionInfo(mContext);
      String bssid = null;
      if (mWifiInfo != null && this.isWifiConnected(mContext)) {
         bssid = mWifiInfo.getBSSID();
      }

      return bssid;
   }

   private WifiInfo getConnectionInfo(Context mContext) {
      WifiManager mWifiManager = (WifiManager)mContext.getSystemService("wifi");
      WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
      return wifiInfo;
   }

   private boolean isWifiConnected(Context mContext) {
      NetworkInfo mWiFiNetworkInfo = this.getWifiNetworkInfo(mContext);
      boolean isWifiConnected = false;
      if (mWiFiNetworkInfo != null) {
         isWifiConnected = mWiFiNetworkInfo.isConnected();
      }

      return isWifiConnected;
   }

   private NetworkInfo getWifiNetworkInfo(Context mContext) {
      ConnectivityManager mConnectivityManager = (ConnectivityManager)mContext.getSystemService("connectivity");
      NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(1);
      return mWiFiNetworkInfo;
   }

   protected static String getWifiSSID(Context mContext) {
      WifiManager wifiManager = (WifiManager)mContext.getSystemService("wifi");
      WifiInfo wifiInfo = wifiManager.getConnectionInfo();
      String wifissid = "";
      if (wifiInfo != null) {
         wifissid = wifiInfo.getSSID();
         if (wifissid != null && wifissid.length() >= 2) {
            StringBuffer buffer = new StringBuffer(wifissid);
            wifissid = buffer.substring(1, wifissid.length() - 1);
         }
      }

      return wifissid;
   }

   public static boolean isWifiConnect(Context context) {
      ConnectivityManager connManager = (ConnectivityManager)context.getSystemService("connectivity");
      NetworkInfo mWifiInfo = connManager.getNetworkInfo(1);
      return mWifiInfo.isConnected();
   }

   public static void copyBigDataToSD(Context context, String srcFile, String strOutFileName) {
      if (!(new File(strOutFileName)).exists()) {
         try {
            OutputStream myOutput = new FileOutputStream(strOutFileName);
            InputStream myInput = context.getAssets().open(srcFile);
            byte[] buffer = new byte[1024];

            for(int length = myInput.read(buffer); length > 0; length = myInput.read(buffer)) {
               myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
         } catch (Exception var7) {
            var7.printStackTrace();
         }

      }
   }

   protected static String getWifiIP(Context mContext) {
      WifiManager wifiManager = (WifiManager)mContext.getSystemService("wifi");
      WifiInfo wifiInfo = wifiManager.getConnectionInfo();
      String wifissid = "";
      if (wifiInfo != null) {
         int ipAddress = wifiInfo.getIpAddress();
         if (ipAddress != 0) {
            return (ipAddress & 255) + "." + (ipAddress >> 8 & 255) + "." + (ipAddress >> 16 & 255) + "." + (ipAddress >> 24 & 255);
         }
      }

      return null;
   }

   protected static boolean isNetAvailable(Context context) {
      ConnectivityManager manager = (ConnectivityManager)context.getSystemService("connectivity");
      NetworkInfo info = manager.getActiveNetworkInfo();
      return info != null && info.isAvailable();
   }

   protected static boolean is5GWifi(Context mContext, String ssid) {
      WifiManager mWifiManager = (WifiManager)mContext.getSystemService("wifi");
      if (ssid != null && ssid.length() > 2) {
         List<ScanResult> scanResults = mWifiManager.getScanResults();
         Iterator var4 = scanResults.iterator();

         while(var4.hasNext()) {
            ScanResult scanResult = (ScanResult)var4.next();
            if (scanResult.SSID.equals(ssid)) {
               if (scanResult.frequency > 4900 && scanResult.frequency < 5900) {
                  SDKLog.d("ssid:" + ssid + "is 5G wifi");
                  return true;
               }

               SDKLog.d("ssid:" + ssid + "is not 5G wifi");
               break;
            }

            SDKLog.d("ssid:" + ssid + "is not in scanResult list");
         }
      } else {
         SDKLog.d("ssid:" + ssid + "is not a valid wifi");
      }

      return false;
   }

   protected static String findDaemonInApk(Context context, String filename) {
      String daemonFileName = null;
      File filesDir = context.getFilesDir();
      File[] daemonFiles = filesDir.listFiles();
      String[] list = filesDir.list();
      if (daemonFiles != null && daemonFiles.length > 0) {
         SDKLog.d("apk has daemon file");

         try {
            boolean hasValidDaemonfile = false;
            File[] var7 = daemonFiles;
            int var8 = daemonFiles.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               File daemonFile = var7[var9];
               String daemonName = daemonFile.getName();
               if (!TextUtils.isEmpty(daemonName) && !"GizWifiSDKDaemon".equals(daemonName) && daemonName.length() > "GizWifiSDKDaemon".length()) {
                  hasValidDaemonfile = true;
                  daemonFileName = daemonName;
                  String daemonVersion = daemonName.substring("GizWifiSDKDaemon".length());
                  SDKLog.d("daemoon version is " + daemonVersion);
                  break;
               }

               SDKLog.d("daemon file is " + daemonName + ", but no version");
            }
         } catch (Exception var13) {
            var13.printStackTrace();
         }
      }

      return daemonFileName;
   }

   protected static String findDaemonInJar(Context context) {
      String daemonFileName = null;

      try {
         URL url = GizWifiSDK.class.getResource("files/default");
         JarURLConnection jarCon = (JarURLConnection)url.openConnection();
         jarCon.setUseCaches(false);
         jarCon.connect();
         JarFile jarFile = jarCon.getJarFile();
         Enumeration entrys = jarFile.entries();

         while(entrys.hasMoreElements()) {
            JarEntry fileEntry = (JarEntry)entrys.nextElement();
            if (fileEntry.getName().contains("com/gizwits/gizwifisdk/api/files/")) {
               String curFileName = fileEntry.getName().substring(fileEntry.getName().lastIndexOf("/"));
               if (curFileName.contains("GizWifiSDKDaemon")) {
                  Constant.DaemonVersion = curFileName.split("-")[1];
                  daemonFileName = curFileName;
                  break;
               }
            }
         }

         jarFile.close();
      } catch (IOException var8) {
         SDKLog.e("loadFiles catch Exception: " + var8.toString());
      }

      return daemonFileName;
   }

   protected static JSONObject readJsonFileInJar(Context context, String fileName) {
      JSONObject jsonObject = null;

      try {
         URL url = GizWifiSDK.class.getResource("files/default");
         JarURLConnection jarCon = (JarURLConnection)url.openConnection();
         jarCon.setUseCaches(false);
         jarCon.connect();
         JarFile jarFile = jarCon.getJarFile();
         Enumeration entrys = jarFile.entries();

         while(true) {
            String curFileName;
            String curUrlStr;
            do {
               JarEntry fileEntry;
               do {
                  if (!entrys.hasMoreElements()) {
                     jarFile.close();
                     return jsonObject;
                  }

                  fileEntry = (JarEntry)entrys.nextElement();
               } while(!fileEntry.getName().contains("com/gizwits/gizwifisdk/api/files/"));

               curFileName = fileEntry.getName().substring(fileEntry.getName().lastIndexOf("/"));
               curUrlStr = url.toString().substring(0, url.toString().lastIndexOf("/")) + curFileName;
            } while(!curFileName.contains(fileName));

            URL curUrl = new URL(curUrlStr);
            InputStream inStream = curUrl.openConnection().getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            StringBuffer sb = new StringBuffer();
            String str = "";

            while((str = reader.readLine()) != null) {
               sb.append(str).append("\n");
            }

            try {
               jsonObject = new JSONObject(sb.toString());
            } catch (JSONException var16) {
               var16.printStackTrace();
            }
         }
      } catch (IOException var17) {
         SDKLog.e("loadFiles catch Exception: " + var17.toString());
         return jsonObject;
      }
   }

   protected static String getLanguage(Context mContext) {
      Locale locale = mContext.getResources().getConfiguration().locale;
      String language = locale.getLanguage();
      return language.endsWith("zh") ? "zh-cn" : "en";
   }

   protected static String getApplicationName(Context context) {
      PackageManager packageManager = null;
      ApplicationInfo applicationInfo = null;

      try {
         packageManager = context.getPackageManager();
         applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
      } catch (NameNotFoundException var4) {
         applicationInfo = null;
      }

      String applicationName = (String)packageManager.getApplicationLabel(applicationInfo);
      return applicationName;
   }

   protected static String getVersion(Context context) {
      try {
         PackageManager manager = context.getPackageManager();
         PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
         String version = info.versionName;
         return version;
      } catch (Exception var4) {
         var4.printStackTrace();
         return "";
      }
   }

   protected static Utils.WifiCipherType getCipherType(Context context, String ssid) {
      WifiManager wifiManager = (WifiManager)context.getSystemService("wifi");
      return Utils.WifiCipherType.WIFICIPHER_INVALID;
   }

   protected static String readFileContentStr(String fullFilename) {
      String readOutStr = null;
      DataInputStream dis = null;

      try {
         dis = new DataInputStream(new FileInputStream(fullFilename));

         try {
            long len = (new File(fullFilename)).length();
            if (len > 2147483647L) {
               throw new IOException("File " + fullFilename + " too large, was " + len + " bytes.");
            }

            byte[] bytes = new byte[(int)len];
            dis.readFully(bytes);
            readOutStr = new String(bytes, "UTF-8");
         } finally {
            if (dis != null) {
               dis.close();
            }

         }

         SDKLog.d("Successfully to read out string from file " + fullFilename);
      } catch (IOException var12) {
         readOutStr = null;
         if (dis != null) {
            try {
               dis.close();
            } catch (IOException var10) {
               var10.printStackTrace();
            }
         }

         SDKLog.d("Fail to read out string from file " + fullFilename + "errormessage : " + var12);
      }

      return readOutStr;
   }

   protected static boolean ping() {
      String result = null;

      try {
         boolean var2;
         try {
            String ip = "114.114.114.114";
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 10 " + ip);
            int status = p.waitFor();
            if (status == 0) {
               result = "successful~";
               boolean var4 = true;
               return var4;
            } else {
               result = "failed~ cannot reach the IP address";
               return false;
            }
         } catch (IOException var9) {
            result = "failed~ IOException";
            var2 = false;
            return var2;
         } catch (InterruptedException var10) {
            result = "failed~ InterruptedException";
            var2 = false;
            return var2;
         }
      } finally {
         ;
      }
   }

   protected static boolean isWifi(Context mContext) {
      ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService("connectivity");
      NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetInfo != null && activeNetInfo.getType() == 1;
   }

   protected static List<String> removeInvalidLength(List<String> list, int length) {
      List<String> savedlist = new ArrayList();
      Iterator var3 = list.iterator();

      while(var3.hasNext()) {
         String item = (String)var3.next();
         if (item.length() == length) {
            savedlist.add(item);
         }
      }

      return savedlist;
   }

   protected static List<String> removeDuplicateString(List<String> originalList) {
      List<String> filteredList = new ArrayList();
      if (originalList == null) {
         return filteredList;
      } else {
         Iterator var2 = originalList.iterator();

         while(true) {
            String item;
            boolean isExist;
            do {
               if (!var2.hasNext()) {
                  return filteredList;
               }

               item = (String)var2.next();
               isExist = false;
            } while(item == null);

            Iterator var5 = filteredList.iterator();

            while(var5.hasNext()) {
               String filteredItem = (String)var5.next();
               if (item.equals(filteredItem)) {
                  isExist = true;
                  break;
               }
            }

            if (!isExist) {
               filteredList.add(item);
            }
         }
      }
   }

   protected static List<ConcurrentHashMap<String, Object>> removeDuplicateMap(List<ConcurrentHashMap<String, Object>> originalMap) {
      List<ConcurrentHashMap<String, Object>> filteredMap = new ArrayList();
      if (originalMap == null) {
         return filteredMap;
      } else {
         Iterator var2 = originalMap.iterator();

         while(true) {
            ConcurrentHashMap item;
            boolean isExist;
            do {
               do {
                  if (!var2.hasNext()) {
                     return filteredMap;
                  }

                  item = (ConcurrentHashMap)var2.next();
                  isExist = false;
               } while(item == null);
            } while(!item.containsKey("productKey"));

            Iterator var5 = filteredMap.iterator();

            while(var5.hasNext()) {
               ConcurrentHashMap<String, Object> filteredItem = (ConcurrentHashMap)var5.next();
               if (item.get("productKey").equals(filteredItem.get("productKey"))) {
                  isExist = true;
                  break;
               }
            }

            if (!isExist) {
               filteredMap.add(item);
            }
         }
      }
   }

   protected static List<GizWifiGAgentType> removeDuplicateGAgentType(List<GizWifiGAgentType> originalList) {
      List<GizWifiGAgentType> filteredList = new ArrayList();
      if (originalList == null) {
         return filteredList;
      } else {
         Iterator var2 = originalList.iterator();

         while(var2.hasNext()) {
            GizWifiGAgentType item = (GizWifiGAgentType)var2.next();
            boolean isExist = false;
            Iterator var5 = filteredList.iterator();

            while(var5.hasNext()) {
               GizWifiGAgentType filteredItem = (GizWifiGAgentType)var5.next();
               if (item.equals(filteredItem)) {
                  isExist = true;
                  break;
               }
            }

            if (!isExist) {
               filteredList.add(item);
            }
         }

         return filteredList;
      }
   }

   protected static GizWifiDevice isExistedProductKeyInDeviceList(String productKey, List<GizWifiDevice> devices, String productUI, boolean isToSet) {
      String product_key = "";
      if (!"".equals(productKey)) {
         try {
            product_key = (new JSONObject(productKey)).getString("product_key");
         } catch (JSONException var8) {
            var8.printStackTrace();
         }
      }

      GizWifiDevice isExisted = null;
      if (devices != null) {
         Iterator var6 = devices.iterator();

         while(var6.hasNext()) {
            GizWifiDevice device = (GizWifiDevice)var6.next();
            if (device.getProductKey().equals(product_key)) {
               isExisted = device;
               if (isToSet) {
                  device.setProductUI(productUI);
               }
            }
         }
      }

      return isExisted;
   }

   protected static String changeString(String ss) {
      return "\"" + ss + "\"";
   }

   protected static synchronized int getSn() {
      if (sn == null) {
         sn = 0;
      } else {
         Integer var0 = sn;
         sn = sn + 1;
      }

      return sn;
   }

   protected static String isEmpty(String ss) {
      return TextUtils.isEmpty(ss) ? "null" : "******";
   }

   protected static String listToString(List<GizScheduleWeekday> list) {
      String repeat = "";
      if (list != null) {
         for(int i = 0; i < list.size(); ++i) {
            GizScheduleWeekday obj = (GizScheduleWeekday)list.get(i);
            if (obj == GizScheduleWeekday.GizScheduleFriday && !repeat.contains("fri")) {
               if (i == 0) {
                  repeat = repeat + "fri";
               } else {
                  repeat = repeat + ",fri";
               }
            } else if (obj == GizScheduleWeekday.GizScheduleMonday && !repeat.contains("mon")) {
               if (i == 0) {
                  repeat = repeat + "mon";
               } else {
                  repeat = repeat + ",mon";
               }
            } else if (obj == GizScheduleWeekday.GizScheduleSaturday && !repeat.contains("sat")) {
               if (i == 0) {
                  repeat = repeat + "sat";
               } else {
                  repeat = repeat + ",sat";
               }
            } else if (obj == GizScheduleWeekday.GizScheduleSunday && !repeat.contains("sun")) {
               if (i == 0) {
                  repeat = repeat + "sun";
               } else {
                  repeat = repeat + ",sun";
               }
            } else if (obj == GizScheduleWeekday.GizScheduleThursday && !repeat.contains("thu")) {
               if (i == 0) {
                  repeat = repeat + "thu";
               } else {
                  repeat = repeat + ",thu";
               }
            } else if (obj == GizScheduleWeekday.GizScheduleTuesday && !repeat.contains("tue")) {
               if (i == 0) {
                  repeat = repeat + "tue";
               } else {
                  repeat = repeat + ",tue";
               }
            } else if (obj == GizScheduleWeekday.GizScheduleWednesday && !repeat.contains("wed")) {
               if (i == 0) {
                  repeat = repeat + "wed";
               } else {
                  repeat = repeat + ",wed";
               }
            }
         }
      }

      return repeat;
   }

   protected static JSONArray getTaskJsonArray(List<ConcurrentHashMap<String, Object>> taskList) {
      try {
         JSONArray array = new JSONArray();

         JSONObject obj;
         label54:
         for(Iterator var2 = taskList.iterator(); var2.hasNext(); array.put(obj)) {
            ConcurrentHashMap<String, Object> concurrentHashMap = (ConcurrentHashMap)var2.next();
            obj = new JSONObject();
            Iterator var5 = concurrentHashMap.keySet().iterator();

            while(true) {
               while(true) {
                  if (!var5.hasNext()) {
                     continue label54;
                  }

                  String key = (String)var5.next();
                  if ("did".equals(key)) {
                     obj.put("did", concurrentHashMap.get(key));
                  } else if ("product_key".equals(key)) {
                     obj.put("product_key", concurrentHashMap.get(key));
                  } else if ("attrs".equals(key)) {
                     Object object = concurrentHashMap.get(key);
                     if (object instanceof ConcurrentHashMap) {
                        ConcurrentHashMap<String, Object> attr = (ConcurrentHashMap)object;
                        JSONObject json = new JSONObject();
                        Iterator var10 = attr.keySet().iterator();

                        while(var10.hasNext()) {
                           String key1 = (String)var10.next();
                           Object objkey1 = attr.get(key1);
                           if (objkey1 instanceof byte[]) {
                              byte[] byts = (byte[])((byte[])objkey1);
                              json.put(key1, GizWifiBinary.encode(byts));
                           } else {
                              json.put(key1, attr.get(key1));
                           }
                        }

                        obj.put("attrs", json);
                     }
                  }
               }
            }
         }

         return array;
      } catch (JSONException var14) {
         var14.printStackTrace();
         return null;
      }
   }

   protected static List<GizScheduleWeekday> getRepeatType(String s) {
      String[] typeString = s.split(",");
      List<GizScheduleWeekday> list = new ArrayList();
      String[] var3 = typeString;
      int var4 = typeString.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String string = var3[var5];
         if ("sun".equals(string)) {
            list.add(GizScheduleWeekday.GizScheduleSunday);
         } else if ("mon".equals(string)) {
            list.add(GizScheduleWeekday.GizScheduleMonday);
         } else if ("tue".equals(string)) {
            list.add(GizScheduleWeekday.GizScheduleTuesday);
         } else if ("wed".equals(string)) {
            list.add(GizScheduleWeekday.GizScheduleWednesday);
         } else if ("thu".equals(string)) {
            list.add(GizScheduleWeekday.GizScheduleThursday);
         } else if ("fri".equals(string)) {
            list.add(GizScheduleWeekday.GizScheduleFriday);
         } else if ("sat".equals(string)) {
            list.add(GizScheduleWeekday.GizScheduleSaturday);
         }
      }

      return list;
   }

   protected static List<ConcurrentHashMap<String, Object>> getList(JSONArray array) {
      try {
         List<ConcurrentHashMap<String, Object>> list = new ArrayList();

         for(int i = 0; i < array.length(); ++i) {
            JSONObject obj = (JSONObject)array.get(i);
            ConcurrentHashMap<String, Object> map = new ConcurrentHashMap();
            ConcurrentHashMap<String, Object> attrmap = new ConcurrentHashMap();
            map.put("did", obj.getString("did"));
            map.put("product_key", obj.getString("product_key"));
            JSONObject attrsobj = obj.getJSONObject("attrs");
            Iterator it_params = attrsobj.keys();

            while(it_params.hasNext()) {
               String param = it_params.next().toString();
               Object value = attrsobj.get(param);
               attrmap.put(param, value);
            }

            map.put("attrs", attrmap);
            list.add(map);
         }

         return list;
      } catch (JSONException var10) {
         var10.printStackTrace();
         return null;
      }
   }

   protected static String getmils() {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
      String str = formatter.format(new Date());
      return str;
   }

   protected static boolean isX86() {
      try {
         Process process = Runtime.getRuntime().exec("getprop");
         InputStream inputStream = process.getInputStream();
         InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
         BufferedReader reader = new BufferedReader(inputStreamReader);
         StringBuilder sb = new StringBuilder();
         String line = null;

         while((line = reader.readLine()) != null) {
            if (line.contains("ro.product.cpu.abi")) {
               sb.append(line);
            }
         }

         inputStream.close();
         reader.close();
         if (sb.toString().contains("x86")) {
            return true;
         } else {
            return false;
         }
      } catch (IOException var6) {
         var6.printStackTrace();
         SDKLog.e("" + var6);
         return false;
      }
   }

   protected static String GetNetworkType(Context context) {
      String strNetworkType = "";
      ConnectivityManager manager = (ConnectivityManager)context.getSystemService("connectivity");
      NetworkInfo networkInfo = manager.getActiveNetworkInfo();
      if (networkInfo != null && networkInfo.isConnected()) {
         if (networkInfo.getType() == 1) {
            strNetworkType = "WIFI";
         } else if (networkInfo.getType() == 0) {
            String _strSubTypeName = networkInfo.getSubtypeName();
            SDKLog.e("Network getSubtypeName : " + _strSubTypeName);
            int networkType = networkInfo.getSubtype();
            switch(networkType) {
               case 1:
               case 2:
               case 4:
               case 7:
               case 11:
                  strNetworkType = "2G";
                  break;
               case 3:
               case 5:
               case 6:
               case 8:
               case 9:
               case 10:
               case 12:
               case 14:
               case 15:
                  strNetworkType = "3G";
                  break;
               case 13:
                  strNetworkType = "4G";
                  break;
               default:
                  if (!_strSubTypeName.equalsIgnoreCase("TD-SCDMA") && !_strSubTypeName.equalsIgnoreCase("WCDMA") && !_strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                     strNetworkType = _strSubTypeName;
                  } else {
                     strNetworkType = "3G";
                  }
            }
         }
      }

      return strNetworkType;
   }

   protected static JSONObject getApiJson(JSONObject zone_domain) {
      TimeZone tz = TimeZone.getDefault();
      String id = tz.getID();
      if (zone_domain != null && zone_domain.has(id)) {
         try {
            JSONObject pushObj = (JSONObject)zone_domain.get(id);
            SDKLog.d("Auto select domain info :" + pushObj);
            return pushObj;
         } catch (JSONException var4) {
            var4.printStackTrace();
            return null;
         }
      } else {
         return null;
      }
   }

   protected static JSONObject getJsonString() {
      try {
         TimeZone tz = TimeZone.getDefault();
         String id = tz.getID();
         int sn2 = getSn();
         JSONObject obj = new JSONObject();
         obj.put("cmd", 1105);
         obj.put("sn", sn2);
         obj.put("tzCity", id);
         return obj;
      } catch (JSONException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   protected static ConcurrentHashMap<String, String> getDomainInfo(ConcurrentHashMap<String, String> cloudServiceInfo, boolean autoByZone, JSONObject zone_domain) {
      ConcurrentHashMap<String, String> domainInfo = new ConcurrentHashMap();
      if (cloudServiceInfo != null) {
         Object domainAPI = cloudServiceInfo.get("openAPIInfo");
         if (domainAPI == null) {
            domainInfo.put("openapi", "api.gizwits.com");
            domainInfo.put("site", "api.gizwits.com");
         } else if (domainAPI != null && domainAPI instanceof String) {
            domainInfo.put("openapi", (String)domainAPI);
            String domainSite = (String)cloudServiceInfo.get("siteInfo");
            if (domainSite != null && domainSite instanceof String) {
               domainInfo.put("site", domainSite);
            } else {
               domainInfo.put("site", (String)domainAPI);
            }

            String domainPush = (String)cloudServiceInfo.get("pushInfo");
            if (domainPush != null && domainPush instanceof String) {
               domainInfo.put("push", domainPush);
            }
         }
      } else if (autoByZone) {
         JSONObject apiJson = getApiJson(zone_domain);
         if (apiJson != null) {
            try {
               domainInfo.put("openapi", (String)apiJson.get("api"));
               domainInfo.put("site", (String)apiJson.get("api"));
               domainInfo.put("push", (String)apiJson.get("push"));
            } catch (JSONException var7) {
               var7.printStackTrace();
            }
         }
      }

      if (TextUtils.isEmpty((CharSequence)domainInfo.get("openapi"))) {
         domainInfo.put("openapi", "api.gizwits.com");
         domainInfo.put("site", "api.gizwits.com");
      }

      SDKLog.d("get domain: " + (String)SDKEventManager.domainInfo.get("openapi"));
      return domainInfo;
   }

   protected static JSONObject getTaskJsonObject(ConcurrentHashMap<String, Object> attrs) throws JSONException {
      JSONObject obj = null;
      if (attrs != null) {
         obj = new JSONObject();
         Iterator var2 = attrs.keySet().iterator();

         while(var2.hasNext()) {
            String key = (String)var2.next();
            Object val = attrs.get(key);
            if (val instanceof byte[]) {
               byte[] byts = (byte[])((byte[])val);
               obj.put(key, GizWifiBinary.encode(byts));
            } else {
               obj.put(key, val);
            }
         }
      }

      return obj;
   }

   protected static Bitmap createQRImage(String url, int width, int height) {
      try {
         if (url != null && !"".equals(url) && url.length() >= 1) {
            Hashtable<EncodeHintType, String> hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = (new QRCodeWriter()).encode(url, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];

            for(int y = 0; y < height; ++y) {
               for(int x = 0; x < width; ++x) {
                  if (bitMatrix.get(x, y)) {
                     pixels[y * width + x] = -16777216;
                  } else {
                     pixels[y * width + x] = -1;
                  }
               }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
         } else {
            return null;
         }
      } catch (WriterException var8) {
         var8.printStackTrace();
         return null;
      }
   }

   protected static Location getLocation(Context c) {
      try {
         LocationManager locationManager = (LocationManager)c.getSystemService("location");
         Location location = locationManager.getLastKnownLocation("gps");
         return location;
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   protected static JSONObject queryAllTaskList(GizWifiDevice device) {
      try {
         if (device != null) {
            int sn2 = getSn();
            JSONObject obj = new JSONObject();
            obj.put("cmd", 1259);
            obj.put("sn", sn2);
            obj.put("mac", device.getMacAddress());
            obj.put("did", device.getDid());
            obj.put("productKey", device.getProductKey());
            return obj;
         } else {
            return null;
         }
      } catch (JSONException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   protected static String schedulerTasksToString(List<GizDeviceSchedulerTask> schedulerTasks) {
      try {
         if (schedulerTasks == null) {
            return null;
         } else {
            JSONArray myarray = new JSONArray();

            for(int i = 0; i < schedulerTasks.size(); ++i) {
               GizDeviceSchedulerTask task = (GizDeviceSchedulerTask)schedulerTasks.get(i);
               if (task != null) {
                  JSONObject mob = new JSONObject();
                  GizSchedulerTaskType taskType = task.getTaskType();
                  mob.put("schedulerTaskType", taskType.ordinal());
                  ConcurrentHashMap data;
                  JSONObject map;
                  Iterator var9;
                  String key;
                  switch(taskType.ordinal()) {
                     case 0:
                        GizWifiDevice device = task.getDevice();
                        if (device != null) {
                           JSONObject mydevice = new JSONObject();
                           mydevice.put("mac", device.getMacAddress());
                           mydevice.put("did", device.getDid());
                           mydevice.put("productKey", device.getProductKey());
                           mob.put("device", mydevice);
                        }

                        data = task.getData();
                        if (data != null && data.size() > 0) {
                           map = new JSONObject();
                           var9 = data.keySet().iterator();

                           while(var9.hasNext()) {
                              key = (String)var9.next();
                              map.put(key, data.get(key));
                           }

                           mob.put("data", map);
                        }
                        break;
                     case 1:
                        mob.put("groupID", task.getGroup() == null ? null : task.getGroup().getGroupID());
                        data = task.getData();
                        if (data != null && data.size() > 0) {
                           map = new JSONObject();
                           var9 = data.keySet().iterator();

                           while(var9.hasNext()) {
                              key = (String)var9.next();
                              map.put(key, data.get(key));
                           }

                           mob.put("data", map);
                        }
                        break;
                     case 2:
                        mob.put("sceneID", task.getScene() == null ? null : task.getScene().getSceneID());
                        mob.put("sceneStatus", task.getSceneStartup());
                  }

                  myarray.put(mob);
               }
            }

            return myarray.toString();
         }
      } catch (JSONException var11) {
         var11.printStackTrace();
         SDKLog.e(var11.toString());
         return null;
      }
   }

   public static String bytesToHexString(byte[] array, String separator) {
      if (array != null && array.length != 0) {
         StringBuilder sb = new StringBuilder();
         Formatter formatter = new Formatter(sb);
         formatter.format("%02X", array[0]);

         for(int i = 1; i < array.length; ++i) {
            if (separator != null || !separator.trim().isEmpty()) {
               sb.append(separator);
            }

            formatter.format("%02X", array[i]);
         }

         formatter.flush();
         formatter.close();
         return sb.toString();
      } else {
         return "";
      }
   }

   protected static String dataMasking(String data) {
      String dataString = "";
      if (data != null && data.length() > 0) {
         int length = data.length();
         if (length >= 1 && length <= 3) {
            dataString = data.substring(0, 1) + "****";
         } else if (length >= 4 && length <= 8) {
            dataString = data.substring(0, 1) + "****" + data.substring(length - 1);
         } else {
            dataString = data.substring(0, 3) + "****" + data.substring(length - 3);
         }
      }

      return dataString;
   }

   protected static GizWifiErrorCode changeToGizErrorCode(int code) {
      return GizWifiErrorCode.valueOf(code);
   }

   protected static int changeErrorCode(int a) {
      int b;
      switch(a) {
         case 0:
            b = 0;
            break;
         case 8002:
            b = -49;
            break;
         case 8004:
            b = -30;
            break;
         case 8006:
            b = -20;
            break;
         case 8020:
            b = -39;
            break;
         case 8021:
            b = -41;
            break;
         case 8022:
            b = -46;
            break;
         case 8023:
            b = -40;
            break;
         case 8029:
            b = -11;
            break;
         case 8033:
         case 8036:
         case 8037:
         case 8038:
            b = -20;
            break;
         case 8041:
         case 8044:
            b = -27;
            break;
         case 8045:
            b = -45;
            break;
         case 8047:
            b = -7;
            break;
         case 8049:
            b = -9;
            break;
         case 8050:
            b = -7;
            break;
         case 8099:
            b = -25;
            break;
         case 8100:
            b = -1;
            break;
         case 8101:
            b = -23;
            break;
         case 8102:
            b = -15;
            break;
         case 8307:
            b = -47;
            break;
         case 8308:
            b = -40;
            break;
         case 8310:
         case 8311:
            b = -39;
            break;
         case 8312:
            b = -42;
            break;
         case 8315:
            b = -46;
            break;
         default:
            b = a;
      }

      return b;
   }

   public static byte[] stringToBytes(String str, int length) {
      if (length <= 0) {
         return str.getBytes(Charset.defaultCharset());
      } else {
         byte[] result = new byte[length];
         byte[] srcBytes = str.getBytes(Charset.defaultCharset());
         if (srcBytes.length <= length) {
            System.arraycopy(srcBytes, 0, result, 0, srcBytes.length);
         } else {
            System.arraycopy(srcBytes, 0, result, 0, length);
         }

         return result;
      }
   }

   public static byte[] reverse(byte[] arr, int begin, int end) {
      while(begin < end) {
         byte temp = arr[end];
         arr[end] = arr[begin];
         arr[begin] = temp;
         ++begin;
         --end;
      }

      return arr;
   }

   public static byte[] reverse(byte[] a) {
      if (a == null) {
         return null;
      } else {
         int p1 = 0;
         int p2 = a.length;
         byte[] result = new byte[p2];

         while(true) {
            --p2;
            if (p2 < 0) {
               return result;
            }

            result[p2] = a[p1++];
         }
      }
   }

   private static enum WifiCipherType {
      WIFICIPHER_WEP,
      WIFICIPHER_WPA,
      WIFICIPHER_NOPASS,
      WIFICIPHER_INVALID;

      private WifiCipherType() {
      }
   }

   private static class DaemonFilter implements FilenameFilter {
      private DaemonFilter() {
      }

      public boolean accept(File dir, String filename) {
         return this.isDaemon(filename);
      }

      public boolean isDaemon(String filename) {
         return TextUtils.isEmpty(filename) ? false : filename.contains("GizWifiSDKDaemon");
      }
   }
}
