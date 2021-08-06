package com.project.graalrestservice.domain.utils;

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
    public String toString() {
        return completed ? new String(toByteArray(), 0, capacity) : new String(toByteArray(), 0, position);
    }

    private synchronized byte[] toByteArray() {
        if (!completed)
            return Arrays.copyOf(buf, position);
        else {
            byte[] result = new byte[capacity];
            for (int i = position, k = 0; k < capacity; i++, k++) {
                if (i == capacity) i = 0;
                result[k] = buf[i];
            }
            return result;
        }
    }

}