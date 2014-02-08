package com.williballenthin.rejistry.record;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class SubkeyListRecord extends Record implements SubkeyList {
    private static final int LIST_LENGTH_OFFSET = 0x2;

    public SubkeyListRecord(ByteBuffer buf, int offset) {
        super(buf, offset);
    }

    /**
     * getListLength fetches the number of subkeys this list has.
     * @return the number of subkeys this list has.
     */
    public int getListLength() {
        return this.getWord(LIST_LENGTH_OFFSET);
    }

    /**
     * getSubkeys fetches an iterator over the of subkeys of this list.
     * @return An iterator over the subkeys of this list.
     */
    public abstract Iterator<NKRecord> getSubkeys();

    /**
     * getSubkey fetches the subkey with name `name` from the list of subkeys.
     * Matching is performed case-insensitively.
     * @param name The name of the subkey to fetch.
     * @return The subkey with name `name` from the list of subkeys.
     * @throws NoSuchElementException if a key with name `name` does not exist in this list.
     */
    public NKRecord getSubkey(String name) throws NoSuchElementException {
        Iterator<NKRecord> it = this.getSubkeys();
        while (it.hasNext()) {
            NKRecord r = it.next();
            try {
                if (r.getName().equalsIgnoreCase(name)) {
                    return r;
                }
            } catch (UnsupportedEncodingException e) {
                continue;
            }
        }
        throw new NoSuchElementException("Cannot find subkey with name " + name);
    }
}
