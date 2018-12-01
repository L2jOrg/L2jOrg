package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

/**
 * @author Bonux
**/
@StaticPacket
public final class ExEnchantTwoOK extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExEnchantTwoOK();

	private ExEnchantTwoOK() { }

	@Override
	protected void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}