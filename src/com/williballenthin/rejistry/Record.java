package com.williballenthin.rejistry;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * Record is a common superclass to structures that are found
 *   within Cells.
 */
public class Record {
	protected final HBIN _hbin;
	protected final ByteBuffer _buf;
	public Record(HBIN hbin, ByteBuffer buf) {
		this._hbin = hbin;
		this._buf = buf;
		this._buf.order(ByteOrder.LITTLE_ENDIAN);
		this._buf.position(0x0);
	}
	
	public Record(Cell cell) {
		this._hbin = cell.getHBIN();
		this._buf = cell.getData();
		this._buf.order(ByteOrder.LITTLE_ENDIAN);
		this._buf.position(0x0);
	}
	
	/**
	 * getMagic fetches the magic bytes that determine this
	 *   Record's type.
	 *   Return value is a string, because for all known Record
	 *   types, the magic values falls within the ASCII range.
	 * @return A two character string that is the magic record header.
	 * @throws RegistryParseException 
	 */
	public String getMagic() throws RegistryParseException {
		try {
			return U.getASCIIString(this._buf, 0x0, 0x2);
		} catch (UnsupportedEncodingException e) {
			throw new RegistryParseException("Unexpected magic header.");
		}
	}
}
