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
package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.util.PropertiesParser;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.TowerSpawn;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * @author JoeAlisson
 */
public final class SiegeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiegeManager.class);

    private final Map<Integer, List<TowerSpawn>> _controlTowers = new HashMap<>();
    private final Map<Integer, List<TowerSpawn>> _flameTowers = new HashMap<>();

    private int _attackerMaxClans = 500; // Max number of clans
    private int _attackerRespawnDelay = 0; // Time in ms. Changeable in siege.config
    private int _defenderMaxClans = 500; // Max number of clans
    private int _flagMaxCount = 1; // Changeable in siege.config
    private int _siegeClanMinLevel = 5; // Changeable in siege.config
    private int _siegeLength = 120; // Time in minute. Changeable in siege.config
    private int _bloodAllianceReward = 0; // Number of Blood Alliance items reward for successful castle defending

    private SiegeManager() {
        load();
    }

    public final void addSiegeSkills(Player player) {
        SkillEngine.getInstance().addSiegeSkills(player);

    }

    public final boolean checkIsRegistered(Clan clan, int castleid) {
        if (clan == null) {
            return false;
        }

        if (clan.getCastleId() > 0) {
            return true;
        }

        boolean register = false;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT clan_id FROM siege_clans where clan_id=? and castle_id=?")) {
            statement.setInt(1, clan.getId());
            statement.setInt(2, castleid);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    register = true;
                }
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Exception: checkIsRegistered(): " + e.getMessage(), e);
        }
        return register;
    }

    public final void removeSiegeSkills(Player player) {
        SkillEngine.getInstance().removeSiegeSkills(player);
    }

    private void load() {
        final PropertiesParser siegeSettings = new PropertiesParser(Config.SIEGE_CONFIG_FILE);

        // Siege setting
        _attackerMaxClans = siegeSettings.getInt("AttackerMaxClans", 500);
        _attackerRespawnDelay = siegeSettings.getInt("AttackerRespawn", 0);
        _defenderMaxClans = siegeSettings.getInt("DefenderMaxClans", 500);
        _flagMaxCount = siegeSettings.getInt("MaxFlags", 1);
        _siegeClanMinLevel = siegeSettings.getInt("SiegeClanMinLevel", 5);
        _siegeLength = siegeSettings.getInt("SiegeLength", 120);
        _bloodAllianceReward = siegeSettings.getInt("BloodAllianceReward", 1);

        for (Castle castle : CastleManager.getInstance().getCastles()) {
            final List<TowerSpawn> controlTowers = new ArrayList<>();
            for (int i = 1; i < 0xFF; i++) {
                final String settingsKeyName = castle.getName() + "ControlTower" + i;
                if (!siegeSettings.containskey(settingsKeyName)) {
                    break;
                }

                final StringTokenizer st = new StringTokenizer(siegeSettings.getString(settingsKeyName, ""), ",");
                try {
                    final int x = Integer.parseInt(st.nextToken());
                    final int y = Integer.parseInt(st.nextToken());
                    final int z = Integer.parseInt(st.nextToken());
                    final int npcId = Integer.parseInt(st.nextToken());

                    controlTowers.add(new TowerSpawn(npcId, new Location(x, y, z)));
                } catch (Exception e) {
                    LOGGER.warn(": Error while loading control tower(s) for " + castle.getName() + " castle.");
                }
            }

            final List<TowerSpawn> flameTowers = new ArrayList<>();
            for (int i = 1; i < 0xFF; i++) {
                final String settingsKeyName = castle.getName() + "FlameTower" + i;
                if (!siegeSettings.containskey(settingsKeyName)) {
                    break;
                }

                final StringTokenizer st = new StringTokenizer(siegeSettings.getString(settingsKeyName, ""), ",");
                try {
                    final int x = Integer.parseInt(st.nextToken());
                    final int y = Integer.parseInt(st.nextToken());
                    final int z = Integer.parseInt(st.nextToken());
                    final int npcId = Integer.parseInt(st.nextToken());
                    final List<Integer> zoneList = new ArrayList<>();

                    while (st.hasMoreTokens()) {
                        zoneList.add(Integer.parseInt(st.nextToken()));
                    }

                    flameTowers.add(new TowerSpawn(npcId, new Location(x, y, z), zoneList));
                } catch (Exception e) {
                    LOGGER.warn(": Error while loading flame tower(s) for " + castle.getName() + " castle.");
                }
            }
            _controlTowers.put(castle.getId(), controlTowers);
            _flameTowers.put(castle.getId(), flameTowers);

            if (castle.getOwnerId() != 0) {
                loadTrapUpgrade(castle.getId());
            }
        }
    }

    public final List<TowerSpawn> getControlTowers(int castleId) {
        return _controlTowers.get(castleId);
    }

    public final List<TowerSpawn> getFlameTowers(int castleId) {
        return _flameTowers.get(castleId);
    }

    public final int getAttackerMaxClans() {
        return _attackerMaxClans;
    }

    public final int getAttackerRespawnDelay() {
        return _attackerRespawnDelay;
    }

    public final int getDefenderMaxClans() {
        return _defenderMaxClans;
    }

    public final int getFlagMaxCount() {
        return _flagMaxCount;
    }

    public final Siege getSiege(ILocational loc) {
        return CastleManager.getInstance().getSiegeOnLocation(loc);
    }

    public final int getSiegeClanMinLevel() {
        return _siegeClanMinLevel;
    }

    public final int getSiegeLength() {
        return _siegeLength;
    }

    public final int getBloodAllianceReward() {
        return _bloodAllianceReward;
    }

    public final List<Siege> getSieges() {
        final List<Siege> sieges = new LinkedList<>();
        for (Castle castle : CastleManager.getInstance().getCastles()) {
            sieges.add(castle.getSiege());
        }
        return sieges;
    }

    private void loadTrapUpgrade(int castleId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM castle_trap_upgrade WHERE castle_id=?")) {
            ps.setInt(1, castleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    _flameTowers.get(castleId).get(rs.getInt("towerIndex")).setUpgradeLevel(rs.getInt("level"));
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Exception: loadTrapUpgrade(): " + e.getMessage(), e);
        }
    }

    public static SiegeManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final SiegeManager INSTANCE = new SiegeManager();
    }
}