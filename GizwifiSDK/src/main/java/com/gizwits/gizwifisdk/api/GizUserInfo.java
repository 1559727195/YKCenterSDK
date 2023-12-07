//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import com.gizwits.gizwifisdk.enumration.GizUserGenderType;
import com.gizwits.gizwifisdk.enumration.XPGUserGenderType;
import java.io.Serializable;

public class GizUserInfo implements Serializable {
    private String address;
    private String birthday;
    private String email;
    private String lang;
    private String name;
    private String phone;
    private String remark;
    private String uid;
    private String username;
    private boolean isAnonymous;
    private XPGUserGenderType gender;
    private GizUserGenderType userGender;
    private String deviceBindTime;

    public GizUserInfo() {
    }

    protected String infoMasking() {
        String addressMasking = this.address == null ? "null" : Utils.dataMasking(this.address);
        String birthdayMasking = this.birthday == null ? "null" : Utils.dataMasking(this.birthday);
        String emailMasking = this.email == null ? "null" : Utils.dataMasking(this.email);
        String phoneMasking = this.phone == null ? "null" : Utils.dataMasking(this.phone);
        return "uid: " + this.uid + ", username: " + Utils.dataMasking(this.username) + ", userGender: " + (this.userGender == null ? "null" : this.userGender.name()) + ", deviceBindTime: " + this.deviceBindTime + ", address: " + addressMasking + ", birthday: " + birthdayMasking + ", email: " + emailMasking + ", phone: " + phoneMasking + ", name: " + this.name + ", remark: " + this.remark + ", lang: " + this.lang;
    }

    public String getDeviceBindTime() {
        return this.deviceBindTime;
    }

    public void setDeviceBindTime(String deviceBindTime) {
        this.deviceBindTime = deviceBindTime;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLang() {
        return this.lang;
    }

    protected void setLang(String lang) {
        this.lang = lang;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return this.phone;
    }

    protected void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUid() {
        return this.uid;
    }

    protected void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return this.username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    public boolean isAnonymous() {
        return this.isAnonymous;
    }

    protected void setAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public GizUserGenderType getUserGender() {
        return this.userGender;
    }

    /** @deprecated */
    public XPGUserGenderType getGender() {
        return this.gender;
    }

    public void setUserGender(GizUserGenderType userGender) {
        this.userGender = userGender;
    }

    /** @deprecated */
    public void setGender(XPGUserGenderType gender) {
        this.gender = gender;
    }
}
