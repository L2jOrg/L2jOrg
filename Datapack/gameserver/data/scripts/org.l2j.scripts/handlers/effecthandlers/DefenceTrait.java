package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.TraitType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Defence Trait effect implementation.
 * @author NosBit
 * @author JoeAlisson
 */
public final class DefenceTrait extends AbstractEffect {
    private final Map<TraitType, Float> defenceTraits;

    private DefenceTrait(StatsSet params) {
        if(params.contains("type")) {
            defenceTraits = Map.of(params.getEnum("type", TraitType.class), params.getFloat("power") / 100);
        } else {
            defenceTraits = new HashMap<>();
            params.getSet().forEach((key, value) -> {
                if(key.startsWith("trait")) {
                    var set = (StatsSet) value;
                    defenceTraits.put(set.getEnum("type", TraitType.class), set.getFloat("power") / 100);
                }
            });
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

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new DefenceTrait(data);
        }

        @Override
        public String effectName() {
            return "defence-trait";
        }
    }
}
