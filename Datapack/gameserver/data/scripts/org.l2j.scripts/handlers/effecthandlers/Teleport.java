package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * Teleport effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class Teleport extends AbstractEffect {
    private final Location loc;

    private Teleport(StatsSet params)
    {
        loc = new Location(params.getInt("x", 0), params.getInt("y", 0), params.getInt("z", 0));
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
    public void instant(Creature effector, Creature effected, Skill skill, Item item)
    {
        effected.teleToLocation(loc, true, null);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Teleport(data);
        }

        @Override
        public String effectName() {
            return "teleport";
        }
    }
}
