package com.williballenthin.rejistry;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.GregorianCalendar;

public class NKRecord extends Record {
    private static final int TIMESTAMP_OFFSET = 0x04;
    private static final int CLASSNAME_OFFSET_OFFSET = 0x30;
    private static final int CLASSNAME_LENGTH_OFFSET = 0x4A;

    /**
     *
     * @param buf
     * @param offset
     * @throws RegistryParseException if the magic header is not the ASCII string "nk".
     */
    public NKRecord(ByteBuffer buf, int offset) throws RegistryParseException {
        super(buf, offset);

        if (!this.getMagic().equals("nk")) {
            throw new RegistryParseException("NKRecord invalid magic header, expected \"nk\", got: " + this.getMagic());
        }
    }

    public boolean hasClassname() {
        return this.getDword(CLASSNAME_OFFSET_OFFSET) != 0xFFFFFFFF;
    }

    /**
     *
     * @return The classname, if it exists, or the empty string.
     * @throws UnsupportedEncodingException
     * @throws RegistryParseException
     */
    public String getClassname() throws UnsupportedEncodingException, RegistryParseException {
        if ( ! this.hasClassname()) {
            return "";
        }
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

    public GregorianCalendar getTimestamp() {
        return this.getWindowsTimestamp(TIMESTAMP_OFFSET);
    }
}
