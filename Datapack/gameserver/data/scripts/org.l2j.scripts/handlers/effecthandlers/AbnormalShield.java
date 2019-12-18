package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

/**
 * An effect that blocks a debuff. Acts like DOTA's Linken Sphere.
 * @author Nik
 */
public final class AbnormalShield extends AbstractEffect {

	private final int times;
	
	public AbnormalShield(StatsSet params)
	{
		times = params.getInt("times", -1);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.setAbnormalShieldBlocks(times);
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.ABNORMAL_SHIELD.getMask();
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.setAbnormalShieldBlocks(Integer.MIN_VALUE);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.ABNORMAL_SHIELD;
	}
}