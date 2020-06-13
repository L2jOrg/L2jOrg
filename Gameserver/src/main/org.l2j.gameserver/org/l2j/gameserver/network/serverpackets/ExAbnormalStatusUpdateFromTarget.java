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

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExAbnormalStatusUpdateFromTarget extends ServerPacket {
    private final Creature _character;
    private final List<BuffInfo> _effects;

    public ExAbnormalStatusUpdateFromTarget(Creature character) {
        //@formatter:off
        _character = character;
        _effects = character.getEffectList().getEffects()
                .stream()
                .filter(Objects::nonNull)
                .filter(BuffInfo::isInUse)
                .filter(b -> !b.getSkill().isToggle())
                .collect(Collectors.toList());
        //@formatter:on
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_ABNORMAL_STATUS_UPDATE_FROM_TARGET);

        writeInt(_character.getObjectId());
        writeShort((short) _effects.size());

        for (BuffInfo info : _effects) {
            writeInt(info.getSkill().getDisplayId());
            writeShort((short) info.getSkill().getDisplayLevel());
            // writeShort((short)info.getSkill().getSubLevel());
            writeShort((short) info.getSkill().getAbnormalType().getClientId());
            writeOptionalD(info.getSkill().isAura() ? -1 : info.getTime());
            writeInt(info.getEffectorObjectId());
        }
    }

}
