package com.williballenthin.rejistry;

public class U {
    public static final void d(String s) {
        System.out.println(s);
    }

    public static final String hex(int i) {
        return String.format("%x", i);
    }

    public static final void hex_out(int i) {
        d(hex(i));
    }
}
