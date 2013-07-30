package com.williballenthin.rejistry.valuetype;

/**
 * Used for Registry value types REG_DWORD, REG_QWORD, REG_BIG_ENDIAN.
 */
public class NumberValueType implements ValueType {
    private static long _n;

    public NumberValueType(long n) {
        this._n = n;
    }

    @Override
    public String toString() {
        return String.format("0x%x", this._n);
    }
}
