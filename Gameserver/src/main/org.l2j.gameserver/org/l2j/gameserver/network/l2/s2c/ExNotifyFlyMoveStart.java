package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

@StaticPacket
public final class ExNotifyFlyMoveStart extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExNotifyFlyMoveStart();

	private ExNotifyFlyMoveStart() { }

	@Override
	protected void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}