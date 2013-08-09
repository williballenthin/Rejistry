package com.williballenthin.rejistry.record;

import com.williballenthin.rejistry.*;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RIRecord extends SubkeyListRecord {
    public static final String MAGIC = "ri";
    private static final int LIST_START_OFFSET = 0x4;
    private static final int LIST_ENTRY_SIZE = 0x4;

    /**
     * @throws com.williballenthin.rejistry.RegistryParseException if the magic header is not the ASCII string "ri".
     */
    public RIRecord(ByteBuffer buf, int offset) throws RegistryParseException {
        super(buf, offset);

        if (!this.getMagic().equals(RIRecord.MAGIC)) {
            throw new RegistryParseException("RIRecord invalid magic header, expected \"ri\", got: " + this.getMagic());
        }
    }

    /**
     * getSubkeyLists returns an iterator over each of the SubkeyLists pointed to by this RIRecord.
     *   Since an RIRecord is like an indirect block, this iterates over an instance's direct blocks.
     * @return an iterator over each of the SubkeyLists pointed to by this RIRecord.
     */
    private Iterator<SubkeyList> getSubkeyLists() {
        return new Iterator<SubkeyList>() {
            private int _index = 0;
            private int _max_index = RIRecord.this.getListLength();
            private SubkeyList _next = null;

            @Override
            public boolean hasNext() {
                if (this._index + 1 > this._max_index) {
                    return false;
                }

                int offset = (int)(RIRecord.this.getDword(RIRecord.LIST_START_OFFSET + (this._index * RIRecord.LIST_ENTRY_SIZE)));
                int parent_offset = REGFHeader.FIRST_HBIN_OFFSET + offset;
                Cell c = new Cell(RIRecord.this._buf, parent_offset);
                try {
                    this._next = c.getSubkeyList();
                } catch (RegistryParseException e) {
                    return false;
                }
                return true;
            }

            @Override
            public SubkeyList next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No more subkey lists in the subkey lists list");
                }
                this._index++;
                return this._next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported for subkey list lists");
            }
        };
    }

    public Iterator<NKRecord> getSubkeys() {
        return new Iterator<NKRecord>() {
            private Iterator<SubkeyList> _lists = RIRecord.this.getSubkeyLists();
            private Iterator<NKRecord> _current_list = null;
            private NKRecord _next = null;
            // hasNext needs to have no visible side effects, so this flag tells hasNext
            //   if it should update the _next reference.
            private boolean _next_was_fetched = false;
            // if _next_was_fetched is false, then we'll use this variable to return
            //   the previously computed result. This means there were repeated calls
            //   to .hasNext().
            private boolean _cached_has_next = false;

            @Override
            public boolean hasNext() {
                // this is initial setup that should only happen once
                if (this._current_list == null) {
                    if (this._lists.hasNext()) {
                        SubkeyList list_record = this._lists.next();
                        this._current_list = list_record.getSubkeys();
                    } else {
                        this._cached_has_next = false;
                        return false;
                    }
                }
                // this is initial setup that should only happen once
                //   note the early return, since we've primed the iterator
                if (this._next == null) {
                    // skip lists that have no entries
                    while ( ! this._current_list.hasNext()) {
                        if (this._lists.hasNext()) {
                            SubkeyList list_record = this._lists.next();
                            this._current_list = list_record.getSubkeys();
                        } else {
                            this._cached_has_next = false;
                            return false;
                        }
                    }
                    this._next = this._current_list.next();

                    this._cached_has_next = true;
                    return true;
                }

                // can't blindly iterate the list every .hasNext() call.
                //   Have to go in sync with the calls to .next().
                if ( ! this._next_was_fetched) {
                    return this._cached_has_next;
                }
                this._next_was_fetched = false;

                // at this point, _current_list should be set to the list in-use
                // and _next should point to the NKRecord already returned by .next()

                if (this._current_list.hasNext()) {
                    this._next = this._current_list.next();
                    this._cached_has_next = true;
                    return true;
                } else {
                    // skip lists that have no entries
                    while ( ! this._current_list.hasNext()) {
                        if (this._lists.hasNext()) {
                            SubkeyList list_record = this._lists.next();
                            this._current_list = list_record.getSubkeys();
                        } else {
                            // this will be the case that will most likely
                            //   end this iterator
                            this._cached_has_next = false;
                            return false;
                        }
                    }
                    this._next = this._current_list.next();

                    this._cached_has_next = true;
                    return true;
                }
            }

            @Override
            public NKRecord next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No more subkeys in the subkey list");
                }
                this._next_was_fetched = true;
                return this._next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported for subkey lists");
            }
        };
    }
}
