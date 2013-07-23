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

    public abstract Iterator<NKRecord> getSubkeys();

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
