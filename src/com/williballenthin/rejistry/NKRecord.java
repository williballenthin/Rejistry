package com.williballenthin.rejistry;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.GregorianCalendar;

public class NKRecord extends Record {
    private static final int IS_ROOT_OFFSET = 0x02;
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

    /**
     * hasClassname returns True if the key has a classname.
     * @return True if the NKRecord has a classname.
     */
    public boolean hasClassname() {
        return this.getDword(CLASSNAME_OFFSET_OFFSET) != 0xFFFFFFFF;
    }

    /**
     * getClassname fetches the classname of the NKRecord, if it exists, or returns the
     *   empty string if it does not.
     * @return The classname, if it exists, or the empty string.
     * @throws UnsupportedEncodingException if the UTF-16LE classname cannot be decoded.
     * @throws RegistryParseException if the size of the Classname cell has an insufficient size.
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

    /**
     * getTimestamp fetches the modification timestamp of the NKRecord.
     *   Note, this timestamp is mutable, so don't change it, or I'll start returning copies.
     * @return The modification timestamp of this key, in UTC, with millisecond precision.
     */
    public GregorianCalendar getTimestamp() {
        return this.getWindowsTimestamp(TIMESTAMP_OFFSET);
    }

    /**
     * isRootKey returns True if the key is a root key.
     * @return True if the NKRecord is a root record.
     */
    public boolean isRootKey() {
        return this.getWord(IS_ROOT_OFFSET) == 0x2C;
    }
}
