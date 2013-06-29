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
	
	public static final String getWString(ByteBuffer buf, int offset, int length) throws UnsupportedEncodingException {
		int saved_position = buf.position();
		byte[] sb = new byte[length];
		
		buf.position(offset);
		buf.get(sb, 0, length);
		buf.position(saved_position);
		
		String s = new String(sb, "UTF-16LE");
		
		int eos = s.indexOf(0x0);
		if (eos != -1) {
			return s = s.substring(0, eos);
		}
		
		return s;
	}
}
