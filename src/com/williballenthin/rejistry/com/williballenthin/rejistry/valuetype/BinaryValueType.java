package com.williballenthin.rejistry.com.williballenthin.rejistry.valuetype;

import java.nio.ByteBuffer;

public class BinaryValueType implements ValueType {
    private ByteBuffer _b;

    public BinaryValueType(ByteBuffer b) {
        this._b = b;
    }

    @Override
    public String toString() {
        return HexDump.dumpHexString(this._b);
    }
}
