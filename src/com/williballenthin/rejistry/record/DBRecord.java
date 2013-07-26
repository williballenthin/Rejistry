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

    public ByteBuffer getData(int length) throws RegistryParseException {
        int offset = (int)this.getDword(INDIRECT_BLOCK_OFFSET_OFFSET);
        offset += REGFHeader.FIRST_HBIN_OFFSET;

        Cell c = new Cell(this._buf, offset);
        DBIndirectRecord dbi = c.getDBIndirectRecord();
        return dbi.getData(length);
    }
}
