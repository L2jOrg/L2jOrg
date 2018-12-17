package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

/**
 * @author VISTALL
 * @date 11:33/03.07.2011
 */
@StaticPacket
public class ExGoodsInventoryChangedNotify extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExGoodsInventoryChangedNotify();

	private ExGoodsInventoryChangedNotify() { }

	@Override
	protected void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}
