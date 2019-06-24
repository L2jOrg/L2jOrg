package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExVariationCancelResult;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.util.GameUtils;

/**
 * Format(ch) d
 *
 * @author -Wooden-
 */
public final class RequestRefineCancel extends ClientPacket {
    private int _targetItemObjId;

    @Override
    public void readImpl() {
        _targetItemObjId = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
        if (targetItem == null) {
            client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            return;
        }

        if (targetItem.getOwnerId() != activeChar.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(client.getActiveChar(), "Warning!! Character " + client.getActiveChar().getName() + " of account " + client.getActiveChar().getAccountName() + " tryied to augment item that doesn't own.", Config.DEFAULT_PUNISH);
            return;
        }

        // cannot remove augmentation from a not augmented item
        if (!targetItem.isAugmented()) {
            client.sendPacket(SystemMessageId.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
            client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            return;
        }

        // get the price
        final long price = VariationData.getInstance().getCancelFee(targetItem.getId(), targetItem.getAugmentation().getMineralId());
        if (price < 0) {
            client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            return;
        }

        // try to reduce the players adena
        if (!activeChar.reduceAdena("RequestRefineCancel", price, targetItem, true)) {
            client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        // unequip item
        if (targetItem.isEquipped()) {
            activeChar.disarmWeapons();
        }

        // remove the augmentation
        targetItem.removeAugmentation();

        // send ExVariationCancelResult
        client.sendPacket(ExVariationCancelResult.STATIC_PACKET_SUCCESS);

        // send inventory update
        InventoryUpdate iu = new InventoryUpdate();
        iu.addModifiedItem(targetItem);
        activeChar.sendInventoryUpdate(iu);
    }
}
