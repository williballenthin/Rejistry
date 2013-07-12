package com.williballenthin.rejistry;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.GregorianCalendar;

/**
 * NKRecord is the structure that backs a Registry key. It has a name, and may have values and subkeys.
 */
public class NKRecord extends Record {
    private static final int IS_ROOT_OFFSET = 0x02;
    private static final int TIMESTAMP_OFFSET = 0x04;
    private static final int PARENT_RECORD_OFFSET_OFFSET = 0x10;
    private static final int SUBKEY_NUMBER_OFFSET = 0x14;
    private static final int SUBKEY_LIST_OFFSET_OFFSET = 0x1C;
    private static final int VALUES_NUMBER_OFFSET = 0x24;
    private static final int CLASSNAME_OFFSET_OFFSET = 0x30;
    private static final int NAME_LENGTH_OFFSET = 0x48;
    private static final int CLASSNAME_LENGTH_OFFSET = 0x4A;
    private static final int NAME_OFFSET = 0x4C;

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

    /**
     * getName fetches the name of the Registry key represented by this NKRecord. It is not the
     *   full path, but a single path component.
     * @return The name of the associated Registry key.
     * @throws UnsupportedEncodingException if the ASCII name of the NKRecord cannot be decoded.
     */
    public String getName() throws UnsupportedEncodingException {
        int length = this.getDword(NAME_LENGTH_OFFSET);
        return this.getASCIIString(NAME_OFFSET, length);
    }

    /**
     * hasParentRecord returns True if the key has a parent key.
     * @return True if the key has a parent key, False otherwise.
     */
    public boolean hasParentRecord() {
        if ( ! this.isRootKey()) {
            return false;
        }

        try {
            this.getParentRecord();
            return true;
        } catch (RegistryParseException e) {
            return false;
        }
    }

    /**
     * getParentRecord fetches the parent NKRecord of this record.
     * @return the parent NKRecord of this record.
     * @throws RegistryParseException If the NKRecord cannot be parsed from the cell data.
     */
    public NKRecord getParentRecord() throws RegistryParseException {
        int offset = this.getDword(PARENT_RECORD_OFFSET_OFFSET);

        int parent_offset = REGFHeader.FIRST_HBIN_OFFSET + offset;
        Cell c = new Cell(this._buf, parent_offset);
        return c.getNKRecord();
    }

    /**
     * getNumberOfValues fetches the number of values the key has.
     * @return the number of values the key has.
     */
    public int getNumberOfValues() {
        int num = this.getDword(VALUES_NUMBER_OFFSET);
        if (num == 0xFFFFFFFF) {
            return 0;
        } else {
            return num;
        }
    }

    /**
     * getNumberOfSubkeys fetches the number of subkeys the key has.
     * @return the number of subkeys the key has.
     */
    public int getNumberOfSubkeys() {
        int num = this.getDword(SUBKEY_NUMBER_OFFSET);
        if (num == 0xFFFFFFFF) {
            return 0;
        } else {
            return num;
        }
    }

    public SubkeyList getSubkeyList() throws RegistryParseException {
        if (this.getNumberOfSubkeys() == 0) {
            return new EmptySubkeyList();
        }

        int subkeylist_offset = REGFHeader.FIRST_HBIN_OFFSET + SUBKEY_LIST_OFFSET_OFFSET;
        Cell c = new Cell(this._buf, subkeylist_offset);

        String magic;
        try {
            magic = c.getDataSignature();
        } catch (UnsupportedEncodingException e) {
            throw new RegistryParseException("Unexpected subkey list type: binary");
        }
        if (magic.equals("lf")) {
            return c.getLFRecord();
        } else if (magic.equals("lh")) {
            return c.getLHRecord();
        } else if (magic.equals("ri")) {
            return c.getRIRecord();
        } else if (magic.equals("li")) {
            return c.getLIRecord();
        } else {
            throw new RegistryParseException("Unexpected subkey list type: " + magic);
        }
    }
}
