package com.williballenthin.rejistry;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LIRecord extends Record implements SubkeyList {
    /**
     *
     * @param buf
     * @param offset
     * @throws RegistryParseException if the magic header is not the ASCII string "li".
     */
    public LIRecord(ByteBuffer buf, int offset) throws RegistryParseException {
        super(buf, offset);

        if (!this.getMagic().equals("li")) {
            throw new RegistryParseException("LIRecord invalid magic header, expected \"li\", got: " + this.getMagic());
        }
    }

    public Iterator<NKRecord> getSubkeys() {
        return new Iterator<NKRecord>() {
            @Override
            public boolean hasNext() {
                // TODO(wb): implement me
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public NKRecord next() {
                // TODO(wb): implement me
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public void remove() {
                // TODO(wb): implement me
                throw new UnsupportedOperationException("Remove not supported for subkey lists");
            }
        };
    }

    public NKRecord getSubkey(String name) throws NoSuchElementException {
        // TODO(wb): implement me
        throw new UnsupportedOperationException("TODO");
    }
}
