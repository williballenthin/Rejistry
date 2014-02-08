package com.williballenthin.rejistry;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class U {
    public static void d(String s) {
        System.out.println(s);
    }

    public static String hex(int i) {
        return String.format("%x", i);
    }

    public static String hex(long i) {
        return String.format("%x", i);
    }

    public static void hex_out(int i) {
        d(hex(i));
    }

    public static void hex_out(long i) {
        d(hex(i));
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
     * parseASCIIString fetches `length` bytes from `buf` at relative offset `offset`
     * and interprets them as a ASCII string.
     * This will return only the characters found before any NULL characters
     * (not NULL bytes).
     *
     * @param offset The relative offset into the buffer from which to read.
     * @param length The number of bytes to read.
     * @return A string decoded from ASCII bytes.
     * @throws UnsupportedEncodingException if the bytes cannot be decoded as an ASCII string.
     */
    public static String parseASCIIString(ByteBuffer buf, int offset, int length) throws UnsupportedEncodingException {
        int saved_position = buf.position();
        byte[] sb = new byte[length];

        buf.position(offset);
        buf.get(sb, 0, length);
        buf.position(saved_position);

        String s = new String(sb, "ASCII");

        int eos = s.indexOf(0x0);
        if (eos != -1) {
            s = s.substring(0, eos);
        }
        return s;
    }
}
