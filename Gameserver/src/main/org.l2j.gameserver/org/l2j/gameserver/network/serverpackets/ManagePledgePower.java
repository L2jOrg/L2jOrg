package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ManagePledgePower extends IClientOutgoingPacket {
    private final int _action;
    private final L2Clan _clan;
    private final int _rank;

    public ManagePledgePower(L2Clan clan, int action, int rank) {
        _clan = clan;
        _action = action;
        _rank = rank;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.MANAGE_PLEDGE_POWER.writeId(packet);

        packet.putInt(_rank);
        packet.putInt(_action);
        packet.putInt(_clan.getRankPrivs(_rank).getBitmask());
    }
}
