package org.l2j.commons;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class ByteBufferTest {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(50);
        buffer.putInt(20);
        buffer.putLong(80);
        buffer.putFloat(90);
        buffer.put("String".getBytes(StandardCharsets.UTF_16BE));
        buffer.putChar('\000');
        int position = buffer.position();
        byte[] array = new byte[position];
        buffer.rewind();
        buffer.get(array);
        buffer.flip();
        int i = buffer.getInt();
        long l = buffer.getLong();
        float f = buffer.getFloat();
        StringBuilder b = new StringBuilder();
        char c;
        while((c = buffer.getChar()) != '\000') {
            b.append(c);
        }
        String s = b.toString();
        System.out.println(s);
    }
}
