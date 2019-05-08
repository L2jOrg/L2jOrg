package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class SurrenderPledgeWar extends IClientOutgoingPacket {
    private final String _pledgeName;
    private final String _playerName;

    public SurrenderPledgeWar(String pledge, String charName) {
        _pledgeName = pledge;
        _playerName = charName;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SURRENDER_PLEDGE_WAR.writeId(packet);

        writeString(_pledgeName, packet);
        writeString(_playerName, packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9 + (_playerName.length() + _pledgeName.length() ) * 2;
    }
}