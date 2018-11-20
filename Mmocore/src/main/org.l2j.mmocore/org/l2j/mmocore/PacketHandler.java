package org.l2j.mmocore;

public interface PacketHandler<T extends Client<Connection<T>>> {

	ReadablePacket<T> handlePacket(DataWrapper data, T client);
}
