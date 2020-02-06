package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static java.util.Objects.nonNull;

/**
 * Escape effect implementation.
 * @author Adry_85
 */
public final class Escape extends AbstractEffect {
	public final TeleportWhereType escapeType;
	
	public Escape(StatsSet params)
	{
		escapeType = params.getEnum("escapeType", TeleportWhereType.class, null);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.TELEPORT;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill) {
		// While affected by escape blocking effect you cannot use Blink or Scroll of Escape
		return super.canStart(effector, effected, skill) && !effected.cannotEscape();
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (nonNull(escapeType)) {
			effected.teleToLocation(escapeType, null);
		}
	}
}
