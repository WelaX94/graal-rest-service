package com.project.graalrestservice.domain.utils;

import java.io.OutputStream;
import java.util.Arrays;

/**
 * Class extends the output stream and works on the principle of Circular buffer
 */
public class CircularOutputStream extends OutputStream{

    private final byte[] buf;
    private final int capacity;
    private int position = 0;
    private boolean completed = false;
    private int readPosition = 0;
    private boolean readComplete = true;

    /**
     * Basic constructor
     * @param capacity stream capacity
     */
    public CircularOutputStream(int capacity) {
        buf = new byte[capacity];
        this.capacity = capacity;
    }

    /**
     * Method to write byte to stream
     * @param b byte to write
     */
    @Override
    public synchronized void write(int b) {
        readComplete = false;
        if (capacity == position) {
            completed = true;
            position = 0;
        }
        buf[position++] = (byte)b;
    }

    /**
     * The method returns the content of the stream as a string
     * @return the content of the stream
     */
    @Override
    public String toString() {
        return completed ? new String(toByteArray(), 0, capacity) : new String(toByteArray(), 0, position);
    }

    /**
     * The method returns an array of stream content in the correct order
     * @return array of stream content in the correct order
     */
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

    /**
     * The method returns the following byte for reading
     * @return the following byte for reading
     */
    public byte getNextByte() {
        if (readComplete) throw new ArrayIndexOutOfBoundsException("Read has already complete");
        byte currentByte = buf[readPosition++];
        if (readPosition == capacity) readPosition = 0;
        if (readPosition == position) readComplete = true;
        return currentByte;
    }

    /**
     * A method for understanding whether all bytes have been read or not
     * @return true if bytes are present and false if not
     */
    public synchronized boolean isReadComplete() {
        return readComplete;
    }

}