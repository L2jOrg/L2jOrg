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

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collections;
import java.util.Set;

public final class ExEnchantSkillInfo extends ServerPacket {
    private final Set<Integer> _routes;

    private final int _skillId;
    private final int _skillLevel;
    private final int _skillSubLevel;
    private final int _currentSubLevel;

    public ExEnchantSkillInfo(int skillId, int skillLevel, int skillSubLevel, int currentSubLevel) {
        _skillId = skillId;
        _skillLevel = skillLevel;
        _skillSubLevel = skillSubLevel;
        _currentSubLevel = currentSubLevel;
        _routes = Collections.emptySet();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_ENCHANT_SKILL_INFO);
        writeInt(_skillId);
        writeShort((short) _skillLevel);
        writeShort((short) _skillSubLevel);
        writeInt((_skillSubLevel % 1000) == 0);
        writeInt(_skillSubLevel > 1000 ? 1 : 0);
        writeInt(_routes.size());
        _routes.forEach(route ->
        {
            final int routeId = route / 1000;
            final int currentRouteId = _skillSubLevel / 1000;
            final int subLevel = _currentSubLevel > 0 ? (route + (_currentSubLevel % 1000)) - 1 : route;
            writeShort((short) _skillLevel);
            writeShort((short)( currentRouteId != routeId ? subLevel : Math.min(subLevel + 1, route)));
        });
    }

}
