package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

/**
 * Close the CommandChannel Information window
 */
@StaticPacket
public class ExCloseMPCCPacket extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExCloseMPCCPacket();

	private ExCloseMPCCPacket() { }

	@Override
	protected void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}
