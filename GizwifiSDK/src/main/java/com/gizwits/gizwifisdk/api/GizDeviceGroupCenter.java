//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizDeviceGroupCenterListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceGroupCenter {
   private static ConcurrentHashMap<GizWifiDevice, List<GizDeviceGroup>> groupListMap = new ConcurrentHashMap();
   private static List<Integer> messageQueue = new ArrayList();
   private static final int MSG_RECV = 5;
   private static GizDeviceGroupCenterListener mListener;
   private static GizWifiDevice addGroupRequestOwner = null;
   private static GizWifiDevice removeGroupRequestOwner = null;
   private static GizWifiDevice updateGroupRequestOwner = null;
   protected static Handler timeoutHandler = new Handler(Looper.getMainLooper()) {
      public void handleMessage(Message msg) {
         super.handleMessage(msg);
         GizDeviceGroupCenter.handleTimeoutMessage(msg);
      }
   };
   protected static Handler messageHandler = new Handler(Looper.getMainLooper()) {
      public void handleMessage(Message msg) {
         switch(msg.what) {
            case 5:
               try {
                  String jsonStr = (String)msg.obj;
                  JSONObject obj = new JSONObject(jsonStr);
                  int cmd = obj.has("cmd") ? Integer.parseInt(obj.getString("cmd")) : 0;
                  int sn = obj.has("sn") ? Integer.parseInt(obj.getString("sn")) : 0;
                  String did = obj.has("did") ? obj.getString("did") : "";
                  String mac = obj.has("mac") ? obj.getString("mac") : "";
                  String productKey = obj.has("productKey") ? obj.getString("productKey") : "";
                  GizWifiDevice deviceOwner = SDKEventManager.getInstance().findDeviceInTotalDeviceList(did, mac, productKey);
                  if (deviceOwner == null) {
                     SDKLog.d("owner<mac: " + mac + ", productKey: " + productKey + ", did: " + did + ">is null");
                  }

                  if (cmd > 2000) {
                     sn = 0;
                  }

                  GizDeviceGroupCenter.handleReceiveMessage(cmd, obj, sn, deviceOwner);
               } catch (NumberFormatException var10) {
                  var10.printStackTrace();
               } catch (JSONException var11) {
                  var11.printStackTrace();
               }
            default:
         }
      }
   };

   public GizDeviceGroupCenter() {
   }

   protected static String listMasking(List<GizDeviceGroup> list) {
      String masking = "{size= " + (list == null ? "0" : list.size()) + ", ";
      if (list != null) {
         for(Iterator var2 = list.iterator(); var2.hasNext(); masking = masking + ", ") {
            GizDeviceGroup object = (GizDeviceGroup)var2.next();
            masking = masking + "[" + object.infoMasking() + "]";
         }
      }

      return masking.substring(0, masking.length() - 2) + "}";
   }

   private static String mapMasking(ConcurrentHashMap<GizWifiDevice, List<GizDeviceGroup>> map) {
      String masking = "{size= " + (map == null ? "0" : map.size()) + ", ";
      if (map != null) {
         for(Iterator var2 = map.keySet().iterator(); var2.hasNext(); masking = masking + ", ") {
            GizWifiDevice key = (GizWifiDevice)var2.next();
            List<GizDeviceGroup> list = (List)map.get(key);
            masking = "{" + key.simpleInfoMasking() + ": " + listMasking(list) + "}";
         }
      }

      return masking.substring(0, masking.length() - 2) + "}";
   }

   private static ConcurrentHashMap<GizWifiDevice, List<GizDeviceGroup>> getGroupListMap() {
      return groupListMap;
   }

   protected static List<GizDeviceGroup> getTotalGroupListByOwner(GizWifiDevice owner) {
      List<GizDeviceGroup> list = new ArrayList();
      Iterator var2 = groupListMap.keySet().iterator();

      while(var2.hasNext()) {
         GizWifiDevice key = (GizWifiDevice)var2.next();
         if (owner != null && key.getMacAddress().equals(owner.getMacAddress()) && key.getDid().equals(owner.getDid()) && key.getProductKey().equals(owner.getProductKey())) {
            List<GizDeviceGroup> maplist = (List)groupListMap.get(key);
            if (maplist != null && maplist.size() > 0) {
               list.addAll(maplist);
            }
            break;
         }
      }

      return list;
   }

   private static ConcurrentHashMap<GizWifiDevice, List<GizDeviceGroup>> getValidGroupListMap() {
      ConcurrentHashMap<GizWifiDevice, List<GizDeviceGroup>> mapList = new ConcurrentHashMap();
      Iterator var1 = getGroupListMap().keySet().iterator();

      while(var1.hasNext()) {
         GizWifiDevice key = (GizWifiDevice)var1.next();
         List<GizDeviceGroup> groupList = new ArrayList();
         Iterator var4 = ((List)groupListMap.get(key)).iterator();

         while(var4.hasNext()) {
            GizDeviceGroup group = (GizDeviceGroup)var4.next();
            if (group.getIsValid()) {
               groupList.add(group);
            }
         }

         if (groupList.size() > 0) {
            mapList.put(key, groupList);
         }
      }

      return mapList;
   }

   protected static List<GizDeviceGroup> getValidGroupListByOwner(GizWifiDevice owner) {
      List<GizDeviceGroup> list = new ArrayList();
      ConcurrentHashMap<GizWifiDevice, List<GizDeviceGroup>> maplist = getValidGroupListMap();
      Iterator var3 = maplist.keySet().iterator();

      while(var3.hasNext()) {
         GizWifiDevice key = (GizWifiDevice)var3.next();
         if (owner != null && key.getMacAddress().equals(owner.getMacAddress()) && key.getDid().equals(owner.getDid()) && key.getProductKey().equals(owner.getProductKey())) {
            list = (List)maplist.get(owner);
            break;
         }
      }

      return (List)list;
   }

   protected static GizDeviceGroup getGroupByOwner(GizWifiDevice owner, String groupID) {
      List<GizDeviceGroup> list = getTotalGroupListByOwner(owner);
      GizDeviceGroup group = null;
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         GizDeviceGroup deviceGroup = (GizDeviceGroup)var4.next();
         if (deviceGroup.getGroupID().equals(groupID)) {
            group = deviceGroup;
            break;
         }
      }

      return group;
   }

   public static List<GizDeviceGroup> getGroupListGateway(GizWifiDevice groupOwner) {
      SDKLog.a("Start => groupOwner: " + (groupOwner == null ? "" : groupOwner.simpleInfoMasking()));
      List<GizDeviceGroup> list = getValidGroupListByOwner(groupOwner);
      SDKLog.a("End <= ");
      return list;
   }

   public static void setListener(GizDeviceGroupCenterListener listener) {
      SDKLog.a("Start => listener: " + (listener == null ? "null" : listener));
      mListener = listener;
      SDKLog.a("End <= ");
   }

   public static void addGroup(GizWifiDevice groupOwner, String groupType, String groupName, List<GizWifiDevice> groupDevices) {
      SDKLog.a("Start => , groupOwner: " + (groupOwner == null ? "null" : groupOwner.simpleInfoMasking()) + ", groupType: " + groupType + "groupName: " + groupName);
      ArrayList groupList;
      if (!Constant.ishandshake) {
         groupList = new ArrayList();
         OnDidUpdateGroups(groupOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, groupList);
         SDKLog.a("End <= ");
      } else if (groupOwner == null) {
         groupList = new ArrayList();
         OnDidUpdateGroups(groupOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, groupList);
         SDKLog.a("End <= ");
      } else if (!TextUtils.isEmpty(groupType) && groupType.length() == 32) {
         addGroupRequestOwner = groupOwner;
         JSONObject obj = new JSONObject();
         int sn = Utils.getSn();

         try {
            obj.put("cmd", 1301);
            obj.put("sn", sn);
            obj.put("mac", groupOwner.getMacAddress());
            obj.put("did", groupOwner.getDid());
            obj.put("productKey", groupOwner.getProductKey());
            obj.put("groupType", groupType);
            obj.put("groupName", groupName);
            if (groupDevices != null && groupDevices.size() > 0) {
               JSONArray array = new JSONArray();
               Iterator var7 = groupDevices.iterator();

               while(var7.hasNext()) {
                  GizWifiDevice device = (GizWifiDevice)var7.next();
                  if (device != null) {
                     JSONObject ob = new JSONObject();
                     ob.put("mac", device.getMacAddress());
                     ob.put("did", device.getDid());
                     ob.put("productKey", device.getProductKey());
                     array.put(ob);
                  }
               }

               obj.put("groupDevices", array);
            }
         } catch (JSONException var10) {
            SDKLog.e(var10.toString());
            var10.printStackTrace();
         }

         sendMessageToDaemon(obj);
         if (groupOwner.isLAN) {
            makeTimer(9000, 1302, sn);
         } else {
            makeTimer(20000, 1302, sn);
         }

         SDKLog.a("End <= ");
      } else {
         groupList = new ArrayList();
         OnDidUpdateGroups(groupOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, groupList);
         SDKLog.a("End <= ");
      }
   }

   public static void removeGroup(GizWifiDevice groupOwner, GizDeviceGroup group) {
      SDKLog.a("Start => , groupOwner: " + (groupOwner == null ? "null" : groupOwner.simpleInfoMasking()) + ", group: " + (group == null ? "null" : group.getGroupID()));
      ArrayList groupList;
      if (!Constant.ishandshake) {
         groupList = new ArrayList();
         OnDidUpdateGroups(groupOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, groupList);
         SDKLog.a("End <= ");
      } else if (groupOwner == null) {
         groupList = new ArrayList();
         OnDidUpdateGroups(groupOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, groupList);
         SDKLog.a("End <= ");
      } else if (group == null) {
         groupList = new ArrayList();
         OnDidUpdateGroups(groupOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, groupList);
         SDKLog.a("End <= ");
      } else {
         removeGroupRequestOwner = groupOwner;
         JSONObject obj = new JSONObject();
         int sn = Utils.getSn();

         try {
            obj.put("cmd", 1305);
            obj.put("sn", sn);
            obj.put("mac", groupOwner.getMacAddress());
            obj.put("did", groupOwner.getDid());
            obj.put("productKey", groupOwner.getProductKey());
            obj.put("groupID", group.getGroupID());
         } catch (JSONException var5) {
            SDKLog.e(var5.toString());
            var5.printStackTrace();
         }

         sendMessageToDaemon(obj);
         if (groupOwner.isLAN) {
            makeTimer(9000, 1306, sn);
         } else {
            makeTimer(20000, 1306, sn);
         }

         SDKLog.a("End <= ");
      }
   }

   public static void updateGroups(GizWifiDevice groupOwner) {
      SDKLog.a("Start => , groupOwner: " + (groupOwner == null ? "null" : groupOwner.simpleInfoMasking()));
      ArrayList groupList;
      if (!Constant.ishandshake) {
         groupList = new ArrayList();
         OnDidUpdateGroups(groupOwner, GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN, groupList);
         SDKLog.a("End <= ");
      } else if (groupOwner == null) {
         groupList = new ArrayList();
         OnDidUpdateGroups(groupOwner, GizWifiErrorCode.GIZ_SDK_PARAM_INVALID, groupList);
         SDKLog.a("End <= ");
      } else {
         updateGroupRequestOwner = groupOwner;
         JSONObject obj = new JSONObject();
         int sn = Utils.getSn();

         try {
            obj.put("cmd", 1307);
            obj.put("sn", sn);
            obj.put("mac", groupOwner.getMacAddress());
            obj.put("did", groupOwner.getDid());
            obj.put("productKey", groupOwner.getProductKey());
         } catch (JSONException var4) {
            SDKLog.e(var4.toString());
            var4.printStackTrace();
         }

         sendMessageToDaemon(obj);
         if (groupOwner.isLAN) {
            makeTimer(9000, 1308, sn);
         } else {
            makeTimer(20000, 1308, sn);
         }

         SDKLog.a("End <= ");
      }
   }

   private static void OnDidUpdateGroups(GizWifiDevice groupOwner, GizWifiErrorCode result, List<GizDeviceGroup> groupList) {
      SDKLog.d("Ready to callback, listener: " + (mListener == null ? "null" : mListener));
      SDKLog.d("Callback begin, result: " + result.name() + ", groupOwner: " + (groupOwner == null ? "null" : groupOwner.simpleInfoMasking()) + ", groupList: " + listMasking(groupList));
      if (mListener != null) {
         mListener.didUpdateGroups(groupOwner, result, groupList);
         SDKLog.d("Callback end");
      }

   }

   private static void sendMessageToDaemon(JSONObject obj) {
      MessageHandler.getSingleInstance().send(obj.toString());
   }

   private static void makeTimer(int timeout, int cmd, int sn) {
      Message mes = Message.obtain();
      mes.what = sn;
      mes.obj = cmd;
      timeoutHandler.sendMessageDelayed(mes, (long)timeout);
   }

   private static void handleTimeoutMessage(Message msg) {
      int cmd = (Integer)msg.obj;
      List<GizDeviceGroup> list = new ArrayList();
      switch(cmd) {
         case 1302:
            timeoutHandler.removeMessages(msg.what);
            OnDidUpdateGroups(addGroupRequestOwner, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
            break;
         case 1306:
            timeoutHandler.removeMessages(msg.what);
            OnDidUpdateGroups(removeGroupRequestOwner, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
            break;
         case 1308:
            timeoutHandler.removeMessages(msg.what);
            OnDidUpdateGroups(updateGroupRequestOwner, GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT, list);
      }

   }

   private static void handleReceiveMessage(int cmd, JSONObject obj, int sn, GizWifiDevice deviceOwner) throws JSONException {
      int errorCode;
      JSONArray groups;
      List<GizDeviceGroup> list;
      String groupID;
      switch(cmd) {
         case 1302:
            errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
            if (errorCode != 0) {
               timeoutHandler.removeMessages(sn);
                list = new ArrayList();
               OnDidUpdateGroups(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
            } else {
               messageQueue.add(sn);
            }
            break;
         case 1306:
            errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
             groupID = obj.has("groupID") ? obj.getString("groupID") : "";
            if (errorCode != 0) {
               timeoutHandler.removeMessages(sn);
               list = new ArrayList();
               OnDidUpdateGroups(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
            } else {
               messageQueue.add(sn);
               Iterator var20 = getValidGroupListByOwner(deviceOwner).iterator();

               GizDeviceGroup group;
               do {
                  if (!var20.hasNext()) {
                     return;
                  }

                  group = (GizDeviceGroup)var20.next();
               } while(!group.getGroupID().equals(groupID));

               group.setIsValid(false);
            }
            break;
         case 1308:
            errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
            groups = obj.has("groups") ? obj.getJSONArray("groups") : null;
            timeoutHandler.removeMessages(sn);
            if (errorCode != 0) {
               list = new ArrayList();
               OnDidUpdateGroups(deviceOwner, GizWifiErrorCode.valueOf(errorCode), list);
            } else {
               synchronizedMyGroupList(deviceOwner, groups);
               getAllGroupDeviceJson(deviceOwner.getMacAddress(), deviceOwner.getDid(), deviceOwner.getProductKey());
               OnDidUpdateGroups(deviceOwner, GizWifiErrorCode.valueOf(errorCode), getValidGroupListByOwner(deviceOwner));
            }
            break;
         case 1318:
            errorCode = obj.has("errorCode") ? obj.getInt("errorCode") : GizWifiErrorCode.GIZ_SDK_OTHERWISE.getResult();
            groups = obj.has("groups") ? obj.getJSONArray("groups") : null;
            if (errorCode == 0 && groups != null) {
               for(int i = 0; i < groups.length(); ++i) {
                  JSONObject jsonOb = (JSONObject)groups.get(i);
                   groupID = jsonOb.has("groupID") ? jsonOb.getString("groupID") : "";
                  JSONArray groupDevices = jsonOb.has("groupDevices") ? jsonOb.getJSONArray("groupDevices") : null;
                  Iterator var10 = getValidGroupListByOwner(deviceOwner).iterator();

                  while(var10.hasNext()) {
                     GizDeviceGroup group = (GizDeviceGroup)var10.next();
                     if (group.getGroupID().equals(groupID)) {
                        boolean update = group.saveGroupDeviceList(groupDevices);
                        if (update) {
                           group.OnDidUpdateGroupDevices(group, GizWifiErrorCode.GIZ_SDK_SUCCESS, group.getGroupDeviceList());
                        }
                        break;
                     }
                  }
               }

               return;
            } else {
               SDKLog.d("cmd 1318: " + GizWifiErrorCode.valueOf(errorCode));
               break;
            }
         case 2021:
             groups = obj.has("groups") ? obj.getJSONArray("groups") : null;
            Iterator var5 = messageQueue.iterator();

            while(var5.hasNext()) {
               Integer waitsn = (Integer)var5.next();
               if (timeoutHandler.hasMessages(waitsn)) {
                  timeoutHandler.removeMessages(waitsn);
               }
            }

            synchronizedMyGroupList(deviceOwner, groups);
            getAllGroupDeviceJson(deviceOwner.getMacAddress(), deviceOwner.getDid(), deviceOwner.getProductKey());
            OnDidUpdateGroups(deviceOwner, GizWifiErrorCode.GIZ_SDK_SUCCESS, getValidGroupListByOwner(deviceOwner));
      }

   }

   private static void getAllGroupDeviceJson(String mac, String did, String pk) {
      int sn = Utils.getSn();
      JSONObject obj = new JSONObject();

      try {
         obj.put("cmd", 1317);
         obj.put("sn", sn);
         obj.put("mac", mac);
         obj.put("did", did);
         obj.put("productKey", pk);
      } catch (JSONException var6) {
         SDKLog.e(var6.toString());
         var6.printStackTrace();
      }

      sendMessageToDaemon(obj);
   }

   private static void synchronizedMyGroupList(GizWifiDevice owner, JSONArray jsonArray) throws JSONException {
      if (owner != null && jsonArray != null) {
         List<GizDeviceGroup> newList = new ArrayList();
         List<GizDeviceGroup> cacheList = new ArrayList();
         boolean isHasOwner = false;
         Iterator var5 = groupListMap.keySet().iterator();

         while(var5.hasNext()) {
            GizWifiDevice key = (GizWifiDevice)var5.next();
            if (owner != null && key.getMacAddress().equals(owner.getMacAddress()) && key.getDid().equals(owner.getDid()) && key.getProductKey().equals(owner.getProductKey())) {
               isHasOwner = true;
               cacheList = (List)groupListMap.get(key);
               break;
            }
         }

         var5 = ((List)cacheList).iterator();

         while(var5.hasNext()) {
            GizDeviceGroup object = (GizDeviceGroup)var5.next();
            object.setIsValid(false);
         }

         for(int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonOb = (JSONObject)jsonArray.get(i);
            String groupID = jsonOb.has("groupID") ? jsonOb.getString("groupID") : "";
            String groupType = jsonOb.has("groupType") ? jsonOb.getString("groupType") : null;
            String groupName = jsonOb.has("groupName") ? jsonOb.getString("groupName") : null;
            if (!isHasOwner) {
               GizDeviceGroup group = new GizDeviceGroup();
               group.setGroupID(groupID);
               group.setGroupName(groupName);
               group.setGroupType(groupType);
               group.setGroupOwner(owner);
               group.setIsValid(true);
               newList.add(group);
            } else {
               boolean isHasOb = false;
               Iterator var11 = ((List)cacheList).iterator();

               while(var11.hasNext()) {
                  GizDeviceGroup object = (GizDeviceGroup)var11.next();
                  if (groupID.equals(object.getGroupID())) {
                     object.setGroupType(groupType);
                     object.setGroupName(groupName);
                     object.setGroupOwner(owner);
                     object.setIsValid(true);
                     isHasOb = true;
                     break;
                  }
               }

               if (!isHasOb) {
                  GizDeviceGroup group = new GizDeviceGroup();
                  group.setGroupID(groupID);
                  group.setGroupName(groupName);
                  group.setGroupType(groupType);
                  group.setGroupOwner(owner);
                  group.setIsValid(true);
                  ((List)cacheList).add(group);
               }
            }
         }

         if (!isHasOwner && newList.size() > 0) {
            groupListMap.put(owner, newList);
         }

      } else {
         SDKLog.e("owner: " + owner + ", jsonArray: " + jsonArray);
      }
   }
}
