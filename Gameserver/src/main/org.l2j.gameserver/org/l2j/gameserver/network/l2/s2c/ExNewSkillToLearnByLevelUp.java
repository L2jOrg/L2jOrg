package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

@StaticPacket
public class ExNewSkillToLearnByLevelUp extends L2GameServerPacket  {
	public static final L2GameServerPacket STATIC = new ExNewSkillToLearnByLevelUp();

	private ExNewSkillToLearnByLevelUp()  {  }

	@Override
	protected void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}
