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

import org.l2j.gameserver.enums.CharacterDeleteFailType;
import org.l2j.gameserver.model.PlayerSelectInfo;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDelete;
import org.l2j.gameserver.network.serverpackets.CharDeleteFail;
import org.l2j.gameserver.network.serverpackets.CharDeleteSuccess;
import org.l2j.gameserver.network.serverpackets.PlayerSelectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CharacterDelete extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterDelete.class);
    // cd
    private int _charSlot;

    @Override
    public void readImpl() {
        _charSlot = readInt();
    }

    @Override
    public void runImpl() {
        try {
            final CharacterDeleteFailType failType = client.markToDeleteChar(_charSlot);
            if (failType == CharacterDeleteFailType.NONE) {// Success!
                client.sendPacket(new CharDeleteSuccess());
                final PlayerSelectInfo charInfo = client.getPlayerSelection(_charSlot);
                EventDispatcher.getInstance().notifyEvent(new OnPlayerDelete(charInfo.getObjectId(), charInfo.getName(), client), Listeners.players());
            } else {
                client.sendPacket(new CharDeleteFail(failType));
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }

        client.sendPacket(new PlayerSelectionInfo(client, 0));
    }
}
