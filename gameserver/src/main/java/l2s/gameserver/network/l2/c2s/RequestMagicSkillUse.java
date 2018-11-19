package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.attachment.FlagItemAttachment;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.skills.SkillEntry;

public class RequestMagicSkillUse extends L2GameClientPacket
{
	private Integer _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;

	/**
	 * packet type id 0x39
	 * format:		cddc
	 */
	@Override
	protected void readImpl()
	{
		_magicId = readD();
		_ctrlPressed = readD() != 0;
		_shiftPressed = readC() != 0;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
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