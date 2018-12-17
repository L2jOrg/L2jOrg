package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

@StaticPacket
public class ExEnSoulExtractionShow extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExEnSoulExtractionShow();

	private ExEnSoulExtractionShow() { }

	@Override
	protected final void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}