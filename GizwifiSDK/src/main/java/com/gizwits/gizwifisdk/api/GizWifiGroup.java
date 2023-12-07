//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiGroupListener;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** @deprecated */
public class GizWifiGroup implements Serializable {
    public JSONObject groupJson;
    protected String uid;
    /** @deprecated */
    public String gid;
    /** @deprecated */
    public String productKey;
    private String groupType;
    /** @deprecated */
    public String groupName;
    private GizWifiGroupListener mListener;
    private List<ConcurrentHashMap<String, String>> deviceList = new ArrayList();

    public String getGroupType() {
        return this.groupType;
    }

    protected GizWifiGroup(String gid, String groupType, String groupName) {
        this.gid = gid;
        this.productKey = groupType;
        this.groupName = groupName;
        this.groupType = groupType;
    }

    protected GizWifiGroup(JSONObject json) {
        this.groupJson = json;

        try {
            this.gid = this.groupJson.getString("gid");
            this.groupType = this.groupJson.getString("productKey");
            this.groupName = this.groupJson.getString("groupName");
            this.uid = this.groupJson.getString("uid");
        } catch (JSONException var3) {
            var3.printStackTrace();
        }

    }

    protected void onGroupDidGetDevices(GizWifiErrorCode result, GizWifiGroup group, List<ConcurrentHashMap<String, String>> deviceList) {
        SDKLog.d("Ready to callback, listener: " + (this.mListener == null ? "null" : this.mListener));
        if (this.mListener != null) {
            this.mListener.didGetDevices(result, this, deviceList);
            this.mListener.didGetDevices(Utils.changeErrorCode(result.getResult()), deviceList);
            SDKLog.d("Callback end");
        }

    }

    public void setListener(GizWifiGroupListener listener) {
        this.mListener = listener;
    }

    public String getGid() {
        return this.gid;
    }

    protected void setGid(String gid) {
        this.gid = gid;
    }

    protected void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getGroupName() {
        return this.groupName;
    }

    protected void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    protected static String getGroupConfigFilePath(String uid) {
        String groupConfigFilePath = "";
        SDKEventManager.getInstance();
        if (null == SDKEventManager.mContext) {
            SDKLog.e("Please startWithAppID first!");
            return groupConfigFilePath;
        } else {
            StringBuilder var10000 = new StringBuilder();
            SDKEventManager.getInstance();
            return var10000.append(SDKEventManager.mContext.getFilesDir()).append("/XPGWifiSDK/GroupConfigInfo/").append(uid).append("_group.json").toString();
        }
    }

    protected static String readFromFile(String path) {
        String content = new String();
        File file = new File(path);
        if (file.exists()) {
            try {
                FileReader reader = new FileReader(file);
                char[] chars = new char[(int)file.length()];
                reader.read(chars);
                content = new String(chars);
                reader.close();
            } catch (IOException var5) {
                var5.printStackTrace();
            }
        }

        return content;
    }

    protected static String writeToFile(String str, String path) {
        String content = null;
        File file = new File(path);
        File folder = new File(path.substring(0, path.lastIndexOf(47)));
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(str);
            writer.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return (String)content;
    }

    protected static JSONArray getGroupConfigJson(String uid) throws JSONException {
        JSONArray groupArray = new JSONArray();
        String groupJsonStr = readFromFile(getGroupConfigFilePath(uid));
        if (groupJsonStr.length() > 0) {
            groupArray = new JSONArray(groupJsonStr);
        }

        return groupArray;
    }

    protected static JSONArray removeJSONArray(JSONArray jsonArray, int pos) {
        JSONArray newJsonArray = new JSONArray();

        try {
            for(int i = 0; i < jsonArray.length(); ++i) {
                if (i != pos) {
                    newJsonArray.put(jsonArray.get(i));
                }
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return newJsonArray;
    }

    public void getDevices() {
        SDKLog.d("Start => gid: " + this.getGid() + ", groupName: " + this.getGroupName());
        List<ConcurrentHashMap<String, String>> devices = this.getDeviceList();
        if (devices != null) {
            this.onGroupDidGetDevices(GizWifiErrorCode.GIZ_SDK_SUCCESS, this, devices);
        } else {
            this.onGroupDidGetDevices(GizWifiErrorCode.GIZ_SDK_GROUP_LIST_UPDATE_FAILED, this, new ArrayList());
        }

        SDKLog.d("End <= ");
    }

    private void saveGroupJsonToFile() throws JSONException {
        JSONArray groupArray = getGroupConfigJson(this.uid);

        for(int i = 0; i < groupArray.length(); ++i) {
            if (groupArray.getJSONObject(i).getString("gid").equals(this.gid)) {
                groupArray.put(i, this.groupJson);
                writeToFile(groupArray.toString(), getGroupConfigFilePath(this.uid));
                break;
            }
        }

    }

    public void addDevice(String did, String subdid) {
        if (this.groupJson == null) {
            this.onGroupDidGetDevices(GizWifiErrorCode.GIZ_SDK_GROUP_LIST_UPDATE_FAILED, this, new ArrayList());
        } else {
            try {
                JSONArray devicesJson = new JSONArray();
                JSONObject deviceJson = new JSONObject();
                deviceJson.put("did", did);
                deviceJson.put("sdid", subdid);
                if (!this.groupJson.has("devices")) {
                    devicesJson.put(deviceJson);
                } else {
                    devicesJson = this.groupJson.getJSONArray("devices");
                    boolean isExisted = false;

                    for(int i = 0; i < devicesJson.length(); ++i) {
                        if (devicesJson.getJSONObject(i).getString("sdid").equals(subdid) && devicesJson.getJSONObject(i).getString("did").equals(did)) {
                            isExisted = true;
                            break;
                        }
                    }

                    if (!isExisted) {
                        devicesJson.put(deviceJson);
                    }
                }

                this.groupJson.put("devices", devicesJson);
                this.saveGroupJsonToFile();
                this.onGroupDidGetDevices(GizWifiErrorCode.GIZ_SDK_SUCCESS, this, this.getDeviceList());
            } catch (JSONException var7) {
                SDKLog.e(var7.toString());
                var7.printStackTrace();
                this.onGroupDidGetDevices(GizWifiErrorCode.GIZ_SDK_GROUP_LIST_UPDATE_FAILED, this, new ArrayList());
            }
        }

        SDKLog.d("End <= ");
    }

    public void removeDevice(String did, String subdid) {
        if (this.groupJson == null) {
            this.onGroupDidGetDevices(GizWifiErrorCode.GIZ_SDK_GROUP_DELETE_FAILED, this, new ArrayList());
        } else {
            try {
                if (this.groupJson.has("devices")) {
                    JSONArray devicesJson = this.groupJson.getJSONArray("devices");
                    int j = -1;

                    for(int i = 0; i < devicesJson.length(); ++i) {
                        if (devicesJson.getJSONObject(i).getString("sdid").equals(subdid) && devicesJson.getJSONObject(i).getString("did").equals(did)) {
                            j = i;
                            break;
                        }
                    }

                    if (j >= 0) {
                        devicesJson = removeJSONArray(devicesJson, j);
                        this.groupJson.put("devices", devicesJson);
                        this.saveGroupJsonToFile();
                        this.onGroupDidGetDevices(GizWifiErrorCode.GIZ_SDK_SUCCESS, this, this.getDeviceList());
                    } else {
                        this.onGroupDidGetDevices(GizWifiErrorCode.GIZ_SDK_GROUP_DELETE_FAILED, this, new ArrayList());
                    }
                }
            } catch (JSONException var6) {
                var6.printStackTrace();
                SDKLog.e(var6.toString());
                this.onGroupDidGetDevices(GizWifiErrorCode.GIZ_SDK_GROUP_DELETE_FAILED, this, new ArrayList());
            }
        }

        SDKLog.d("End <= ");
    }

    public List<ConcurrentHashMap<String, String>> getDeviceList() {
        if (this.groupJson == null) {
            SDKLog.d("There is no group json");
            return null;
        } else {
            List<ConcurrentHashMap<String, String>> devicesList = new ArrayList();
            if (this.groupJson != null) {
                try {
                    JSONArray devicesJson = this.groupJson.getJSONArray("devices");

                    for(int i = 0; i < devicesJson.length(); ++i) {
                        ConcurrentHashMap<String, String> deviceMap = new ConcurrentHashMap();
                        deviceMap.put("did", devicesJson.getJSONObject(i).getString("did"));
                        deviceMap.put("sdid", devicesJson.getJSONObject(i).getString("sdid"));
                        devicesList.add(deviceMap);
                    }
                } catch (JSONException var5) {
                    var5.printStackTrace();
                }
            }

            this.deviceList.clear();
            this.deviceList.addAll(devicesList);
            return this.deviceList;
        }
    }

    protected void setDeviceList(List<ConcurrentHashMap<String, String>> deviceList) {
        if (this.groupJson == null) {
            SDKLog.d("Can't set, group Json is null");
        } else {
            try {
                JSONArray devicesJson = new JSONArray();

                for(int i = 0; i < deviceList.size(); ++i) {
                    ConcurrentHashMap<String, String> device = (ConcurrentHashMap)deviceList.get(i);
                    JSONObject newdeviceJson = new JSONObject();
                    newdeviceJson.put("did", device.get("did"));
                    newdeviceJson.put("sdid", device.get("sdid"));
                    boolean isExisted = false;
                    if (this.groupJson.has("devices")) {
                        devicesJson = this.groupJson.getJSONArray("devices");

                        for(int j = 0; j < devicesJson.length(); ++j) {
                            String SDID = devicesJson.getJSONObject(j).getString("sdid");
                            String DID = devicesJson.getJSONObject(j).getString("did");
                            if (SDID.equals(device.get("sdid")) && DID.equals(device.get("did"))) {
                                isExisted = true;
                                break;
                            }
                        }
                    }

                    if (!isExisted) {
                        devicesJson.put(newdeviceJson);
                    }
                }

                this.groupJson.put("devices", devicesJson);
                this.saveGroupJsonToFile();
            } catch (JSONException var10) {
                var10.printStackTrace();
                SDKLog.e(var10.toString());
            }

        }
    }
}
