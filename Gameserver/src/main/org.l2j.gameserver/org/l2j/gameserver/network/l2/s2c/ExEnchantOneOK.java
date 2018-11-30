package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

/**
 * @author Bonux
**/
@StaticPacket
public final class ExEnchantOneOK extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExEnchantOneOK();

	private ExEnchantOneOK() {  }

	@Override
	protected void writeImpl() { }
}