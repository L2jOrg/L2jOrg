package l2s.gameserver.handler.usercommands.impl;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.handler.usercommands.IUserCommandHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.network.l2.components.CustomMessage;

/**
 * Support for /unstuck command
 */
public class Escape implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS = { 52 };

	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if(id != COMMAND_IDS[0])
			return false;

		if(activeChar.isMovementDisabled() || activeChar.isInOlympiadMode())
			return false;

		if(activeChar.getTeleMode() != 0 || !activeChar.getPlayerAccess().UseTeleport)
		{
			activeChar.sendMessage(new CustomMessage("common.TryLater"));
			return false;
		}

		if(activeChar.isInDuel() || activeChar.getTeam() != TeamType.NONE)
		{
			activeChar.sendMessage(new CustomMessage("common.RecallInDuel"));
			return false;
		}

		activeChar.abortAttack(true, true);
		activeChar.abortCast(true, true);
		activeChar.stopMove();

		Skill skill;
		if(activeChar.getPlayerAccess().FastUnstuck)
			skill = SkillHolder.getInstance().getSkill(2100, 2);
		else
			skill = SkillHolder.getInstance().getSkill(2099, 1);

		if(skill != null && skill.checkCondition(activeChar, activeChar, false, false, true))
			activeChar.getAI().Cast(skill, activeChar, false, true);

		return true;
	}

	@Override
	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}