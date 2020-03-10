package org.l2j.gameserver.data.sql.impl;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.data.database.dao.CharacterDAO;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.settings.GeneralSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * Loads name and access level for all players.
 *
 * @version 2005/03/27
 */
public class PlayerNameTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerNameTable.class);

    private final IntMap<String> playerData = new CHashIntMap<>();
    private final Map<Integer, Integer> accessLevels = new ConcurrentHashMap<>();

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
        if (name != null) {
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

        var characterData = getDAO(CharacterDAO.class).findIdAndAccessLevelByName(name);
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
        if (name != null) {
            return name;
        }

        if (getSettings(GeneralSettings.class).cachePlayersName()) {
            return null;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT char_name,accesslevel FROM characters WHERE charId=?")) {
            ps.setInt(1, id);
            try (ResultSet rset = ps.executeQuery()) {
                if (rset.next()) {
                    name = rset.getString("char_name");
                    playerData.put(id, name);
                    accessLevels.put(id, rset.getInt("accesslevel"));
                    return name;
                }
            }
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Could not check existing char id: " + e.getMessage(), e);
        }

        return null; // not found
    }

    public final int getAccessLevelById(int objectId) {
        return getNameById(objectId) != null ? accessLevels.get(objectId) : 0;
    }

    public synchronized boolean doesCharNameExist(String name) {
        boolean result = false;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as count FROM characters WHERE char_name=?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Could not check existing charname: " + e.getMessage(), e);
        }
        return result;
    }

    public int getAccountCharacterCount(String account) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(char_name) as count FROM characters WHERE account_name=?")) {
            ps.setString(1, account);
            try (ResultSet rset = ps.executeQuery()) {
                if (rset.next()) {
                    return rset.getInt("count");
                }
            }
        } catch (SQLException e) {
            LOGGER.warn("Couldn't retrieve account for id: " + e.getMessage(), e);
        }
        return 0;
    }

    public int getLevelById(int objectId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT level FROM characters WHERE charId = ?")) {
            ps.setInt(1, objectId);
            try (ResultSet rset = ps.executeQuery()) {
                if (rset.next()) {
                    return rset.getInt("level");
                }
            }
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Could not check existing char count: " + e.getMessage(), e);
        }
        return 0;
    }

    public int getClassIdById(int objectId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT classid FROM characters WHERE charId = ?")) {
            ps.setInt(1, objectId);
            try (ResultSet rset = ps.executeQuery()) {
                if (rset.next()) {
                    return rset.getInt("classid");
                }
            }
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't retrieve class for id: " + e.getMessage(), e);
        }
        return 0;
    }

    private void loadAll() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT charId, char_name, accesslevel FROM characters")) {
            while (rs.next()) {
                final int id = rs.getInt("charId");
                playerData.put(id, rs.getString("char_name"));
                accessLevels.put(id, rs.getInt("accesslevel"));
            }
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't retrieve all char id/name/access: " + e.getMessage(), e);
        }
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + playerData.size() + " char names.");
    }

    public static PlayerNameTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PlayerNameTable INSTANCE = new PlayerNameTable();
    }
}
