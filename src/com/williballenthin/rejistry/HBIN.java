package com.williballenthin.rejistry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * HBIN is an allocation unit of a Registry hive that is usually 
 *   0x1000 bytes long.
 */
public class HBIN {
	private static final int FIRST_HBIN_OFFSET_OFFSET = 0x4;
	private static final int NEXT_HBIN_OFFSET_OFFSET = 0x8;
	private static final int FIRST_CELL_OFFSET = 0x20;
	private final REGFHeader _header;
	private final ByteBuffer _buf;
	
	public HBIN(REGFHeader header, ByteBuffer buf) throws RegistryParseException {
		this._header = header;
		this._buf = buf;
		this._buf.order(ByteOrder.LITTLE_ENDIAN);
		this._buf.position(0x0);
		
		if (this._buf.getInt(0x0) != 0x6E696268) {
			throw new RegistryParseException("Invalid magic header.");
		}
	}
	
	/**
	 * getRelativeOffsetNextHBIN fetches the relative offset from the
	 *   start of this HBIN to the next HBIN structure. I suppose it is probably
	 *   be positive, but would be pretty cool if it were negative. This also
	 *   probably describes the length of this HBIN.
	 *   Note, if this is not a positive number, then other parsing code in this
	 *   library will be broken (see `getCells`).
	 * @return The relative offset to the next HBIN.
	 */
	public int getRelativeOffsetNextHBIN() {
		return this._buf.getInt(NEXT_HBIN_OFFSET_OFFSET);
	}
	
	/**
	 * getRelativeOffsetFirstHBIN fetches the relative offset from the
	 *   start of this HBIN to the first HBIN in the hive.
	 *   This will be a positive number, which is the number of bytes
	 *   you need to go backwards to get the first HBIN.
	 * @return The relative offset to the first HBIN in the hive.
	 */
	public int getRelativeOffsetFirstHBIN() {
		return this._buf.getInt(FIRST_HBIN_OFFSET_OFFSET);
	}
	
	// it would be nice to have this method, but we cannot, since
	//   our buffer doesn't extend backwards at all.
	//public HBIN getFirstHBIN() { }
	
	/**
	 * getCellss creates an iterator over the Cells that make up 
	 *   this HBIN The iterator does not support the `remove` operation.
	 * @return An iterator over the Cells in this HBIN.
	 */
	public Iterator<Cell> getCells() {
		return new Iterator<Cell>() {
			///< offset is the absolute byte offset of the Cell that would
			///    be returned in the next call to .next()
			private int _offset = FIRST_CELL_OFFSET;
			private Cell _next = null;
			
			@Override
			public boolean hasNext() {
				HBIN.this._buf.position(0);

				if ( ! (HBIN.this._buf.capacity() > _offset &&
						HBIN.this.getRelativeOffsetNextHBIN() > _offset)) { 
					return false;
				}
				
				
				try {
					HBIN.this._buf.position(_offset);
					ByteBuffer data = HBIN.this._buf.slice();
					data.limit(HBIN.this.getRelativeOffsetNextHBIN());
					_next = new Cell(HBIN.this, data);
				} catch (RegistryParseException e) {
					return false;
				}
				return true;
			}

			@Override
			public Cell next() {
				if (!this.hasNext()) {
					throw new NoSuchElementException("No more Cells");
				}
				if (this._next.getLength() == 0x0) {
					// this really shouldn't be a NoSuchElementException, but its all we've got
					//  we want to make sure we're always moving forward at each call to avoid
					//  endless loops.
					throw new NoSuchElementException("Invalid offset to next Cell");
				}
				_offset += this._next.getLength();
				// we use this cached copy rather than creating the object here
				//   because the object construction may fail due to an invalid 
				//   structure.
				return this._next;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};		
	}
	
	/**
	 * getCellAtOffset returns a Cell that starts at the given relative offset into
	 *   this HBIN.
	 *   Note, no checking of the Cell structure is performed, so accessing the correct
	 *   offset is up to the caller.
	 *   Note, this function is meant for internal use.
	 *   Visibility: package-protected.
	 * @param offset The relative offset into this HBIN to construct the Cell.
	 * @return A Cell that starts at the given offset.
	 * @throws RegistryParseException
	 */
	Cell getCellAtOffset(int offset) throws RegistryParseException {
		this._buf.position(offset);
		return new Cell(this, this._buf.slice());
	}
}
