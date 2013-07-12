package com.williballenthin.rejistry;

import java.nio.ByteBuffer;

public class LFRecord extends DirectSubkeyListRecord implements SubkeyList {
    /**
     *
     * @param buf
     * @param offset
     * @throws RegistryParseException if the magic header is not the ASCII string "nk".
     */
    public LFRecord(ByteBuffer buf, int offset) throws RegistryParseException {
        super(buf, offset, 0x8);

        if (!this.getMagic().equals("lf")) {
            throw new RegistryParseException("LFRecord invalid magic header, expected \"lf\", got: " + this.getMagic());
        }
    }
}
