package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.data.xml.CombinationItemsManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.model.items.combination.CombinationItem;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneOK;

import java.util.List;

/**
 * @author UnAfraid
 */
public class RequestNewEnchantPushOne extends ClientPacket {
    private int _objectId;

    @Override
    public void readImpl() {
        _objectId = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        } else if (player.isInStoreMode()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        } else if (player.isProcessingTransaction() || player.isProcessingRequest()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }

        final CompoundRequest request = new CompoundRequest(player);
        if (!player.addRequest(request)) {
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }

        // Make sure player owns this item.
        request.setItemOne(_objectId);
        final Item itemOne = request.getItemOne();
        if (itemOne == null) {
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            player.removeRequest(request.getClass());
            return;
        }

        final List<CombinationItem> combinationItems = CombinationItemsManager.getInstance().getItemsByFirstSlot(itemOne.getId());

        // Not implemented or not able to merge!
        if (combinationItems.isEmpty()) {
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            player.removeRequest(request.getClass());
            return;
        }

        client.sendPacket(ExEnchantOneOK.STATIC_PACKET);
    }
}
