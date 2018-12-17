package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

/**
 * Открывает окно аугмента, название от фонаря.
 */
@StaticPacket
public class ExShowVariationMakeWindow extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExShowVariationMakeWindow();

	private ExShowVariationMakeWindow() { }

	@Override
	protected final void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}