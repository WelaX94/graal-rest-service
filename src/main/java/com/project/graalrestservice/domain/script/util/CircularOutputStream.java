package com.project.graalrestservice.domain.script.util;

import java.io.OutputStream;
import java.util.Arrays;

/**
 * Class extends the output stream and works on the principle of Circular buffer
 */
public class CircularOutputStream extends OutputStream {

  private final byte[] buf;
  private final byte[] temp = new byte[1];
  private final int capacity;
  private int position = 0;
  private boolean completed = false;

  /**
   * Basic constructor
   *
   * @param capacity stream capacity
   */
  public CircularOutputStream(int capacity) {
    this.buf = new byte[capacity];
    this.capacity = capacity;
  }

  /**
   * Method to write byte to stream.
   *
   * @param b byte to write
   */
  @Override
  public synchronized void write(int b) {
    this.temp[0] = (byte) b;
    write(temp, 0, 1);
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
  public synchronized void write(byte[] b, int off, int len) {
    if (len >= this.capacity) {
      System.arraycopy(b, len - this.capacity, this.buf, 0, this.capacity);
      this.completed = true;
      this.position = 0;
    } else if (this.capacity >= this.position + len) {
      System.arraycopy(b, off, this.buf, this.position, len);
      this.position += len;
      if (this.capacity == this.position) {
        this.completed = true;
        this.position = 0;
      }
    } else {
      int lenFirstPart = (this.capacity - this.position);
      System.arraycopy(b, off, this.buf, this.position, lenFirstPart);
      int lenSecondPart = len - lenFirstPart;
      System.arraycopy(b, off + lenFirstPart, this.buf, 0, lenSecondPart);
      this.position = lenSecondPart == this.capacity ? 0 : lenSecondPart;
      this.completed = true;
    }

  }

  /**
   * The method returns the content of the stream as a string
   *
   * @return the content of the stream
   */
  @Override
  public String toString() {
    return this.completed ? new String(toByteArray(), 0, this.capacity)
        : new String(toByteArray(), 0, this.position);
  }

  /**
   * The method returns an array of stream content in the correct order
   *
   * @return array of stream content in the correct order
   */
  private synchronized byte[] toByteArray() {
    if (!this.completed)
      return Arrays.copyOf(this.buf, this.position);
    else {
      byte[] result = new byte[this.capacity];
      int i = this.position;
      for (int k = 0; k < this.capacity; i++, k++) {
        if (i == this.capacity)
          i = 0;
        result[k] = this.buf[i];
      }
      return result;
    }
  }

}
