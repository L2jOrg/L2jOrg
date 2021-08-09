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

import org.l2j.gameserver.data.database.dao.PetitionDAO;
import org.l2j.gameserver.model.actor.instance.Player;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Plim
 */
public class RequestPetitionFeedback extends ClientPacket {

    // cdds
    // private int _unknown;
    private int _rate; // 4=VeryGood, 3=Good, 2=Fair, 1=Poor, 0=VeryPoor
    private String _message;

    @Override
    public void readImpl() {
        // _unknown =
        readInt(); // unknown
        _rate = readInt();
        _message = readString();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();

        if ((player == null) || (player.getLastPetitionGmName() == null)) {
            return;
        }

        if ((_rate > 4) || (_rate < 0)) {
            return;
        }

        getDAO(PetitionDAO.class).saveFeedback(player.getName(), player.getLastPetitionGmName(), _rate, _message, System.currentTimeMillis());
    }

}
