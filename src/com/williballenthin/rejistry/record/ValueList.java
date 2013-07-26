package com.williballenthin.rejistry.record;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface ValueList {
    public Iterator<VKRecord> getValues();
    public VKRecord getValue(String name) throws NoSuchElementException;
}
