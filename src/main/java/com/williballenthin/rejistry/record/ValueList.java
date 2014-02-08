package com.williballenthin.rejistry.record;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface ValueList {
    /**
     * getValues fetches an iterator over the values linked in this list.
     * @return An iterator over the values linked in this list.
     */
    public Iterator<VKRecord> getValues();

    /**
     * getValue fetches the value with name `name from the list of values.
     * Matching is done case-insensitively.
     * @param name The name of the value to fetch.
     * @return The value with name `name` from this list of values.
     * @throws NoSuchElementException if a value with name `name` does not exist in this list.
     */
    public VKRecord getValue(String name) throws NoSuchElementException;
}
