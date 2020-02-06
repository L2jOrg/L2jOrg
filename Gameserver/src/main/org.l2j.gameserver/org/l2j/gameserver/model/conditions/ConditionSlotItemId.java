package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * The Class ConditionSlotItemId.
 *
 * @author mkizub
 */
public final class ConditionSlotItemId extends ConditionInventory {
    private final int _itemId;
    private final int _enchantLevel;

    /**
     * Instantiates a new condition slot item id.
     *
     * @param slot         the slot
     * @param itemId       the item id
     * @param enchantLevel the enchant level
     */
    public ConditionSlotItemId(int slot, int itemId, int enchantLevel) {
        super(slot);
        _itemId = itemId;
        _enchantLevel = enchantLevel;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (!isPlayer(effector)) {
            return false;
        }

        final Item itemSlot = effector.getInventory().getPaperdollItem(InventorySlot.fromId(_slot));
        if (itemSlot == null) {
            return _itemId == 0;
        }
        return (itemSlot.getId() == _itemId) && (itemSlot.getEnchantLevel() >= _enchantLevel);
    }
}
