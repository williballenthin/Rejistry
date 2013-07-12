package com.williballenthin.rejistry;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LFRecord extends Record implements SubkeyList {
    /**
     *
     * @param buf
     * @param offset
     * @throws RegistryParseException if the magic header is not the ASCII string "nk".
     */
    public LFRecord(ByteBuffer buf, int offset) throws RegistryParseException {
        super(buf, offset);

        if (!this.getMagic().equals("lf")) {
            throw new RegistryParseException("LFRecord invalid magic header, expected \"lf\", got: " + this.getMagic());
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
