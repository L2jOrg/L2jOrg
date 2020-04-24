package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 */
public class ExDuelUpdateUserInfo extends ServerPacket {
    private final Player _activeChar;

    public ExDuelUpdateUserInfo(Player cha) {
        _activeChar = cha;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_DUEL_UPDATE_USER_INFO);

        writeString(_activeChar.getName());
        writeInt(_activeChar.getObjectId());
        writeInt(_activeChar.getClassId().getId());
        writeInt(_activeChar.getLevel());
        writeInt((int) _activeChar.getCurrentHp());
        writeInt(_activeChar.getMaxHp());
        writeInt((int) _activeChar.getCurrentMp());
        writeInt(_activeChar.getMaxMp());
        writeInt((int) _activeChar.getCurrentCp());
        writeInt(_activeChar.getMaxCp());
    }

}
