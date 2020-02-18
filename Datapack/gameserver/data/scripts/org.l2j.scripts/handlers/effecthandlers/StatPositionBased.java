package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author JoeAlisson
 */
public class StatPositionBased extends AbstractEffect {

    private final double power;
    private final Position position;
    private final Stat stat;

    private StatPositionBased(StatsSet params) {
        stat = params.getEnum("stat", Stat.class);
        power = params.getDouble("power", 0);
        position = params.getEnum("position", Position.class, Position.FRONT);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item)
    {
        effected.getStats().mergePositionTypeValue(stat, position, (power / 100) + 1, MathUtil::mul);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.getStats().mergePositionTypeValue(stat, position, (power / 100) + 1, MathUtil::div);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new StatPositionBased(data);
        }

        @Override
        public String effectName() {
            return "stat-position-based";
        }
    }
}
