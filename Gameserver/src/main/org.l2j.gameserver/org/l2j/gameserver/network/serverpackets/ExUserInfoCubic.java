package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExUserInfoCubic extends ServerPacket {
    private final Player _activeChar;

    public ExUserInfoCubic(Player cha) {
        _activeChar = cha;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_USER_INFO_CUBIC);

        writeInt(_activeChar.getObjectId());
        writeShort((short) _activeChar.getCubics().size());

        _activeChar.getCubics().keySet().forEach(key -> writeShort(key.shortValue()));

        writeInt(_activeChar.getAgathionId());
    }

}
