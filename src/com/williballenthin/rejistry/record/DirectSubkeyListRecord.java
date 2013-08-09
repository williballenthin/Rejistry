package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.Cell;
import com.williballenthin.rejistry.REGFHeader;
import com.williballenthin.rejistry.RegistryParseException;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DirectSubkeyListRecord extends SubkeyListRecord {
    private final int LIST_START_OFFSET = 0x4;
    private final int _item_size;

    public DirectSubkeyListRecord(ByteBuffer buf, int offset, int item_size) throws RegistryParseException {
        super(buf, offset);
        this._item_size = item_size;
    }

    public Iterator<NKRecord> getSubkeys() {
        return new Iterator<NKRecord>() {
            private int _index = 0;
            private int _max_index = DirectSubkeyListRecord.this.getListLength();
            private NKRecord _next = null;

            @Override
            public boolean hasNext() {
                if (this._index + 1 > this._max_index) {
                    return false;
                }

                int rel_off = DirectSubkeyListRecord.this.LIST_START_OFFSET + (this._index * DirectSubkeyListRecord.this._item_size);
                int offset = (int)DirectSubkeyListRecord.this.getDword(rel_off);
                int parent_offset = REGFHeader.FIRST_HBIN_OFFSET + offset;
                Cell c = new Cell(DirectSubkeyListRecord.this._buf, parent_offset);
                try {
                    this._next = c.getNKRecord();
                } catch (RegistryParseException e) {
                    return false;
                }

                return true;
            }

            @Override
            public NKRecord next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No more subkeys in the subkey list");
                }
                this._index++;
                return this._next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove operation not supported for subkey lists");
            }
        };
    }
}
