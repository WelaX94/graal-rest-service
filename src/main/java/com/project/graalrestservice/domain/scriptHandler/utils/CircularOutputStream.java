package com.project.graalrestservice.domain.scriptHandler.utils;  //NOSONAR

import org.springframework.lang.NonNull;

import java.io.OutputStream;
import java.util.Arrays;

/**
 * Class extends the output stream and works on the principle of Circular buffer
 */
public class CircularOutputStream extends OutputStream {

  private final byte[] buf;
  private final int capacity;
  private int position = 0;
  private boolean completed = false;

  /**
   * Basic constructor
   * 
   * @param capacity stream capacity
   */
  public CircularOutputStream(int capacity) {
    buf = new byte[capacity];
    this.capacity = capacity;
  }

  /**
   * Method to write byte to stream. If the capacity runs out, it starts overwriting in a circle.
   * 
   * @param b byte to write
   */
  @Override
  public synchronized void write(int b) {
    if (capacity == position) {
      completed = true;
      position = 0;
    }
    buf[position++] = (byte) b;
  }

  /**
   * Writes len bytes from the specified byte array starting at offset off to this output stream. If
   * the capacity runs out, it starts overwriting in a circle.
   * 
   * @param b the data.
   * @param off the start offset in the data.
   * @param len the number of bytes to write.
   */
  @Override
  public synchronized void write(@NonNull byte[] b, int off, int len) {
    for (int i = 0; i < len; i++) {
      write(b[off++]);
    }
  }

  /**
   * The method returns the content of the stream as a string
   *
   * @return the content of the stream
   */
  @Override
  public String toString() {
    return completed ? new String(toByteArray(), 0, capacity)
        : new String(toByteArray(), 0, position);
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
      int i = position;
      for (int k = 0; k < capacity; i++, k++) {
        if (i == capacity)
          i = 0;
        result[k] = buf[i];
      }
      return result;
    }
  }

}
