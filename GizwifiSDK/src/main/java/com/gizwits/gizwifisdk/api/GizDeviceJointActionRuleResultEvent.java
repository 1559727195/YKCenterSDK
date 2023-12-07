package com.gizwits.gizwifisdk.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.gizwits.gizwifisdk.api.GizDeviceGroup;
import com.gizwits.gizwifisdk.api.GizDeviceScene;
import com.gizwits.gizwifisdk.api.GizDeviceSchedulerSuper;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizJointActionRulerEventType;
import java.util.concurrent.ConcurrentHashMap;

public class GizDeviceJointActionRuleResultEvent implements Parcelable {

   private GizWifiDevice device;
   private GizDeviceGroup group;
   private GizDeviceScene scene;
   private GizDeviceSchedulerSuper scheduler;
   private boolean enabled;
   private ConcurrentHashMap data;
   private GizJointActionRulerEventType resultEventType;
   private boolean isValid = true;
   public static final Creator CREATOR = new Creator() {
      public GizDeviceJointActionRuleResultEvent createFromParcel(Parcel source) {
         return new GizDeviceJointActionRuleResultEvent(source);
      }
      public GizDeviceJointActionRuleResultEvent[] newArray(int size) {
         return new GizDeviceJointActionRuleResultEvent[size];
      }
   };


   public String toString() {
      return "GizDeviceJointActionRuleResultEvent [device=" + this.device + ", group=" + this.group + ", data=" + this.data + ", resultEventType=" + this.resultEventType + "]";
   }

   protected String infoMasking() {
      return "GizDeviceJointActionRuleResultEvent [isValid=" + this.isValid + ", resultEventType=" + this.resultEventType + ", device=" + (this.device == null?"null":this.device.moreSimpleInfoMasking()) + ", group=" + (this.group == null?"null":this.group.simpleInfoMasking()) + ", data=" + this.data + ", scene=" + (this.scene == null?"null":this.scene.simpleInfoMasking()) + ", scheduler=" + (this.scheduler == null?"null":this.scheduler.infoMasking()) + ", enabled=" + this.enabled + "]";
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

   public ConcurrentHashMap getData() {
      return this.data;
   }

   protected void setData(ConcurrentHashMap data) {
      this.data = data;
   }

   public GizDeviceScene getScene() {
      return this.scene;
   }

   protected void setScene(GizDeviceScene scene) {
      this.scene = scene;
   }

   public GizDeviceSchedulerSuper getScheduler() {
      return this.scheduler;
   }

   protected void setScheduler(GizDeviceSchedulerSuper scheduler) {
      this.scheduler = scheduler;
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   protected void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public GizJointActionRulerEventType getResultEventType() {
      return this.resultEventType;
   }

   protected void setResultEventType(GizJointActionRulerEventType resultEventType) {
      this.resultEventType = resultEventType;
   }

   protected boolean getIsValid() {
      return this.isValid;
   }

   protected void setIsValid(boolean isValid) {
      this.isValid = isValid;
   }

   public GizDeviceJointActionRuleResultEvent(GizWifiDevice device, ConcurrentHashMap data) {
      this.setDevice(device);
      this.setData(data);
      this.setResultEventType(GizJointActionRulerEventType.GizJointActionRulerEventDevice);
   }

   public GizDeviceJointActionRuleResultEvent(GizDeviceGroup group, ConcurrentHashMap data) {
      this.setGroup(group);
      this.setData(data);
      this.setResultEventType(GizJointActionRulerEventType.GizJointActionRulerEventGroup);
   }

   public GizDeviceJointActionRuleResultEvent(GizDeviceScene scene, boolean enabled) {
      this.setScene(scene);
      this.setEnabled(enabled);
      this.setResultEventType(GizJointActionRulerEventType.GizJointActionRulerEventScene);
   }

   public GizDeviceJointActionRuleResultEvent(GizDeviceSchedulerSuper scheduler, boolean enabled) {
      this.setScheduler(scheduler);
      this.setEnabled(enabled);
      this.setResultEventType(GizJointActionRulerEventType.GizJointActionRulerEventScheduler);
   }

   public int describeContents() {
      return 0;
   }

   public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(this.device, flags);
      dest.writeParcelable(this.group, flags);
      dest.writeParcelable(this.scene, flags);
      dest.writeParcelable((Parcelable)this.scheduler, flags);
      dest.writeByte((byte)(this.enabled?1:0));
      dest.writeSerializable(this.data);
      dest.writeInt(this.resultEventType == null?-1:this.resultEventType.ordinal());
      dest.writeByte((byte)(this.isValid?1:0));
   }

   protected GizDeviceJointActionRuleResultEvent(Parcel in) {
      this.device = (GizWifiDevice)in.readParcelable(GizWifiDevice.class.getClassLoader());
      this.group = (GizDeviceGroup)in.readParcelable(GizDeviceGroup.class.getClassLoader());
      this.scene = (GizDeviceScene)in.readParcelable(GizDeviceScene.class.getClassLoader());
      this.scheduler = (GizDeviceSchedulerSuper)in.readParcelable(GizDeviceSchedulerSuper.class.getClassLoader());
      this.enabled = in.readByte() != 0;
      this.data = (ConcurrentHashMap)in.readSerializable();
      int tmpResultEventType = in.readInt();
      this.resultEventType = tmpResultEventType == -1?null:GizJointActionRulerEventType.values()[tmpResultEventType];
      this.isValid = in.readByte() != 0;
   }

}
