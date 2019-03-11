package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExVitalityEffectInfo extends IClientOutgoingPacket {
    private final int _vitalityBonus;
    private final int _vitalityItemsRemaining;
    private final int _points;

    public ExVitalityEffectInfo(L2PcInstance cha) {
        _points = cha.getVitalityPoints();
        _vitalityBonus = (int) cha.getStat().getVitalityExpBonus() * 100;
        _vitalityItemsRemaining = Config.VITALITY_MAX_ITEMS_ALLOWED - cha.getVitalityItemsUsed();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_VITALITY_EFFECT_INFO.writeId(packet);

        packet.putInt(_points);
        packet.putInt(_vitalityBonus); // Vitality Bonus
        packet.putShort((short) 0x00); // Vitality additional bonus in %
        packet.putShort((short) _vitalityItemsRemaining); // How much vitality items remaining for use
        packet.putShort((short) Config.VITALITY_MAX_ITEMS_ALLOWED); // Max number of items for use
    }
}