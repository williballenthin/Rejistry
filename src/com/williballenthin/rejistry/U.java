package com.williballenthin.rejistry;

class U {
    public static void d(String s) {
        System.out.println(s);
    }

    public static String hex(int i) {
        return String.format("%x", i);
    }

    public static void hex_out(int i) {
        d(hex(i));
    }
}
