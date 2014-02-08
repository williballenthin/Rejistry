package com.williballenthin.rejistry;

import com.williballenthin.rejistry.record.VKRecord;

import java.io.UnsupportedEncodingException;

public class RegistryValue {
    private final VKRecord _vk;

    public RegistryValue(VKRecord vk) {
        this._vk = vk;
    }

    public String getName() throws UnsupportedEncodingException {
        return this._vk.getName();
    }

    public RegistryValueType getValueType() throws RegistryParseException {
        return this._vk.getValueType();
    }

    public ValueData getValue() throws UnsupportedEncodingException, RegistryParseException {
        return this._vk.getValue();
    }
}
