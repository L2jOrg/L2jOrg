package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.ItemAuctionManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.itemauction.ItemAuction;
import org.l2j.gameserver.model.itemauction.ItemAuctionInstance;
import org.l2j.gameserver.network.serverpackets.ExItemAuctionInfoPacket;

import java.nio.ByteBuffer;

/**
 * @author Forsaiken
 */
public final class RequestInfoItemAuction extends IClientIncomingPacket {
    private int _instanceId;

    @Override
    public void readImpl() {
        _instanceId = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getItemAuction().tryPerformAction("RequestInfoItemAuction")) {
            return;
        }

        final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(_instanceId);
        if (instance == null) {
            return;
        }

        final ItemAuction auction = instance.getCurrentAuction();
        if (auction == null) {
            return;
        }

        activeChar.updateLastItemAuctionRequest();
        client.sendPacket(new ExItemAuctionInfoPacket(true, auction, instance.getNextAuction()));
    }
}