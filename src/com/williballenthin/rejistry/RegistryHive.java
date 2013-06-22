package com.williballenthin.rejistry;

import java.nio.ByteBuffer;

public interface RegistryHive {
	public RegistryKey getRoot();
	public ByteBuffer getBuf();

}
