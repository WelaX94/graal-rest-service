package com.project.graalrestservice.domain.scriptHandler.utils;

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
