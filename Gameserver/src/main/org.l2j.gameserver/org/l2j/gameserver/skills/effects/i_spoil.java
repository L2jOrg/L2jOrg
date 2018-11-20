package org.l2j.gameserver.skills.effects;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.instances.MonsterInstance;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 */
public class i_spoil extends i_abstract_effect
{
	public i_spoil(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffector().isPlayer())
			return false;

		if(getEffected().isDead())
			return false;

		if(!getEffected().isMonster())
			return false;

		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		final MonsterInstance monster = (MonsterInstance) getEffected();
		if(monster.isSpoiled())
		{
			getEffector().sendPacket(SystemMsg.IT_HAS_ALREADY_BEEN_SPOILED);
			return;
		}

		final Player player = getEffector().getPlayer();
		final int monsterLevel = monster.getLevel();
		final int modifier = Math.abs(monsterLevel - player.getLevel());
		double rateOfSpoil = Config.BASE_SPOIL_RATE;

		if(modifier > 8)
			rateOfSpoil = rateOfSpoil - rateOfSpoil * (modifier - 8) * 9 / 100;

		rateOfSpoil = rateOfSpoil * getSkill().getMagicLevel() / monsterLevel;

		if(rateOfSpoil < Config.MINIMUM_SPOIL_RATE)
			rateOfSpoil = Config.MINIMUM_SPOIL_RATE;
		else if(rateOfSpoil > 99.)
			rateOfSpoil = 99.;

		if(player.isGM())
			player.sendMessage(new CustomMessage("org.l2j.gameserver.skills.skillclasses.Spoil.Chance").addNumber((long) rateOfSpoil));

		doSpoil(Rnd.chance(rateOfSpoil));
	}

	protected void doSpoil(boolean success)
	{
		if(success)
		{
			((MonsterInstance) getEffected()).setSpoiled(getEffector().getPlayer());
			getEffector().sendPacket(SystemMsg.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
		}
		else
			getEffector().sendPacket(new SystemMessagePacket(SystemMsg.S1_HAS_FAILED).addSkillName(getSkill()));
	}
}