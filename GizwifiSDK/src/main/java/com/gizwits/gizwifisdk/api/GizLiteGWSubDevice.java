//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class GizLiteGWSubDevice extends GizWifiDevice {
    private String meshId;

    protected GizLiteGWSubDevice() {
    }

    public String getMeshId() {
        return this.meshId;
    }

    protected void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    protected void syncBtInfoFromJson(JSONObject jsonObject) throws JSONException {
        String meshId = jsonObject.has("meshID") ? jsonObject.getString("meshID") : "";
        this.setMeshId(meshId);
    }
}
