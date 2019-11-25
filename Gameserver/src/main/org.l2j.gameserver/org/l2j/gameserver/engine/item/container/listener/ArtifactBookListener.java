package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.instance.Item;

public final class ArtifactBookListener implements PlayerInventoryListener {

    private ArtifactBookListener() {

    }

    @Override
    public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
        if (item.getBodyPart() == BodyPart.ARTIFACT_BOOK) {
            InventorySlot.balanceArtifacts().forEach(inventory::unEquipItemInSlot);
            InventorySlot.spiritArtifacts().forEach(inventory::unEquipItemInSlot);
            InventorySlot.supportArtifact().forEach(inventory::unEquipItemInSlot);
            InventorySlot.protectionArtifacts().forEach(inventory::unEquipItemInSlot);
        }
    }

    @Override
    public void notifyEquiped(InventorySlot slot, Item item, Inventory inventory) {
    }


    public static ArtifactBookListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ArtifactBookListener INSTANCE = new ArtifactBookListener();
    }
}
