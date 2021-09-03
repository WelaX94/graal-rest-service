package com.project.graalrestservice.web.util;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.io.OutputStream;

public class EmitterOutputStream extends OutputStream {

  private final ResponseBodyEmitter rbe;
  private final byte[] symbol = new byte[1];

  public EmitterOutputStream(ResponseBodyEmitter rbe) {
    this.rbe = rbe;
  }

  @Override
  public synchronized void write(byte[] b, int off, int len) throws IOException {
    rbe.send(new String(b, off, len));
  }

  @Override
  public synchronized void write(int b) throws IOException {
    this.symbol[0] = (byte) b;
    write(symbol, 0, 1);
  }

  @Override
  public synchronized void close() {
    rbe.complete();
  }

}
