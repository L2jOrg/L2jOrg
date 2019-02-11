package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.items.attachment.FlagItemAttachment;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.skills.SkillEntry;

import java.nio.ByteBuffer;

public class RequestMagicSkillUse extends L2GameClientPacket
{
	private Integer _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;

	/**
	 * packet type id 0x39
	 * format:		cddc
	 * @param buffer
	 */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_magicId = buffer.getInt();
		_ctrlPressed = buffer.getInt() != 0;
		_shiftPressed = buffer.get() != 0;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;
		activeChar.setActive();

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(_magicId, activeChar.getSkillLevel(_magicId));
		if(skillEntry != null)
		{
			Skill skill = skillEntry.getTemplate();
			if(!skill.isActive() && !skill.isToggle())
			{
				activeChar.sendActionFailed();
				return;
			}

			FlagItemAttachment attachment = activeChar.getActiveWeaponFlagAttachment();
			if(attachment != null && !attachment.canCast(activeChar, skill))
			{
				activeChar.sendActionFailed();
				return;
			}

			// В режиме трансформации доступны только скилы трансформы
			if(activeChar.isTransformed() && !activeChar.getAllSkills().contains(skillEntry))
			{
				activeChar.sendActionFailed();
				return;
			}

			if(skill.isToggle())
			{
				if(activeChar.getAbnormalList().contains(skill))
				{
					if(!skill.isNecessaryToggle())
					{
						if(activeChar.isSitting())
						{
							activeChar.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
							return;
						}
						activeChar.getAbnormalList().stop(skill.getId());
						activeChar.sendActionFailed();
					}
					activeChar.sendActionFailed();
					return;
				}
			}

			Creature target = skill.getAimingTarget(activeChar, activeChar.getTarget());

			activeChar.setGroundSkillLoc(null);
			activeChar.getAI().Cast(skill, target, _ctrlPressed, _shiftPressed);
		}
		else
			activeChar.sendActionFailed();
	}
}