package com.gizwits.gizwifisdk.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.gizwits.gizwifisdk.api.GizUserInfo;
import com.gizwits.gizwifisdk.api.Utils;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingStatus;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingType;
import com.gizwits.gizwifisdk.enumration.GizDeviceSharingWay;

public class GizDeviceSharingInfo implements Parcelable {

   private int id;
   private String deviceID;
   private String productName;
   private String deviceAlias;
   private GizUserInfo userInfo;
   private String alias;
   private GizDeviceSharingType type;
   private GizDeviceSharingWay way;
   private GizDeviceSharingStatus status;
   private String createdAt;
   private String updatedAt;
   private String expiredAt;
   public static final Creator CREATOR = new Creator() {
      public GizDeviceSharingInfo createFromParcel(Parcel source) {
         return new GizDeviceSharingInfo(source);
      }
      public GizDeviceSharingInfo[] newArray(int size) {
         return new GizDeviceSharingInfo[size];
      }
   };


   protected String simpleInfoMasking() {
      return "GizDeviceSharingInfo [id=" + this.id + ", sharingType=" + (this.type == null?"null":this.type.name()) + ", way=" + (this.way == null?"null":this.way.name()) + ", status=" + (this.status == null?"null":this.status.name()) + "]";
   }

   protected String infoMasking() {
      String simpleInfo = this.simpleInfoMasking();
      return simpleInfo + "->[deviceID=" + Utils.dataMasking(this.deviceID) + ", productName=" + this.productName + ", deviceAlias=" + this.deviceAlias + ", userInfo=" + this.userInfo.infoMasking() + "]";
   }

   public int getId() {
      return this.id;
   }

   protected void setId(int id) {
      this.id = id;
   }

   public String getDeviceID() {
      return this.deviceID;
   }

   protected void setDeviceID(String deviceID) {
      this.deviceID = deviceID;
   }

   public String getProductName() {
      return this.productName;
   }

   protected void setProductName(String productName) {
      this.productName = productName;
   }

   public String getDeviceAlias() {
      return this.deviceAlias;
   }

   protected void setDeviceAlias(String deviceAlias) {
      this.deviceAlias = deviceAlias;
   }

   public GizUserInfo getUserInfo() {
      return this.userInfo;
   }

   protected void setUserInfo(GizUserInfo userInfo) {
      this.userInfo = userInfo;
   }

   public String getAlias() {
      return this.alias;
   }

   protected void setAlias(String alias) {
      this.alias = alias;
   }

   public GizDeviceSharingType getType() {
      return this.type;
   }

   protected void setType(GizDeviceSharingType type) {
      this.type = type;
   }

   public GizDeviceSharingWay getWay() {
      return this.way;
   }

   protected void setWay(GizDeviceSharingWay way) {
      this.way = way;
   }

   public GizDeviceSharingStatus getStatus() {
      return this.status;
   }

   protected void setStatus(GizDeviceSharingStatus status) {
      this.status = status;
   }

   public String getCreatedAt() {
      return this.createdAt;
   }

   protected void setCreatedAt(String createdAt) {
      this.createdAt = createdAt;
   }

   public String getUpdatedAt() {
      return this.updatedAt;
   }

   protected void setUpdatedAt(String updatedAt) {
      this.updatedAt = updatedAt;
   }

   public String getExpiredAt() {
      return this.expiredAt;
   }

   protected void setExpiredAt(String expiredAt) {
      this.expiredAt = expiredAt;
   }

   public String toString() {
      return "GizDeviceSharingInfo [id=" + this.id + ", deviceID=" + this.deviceID + ", productName=" + this.productName + ", deviceAlias=" + this.deviceAlias + ", userInfo=" + this.userInfo + ", alias=" + this.alias + ", type=" + this.type + ", way=" + this.way + ", status=" + this.status + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", expiredAt=" + this.expiredAt + "]";
   }

   public int describeContents() {
      return 0;
   }

   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.id);
      dest.writeString(this.deviceID);
      dest.writeString(this.productName);
      dest.writeString(this.deviceAlias);
      dest.writeSerializable(this.userInfo);
      dest.writeString(this.alias);
      dest.writeInt(this.type == null?-1:this.type.ordinal());
      dest.writeInt(this.way == null?-1:this.way.ordinal());
      dest.writeInt(this.status == null?-1:this.status.ordinal());
      dest.writeString(this.createdAt);
      dest.writeString(this.updatedAt);
      dest.writeString(this.expiredAt);
   }

   public GizDeviceSharingInfo() {}

   protected GizDeviceSharingInfo(Parcel in) {
      this.id = in.readInt();
      this.deviceID = in.readString();
      this.productName = in.readString();
      this.deviceAlias = in.readString();
      this.userInfo = (GizUserInfo)in.readSerializable();
      this.alias = in.readString();
      int tmpType = in.readInt();
      this.type = tmpType == -1?null:GizDeviceSharingType.values()[tmpType];
      int tmpWay = in.readInt();
      this.way = tmpWay == -1?null:GizDeviceSharingWay.values()[tmpWay];
      int tmpStatus = in.readInt();
      this.status = tmpStatus == -1?null:GizDeviceSharingStatus.values()[tmpStatus];
      this.createdAt = in.readString();
      this.updatedAt = in.readString();
      this.expiredAt = in.readString();
   }

}
