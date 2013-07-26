package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.Cell;
import com.williballenthin.rejistry.REGFHeader;
import com.williballenthin.rejistry.RegistryParseException;

import java.nio.ByteBuffer;

public class DBRecord extends Record {
    public static final String MAGIC = "db";
    private static final int INDIRECT_BLOCK_OFFSET_OFFSET = 0x4;

    public DBRecord(ByteBuffer buf, int offset) throws RegistryParseException {
        super(buf, offset);

        if (!this.getMagic().equals(DBRecord.MAGIC)) {
            throw new RegistryParseException("DBRecord invalid magic header, expected \"db\", got: " + this.getMagic());
        }
    }

    /**
     * getData fetches `length` data from the blocks pointed to by this DBRecord.
     * Note, there's no structural checking performed to ensure the direct blocks are
     *   valid.
     * @param length The number of bytes to attempt to parse from the direct blocks.
     * @return The bytes parsed from the blocks.
     * @throws java.nio.BufferOverflowException if too much data is requested.
     */
    public ByteBuffer getData(int length) throws RegistryParseException {
        int offset = (int)this.getDword(INDIRECT_BLOCK_OFFSET_OFFSET);
        offset += REGFHeader.FIRST_HBIN_OFFSET;

        Cell c = new Cell(this._buf, offset);
        DBIndirectRecord dbi = c.getDBIndirectRecord();
        return dbi.getData(length);
    }
}
