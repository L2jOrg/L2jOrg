package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.instance.Item;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public final class BroochListener implements PlayerInventoryListener {

    private BroochListener() {
    }

    @Override
    public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
        if (slot == InventorySlot.BROOCH) {
            InventorySlot.brochesJewel().forEach(inventory::unEquipItemInSlot);
        } else if(item.getBodyPart() == BodyPart.BROOCH_JEWEL && inventory instanceof PlayerInventory inv) {
            updateAdditionalSoulshot(inv);
        }
    }

    private void updateAdditionalSoulshot(PlayerInventory inventory) {
        int jewel = 0;
        int currentLevel = -1;
        for (InventorySlot slot : InventorySlot.brochesJewel()) {
            var item = inventory.getPaperdollItem(slot);

            if(nonNull(item)) {
                var itemLevel = item.getSkills(ItemSkillType.NORMAL).stream().mapToInt(SkillHolder::getLevel).max().orElse(-1);
                if(jewel == 0 || itemLevel > currentLevel) {
                    jewel = item.getId();
                    currentLevel = itemLevel;
                }
            }
        }
        inventory.getOwner().setAdditionalSoulshot(jewel);
    }

    @Override
    public void notifyEquiped(InventorySlot slot, Item item, Inventory inventory) {
        if(inventory instanceof PlayerInventory inv) {
            updateAdditionalSoulshot(inv);
        }
    }

    public static BroochListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BroochListener INSTANCE = new BroochListener();
    }
}
