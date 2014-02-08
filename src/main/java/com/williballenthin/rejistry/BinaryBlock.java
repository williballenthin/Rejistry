package com.williballenthin.rejistry;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * BinaryBlock is a convenient parsing structure. Its a bookmark into a view of bytes with
 *   methods for accessing fields at relative offsets.
 */
public class BinaryBlock {
    ///< The backing bytes for this structure. This ByteBuffer may be shared across many instances.
    protected final ByteBuffer _buf;
    ///< The absolute offset into the ByteBuffer at which this structure begins.
    protected final int _offset;

    public BinaryBlock(ByteBuffer buf, int offset) {
        this._buf = buf;
        this._offset = offset;

        this._buf.order(ByteOrder.LITTLE_ENDIAN);
        this._buf.position(0x0);
    }

    /**
     * getChar parses a 8bit byte from the specified relative offset.
     *   This method help self-document code. It is equivalent to the instance._buf.get(instance._offset + offset),
     *   but reads a lot better.
     *
     * @param offset The relative offset from which to parse the number.
     * @return The byte read from the specified offset.
     */
    protected byte getByte(int offset) {
        return this._buf.get(this._offset + offset);
    }

    /**
     * getDword parses a 16bit number from the specified relative offset with the range 0-2**16 - 1;
     *   This method help self-document code. It is equivalent to the instance._buf.getShort(instance._offset + offset),
     *   but reads a lot better.
     *
     * @param offset The relative offset from which to parse the number.
     * @return A non-negative 16bit number with the range 0-2**16-1.
     */
    protected int getWord(int offset) {
        //noinspection PointlessBitwiseExpression
        return this._buf.getShort(this._offset + offset) & 0xFFFF;
    }

    /**
     * getDword parses a 32bit number from the specified relative offset with the range 0-2**32 - 1;
     *   The integer is decoded as a little endian value.
     *
     * @param offset The relative offset from which to parse the number.
     * @return A non-negative 32bit number with the range 0-2**32-1.
     */
    protected long getDword(int offset) {
        return this._buf.getInt(this._offset + offset) & 0xFFFFFFFFL;
    }

    /**
     * getDwordBE parses a 32bit number from the specified relative offset with the range 0-2**32 - 1;
     *   The integer is decoded as a big endian value.
     *   This is not thread safe.
     *
     * @param offset The relative offset from which to parse the number.
     * @return A non-negative 32bit number with the range 0-2**32-1.
     */
    protected long getDwordBE(int offset) {
        this._buf.order(ByteOrder.BIG_ENDIAN);
        long d = this._buf.getInt(this._offset + offset) & 0xFFFFFFFFL;
        this._buf.order(ByteOrder.LITTLE_ENDIAN);
        return d;
    }

    /**
     * getQword parses a 64bit number from the specified relative offset with the range 0-2**64 - 1;
     *   This method help self-document code. It is equivalent to the instance._buf.getLong(instance._offset + offset),
     *   but reads a lot better.
     *
     * @param offset The relative offset from which to parse the number.
     * @return A non-negative 64bit number with the range 0-2**64-1;
     */
    protected long getQword(int offset) {
        //noinspection PointlessBitwiseExpression
        return this._buf.getLong(this._offset + offset) & 0xFFFFFFFFFFFFFFFFL;
    }

    /**
     * getWString fetches `length` bytes from `buf` at relative offset `offset`
     * and interprets them as a UTF-16LE string.
     * This will return only the characters found before any NULL characters
     * (not NULL bytes).
     * This is not thread safe.
     *
     * @param offset The relative offset into the buffer from which to read.
     * @param length The number of bytes to read.
     * @return A string decoded from UTF-16LE bytes.
     * @throws UnsupportedEncodingException if the bytes cannot be decoded as an UTF-16LE string.
     */
    protected String getWString(int offset, int length) throws UnsupportedEncodingException {
        int saved_position = this._buf.position();
        byte[] sb = new byte[length];

        this._buf.position(this._offset + offset);
        this._buf.get(sb, 0, length);
        this._buf.position(saved_position);

        String s = new String(sb, "UTF-16LE");

        int eos = s.indexOf(0x0);
        if (eos != -1) {
            s = s.substring(0, eos);
        }

        return s;
    }

    /**
     * getWString fetches `length` bytes from `buf` at relative offset `offset`
     * and interprets them as an ASCII string.
     * This will return only the characters found before any NULL characters
     * (not NULL bytes).
     * This is not thread safe.
     *
     * @param offset The relative offset into the buffer from which to read.
     * @param length The number of bytes to read.
     * @return A string decoded from ASCII bytes.
     * @throws UnsupportedEncodingException if the bytes cannot be decoded as an ASCII string.
     */
    protected String getASCIIString(int offset, int length) throws UnsupportedEncodingException {
        int saved_position = this._buf.position();
        byte[] sb = new byte[length];

        this._buf.position(this._offset + offset);
        this._buf.get(sb, 0, length);
        this._buf.position(saved_position);

        String s = new String(sb, "ASCII");

        int eos = s.indexOf(0x0);
        if (eos != -1) {
            s = s.substring(0, eos);
        }
        return s;
    }

    /**
     * getWindowsTimestamp fetches the 8-byte Windows timestamp at the relative offset.
     *   Note, no bounds checking is performed.
     *   The datetime is in UTC and has a resolution of milliseconds
     * @param offset The relative offset intot he buffer from which to read.
     * @return The datetime in UTC.
     */
    protected Calendar getWindowsTimestamp(int offset) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTimeInMillis((long)(this.getQword(offset) * 1e-4 - 11644473600000L));
        return c;
    }

    /**
     * getAbsoluteOffset calculates the absolute offset given a relative offset into this block.
     * @param offset A relative offset into this block.
     * @return The absolute offset from the start of the underlying structure.
     */
    protected int getAbsoluteOffset(int offset) {
        return this._offset + offset;
    }
}
