package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

/**
 * Opens the CommandChannel Information window
 */
@StaticPacket
public class ExOpenMPCCPacket extends L2GameServerPacket  {
	public static final L2GameServerPacket STATIC = new ExOpenMPCCPacket();

	private ExOpenMPCCPacket() { }

	@Override
	protected void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}