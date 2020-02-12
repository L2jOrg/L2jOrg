package handlers.effecthandlers.stat;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.conditions.ConditionUsingItemType;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.type.ArmorType;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.model.stats.Stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sdw
 * @author JoeAlisson
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
        amount = params.getDouble("power", 0);
        mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);

        int weaponTypesMask = Arrays.stream(params.getString("weapon-type").trim().split(" "))
                .mapToInt(w -> WeaponType.valueOf(w).mask()).reduce(0, (a, b) -> a | b);

        int armorTypesMask = Arrays.stream(params.getString("armor-type").trim().split(" "))
                .mapToInt(armor -> ArmorType.valueOf(armor).mask()).reduce(0, (a, b) -> a | b);


        if (weaponTypesMask != 0) {
            conditions.add(new ConditionUsingItemType(weaponTypesMask));
        }

        if (armorTypesMask != 0) {
            conditions.add(new ConditionUsingItemType(armorTypesMask));
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
