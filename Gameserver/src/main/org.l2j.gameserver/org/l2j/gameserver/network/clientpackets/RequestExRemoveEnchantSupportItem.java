package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ExRemoveEnchantSupportItemResult;

/**
 * @author Sdw
 */
public class RequestExRemoveEnchantSupportItem extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final EnchantItemRequest request = activeChar.getRequest(EnchantItemRequest.class);
        if ((request == null) || request.isProcessing()) {
            return;
        }

        final Item supportItem = request.getSupportItem();
        if ((supportItem == null) || (supportItem.getCount() < 1)) {
            request.setSupportItem(Player.ID_NONE);
        }

        request.setTimestamp(System.currentTimeMillis());
        activeChar.sendPacket(ExRemoveEnchantSupportItemResult.STATIC_PACKET);
    }
}
