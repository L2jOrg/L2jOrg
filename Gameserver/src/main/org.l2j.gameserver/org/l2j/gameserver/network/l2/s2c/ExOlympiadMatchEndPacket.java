package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

@StaticPacket
public class ExOlympiadMatchEndPacket extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExOlympiadMatchEndPacket();

	private ExOlympiadMatchEndPacket() { }

	@Override
	protected void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}