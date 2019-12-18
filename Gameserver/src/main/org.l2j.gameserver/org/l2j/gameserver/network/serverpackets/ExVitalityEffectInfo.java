package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExVitalityEffectInfo extends ServerPacket {
    private final int _vitalityBonus;
    private final int _vitalityItemsRemaining;
    private final int _points;

    public ExVitalityEffectInfo(Player cha) {
        _points = cha.getVitalityPoints();
        _vitalityBonus = (int) cha.getStats().getVitalityExpBonus() * 100;
        _vitalityItemsRemaining = Config.VITALITY_MAX_ITEMS_ALLOWED - cha.getVitalityItemsUsed();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_VITALITY_EFFECT_INFO);

        writeInt(_points);
        writeInt(_vitalityBonus); // Vitality Bonus
        writeShort((short) 0x00); // Vitality additional bonus in %
        writeShort((short) _vitalityItemsRemaining); // How much vitality items remaining for use
        writeShort((short) Config.VITALITY_MAX_ITEMS_ALLOWED); // Max number of items for use
    }

}