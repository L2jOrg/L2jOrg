package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

@StaticPacket
public class ExNotifyPremiumItem extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExNotifyPremiumItem();

	private ExNotifyPremiumItem() { }

	@Override
	protected void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}