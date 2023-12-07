//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.gizwits.gizwifisdk.enumration.GizConditionOperator;
import java.util.concurrent.ConcurrentHashMap;

public class GizDeviceJointActionRuleCondition implements Parcelable {
    private GizWifiDevice device;
    private ConcurrentHashMap<String, Object> data;
    private GizConditionOperator conditionOperator;
    private boolean isValid = true;
    public static final Creator<GizDeviceJointActionRuleCondition> CREATOR = new Creator<GizDeviceJointActionRuleCondition>() {
        public GizDeviceJointActionRuleCondition createFromParcel(Parcel source) {
            return new GizDeviceJointActionRuleCondition(source);
        }

        public GizDeviceJointActionRuleCondition[] newArray(int size) {
            return new GizDeviceJointActionRuleCondition[size];
        }
    };

    public String toString() {
        return "GizDeviceJointActionRuleCondition [device=" + this.device + ", data=" + this.data + ", conditionOperator=" + this.conditionOperator + "]";
    }

    protected String infoMasking() {
        return "GizDeviceJointActionRuleCondition [device=" + this.device.moreSimpleInfoMasking() + ", data=" + this.data + ", conditionOperator=" + this.conditionOperator + ", isValid=" + this.isValid + "]";
    }

    public GizWifiDevice getDevice() {
        return this.device;
    }

    protected void setDevice(GizWifiDevice device) {
        this.device = device;
    }

    public ConcurrentHashMap<String, Object> getData() {
        return this.data;
    }

    protected void setData(ConcurrentHashMap<String, Object> data) {
        this.data = data;
    }

    public GizConditionOperator getConditionOperator() {
        return this.conditionOperator;
    }

    protected void setConditionOperator(GizConditionOperator conditionOperator) {
        this.conditionOperator = conditionOperator;
    }

    protected boolean getIsValid() {
        return this.isValid;
    }

    protected void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public GizDeviceJointActionRuleCondition(GizWifiDevice device, ConcurrentHashMap<String, Object> data, GizConditionOperator conditionOperator) {
        this.setDevice(device);
        this.setData(data);
        this.setConditionOperator(conditionOperator);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.device, flags);
        dest.writeSerializable(this.data);
        dest.writeInt(this.conditionOperator == null ? -1 : this.conditionOperator.ordinal());
        dest.writeByte((byte)(this.isValid ? 1 : 0));
    }

    protected GizDeviceJointActionRuleCondition(Parcel in) {
        this.device = (GizWifiDevice)in.readParcelable(GizWifiDevice.class.getClassLoader());
        this.data = (ConcurrentHashMap)in.readSerializable();
        int tmpConditionOperator = in.readInt();
        this.conditionOperator = tmpConditionOperator == -1 ? null : GizConditionOperator.values()[tmpConditionOperator];
        this.isValid = in.readByte() != 0;
    }
}
