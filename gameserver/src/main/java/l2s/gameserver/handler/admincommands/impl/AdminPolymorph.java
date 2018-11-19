package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;

public class AdminPolymorph implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_polyself,
		admin_polymorph,
		admin_poly,
		admin_unpolyself,
		admin_unpolymorph,
		admin_unpoly
	}

	@Override
	@SuppressWarnings("fallthrough")
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanPolymorph)
			return false;

		GameObject target = activeChar.getTarget();

		switch(command)
		{
			case admin_polyself:
				target = activeChar;
			case admin_polymorph:
			case admin_poly:
				if(target == null || !target.isPlayer())
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
					return false;
				}
				try
				{
					int id = Integer.parseInt(wordList[1]);
					if(NpcHolder.getInstance().getTemplate(id) != null)
					{
						((Player) target).setPolyId(id);
						((Player) target).broadcastCharInfo();
					}
				}
				catch(Exception e)
				{
					activeChar.sendMessage("USAGE: //poly id [type:npc|item]");
					return false;
				}
				break;
			case admin_unpolyself:
				target = activeChar;
			case admin_unpolymorph:
			case admin_unpoly:
				if(target == null || !target.isPlayer())
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
					return false;
				}
				((Player) target).setPolyId(0);
				((Player) target).broadcastCharInfo();
				break;
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}