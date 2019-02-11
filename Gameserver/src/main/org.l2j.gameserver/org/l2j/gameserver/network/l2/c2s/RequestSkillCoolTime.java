package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.SkillCoolTimePacket;

import java.nio.ByteBuffer;

public class RequestSkillCoolTime extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{

	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		player.sendPacket(new SkillCoolTimePacket(player));
	}
}