package com.williballenthin.rejistry;

import java.nio.ByteBuffer;

public class NKRecord extends Record {
	private static final int CLASSNAME_OFFSET_OFFSET = 0x30;
	private static final int CLASSNAME_LENGTH_OFFSET = 0x4A;
	
	public NKRecord(HBIN hbin, ByteBuffer buf) throws RegistryParseException {
		super(hbin, buf);
		
		if (this.getMagic() != "NK") {
			throw new RegistryParseException("NKRecord invalid magic header");
		}
	}
	
	public NKRecord(Cell cell) throws RegistryParseException {
		super(cell);
		
		if (this.getMagic() != "NK") {
			throw new RegistryParseException("NKRecord invalid magic header");
		}
	}
	
	public boolean hasClassname() {
		return this._buf.getInt(CLASSNAME_OFFSET_OFFSET) != 0xFFFFFFFF;
	}
	
	public String getClassname() {
		int offset = this._buf.getInt(CLASSNAME_OFFSET_OFFSET);
		int length = this._buf.getInt(CLASSNAME_LENGTH_OFFSET);
	}
}
