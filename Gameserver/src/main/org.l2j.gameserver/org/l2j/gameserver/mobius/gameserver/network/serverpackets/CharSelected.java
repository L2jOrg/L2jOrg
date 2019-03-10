package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.GameTimeController;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class CharSelected extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final int _sessionId;

    /**
     * @param cha
     * @param sessionId
     */
    public CharSelected(L2PcInstance cha, int sessionId) {
        _activeChar = cha;
        _sessionId = sessionId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CHARACTER_SELECTED.writeId(packet);

        writeString(_activeChar.getName(), packet);
        packet.putInt(_activeChar.getObjectId());
        writeString(_activeChar.getTitle(), packet);
        packet.putInt(_sessionId);
        packet.putInt(_activeChar.getClanId());
        packet.putInt(0x00); // ??
        packet.putInt(_activeChar.getAppearance().getSex() ? 1 : 0);
        packet.putInt(_activeChar.getRace().ordinal());
        packet.putInt(_activeChar.getClassId().getId());
        packet.putInt(0x01); // active ??
        packet.putInt(_activeChar.getX());
        packet.putInt(_activeChar.getY());
        packet.putInt(_activeChar.getZ());
        packet.putDouble(_activeChar.getCurrentHp());
        packet.putDouble(_activeChar.getCurrentMp());
        packet.putLong(_activeChar.getSp());
        packet.putLong(_activeChar.getExp());
        packet.putInt(_activeChar.getLevel());
        packet.putInt(_activeChar.getReputation());
        packet.putInt(_activeChar.getPkKills());
        packet.putInt(GameTimeController.getInstance().getGameTime() % (24 * 60)); // "reset" on 24th hour
        packet.putInt(0x00);
        packet.putInt(_activeChar.getClassId().getId());

        packet.put(new byte[16]);

        packet.putInt(0x00);
        packet.putInt(0x00);
        packet.putInt(0x00);
        packet.putInt(0x00);

        packet.putInt(0x00);

        packet.putInt(0x00);
        packet.putInt(0x00);
        packet.putInt(0x00);
        packet.putInt(0x00);

        packet.put(new byte[28]);
        packet.putInt(0x00);
    }
}
