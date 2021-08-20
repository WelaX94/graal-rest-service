package com.project.graalrestservice.domain.scriptHandler.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class OutputStreamSplitter extends OutputStream {

    public static Logger logger = LoggerFactory.getLogger(OutputStreamSplitter.class);
    private final Set<OutputStream> streamSet = new CopyOnWriteArraySet<>();
    private boolean autoFlushable = true;

    @Override
    public void write(int b) throws IOException {
        for(OutputStream outputStream: streamSet) {
            try {
                outputStream.write(b);
                if (autoFlushable) outputStream.flush();
            } catch (IOException e) {
                deleteStream(outputStream);
                logger.warn("[{}] - Stream recording error. It will be removed from the stream list", MDC.get("scriptName"));
                if (streamSet.isEmpty()) throw new IOException("OutputStreamSplitter: no streams for recording");
            }
        }
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
