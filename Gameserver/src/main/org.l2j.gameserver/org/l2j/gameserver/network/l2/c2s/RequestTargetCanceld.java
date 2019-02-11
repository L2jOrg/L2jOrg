package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;

import java.nio.ByteBuffer;

public class RequestTargetCanceld extends L2GameClientPacket
{
	private int _unselect;

	/**
	 * packet type id 0x48
	 * format:		ch
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_unselect = buffer.getShort();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
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