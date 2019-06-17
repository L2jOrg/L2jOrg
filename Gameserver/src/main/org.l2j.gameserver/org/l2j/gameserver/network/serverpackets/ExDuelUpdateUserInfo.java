package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
public class ExDuelUpdateUserInfo extends ServerPacket {
    private final L2PcInstance _activeChar;

    public ExDuelUpdateUserInfo(L2PcInstance cha) {
        _activeChar = cha;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_DUEL_UPDATE_USER_INFO);

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
