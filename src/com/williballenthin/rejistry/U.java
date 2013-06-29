package com.williballenthin.rejistry;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class U {
	public static final void d(String s) {
		System.out.println(s);
	}
	
	public static final String hex(int i) {
		return String.format("%x", i);
	}
	
	public static final void hex_out(int i) {
		d(hex(i));
	}
	
	/**
	 * getWString fetches `length` bytes from `buf` at relative offset `offset`
	 *   and interprets them as a UTF-16LE string.
	 *   This will return only the characters found before any NULL characters
	 *   (not NULL bytes).
	 * @param buf The buffer from which to read the string.
	 * @param offset The relative offset into the buffer from which to read.
	 * @param length The number of bytes to read.
	 * @return A string decoded from UTF-16LE bytes.
	 * @throws UnsupportedEncodingException
	 */
	public static final String getWString(ByteBuffer buf, int offset, int length) throws UnsupportedEncodingException {
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
	 * getWString fetches `length` bytes from `buf` at relative offset `offset`
	 *   and interprets them as an ASCII string.
	 *   This will return only the characters found before any NULL characters
	 *   (not NULL bytes).
	 * @param buf The buffer from which to read the string.
	 * @param offset The relative offset into the buffer from which to read.
	 * @param length The number of bytes to read.
	 * @return A string decoded from ASCII bytes.
	 * @throws UnsupportedEncodingException
	 */
	public static final String getASCIIString(ByteBuffer buf, int offset, int length) throws UnsupportedEncodingException {
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
