package handlers.bypasshandlers;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Folk;
import org.l2j.gameserver.model.actor.instance.Player;

import static org.l2j.gameserver.util.GameUtils.isNpc;

public class SkillList implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"SkillList"
	};
	
	@Override
	public boolean useBypass(String command, Player activeChar, Creature target)
	{
		if (!isNpc(target))
		{
			return false;
		}
		Folk.showSkillList(activeChar, (Npc) target, activeChar.getClassId());
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
