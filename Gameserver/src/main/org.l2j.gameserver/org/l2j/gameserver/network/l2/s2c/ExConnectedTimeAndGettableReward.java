package org.l2j.gameserver.network.l2.s2c;


import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

// this packet cause onedayreward menu displaying
@StaticPacket
public class ExConnectedTimeAndGettableReward extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExConnectedTimeAndGettableReward();

	private ExConnectedTimeAndGettableReward() { }

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {
		buffer.putInt(0x00);       // unk 1
		buffer.putInt(0x00);       // unk 2
		buffer.putInt(0x00);       // unk 3
		buffer.putInt(0x00);       // unk 4
		buffer.putInt(0x00);       // unk 5
		buffer.putInt(0x00);       // unk 6
		buffer.putInt(0x00);       // unk 7
		buffer.putInt(0x00);       // unk 8
		buffer.putInt(0x00);       // unk 9
		buffer.putInt(0x00);       // unk 10
		buffer.putInt(0x00);       // unk 11
	}

	@Override
	protected int size(GameClient client) {
		return 49;
	}
}