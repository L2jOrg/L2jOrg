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
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.ArrayList;
import java.util.List;

public class ExAbnormalStatusUpdateFromTarget extends ServerPacket {
    private final Creature _character;
    private final List<BuffInfo> _effects;

    public ExAbnormalStatusUpdateFromTarget(Creature creature) {
        _character = creature;
        var effectList = creature.getEffectList().getEffects();
        _effects = new ArrayList<>(effectList.size());
        for (BuffInfo info : effectList) {
            if(info.isInUse() && !info.getSkill().isToggle()) {
                _effects.add(info);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_ABNORMAL_STATUS_UPDATE_FROM_TARGET, buffer );

        buffer.writeInt(_character.getObjectId());
        buffer.writeShort(_effects.size());

        for (BuffInfo info : _effects) {
            buffer.writeInt(info.getSkill().getDisplayId());
            buffer.writeShort(info.getSkill().getDisplayLevel());
            buffer.writeShort(info.getSkill().getAbnormalType().getClientId());
            writeOptionalD(info.getSkill().isAura() ? -1 : info.getTime(), buffer);
            buffer.writeInt(info.getEffectorObjectId());
        }
    }

}
