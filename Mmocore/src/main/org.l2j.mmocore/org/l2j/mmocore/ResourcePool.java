package org.l2j.mmocore;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.nonNull;

class ResourcePool {

    private static final Map<Integer, Queue<ByteBuffer>> buffers = new HashMap<>();

    static int bufferSize;
    private static ConnectionConfig config;

    static ByteBuffer getPooledBuffer() {
        return getSizedBuffer(config.bufferDefaultSize);
    }

    static ByteBuffer getPooledBuffer(int size) {
        size = determineBufferSize(size);
        return getSizedBuffer(size);
    }

    private static ByteBuffer getSizedBuffer(int size) {
        ByteBuffer buffer = buffers.get(size).poll();
        return nonNull(buffer) ? buffer : ByteBuffer.allocateDirect(size).order(config.byteOrder);
    }

    private static int determineBufferSize(int size) {
        if(size <= config.bufferMinSize) {
            size = config.bufferMinSize;
        } else if( size <= config.bufferMediumSize) {
            size = config.bufferMediumSize;
        } else if( size <= config.bufferLargeSize) {
            size = config.bufferLargeSize;
        } else {
            size = config.bufferDefaultSize;
        }
        return size;
    }

    static void recycleBuffer(ByteBuffer buffer) {
        if(nonNull(buffer)) {
            int size = buffer.capacity();
            int poolSize = determinePoolSize(size);
            Queue<ByteBuffer> queue = buffers.get(buffer.capacity());
            if(queue.size() < poolSize) {
                buffer.clear();
                queue.add(buffer);
            }
        }
    }

    private static int determinePoolSize(int size) {
        int poolSize = config.bufferPoolSize;
        if(size == config.bufferMinSize) {
            poolSize = config.bufferMinPoolSize;
        } else if( size == config.bufferMediumSize) {
            poolSize = config.bufferMediumPoolSize;
        } else if( size == config.bufferLargePoolSize) {
            poolSize = config.bufferLargePoolSize;
        }
        return poolSize;
    }

    public static  void initialize(ConnectionConfig config) {
        ResourcePool.config = config;
        bufferSize = config.bufferDefaultSize;
        buffers.put(config.bufferDefaultSize, new ConcurrentLinkedQueue<>());
        buffers.put(config.bufferMinSize, new ConcurrentLinkedQueue<>());
        buffers.put(config.bufferMediumSize, new ConcurrentLinkedQueue<>());
        buffers.put(config.bufferLargeSize, new ConcurrentLinkedQueue<>());
    }
}
