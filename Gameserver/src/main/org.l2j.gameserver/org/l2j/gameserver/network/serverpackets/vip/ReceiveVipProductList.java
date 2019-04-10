package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

public class ReceiveVipProductList extends IClientOutgoingPacket {


    @Override
    protected void writeImpl(L2GameClient client, ByteBuffer packet) throws Exception {
        OutgoingPackets.RECEIVE_VIP_PRODUCT_LIST.writeId(packet);

        var player = client.getActiveChar();

        packet.putLong(player.getAdena());
        packet.putLong(1); // Rusty Coin Amount
        packet.putLong(2); // Silver Coin Amount
        packet.put((byte) 1); // Show Reward tab

        var count = 16;
        packet.putInt(count);  // count

        for (var i = 0; i < count; i++) {
            packet.putInt(100002); // product id ?
            packet.put((byte) 13); // Type 11 - Supplier; 12 - Cosmetic; 13 - VIP; 14 - Event; 15 - Reward
            packet.put((byte) 0); // Payment Type 0 - NCoin; 3 - Vip Coin
            packet.putInt(0); // price NCoin ?
            packet.putInt(0); //  Price Vip Coin ?
            packet.put((byte) 0); // NEW - 6; HOT - 5 ... UNK
            packet.put((byte) 0); // VIP Tier
            packet.put((byte) 7); // unk
            var itemsReceived  = 1;
            packet.put((byte) itemsReceived); // Items received count

            for (var j =0; j < itemsReceived; j++) {
                packet.putInt(71254 + i); // item Id
                packet.putInt(1 + i); // count
            }
        }
    }

}
