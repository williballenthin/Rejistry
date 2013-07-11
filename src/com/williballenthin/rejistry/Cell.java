package com.williballenthin.rejistry;

import java.nio.ByteBuffer;

public class Cell extends BinaryBlock {
    private static final int LENGTH_OFFSET = 0x0;

    public Cell(ByteBuffer buf, int offset) {
        super(buf, offset);
    }

    /**
     * getLength fetches the size of this cell. It will always be a
     * positive value.
     *
     * @return The length of this cell.
     */
    public int getLength() {
        int length = this.getDword(LENGTH_OFFSET);
        if (length < 0x0) {
            return -length;
        } else {
            return length;
        }
    }

    /**
     * isActive describes if the cell contains an active structure.
     *
     * @return True if the cell is in use.
     */
    public boolean isActive() {
        return this.getDword(LENGTH_OFFSET) < 0x0;
    }

    /**
     * getData fetches a view into the data of this cell.
     * The returned ByteBuffer is limited to the range of
     * this cell, and now beyond.
     *
     * @return A view of the data in this cell.
     */
    public ByteBuffer getData() {
        this._buf.position(this.getAbsoluteOffset(0x4));
        ByteBuffer data = this._buf.slice();
        data.limit(this.getLength() - 0x4);
        return data;
    }

    /**
     * getNKRecord interprets the data of this cell as an NKRecord and
     *   returns the parsed out structure.
     *
     * @return The NKRecord found within this Cell.
     * @throws RegistryParseException if the creation of the NKRecord fails.
     */
    public NKRecord getNKRecord() throws RegistryParseException {
        return new NKRecord(this._buf, this.getAbsoluteOffset(0x4));
    }
}
