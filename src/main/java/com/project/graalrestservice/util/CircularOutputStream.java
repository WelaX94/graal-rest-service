package com.project.graalrestservice.util;

import java.io.OutputStream;
import java.util.Arrays;

public class CircularOutputStream extends OutputStream{

    private final byte[] buf;
    private final int capacity;
    private int position = 0;
    private boolean completed = false;

    public CircularOutputStream(int capacity) {
        buf = new byte[capacity];
        this.capacity = capacity;
    }

    @Override
    public synchronized void write(int b) {
        if (capacity == position) {
            completed = true;
            position = 0;
        }
        buf[position++] = (byte)b;
    }

    @Override
    public synchronized String toString() {
        return new String(toByteArray(), 0, toByteArray().length);
    }

    private byte[] toByteArray() {
        if (!completed)
            return Arrays.copyOf(buf, position);
        byte[] result = new byte[capacity];
        System.arraycopy(buf, position, result, 0, capacity - position);
        System.arraycopy(buf, 0, result, capacity - position, position);
        return result;
    }

}