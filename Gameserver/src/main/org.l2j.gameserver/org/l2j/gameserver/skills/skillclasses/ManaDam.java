package org.l2j.gameserver.skills.skillclasses;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExMagicAttackInfo;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.stats.Formulas;
import org.l2j.gameserver.stats.Stats;
import org.l2j.gameserver.templates.StatsSet;

public class ManaDam extends Skill
{
	public ManaDam(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(target.isDead())
			return;

		final int magicLevel = getMagicLevel() == 0 ? activeChar.getLevel() : getMagicLevel();
		final int landRate = Rnd.get(30, 100) * target.getLevel() / magicLevel;

		if(Rnd.chance(landRate))
		{
			double mAtk = activeChar.getMAtk(target, this);

			if(isSSPossible())
				mAtk *= ((100 + activeChar.getChargedSpiritshotPower()) / 100.);

			double mDef = Math.max(1., target.getMDef(activeChar, this));
			if(mDef < 1.)
				mDef = 1.;

			double damage = Math.sqrt(mAtk) * this.getPower() * (target.getMaxMp() / 97) / mDef;

			if(Formulas.calcMCrit(activeChar, target, this))
			{
				activeChar.sendPacket(SystemMsg.MAGIC_CRITICAL_HIT);
                damage *= (2.0 + (activeChar.calcStat(Stats.P_MAGIC_CRITICAL_DMG_PER, target, this) * 0.01 - 1.0));
                damage += activeChar.calcStat(Stats.P_MAGIC_CRITICAL_DMG_DIFF, target, this);
			}
			target.reduceCurrentMp(damage, activeChar);
		}
		else
		{
			SystemMessagePacket msg = new SystemMessagePacket(SystemMsg.C1_RESISTED_C2S_MAGIC).addName(target).addName(activeChar);
			activeChar.sendPacket(msg);
			target.sendPacket(msg);
            activeChar.sendPacket(new ExMagicAttackInfo(activeChar.getObjectId(), target.getObjectId(), ExMagicAttackInfo.RESISTED));
            target.sendPacket(new ExMagicAttackInfo(activeChar.getObjectId(), target.getObjectId(), ExMagicAttackInfo.RESISTED));
			target.reduceCurrentHp(1., activeChar, this, true, true, false, true, false, false, true);
		}
	}
}