package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

/**
 * @author Erlandys
 */
@StaticPacket
public class ExDivideAdenaCancel extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExDivideAdenaCancel();

	private ExDivideAdenaCancel() { }

	@Override
	protected final void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}