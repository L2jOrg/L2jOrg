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

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Death Link effect implementation.
 * @author Adry_85
 */
public final class DeathLink extends AbstractEffect {
    private final double power;

    private DeathLink(StatsSet params)
    {
        power = params.getDouble("power", 0);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.DEATH_LINK;
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

        if (isPlayer(effected) && effected.getActingPlayer().isFakeDeath()) {
            effected.stopFakeDeath(true);
        }

        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
        final double damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power * (-((effector.getCurrentHp() * 2) / effector.getMaxHp()) + 2), effected.getMDef(), mcrit);
        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new DeathLink(data);
        }

        @Override
        public String effectName() {
            return "DeathLink";
        }
    }
}