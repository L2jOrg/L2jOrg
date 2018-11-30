package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

@StaticPacket
public class ExMailArrivedPacket extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExMailArrivedPacket();

	private ExMailArrivedPacket() { }

	@Override
	protected final void writeImpl() {  }
}