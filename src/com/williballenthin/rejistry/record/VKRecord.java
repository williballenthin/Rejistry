package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.*;

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
    private static final long LARGE_DATA_SIZE = 0x80000000L;

    /**
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
     *   This is the actual length of the data that should be parsed for the value.
     * @return The length of the value data.
     */
    public long getDataLength() {
        long size = this.getDword(DATA_LENGTH_OFFSET);
        if (size > LARGE_DATA_SIZE) {
            size -= LARGE_DATA_SIZE;
        }
        return size;
    }

    /**
     * getRawDataLength fetches the literal value that describes the value data length.
     *   Some interpretation may be required to make this value reasonable.
     * @return The literal value that describes the value data length.
     */
    public long getRawDataLength() {
        return this.getDword(DATA_LENGTH_OFFSET);
    }

    /**
     * getDataOffset fetches the *absolute* offset to the value data.
     * @return The absolute offset to the value data.
     */
    public long getDataOffset() {
        if (this.getRawDataLength() < SMALL_DATA_SIZE || this.getRawDataLength() >= LARGE_DATA_SIZE) {
            return this._offset + DATA_OFFSET_OFFSET;
        } else {
            return REGFHeader.FIRST_HBIN_OFFSET + this.getDword(DATA_OFFSET_OFFSET);
        }
    }

    /**
     * getValue parses and returns the data associated with this value.
     * @return The data associated with this value.
     * @throws RegistryParseException
     */
    public ValueData getValue() throws RegistryParseException, UnsupportedEncodingException {
        RegistryValueType t = this.getValueType();
        long length = this.getRawDataLength();
        int offset = (int)this.getDataOffset();

        if (length > LARGE_DATA_SIZE + DB_DATA_SIZE) {
            throw new RegistryParseException("Value size too large: " + length);
        }

        switch(t) {
            case REG_BIN: // intentional fallthrough
            case REG_NONE: // intentional fallthrough
            case REG_SZ: // intentional fallthrough
            case REG_EXPAND_SZ: // intentional fallthrough
            case REG_MULTI_SZ: // intentional fallthrough
            case REG_LINK:  // intentional fallthrough
            case REG_RESOURCE_LIST:  // intentional fallthrough
            case REG_FULL_RESOURCE_DESCRIPTOR:  // intentional fallthrough
            case REG_RESOURCE_REQUIREMENTS_LIST: {
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
                data.position(0x0);
                return new ValueData(data, t);
            }

            case REG_DWORD:
            case REG_BIG_ENDIAN: {
                ByteBuffer data;
                int bufSize = 0x4;
                data = ByteBuffer.allocate(bufSize);
                data.position(0x0);
                data.limit(bufSize);
                for (int i = 0; i < bufSize; i++) {
                    data.put(this.getByte(DATA_OFFSET_OFFSET + i));
                }
                data.position(0x0);
                return new ValueData(data, t);
            }

            case REG_QWORD: {
                ByteBuffer data;
                Cell c = new Cell(this._buf, offset);
                data = c.getData();
                data.limit((int)length);
                data.position(0x0);
                return new ValueData(data, t);
            }

            default: {
                throw new NotImplementedException();
            }
        }
    }


}
