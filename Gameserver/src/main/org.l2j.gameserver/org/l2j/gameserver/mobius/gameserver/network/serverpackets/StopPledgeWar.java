package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class StopPledgeWar extends IClientOutgoingPacket {
    private final String _pledgeName;
    private final String _playerName;

    public StopPledgeWar(String pledge, String charName) {
        _pledgeName = pledge;
        _playerName = charName;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.STOP_PLEDGE_WAR.writeId(packet);

        writeString(_pledgeName, packet);
        writeString(_playerName, packet);
    }
}