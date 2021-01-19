package com.xie.song.XUNET.test;

import com.xie.song.XUNET.Bootstrap;

public class Test3 {
    public static void main(String[] args) {
//        Bootstrap.start(Test2.class);

        System.out.println(getDataLength(addBytesByLength(-190123456)));

    }

    public static int getDataLength(byte[] bytes) {
        int target = 0;
        target = bytes[0] << 24 | (bytes[1] << 24) >>> 8
                | (bytes[2] << 8) & 0xff00 | bytes[3] & 0xff;
        return target;
    }

    public static byte[] addBytesByLength(int length) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (length & 0xff);
        bytes[2] = (byte) (length >> 8 & 0xff);
        bytes[1] = (byte) (length >> 16 & 0xff);
        bytes[0] = (byte) (length >> 24 & 0xff);
        return bytes;
    }
}
