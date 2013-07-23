package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.RegistryParseException;

import java.nio.ByteBuffer;

public class LHRecord extends DirectSubkeyListRecord {
    public static final String MAGIC = "lh";

    /**
     * @throws com.williballenthin.rejistry.RegistryParseException if the magic header is not the ASCII string "lh".
     */
    public LHRecord(ByteBuffer buf, int offset) throws RegistryParseException {
        super(buf, offset, 0x8);

        if (!this.getMagic().equals(LHRecord.MAGIC)) {
            throw new RegistryParseException("LHRecord invalid magic header, expected \"lh\", got: " + this.getMagic());
        }
    }
}
