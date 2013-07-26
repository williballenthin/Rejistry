package com.williballenthin.rejistry.com.williballenthin.rejistry.valuetype;

public class NumberValueType implements ValueType {
    private static long _n;

    public NumberValueType(long n) {
        this._n = n;
    }

    @Override
    public String toString() {
        return String.format("%x", this._n);
    }
}
