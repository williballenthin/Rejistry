package com.williballenthin.rejistry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * REGFHeader is the file header structure for a Registry hive.
 */
public class REGFHeader extends BinaryBlock {
    private static final int MAGIC_OFFSET = 0x0;
    private static final int SEQ1_OFFSET = 0x4;
    private static final int SEQ2_OFFSET = 0x8;
    private static final int MAJOR_VERSION_OFFSET = 0x14;
    private static final int MINOR_VERSION_OFFSET = 0x18;
    private static final int FIRST_KEY_OFFSET_OFFSET = 0x24;
    private static final int HIVE_NAME_OFFSET = 0x30;
    private static final int LAST_HBIN_OFFSET_OFFSET = 0x28;
    ///< scope: package protected
    static final int FIRST_HBIN_OFFSET = 0x1000;

    /**
     * @param buf
     * @param offset
     * @throws RegistryParseException if the magic header is not the ASCII string "REGF".
     */
    public REGFHeader(ByteBuffer buf, int offset) throws RegistryParseException {
        super(buf, offset);

        int magic = this.getDword(MAGIC_OFFSET);
        if (magic != 0x66676572) {
            throw new RegistryParseException("Invalid magic header.");
        }
    }

    public boolean isSynchronized() {
        return this.getDword(SEQ1_OFFSET) == this.getDword(SEQ2_OFFSET);
    }

    public int getMajorVersion() {
        return this.getDword(MAJOR_VERSION_OFFSET);
    }

    public int getMinorVersion() {
        return this.getDword(MINOR_VERSION_OFFSET);
    }

    public String getHiveName() throws UnsupportedEncodingException {
        return this.getWString(HIVE_NAME_OFFSET, 0x40);
    }

    public int getLastHbinOffset() {
        return this.getDword(LAST_HBIN_OFFSET_OFFSET);
    }

    /**
     * getHBINs creates an iterator over the HBINs that make up
     * this hive. The iterator does not support the `remove` operation.
     *
     * @return An iterator over the HBINs of this hive.
     */
    @Nullable
    public Iterator<HBIN> getHBINs() {
        return new Iterator<HBIN>() {
            ///< offset is the relative byte offset of the HBIN that would be returned in the next call to .next()
            private int _offset = FIRST_HBIN_OFFSET;
            @Nullable
            private HBIN _next = null;

            @Override
            public boolean hasNext() {
                if (!(REGFHeader.this._buf.capacity() > REGFHeader.this.getAbsoluteOffset(_offset) &&
                        REGFHeader.this.getLastHbinOffset() > REGFHeader.this.getAbsoluteOffset(_offset) &&
                        REGFHeader.this.getDword(_offset) == 0x6E696268)) {
                    return false;
                }

                try {
                    _next = new HBIN(REGFHeader.this, REGFHeader.this._buf, REGFHeader.this.getAbsoluteOffset(_offset));
                } catch (RegistryParseException e) {
                    return false;
                }
                return true;
            }

            @Nullable
            @Override
            public HBIN next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No more HBINs");
                }
                if (this._next.getRelativeOffsetNextHBIN() <= 0x0) {
                    // this really shouldn't be a NoSuchElementException, but its all we've got
                    //  we want to make sure we're always moving forward at each call to avoid
                    //  endless loops.
                    throw new NoSuchElementException("Invalid offset to next HBIN");
                }
                _offset += this._next.getRelativeOffsetNextHBIN();
                // we use this cached copy rather than creating the object here
                //   because the object construction may fail due to an invalid
                //   structure.
                return this._next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * getFirstHBIN fetches the first HBIN in the Registry hive, or raises an
     * exception if the hive is empty.
     *
     * @return The first HBIN in this hive.
     * @throws NoSuchElementException Better to throw exception than return null.
     */
    public HBIN getFirstHBIN() throws NoSuchElementException {
        return this.getHBINs().next();
    }

    /**
     * getRootNKRecord fetches the NKRecord that the Hive claims as the root.
     *
     * @return The root NKRecord.
     * @throws RegistryParseException if the creation of the NKRecord fails, or there are no HBINs in this Hive.
     */
    @NotNull
    public NKRecord getRootNKRecord() throws RegistryParseException {
        int first_cell_offset = this.getDword(FIRST_KEY_OFFSET_OFFSET);
        try {
            Cell cell = this.getFirstHBIN().getCellAtOffset(first_cell_offset);
            return cell.getNKRecord();
        } catch (NoSuchElementException e) {
            throw new RegistryParseException("No HBINs from which to fetch the root NKRecord");
        }
    }
}
