package com.williballenthin.rejistry;

public enum RegistryValueType {
    REG_NONE(0x0),
    REG_SZ(0x1),
    REG_EXPAND_SZ(0x2),
    REG_BIN(0x3),
    REG_DWORD(0x4),
    REG_BIG_ENDIAN(0x5),
    REG_LINK(0x6),
    REG_MULTI_SZ(0x7),
    REG_RESOURCE_LIST(0x8),
    REG_FULL_RESOURCE_DESCRIPTOR(0x9),
    REG_RESOURCE_REQUIREMENTS_LIST(0xA),
    REG_QWORD(0xB);

    private final int _value;
    private RegistryValueType(int value) {
        this._value = value;
    }

    /**
     * getValue fetches the integer value for the type.
     * @return The integer value associated with type type.
     */
    public int getValue() {
        return  this._value;
    }

    @Override
    public String toString() {
        // I don't like how we have to repeat the constants here, but I'm a Java n00b.
        switch(this._value) {
            case 0x0:
                return "REG_NONE";
            case 0x1:
                return "REG_SZ";
            case 0x2:
                return  "REG_EXPAND_SZ";
            case 0x3:
                return "REG_BIN";
            case 0x4:
                return "REG_DWORD";
            case 0x5:
                return "REG_BIG_ENDIAN";
            case 0x6:
                return  "REG_LINK";
            case 0x7:
                return  "REG_MULTI_SZ";
            case 0x8:
                return "REG_RESOURCE_LIST";
            case 0x9:
                return "REG_FULL_RESOURCE_DESCRIPTOR";
            case 0xA:
                return "REG_RESOURCE_REQUIREMENTS_LIST";
            case 0xB:
                return "REG_QWORD";
            default:
                throw new IllegalArgumentException("Unknown RegistryValueType");
        }
    }

    /**
     * valueOf fetches the enum value given an integer.
     * @param v The Registry value type as a integer.
     * @return The Registry value type associated with the given integer.
     */
    public static RegistryValueType valueOf(int v) {
        // I don't like how we have to repeat the constants here, but I'm a Java n00b.
        switch(v) {
            case 0x0:
                return REG_NONE;
            case 0x1:
                return REG_SZ;
            case 0x2:
                return REG_EXPAND_SZ;
            case 0x3:
                return REG_BIN;
            case 0x4:
                return REG_DWORD;
            case 0x5:
                return REG_BIG_ENDIAN;
            case 0x6:
                return REG_LINK;
            case 0x7:
                return REG_MULTI_SZ;
            case 0x8:
                return REG_RESOURCE_LIST;
            case 0x9:
                return REG_FULL_RESOURCE_DESCRIPTOR;
            case 0xA:
                return REG_RESOURCE_REQUIREMENTS_LIST;
            case 0xB:
                return REG_QWORD;
            default:
                throw new IllegalArgumentException("Unknown RegistryValueType");
        }
    }
}
