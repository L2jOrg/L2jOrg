package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class SurrenderPledgeWar extends ServerPacket {
    private final String _pledgeName;
    private final String _playerName;

    public SurrenderPledgeWar(String pledge, String charName) {
        _pledgeName = pledge;
        _playerName = charName;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.SURRENDER_PLEDGE_WAR);

        writeString(_pledgeName);
        writeString(_playerName);
    }

}