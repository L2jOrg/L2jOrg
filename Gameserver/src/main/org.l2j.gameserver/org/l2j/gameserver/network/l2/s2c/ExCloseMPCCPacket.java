package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * Close the CommandChannel Information window
 */
@StaticPacket
public class ExCloseMPCCPacket extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExCloseMPCCPacket();

	private ExCloseMPCCPacket() { }

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer) {  }

	@Override
	protected int size(GameClient client) {
		return 5;
	}
}
