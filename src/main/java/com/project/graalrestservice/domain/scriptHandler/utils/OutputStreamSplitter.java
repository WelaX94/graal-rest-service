package com.project.graalrestservice.domain.scriptHandler.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OutputStreamSplitter extends OutputStream {

    private final Set<OutputStream> streamSet = new ConcurrentHashMap<OutputStream, Object>().keySet();

    @Override
    public void write(int b) throws IOException {
        for(OutputStream outputStream: streamSet) {
            outputStream.write(b);
        }
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
