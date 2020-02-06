package handlers.effecthandlers.stat;

import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.conditions.ConditionPlayerIsInCombat;
import org.l2j.gameserver.model.conditions.ConditionUsingItemType;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.type.ArmorType;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * @author Sdw
 */
public abstract class AbstractStatEffect extends AbstractEffect {

    public final Stat addStat;
    public final Stat mulStat;
    public final double amount;
    public final StatModifierType mode;
    public final List<Condition> conditions = new ArrayList<>();

    public AbstractStatEffect(StatsSet params, Stat stat) {
        this(params, stat, stat);
    }

    public AbstractStatEffect(StatsSet params, Stat mulStat, Stat addStat) {
        this.addStat = addStat;
        this.mulStat = mulStat;
        amount = params.getDouble("amount", 0);
        mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);

        int weaponTypesMask = 0;
        final List<String> weaponTypes = params.getList("weaponType", String.class);

        if (nonNull(weaponTypes)) {
            for (String weaponType : weaponTypes) {
                try {
                    weaponTypesMask |= WeaponType.valueOf(weaponType).mask();
                } catch (IllegalArgumentException e) {
                    final IllegalArgumentException exception = new IllegalArgumentException("weaponType should contain WeaponType enum value but found " + weaponType);
                    exception.addSuppressed(e);
                    throw exception;
                }
            }
        }

        int armorTypesMask = 0;
        final List<String> armorTypes = params.getList("armorType", String.class);
        if (nonNull(armorTypes)) {
            for (String armorType : armorTypes) {
                try {
                    armorTypesMask |= ArmorType.valueOf(armorType).mask();
                } catch (IllegalArgumentException e) {
                    final IllegalArgumentException exception = new IllegalArgumentException("armorTypes should contain ArmorType enum value but found " + armorType);
                    exception.addSuppressed(e);
                    throw exception;
                }
            }
        }

        if (weaponTypesMask != 0) {
            conditions.add(new ConditionUsingItemType(weaponTypesMask));
        }

        if (armorTypesMask != 0) {
            conditions.add(new ConditionUsingItemType(armorTypesMask));
        }

        if (params.contains("inCombat")) {
            conditions.add(new ConditionPlayerIsInCombat(params.getBoolean("inCombat")));
        }
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        if (conditions.isEmpty() || conditions.stream().allMatch(cond -> cond.test(effected, effected, skill))) {
            switch (mode) {
                case DIFF -> effected.getStats().mergeAdd(addStat, amount);
                case PER -> effected.getStats().mergeMul(mulStat, amount / 100 + 1);
            }
        }
    }
}
