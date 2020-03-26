package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;

/**
 * HP Drain effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class HpDrain extends AbstractEffect {
    private final double power;
    private final double percentage;

    private HpDrain(StatsSet params) {
        power = params.getDouble("power", 0);
        percentage = params.getDouble("percentage", 0);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.HP_DRAIN;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effector.isAlikeDead()) {
            return;
        }

        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
        final double damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power, effected.getMDef(), mcrit);

        double drain;
        final int cp = (int) effected.getCurrentCp();
        final int hp = (int) effected.getCurrentHp();

        if (cp > 0) {
            drain = (damage < cp) ? 0 : (damage - cp);
        } else if (damage > hp) {
            drain = hp;
        } else {
            drain = damage;
        }

        final double hpAdd = ((percentage / 100) * drain);
        final double hpFinal = effector.getCurrentHp() + hpAdd > effector.getMaxHp() ? effector.getMaxHp() : effector.getCurrentHp() + hpAdd;
        effector.setCurrentHp(hpFinal);
        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new HpDrain(data);
        }

        @Override
        public String effectName() {
            return "hp-drain";
        }
    }
}