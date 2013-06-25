package com.williballenthin.rejistry;

import java.nio.ByteBuffer;

public class HBIN {
	private REGFHeader _header;
	private ByteBuffer _buf;
	public HBIN(REGFHeader header, ByteBuffer buf) throws RegistryParseException {
		this._header = header;
		this._buf = buf;
		
		if (this._buf.getInt(0x0) != 0x6E696268) {
			throw new RegistryParseException("Invalid magic header.");
		}
	}
}
