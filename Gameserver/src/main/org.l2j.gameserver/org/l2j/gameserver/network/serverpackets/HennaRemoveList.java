package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.L2Henna;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Zoey76
 */
public class HennaRemoveList extends IClientOutgoingPacket {
    private final L2PcInstance _player;

    public HennaRemoveList(L2PcInstance player) {
        _player = player;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.HENNA_UNEQUIP_LIST.writeId(packet);

        packet.putLong(_player.getAdena());
        packet.putInt(0x03); // seems to be max size
        packet.putInt(3 - _player.getHennaEmptySlots());

        for (L2Henna henna : _player.getHennaList()) {
            if (henna != null) {
                packet.putInt(henna.getDyeId());
                packet.putInt(henna.getDyeItemId());
                packet.putLong(henna.getCancelCount());
                packet.putLong(henna.getCancelFee());
                packet.putInt(0x00);
            }
        }
    }
}
