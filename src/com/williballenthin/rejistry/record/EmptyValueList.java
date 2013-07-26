package com.williballenthin.rejistry.record;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyValueList implements ValueList {
    public Iterator<VKRecord> getValues() {
        return new Iterator<VKRecord>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public VKRecord next() {
                throw new NoSuchElementException("Empty value list has no VKRecords");
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported for value lists");
            }
        };
    }

    public VKRecord getValue(String name) throws NoSuchElementException {
        throw new NoSuchElementException("Empty value list has no VKRecords");
    }
}
