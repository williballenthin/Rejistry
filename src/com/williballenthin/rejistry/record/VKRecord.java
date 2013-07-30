package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.*;
import com.williballenthin.rejistry.valuetype.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

public class VKRecord extends Record {
    public static final String MAGIC = "vk";

    private static final int NAME_LENGTH_OFFSET = 0x2;
    private static final int DATA_LENGTH_OFFSET = 0x4;
    private static final int DATA_OFFSET_OFFSET = 0x8;
    private static final int VALUE_TYPE_OFFSET = 0xC;
    private static final int NAME_FLAGS_OFFSET = 0x10;
    private static final int NAME_OFFSET_OFFSET = 0x14;

    private static final int SMALL_DATA_SIZE = 0x5;
    private static final int DB_DATA_SIZE = 0x3FD8;
    private static final long LARGE_DATA_SIZE = 0x80000000L;

    /**
     *
     * @param buf
     * @param offset
     * @throws com.williballenthin.rejistry.RegistryParseException if the magic header is not the ASCII string "vk".
     */
    public VKRecord(ByteBuffer buf, int offset) throws RegistryParseException {
        super(buf, offset);

        if (!this.getMagic().equals(VKRecord.MAGIC)) {
            throw new RegistryParseException("VKRecord invalid magic header, expected \"vk\", got: " + this.getMagic());
        }
    }

    /**
     * hasName returns False if the value has the default name.
     * @return True if the value has an explicit name, or False if the value has the default name.
     */
    public boolean hasName() {
        return this.getWord(NAME_LENGTH_OFFSET) != 0x0;
    }

    /**
     * hasAsciiName returns True if the value name is in the ASCII character set. Otherwise, the
     *   value name should be UTF-16LE (according to Morgan).
     * @return True if the value name is stored as ASCII, False for UTF-16LE.
     */
    public boolean hasAsciiName() {
        return (this.getWord(NAME_FLAGS_OFFSET) & 0x1) == 0x1;
    }

    /**
     * getName fetches the name of the Registry value stored by this VKRecord.
     * @return The name of the value.
     * @throws UnsupportedEncodingException if the name cannot be parsed.
     */
    public String getName() throws UnsupportedEncodingException {
        if ( ! this.hasName()) {
            return  "";
        }
        int name_length = this.getWord(NAME_LENGTH_OFFSET);
        if (this.hasAsciiName()) {
            return this.getASCIIString(NAME_OFFSET_OFFSET, name_length);
        } else {
            return this.getWString(NAME_OFFSET_OFFSET, name_length);
        }
    }

    /**
     * getValueType fetches the type of the Registry value stored by this VK record.
     * @return The type of the value.
     * @throws RegistryParseException if the value type is not recognized as valid.
     */
    public RegistryValueType getValueType() throws RegistryParseException {
        try {
            return RegistryValueType.valueOf((int)this.getDword(VALUE_TYPE_OFFSET));
        } catch (InvalidParameterException e) {
            throw new RegistryParseException("Unexpected Registry value type: " + this.getDword(VALUE_TYPE_OFFSET));
        }
    }

    /**
     * getDataLength fetches the length of the value data.
     * @return The length of the value data.
     */
    public long getDataLength() {
        return this.getDword(DATA_LENGTH_OFFSET);
    }

    /**
     * getDataOffset fetches the *absolute* offset to the value data.
     * @return The absolute offset to the value data.
     */
    public long getDataOffset() {
        if (this.getDataLength() < SMALL_DATA_SIZE || this.getDataLength() >= LARGE_DATA_SIZE) {
            return this._offset + DATA_OFFSET_OFFSET;
        } else {
            return REGFHeader.FIRST_HBIN_OFFSET + this.getDword(DATA_OFFSET_OFFSET);
        }
    }

    /**
     * getValue parses and returns the data associated with this value.
     * @return The data associated with this value.
     * @throws RegistryParseException
     * @throws UnsupportedEncodingException if the value type is a string type, and the string
     *   data cannot be decoded.
     * @throws NotImplementedException if the value type is one of: REG_LINK, REG_RESOURCE_LIST,
     *   REG_FULL_RESOURCE_DESCRIPTOR, or REG_RESOURCE_REQUIREMENTS_LIST.
     */
    public ValueType getValue() throws RegistryParseException, UnsupportedEncodingException {
        RegistryValueType t = this.getValueType();
        long length = this.getDataLength();
        int offset = (int)this.getDataOffset();

        if (length > LARGE_DATA_SIZE + DB_DATA_SIZE) {
            throw new RegistryParseException("Value size too large: " + length);
        }

        switch(t) {
            case REG_BIN:  // intentional fallthrough
            case REG_NONE: {
                ByteBuffer data;
                if (length >= LARGE_DATA_SIZE) {
                    int bufSize = (int)(length - LARGE_DATA_SIZE);
                    data = ByteBuffer.allocate(bufSize);
                    data.position(0x0);
                    data.limit(bufSize);
                    for (int i = 0; i < bufSize; i++) {
                        data.put(this.getByte(DATA_OFFSET_OFFSET + i));
                    }
                } else if (DB_DATA_SIZE < length && length < LARGE_DATA_SIZE) {
                    Cell c = new Cell(this._buf, offset);
                    try {
                        DBRecord db = c.getDBRecord();
                        data = db.getData((int)length);
                    } catch (RegistryParseException e) {
                        data = c.getData();
                        data.limit((int)length);
                    }
                } else {
                    Cell c = new Cell(this._buf, offset);
                    data = c.getData();
                    data.limit((int)length);
                }
                return new BinaryValueType(data);
            }

            case REG_SZ:  // intentional fallthrough
            case REG_EXPAND_SZ:
                if (length >= LARGE_DATA_SIZE) {
                    return new StringValueType(this.parseWString(this._buf, offset, (int)(length - LARGE_DATA_SIZE)));
                } else if (DB_DATA_SIZE < length && length < LARGE_DATA_SIZE) {
                    Cell c = new Cell(this._buf, offset);
                    ByteBuffer data;
                    try {
                        DBRecord db = c.getDBRecord();
                        data = db.getData((int)length);
                    } catch (RegistryParseException e) {
                        data = c.getData();
                        data.limit((int)length);
                    }
                    return new StringValueType(VKRecord.parseWString(data, 0x0, (int)length));
                } else {
                    Cell c = new Cell(this._buf, offset);
                    ByteBuffer buf = c.getData();
                    buf.limit((int)length);
                    return new StringValueType(VKRecord.parseWString(buf, 0x0, (int)length));
                }

            case REG_DWORD:
                return new NumberValueType(this.getDword(DATA_OFFSET_OFFSET));

            case REG_QWORD: {
                Cell c = new Cell(this._buf, offset);
                return new NumberValueType(c.getDataQword());
            }

            case REG_MULTI_SZ:
                if (length >= LARGE_DATA_SIZE) {
                    return new MultiStringValueType();
                } else if (DB_DATA_SIZE < length && length < LARGE_DATA_SIZE) {
                    Cell c = new Cell(this._buf, offset);
                    ByteBuffer data;
                    try {
                        DBRecord db = c.getDBRecord();
                        data = db.getData((int)length);
                    } catch (RegistryParseException e) {
                        data = c.getData();
                        data.limit((int)length);
                    }
                    return new MultiStringValueType(VKRecord.parseWStringArray(data, 0x0, (int)length));
                } else {
                    Cell c = new Cell(this._buf, offset);
                    ByteBuffer buf = c.getData();
                    buf.limit((int)length);
                    return new MultiStringValueType(VKRecord.parseWStringArray(buf, 0x0, (int)length));
                }

            case REG_BIG_ENDIAN: {
                return new NumberValueType(this.getDwordBE(DATA_OFFSET_OFFSET));
            }

            case REG_LINK:  // intentional fallthrough
            case REG_RESOURCE_LIST:  // intentional fallthrough
            case REG_FULL_RESOURCE_DESCRIPTOR:  // intentional fallthrough
            case REG_RESOURCE_REQUIREMENTS_LIST:  // intentional fallthrough
            default: {
                if (length < 0x5 || length > LARGE_DATA_SIZE) {
                    return new NumberValueType(this.getDword(DATA_OFFSET_OFFSET));
                } else {
                    throw new RegistryParseException("Unsupported Registry value type: " + t.toString());
                }
            }
        }
    }

    /**
     * parseWString fetches `length` bytes from `buf` at relative offset `offset`
     * and interprets them as a UTF-16LE string.
     * This will return only the characters found before any NULL characters
     * (not NULL bytes).
     *
     * @param offset The relative offset into the buffer from which to read.
     * @param length The number of bytes to read.
     * @return A string decoded from UTF-16LE bytes.
     * @throws UnsupportedEncodingException if the bytes cannot be decoded as an UTF-16LE string.
     * TODO(wb): don't like the dubplication here.
     */
    public static String parseWString(ByteBuffer buf, int offset, int length) throws UnsupportedEncodingException {
        int saved_position = buf.position();
        byte[] sb = new byte[length];

        buf.position(offset);
        buf.get(sb, 0, length);
        buf.position(saved_position);

        String s = new String(sb, "UTF-16LE");

        int eos = s.indexOf(0x0);
        if (eos != -1) {
            s = s.substring(0, eos);
        }
        return s;
    }

    /**
     * parseWStringArray fetches `length` bytes from `buf` at relative offset `offset`
     * and interprets them as a list of UTF-16LE strings separated by NULLs.
     *
     * @param offset The relative offset into the buffer from which to read.
     * @param length The number of bytes to read.
     * @return A list of strings decoded from UTF-16LE bytes separated by NULL characters.
     * @throws UnsupportedEncodingException if the bytes cannot be decoded as UTF-16LE strings.
     */
    public static List<String> parseWStringArray(ByteBuffer buf, int offset, int length) throws UnsupportedEncodingException {
        int saved_position = buf.position();
        byte[] sb = new byte[length];

        buf.position(offset);
        buf.get(sb, 0, length);
        buf.position(saved_position);

        String stringBuffer = new String(sb, "UTF-16LE");

        List<String> ret = new LinkedList<String>();
        int index = 0;

        while (index < stringBuffer.length()) {
            int eos = stringBuffer.indexOf(0x0, index);
            if (eos != -1) {
                String s = stringBuffer.substring(index, eos);
                ret.add(s);
                index += s.length() + 1;
            } else {
                ret.add(stringBuffer.substring(index));
                break;
            }
        }

        return ret;
    }
}
