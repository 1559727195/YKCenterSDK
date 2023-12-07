//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.gizwits.gizwifisdk.enumration.GizSchedulerTaskType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GizDeviceSchedulerTask implements Parcelable {
    GizDeviceGroup group;
    private ConcurrentHashMap<String, Object> data;
    GizDeviceScene scene;
    GizWifiDevice device;
    boolean sceneStartup;
    protected GizSchedulerTaskType taskType;
    public static final Creator<GizDeviceSchedulerTask> CREATOR = new Creator<GizDeviceSchedulerTask>() {
        public GizDeviceSchedulerTask createFromParcel(Parcel source) {
            GizDeviceSchedulerTask task = null;
            int ordinal = source.readInt();
            HashMap map;
            ConcurrentHashMap hash;
            switch(ordinal) {
                case 0:
                    GizWifiDevice device = (GizWifiDevice)source.readParcelable(GizWifiDevice.class.getClassLoader());
                    map = source.readHashMap(HashMap.class.getClassLoader());
                    hash = new ConcurrentHashMap();
                    if (map != null) {
                        Iterator var10 = map.keySet().iterator();

                        while(var10.hasNext()) {
                            String key = (String)var10.next();
                            hash.put(key, map.get(key));
                        }
                    }

                    task = new GizDeviceSchedulerTask(device, hash);
                    break;
                case 1:
                    GizDeviceGroup group = (GizDeviceGroup)source.readParcelable(GizDeviceGroup.class.getClassLoader());
                    map = source.readHashMap(HashMap.class.getClassLoader());
                    hash = new ConcurrentHashMap();
                    if (map != null) {
                        Iterator var11 = map.keySet().iterator();

                        while(var11.hasNext()) {
                            String keyx = (String)var11.next();
                            hash.put(keyx, map.get(keyx));
                        }
                    }

                    task = new GizDeviceSchedulerTask(group, hash);
                    break;
                case 2:
                    GizDeviceScene scene = (GizDeviceScene)source.readParcelable(GizDeviceScene.class.getClassLoader());
                    boolean b = source.readByte() != 0;
                    task = new GizDeviceSchedulerTask(scene, b);
            }

            return task;
        }

        public GizDeviceSchedulerTask[] newArray(int size) {
            return null;
        }
    };

    protected String infoMasking() {
        return "GizDeviceSchedulerTask [taskType=" + this.taskType.name() + ", device=" + (this.device == null ? "null" : this.device.moreSimpleInfoMasking()) + ", group=" + (this.group == null ? "null" : this.group.getGroupID()) + ", data=" + (this.data == null ? "null" : this.data) + ", scene=" + (this.scene == null ? "null" : this.scene.getSceneID()) + ", sceneStartup=" + this.sceneStartup + "]";
    }

    public ConcurrentHashMap<String, Object> getData() {
        return this.data;
    }

    public void setData(ConcurrentHashMap<String, Object> data) {
        this.data = data;
    }

    public GizDeviceGroup getGroup() {
        return this.group;
    }

    protected void setGroup(GizDeviceGroup group) {
        this.group = group;
    }

    public GizDeviceScene getScene() {
        return this.scene;
    }

    protected void setScene(GizDeviceScene scene) {
        this.scene = scene;
    }

    public GizSchedulerTaskType getTaskType() {
        return this.taskType;
    }

    protected void setTaskType(GizSchedulerTaskType taskType) {
        this.taskType = taskType;
    }

    public GizWifiDevice getDevice() {
        return this.device;
    }

    public void setDevice(GizWifiDevice device) {
        this.device = device;
    }

    public boolean getSceneStartup() {
        return this.sceneStartup;
    }

    public void setSceneStartup(boolean sceneStartup) {
        this.sceneStartup = sceneStartup;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeInt(this.taskType.ordinal());
        HashMap mypp;
        Iterator var4;
        String key;
        switch(this.taskType.ordinal()) {
            case 0:
                arg0.writeParcelable(this.device, arg1);
                if (this.data != null) {
                    mypp = new HashMap();
                    var4 = this.data.keySet().iterator();

                    while(var4.hasNext()) {
                        key = (String)var4.next();
                        mypp.put(key, this.data.get(key));
                    }

                    arg0.writeMap(mypp);
                } else {
                    arg0.writeMap((Map)null);
                }
                break;
            case 1:
                arg0.writeParcelable(this.group, arg1);
                if (this.data != null) {
                    mypp = new HashMap();
                    var4 = this.data.keySet().iterator();

                    while(var4.hasNext()) {
                        key = (String)var4.next();
                        mypp.put(key, this.data.get(key));
                    }

                    arg0.writeMap(mypp);
                } else {
                    arg0.writeMap((Map)null);
                }
                break;
            case 2:
                arg0.writeParcelable(this.scene, arg1);
                arg0.writeByte((byte)(this.sceneStartup ? 1 : 0));
        }

    }

    public GizDeviceSchedulerTask(GizWifiDevice device, ConcurrentHashMap<String, Object> data) {
        this.taskType = GizSchedulerTaskType.GizSchedulerTaskDevice;
        this.setDevice(device);
        this.setData(data);
    }

    public GizDeviceSchedulerTask(GizDeviceGroup group, ConcurrentHashMap<String, Object> data) {
        this.taskType = GizSchedulerTaskType.GizSchedulerTaskGroup;
        this.setGroup(group);
        this.setData(data);
    }

    public GizDeviceSchedulerTask(GizDeviceScene scene, boolean startUp) {
        this.taskType = GizSchedulerTaskType.GizSchedulerTaskScene;
        this.setScene(scene);
        this.setSceneStartup(startUp);
    }
}
