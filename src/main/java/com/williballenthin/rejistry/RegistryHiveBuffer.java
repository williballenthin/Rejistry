package com.williballenthin.rejistry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RegistryHiveBuffer implements RegistryHive {
    private ByteBuffer _buf;

    public RegistryHiveBuffer(ByteBuffer buf) {
        this._buf = buf.asReadOnlyBuffer();
        this._buf.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public RegistryKey getRoot() throws RegistryParseException {
        return new RegistryKey(this.getHeader().getRootNKRecord());
    }

    @Override
    public REGFHeader getHeader() throws RegistryParseException {
        return new REGFHeader(this._buf, 0x0);
    }
}
