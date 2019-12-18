package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

/**
 * Note: In retail this effect doesn't stack. It appears that the active value is taken from the last such effect.
 * @author Sdw
 */
public class SkillEvasion extends AbstractEffect {
	private final int magicType;
	private final double amount;
	
	public SkillEvasion(StatsSet params){
		magicType = params.getInt("magicType", 0);
		amount = params.getDouble("amount", 0);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.getStats().addSkillEvasionTypeValue(magicType, amount);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.getStats().removeSkillEvasionTypeValue(magicType, amount);
	}
}
