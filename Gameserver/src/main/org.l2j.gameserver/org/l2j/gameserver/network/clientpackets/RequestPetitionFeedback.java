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

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Plim
 */
public class RequestPetitionFeedback extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPetitionFeedback.class);
    private static final String INSERT_FEEDBACK = "INSERT INTO petition_feedback VALUES (?,?,?,?,?)";

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

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_FEEDBACK)) {
            statement.setString(1, player.getName());
            statement.setString(2, player.getLastPetitionGmName());
            statement.setInt(3, _rate);
            statement.setString(4, _message);
            statement.setLong(5, System.currentTimeMillis());
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Error while saving petition feedback");
        }
    }

}
