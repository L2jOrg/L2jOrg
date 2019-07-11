package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.EtcItem;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.Arrays;

/**
 * @author Zoey76
 */
public class RequestUnEquipItem extends ClientPacket {
    private int _slot;

    /**
     * Packet type id 0x16 format: cd
     */
    @Override
    public void readImpl() {
        _slot = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Item item = activeChar.getInventory().getPaperdollItemByL2ItemId(_slot);
        // Wear-items are not to be unequipped.
        if (item == null) {
            return;
        }

        // The English system message say weapon, but it's applied to any equipped item.
        if (activeChar.isAttackingNow() || activeChar.isCastingNow()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_CHANGE_WEAPONS_DURING_AN_ATTACK);
            return;
        }

        // Arrows and bolts.
        if ((_slot == ItemTemplate.SLOT_L_HAND) && (item.getItem() instanceof EtcItem)) {
            return;
        }

        // Prevent of unequipping a cursed weapon.
        if ((_slot == ItemTemplate.SLOT_LR_HAND) && (activeChar.isCursedWeaponEquipped() || activeChar.isCombatFlagEquipped())) {
            return;
        }

        // Prevent player from unequipping items in special conditions.
        if (activeChar.hasBlockActions() || activeChar.isAlikeDead()) {
            return;
        }

        if (!activeChar.getInventory().canManipulateWithItemId(item.getId())) {
            client.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_TAKEN_OFF);
            return;
        }

        if (item.isWeapon() && item.getWeaponItem().isForceEquip() && !activeChar.canOverrideCond(PcCondOverride.ITEM_CONDITIONS)) {
            client.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_TAKEN_OFF);
            return;
        }

        final Item[] unequipped = activeChar.getInventory().unEquipItemInBodySlotAndRecord(_slot);
        activeChar.broadcastUserInfo();

        // This can be 0 if the user pressed the right mouse button twice very fast.
        if (unequipped.length > 0) {
            SystemMessage sm = null;
            if (unequipped[0].getEnchantLevel() > 0) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
                sm.addInt(unequipped[0].getEnchantLevel());
            } else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
            }
            sm.addItemName(unequipped[0]);
            client.sendPacket(sm);

            final InventoryUpdate iu = new InventoryUpdate();
            iu.addItems(Arrays.asList(unequipped));
            activeChar.sendInventoryUpdate(iu);
        }
    }
}
