package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.Cell;
import com.williballenthin.rejistry.REGFHeader;
import com.williballenthin.rejistry.RegistryParseException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ValueListRecord extends Record implements ValueList {
    private static final int VALUE_LIST_OFFSET = 0x0;
    private final int _numValues;

    public ValueListRecord(ByteBuffer buf, int offset, int numValues) {
        super(buf, offset);

        this._numValues = numValues;
    }

    public Iterator<VKRecord> getValues() {
        return new Iterator<VKRecord>() {
            private int _index = 0x0;
            private VKRecord _next = null;

            @Override
            public boolean hasNext() {
                if (this._index >= ValueListRecord.this._numValues) {
                    return false;
                }

                int offset = (int)ValueListRecord.this.getDword(VALUE_LIST_OFFSET + (0x4 * _index));
                offset += REGFHeader.FIRST_HBIN_OFFSET;
                Cell c = new Cell(ValueListRecord.this._buf, offset);
                try {
                    this._next = c.getVKRecord();
                } catch (RegistryParseException e) {
                    return false;
                }
                return true;
            }

            @Override
            public VKRecord next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No more VKRecords");
                }
                this._index++;
                return this._next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public VKRecord getValue(String name) throws NoSuchElementException {
        Iterator<VKRecord> it = this.getValues();
        while (it.hasNext()) {
            VKRecord r = it.next();
            try {
                if (r.getName().equalsIgnoreCase(name)) {
                    return r;
                }
            } catch (UnsupportedEncodingException e) {
                continue;
            }
        }
        throw new NoSuchElementException("Cannot find value with name " + name);
    }
}
