package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

@StaticPacket
public class ExShowQuestInfoPacket extends L2GameServerPacket  {
	public static final L2GameServerPacket STATIC = new ExShowQuestInfoPacket();

	private ExShowQuestInfoPacket() { }

	@Override
	protected final void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}

}