package com.williballenthin.rejistry;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
     * getDword parses a 32bit number from the specified relative offset with the range 0-2**32 - 1;
     *
     * @param offset The relative offset from which to parse the number.
     * @return A non-negative 32bit number with the range 0-2**32-1;
     */
    protected int getDword(int offset) {
        return this._buf.getInt(this._offset + offset) & 0xFFFFFFFF;
    }

    /**
     * getWString fetches `length` bytes from `buf` at relative offset `offset`
     * and interprets them as a UTF-16LE string.
     * This will return only the characters found before any NULL characters
     * (not NULL bytes).
     *
     * @param offset The relative offset into the buffer from which to read.
     * @param length The number of bytes to read.
     * @return A string decoded from UTF-16LE bytes.
     * @throws UnsupportedEncodingException if the bytes cannot be decoded as an UTF-16LE string.
     */
    protected String getWString(int offset, int length) throws UnsupportedEncodingException {
        int saved_position = this._buf.position();
        byte[] sb = new byte[length];

        this._buf.position(offset);
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
     *
     * @param offset The relative offset into the buffer from which to read.
     * @param length The number of bytes to read.
     * @return A string decoded from ASCII bytes.
     * @throws UnsupportedEncodingException if the bytes cannot be decoded as an ASCII string.
     */
    protected String getASCIIString(int offset, int length) throws UnsupportedEncodingException {
        int saved_position = this._buf.position();
        byte[] sb = new byte[length];

        this._buf.position(offset);
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
     * getAbsoluteOffset calculates the absolute offset given a relative offset into this block.
     * @param offset A relative offset into this block.
     * @return The absolute offset from the start of the underlying structure.
     */
    protected int getAbsoluteOffset(int offset) {
        return this._offset + offset;
    }
}
