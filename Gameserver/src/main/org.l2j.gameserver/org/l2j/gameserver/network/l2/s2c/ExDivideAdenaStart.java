package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

/**
 * @author Erlandys
 */
@StaticPacket
public class ExDivideAdenaStart extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExDivideAdenaStart();

	private ExDivideAdenaStart() { }

	@Override
	protected final void writeImpl() {  }
}