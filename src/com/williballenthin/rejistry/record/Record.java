package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.BinaryBlock;
import com.williballenthin.rejistry.RegistryParseException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Record is a common superclass to structures that are found
 *   within Cells.
 */
public class Record extends BinaryBlock {

    private static final int MAGIC_OFFSET = 0x0;

    public Record(ByteBuffer buf, int offset) {
        super(buf, offset);
    }

    /**
     * getMagic fetches the magic bytes that determine this
     * Record's type.
     * Return value is a string, because for all known Record
     * types, the magic values falls within the ASCII range.
     *
     * @return A two character string that is the magic record header.
     * @throws com.williballenthin.rejistry.RegistryParseException if the magic header is a two byte ASCII string.
     */
    public String getMagic() throws RegistryParseException {
        try {
            return this.getASCIIString(MAGIC_OFFSET, 0x2);
        } catch (UnsupportedEncodingException e) {
            throw new RegistryParseException("Unexpected magic header.");
        }
    }
}
