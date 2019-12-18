package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.InventoryListener;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.instance.Item;

/**
 * @author JoeAlisson
 */
public final class StatsListener implements InventoryListener {

    private StatsListener() {
    }

    @Override
    public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
        var charStat = inventory.getOwner().getStats();
        item.getTemplate().getFunctionTemplates().forEach(func -> {
            if(func.getStat().hasDefaultFinalizer()) {
                charStat.removeAddAdditionalStat(func.getStat(), func.getValue());
            }
        });
        inventory.getOwner().getStats().recalculateStats(true);
    }

    @Override
    public void notifyEquiped(InventorySlot slot, Item item, Inventory inventory) {
        var charStat = inventory.getOwner().getStats();
        item.getTemplate().getFunctionTemplates().forEach(func -> {
            if(func.getStat().hasDefaultFinalizer()) {
                charStat.addAdditionalStat(func.getStat(), func.getValue());
            }
        });
        inventory.getOwner().getStats().recalculateStats(true);
    }

    public static StatsListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final StatsListener INSTANCE = new StatsListener();
    }
}
