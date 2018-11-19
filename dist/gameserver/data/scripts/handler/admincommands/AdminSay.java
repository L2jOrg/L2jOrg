package handler.admincommands;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.NSPacket;
import l2s.gameserver.network.l2.s2c.SayPacket2;

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
