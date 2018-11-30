package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
@StaticPacket
public class ExPledgeWaitingListAlarm extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExPledgeWaitingListAlarm();

	private ExPledgeWaitingListAlarm() { }

	@Override
	protected void writeImpl() {  }
}