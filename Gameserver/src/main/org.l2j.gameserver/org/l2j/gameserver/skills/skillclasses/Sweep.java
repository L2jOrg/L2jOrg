package org.l2j.gameserver.skills.skillclasses;

import java.util.List;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.instances.MonsterInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.templates.StatsSet;

public class Sweep extends Skill
{
	public Sweep(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(isNotTargetAoE())
			return true;

		if(target == null)
			return false;

		if(!target.isMonster() || !target.isDead())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		final MonsterInstance monter = (MonsterInstance) target;
		if(monter.isSweeped())
			return false;

		if(!monter.isSpoiled())
		{
			activeChar.sendPacket(SystemMsg.SWEEPER_FAILED_TARGET_NOT_SPOILED);
			return false;
		}

		if(!monter.isSpoiled((Player) activeChar))
		{
			activeChar.sendPacket(SystemMsg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
			return false;
		}

		return true;
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!activeChar.isPlayer())
			return;

		if(target == null || !target.isMonster() || !target.isDead())
			return;

		final MonsterInstance monter = (MonsterInstance) target;
		if(monter.isSweeped() || !monter.isSpoiled())
			return;

		final Player player = activeChar.getPlayer();
		if(!monter.isSpoiled(player))
		{
			activeChar.sendPacket(SystemMsg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
			return;
		}

		monter.takeSweep(player);
	}

	@Override
	public void onFinishCast(Creature aimingTarget, Creature activeChar, List<Creature> targets)
	{
		if(getTargetType() == SkillTargetType.TARGET_AREA_AIM_CORPSE)
		{
			if(!aimingTarget.isMonster() || !aimingTarget.isDead())
				return;

			final MonsterInstance monter = (MonsterInstance) aimingTarget;
			if(!monter.isSweeped())
				return;

			monter.endDecayTask();
		}
		else if(getTargetType() == SkillTargetType.TARGET_CORPSE)
		{
			for(Creature target : targets)
			{
				if(!target.isMonster() || !target.isDead())
					continue;

				final MonsterInstance monter = (MonsterInstance) target;
				if(!monter.isSweeped())
					continue;

				monter.endDecayTask();
			}
		}
	}
}