package com.williballenthin.rejistry;

import java.nio.ByteBuffer;

public class REGFHeader {
	private ByteBuffer _buf;
	
	private static int MAGIC_OFFSET = 0x0;
	private static int SEQ1_OFFSET = 0x4;
	private static int SEQ2_OFFSET = 0x8;
	private static int MAJOR_VERSION_OFFSET = 0x14;
	private static int MINOR_VERSION_OFFSET = 0x18;
	private static int HIVE_NAME_OFFSET = 0x30;
	private static int LAST_HBIN_OFFSET_OFFSET = 0x28;
	
	public REGFHeader(ByteBuffer buf) throws RegistryParseException {
		this._buf = buf;
		
		int magic = this._buf.getInt(MAGIC_OFFSET);
		if (magic != 0x66676572) {
			throw new RegistryParseException("Invalid magic header.");
		}
		
		
	}
	
	public boolean isSynchronized() {
		return this._buf.getInt(SEQ1_OFFSET) == this._buf.getInt(SEQ2_OFFSET);
	}
	
	public int getMajorVersion() {
		return this._buf.getInt(MAJOR_VERSION_OFFSET);
	}
	
	public int getMinorVersion() {
		return this._buf.getInt(MINOR_VERSION_OFFSET);
	}
	
	public String getHiveName() {
		String s = (String) (this._buf.asCharBuffer().subSequence(HIVE_NAME_OFFSET, HIVE_NAME_OFFSET + 0x40));
		int eos = s.indexOf(0x0);
		if (eos != -1) {
			return s.substring(0, eos);
		}
		return s;
	}
	
	public int getLastHbinOffset() {
		return this._buf.getInt(LAST_HBIN_OFFSET_OFFSET);
	}
	
	public HBIN getFirstHBIN() {
		return HBIN(this._buf)
	}
}
