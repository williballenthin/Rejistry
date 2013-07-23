package com.williballenthin.rejistry.com.williballenthin.rejistry.valuetype;

public class StringValueType implements ValueType {
    private final String _s;

    public StringValueType(String s) {
        super();

        this._s = s;
    }

    @Override
    public String toString() {
        return this._s;
    }
}
