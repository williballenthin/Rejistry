package com.williballenthin.rejistry.valuetype;

import java.nio.ByteBuffer;

/**
 * Used for Registry value types REG_NONE and REG_BIN.
 */
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
