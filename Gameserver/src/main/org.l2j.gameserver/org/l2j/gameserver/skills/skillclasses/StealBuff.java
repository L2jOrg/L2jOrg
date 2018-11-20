package org.l2j.gameserver.skills.skillclasses;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExMagicAttackInfo;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.skills.EffectUseType;
import org.l2j.gameserver.stats.Stats;
import org.l2j.gameserver.templates.StatsSet;
import org.l2j.gameserver.utils.AbnormalsComparator;

/**
 * @author pchayka, reworked by Bonux
 */
public class StealBuff extends Skill
{
	private final int _stealCount;

	public StealBuff(StatsSet set)
	{
		super(set);
		_stealCount = set.getInteger("stealCount", 1);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
        if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
            return false;

		if(target == null || !target.isPlayer())
		{
			activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

        return true;
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!target.isPlayer())
			return;

		if(calcStealChance(target, activeChar))
		{
			int stealCount = Rnd.get(1, _stealCount); // ToCheck

			TIntSet stelledSkillIds = new TIntHashSet();

            List<Abnormal> effects = new ArrayList<Abnormal>(target.getAbnormalList().values());
			Collections.sort(effects, AbnormalsComparator.getInstance()); // ToFix: Comparator to HF
			Collections.reverse(effects);
			for(Abnormal effect : effects)
			{
				if(effect.isOffensive())
					continue;
	
				if(!effect.isOfUseType(EffectUseType.NORMAL))
					continue;

				if(!effect.isCancelable())
					continue;

				Skill effectSkill = effect.getSkill();
				if(effectSkill == null)
					continue;

				if(!stelledSkillIds.contains(effectSkill.getId()) && stelledSkillIds.size() < stealCount)
					continue;

				if(effectSkill.isToggle())
					continue;

				if(effectSkill.isPassive())
					continue;

				if(target.isSpecialAbnormal(effectSkill))
					continue;

                stealAbnormal(activeChar, effect);
				effect.exit();

				stelledSkillIds.add(effectSkill.getId());
			}
		}
		else
        {
            activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addName(target).addSkillName(getId(), getLevel()));
            activeChar.sendPacket(new ExMagicAttackInfo(activeChar.getObjectId(), target.getObjectId(), ExMagicAttackInfo.RESISTED));
        }
	}

    private boolean calcStealChance(Creature effected, Creature effector)
	{
		double cancel_res_multiplier = effected.calcStat(Stats.CANCEL_RESIST, 1, null, null);
		int dml = effector.getLevel() - effected.getLevel(); // to check: magicLevel or player level? Since it's magic skill setting player level as default
		double prelimChance = (dml + 50) * (1 - cancel_res_multiplier * .01); // 50 is random reasonable constant which gives ~50% chance of steal success while else is equal
		return Rnd.chance(prelimChance);
	}

    private void stealAbnormal(Creature character, Abnormal abnormal)
	{
        Abnormal a = new Abnormal(character, character, abnormal);
		a.setDuration(abnormal.getDuration());
		a.setTimeLeft(abnormal.getTimeLeft());
        character.getAbnormalList().add(a);
	}
}