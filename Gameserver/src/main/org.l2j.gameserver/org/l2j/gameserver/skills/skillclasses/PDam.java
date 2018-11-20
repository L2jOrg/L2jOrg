package org.l2j.gameserver.skills.skillclasses;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.FinishRotatingPacket;
import org.l2j.gameserver.network.l2.s2c.StartRotatingPacket;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.stats.Formulas;
import org.l2j.gameserver.stats.Formulas.AttackInfo;
import org.l2j.gameserver.templates.StatsSet;

public class PDam extends Skill
{
	private final boolean _onCrit;
	private final boolean _directHp;
	private final boolean _turner;
	private final boolean _blow;
	private final boolean _static;

	public PDam(StatsSet set)
	{
		super(set);
		_onCrit = set.getBool("onCrit", false);
		_directHp = set.getBool("directHp", false);
		_turner = set.getBool("turner", false);
		_blow = set.getBool("blow", false);
		_static = set.getBool("static", false);
	}

	@Override
	public boolean calcCriticalBlow(Creature caster, Creature target)
	{
		if(_onCrit)
			return Formulas.calcBlow(caster, target, this);
		return false;
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(target.isDead())
			return;

		if(_turner && !target.isInvulnerable())
		{
			target.broadcastPacket(new StartRotatingPacket(target, target.getHeading(), 1, 65535));
			target.broadcastPacket(new FinishRotatingPacket(target, activeChar.getHeading(), 65535));
			target.setHeading(activeChar.getHeading());
			target.sendPacket(new SystemMessagePacket(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(this));
		}

		final Creature realTarget = reflected ? activeChar : target;

		double power = getPower();
		if(_static)
		{
			realTarget.reduceCurrentHp(power, activeChar, this, true, true, _directHp, true, false, false, power != 0, true, false, false, false);
			return;
		}

		final AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, 1, power, false, _blow, isSSPossible(), _onCrit, true);
		if(info == null)
			return;

		realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, _directHp, true, false, false, power != 0, true, info.crit || info.blow, false, false);

		if(!info.miss || info.damage >= 1)
		{
			double lethalDmg = Formulas.calcLethalDamage(activeChar, realTarget, this);
			if(lethalDmg > 0)
				realTarget.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
			else if(!reflected)
				realTarget.doCounterAttack(this, activeChar, _blow);
		}
	}
}