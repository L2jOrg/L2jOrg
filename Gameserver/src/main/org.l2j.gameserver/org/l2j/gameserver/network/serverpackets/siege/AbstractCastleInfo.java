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
package org.l2j.gameserver.network.serverpackets.siege;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public abstract class AbstractCastleInfo extends ServerPacket {

    protected final Castle castle;

    protected AbstractCastleInfo(Castle castle) {
        this.castle = Objects.requireNonNull(castle);
    }

    protected void writeCastleInfo(WritableBuffer buffer) {
        buffer.writeInt(castle.getId());

        final var owner = castle.getOwner();
        if(nonNull(owner)) {
            buffer.writeInt(owner.getId());
            buffer.writeInt(owner.getCrestId());
            buffer.writeSizedString(owner.getName());
            buffer.writeSizedString(owner.getLeaderName());
        } else {
            buffer.writeInt(0);
            buffer.writeInt(0);
            buffer.writeSizedString("-");
            buffer.writeSizedString("-");
        }
    }
}
