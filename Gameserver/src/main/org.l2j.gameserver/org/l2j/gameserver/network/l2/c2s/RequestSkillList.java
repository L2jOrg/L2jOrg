package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.utils.MulticlassUtils;

import java.nio.ByteBuffer;

public final class RequestSkillList extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		// this is just a trigger packet. it has no content
	}

	@Override
	protected void runImpl()
	{
		Player cha = client.getActiveChar();
		if(cha != null)
		{
			cha.sendSkillList();
			if(Config.MULTICLASS_SYSTEM_SHOW_LEARN_LIST_ON_OPEN_SKILL_LIST)
				MulticlassUtils.showMulticlassList(cha);
		}
	}
}
