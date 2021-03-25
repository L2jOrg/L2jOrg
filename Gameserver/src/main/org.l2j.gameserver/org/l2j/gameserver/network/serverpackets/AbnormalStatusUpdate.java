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
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

public class AbnormalStatusUpdate extends ServerPacket {
    private final List<BuffInfo> _effects = new ArrayList<>();

    public void addSkill(BuffInfo info) {
        if (!info.getSkill().isHealingPotionSkill()) {
            _effects.add(info);
        }
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.ABNORMAL_STATUS_UPDATE, buffer );

        buffer.writeShort(_effects.size());
        for (BuffInfo info : _effects) {
            if ((info != null) && info.isInUse()) {
                buffer.writeInt(info.getSkill().getDisplayId());
                buffer.writeShort(info.getSkill().getDisplayLevel());
                buffer.writeInt(info.getSkill().getAbnormalType().getClientId());
                writeOptionalD(info.getSkill().isAura() ? -1 : info.getTime(), buffer);
            }
        }
    }

}
