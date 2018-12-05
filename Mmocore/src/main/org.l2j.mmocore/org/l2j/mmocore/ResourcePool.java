package org.l2j.mmocore;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

class ResourcePool {

    static final int DEFAULT_BUFFER_SIZE = 4 * 1024;

    private final Map<Integer, Queue<ByteBuffer>> buffers = new HashMap<>();
    private final ConnectionConfig config;

    private ResourcePool(ConnectionConfig config) {
        this.config = config;
    }

    ByteBuffer getPooledBuffer() {
        return getSizedBuffer(config.bufferDefaultSize);
    }

    ByteBuffer getPooledBuffer(int size) {
        size = determineBufferSize(size);
        return getSizedBuffer(size);
    }

    private ByteBuffer getSizedBuffer(int size) {
        Queue<ByteBuffer> queue = queueFromSize(size);
        ByteBuffer buffer = queue.poll();
        return nonNull(buffer) ? buffer : ByteBuffer.allocateDirect(size).order(config.byteOrder);
    }

    private Queue<ByteBuffer> queueFromSize(int size) {
        Queue<ByteBuffer> queue = buffers.get(size);
        if(isNull(queue)) {
            queue = new ConcurrentLinkedQueue<>();
            buffers.put(size, queue);
        }
        return queue;
    }

    private int determineBufferSize(int size) {
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

    void recycleBuffer(ByteBuffer buffer) {
        if(nonNull(buffer)) {
            int size = buffer.capacity();
            int poolSize = determinePoolSize(size);
            Queue<ByteBuffer> queue = buffers.get(buffer.capacity());
            if(nonNull(queue) && queue.size() < poolSize) {
                buffer.clear();
                queue.add(buffer);
            }
        }
    }

    private int determinePoolSize(int size) {
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

    static ResourcePool initialize(ConnectionConfig config) {
        ResourcePool resourcePool = new ResourcePool(config);
        return resourcePool;
    }
}
