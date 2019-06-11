package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class StartPledgeWar extends IClientOutgoingPacket {
    private final String _pledgeName;
    private final String _playerName;

    public StartPledgeWar(String pledge, String charName) {
        _pledgeName = pledge;
        _playerName = charName;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.START_PLEDGE_WAR);

        writeString(_playerName);
        writeString(_pledgeName);
    }

}