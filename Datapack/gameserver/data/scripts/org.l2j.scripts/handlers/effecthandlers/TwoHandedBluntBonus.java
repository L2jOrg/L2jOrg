package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.conditions.ConditionUsingItemType;
import org.l2j.gameserver.model.conditions.ConditionUsingSlotType;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class TwoHandedBluntBonus extends AbstractEffect {

    private static final Condition weaponTypeCondition = new ConditionUsingItemType(WeaponType.BLUNT.mask());
    private static final Condition slotCondition = new ConditionUsingSlotType(BodyPart.TWO_HAND.getId());

    private final double pAtkAmount;
    private final StatModifierType pAtkmode;

    private final double accuracyAmount;
    private final StatModifierType accuracyMode;

    private TwoHandedBluntBonus(StatsSet params) {
        pAtkAmount = params.getDouble("power", 0);
        pAtkmode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);

        accuracyAmount = params.getDouble("accuracy", 0);
        accuracyMode = params.getEnum("accuracy-mode", StatModifierType.class, StatModifierType.DIFF);
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        if (weaponTypeCondition.test(effected, effected, skill) && slotCondition.test(effected, effected, skill)) {
            switch (pAtkmode) {
                case DIFF -> effected.getStats().mergeAdd(Stat.PHYSICAL_ATTACK, pAtkAmount);
                case PER -> effected.getStats().mergeMul(Stat.PHYSICAL_ATTACK, (pAtkAmount / 100) + 1);
            }

            switch (accuracyMode) {
                case DIFF -> effected.getStats().mergeAdd(Stat.ACCURACY, accuracyAmount);
                case PER -> effected.getStats().mergeMul(Stat.ACCURACY, (accuracyAmount / 100) + 1);
            }
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TwoHandedBluntBonus(data);
        }

        @Override
        public String effectName() {
            return "two-hand-blunt-bonus";
        }
    }
}
