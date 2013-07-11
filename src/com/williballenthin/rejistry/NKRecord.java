package com.williballenthin.rejistry;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class NKRecord extends Record {
    private static final int CLASSNAME_OFFSET_OFFSET = 0x30;
    private static final int CLASSNAME_LENGTH_OFFSET = 0x4A;

    /**
     *
     * @param buf
     * @param offset
     * @throws RegistryParseException if the magic header is not the ASCII string "NK".
     */
    public NKRecord(ByteBuffer buf, int offset) throws RegistryParseException {
        super(hbin, buf, offset);

        if (!this.getMagic().equals("NK")) {
            throw new RegistryParseException("NKRecord invalid magic header");
        }
    }

    public boolean hasClassname() {
        return this.getDword(CLASSNAME_OFFSET_OFFSET) != 0xFFFFFFFF;
    }

    @NotNull
    public String getClassname() throws UnsupportedEncodingException, RegistryParseException {
        int offset = this.getDword(CLASSNAME_OFFSET_OFFSET);
        int length = this.getDword(CLASSNAME_LENGTH_OFFSET);

        int classname_offset = REGFHeader.FIRST_HBIN_OFFSET + offset;
        Cell c = new Cell(this._buf, classname_offset);
        ByteBuffer b = c.getData();
        if (length > b.limit()) {
            throw new RegistryParseException("Cell size insufficient for parsing classname");
        }

        return U.parseWString(b, 0, length);
    }
}
