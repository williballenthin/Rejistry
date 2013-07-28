package com.williballenthin.rejistry;

import com.williballenthin.rejistry.record.NKRecord;
import com.williballenthin.rejistry.record.VKRecord;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class RegistryKey {
    private final NKRecord _nk;

    public RegistryKey(NKRecord nk) {
        this._nk = nk;
    }

    public Calendar getTimestamp() {
        return this._nk.getTimestamp();
    }

    public String getName() throws UnsupportedEncodingException {
        return this._nk.getName();
    }

    public RegistryKey getParent() throws RegistryParseException {
        if ( ! this._nk.hasParentRecord()) {
            throw new NoSuchElementException("Registry Key has no parent");
        }
        return new RegistryKey(this._nk.getParentRecord());
    }

    public List<RegistryKey> getSubkeyList() throws RegistryParseException {
        LinkedList<RegistryKey> l = new LinkedList<RegistryKey>();
        Iterator<NKRecord> nkit = this._nk.getSubkeyList().getSubkeys();

        while (nkit.hasNext()) {
            l.add(new RegistryKey(nkit.next()));
        }
        return l;
    }

    public RegistryKey getSubkey(String name) throws RegistryParseException {
        return new RegistryKey(this._nk.getSubkeyList().getSubkey(name));
    }

    public List<RegistryValue> getValueList() throws RegistryParseException {
        LinkedList<RegistryValue> l = new LinkedList<RegistryValue>();
        Iterator<VKRecord> vkit = this._nk.getValueList().getValues();

        while (vkit.hasNext()) {
            l.add(new RegistryValue(vkit.next()));
        }
        return l;
    }

    public RegistryValue getValue(String name) throws RegistryParseException {
        return new RegistryValue(this._nk.getValueList().getValue(name));
    }
}
