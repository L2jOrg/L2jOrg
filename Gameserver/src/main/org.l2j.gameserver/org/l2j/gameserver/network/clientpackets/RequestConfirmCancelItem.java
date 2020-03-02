package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPutItemResultForVariationCancel;
import org.l2j.gameserver.util.GameUtils;

/**
 * Format(ch) d
 *
 * @author -Wooden-
 */
public final class RequestConfirmCancelItem extends ClientPacket {
    private int _objectId;

    @Override
    public void readImpl() {
        _objectId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }
        final Item item = activeChar.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            return;
        }

        if (item.getOwnerId() != activeChar.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(client.getPlayer(), "Warning!! Character " + client.getPlayer().getName() + " of account " + client.getPlayer().getAccountName() + " tryied to destroy augment on item that doesn't own.");
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
