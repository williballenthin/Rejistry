package com.williballenthin.rejistry;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface SubkeyList {
    public Iterator<NKRecord> getSubkeys();
    public NKRecord getSubkey(String name) throws NoSuchElementException;
}
