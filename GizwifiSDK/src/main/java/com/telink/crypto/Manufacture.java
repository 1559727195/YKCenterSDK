//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.telink.crypto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Manufacture {
    private static final Manufacture DEFAULT_MANUFACTURE = (new Manufacture.Builder()).build();
    private static Manufacture definitionManufacture;
    private final Map<String, UUID> uuidMap;
    private String name;
    private String version;
    private String info;
    private String factoryName;
    private String factoryPassword;
    private byte[] factoryLtk;
    private int vendorId;
    private int otaDelay;
    private int otaSize;

    private Manufacture(String name, String version, String info, String defaultMeshName, String defaultPassword, byte[] defaultLongTermKey, int vendorId, int otaDelay, int otaSize, UUID serviceUUID, UUID pairUUID, UUID commandUUID, UUID notifyUUID, UUID otaUUID) {
        this.uuidMap = new HashMap();
        this.name = name;
        this.version = version;
        this.info = info;
        this.factoryName = defaultMeshName;
        this.factoryPassword = defaultPassword;
        this.factoryLtk = Arrays.copyOf(defaultLongTermKey, 16);
        this.vendorId = vendorId;
        this.otaDelay = otaDelay;
        this.otaSize = otaSize;
        this.putUUID(Manufacture.UUIDType.SERVICE.getKey(), serviceUUID);
        this.putUUID(Manufacture.UUIDType.PAIR.getKey(), pairUUID);
        this.putUUID(Manufacture.UUIDType.COMMAND.getKey(), commandUUID);
        this.putUUID(Manufacture.UUIDType.OTA.getKey(), otaUUID);
        this.putUUID(Manufacture.UUIDType.NOTIFY.getKey(), notifyUUID);
    }

    public static Manufacture getDefaultManufacture() {
        return DEFAULT_MANUFACTURE;
    }

    public static Manufacture getDefinitionManufacture() {
        Class var0 = Manufacture.class;
        synchronized(Manufacture.class) {
            return definitionManufacture;
        }
    }

    public static void setManufacture(Manufacture manufacture) {
        Class var1 = Manufacture.class;
        synchronized(Manufacture.class) {
            definitionManufacture = manufacture;
        }
    }

    public static Manufacture getDefault() {
        Class var0 = Manufacture.class;
        synchronized(Manufacture.class) {
            if (definitionManufacture == null) {
                return DEFAULT_MANUFACTURE;
            }
        }

        return definitionManufacture;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getInfo() {
        return this.info;
    }

    public String getFactoryName() {
        return this.factoryName;
    }

    public String getFactoryPassword() {
        return this.factoryPassword;
    }

    public byte[] getFactoryLtk() {
        return this.factoryLtk;
    }

    public int getVendorId() {
        return this.vendorId;
    }

    public int getOtaDelay() {
        return this.otaDelay;
    }

    public int getOtaSize() {
        return this.otaSize;
    }

    public UUID getUUID(Manufacture.UUIDType uuidType) {
        return this.getUUID(uuidType.getKey());
    }

    public UUID getUUID(String key) {
        UUID result = null;
        synchronized(this.uuidMap) {
            if (this.uuidMap.containsKey(key)) {
                result = (UUID)this.uuidMap.get(key);
            }

            return result;
        }
    }

    public void putUUID(String key, UUID value) {
        synchronized(this.uuidMap) {
            if (!this.uuidMap.containsKey(key)) {
                this.uuidMap.put(key, value);
            }

        }
    }

    public static final class Builder {
        private String name = "telink";
        private String version = "1.0";
        private String info = "TELINK SEMICONDUCTOR (Shanghai) CO, LTD is a fabless IC design company";
        private String factoryName = "telink_mesh1";
        private String factoryPassword = "123";
        private byte[] factoryLtk = new byte[]{-64, -63, -62, -61, -60, -59, -58, -57, -40, -39, -38, -37, -36, -35, -34, -33};
        private int vendorId = 529;
        private UUID serviceUUID;
        private UUID pairUUID;
        private UUID commandUUID;
        private UUID notifyUUID;
        private UUID otaUUID;
        private int otaDelay;
        private int otaSize;

        public Builder() {
            this.serviceUUID = UuidInformation.TELINK_SERVICE.getValue();
            this.pairUUID = UuidInformation.TELINK_CHARACTERISTIC_PAIR.getValue();
            this.commandUUID = UuidInformation.TELINK_CHARACTERISTIC_COMMAND.getValue();
            this.notifyUUID = UuidInformation.TELINK_CHARACTERISTIC_NOTIFY.getValue();
            this.otaUUID = UuidInformation.TELINK_CHARACTERISTIC_OTA.getValue();
            this.otaDelay = 0;
            this.otaSize = 128;
        }

        public Manufacture.Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Manufacture.Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Manufacture.Builder setInfo(String info) {
            this.info = info;
            return this;
        }

        public Manufacture.Builder setFactoryName(String factoryName) {
            this.factoryName = factoryName;
            return this;
        }

        public Manufacture.Builder setFactoryPassword(String factoryPassword) {
            this.factoryPassword = factoryPassword;
            return this;
        }

        public Manufacture.Builder setFactoryLtk(byte[] factoryLtk) {
            this.factoryLtk = factoryLtk;
            return this;
        }

        public Manufacture.Builder setVendorId(int vendorId) {
            this.vendorId = vendorId;
            return this;
        }

        public Manufacture.Builder setOtaDelay(int otaDelay) {
            this.otaDelay = otaDelay;
            return this;
        }

        public Manufacture.Builder setOtaSize(int otaSize) {
            this.otaSize = otaSize;
            return this;
        }

        public Manufacture.Builder setServiceUUID(UUID serviceUUID) {
            this.serviceUUID = serviceUUID;
            return this;
        }

        public Manufacture.Builder setPairUUID(UUID pairUUID) {
            this.pairUUID = pairUUID;
            return this;
        }

        public Manufacture.Builder setCommandUUID(UUID commandUUID) {
            this.commandUUID = commandUUID;
            return this;
        }

        public Manufacture.Builder setNotifyUUID(UUID notifyUUID) {
            this.notifyUUID = notifyUUID;
            return this;
        }

        public Manufacture.Builder setOtaUUID(UUID otaUUID) {
            this.otaUUID = otaUUID;
            return this;
        }

        public Manufacture build() {
            return new Manufacture(this.name, this.version, this.info, this.factoryName, this.factoryPassword, this.factoryLtk, this.vendorId, this.otaDelay, this.otaSize, this.serviceUUID, this.pairUUID, this.commandUUID, this.notifyUUID, this.otaUUID);
        }
    }

    public static enum UUIDType {
        SERVICE("SERVICE_UUID"),
        PAIR("PAIR_UUID"),
        COMMAND("COMMAND_UUID"),
        OTA("OTA_UUID"),
        NOTIFY("NOTIFY_UUID");

        private final String key;

        private UUIDType(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}
