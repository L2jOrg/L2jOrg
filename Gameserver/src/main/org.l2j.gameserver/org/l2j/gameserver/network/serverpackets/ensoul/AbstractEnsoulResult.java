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
package org.l2j.gameserver.network.serverpackets.ensoul;

import org.l2j.gameserver.engine.item.EnsoulOption;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
abstract class AbstractEnsoulResult extends ServerPacket {

    private final Item item;
    private final boolean success;

    AbstractEnsoulResult(boolean success, Item item) {
        this.success = success;
        this.item = item;
    }

    protected void writeResult() {
        writeByte(success);
        writeEnsoul(item.getSpecialAbility());
        writeEnsoul(item.getAdditionalSpecialAbility());
    }

    private void writeEnsoul(EnsoulOption ensoul) {
        if(nonNull(ensoul)) {
            writeByte(0x01); // ensoul amount
            writeInt(ensoul.id());
        } else {
            writeByte(0x00);
        }
    }
}
