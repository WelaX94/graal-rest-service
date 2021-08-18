package com.project.graalrestservice.domain.scriptHandler.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class QueueOutputStream extends OutputStream {

    private final ArrayBlockingQueue<Byte> queue;

    public QueueOutputStream(int capacity) {
        queue = new ArrayBlockingQueue<Byte>(capacity);
    }

    @Override
    public void write(int b) throws IOException {
        try {
            queue.put((byte)b);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public byte[] readBytes() {
        int size = queue.size();
        byte [] output = new byte[size];
        for(int i = 0; i < size; i++) {
            output[i] = queue.remove();
        }
        return output;
    }

    public void clearStream(){
        queue.clear();
    }

    public boolean hasNextByte() {
        return !queue.isEmpty();
    }

}
