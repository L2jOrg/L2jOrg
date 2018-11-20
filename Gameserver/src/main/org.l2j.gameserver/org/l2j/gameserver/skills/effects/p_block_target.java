package org.l2j.gameserver.skills.effects;

import java.util.List;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class p_block_target extends Effect
{
	public p_block_target(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		return getEffected().isTargetable(getEffector());
	}

	@Override
	public void onStart()
	{
		Creature effected = getEffected();
		effected.setTargetable(false);

		effected.abortAttack(true, true);
		effected.abortCast(true, true);

		List<Creature> characters = World.getAroundCharacters(effected);
		for(Creature character : characters)
		{
			if(character.getTarget() != effected && character.getAI().getAttackTarget() != effected && character.getAI().getCastTarget() != effected)
				continue;

			if(character.isNpc())
				((NpcInstance) character).getAggroList().remove(effected, true);

			if(character.getTarget() == effected)
				character.setTarget(null);

			if(character.getAI().getAttackTarget() == effected)
				character.abortAttack(true, true);

			if(character.getAI().getCastTarget() == effected)
				character.abortCast(true, true);

			character.sendActionFailed();
			character.stopMove();
			character.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
	}

	@Override
	public void onExit()
	{
		getEffected().setTargetable(true);
	}
}