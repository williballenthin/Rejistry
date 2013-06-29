package com.williballenthin.rejistry;

import java.nio.ByteBuffer;

public interface RegistryHive {
	/**
	 * getRoot fetches the root RegistryKey of the hive.
	 * @return The root of the hive.
	 */
	public RegistryKey getRoot();
	
	/**
	 * getBuf fetches a view of the ByteBuffer that backs the hive.
	 * 	This is probably only used by low level parsing code.
	 * @return A read-only ByteBuffer that backs the hive.
	 */
	public ByteBuffer getBuf();

	
	/**
	 * getHeader fetches the REGFHeader that defined metadata
	 *   for this Registry hive.
	 * @return The header of the hive.
	 */
	public REGFHeader getHeader() throws RegistryParseException;
}
