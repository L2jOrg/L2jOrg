package l2s.gameserver.skills.skillclasses;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.StatsSet;

public class Continuous extends Skill
{
	private final int _lethal1;
	private final int _lethal2;

	public Continuous(StatsSet set)
	{
		super(set);
		_lethal1 = set.getInteger("lethal1", 0);
		_lethal2 = set.getInteger("lethal2", 0);
	}
	
	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		final Creature realTarget = reflected ? activeChar : target;

		final double mult = 0.01 * realTarget.calcStat(Stats.DEATH_VULNERABILITY, activeChar, this);
		final double lethal1 = _lethal1 * mult;
		final double lethal2 = _lethal2 * mult;

		if(lethal1 > 0 && Rnd.chance(lethal1))
		{
			if(realTarget.isPlayer())
			{//TODO: utochnit!
				realTarget.reduceCurrentHp(realTarget.getCurrentHp() / 2 + realTarget.getCurrentCp(), activeChar, this, true, true, false, true, false, false, true);
				realTarget.sendPacket(SystemMsg.LETHAL_STRIKE);
				activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
			}
			else if(realTarget.isNpc() && !realTarget.isLethalImmune())
			{
				realTarget.reduceCurrentHp(realTarget.getCurrentHp() / 2, activeChar, this, true, true, false, true, false, false, true);
				activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
			}
		}
		else if(lethal2 > 0 && Rnd.chance(lethal2))
		{
			if(realTarget.isPlayer())
			{
				realTarget.reduceCurrentHp(realTarget.getCurrentHp() + realTarget.getCurrentCp() - 1, activeChar, this, true, true, false, true, false, false, true);
				realTarget.sendPacket(SystemMsg.LETHAL_STRIKE);
				activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
			}
			else if(realTarget.isNpc() && !realTarget.isLethalImmune())
			{
				realTarget.reduceCurrentHp(realTarget.getCurrentHp() - 1, activeChar, this, true, true, false, true, false, false, true);
				activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
			}
		}
	}
}