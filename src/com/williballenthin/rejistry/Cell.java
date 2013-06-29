package com.williballenthin.rejistry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Cell {
	private static final int LENGTH_OFFSET = 0x0;
	private final ByteBuffer _buf;
	private final HBIN _hbin;
	
	public Cell(HBIN hbin, ByteBuffer buf) throws RegistryParseException {
		this._hbin = hbin;
		this._buf = buf;
		this._buf.order(ByteOrder.LITTLE_ENDIAN);
		this._buf.position(0x0);
	}
	
	/**
	 * getLength fetches the size of this cell. It will always be a 
	 *   positive value.
	 * @return The length of this cell.
	 */
	public int getLength() {
		int length = this._buf.getInt(LENGTH_OFFSET);
		if (length < 0x0) {
			return -length;
		} else {
			return length;
		}
	}
	
	/**
	 * isActive describes if the cell contains an active structure.
	 * @return True if the cell is in use.
	 */
	public boolean isActive() {
		return this._buf.getInt(LENGTH_OFFSET) < 0x0;
	}
}
