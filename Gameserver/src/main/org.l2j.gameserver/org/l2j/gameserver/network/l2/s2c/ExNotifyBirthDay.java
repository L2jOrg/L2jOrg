package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

@StaticPacket
public class ExNotifyBirthDay extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExNotifyBirthDay();

	private ExNotifyBirthDay() { }

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0); // Actor OID
	}

	@Override
	protected int size(GameClient client) {
		return 9;
	}
}