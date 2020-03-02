package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.Henna;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.HennaEquipList;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zoey76
 */
public final class RequestHennaEquip extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHennaEquip.class);
    private int _symbolId;

    @Override
    public void readImpl() {
        _symbolId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("HennaEquip")) {
            return;
        }

        if (activeChar.getHennaEmptySlots() == 0) {
            activeChar.sendPacket(SystemMessageId.NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final Henna henna = HennaData.getInstance().getHenna(_symbolId);
        if (henna == null) {
            LOGGER.warn("Invalid Henna Id: " + _symbolId + " from player " + activeChar);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final long _count = activeChar.getInventory().getInventoryItemCount(henna.getDyeItemId(), -1);
        if (henna.isAllowedClass(activeChar.getClassId()) && (_count >= henna.getWearCount()) && (activeChar.getAdena() >= henna.getWearFee()) && activeChar.addHenna(henna)) {
            activeChar.destroyItemByItemId("Henna", henna.getDyeItemId(), henna.getWearCount(), activeChar, true);
            activeChar.getInventory().reduceAdena("Henna", henna.getWearFee(), activeChar, activeChar.getLastFolkNPC());
            final InventoryUpdate iu = new InventoryUpdate();
            iu.addModifiedItem(activeChar.getInventory().getAdenaInstance());
            activeChar.sendInventoryUpdate(iu);
            activeChar.sendPacket(new HennaEquipList(activeChar));
            activeChar.sendPacket(SystemMessageId.THE_SYMBOL_HAS_BEEN_ADDED);
        } else {
            activeChar.sendPacket(SystemMessageId.THE_SYMBOL_CANNOT_BE_DRAWN);
            if (!activeChar.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !henna.isAllowedClass(activeChar.getClassId())) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Exploit attempt: Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tryed to add a forbidden henna.");
            }
            client.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}
