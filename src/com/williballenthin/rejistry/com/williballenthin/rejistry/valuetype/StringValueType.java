package com.williballenthin.rejistry.com.williballenthin.rejistry.valuetype;

/**
 * Used for Registry value types REG_SZ and REG_EXPAND_SZ.
 */
public class StringValueType implements ValueType {
    private final String _s;

    public StringValueType(String s) {
        this._s = s;
    }

    @Override
    public String toString() {
        return this._s;
    }
}
