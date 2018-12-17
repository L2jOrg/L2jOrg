package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

/**
 * @author Bonux
 */
@StaticPacket
public class ExChangeAttributeOk extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExChangeAttributeOk();

	private ExChangeAttributeOk() { }

	protected void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}