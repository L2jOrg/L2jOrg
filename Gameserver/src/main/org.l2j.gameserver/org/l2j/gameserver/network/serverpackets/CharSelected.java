package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class CharSelected extends ServerPacket {
    private final Player _activeChar;
    private final int _sessionId;

    public CharSelected(Player cha, int sessionId) {
        _activeChar = cha;
        _sessionId = sessionId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CHARACTER_SELECTED);

        writeString(_activeChar.getName());
        writeInt(_activeChar.getObjectId());
        writeString(_activeChar.getTitle());
        writeInt(_sessionId);
        writeInt(_activeChar.getClanId());
        writeInt(0x00); // ??
        writeInt(_activeChar.getAppearance().isFemale() ? 1 : 0);
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
        writeInt(WorldTimeController.getInstance().getGameTime() % (24 * 60)); // "reset" on 24th hour
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
