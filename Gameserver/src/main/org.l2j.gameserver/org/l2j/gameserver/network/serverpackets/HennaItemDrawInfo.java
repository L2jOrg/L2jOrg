package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.L2Henna;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Zoey76
 */
public class HennaItemDrawInfo extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final L2Henna _henna;

    public HennaItemDrawInfo(L2Henna henna, L2PcInstance player) {
        _henna = henna;
        _activeChar = player;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.HENNA_ITEM_INFO.writeId(packet);

        packet.putInt(_henna.getDyeId()); // symbol Id
        packet.putInt(_henna.getDyeItemId()); // item id of dye
        packet.putLong(_henna.getWearCount()); // total amount of dye require
        packet.putLong(_henna.getWearFee()); // total amount of Adena require to draw symbol
        packet.putInt(_henna.isAllowedClass(_activeChar.getClassId()) ? 0x01 : 0x00); // able to draw or not 0 is false and 1 is true
        packet.putLong(_activeChar.getAdena());
        packet.putInt(_activeChar.getINT()); // current INT
        packet.putShort((short) (_activeChar.getINT() + _activeChar.getHennaValue(BaseStats.INT))); // equip INT
        packet.putInt(_activeChar.getSTR()); // current STR
        packet.putShort((short) (_activeChar.getSTR() + _activeChar.getHennaValue(BaseStats.STR))); // equip STR
        packet.putInt(_activeChar.getCON()); // current CON
        packet.putShort((short) (_activeChar.getCON() + _activeChar.getHennaValue(BaseStats.CON))); // equip CON
        packet.putInt(_activeChar.getMEN()); // current MEN
        packet.putShort((short) (_activeChar.getMEN() + _activeChar.getHennaValue(BaseStats.MEN))); // equip MEN
        packet.putInt(_activeChar.getDEX()); // current DEX
        packet.putShort((short) (_activeChar.getDEX() + _activeChar.getHennaValue(BaseStats.DEX))); // equip DEX
        packet.putInt(_activeChar.getWIT()); // current WIT
        packet.putShort((short) (_activeChar.getWIT() + _activeChar.getHennaValue(BaseStats.WIT))); // equip WIT
        packet.putInt(0x00); // TODO: Find me!
    }

    @Override
    protected int size(L2GameClient client) {
        return 95;
    }
}
