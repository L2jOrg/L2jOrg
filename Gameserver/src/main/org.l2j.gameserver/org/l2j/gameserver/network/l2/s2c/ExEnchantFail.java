package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

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
	protected void writeImpl() {
		writeInt(_itemOne);
		writeInt(_itemTwo);
	}

	@Override
	protected int packetSize() {
		return 13;
	}
}