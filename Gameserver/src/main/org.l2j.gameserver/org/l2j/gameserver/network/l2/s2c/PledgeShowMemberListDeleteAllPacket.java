package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

@StaticPacket
public class PledgeShowMemberListDeleteAllPacket extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new PledgeShowMemberListDeleteAllPacket();

	private PledgeShowMemberListDeleteAllPacket() { }

	@Override
	protected final void writeImpl() { }

	@Override
	protected int packetSize() {
		return 3;
	}
}