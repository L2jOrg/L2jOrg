package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExDuelUpdateUserInfo extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;

    public ExDuelUpdateUserInfo(L2PcInstance cha) {
        _activeChar = cha;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_DUEL_UPDATE_USER_INFO.writeId(packet);

        writeString(_activeChar.getName(), packet);
        packet.putInt(_activeChar.getObjectId());
        packet.putInt(_activeChar.getClassId().getId());
        packet.putInt(_activeChar.getLevel());
        packet.putInt((int) _activeChar.getCurrentHp());
        packet.putInt(_activeChar.getMaxHp());
        packet.putInt((int) _activeChar.getCurrentMp());
        packet.putInt(_activeChar.getMaxMp());
        packet.putInt((int) _activeChar.getCurrentCp());
        packet.putInt(_activeChar.getMaxCp());
    }
}
