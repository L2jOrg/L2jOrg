package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

@StaticPacket
public class CharacterCreateSuccessPacket extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new CharacterCreateSuccessPacket();

	private CharacterCreateSuccessPacket() { }

	@Override
	protected final void writeImpl()
	{
		writeInt(0x01);
	}

	@Override
	protected int packetSize() {
		return 9;
	}
}