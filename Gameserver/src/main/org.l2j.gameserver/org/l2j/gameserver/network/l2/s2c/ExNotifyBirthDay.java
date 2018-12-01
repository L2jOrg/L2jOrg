package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

@StaticPacket
public class ExNotifyBirthDay extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExNotifyBirthDay();

	private ExNotifyBirthDay() { }

	@Override
	protected void writeImpl()
	{
		writeInt(0); // Actor OID
	}

	@Override
	protected int packetSize() {
		return 9;
	}
}