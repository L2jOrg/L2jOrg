/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.settings.RateSettings;

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
        _vitalityItemsRemaining = RateSettings.maxItemsVitality() - cha.getVitalityItemsUsed();

    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_VITALITY_EFFECT_INFO, buffer );

        buffer.writeInt(_points);
        buffer.writeInt(_vitalityBonus); // Vitality Bonus
        buffer.writeShort(0x00); // Vitality additional bonus in %
        buffer.writeShort(_vitalityItemsRemaining); // How much vitality items remaining for use
        buffer.writeShort(RateSettings.maxItemsVitality()); // Max number of items for use
    }

}