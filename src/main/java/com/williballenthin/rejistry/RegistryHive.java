package com.williballenthin.rejistry;

public interface RegistryHive {
    /**
     * getRoot fetches the root RegistryKey of the hive.
     *
     * @return The root of the hive.
     */
    public RegistryKey getRoot() throws RegistryParseException;

    /**
     * getHeader fetches the REGFHeader that defined metadata
     * for this Registry hive.
     *
     * @return The header of the hive.
     */
    public REGFHeader getHeader() throws RegistryParseException;
}
