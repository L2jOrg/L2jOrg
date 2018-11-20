package org.l2j.gameserver.skills.skillclasses;

import java.util.List;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.MagicSkillUse;
import org.l2j.gameserver.stats.Formulas;
import org.l2j.gameserver.stats.Formulas.AttackInfo;
import org.l2j.gameserver.templates.StatsSet;

public class Charge extends Skill
{
	public static final int MAX_CHARGE = 10;

	private int _charges;
	private boolean _fullCharge;

	public Charge(StatsSet set)
	{
		super(set);
		_charges = set.getInteger("charges", getLevel());
		_fullCharge = set.getBool("fullCharge", false);
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(!activeChar.isPlayer())
			return false;

		Player player = (Player) activeChar;

		int charges = _charges == -1 ? player.getMaxIncreasedForce() : _charges;

		//Камушки можно юзать даже если заряд > 7, остальное только если заряд < уровень скила
		if(getPower() <= 0 && getId() != 2165 && player.getIncreasedForce() >= charges)
		{
			activeChar.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
			return false;
		}
		else if(getId() == 2165)
			player.sendPacket(new MagicSkillUse(player, player, 2165, 1, 0, 0));

		return true;
	}

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		if(activeChar.isPlayer())
			chargePlayer((Player) activeChar, getId());
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!activeChar.isPlayer())
			return;

		if(target.isDead())
			return;

		if(target == activeChar)
			return;

		if(getPower() <= 0)
			return;

		final Creature realTarget = reflected ? activeChar : target;
		final AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, false, isSSPossible(), false);
		if(info == null)
			return;

		realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true, true, info.crit || info.blow, false, false);
		if(!info.miss || info.damage >= 1)
		{
			double lethalDmg = Formulas.calcLethalDamage(activeChar, realTarget, this);
			if(lethalDmg > 0)
				realTarget.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
			else if(!reflected)
				realTarget.doCounterAttack(this, activeChar, false);
		}
	}

	public void chargePlayer(Player player, Integer skillId)
	{
		int charges = _charges == -1 ? player.getMaxIncreasedForce() : _charges;
		if(player.getIncreasedForce() >= charges)
		{
			player.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
			return;
		}
		if(_fullCharge)
			player.setIncreasedForce(charges);
		else
			player.setIncreasedForce(player.getIncreasedForce() + 1);
	}
}