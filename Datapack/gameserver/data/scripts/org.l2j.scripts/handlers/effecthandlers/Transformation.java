package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;

import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Transformation effect implementation.
 * @author nBd
 */
public final class Transformation extends AbstractEffect {
    private final int[] id;

    private Transformation(StatsSet params) {
        id = params.getIntArray("id", " ");
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {
        return !isDoor(effected);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (id.length > 0) {
            effected.transform(Rnd.get(id), true);
        }
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.stopTransformation(false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Transformation(data);
        }

        @Override
        public String effectName() {
            return "transformation";
        }
    }
}
