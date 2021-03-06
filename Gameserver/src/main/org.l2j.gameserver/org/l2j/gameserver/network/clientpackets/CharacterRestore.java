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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.PlayerSelectInfo;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerRestore;
import org.l2j.gameserver.network.serverpackets.PlayerSelectionInfo;

import static java.util.Objects.nonNull;

public final class CharacterRestore extends ClientPacket {

    private int slot;

    @Override
    public void readImpl() {
        slot = readInt();
    }

    @Override
    public void runImpl() {
        if (!client.getFloodProtectors().getCharacterSelect().tryPerformAction("CharacterRestore")) {
            return;
        }

        client.restore(slot);
        client.sendPacket(new PlayerSelectionInfo(client, slot));

        final PlayerSelectInfo playerInfo = client.getPlayerSelection(slot);
        if(nonNull(playerInfo)) {
            EventDispatcher.getInstance().notifyEvent(new OnPlayerRestore(playerInfo.getObjectId(), playerInfo.getName(), client));
        }
    }
}
