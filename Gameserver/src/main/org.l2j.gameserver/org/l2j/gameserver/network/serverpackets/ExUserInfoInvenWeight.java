package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExUserInfoInvenWeight extends ServerPacket {
    private final Player _activeChar;

    public ExUserInfoInvenWeight(Player cha) {
        _activeChar = cha;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_USER_INFO_INVEN_WEIGHT);

        writeInt(_activeChar.getObjectId());
        writeInt(_activeChar.getCurrentLoad());
        writeInt(_activeChar.getMaxLoad());
    }

}