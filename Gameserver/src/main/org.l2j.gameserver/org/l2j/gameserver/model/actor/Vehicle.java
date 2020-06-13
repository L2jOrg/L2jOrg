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
package org.l2j.gameserver.model.actor;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.VehiclePathPoint;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.stat.VehicleStats;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.ZoneRegion;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.gameserver.util.MathUtil.calculateDistance2D;
import static org.l2j.gameserver.util.MathUtil.calculateHeadingFrom;

/**
 * @author DS
 */
public abstract class Vehicle extends Creature {
    protected final Set<Player> _passengers = ConcurrentHashMap.newKeySet();
    protected int _dockId = 0;
    protected Location _oustLoc = null;
    protected VehiclePathPoint[] _currentPath = null;
    protected int _runState = 0;
    private Runnable _engine = null;

    public Vehicle(CreatureTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2Vehicle);
        setIsFlying(true);
    }

    public boolean isBoat() {
        return false;
    }

    public boolean canBeControlled() {
        return _engine == null;
    }

    public void registerEngine(Runnable r) {
        _engine = r;
    }

    public void runEngine(int delay) {
        if (_engine != null) {
            ThreadPool.schedule(_engine, delay);
        }
    }

    public void executePath(VehiclePathPoint[] path) {
        _runState = 0;
        _currentPath = path;

        if ((_currentPath != null) && (_currentPath.length > 0)) {
            final VehiclePathPoint point = _currentPath[0];
            if (point.getMoveSpeed() > 0) {
                getStats().setMoveSpeed(point.getMoveSpeed());
            }
            if (point.getRotationSpeed() > 0) {
                getStats().setRotationSpeed(point.getRotationSpeed());
            }

            getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(point.getX(), point.getY(), point.getZ(), 0));
            return;
        }
        getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    @Override
    public boolean moveToNextRoutePoint() {
        _move = null;

        if (_currentPath != null) {
            _runState++;
            if (_runState < _currentPath.length) {
                final VehiclePathPoint point = _currentPath[_runState];
                if (!isMovementDisabled()) {
                    if (point.getMoveSpeed() == 0) {
                        point.setHeading(point.getRotationSpeed());
                        teleToLocation(point, false);
                        _currentPath = null;
                    } else {
                        if (point.getMoveSpeed() > 0) {
                            getStats().setMoveSpeed(point.getMoveSpeed());
                        }
                        if (point.getRotationSpeed() > 0) {
                            getStats().setRotationSpeed(point.getRotationSpeed());
                        }

                        final MoveData m = new MoveData();
                        m.disregardingGeodata = false;
                        m.onGeodataPathIndex = -1;
                        m._xDestination = point.getX();
                        m._yDestination = point.getY();
                        m._zDestination = point.getZ();
                        m._heading = 0;

                        final double distance = calculateDistance2D(point, this);
                        if (distance > 1) {
                            setHeading(calculateHeadingFrom(this, point));
                        }

                        m._moveStartTime = WorldTimeController.getInstance().getGameTicks();
                        _move = m;

                        WorldTimeController.getInstance().registerMovingObject(this);
                        return true;
                    }
                }
            } else {
                _currentPath = null;
            }
        }

        runEngine(10);
        return false;
    }

    @Override
    public VehicleStats getStats() {
        return (VehicleStats) super.getStats();
    }

    @Override
    public void initCharStat() {
        setStat(new VehicleStats(this));
    }

    public boolean isInDock() {
        return _dockId > 0;
    }

    public void setInDock(int d) {
        _dockId = d;
    }

    public int getDockId() {
        return _dockId;
    }

    public Location getOustLoc() {
        return _oustLoc != null ? _oustLoc : MapRegionManager.getInstance().getTeleToLocation(this, TeleportWhereType.TOWN);
    }

    public void setOustLoc(Location loc) {
        _oustLoc = loc;
    }

    public void oustPlayers() {
        Player player;

        // Use iterator because oustPlayer will try to remove player from _passengers
        final Iterator<Player> iter = _passengers.iterator();
        while (iter.hasNext()) {
            player = iter.next();
            iter.remove();
            if (player != null) {
                oustPlayer(player);
            }
        }
    }

    public void oustPlayer(Player player) {
        player.setVehicle(null);
        player.setInVehiclePosition(null);
        removePassenger(player);
    }

    public boolean addPassenger(Player player) {
        if ((player == null) || _passengers.contains(player)) {
            return false;
        }

        // already in other vehicle
        if ((player.getVehicle() != null) && (player.getVehicle() != this)) {
            return false;
        }

        _passengers.add(player);
        return true;
    }

    public void removePassenger(Player player) {
        try {
            _passengers.remove(player);
        } catch (Exception e) {
        }
    }

    public boolean isEmpty() {
        return _passengers.isEmpty();
    }

    public Set<Player> getPassengers() {
        return _passengers;
    }

    public void broadcastToPassengers(ServerPacket sm) {
        for (Player player : _passengers) {
            if (player != null) {
                player.sendPacket(sm);
            }
        }
    }

    /**
     * Consume ticket(s) and teleport player from boat if no correct ticket
     *
     * @param itemId Ticket itemId
     * @param count  Ticket count
     * @param oustX
     * @param oustY
     * @param oustZ
     */
    public void payForRide(int itemId, int count, int oustX, int oustY, int oustZ) {
        World.getInstance().forEachVisibleObjectInRange(this, Player.class, 1000, player ->
        {
            if (player.isInBoat() && (player.getBoat() == this)) {
                if (itemId > 0) {
                    final Item ticket = player.getInventory().getItemByItemId(itemId);
                    if ((ticket == null) || (player.getInventory().destroyItem("Boat", ticket, count, player, this) == null)) {
                        player.sendPacket(SystemMessageId.YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT);
                        player.teleToLocation(new Location(oustX, oustY, oustZ), true);
                        return;
                    }
                    final InventoryUpdate iu = new InventoryUpdate();
                    iu.addModifiedItem(ticket);
                    player.sendInventoryUpdate(iu);
                }
                addPassenger(player);
            }
        });
    }

    @Override
    public boolean updatePosition() {
        final boolean result = super.updatePosition();

        for (Player player : _passengers) {
            if ((player != null) && (player.getVehicle() == this)) {
                player.setXYZ(getX(), getY(), getZ());
                player.revalidateZone(false);
            }
        }

        return result;
    }

    @Override
    public void teleToLocation(ILocational loc, boolean allowRandomOffset) {
        if (isMoving()) {
            stopMove(null);
        }

        setIsTeleporting(true);

        getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);

        for (Player player : _passengers) {
            if (player != null) {
                player.teleToLocation(loc, false);
            }
        }

        decayMe();
        setXYZ(loc);

        // temporary fix for heading on teleports
        if (loc.getHeading() != 0) {
            setHeading(loc.getHeading());
        }

        onTeleported();
        revalidateZone(true);
    }

    @Override
    public void stopMove(Location loc) {
        _move = null;
        if (loc != null) {
            setXYZ(loc);
            setHeading(loc.getHeading());
            revalidateZone(true);
        }

    }

    @Override
    public boolean deleteMe() {
        _engine = null;

        try {
            if (isMoving()) {
                stopMove(null);
            }
        } catch (Exception e) {
            LOGGER.error("Failed stopMove().", e);
        }

        try {
            oustPlayers();
        } catch (Exception e) {
            LOGGER.error("Failed oustPlayers().", e);
        }

        final ZoneRegion oldZoneRegion = ZoneManager.getInstance().getRegion(this);

        try {
            decayMe();
        } catch (Exception e) {
            LOGGER.error("Failed decayMe().", e);
        }

        oldZoneRegion.removeFromZones(this);

        return super.deleteMe();
    }

    @Override
    public Item getActiveWeaponInstance() {
        return null;
    }

    @Override
    public Weapon getActiveWeaponItem() {
        return null;
    }

    @Override
    public Item getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public Weapon getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    public void detachAI() {
    }

    @Override
    public boolean isVehicle() {
        return true;
    }
}
