package org.l2j.mmocore;

import java.nio.channels.AsynchronousSocketChannel;

public interface ConnectionFilter {

	boolean accept(AsynchronousSocketChannel channel);
}
