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
package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.AirShipTeleportList;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.VehiclePathPoint;
import org.l2j.gameserver.model.actor.instance.AirShip;
import org.l2j.gameserver.model.actor.instance.ControllableAirShip;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.L2CharTemplate;
import org.l2j.gameserver.network.serverpackets.ExAirShipTeleportList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class AirShipManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirShipManager.class);

    private static final String LOAD_DB = "SELECT * FROM airships";
    private static final String ADD_DB = "INSERT INTO airships (owner_id,fuel) VALUES (?,?)";
    private static final String UPDATE_DB = "UPDATE airships SET fuel=? WHERE owner_id=?";
    private final Map<Integer, StatsSet> _airShipsInfo = new HashMap<>();
    private final Map<Integer, AirShip> _airShips = new HashMap<>();
    private final Map<Integer, AirShipTeleportList> _teleports = new HashMap<>();
    private L2CharTemplate _airShipTemplate = null;

    private AirShipManager() {
        final StatsSet npcDat = new StatsSet();
        npcDat.set("npcId", 9);
        npcDat.set("level", 0);
        npcDat.set("jClass", "boat");

        npcDat.set("baseSTR", 0);
        npcDat.set("baseCON", 0);
        npcDat.set("baseDEX", 0);
        npcDat.set("baseINT", 0);
        npcDat.set("baseWIT", 0);
        npcDat.set("baseMEN", 0);

        npcDat.set("baseShldDef", 0);
        npcDat.set("baseShldRate", 0);
        npcDat.set("baseAccCombat", 38);
        npcDat.set("baseEvasRate", 38);
        npcDat.set("baseCritRate", 38);

        npcDat.set("collision_radius", 0);
        npcDat.set("collision_height", 0);
        npcDat.set("sex", "male");
        npcDat.set("type", "");
        npcDat.set("baseAtkRange", 0);
        npcDat.set("baseMpMax", 0);
        npcDat.set("baseCpMax", 0);
        npcDat.set("rewardExp", 0);
        npcDat.set("rewardSp", 0);
        npcDat.set("basePAtk", 0);
        npcDat.set("baseMAtk", 0);
        npcDat.set("basePAtkSpd", 0);
        npcDat.set("aggroRange", 0);
        npcDat.set("baseMAtkSpd", 0);
        npcDat.set("rhand", 0);
        npcDat.set("lhand", 0);
        npcDat.set("armor", 0);
        npcDat.set("baseWalkSpd", 0);
        npcDat.set("baseRunSpd", 0);
        npcDat.set("name", "AirShip");
        npcDat.set("baseHpMax", 50000);
        npcDat.set("baseHpReg", 3.e-3f);
        npcDat.set("baseMpReg", 3.e-3f);
        npcDat.set("basePDef", 100);
        npcDat.set("baseMDef", 100);
        _airShipTemplate = new L2CharTemplate(npcDat);

        load();
    }

    public AirShip getNewAirShip(int x, int y, int z, int heading) {
        final AirShip airShip = new AirShip(_airShipTemplate);

        airShip.setHeading(heading);
        airShip.setXYZInvisible(x, y, z);
        airShip.spawnMe();
        airShip.getStat().setMoveSpeed(280);
        airShip.getStat().setRotationSpeed(2000);
        return airShip;
    }

    public AirShip getNewAirShip(int x, int y, int z, int heading, int ownerId) {
        final StatsSet info = _airShipsInfo.get(ownerId);
        if (info == null) {
            return null;
        }

        final AirShip airShip;
        if (_airShips.containsKey(ownerId)) {
            airShip = _airShips.get(ownerId);
            airShip.refreshID();
        } else {
            airShip = new ControllableAirShip(_airShipTemplate, ownerId);
            _airShips.put(ownerId, airShip);

            airShip.setMaxFuel(600);
            airShip.setFuel(info.getInt("fuel"));
            airShip.getStat().setMoveSpeed(280);
            airShip.getStat().setRotationSpeed(2000);
        }

        airShip.setHeading(heading);
        airShip.setXYZInvisible(x, y, z);
        airShip.spawnMe();
        return airShip;
    }

    public void removeAirShip(AirShip ship) {
        if (ship.getOwnerId() != 0) {
            storeInDb(ship.getOwnerId());
            final StatsSet info = _airShipsInfo.get(ship.getOwnerId());
            if (info != null) {
                info.set("fuel", ship.getFuel());
            }
        }
    }

    public boolean hasAirShipLicense(int ownerId) {
        return _airShipsInfo.containsKey(ownerId);
    }

    public void registerLicense(int ownerId) {
        if (!_airShipsInfo.containsKey(ownerId)) {
            final StatsSet info = new StatsSet();
            info.set("fuel", 600);

            _airShipsInfo.put(ownerId, info);

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement(ADD_DB)) {
                ps.setInt(1, ownerId);
                ps.setInt(2, info.getInt("fuel"));
                ps.executeUpdate();
            } catch (SQLException e) {
                LOGGER.warn(getClass().getSimpleName() + ": Could not add new airship license: ", e);
            } catch (Exception e) {
                LOGGER.warn(getClass().getSimpleName() + ": Error while initializing: ", e);
            }
        }
    }

    public boolean hasAirShip(int ownerId) {
        final AirShip ship = _airShips.get(ownerId);
        if ((ship == null) || !(ship.isSpawned() || ship.isTeleporting())) {
            return false;
        }

        return true;
    }

    public void registerAirShipTeleportList(int dockId, int locationId, VehiclePathPoint[][] tp, int[] fuelConsumption) {
        if (tp.length != fuelConsumption.length) {
            return;
        }

        _teleports.put(dockId, new AirShipTeleportList(locationId, fuelConsumption, tp));
    }

    public void sendAirShipTeleportList(Player player) {
        if ((player == null) || !player.isInAirShip()) {
            return;
        }

        final AirShip ship = player.getAirShip();
        if (!ship.isCaptain(player) || !ship.isInDock() || ship.isMoving()) {
            return;
        }

        final int dockId = ship.getDockId();
        if (!_teleports.containsKey(dockId)) {
            return;
        }

        final AirShipTeleportList all = _teleports.get(dockId);
        player.sendPacket(new ExAirShipTeleportList(all.getLocation(), all.getRoute(), all.getFuel()));
    }

    public VehiclePathPoint[] getTeleportDestination(int dockId, int index) {
        final AirShipTeleportList all = _teleports.get(dockId);
        if (all == null) {
            return null;
        }

        if ((index < -1) || (index >= all.getRoute().length)) {
            return null;
        }

        return all.getRoute()[index + 1];
    }

    public int getFuelConsumption(int dockId, int index) {
        final AirShipTeleportList all = _teleports.get(dockId);
        if (all == null) {
            return 0;
        }

        if ((index < -1) || (index >= all.getFuel().length)) {
            return 0;
        }

        return all.getFuel()[index + 1];
    }

    private void load() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery(LOAD_DB)) {
            StatsSet info;
            while (rs.next()) {
                info = new StatsSet();
                info.set("fuel", rs.getInt("fuel"));
                _airShipsInfo.put(rs.getInt("owner_id"), info);
            }
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Could not load airships table: ", e);
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Error while initializing: ", e);
        }
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _airShipsInfo.size() + " private airships");
    }

    private void storeInDb(int ownerId) {
        final StatsSet info = _airShipsInfo.get(ownerId);
        if (info == null) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_DB)) {
            ps.setInt(1, info.getInt("fuel"));
            ps.setInt(2, ownerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Could not update airships table: ", e);
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Error while save: ", e);
        }
    }

    public static AirShipManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final AirShipManager INSTANCE = new AirShipManager();
    }
}