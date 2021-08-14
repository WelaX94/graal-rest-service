package com.project.graalrestservice.domain.utils;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Class extends the output stream and works on the principle of Circular buffer
 */
public class CircularOutputStream extends OutputStream {

    private final byte[] buf;
    private final int capacity;
    private int position = 0;
    private boolean completed = false;
    private final boolean readable;
    private ArrayBlockingQueue<Byte> queue;

    /**
     * Basic constructor
     * @param capacity stream capacity
     * @param readable parameter indicates whether the logs will be read in real time or not
     */
    public CircularOutputStream(int capacity, boolean readable) {
        buf = new byte[capacity];
        this.capacity = capacity;
        this.readable = readable;
        if (readable) this.queue = new ArrayBlockingQueue<>(capacity);

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
        if (readable) {
            try {
                queue.put((byte) b);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    /**
     * The method returns the following bytes for reading in real time
     *
     * @return the following bytes for reading
     */
    public byte[] getNextBytes() throws InterruptedException {
        int size = queue.size();
        byte[] output = new byte[size];
        for (int i = 0; i < size; i++) {
            output[i] = queue.take();
        }
        return output;
    }

    /**
     * A method for understanding whether all bytes have been read or not (needs only for real time reading)
     *
     * @return true if bytes are present and false if not
     */
    public synchronized boolean isReadComplete() {
        return queue.isEmpty();
    }

}