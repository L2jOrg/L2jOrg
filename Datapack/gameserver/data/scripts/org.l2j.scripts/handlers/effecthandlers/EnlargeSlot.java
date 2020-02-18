package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.StorageType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.serverpackets.ExStorageMaxCount;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class EnlargeSlot extends AbstractEffect {

    private final StorageType type;
    private final double power;

    private EnlargeSlot(StatsSet params) {
        power = params.getDouble("power", 0);
        type = params.getEnum("type", StorageType.class, StorageType.INVENTORY_NORMAL);
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        Stat stat = switch (type) {
            case TRADE_BUY -> Stat.TRADE_BUY;
            case TRADE_SELL -> Stat.TRADE_SELL;
            case RECIPE_DWARVEN -> Stat.RECIPE_DWARVEN;
            case RECIPE_COMMON -> Stat.RECIPE_COMMON;
            case STORAGE_PRIVATE -> Stat.STORAGE_PRIVATE;
            default -> Stat.INVENTORY_NORMAL;
        };

        effected.getStats().mergeAdd(stat, power);
        if (isPlayer(effected)) {
            effected.sendPacket(new ExStorageMaxCount((Player) effected));
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new EnlargeSlot(data);
        }

        @Override
        public String effectName() {
            return "enlarge-slot";
        }
    }
}
