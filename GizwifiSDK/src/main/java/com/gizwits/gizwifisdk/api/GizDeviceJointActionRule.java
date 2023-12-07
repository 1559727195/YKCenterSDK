//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.gizwits.gizwifisdk.enumration.GizConditionOperator;
import com.gizwits.gizwifisdk.enumration.GizJointActionRulerEventType;
import com.gizwits.gizwifisdk.enumration.GizLogicalOperator;
import com.gizwits.gizwifisdk.log.SDKLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GizDeviceJointActionRule implements Parcelable {
    private List<GizDeviceJointActionRuleCondition> conditionList = new ArrayList();
    private List<GizDeviceJointActionRuleResultEvent> resultEventList = new ArrayList();
    private GizLogicalOperator conditionCombType;
    public static final Creator<GizDeviceJointActionRule> CREATOR = new Creator<GizDeviceJointActionRule>() {
        public GizDeviceJointActionRule createFromParcel(Parcel source) {
            return new GizDeviceJointActionRule(source);
        }

        public GizDeviceJointActionRule[] newArray(int size) {
            return new GizDeviceJointActionRule[size];
        }
    };

    public String toString() {
        return "GizDeviceJointActionRule [conditionList=" + this.conditionList + ", conditionCombType=" + this.conditionCombType + ", resultEventList=" + this.resultEventList + "]";
    }

    protected String infoMasking() {
        return "GizDeviceJointActionRule [conditionCombType=" + this.conditionCombType + ", conditionList(" + this.conditionList.size() + ")->" + conditionListMasking(this.conditionList) + ", resultEventList(" + this.resultEventList.size() + ")->" + resultListMasking(this.resultEventList) + "]";
    }

    protected static String conditionListMasking(List<GizDeviceJointActionRuleCondition> list) {
        String masking = "{size= " + (list == null ? "0" : list.size()) + ", ";
        if (list != null) {
            for(Iterator var2 = list.iterator(); var2.hasNext(); masking = masking + ", ") {
                GizDeviceJointActionRuleCondition object = (GizDeviceJointActionRuleCondition)var2.next();
                masking = masking + "[" + object.infoMasking() + "]";
            }
        }

        return masking.substring(0, masking.length() - 2) + "}";
    }

    protected static String resultListMasking(List<GizDeviceJointActionRuleResultEvent> list) {
        String masking = "{size= " + (list == null ? "0" : list.size()) + ", ";
        if (list != null) {
            for(Iterator var2 = list.iterator(); var2.hasNext(); masking = masking + ", ") {
                GizDeviceJointActionRuleResultEvent object = (GizDeviceJointActionRuleResultEvent)var2.next();
                masking = masking + "[" + object.infoMasking() + "]";
            }
        }

        return masking.substring(0, masking.length() - 2) + "}";
    }

    protected GizDeviceJointActionRule() {
    }

    public GizDeviceJointActionRule(List<GizDeviceJointActionRuleCondition> conditions, GizLogicalOperator conditionCombType, List<GizDeviceJointActionRuleResultEvent> resultEvents) {
        this.setConditionCombType(conditionCombType);
        this.setConditionList(conditions);
        this.setResultEventList(resultEvents);
    }

    public List<GizDeviceJointActionRuleCondition> getConditionList() {
        List<GizDeviceJointActionRuleCondition> myConditionList = new ArrayList();
        if (this.conditionList != null && this.conditionList.size() > 0) {
            Iterator var2 = this.conditionList.iterator();

            while(var2.hasNext()) {
                GizDeviceJointActionRuleCondition jointActionRuleCondition = (GizDeviceJointActionRuleCondition)var2.next();
                if (jointActionRuleCondition != null && jointActionRuleCondition.getIsValid()) {
                    myConditionList.add(jointActionRuleCondition);
                }
            }
        }

        return myConditionList;
    }

    protected void setConditionList(List<GizDeviceJointActionRuleCondition> conditionList) {
        this.conditionList = conditionList;
    }

    public GizLogicalOperator getConditionCombType() {
        return this.conditionCombType;
    }

    protected void setConditionCombType(GizLogicalOperator conditionCombType) {
        this.conditionCombType = conditionCombType;
    }

    public List<GizDeviceJointActionRuleResultEvent> getResultEventList() {
        List<GizDeviceJointActionRuleResultEvent> myResultEventList = new ArrayList();
        if (this.resultEventList != null && this.resultEventList.size() > 0) {
            Iterator var2 = this.resultEventList.iterator();

            while(var2.hasNext()) {
                GizDeviceJointActionRuleResultEvent jointActionRuleResultEvent = (GizDeviceJointActionRuleResultEvent)var2.next();
                if (jointActionRuleResultEvent != null && jointActionRuleResultEvent.getIsValid()) {
                    myResultEventList.add(jointActionRuleResultEvent);
                }
            }
        }

        return myResultEventList;
    }

    protected void setResultEventList(List<GizDeviceJointActionRuleResultEvent> resultEventList) {
        this.resultEventList = resultEventList;
    }

    protected boolean updateRuleSettings(GizWifiDevice deviceOwner, JSONObject jsonObj) {
        boolean update = false;
        if (jsonObj != null && jsonObj.length() != 0) {
            try {
                JSONArray conditionJsonArray = jsonObj.has("conditions") ? jsonObj.getJSONArray("conditions") : null;
                JSONArray resultEventJsonArray = jsonObj.has("resultEvents") ? jsonObj.getJSONArray("resultEvents") : null;
                int conditionType = jsonObj.has("conditionCombType") ? jsonObj.getInt("conditionCombType") : -1;
                if (this.conditionCombType == null && -1 != conditionType) {
                    update = true;
                }

                if (conditionType == GizLogicalOperator.GizLogicalAnd.ordinal()) {
                    this.conditionCombType = GizLogicalOperator.GizLogicalAnd;
                } else if (conditionType == GizLogicalOperator.GizLogicalOr.ordinal()) {
                    this.conditionCombType = GizLogicalOperator.GizLogicalOr;
                }

                Iterator var7;
                int conditionOperator;
                String schedulerID;
                String mac;
                String did;
                Iterator var16;
                int i;
                JSONObject arrayItem;
                List maps;
                ConcurrentHashMap maps_v4a2;
                if (conditionJsonArray != null && conditionJsonArray.length() > 0) {
                    var7 = this.conditionList.iterator();

                    while(var7.hasNext()) {
                        GizDeviceJointActionRuleCondition condition = (GizDeviceJointActionRuleCondition)var7.next();
                        condition.setIsValid(false);
                    }

                    for(i = 0; i < conditionJsonArray.length(); ++i) {
                        arrayItem = (JSONObject)conditionJsonArray.get(i);
                        conditionOperator = arrayItem.has("conditionOperator") ? arrayItem.getInt("conditionOperator") : -1;
                        if (conditionOperator != -1 && conditionOperator <= GizConditionOperator.GizConditionNotEqual.ordinal() && conditionOperator >= GizConditionOperator.GizConditionEqual.ordinal()) {
                            JSONObject deviceJson = arrayItem.has("device") ? arrayItem.getJSONObject("device") : null;
                            if (deviceJson != null && deviceJson.length() != 0) {
                                schedulerID = deviceJson.has("mac") ? deviceJson.getString("mac") : "";
                                mac = deviceJson.has("did") ? deviceJson.getString("did") : "";
                                did = deviceJson.has("productKey") ? deviceJson.getString("productKey") : "";
                                GizWifiDevice conditionDevice = SDKEventManager.getInstance().findDeviceInTotalDeviceList(mac, schedulerID, did);
                                if (conditionDevice == null) {
                                    SDKLog.d("json device<mac:" + schedulerID + ", did:" + mac + ", productKey:" + did + "> is not in devicelist, skip");
                                } else {
                                    GizDeviceJointActionRuleCondition conditionExist = null;
                                    var16 = this.conditionList.iterator();

                                    while(var16.hasNext()) {
                                        GizDeviceJointActionRuleCondition condition = (GizDeviceJointActionRuleCondition)var16.next();
                                        if (condition.getDevice().isEqualToJsonObj(deviceJson)) {
                                            conditionExist = condition;
                                            break;
                                        }
                                    }

                                    maps = conditionDevice.parseDeviceStatusForKey(arrayItem, "attrStatus");
                                    maps_v4a2 = (ConcurrentHashMap)maps.get(1);
                                    if (maps_v4a2 != null && maps_v4a2.size() != 0) {
                                        if (maps_v4a2.containsKey("data")) {
                                            maps_v4a2 = (ConcurrentHashMap)maps_v4a2.get("data");
                                        }

                                        if (conditionExist != null) {
                                            if (conditionExist.getConditionOperator() != GizConditionOperator.valueOf(conditionOperator)) {
                                                update = true;
                                            }

                                            conditionExist.setConditionOperator(GizConditionOperator.valueOf(conditionOperator));
                                            conditionExist.setData(maps_v4a2);
                                            conditionExist.setIsValid(true);
                                        } else {
                                            GizDeviceJointActionRuleCondition condition = new GizDeviceJointActionRuleCondition(conditionDevice, maps_v4a2, GizConditionOperator.valueOf(conditionOperator));
                                            this.conditionList.add(condition);
                                            update = true;
                                        }
                                    } else {
                                        SDKLog.d("can not get attrStatus parse, skip");
                                    }
                                }
                            } else {
                                SDKLog.d("no device in json, skip");
                            }
                        } else {
                            SDKLog.d("conditionOperator in json is invalid, skip");
                        }
                    }
                }

                if (resultEventJsonArray != null && resultEventJsonArray.length() > 0) {
                    var7 = this.resultEventList.iterator();

                    while(var7.hasNext()) {
                        GizDeviceJointActionRuleResultEvent resultEvent = (GizDeviceJointActionRuleResultEvent)var7.next();
                        resultEvent.setIsValid(false);
                    }

                    for(i = 0; i < resultEventJsonArray.length(); ++i) {
                        arrayItem = (JSONObject)resultEventJsonArray.get(i);
                        conditionOperator = arrayItem.has("resultEventType") ? arrayItem.getInt("resultEventType") : -1;
                        if (conditionOperator != -1 && conditionOperator <= GizJointActionRulerEventType.GizJointActionRulerEventScheduler.ordinal() && conditionOperator >= GizJointActionRulerEventType.GizJointActionRulerEventDevice.ordinal()) {
                            GizDeviceJointActionRuleResultEvent resultEventExist = null;
                            if (conditionOperator == GizJointActionRulerEventType.GizJointActionRulerEventDevice.ordinal()) {
                                JSONObject deviceJson = arrayItem.has("device") ? arrayItem.getJSONObject("device") : null;
                                if (deviceJson != null && deviceJson.length() != 0) {
                                    mac = deviceJson.has("mac") ? deviceJson.getString("mac") : "";
                                    did = deviceJson.has("did") ? deviceJson.getString("did") : "";
                                    String productKey = deviceJson.has("productKey") ? deviceJson.getString("productKey") : "";
                                    GizWifiDevice resultEventDevice = SDKEventManager.getInstance().findDeviceInTotalDeviceList(did, mac, productKey);
                                    if (resultEventDevice == null) {
                                        SDKLog.d("result event json device<mac:" + mac + ", did:" + did + ", productKey:" + productKey + "> is not in devicelist, skip");
                                    } else {
                                        var16 = this.resultEventList.iterator();

                                        while(var16.hasNext()) {
                                            GizDeviceJointActionRuleResultEvent resultEvent = (GizDeviceJointActionRuleResultEvent)var16.next();
                                            if (resultEvent.getDevice() != null && resultEvent.getDevice().getMacAddress().equals(resultEventDevice.getMacAddress()) && resultEvent.getDevice().getDid().equals(resultEventDevice.getDid()) && resultEvent.getDevice().getProductKey().equals(resultEventDevice.getProductKey())) {
                                                resultEventExist = resultEvent;
                                                break;
                                            }
                                        }

                                        maps = resultEventDevice.parseDeviceStatusForKey(arrayItem, "attrStatus");
                                        maps_v4a2 = maps != null ? (ConcurrentHashMap)maps.get(1) : null;
                                        if (maps_v4a2 != null && maps_v4a2.containsKey("data")) {
                                            maps_v4a2 = (ConcurrentHashMap)maps_v4a2.get("data");
                                        }

                                        if (resultEventExist != null) {
                                            if (maps_v4a2 != null && maps_v4a2.size() > 0) {
                                                resultEventExist.setData(maps_v4a2);
                                            }

                                            resultEventExist.setIsValid(true);
                                        } else {
                                            resultEventExist = new GizDeviceJointActionRuleResultEvent(resultEventDevice, maps_v4a2);
                                            this.resultEventList.add(resultEventExist);
                                            update = true;
                                        }
                                    }
                                } else {
                                    SDKLog.d("no device in result event json, skip");
                                }
                            } else if (conditionOperator == GizJointActionRulerEventType.GizJointActionRulerEventGroup.ordinal()) {
                                schedulerID = arrayItem.has("groupID") ? arrayItem.getString("groupID") : "";
                                GizDeviceGroup resultEventGroup = GizDeviceGroupCenter.getGroupByOwner(deviceOwner, schedulerID);
                                if (resultEventGroup == null) {
                                    SDKLog.d("result event json group: " + schedulerID + " is not in grouplist, skip");
                                } else {
                                    GizWifiDevice deviceByProductKey = SDKEventManager.getInstance().findDeviceByProductKeyInTotalDeviceList(resultEventGroup.getGroupType());
                                    if (deviceByProductKey == null) {
                                        SDKLog.d("result event json group productKey: " + deviceByProductKey + " is invalid, skip");
                                    } else {
                                        Iterator var32 = this.resultEventList.iterator();

                                        while(var32.hasNext()) {
                                            GizDeviceJointActionRuleResultEvent resultEvent = (GizDeviceJointActionRuleResultEvent)var32.next();
                                            if (resultEvent.getGroup() != null && resultEvent.getGroup().getGroupID().equals(schedulerID)) {
                                                resultEventExist = resultEvent;
                                                break;
                                            }
                                        }

                                         maps = deviceByProductKey.parseDeviceStatusForKey(arrayItem, "attrStatus");
                                         maps_v4a2 = maps != null ? (ConcurrentHashMap)maps.get(1) : null;
                                        if (maps_v4a2 != null && maps_v4a2.containsKey("data")) {
                                            maps_v4a2 = (ConcurrentHashMap)maps_v4a2.get("data");
                                        }

                                        if (resultEventExist != null) {
                                            if (maps_v4a2 != null && maps_v4a2.size() > 0) {
                                                resultEventExist.setData(maps_v4a2);
                                            }

                                            resultEventExist.setIsValid(true);
                                        } else {
                                            resultEventExist = new GizDeviceJointActionRuleResultEvent(resultEventGroup, maps_v4a2);
                                            this.resultEventList.add(resultEventExist);
                                            update = true;
                                        }
                                    }
                                }
                            } else {
                                Iterator var28;
                                boolean enabled;
                                GizDeviceJointActionRuleResultEvent resultEvent;
                                if (conditionOperator == GizJointActionRulerEventType.GizJointActionRulerEventScene.ordinal()) {
                                    schedulerID = arrayItem.has("sceneID") ? arrayItem.getString("sceneID") : "";
                                    GizDeviceScene resultEventScene = GizDeviceSceneCenter.getSceneByOwner(deviceOwner, schedulerID);
                                    if (resultEventScene == null) {
                                        SDKLog.d("result event json scene: " + schedulerID + " is not in scenelist, skip");
                                    } else {
                                        var28 = this.resultEventList.iterator();

                                        while(var28.hasNext()) {
                                            resultEvent = (GizDeviceJointActionRuleResultEvent)var28.next();
                                            if (resultEvent.getScene() != null && resultEvent.getScene().getSceneID().equals(schedulerID)) {
                                                resultEventExist = resultEvent;
                                                break;
                                            }
                                        }

                                        enabled = arrayItem.has("enabled") ? arrayItem.getBoolean("enabled") : false;
                                        if (resultEventExist != null) {
                                            if (enabled != resultEventExist.getEnabled()) {
                                                update = true;
                                            }

                                            resultEventExist.setEnabled(enabled);
                                            resultEventExist.setIsValid(true);
                                        } else {
                                            resultEventExist = new GizDeviceJointActionRuleResultEvent(resultEventScene, enabled);
                                            this.resultEventList.add(resultEventExist);
                                            update = true;
                                        }
                                    }
                                } else if (conditionOperator == GizJointActionRulerEventType.GizJointActionRulerEventScheduler.ordinal()) {
                                    schedulerID = arrayItem.has("schedulerID") ? arrayItem.getString("schedulerID") : "";
                                    GizDeviceSchedulerGateway resultEventSchedulerGateway = GizDeviceSchedulerCenter.getSchedulerGatewayByOwner(deviceOwner, schedulerID);
                                    if (resultEventSchedulerGateway == null) {
                                        SDKLog.d("result event json scheduler: " + schedulerID + " is not in schedulerlist, skip");
                                    } else {
                                        var28 = this.resultEventList.iterator();

                                        while(var28.hasNext()) {
                                            resultEvent = (GizDeviceJointActionRuleResultEvent)var28.next();
                                            if (resultEvent.getScheduler() != null && resultEvent.getScheduler().getSchedulerID().equals(schedulerID)) {
                                                resultEventExist = resultEvent;
                                                break;
                                            }
                                        }

                                        enabled = arrayItem.has("enabled") ? arrayItem.getBoolean("enabled") : false;
                                        if (resultEventExist != null) {
                                            if (enabled != resultEventExist.getEnabled()) {
                                                update = true;
                                            }

                                            resultEventExist.setEnabled(enabled);
                                            resultEventExist.setIsValid(true);
                                        } else {
                                            resultEventExist = new GizDeviceJointActionRuleResultEvent(resultEventSchedulerGateway, enabled);
                                            this.resultEventList.add(resultEventExist);
                                            update = true;
                                        }
                                    }
                                }
                            }
                        } else {
                            SDKLog.d("resultEventType in result event json is invalid, skip");
                        }
                    }
                }
            } catch (JSONException var19) {
                var19.printStackTrace();
            }

            return update;
        } else {
            return false;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.conditionList);
        dest.writeTypedList(this.resultEventList);
        dest.writeInt(this.conditionCombType == null ? -1 : this.conditionCombType.ordinal());
    }

    protected GizDeviceJointActionRule(Parcel in) {
        this.conditionList = in.createTypedArrayList(GizDeviceJointActionRuleCondition.CREATOR);
        this.resultEventList = in.createTypedArrayList(GizDeviceJointActionRuleResultEvent.CREATOR);
        int tmpConditionCombType = in.readInt();
        this.conditionCombType = tmpConditionCombType == -1 ? null : GizLogicalOperator.values()[tmpConditionCombType];
    }
}
