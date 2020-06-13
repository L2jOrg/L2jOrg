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
package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.GameClient;

import static java.util.Objects.isNull;

public abstract class UpdateElementalSpiritPacket extends AbstractElementalSpiritPacket {

    private final byte type;
    private final boolean update;

    UpdateElementalSpiritPacket(byte type, boolean update) {
        this.type = type;
        this.update = update;
    }

    protected void writeUpdate(GameClient client) {
        var player = client.getPlayer();
        writeByte(update);
        writeByte(type);

        if(update) {
            var spirit = player.getElementalSpirit(ElementalType.of(type));

            if(isNull(spirit)) {
                return;
            }

            writeByte(type);
            writeSpiritInfo(spirit);
        }
    }
}
