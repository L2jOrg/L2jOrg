package org.l2j.gameserver.mobius.gameserver.model.actor;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.mobius.gameserver.GameTimeController;
import org.l2j.gameserver.mobius.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.mobius.gameserver.enums.InstanceType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MapRegionManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.ZoneManager;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.mobius.gameserver.model.VehiclePathPoint;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.stat.VehicleStat;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2CharTemplate;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Weapon;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneRegion;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * @author DS
 */
public abstract class L2Vehicle extends L2Character {
    protected final Set<L2PcInstance> _passengers = ConcurrentHashMap.newKeySet();
    protected int _dockId = 0;
    protected Location _oustLoc = null;
    protected VehiclePathPoint[] _currentPath = null;
    protected int _runState = 0;
    private Runnable _engine = null;

    public L2Vehicle(L2CharTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2Vehicle);
        setIsFlying(true);
    }

    public boolean isBoat() {
        return false;
    }

    public boolean isAirShip() {
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
            ThreadPoolManager.getInstance().schedule(_engine, delay);
        }
    }

    public void executePath(VehiclePathPoint[] path) {
        _runState = 0;
        _currentPath = path;

        if ((_currentPath != null) && (_currentPath.length > 0)) {
            final VehiclePathPoint point = _currentPath[0];
            if (point.getMoveSpeed() > 0) {
                getStat().setMoveSpeed(point.getMoveSpeed());
            }
            if (point.getRotationSpeed() > 0) {
                getStat().setRotationSpeed(point.getRotationSpeed());
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
                            getStat().setMoveSpeed(point.getMoveSpeed());
                        }
                        if (point.getRotationSpeed() > 0) {
                            getStat().setRotationSpeed(point.getRotationSpeed());
                        }

                        final MoveData m = new MoveData();
                        m.disregardingGeodata = false;
                        m.onGeodataPathIndex = -1;
                        m._xDestination = point.getX();
                        m._yDestination = point.getY();
                        m._zDestination = point.getZ();
                        m._heading = 0;

                        final double distance = Math.hypot(point.getX() - getX(), point.getY() - getY());
                        if (distance > 1) {
                            setHeading(Util.calculateHeadingFrom(getX(), getY(), point.getX(), point.getY()));
                        }

                        m._moveStartTime = GameTimeController.getInstance().getGameTicks();
                        _move = m;

                        GameTimeController.getInstance().registerMovingObject(this);
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
    public VehicleStat getStat() {
        return (VehicleStat) super.getStat();
    }

    @Override
    public void initCharStat() {
        setStat(new VehicleStat(this));
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
        L2PcInstance player;

        // Use iterator because oustPlayer will try to remove player from _passengers
        final Iterator<L2PcInstance> iter = _passengers.iterator();
        while (iter.hasNext()) {
            player = iter.next();
            iter.remove();
            if (player != null) {
                oustPlayer(player);
            }
        }
    }

    public void oustPlayer(L2PcInstance player) {
        player.setVehicle(null);
        player.setInVehiclePosition(null);
        removePassenger(player);
    }

    public boolean addPassenger(L2PcInstance player) {
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

    public void removePassenger(L2PcInstance player) {
        try {
            _passengers.remove(player);
        } catch (Exception e) {
        }
    }

    public boolean isEmpty() {
        return _passengers.isEmpty();
    }

    public Set<L2PcInstance> getPassengers() {
        return _passengers;
    }

    public void broadcastToPassengers(IClientOutgoingPacket sm) {
        for (L2PcInstance player : _passengers) {
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
        L2World.getInstance().forEachVisibleObjectInRange(this, L2PcInstance.class, 1000, player ->
        {
            if (player.isInBoat() && (player.getBoat() == this)) {
                if (itemId > 0) {
                    final L2ItemInstance ticket = player.getInventory().getItemByItemId(itemId);
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

        for (L2PcInstance player : _passengers) {
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

        for (L2PcInstance player : _passengers) {
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
            LOGGER.log(Level.SEVERE, "Failed stopMove().", e);
        }

        try {
            oustPlayers();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed oustPlayers().", e);
        }

        final ZoneRegion oldZoneRegion = ZoneManager.getInstance().getRegion(this);

        try {
            decayMe();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed decayMe().", e);
        }

        oldZoneRegion.removeFromZones(this);

        return super.deleteMe();
    }

    @Override
    public L2ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public L2Weapon getActiveWeaponItem() {
        return null;
    }

    @Override
    public L2ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public L2Weapon getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
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
