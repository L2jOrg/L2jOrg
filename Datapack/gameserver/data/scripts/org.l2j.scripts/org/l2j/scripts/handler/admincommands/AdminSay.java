package org.l2j.scripts.handler.admincommands;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.ChatType;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;
import org.l2j.gameserver.network.l2.s2c.NSPacket;
import org.l2j.gameserver.network.l2.s2c.SayPacket2;

/**
 * @author VISTALL
 * @date 20:27/02.08.2011
 */
public class AdminSay extends ScriptAdminCommand
{
	private enum Commands
	{
		admin_say
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		if(target == null || !target.isCreature())
			target = activeChar;

		if(wordList.length < 3)
			return false;

		ChatType type = ChatType.valueOf(wordList[1]);
		NpcString npcString = NpcString.NONE;
		String[] arg = new String[5];
		try
		{
			npcString = NpcString.valueOf(wordList[2]);
		}
		catch(IllegalArgumentException e)
		{
			arg[0] = wordList[2];
		}

		L2GameServerPacket packet = null;
		if(target.isNpc())
			packet = new NSPacket((NpcInstance)target, type, npcString, arg);
		else
			packet = new SayPacket2(target.getObjectId(), type, target.getName(), npcString, arg);

		((Creature) target).broadcastPacket(packet);
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
