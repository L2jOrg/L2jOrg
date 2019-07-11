package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ChairSit extends ServerPacket {
    private final Player _activeChar;
    private final int _staticObjectId;

    /**
     * @param player
     * @param staticObjectId
     */
    public ChairSit(Player player, int staticObjectId) {
        _activeChar = player;
        _staticObjectId = staticObjectId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.CHAIR_SIT);

        writeInt(_activeChar.getObjectId());
        writeInt(_staticObjectId);
    }

}
