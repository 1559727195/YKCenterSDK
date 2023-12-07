//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.gizwits.gizwifisdk.enumration.GizSceneItemType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class GizDeviceSceneItem implements Parcelable {
    GizDeviceGroup group;
    private ConcurrentHashMap<String, Object> data = new ConcurrentHashMap();
    private int delayTime;
    private GizWifiDevice device;
    GizSceneItemType sceneItemType;
    public static final Creator<GizDeviceSceneItem> CREATOR = new Creator<GizDeviceSceneItem>() {
        public GizDeviceSceneItem createFromParcel(Parcel source) {
            GizDeviceSceneItem item = null;
            int type = source.readInt();
            HashMap map;
            ConcurrentHashMap hash;
            switch(type) {
                case 0:
                    GizWifiDevice device = (GizWifiDevice)source.readParcelable(GizWifiDevice.class.getClassLoader());
                    map = source.readHashMap(HashMap.class.getClassLoader());
                    hash = new ConcurrentHashMap();
                    Iterator var10 = map.keySet().iterator();

                    while(var10.hasNext()) {
                        String key = (String)var10.next();
                        hash.put(key, map.get(key));
                    }

                    item = new GizDeviceSceneItem(device, hash);
                    break;
                case 1:
                    GizDeviceGroup gp = (GizDeviceGroup)source.readParcelable(GizDeviceGroup.class.getClassLoader());
                    map = source.readHashMap(HashMap.class.getClassLoader());
                    hash = new ConcurrentHashMap();
                    Iterator var11 = map.keySet().iterator();

                    while(var11.hasNext()) {
                        String keyx = (String)var11.next();
                        hash.put(keyx, map.get(keyx));
                    }

                    item = new GizDeviceSceneItem(gp, hash);
                    break;
                case 2:
                    int delaytime = source.readInt();
                    item = new GizDeviceSceneItem(delaytime);
            }

            return item;
        }

        public GizDeviceSceneItem[] newArray(int size) {
            return null;
        }
    };

    public String toString() {
        return "GizDeviceSceneItem [data = " + this.data + ", delayTime =" + this.delayTime + "]";
    }

    protected String infoMasking() {
        return "GizDeviceSceneItem [sceneItemType=" + this.sceneItemType + ", delayTime=" + this.delayTime + ", device=" + (this.device == null ? "null" : this.device.moreSimpleInfoMasking()) + ", group=" + (this.group == null ? "null" : this.group.getGroupID()) + ", data=" + this.data + "]";
    }

    public GizDeviceSceneItem(GizWifiDevice device, ConcurrentHashMap<String, Object> data) {
        this.setSceneItemType(GizSceneItemType.GizSceneItemDevice);
        this.setDevice(device);
        this.setData(data);
    }

    public GizDeviceSceneItem(GizDeviceGroup group, ConcurrentHashMap<String, Object> data) {
        this.setSceneItemType(GizSceneItemType.GizSceneItemGroup);
        this.setGroup(group);
        this.setData(data);
    }

    public GizDeviceSceneItem(int delayTime) {
        this.setSceneItemType(GizSceneItemType.GizSceneItemDelay);
        this.setDelayTime(delayTime);
    }

    public GizWifiDevice getDevice() {
        return this.device;
    }

    protected void setDevice(GizWifiDevice device) {
        this.device = device;
    }

    public GizDeviceGroup getGroup() {
        return this.group;
    }

    protected void setGroup(GizDeviceGroup group) {
        this.group = group;
    }

    public GizSceneItemType getSceneItemType() {
        return this.sceneItemType;
    }

    protected void setSceneItemType(GizSceneItemType sceneItemType) {
        this.sceneItemType = sceneItemType;
    }

    public ConcurrentHashMap<String, Object> getData() {
        return this.data;
    }

    protected void setData(ConcurrentHashMap<String, Object> data) {
        this.data.putAll(data);
    }

    public int getDelayTime() {
        return this.delayTime;
    }

    protected void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeInt(this.sceneItemType.ordinal());
        HashMap map;
        Iterator var4;
        String key;
        switch(this.sceneItemType.ordinal()) {
            case 0:
                arg0.writeParcelable(this.device, 1);
                map = new HashMap();
                if (this.data != null) {
                    var4 = this.data.keySet().iterator();

                    while(var4.hasNext()) {
                        key = (String)var4.next();
                        map.put(key, this.data.get(key));
                    }
                }

                arg0.writeMap(map);
                break;
            case 1:
                arg0.writeParcelable(this.group, 1);
                map = new HashMap();
                if (this.data != null) {
                    var4 = this.data.keySet().iterator();

                    while(var4.hasNext()) {
                        key = (String)var4.next();
                        map.put(key, this.data.get(key));
                    }
                }

                arg0.writeMap(map);
                break;
            case 2:
                arg0.writeInt(this.delayTime);
        }

    }
}
