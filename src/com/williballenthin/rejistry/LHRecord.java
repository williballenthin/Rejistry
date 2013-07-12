package com.williballenthin.rejistry;

import java.nio.ByteBuffer;
import java.util.Iterator;

public class LHRecord extends SubkeyListRecord  {
    /**
     * @throws RegistryParseException if the magic header is not the ASCII string "lh".
     */
    public LHRecord(ByteBuffer buf, int offset) throws RegistryParseException {
        super(buf, offset);

        if (!this.getMagic().equals("lh")) {
            throw new RegistryParseException("LHRecord invalid magic header, expected \"lh\", got: " + this.getMagic());
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
}
