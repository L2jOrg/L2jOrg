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

import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExEnsoulResult extends ServerPacket {
    private final int _success;
    private final Item _item;

    public ExEnsoulResult(int success, Item item) {
        _success = success;
        _item = item;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_ENSOUL_RESULT);
        writeByte((byte) _success); // success / failure
        writeByte((byte) _item.getSpecialAbilities().size());
        for (EnsoulOption option : _item.getSpecialAbilities()) {
            writeInt(option.getId());
        }
        writeByte((byte) _item.getAdditionalSpecialAbilities().size());
        for (EnsoulOption option : _item.getAdditionalSpecialAbilities()) {
            writeInt(option.getId());
        }
    }

}
