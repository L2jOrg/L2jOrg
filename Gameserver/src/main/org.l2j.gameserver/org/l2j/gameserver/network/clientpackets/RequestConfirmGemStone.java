package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.options.VariationFee;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPutCommissionResultForVariationMake;

/**
 * Format:(ch) dddd
 *
 * @author -Wooden-
 */
public final class RequestConfirmGemStone extends AbstractRefinePacket {
    private int _targetItemObjId;
    private int _mineralItemObjId;
    private int _feeItemObjId;
    private long _feeCount;

    @Override
    public void readImpl() {
        _targetItemObjId = readInt();
        _mineralItemObjId = readInt();
        _feeItemObjId = readInt();
        _feeCount = readLong();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
        if (targetItem == null) {
            return;
        }

        final L2ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(_mineralItemObjId);
        if (refinerItem == null) {
            return;
        }

        final L2ItemInstance gemStoneItem = activeChar.getInventory().getItemByObjectId(_feeItemObjId);
        if (gemStoneItem == null) {
            return;
        }

        final VariationFee fee = VariationData.getInstance().getFee(targetItem.getId(), refinerItem.getId());
        if (!isValid(activeChar, targetItem, refinerItem, gemStoneItem, fee)) {
            client.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        // Check for fee count
        if (_feeCount != fee.getItemCount()) {
            client.sendPacket(SystemMessageId.GEMSTONE_QUANTITY_IS_INCORRECT);
            return;
        }

        client.sendPacket(new ExPutCommissionResultForVariationMake(_feeItemObjId, _feeCount, gemStoneItem.getId()));
    }
}
