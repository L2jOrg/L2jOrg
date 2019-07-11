package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.options.VariationFee;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPutIntensiveResultForVariationMake;

/**
 * Fromat(ch) dd
 *
 * @author -Wooden-
 */
public class RequestConfirmRefinerItem extends AbstractRefinePacket {
    private int _targetItemObjId;
    private int _refinerItemObjId;

    @Override
    public void readImpl() {
        _targetItemObjId = readInt();
        _refinerItemObjId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Item targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
        if (targetItem == null) {
            return;
        }

        final Item refinerItem = activeChar.getInventory().getItemByObjectId(_refinerItemObjId);
        if (refinerItem == null) {
            return;
        }

        final VariationFee fee = VariationData.getInstance().getFee(targetItem.getId(), refinerItem.getId());
        if ((fee == null) || !isValid(activeChar, targetItem, refinerItem)) {
            activeChar.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        activeChar.sendPacket(new ExPutIntensiveResultForVariationMake(_refinerItemObjId, refinerItem.getId(), fee.getItemId(), fee.getItemCount()));
    }
}
