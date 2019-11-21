package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.EtcItem;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Zoey76
 * @author JoeAlisson
 */
public class RequestUnEquipItem extends ClientPacket {
    private int slot;

    /**
     * Packet type id 0x16 format: cd
     */
    @Override
    public void readImpl() {
        slot = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        var bodyPart = BodyPart.fromSlot(slot);

        final Item item = player.getInventory().getItemByBodyPart(bodyPart);
        // Wear-item are not to be unequipped.
        if (item == null) {
            return;
        }

        // The English system message say weapon, but it's applied to any equipped item.
        if (player.isAttackingNow() || player.isCastingNow()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_CHANGE_WEAPONS_DURING_AN_ATTACK);
            return;
        }

        // Arrows and bolts.
        if ((bodyPart == BodyPart.LEFT_HAND) && (item.getTemplate() instanceof EtcItem)) {
            return;
        }

        // Prevent of unequipping a cursed weapon.
        if ((bodyPart == BodyPart.TWO_HAND) && (player.isCursedWeaponEquipped() || player.isCombatFlagEquipped())) {
            return;
        }

        // Prevent player from unequipping item in special conditions.
        if (player.hasBlockActions() || player.isAlikeDead()) {
            return;
        }

        if (!player.getInventory().canManipulate(item)) {
            client.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_TAKEN_OFF);
            return;
        }

        var  modified = player.getInventory().unEquipItemInBodySlotAndRecord(bodyPart);
        player.broadcastUserInfo();

        var iterator = modified.iterator();
        if(iterator.hasNext()) {
            final InventoryUpdate iu = new InventoryUpdate(modified);
            player.sendInventoryUpdate(iu);

            var unequipped = iterator.next();
            SystemMessage sm;
            if(unequipped.getEnchantLevel() > 0) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(unequipped.getEnchantLevel());
            } else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
            }
            sm.addItemName(unequipped);
            client.sendPacket(sm);
        }
    }
}
