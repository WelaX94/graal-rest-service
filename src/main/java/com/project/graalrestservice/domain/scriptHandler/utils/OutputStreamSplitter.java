package com.project.graalrestservice.domain.scriptHandler.utils; // NOSONAR

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Class extends the output stream and needed to divide one OutputStream into several independent of
 * each other.
 */
public class OutputStreamSplitter extends OutputStream {

  public static final Logger logger = LoggerFactory.getLogger(OutputStreamSplitter.class);
  private final Set<OutputStream> streamSet = new CopyOnWriteArraySet<>();
  /**
   * If true, then the {@link #flush()} method will be automatically called after writing a new byte
   * to the stream
   */
  private boolean autoFlushable = true;

  /**
   * Method to write byte to set of streams. There must always be at least one stream in a
   * {@link #streamSet}, otherwise an {@link IOException exception} will be thrown. Since there is
   * no way to control the strips added for recording, if an {@link IOException} is thrown, the
   * guilty stream will be removed from the {@link #streamSet}. If {@link #autoFlushable}=true, then
   * the {@link #flush()} method will be automatically called after writing a new byte to the stream
   * 
   * @param b byte to write
   * @throws IOException if {@link #streamSet} is empty
   */
  @Override
  public void write(int b) throws IOException {
    for (OutputStream outputStream : streamSet) {
      try {
        outputStream.write(b);
        if (autoFlushable)
          outputStream.flush();
      } catch (IOException e) {
        handleIOException(e, outputStream);
      }
    }
  }

  /**
   * Writes len bytes from the specified byte array starting at offset off to this output stream.
   * The rest of the logic is the same as {@link #write(int) this method}
   * 
   * @param b the data.
   * @param off the start offset in the data.
   * @param len the number of bytes to write.
   * @throws IOException if streamSet is empty
   */
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    for (OutputStream outputStream : streamSet) {
      try {
        outputStream.write(b, off, len);
        if (autoFlushable)
          outputStream.flush();
      } catch (IOException e) {
        handleIOException(e, outputStream);
      }
    }
  }

  /**
   * Handles {@link IOException} thrown from {@link #write(int) this} or
   * {@link #write(byte[], int, int) that} methods.
   * 
   * @param e processing {@link IOException}
   * @param outputStream {@link OutputStream} from which the exception was thrown
   * @throws IOException if streamSet is empty
   */
  private void handleIOException(IOException e, OutputStream outputStream) throws IOException {
    deleteStream(outputStream);
    logger.warn("[{}] - Stream recording error ({}). It will be removed from the stream list",
        MDC.get("scriptName"), e.getMessage());
    if (streamSet.isEmpty())
      throw new IOException("OutputStreamSplitter: no streams for recording");
  }

  public void setAutoFlushable(boolean autoFlushable) {
    this.autoFlushable = autoFlushable;
  }

  public boolean isAutoFlushable() {
    return autoFlushable;
  }

  public boolean addStream(OutputStream outputStream) {
    return streamSet.add(outputStream);
  }

  public boolean deleteStream(OutputStream outputStream) {
    return streamSet.remove(outputStream);
  }

  public int currentNumberOfStreams() {
    return streamSet.size();
  }

  public void deleteAllStreams() {
    streamSet.clear();
  }

}
