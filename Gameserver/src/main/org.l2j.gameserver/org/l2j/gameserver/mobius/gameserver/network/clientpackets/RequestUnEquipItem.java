package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.PcCondOverride;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.L2EtcItem;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Zoey76
 */
public class RequestUnEquipItem extends IClientIncomingPacket
{
    private int _slot;

    /**
     * Packet type id 0x16 format: cd
     */
    @Override
    public void readImpl(ByteBuffer packet)
    {
        _slot = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        final L2ItemInstance item = activeChar.getInventory().getPaperdollItemByL2ItemId(_slot);
        // Wear-items are not to be unequipped.
        if (item == null)
        {
            return;
        }

        // The English system message say weapon, but it's applied to any equipped item.
        if (activeChar.isAttackingNow() || activeChar.isCastingNow())
        {
            client.sendPacket(SystemMessageId.YOU_CANNOT_CHANGE_WEAPONS_DURING_AN_ATTACK);
            return;
        }

        // Arrows and bolts.
        if ((_slot == L2Item.SLOT_L_HAND) && (item.getItem() instanceof L2EtcItem))
        {
            return;
        }

        // Prevent of unequipping a cursed weapon.
        if ((_slot == L2Item.SLOT_LR_HAND) && (activeChar.isCursedWeaponEquipped() || activeChar.isCombatFlagEquipped()))
        {
            return;
        }

        // Prevent player from unequipping items in special conditions.
        if (activeChar.hasBlockActions() || activeChar.isAlikeDead())
        {
            return;
        }

        if (!activeChar.getInventory().canManipulateWithItemId(item.getId()))
        {
            client.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_TAKEN_OFF);
            return;
        }

        if (item.isWeapon() && item.getWeaponItem().isForceEquip() && !activeChar.canOverrideCond(PcCondOverride.ITEM_CONDITIONS))
        {
            client.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_TAKEN_OFF);
            return;
        }

        final L2ItemInstance[] unequipped = activeChar.getInventory().unEquipItemInBodySlotAndRecord(_slot);
        activeChar.broadcastUserInfo();

        // This can be 0 if the user pressed the right mouse button twice very fast.
        if (unequipped.length > 0)
        {
            SystemMessage sm = null;
            if (unequipped[0].getEnchantLevel() > 0)
            {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
                sm.addInt(unequipped[0].getEnchantLevel());
            }
            else
            {
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
