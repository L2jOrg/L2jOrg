package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.Formulas;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Magical Attack By Abnormal Slot effect implementation.
 * @author Sdw
 */
public final class MagicalAttackByAbnormalSlot extends AbstractEffect {
    private final double power;
    private final Set<AbnormalType> abnormals;

    private MagicalAttackByAbnormalSlot(StatsSet params) {
        power = params.getDouble("power", 0);
        abnormals = Arrays.stream(params.getString("abnormals", "").split(" ")).map(AbnormalType::valueOf).collect(Collectors.toSet());
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.MAGICAL_ATTACK;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effector.isAlikeDead() || abnormals.stream().noneMatch(effected::hasAbnormalType)) {
            return;
        }

        if (isPlayer(effected) && effected.getActingPlayer().isFakeDeath()) {
            effected.stopFakeDeath(true);
        }

        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
        final double damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power, effected.getMDef(), mcrit);

        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new MagicalAttackByAbnormalSlot(data);
        }

        @Override
        public String effectName() {
            return "magical-attack-by-abnormal";
        }
    }
}