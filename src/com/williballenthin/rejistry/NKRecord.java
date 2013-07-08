package com.williballenthin.rejistry;

import java.nio.ByteBuffer;

public class NKRecord extends Record {
    private static final int CLASSNAME_OFFSET_OFFSET = 0x30;
    private static final int CLASSNAME_LENGTH_OFFSET = 0x4A;

    /**
     * @param hbin
     * @param buf
     * @param offset
     * @throws RegistryParseException if the magic header is not the ASCII string "NK".
     */
    public NKRecord(HBIN hbin, ByteBuffer buf, int offset) throws RegistryParseException {
        super(hbin, buf, offset);

        if (this.getMagic() != "NK") {
            throw new RegistryParseException("NKRecord invalid magic header");
        }
    }

    public boolean hasClassname() {
        return this.getDword(CLASSNAME_OFFSET_OFFSET) != 0xFFFFFFFF;
    }

    public String getClassname() {
        int offset = this.getDword(CLASSNAME_OFFSET_OFFSET);
        int length = this.getDword(CLASSNAME_LENGTH_OFFSET);
        return "TODO!!!"; // TODO(wb): do this!
    }
}
