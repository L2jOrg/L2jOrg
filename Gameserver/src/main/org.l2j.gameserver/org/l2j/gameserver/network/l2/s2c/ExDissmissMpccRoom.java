package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
@StaticPacket
public class ExDissmissMpccRoom extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExDissmissMpccRoom();

	private ExDissmissMpccRoom() { }

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer) {  }

	@Override
	protected int size(GameClient client) {
		return 5;
	}
}