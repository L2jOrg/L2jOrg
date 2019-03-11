package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPutItemResultForVariationCancel;
import org.l2j.gameserver.util.Util;

import java.nio.ByteBuffer;

/**
 * Format(ch) d
 *
 * @author -Wooden-
 */
public final class RequestConfirmCancelItem extends IClientIncomingPacket {
    private int _objectId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        final L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            return;
        }

        if (item.getOwnerId() != activeChar.getObjectId()) {
            Util.handleIllegalPlayerAction(client.getActiveChar(), "Warning!! Character " + client.getActiveChar().getName() + " of account " + client.getActiveChar().getAccountName() + " tryied to destroy augment on item that doesn't own.", Config.DEFAULT_PUNISH);
            return;
        }

        if (!item.isAugmented()) {
            activeChar.sendPacket(SystemMessageId.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
            return;
        }

        if (item.isPvp() && !Config.ALT_ALLOW_AUGMENT_PVP_ITEMS) {
            activeChar.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        final long price = VariationData.getInstance().getCancelFee(item.getId(), item.getAugmentation().getMineralId());
        if (price < 0) {
            activeChar.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        activeChar.sendPacket(new ExPutItemResultForVariationCancel(item, price));
    }
}
