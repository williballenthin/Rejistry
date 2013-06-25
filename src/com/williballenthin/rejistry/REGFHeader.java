package com.williballenthin.rejistry;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class REGFHeader {
	private final ByteBuffer _buf;
	
	private static final int MAGIC_OFFSET = 0x0;
	private static final int SEQ1_OFFSET = 0x4;
	private static final int SEQ2_OFFSET = 0x8;
	private static final int MAJOR_VERSION_OFFSET = 0x14;
	private static final int MINOR_VERSION_OFFSET = 0x18;
	private static final int HIVE_NAME_OFFSET = 0x30;
	private static final int LAST_HBIN_OFFSET_OFFSET = 0x28;
	
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
	
	public String getHiveName() throws UnsupportedEncodingException {
		byte[] sb = null;
		this._buf.get(sb, HIVE_NAME_OFFSET, 0x40);
		String s = new String(sb, "ASCII");
		
		int eos = s.indexOf(0x0);
		if (eos != -1) {
			return s = s.substring(0, eos);
		}
		
		return s;
	}
	
	public int getLastHbinOffset() {
		return this._buf.getInt(LAST_HBIN_OFFSET_OFFSET);
	}
	
	/**
	 * getHBINs creates an iterator over the HBINs that make up 
	 *   this hive. The iterator does not support the `remove` operation.
	 * @return An iterator over the HBINs of this hive.
	 */
	public Iterator<HBIN> getHBINs() {
		return new Iterator<HBIN>() {
			///< offset is the absolute byte offset of the HBIN that would
			///    be returned in the next call to .next()
			private int _offset = 0x1000;
			private HBIN _next = null;
			
			@Override
			public boolean hasNext() {
				if ( ! (REGFHeader.this._buf.capacity() > _offset && 
						REGFHeader.this.getLastHbinOffset() > _offset &&
						REGFHeader.this._buf.getInt(_offset) == 0x6E696268)) {
					return false;
				}
				REGFHeader.this._buf.position(_offset);
				
				try {
					_next = new HBIN(REGFHeader.this, REGFHeader.this._buf.slice());
				} catch (RegistryParseException e) {
					return false;
				}
				return true;
			}

			@Override
			public HBIN next() {
				if (!this.hasNext()) {
					throw new NoSuchElementException("No more HBINs");
				}
				_offset += 0x1000;
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
	 * getFirstHBIN fetches the first HBIN in the Registry hive, or raises an 
	 *   exception if the hive is empty.
	 * @return The first HBIN in this hive.
	 * @throws NoSuchElementException Better to throw exception than 
	 *   return null.
	 */
	public HBIN getFirstHBIN() throws NoSuchElementException {
		return this.getHBINs().next();
	}
}
