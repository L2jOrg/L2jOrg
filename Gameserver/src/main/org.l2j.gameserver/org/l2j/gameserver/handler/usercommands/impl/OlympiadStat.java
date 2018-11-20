package org.l2j.gameserver.handler.usercommands.impl;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.usercommands.IUserCommandHandler;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.ClassLevel;
import org.l2j.gameserver.model.entity.olympiad.Olympiad;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;

/**
 * Support for /olympiadstat command
 */
public class OlympiadStat implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS = { 109 };

	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if(id != COMMAND_IDS[0])
			return false;

		GameObject objectTarget = Config.OLYMPIAD_OLDSTYLE_STAT ? activeChar : activeChar.getTarget();
		if(objectTarget == null || !objectTarget.isPlayer() || objectTarget.getPlayer().getClassLevel().ordinal() < ClassLevel.SECOND.ordinal())
		{
			activeChar.sendPacket(SystemMsg.THIS_COMMAND_CAN_ONLY_BE_USED_BY_A_NOBLESSE);
			return true;
		}

		Player playerTarget = objectTarget.getPlayer();

		SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.FOR_THE_CURRENT_GRAND_OLYMPIAD_YOU_HAVE_PARTICIPATED_IN_S1_MATCHES_S2_WINS_S3_DEFEATS_YOU_CURRENTLY_HAVE_S4_OLYMPIAD_POINTS);
		sm.addInteger(Olympiad.getCompetitionDone(playerTarget.getObjectId()));
		sm.addInteger(Olympiad.getCompetitionWin(playerTarget.getObjectId()));
		sm.addInteger(Olympiad.getCompetitionLoose(playerTarget.getObjectId()));
		sm.addInteger(Olympiad.getParticipantPoints(playerTarget.getObjectId()));

		activeChar.sendPacket(sm);

		int[] ar = Olympiad.getWeekGameCounts(playerTarget.getObjectId());
		sm = new SystemMessagePacket(SystemMsg.YOU_HAVE_S1_MATCHES_REMAINING_THAT_YOU_CAN_PARTICIPATE_IN_THIS_WEEK_S2_1_VS_1_CLASS_MATCHES_S3_1_VS_1_MATCHES__S4_3_VS_3_TEAM_MATCHES);
		sm.addInteger(ar[0]);
		sm.addInteger(ar[1]);
		sm.addInteger(ar[2]);
		sm.addInteger(0/*ar[3]*/);
		activeChar.sendPacket(sm);
		return true;
	}

	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}