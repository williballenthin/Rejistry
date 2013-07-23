package com.williballenthin.rejistry.record;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptySubkeyList implements SubkeyList {

    public EmptySubkeyList() { }

    public Iterator<NKRecord> getSubkeys() {
        return new Iterator<NKRecord>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public NKRecord next() {
                throw new NoSuchElementException("Empty subkey list has no NKRecords");
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported for subkey lists");
            }
        };
    }

    public NKRecord getSubkey(String name) throws NoSuchElementException {
        throw new NoSuchElementException("Empty subkey list has no NKRecords");
    }
}
