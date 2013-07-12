package com.williballenthin.rejistry;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class SubkeyListRecord extends Record implements SubkeyList {
    private static final int SUBKEY_COUNT_OFFSET = 0x2;

    /**
     *
     * @param buf
     * @param offset
     */
    public SubkeyListRecord(ByteBuffer buf, int offset) {
        super(buf, offset);
    }

    /**
     * getSubkeyCount fetches the number of subkeys this list has.
     * @return the number of subkeys this list has.
     */
    public int getSubkeyCount() {
        return this.getWord(SUBKEY_COUNT_OFFSET);
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
