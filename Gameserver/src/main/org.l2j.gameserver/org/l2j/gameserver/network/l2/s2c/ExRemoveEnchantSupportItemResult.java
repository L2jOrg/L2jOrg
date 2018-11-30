package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

@StaticPacket
public class ExRemoveEnchantSupportItemResult extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExRemoveEnchantSupportItemResult();

	private ExRemoveEnchantSupportItemResult() { }

	@Override
	protected void writeImpl() { }
}