package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class StopPledgeWar extends ServerPacket {
    private final String _pledgeName;
    private final String _playerName;

    public StopPledgeWar(String pledge, String charName) {
        _pledgeName = pledge;
        _playerName = charName;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.STOP_PLEDGE_WAR);

        writeString(_pledgeName);
        writeString(_playerName);
    }

}