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
package org.l2j.gameserver.data.sql.impl;

import io.github.joealisson.primitive.CHashIntIntMap;
import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.settings.GeneralSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * Loads name and access level for all players.
 *
 * @author JoeAlisson
 */
public class PlayerNameTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerNameTable.class);

    private final IntMap<String> playerData = new CHashIntMap<>();
    private final IntIntMap accessLevels = new CHashIntIntMap();

    private PlayerNameTable() {
        if (getSettings(GeneralSettings.class).cachePlayersName()) {
            loadAll();
        }
    }

    public final void addName(Player player) {
        if (nonNull(player)) {
            addName(player.getObjectId(), player.getName());
            accessLevels.put(player.getObjectId(), player.getAccessLevel().getLevel());
        }
    }

    private void addName(int objectId, String name) {
        if (nonNull(name)) {
            if (!name.equals(playerData.get(objectId))) {
                playerData.put(objectId, name);
            }
        }
    }

    public final void removeName(int objId) {
        playerData.remove(objId);
        accessLevels.remove(objId);
    }

    public final int getIdByName(String name) {
        if (isNullOrEmpty(name)){
            return -1;
        }

        for (var entry : playerData.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) {
                return entry.getKey();
            }
        }

        if (getSettings(GeneralSettings.class).cachePlayersName()) {
            return -1;
        }

        var characterData = getDAO(PlayerDAO.class).findIdAndAccessLevelByName(name);
        if(nonNull(characterData)) {
            playerData.put(characterData.getCharId(), name);
            accessLevels.put(characterData.getCharId(), characterData.getAccessLevel());
            return  characterData.getCharId();
        }

        return -1; // not found
    }

    public final String getNameById(int id) {
        if (id <= 0) {
            return null;
        }

        String name = playerData.get(id);
        if (nonNull(name)) {
            return name;
        }

        if (getSettings(GeneralSettings.class).cachePlayersName()) {
            return null;
        }

        var data = getDAO(PlayerDAO.class).findNameAndAccessLevelById(id);
        if(nonNull(data)) {
            playerData.put(id, data.getName());
            accessLevels.put(id, data.getAccessLevel());
        }
        return null; // not found
    }

    public final int getAccessLevelById(int objectId) {
        return zeroIfNullOrElse(getNameById(objectId), name -> accessLevels.get(objectId));
    }

    public synchronized boolean doesCharNameExist(String name) {
        return getDAO(PlayerDAO.class).existsByName(name);
    }

    public int getAccountCharacterCount(String account) {
        return getDAO(PlayerDAO.class).playerCountByAccount(account);
    }

    public int getClassIdById(int objectId) {
        return getDAO(PlayerDAO.class).findClassIdById(objectId);
    }

    private void loadAll() {
        getDAO(PlayerDAO.class).withPlayersDataDo(resultSet -> {
            try {
                while (resultSet.next()) {
                    final int id = resultSet.getInt("charId");
                    playerData.put(id, resultSet.getString("char_name"));
                    accessLevels.put(id, resultSet.getInt("accesslevel"));
                }
            } catch (SQLException e) {
                LOGGER.warn(getClass().getSimpleName() + ": Couldn't retrieve all char id/name/access: " + e.getMessage(), e);
            }
        });
        LOGGER.info("Loaded {} player names.", playerData.size());
    }

    public static PlayerNameTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PlayerNameTable INSTANCE = new PlayerNameTable();
    }
}
