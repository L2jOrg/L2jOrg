package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.TraitType;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Defence Trait effect implementation.
 * @author NosBit
 */
public final class DefenceTrait extends AbstractEffect {
	public final Map<TraitType, Float> defenceTraits = new HashMap<>();
	
	public DefenceTrait(StatsSet params) {
		if (params.isEmpty()) {
			LOGGER.warn("must have parameters.");
			return;
		}
		
		for (Entry<String, Object> param : params.getSet().entrySet()) {
			defenceTraits.put(TraitType.valueOf(param.getKey()), Float.parseFloat((String) param.getValue()) / 100);
		}
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		for (var trait : defenceTraits.entrySet()) {
			if (trait.getValue() < 1.0f) {
				effected.getStats().mergeDefenceTrait(trait.getKey(), trait.getValue());
			} else {
				effected.getStats().mergeInvulnerableTrait(trait.getKey());
			}
		}
	}

	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		for (var trait : defenceTraits.entrySet()) {
			if (trait.getValue() < 1.0f) {
				effected.getStats().removeDefenceTrait(trait.getKey(), trait.getValue());
			} else {
				effected.getStats().removeInvulnerableTrait(trait.getKey());
			}
		}
	}
}
