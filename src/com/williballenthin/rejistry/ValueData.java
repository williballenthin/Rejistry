package com.williballenthin.rejistry;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

public class ValueData {

    private final RegistryValueType _type;
    private final ByteBuffer _buf;

    public ValueData(ByteBuffer buf, RegistryValueType type) {
        this._buf = buf;
        this._type = type;
    }


    /**
     * getValueType fetches the type of the Registry value stored by this ValueData.
     * @return The type of the value.
     * @throws RegistryParseException if the value type is not recognized as valid.
     */
    public RegistryValueType getValueType() throws RegistryParseException {
        return this._type;
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
     * TODO(wb): don't like the duplication here.
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
            if (eos == -1) {
                break;
            }

            String s = stringBuffer.substring(index, eos);
            ret.add(s);
            index += s.length() + 1;
        }

        return ret;
    }

    /**
     * parseDword parses a 32bit number from the specified relative offset with the range 0-2**32 - 1;
     *   The integer is decoded as a little endian value.
     *
     *   TODO(wb): stop duplicating this everywhere.
     *
     * @param offset The absolute offset from which to parse the number.
     * @return A non-negative 32bit number with the range 0-2**32-1.
     */
    private static long parseDword(ByteBuffer buf, int offset) {
        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.getInt(offset) & 0xFFFFFFFFL;
    }

    /**
     * parseDwordBE parses a 32bit number from the specified relative offset with the range 0-2**32 - 1;
     *   The integer is decoded as a big endian value.
     *   This is not thread safe.
     *
     *   TODO(wb): stop duplicating this everywhere.
     *
     * @param offset The absolute offset from which to parse the number.
     * @return A non-negative 32bit number with the range 0-2**32-1.
     */
    private static long parseDwordBE(ByteBuffer buf, int offset) {
        buf.order(ByteOrder.BIG_ENDIAN);
        return buf.getInt(offset) & 0xFFFFFFFFL;
    }

    /**
     * parseQword parses a 64bit number from the specified relative offset with the range 0-2**64 - 1;
     *   This method help self-document code. It is equivalent to the instance._buf.getLong(instance._offset + offset),
     *   but reads a lot better.
     *
     *   TODO(wb): stop duplicating this everywhere.*
     *
     * @param offset The relative offset from which to parse the number.
     * @return A non-negative 64bit number with the range 0-2**64-1;
     */
    protected long parseQword(ByteBuffer buf, int offset) {
        buf.order(ByteOrder.LITTLE_ENDIAN);
        //noinspection PointlessBitwiseExpression
        return buf.getLong(offset) & 0xFFFFFFFFFFFFFFFFL;
    }

    /**
     * getAsString returns the data from this value as a String, if the underlying Registry datatype is compatible.
     * @throws UnsupportedEncodingException if the string values cannot be parsed.
     * @throws IllegalArgumentException if the data type is not one of REG_SZ or REG_EXPAND_SZ.
     */
    public String getAsString() throws UnsupportedEncodingException {
        switch(this._type) {
            case REG_SZ:  // intentional fallthrough
            case REG_EXPAND_SZ:
                return parseWString(this._buf, 0x0, this._buf.limit());
            default:
                throw new IllegalArgumentException("Cannot parse String from " + this._type.toString());
        }
    }

    /**
     * getAsStringList returns the data from this value as a list of Strings, if the
     *   underlying Registry datatype is compatible.
     * Data that can be parsed as a String is returned as a list with one entry.
     * @throws UnsupportedEncodingException if the string values cannot be parsed.
     * @throws IllegalArgumentException if the data type is not one of REG_SZ, REG_EXPAND_SZ, or REG_MULTI_SZ.
     */
    public List<String> getAsStringList() throws UnsupportedEncodingException {
        switch(this._type) {
            case REG_SZ:  // intentional fallthrough
            case REG_EXPAND_SZ: {
                List<String> ret = new LinkedList<String>();
                ret.add(parseWString(this._buf, 0x0, this._buf.limit()));
                return ret;
            }
            case REG_MULTI_SZ:
                return parseWStringArray(this._buf, 0x0, this._buf.limit());
            default:
                throw new IllegalArgumentException("Cannot parse String list from " + this._type.toString());
        }
    }

    /**
     * getAsRawBinary returns the raw binary data from this value.
     *   It can be used with all datatypes.
     */
    public ByteBuffer getAsRawData() {
        return this._buf.slice();
    }

    /**
     * getAsNumber returns the data from this value as a Number, if the underlying Registry datatype is compatible.
     * Data that can be parsed as a String is returned as a list with one entry.
     * @throws IllegalArgumentException if the data type is not one of REG_DWORD, REG_QWORD, or REG_BIG_ENDIAN.
     */
    public long getAsNumber() {
        switch(this._type) {
            case REG_DWORD:
                return parseDword(this._buf, 0x0);
            case REG_QWORD:
                return parseQword(this._buf, 0x0);
            case REG_BIG_ENDIAN:
                return parseDwordBE(this._buf, 0x0);
            default:
                throw new IllegalArgumentException("Cannot parse Number from " + this._type.toString());
        }
    }
}


