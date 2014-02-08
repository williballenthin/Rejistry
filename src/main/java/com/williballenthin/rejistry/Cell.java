package com.williballenthin.rejistry;

import com.williballenthin.rejistry.record.*;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Cell is a simple container for other data. The container has just the its size as a header.
 *   The remainder is arbitrary data.
 */
public class Cell extends BinaryBlock {
    private static final int LENGTH_OFFSET = 0x0;
    private static final int DATA_OFFSET = 0x4;

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
        // this will cast down to a negative as required
        int length = (int)this.getDword(LENGTH_OFFSET);
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
        return (int)this.getDword(LENGTH_OFFSET) < 0x0;
    }

    /**
     * getData fetches a view into the data of this cell.
     * The returned ByteBuffer is limited to the range of
     * this cell, and now beyond.
     *
     * @return A view of the data in this cell.
     */
    public ByteBuffer getData() {
        this._buf.position(this.getAbsoluteOffset(DATA_OFFSET));
        ByteBuffer data = this._buf.slice();
        data.limit(this.getLength() - DATA_OFFSET);
        return data;
    }

    /**
     * getDataSignature fetches the first two bytes of the data of this cell
     *   and interprets it as the ASCII magic header of a sub-structure.
     * @return The two character string that identifies the substructure of this cell.
     * @throws UnsupportedEncodingException if the magic header does not decode
     *   as an ASCII string.
     */
    public String getDataSignature() throws UnsupportedEncodingException {
        return this.getASCIIString(DATA_OFFSET, 0x2);
    }

    /**
     * getDataQword fetches the first eight bytes of the data of this cell
     *   and interprets it as a little endian QWORD.
     * @return The first eight bytes of this cell interpreted as a little endian QWORD.
     */
    public long getDataQword() {
        return this.getQword(DATA_OFFSET);
    }

    /**
     * getNKRecord interprets the data of this cell as an NKRecord and
     *   returns the parsed out structure.
     *
     * @return The NKRecord found within this Cell.
     * @throws RegistryParseException if the creation of the NKRecord fails.
     */
    public NKRecord getNKRecord() throws RegistryParseException {
        return new NKRecord(this._buf, this.getAbsoluteOffset(DATA_OFFSET));
    }

    /**
     * getLFRecord interprets the data of this cell as an LHRecord and
     *   returns the parsed out structure.
     *
     * @return The LFRecord found within this Cell.
     * @throws RegistryParseException if the creation of the NKRecord fails.
     */
    public LFRecord getLFRecord() throws RegistryParseException {
        return new LFRecord(this._buf, this.getAbsoluteOffset(DATA_OFFSET));
    }

    /**
     * getLHRecord interprets the data of this cell as an LHRecord and
     *   returns the parsed out structure.
     *
     * @return The LHRecord found within this Cell.
     * @throws RegistryParseException if the creation of the NKRecord fails.
     */
    public LHRecord getLHRecord() throws RegistryParseException {
        return new LHRecord(this._buf, this.getAbsoluteOffset(DATA_OFFSET));
    }

    /**
     * getRIRecord interprets the data of this cell as an RIRecord and
     *   returns the parsed out structure.
     *
     * @return The RIRecord found within this Cell.
     * @throws RegistryParseException if the creation of the NKRecord fails.
     */
    public RIRecord getRIRecord() throws RegistryParseException {
        return new RIRecord(this._buf, this.getAbsoluteOffset(DATA_OFFSET));
    }

    /**
     * getLIRecord interprets the data of this cell as an LIRecord and
     *   returns the parsed out structure.
     *
     * @return The LIRecord found within this Cell.
     * @throws RegistryParseException if the creation of the NKRecord fails.
     */
    public LIRecord getLIRecord() throws RegistryParseException {
        return new LIRecord(this._buf, this.getAbsoluteOffset(DATA_OFFSET));
    }

    /**
     * getSubkeyList interprets the data of this cell as a SubkeyList and
     *   returns the parsed out structure.
     * @return The SubkeyList found within this Cell.
     * @throws RegistryParseException if the signature of the data is
     *   incorrect, or the creation of the Subkey fails.
     * TODO(wb): differentiate this failure cases.
     */
    public SubkeyList getSubkeyList() throws RegistryParseException {
        String magic;
        try {
            magic = this.getDataSignature();
        } catch (UnsupportedEncodingException e) {
            throw new RegistryParseException("Unexpected subkey list type: binary");
        }
        if (magic.equals(LFRecord.MAGIC)) {
            return this.getLFRecord();
        } else if (magic.equals(LHRecord.MAGIC)) {
            return this.getLHRecord();
        } else if (magic.equals(RIRecord.MAGIC)) {
            return this.getRIRecord();
        } else if (magic.equals(LIRecord.MAGIC)) {
            return this.getLIRecord();
        } else {
            throw new RegistryParseException("Unexpected subkey list type: " + magic);
        }
    }

    /**
     * getDBRecord interprets the data of this cell as an DBRecord and
     *   returns the parsed out structure.
     *
     * @return The DBRecord found within this Cell.
     * @throws RegistryParseException if the creation of the DBRecord fails.
     */
    public DBRecord getDBRecord() throws RegistryParseException {
        return new DBRecord(this._buf, this.getAbsoluteOffset(DATA_OFFSET));
    }

    /**
     * getDBIndirectRecord interprets the data of this cell as an DBIndirectRecord and
     *   returns the parsed out structure.
     *
     * @return The DBIndirectRecord found within this Cell.
     * @throws RegistryParseException if the creation of the DBIndirectRecord fails.
     */
    public DBIndirectRecord getDBIndirectRecord() throws RegistryParseException {
        return new DBIndirectRecord(this._buf, this.getAbsoluteOffset(DATA_OFFSET));
    }

    /**
     * getVKRecord interprets the data of this cell as an VKRecord and
     *   returns the parsed out structure.
     *
     * @return The VKRecord found within this Cell.
     * @throws RegistryParseException if the creation of the VKRecord fails.
     */
    public VKRecord getVKRecord() throws RegistryParseException {
        return new VKRecord(this._buf, this.getAbsoluteOffset(DATA_OFFSET));
    }

    /**
     * getValueListRecord interprets the data of this cell as a ValueListRecord and
     *   returns the parsed out structure.
     *
     * @param numValues The number of values the value list should attempt to parse.
     * @return The ValueListRecord found within this Cell.
     * @throws RegistryParseException if the creation of the ValueListRecord fails.
     */
    public ValueListRecord getValueListRecord(int numValues) throws RegistryParseException {
        return new ValueListRecord(this._buf, this.getAbsoluteOffset(DATA_OFFSET), numValues);
    }
}
