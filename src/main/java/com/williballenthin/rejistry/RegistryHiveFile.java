package com.williballenthin.rejistry;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class RegistryHiveFile implements RegistryHive {
    private ByteBuffer _buf;

    /**
     * @throws IOException if the file cannot be accessed
     */
    public RegistryHiveFile(File file) throws IOException {
        this._buf = RegistryHiveFile.readFile(file);
        this._buf.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public RegistryKey getRoot() throws RegistryParseException {
        return new RegistryKey(this.getHeader().getRootNKRecord());
    }

    @Override
    public REGFHeader getHeader() throws RegistryParseException {
        return new REGFHeader(this._buf, 0x0);
    }

    /**
     * readFile reads an entire file as a ByteBuffer.
     *
     * @param file The file to read.
     * @return A ByteBuffer of the file contents
     * @throws IOException
     */
    private static ByteBuffer readFile(File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            long length = f.length();
            return f.getChannel().map(MapMode.READ_ONLY, 0, length);
        } finally {
            f.close(); // does not affect ByteBuffer mapping
        }
    }
}
