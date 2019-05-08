package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.L2Henna;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This server packet sends the player's henna information.
 *
 * @author Zoey76
 */
public final class HennaInfo extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final List<L2Henna> _hennas = new ArrayList<>();

    public HennaInfo(L2PcInstance player) {
        _activeChar = player;
        for (L2Henna henna : _activeChar.getHennaList()) {
            if (henna != null) {
                _hennas.add(henna);
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.HENNA_INFO.writeId(packet);

        packet.putShort((short) _activeChar.getHennaValue(BaseStats.INT)); // equip INT
        packet.putShort((short) _activeChar.getHennaValue(BaseStats.STR)); // equip STR
        packet.putShort((short) _activeChar.getHennaValue(BaseStats.CON)); // equip CON
        packet.putShort((short) _activeChar.getHennaValue(BaseStats.MEN)); // equip MEN
        packet.putShort((short) _activeChar.getHennaValue(BaseStats.DEX)); // equip DEX
        packet.putShort((short) _activeChar.getHennaValue(BaseStats.WIT)); // equip WIT
        packet.putShort((short) 0x00); // equip LUC
        packet.putShort((short) 0x00); // equip CHA
        packet.putInt(3 - _activeChar.getHennaEmptySlots()); // Slots
        packet.putInt(_hennas.size()); // Size
        for (L2Henna henna : _hennas) {
            packet.putInt(henna.getDyeId());
            packet.putInt(henna.isAllowedClass(_activeChar.getClassId()) ? 0x01 : 0x00);
        }
        packet.putInt(0x00); // Premium Slot Dye ID
        packet.putInt(0x00); // Premium Slot Dye Time Left
        packet.putInt(0x00); // Premium Slot Dye ID isValid
    }

    @Override
    protected int size(L2GameClient client) {
        return 41 + _hennas.size() * 8;
    }
}
