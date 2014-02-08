package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.RegistryParseException;

import java.nio.ByteBuffer;

public class LIRecord extends DirectSubkeyListRecord {
    public static final String MAGIC = "li";

    /**
     * @throws com.williballenthin.rejistry.RegistryParseException if the magic header is not the ASCII string "li".
     */
    public LIRecord(ByteBuffer buf, int offset) throws RegistryParseException {
        super(buf, offset, 0x4);

        if (!this.getMagic().equals(LIRecord.MAGIC)) {
            throw new RegistryParseException("LIRecord invalid magic header, expected \"li\", got: " + this.getMagic());
        }
    }
}
