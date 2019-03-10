package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExPutItemResultForVariationMake;

import java.nio.ByteBuffer;

/**
 * Format:(ch) d
 *
 * @author -Wooden-
 */
public final class RequestConfirmTargetItem extends AbstractRefinePacket {
    private int _itemObjId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _itemObjId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemObjId);
        if (item == null) {
            return;
        }

        if (!VariationData.getInstance().hasFeeData(item.getId())) {
            client.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        if (!isValid(activeChar, item)) {
            // Different system message here
            if (item.isAugmented()) {
                client.sendPacket(SystemMessageId.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN);
                return;
            }

            client.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        client.sendPacket(new ExPutItemResultForVariationMake(_itemObjId, item.getId()));
    }
}
