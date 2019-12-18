package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Damage Over Time Percent effect implementation.
 * @author Adry_85
 */
public final class DamOverTimePercent extends AbstractEffect {
	private final boolean canKill;
	private final double power;
	
	public DamOverTimePercent(StatsSet params) {
		canKill = params.getBoolean("canKill", false);
		power = params.getDouble("power");
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DMG_OVER_TIME_PERCENT;
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item) {
		if (effected.isDead()) {
			return false;
		}
		
		double damage = effected.getCurrentHp() * power * getTicksMultiplier();
		if (damage >= effected.getCurrentHp() - 1) {
			if (skill.isToggle()) {
				effected.sendPacket(SystemMessageId.YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP);
				return false;
			}
			
			// For DOT skills that will not kill effected player.
			if (!canKill) {
				// Fix for players dying by DOTs if HP < 1 since reduceCurrentHP method will kill them
				if (effected.getCurrentHp() <= 1) {
					return false;
				}
				
				damage = effected.getCurrentHp() - 1;
			}
		}
		
		effector.doAttack(damage, effected, skill, true, false, false, false);
		
		return skill.isToggle();
	}
}
