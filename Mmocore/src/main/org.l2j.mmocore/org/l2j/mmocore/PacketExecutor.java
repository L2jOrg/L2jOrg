package org.l2j.mmocore;

public interface PacketExecutor<T> {

	void execute(ReadablePacket<T> packet);
}
