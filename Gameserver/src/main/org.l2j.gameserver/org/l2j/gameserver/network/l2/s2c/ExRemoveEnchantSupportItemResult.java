package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

@StaticPacket
public class ExRemoveEnchantSupportItemResult extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExRemoveEnchantSupportItemResult();

	private ExRemoveEnchantSupportItemResult() { }

	@Override
	protected void writeImpl() { }

	@Override
	protected int packetSize() {
		return 5;
	}
}