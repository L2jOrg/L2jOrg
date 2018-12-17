package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

/**
 * @author Bonux
**/
@StaticPacket
public final class ExEnchantOneRemoveOK extends L2GameServerPacket  {
	public static final L2GameServerPacket STATIC = new ExEnchantOneRemoveOK();

	private ExEnchantOneRemoveOK() { }

	@Override
	protected void writeImpl() { }

	@Override
	protected int packetSize() {
		return 5;
	}
}