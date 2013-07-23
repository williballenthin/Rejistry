package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.*;
import com.williballenthin.rejistry.com.williballenthin.rejistry.valuetype.StringValueType;
import com.williballenthin.rejistry.com.williballenthin.rejistry.valuetype.ValueType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;

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
    private static final int LARGE_DATA_SIZE = 0x8000000;

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
            return RegistryValueType.valueOf(this.getDword(VALUE_TYPE_OFFSET));
        } catch (InvalidParameterException e) {
            throw new RegistryParseException("Unexpected Registry value type: " + this.getDword(VALUE_TYPE_OFFSET));
        }
    }

    /**
     * getDataLength fetches the length of the value data.
     * @return The length of the value data.
     */
    public int getDataLength() {
        return this.getDword(DATA_LENGTH_OFFSET);
    }

    /**
     * getDataOffset fetches the *absolute* offset to the value data.
     * @return The absolute offset to the value data.
     */
    public int getDataOffset() {
        if (this.getDataLength() < SMALL_DATA_SIZE || this.getDataLength() >= LARGE_DATA_SIZE) {
            return this._offset + DATA_OFFSET_OFFSET;
        } else {
            return REGFHeader.FIRST_HBIN_OFFSET + this.getDword(DATA_OFFSET_OFFSET);
        }
    }

    public ValueType getValue() throws RegistryParseException, UnsupportedEncodingException, NotImplementedException {
        RegistryValueType t = this.getValueType();
        int length = this.getDataLength();
        int offset = this.getDataOffset();
        switch(t) {
            case REG_BIN:  // intentional fallthrough
            case REG_NONE:
                if (length > LARGE_DATA_SIZE) {
                    return new StringValueType(this.getASCIIString(offset, 0x4));
                } else if (DB_DATA_SIZE < length && length < LARGE_DATA_SIZE) {
                    // TODO(wb); parse DB record
                } else {
                    Cell c = new Cell(this._buf, offset);
                    ByteBuffer buf = c.getData();
                    // TODO(wb): try not to use U.*
                    return new StringValueType(U.parseASCIIString(buf, 0x0, length));
                }
                throw new NotImplementedException();

            case REG_SZ:  // intentional fallthrough
            case REG_EXPAND_SZ:
                throw new NotImplementedException();

            case REG_DWORD:
                throw new NotImplementedException();

            case REG_QWORD:
                throw new NotImplementedException();

            case REG_MULTI_SZ:
                throw new NotImplementedException();

            case REG_BIG_ENDIAN:  // intentional fallthrough
            case REG_LINK:  // intentional fallthrough
            case REG_RESOURCE_LIST:  // intentional fallthrough
            case REG_FULL_RESOURCE_DESCRIPTOR:  // intentional fallthrough
            case REG_RESOURCE_REQUIREMENTS_LIST:  // intentional fallthrough
            default:
                throw new RegistryParseException("Unsupported Registry value type: " + t.toString());
        }
    }
}
