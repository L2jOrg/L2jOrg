package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Stat;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class RealDamage extends AbstractEffect {

    private final double power;

    private RealDamage(StatsSet params)
    {
        power = params.getDouble("power", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        final double damage = power - (power * (Math.min(effected.getStats().getValue(Stat.REAL_DAMAGE_RESIST, 1), 1.8) - 1));

        effected.reduceCurrentHp(damage, effector, skill, false, true, false, false);
        if (isPlayer(effector)) {
            effector.sendDamageMessage(effected, skill, (int) damage, 0, false, false);
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new RealDamage(data);
        }

        @Override
        public String effectName() {
            return "RealDamage";
        }
    }
}
