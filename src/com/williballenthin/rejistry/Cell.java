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
	
	/**
	 * getHBIN fetches the parent HBIN of this cell.
	 * visibility: package-protected
	 * @return The HBIN that contains this cell.
	 */
	HBIN getHBIN() {
		return this._hbin;
	}
	
	/**
	 * getData fetches a view into the data of this cell.
	 *   The returned ByteBuffer is limited to the range of
	 *   this cell, and now beyond.
	 * @return A view of the data in this cell.
	 */
	public ByteBuffer getData() {
		this._buf.position(0x4);
		ByteBuffer data = this._buf.slice();
		data.limit(this.getLength() - 0x4);
		return data;
	}
}
