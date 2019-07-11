package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ManagePledgePower extends ServerPacket {
    private final int _action;
    private final Clan _clan;
    private final int _rank;

    public ManagePledgePower(Clan clan, int action, int rank) {
        _clan = clan;
        _action = action;
        _rank = rank;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.MANAGE_PLEDGE_POWER);

        writeInt(_rank);
        writeInt(_action);
        writeInt(_clan.getRankPrivs(_rank).getBitmask());
    }

}
