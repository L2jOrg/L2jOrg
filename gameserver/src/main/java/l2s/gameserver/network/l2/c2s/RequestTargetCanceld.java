package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;

public class RequestTargetCanceld extends L2GameClientPacket
{
	private int _unselect;

	/**
	 * packet type id 0x48
	 * format:		ch
	 */
	@Override
	protected void readImpl()
	{
		_unselect = readH();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getAggressionTarget() != null) // TODO: [Bonux] Проверить это условие.
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isLockedTarget())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(_unselect == 0)
		{
			if(activeChar.isCastingNow())
			{
				Skill skill = activeChar.getCastingSkill();
				activeChar.abortCast(skill != null && (skill.isHandler() || skill.getHitTime() > 1000), false);
			}
			else if(activeChar.getTarget() != null)
				activeChar.setTarget(null);
		}
		else if(activeChar.getTarget() != null)
			activeChar.setTarget(null);
	}
}