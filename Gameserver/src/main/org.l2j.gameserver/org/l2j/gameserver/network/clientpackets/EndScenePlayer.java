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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.MovieHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author JIV
 */
public final class EndScenePlayer extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndScenePlayer.class);
    private int _movieId;

    @Override
    public void readImpl() {
        _movieId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if ((activeChar == null) || (_movieId == 0)) {
            return;
        }

        final MovieHolder holder = activeChar.getMovieHolder();
        if ((holder == null) || (holder.getMovie().getClientId() != _movieId)) {
            LOGGER.warn("Player " + client + " sent EndScenePlayer with wrong movie id: " + _movieId);
            return;
        }
        activeChar.stopMovie();
    }
}