package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PetItemList;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.*;

/**
 * Restoration effect implementation.
 * @author Zoey76, Mobius
 * @author JoeAlisson
 */
public final class Restoration extends AbstractEffect {

    private final int itemId;
    private final int itemCount;
    private final int itemEnchantmentLevel;

    private Restoration(StatsSet params) {
        itemId = params.getInt("item", 0);
        itemCount = params.getInt("count", 0);
        itemEnchantmentLevel = params.getInt("enchant", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayable(effected)){
            return;
        }

        if (itemId <= 0 || itemCount <= 0) {
            effected.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
            LOGGER.warn("effect with wrong item Id/count: {}/{}!", itemId, itemCount);
            return;
        }

        if (isPlayer(effected)) {
            final Item newItem = effected.getActingPlayer().addItem("Skill", itemId, itemCount, effector, true);
            if (nonNull(newItem) && itemEnchantmentLevel > 0) {
                newItem.setEnchantLevel(itemEnchantmentLevel);
            }
        }
        else if (isPet(effected)) {
            final Item newItem = effected.getInventory().addItem("Skill", itemId, itemCount, effected.getActingPlayer(), effector);
            if (itemEnchantmentLevel > 0) {
                newItem.setEnchantLevel(itemEnchantmentLevel);
            }
            effected.getActingPlayer().sendPacket(new PetItemList(effected.getInventory().getItems()));
        }
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.EXTRACT_ITEM;
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Restoration(data);
        }

        @Override
        public String effectName() {
            return "restoration";
        }
    }
}
