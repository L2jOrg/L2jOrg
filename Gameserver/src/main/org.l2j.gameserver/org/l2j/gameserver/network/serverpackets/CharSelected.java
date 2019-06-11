package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

public class CharSelected extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final int _sessionId;

    public CharSelected(L2PcInstance cha, int sessionId) {
        _activeChar = cha;
        _sessionId = sessionId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.CHARACTER_SELECTED);

        writeString(_activeChar.getName());
        writeInt(_activeChar.getObjectId());
        writeString(_activeChar.getTitle());
        writeInt(_sessionId);
        writeInt(_activeChar.getClanId());
        writeInt(0x00); // ??
        writeInt(_activeChar.getAppearance().getSex() ? 1 : 0);
        writeInt(_activeChar.getRace().ordinal());
        writeInt(_activeChar.getClassId().getId());
        writeInt(0x01); // active ??
        writeInt(_activeChar.getX());
        writeInt(_activeChar.getY());
        writeInt(_activeChar.getZ());
        writeDouble(_activeChar.getCurrentHp());
        writeDouble(_activeChar.getCurrentMp());
        writeLong(_activeChar.getSp());
        writeLong(_activeChar.getExp());
        writeInt(_activeChar.getLevel());
        writeInt(_activeChar.getReputation());
        writeInt(_activeChar.getPkKills());
        writeInt(GameTimeController.getInstance().getGameTime() % (24 * 60)); // "reset" on 24th hour
        writeInt(0x00);
        writeInt(_activeChar.getClassId().getId());

        writeBytes(new byte[16]);

        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);

        writeInt(0x00);

        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);

        writeBytes(new byte[28]);
        writeInt(0x00);
    }

}
