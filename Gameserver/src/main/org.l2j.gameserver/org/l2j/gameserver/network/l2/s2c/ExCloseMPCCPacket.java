package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

/**
 * Close the CommandChannel Information window
 */
@StaticPacket
public class ExCloseMPCCPacket extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExCloseMPCCPacket();

	private ExCloseMPCCPacket() { }

	@Override
	protected void writeImpl() {  }
}
