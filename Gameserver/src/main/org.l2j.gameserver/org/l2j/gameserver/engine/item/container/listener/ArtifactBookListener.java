package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.instance.Item;

public final class ArtifactBookListener implements PlayerInventoryListener {

    private ArtifactBookListener() {

    }

    @Override
    public void notifyUnequiped(int slot, Item item, Inventory inventory) {
        if (item.getBodyPart() == BodyPart.ARTIFACT_BOOK) {
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT1);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT2);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT3);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT4);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT5);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT6);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT7);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT8);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT9);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT10);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT11);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT12);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT13);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT14);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT15);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT16);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT17);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT18);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT19);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT20);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_ARTIFACT21);

        }
    }

    @Override
    public void notifyEquiped(int slot, Item item, Inventory inventory) {
    }


    public static ArtifactBookListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ArtifactBookListener INSTANCE = new ArtifactBookListener();
    }
}
