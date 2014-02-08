package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.Cell;
import com.williballenthin.rejistry.REGFHeader;

import java.nio.ByteBuffer;

public class DBIndirectRecord extends Record {
    private static final int OFFSET_LIST_OFFSET = 0x0;

    public DBIndirectRecord(ByteBuffer buf, int offset) {
        super(buf, offset);
    }

    /**
     * getData fetches `length` data from the blocks pointed to by this indirect block.
     * Note, there's no structural checking performed to ensure the direct blocks are
     *   valid.
     * @param length The number of bytes to attempt to parse from the direct blocks.
     * @return The bytes parsed from the blocks.
     * @throws java.nio.BufferOverflowException if too much data is requested.
     */
    public ByteBuffer getData(int length) {
        ByteBuffer b = ByteBuffer.allocate(length);
        b.position(0x0);
        int count = 0;
        while (length > 0) {
            int size = 0x3fd8;
            int offset = (int)this.getDword(OFFSET_LIST_OFFSET + (count * 4));
            offset += REGFHeader.FIRST_HBIN_OFFSET;
            Cell c = new Cell(this._buf, offset);
            ByteBuffer data = c.getData();

            if (length < size) {
                size = length;
            }

            data.position(size);
            data.flip();
            data.limit(size);
            data.position(0x0);

            b.put(data);

            length -= size;
            count += 1;
            // TODO(wb): could use some more error checking here.
        }
        return b;
    }
}
