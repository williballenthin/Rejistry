package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.Cell;
import com.williballenthin.rejistry.REGFHeader;

import java.nio.ByteBuffer;

public class DBIndirectRecord extends Record {
    private static final int OFFSET_LIST_OFFSET = 0x0;

    public DBIndirectRecord(ByteBuffer buf, int offset) {
        super(buf, offset);
    }

    public ByteBuffer getData(int length) {
        ByteBuffer b = ByteBuffer.allocate(length);
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

            data.limit(size);
            data.flip();
            b.put(data);

            length -= size;
            count += 1;
            // TODO(wb): could use some more error checking here.
        }
        return b;
    }
}
