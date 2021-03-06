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

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.friend.FriendListPacket;
import org.l2j.gameserver.settings.GeneralSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author mrTJO & UnAfraid
 */
public final class RequestExFriendListExtended extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        if (!getSettings(GeneralSettings.class).allowMail()) {
            return;
        }

        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new FriendListPacket(activeChar));
    }
}
