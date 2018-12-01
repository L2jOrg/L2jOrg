package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

@StaticPacket
public class ExShowVariationCancelWindow extends L2GameServerPacket  {
	public static final L2GameServerPacket STATIC = new ExShowVariationCancelWindow();

	private ExShowVariationCancelWindow() { }

	@Override
	protected final void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}