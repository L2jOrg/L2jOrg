/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.entity;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;


/**
 * @author evill33t
 */
public class Couple {
    private static final Logger LOGGER = LoggerFactory.getLogger(Couple.class);

    private int _Id = 0;
    private int _player1Id = 0;
    private int _player2Id = 0;
    private boolean _maried = false;
    private Calendar _affiancedDate;
    private Calendar _weddingDate;

    public Couple(int coupleId) {
        _Id = coupleId;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM mods_wedding WHERE id = ?")) {
            ps.setInt(1, _Id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    _player1Id = rs.getInt("player1Id");
                    _player2Id = rs.getInt("player2Id");
                    _maried = rs.getBoolean("married");

                    _affiancedDate = Calendar.getInstance();
                    _affiancedDate.setTimeInMillis(rs.getLong("affianceDate"));

                    _weddingDate = Calendar.getInstance();
                    _weddingDate.setTimeInMillis(rs.getLong("weddingDate"));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception: Couple.load(): " + e.getMessage(), e);
        }
    }

    public Couple(Player player1, Player player2) {
        final int _tempPlayer1Id = player1.getObjectId();
        final int _tempPlayer2Id = player2.getObjectId();

        _player1Id = _tempPlayer1Id;
        _player2Id = _tempPlayer2Id;

        _affiancedDate = Calendar.getInstance();
        _affiancedDate.setTimeInMillis(Calendar.getInstance().getTimeInMillis());

        _weddingDate = Calendar.getInstance();
        _weddingDate.setTimeInMillis(Calendar.getInstance().getTimeInMillis());

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO mods_wedding (id, player1Id, player2Id, married, affianceDate, weddingDate) VALUES (?, ?, ?, ?, ?, ?)")) {
            _Id = IdFactory.getInstance().getNextId();
            ps.setInt(1, _Id);
            ps.setInt(2, _player1Id);
            ps.setInt(3, _player2Id);
            ps.setBoolean(4, false);
            ps.setLong(5, _affiancedDate.getTimeInMillis());
            ps.setLong(6, _weddingDate.getTimeInMillis());
            ps.execute();
        } catch (Exception e) {
            LOGGER.error("Could not create couple: " + e.getMessage(), e);
        }
    }

    public void marry() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE mods_wedding set married = ?, weddingDate = ? where id = ?")) {
            ps.setBoolean(1, true);
            _weddingDate = Calendar.getInstance();
            ps.setLong(2, _weddingDate.getTimeInMillis());
            ps.setInt(3, _Id);
            ps.execute();
            _maried = true;
        } catch (Exception e) {
            LOGGER.error("Could not marry: " + e.getMessage(), e);
        }
    }

    public void divorce() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM mods_wedding WHERE id=?")) {
            ps.setInt(1, _Id);
            ps.execute();
        } catch (Exception e) {
            LOGGER.error("Exception: Couple.divorce(): " + e.getMessage(), e);
        }
    }

    public final int getId() {
        return _Id;
    }

    public final int getPlayer1Id() {
        return _player1Id;
    }

    public final int getPlayer2Id() {
        return _player2Id;
    }

    public final boolean getMaried() {
        return _maried;
    }

    public final Calendar getAffiancedDate() {
        return _affiancedDate;
    }

    public final Calendar getWeddingDate() {
        return _weddingDate;
    }
}
