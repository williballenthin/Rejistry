package com.williballenthin.rejistry;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RegistryHiveFile implements RegistryHive {
	private ByteBuffer _buf;
	
	
	/** @throws IOException if the file cannot be accessed or is larger than 2GB. */
	public RegistryHiveFile(File file) throws IOException {
		this._buf = ByteBuffer.wrap(RegistryHiveFile.readFile(file)).asReadOnlyBuffer();
		this._buf.order(ByteOrder.BIG_ENDIAN);
	}
	
	public RegistryKey getRoot() {
		// TODO(wb): this.
		throw new UnsupportedOperationException();
	}
	
	public ByteBuffer getBuf() {
		return this._buf.asReadOnlyBuffer();
	}
	
	/**
	 * readFile reads an entire file into a byte array.
	 * @param file The file to read.
	 * @return A byte array that contains the file.
	 * @throws IOException
	 */
    private static byte[] readFile(File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength) {
                throw new IOException("File size >= 2 GB");
            }
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
