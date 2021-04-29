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
import org.l2j.gameserver.network.serverpackets.StartRotation;
import org.l2j.gameserver.settings.CharacterSettings;

import static java.util.Objects.isNull;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class StartRotating extends ClientPacket {
    private int _degree;
    private int _side;

    @Override
    public void readImpl() {
        _degree = readInt();
        _side = readInt();
    }

    @Override
    public void runImpl() {
        if (!CharacterSettings.enableKeyboardMovement()) {
            return;
        }

        final Player player = client.getPlayer();
        if (isNull(player) || player.isAlikeDead()) {
            return;
        }
        player.broadcastPacket(new StartRotation(player.getObjectId(), _degree, _side, 0));
    }
}
