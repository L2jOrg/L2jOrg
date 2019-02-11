package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PledgeSkillListAddPacket extends L2GameServerPacket
{
	private int _skillId;
	private int _skillLevel;

	public PledgeSkillListAddPacket(int skillId, int skillLevel)
	{
		_skillId = skillId;
		_skillLevel = skillLevel;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_skillId);
		buffer.putInt(_skillLevel);
	}
}