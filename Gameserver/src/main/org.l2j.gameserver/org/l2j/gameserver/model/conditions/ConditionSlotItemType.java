package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * The Class ConditionSlotItemType.
 *
 * @author mkizub
 */
public final class ConditionSlotItemType extends ConditionInventory {
    private final int _mask;

    /**
     * Instantiates a new condition slot item type.
     *
     * @param slot the slot
     * @param mask the mask
     */
    public ConditionSlotItemType(int slot, int mask) {
        super(slot);
        _mask = mask;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (!isPlayer(effector)) {
            return false;
        }

        final Item itemSlot = effector.getInventory().getPaperdollItem(InventorySlot.fromId(_slot));
        if (itemSlot == null) {
            return false;
        }
        return (itemSlot.getTemplate().getItemMask() & _mask) != 0;
    }
}
