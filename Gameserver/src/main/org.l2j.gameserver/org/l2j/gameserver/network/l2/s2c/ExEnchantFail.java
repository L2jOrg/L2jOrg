package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
@StaticPacket
public final class ExEnchantFail extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExEnchantFail(0, 0);

	private final int _itemOne;
	private final int _itemTwo;

	public ExEnchantFail(int itemOne, int itemTwo) {
		_itemOne = itemOne;
		_itemTwo = itemTwo;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer) {
		buffer.putInt(_itemOne);
		buffer.putInt(_itemTwo);
	}

	@Override
	protected int size(GameClient client) {
		return 13;
	}
}