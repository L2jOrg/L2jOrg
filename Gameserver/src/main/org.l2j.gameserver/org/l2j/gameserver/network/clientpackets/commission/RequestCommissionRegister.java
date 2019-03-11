package org.l2j.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author NosBit
 */
public class RequestCommissionRegister extends IClientIncomingPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestCommissionRegister.class);

    private int _itemObjectId;
    private long _pricePerUnit;
    private long _itemCount;
    private int _durationType; // -1 = None, 0 = 1 Day, 1 = 3 Days, 2 = 5 Days, 3 = 7 Days

    @Override
    public void readImpl(ByteBuffer packet) {
        _itemObjectId = packet.getInt();
        readString(packet); // Item Name they use it for search we will use server side available names.
        _pricePerUnit = packet.getLong();
        _itemCount = packet.getLong();
        _durationType = packet.getInt();
        // packet.getInt(); // Unknown
        // packet.getInt(); // Unknown
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if ((_durationType < 0) || (_durationType > 3)) {
            LOGGER.warn("Player {} sent incorrect commission duration type: {}.", player, _durationType);
            return;
        }

        if (!CommissionManager.isPlayerAllowedToInteract(player)) {
            client.sendPacket(ExCloseCommission.STATIC_PACKET);
            return;
        }

        CommissionManager.getInstance().registerItem(player, _itemObjectId, _itemCount, _pricePerUnit, (byte) ((_durationType * 2) + 1));
    }
}
