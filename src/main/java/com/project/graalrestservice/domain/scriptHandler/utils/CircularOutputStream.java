package com.project.graalrestservice.domain.scriptHandler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.util.Arrays;

/**
 * Class extends the output stream and works on the principle of Circular buffer
 */
public class CircularOutputStream extends OutputStream {

    private static final Logger logger = LogManager.getLogger(CircularOutputStream.class);
    private final byte[] buf;
    private final int capacity;
    private int position = 0;
    private boolean completed = false;

    /**
     * Basic constructor
     * @param capacity stream capacity
     * @param realTimeReading parameter indicates whether the logs will be read in real time or not
     */
    public CircularOutputStream(int capacity) {
        buf = new byte[capacity];
        this.capacity = capacity;

    }

    /**
     * Method to write byte to stream
     *
     * @param b byte to write
     */
    @Override
    public void write(int b) {
        synchronized (this) {
            if (capacity == position) {
                completed = true;
                position = 0;
            }
            buf[position++] = (byte) b;
        }
    }

    /**
     * The method returns the content of the stream as a string
     *
     * @return the content of the stream
     */
    @Override
    public String toString() {
        return completed ? new String(toByteArray(), 0, capacity) : new String(toByteArray(), 0, position);
    }

    /**
     * The method returns an array of stream content in the correct order
     *
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

}