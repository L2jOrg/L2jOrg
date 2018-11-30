package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

/**
 * Reworked: VISTALL
 */
@StaticPacket
public class AcquireSkillDonePacket extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new AcquireSkillDonePacket();

	private AcquireSkillDonePacket() { }

	@Override
	protected void writeImpl() {  }
}