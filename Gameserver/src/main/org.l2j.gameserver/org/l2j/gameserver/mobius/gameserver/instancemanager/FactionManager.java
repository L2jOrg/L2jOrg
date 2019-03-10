/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains objectId and factionId for all players.
 *
 * @author Mobius
 */
public class FactionManager {
    private static Logger LOGGER = Logger.getLogger(FactionManager.class.getName());
    private final Map<Integer, Integer> _playerFactions = new ConcurrentHashMap<>();

    protected FactionManager() {
        loadAll();
    }

    public static FactionManager getInstance() {
        return SingletonHolder._instance;
    }

    private void loadAll() {
        _playerFactions.clear();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT charId, faction FROM characters")) {
            while (rs.next()) {
                _playerFactions.put(rs.getInt(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not load character faction information: " + e.getMessage(), e);
        }
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _playerFactions.size() + " character faction values.");
    }

    public final int getFactionByCharId(int id) {
        if (id <= 0) {
            return 0;
        }

        Integer factionId = _playerFactions.get(id);
        if (factionId != null) {
            return factionId;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT faction FROM characters WHERE charId=?")) {
            ps.setInt(1, id);
            try (ResultSet rset = ps.executeQuery()) {
                if (rset.next()) {
                    factionId = rset.getInt(1);
                    _playerFactions.put(id, factionId);
                    return factionId;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not check existing char id: " + e.getMessage(), e);
        }

        return 0; // not found
    }

    public final boolean isSameFaction(L2PcInstance player1, L2PcInstance player2) {
        // TODO: Maybe add support for multiple factions?
        return (player1.isGood() && player2.isGood()) || (player1.isEvil() && player2.isEvil());
    }

    private static class SingletonHolder {
        protected static final FactionManager _instance = new FactionManager();
    }
}
