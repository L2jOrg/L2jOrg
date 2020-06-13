/*
 * Copyright © 2019-2020 L2JOrg
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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

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
        writeId(ServerExPacketId.EX_VITALITY_EFFECT_INFO);

        writeInt(_points);
        writeInt(_vitalityBonus); // Vitality Bonus
        writeShort((short) 0x00); // Vitality additional bonus in %
        writeShort((short) _vitalityItemsRemaining); // How much vitality items remaining for use
        writeShort((short) Config.VITALITY_MAX_ITEMS_ALLOWED); // Max number of items for use
    }

}