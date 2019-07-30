package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.ActionHandler;
import org.l2j.gameserver.handler.ActionShiftHandler;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.handler.IActionShiftHandler;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.*;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.DeleteObject;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.commons.util.Util.zeroIfNullOrElse;
import static org.l2j.gameserver.util.MathUtil.calculateHeadingFrom;
import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * Base class for all interactive objects.
 */
public abstract class WorldObject extends ListenersContainer implements IIdentifiable, INamable, ISpawnable, IUniqueId, IDecayable, IPositionable {
    /**
     * Name
     */
    private String _name;
    /**
     * Object ID
     */
    private int _objectId;
    /**
     * World Region
     */
    private WorldRegion _worldRegion;
    /**
     * Instance type
     */
    private InstanceType _instanceType;
    private volatile Map<String, Object> _scripts;
    /**
     * X coordinate
     */
    private volatile int _x = 0;
    /**
     * Y coordinate
     */
    private volatile int _y = 0;
    /**
     * Z coordinate
     */
    private volatile int _z = 0;
    /**
     * Orientation
     */
    private volatile int _heading = 0;
    /**
     * Instance id of object. 0 - Global
     */
    private Instance _instance;
    private boolean _isSpawned;
    private boolean _isInvisible;
    private boolean _isTargetable = true;

    public WorldObject(int objectId) {
        setInstanceType(InstanceType.L2Object);
        _objectId = objectId;
    }

    /**
     * Gets the instance type of object.
     *
     * @return the instance type
     */
    public final InstanceType getInstanceType() {
        return _instanceType;
    }

    /**
     * Sets the instance type.
     *
     * @param newInstanceType the instance type to set
     */
    protected final void setInstanceType(InstanceType newInstanceType) {
        _instanceType = newInstanceType;
    }

    /**
     * Verifies if object is of any given instance types.
     *
     * @param instanceTypes the instance types to verify
     * @return {@code true} if object is of any given instance types, {@code false} otherwise
     */
    public final boolean isInstanceTypes(InstanceType... instanceTypes) {
        return _instanceType.isTypes(instanceTypes);
    }

    public final void onAction(Player player) {
        onAction(player, true);
    }

    public void onAction(Player player, boolean interact) {
        final IActionHandler handler = ActionHandler.getInstance().getHandler(getInstanceType());
        if (handler != null) {
            handler.action(player, this, interact);
        }

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    public void onActionShift(Player player) {
        final IActionShiftHandler handler = ActionShiftHandler.getInstance().getHandler(getInstanceType());
        if (handler != null) {
            handler.action(player, this, true);
        }

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    public void onForcedAttack(Player player) {
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    public void onSpawn() {
        broadcastInfo(); // Tempfix for invisible spawns.
    }

    @Override
    public boolean decayMe() {
        _isSpawned = false;
        World.getInstance().removeVisibleObject(this, _worldRegion);
        World.getInstance().removeObject(this);
        return true;
    }

    public void refreshID() {
        World.getInstance().removeObject(this);
        IdFactory.getInstance().releaseId(getObjectId());
        _objectId = IdFactory.getInstance().getNextId();
    }

    @Override
    public final boolean spawnMe() {
        synchronized (this) {
            _isSpawned = true;
            setWorldRegion(World.getInstance().getRegion(this));

            // Add the WorldObject spawn in the _allobjects of World
            World.getInstance().addObject(this);

            // Add the WorldObject spawn to _visibleObjects and if necessary to _allplayers of its WorldRegion
            _worldRegion.addVisibleObject(this);
        }

        // this can synchronize on others instances, so it's out of synchronized, to avoid deadlocks
        // Add the WorldObject spawn in the world as a visible object
        World.getInstance().addVisibleObject(this, getWorldRegion());

        onSpawn();

        return true;
    }

    public final void spawnMe(int x, int y, int z) {
        synchronized (this) {
            setXYZ(x, y, z);
        }

        spawnMe();
    }

    /**
     * Verify if object can be attacked.
     *
     * @return {@code true} if object can be attacked, {@code false} otherwise
     */
    public boolean canBeAttacked() {
        return false;
    }

    public abstract boolean isAutoAttackable(Creature attacker);

    public final boolean isSpawned() {
        return _isSpawned;
    }

    public final void setSpawned(boolean value) {
        _isSpawned = value;
    }

    @Override
    public String getName() {
        return _name;
    }

    public void setName(String value) {
        _name = value;
    }

    @Override
    public final int getObjectId() {
        return _objectId;
    }

    public abstract void sendInfo(Player activeChar);

    public void sendPacket(ServerPacket... packets) {
    }

    public void sendPacket(SystemMessageId id) {
    }

    public Player getActingPlayer() {
        return null;
    }

    /**
     * Verify if object is instance of Servitor.
     *
     * @return {@code true} if object is instance of Servitor, {@code false} otherwise
     */
    public boolean isServitor() {
        return false;
    }


    /**
     * Verifies if the object is a walker NPC.
     *
     * @return {@code true} if object is a walker NPC, {@code false} otherwise
     */
    @Deprecated
    public boolean isWalker() {
        return false;
    }

    /**
     * Verifies if this object is a vehicle.
     *
     * @return {@code true} if object is Vehicle, {@code false} otherwise
     */
    public boolean isVehicle() {
        return false;
    }

    /**
     * @return {@code true} if the object can be targetted by other players, {@code false} otherwise.
     */
    public boolean isTargetable() {
        return _isTargetable;
    }

    public void setTargetable(boolean targetable) {
        if (_isTargetable != targetable) {
            _isTargetable = targetable;
            if (!targetable) {
                World.getInstance().forEachVisibleObject(this, Creature.class, Creature::forgetTarget,  creature -> this == creature.getTarget());
            }
        }
    }


    /**
     * Check if the object is in the given zone Id.
     *
     * @param zone the zone Id to check
     * @return {@code true} if the object is in that zone Id
     */
    public boolean isInsideZone(ZoneId zone) {
        return false;
    }

    /**
     * @param <T>
     * @param script
     * @return
     */
    public final <T> T addScript(T script) {
        if (_scripts == null) {
            // Double-checked locking
            synchronized (this) {
                if (_scripts == null) {
                    _scripts = new ConcurrentHashMap<>();
                }
            }
        }
        _scripts.put(script.getClass().getName(), script);
        return script;
    }

    /**
     * @param <T>
     * @param script
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <T> T removeScript(Class<T> script) {
        if (_scripts == null) {
            return null;
        }
        return (T) _scripts.remove(script.getName());
    }

    /**
     * @param <T>
     * @param script
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <T> T getScript(Class<T> script) {
        if (_scripts == null) {
            return null;
        }
        return (T) _scripts.get(script.getName());
    }

    public void removeStatusListener(Creature object) {

    }

    public final void setXYZInvisible(int x, int y, int z) {
        setXYZ(x, y, z);
        setSpawned(false);
    }

    public final void setLocationInvisible(ILocational loc) {
        setXYZInvisible(loc.getX(), loc.getY(), loc.getZ());
    }

    public final WorldRegion getWorldRegion() {
        return _worldRegion;
    }

    public void setWorldRegion(WorldRegion value) {
        _worldRegion = value;
    }

    /**
     * Gets the X coordinate.
     *
     * @return the X coordinate
     */
    @Override
    public int getX() {
        return _x;
    }

    /**
     * Gets the Y coordinate.
     *
     * @return the Y coordinate
     */
    @Override
    public int getY() {
        return _y;
    }

    /**
     * Gets the Z coordinate.
     *
     * @return the Z coordinate
     */
    @Override
    public int getZ() {
        return _z;
    }

    /**
     * Gets the heading.
     *
     * @return the heading
     */
    @Override
    public int getHeading() {
        return _heading;
    }

    /**
     * Sets heading of object.
     *
     * @param newHeading the new heading
     */
    @Override
    public void setHeading(int newHeading) {
        _heading = newHeading;
    }

    /**
     * Gets the instance ID.
     *
     * @return the instance ID
     */
    public int getInstanceId() {
        return zeroIfNullOrElse(_instance, Instance::getId);
    }

    /**
     * Check if object is inside instance world.
     *
     * @return {@code true} when object is inside any instance world, otherwise {@code false}
     */
    public boolean isInInstance() {
        return _instance != null;
    }

    /**
     * Get instance world where object is currently located.
     *
     * @return {@link Instance} if object is inside instance world, otherwise {@code null}
     */
    public Instance getInstanceWorld() {
        return _instance;
    }

    /**
     * Gets the location object.
     *
     * @return the location object
     */
    @Override
    public Location getLocation() {
        return new Location(_x, _y, _z, _heading);
    }

    /**
     * Sets location of object.
     *
     * @param loc the location object
     */
    @Override
    public void setLocation(Location loc) {
        _x = loc.getX();
        _y = loc.getY();
        _z = loc.getZ();
        _heading = loc.getHeading();
    }

    /**
     * Sets the x, y, z coordinate.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     */
    @Override
    public void setXYZ(int x, int y, int z) {
        if (x > World.MAP_MAX_X) {
            x = World.MAP_MAX_X - 5000;
        }
        if (x < World.MAP_MIN_X) {
            x = World.MAP_MIN_X + 5000;
        }
        if (y > World.MAP_MAX_Y) {
            y = World.MAP_MAX_Y - 5000;
        }
        if (y < World.MAP_MIN_Y) {
            y = World.MAP_MIN_Y + 5000;
        }
        _x = x;
        _y = y;
        _z = z;

        if (_isSpawned) {
            World.getInstance().switchRegionIfNeed(this);
        }
    }

    /**
     * Sets the x, y, z coordinate.
     *
     * @param loc the location object
     */
    @Override
    public void setXYZ(ILocational loc) {
        setXYZ(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Sets instance for current object by instance ID.<br>
     *
     * @param id ID of instance world which should be set (0 means normal world)
     */
    public void setInstanceById(int id) {
        final Instance instance = InstanceManager.getInstance().getInstance(id);
        if ((id != 0) && (instance == null)) {
            return;
        }
        setInstance(instance);
    }

    /**
     * Sets instance where current object belongs.
     *
     * @param newInstance new instance world for object
     */
    public synchronized void setInstance(Instance newInstance) {
        // Check if new and old instances are identical
        if (_instance == newInstance) {
            return;
        }

        // Leave old instance
        if (_instance != null) {
            _instance.onInstanceChange(this, false);
        }

        // Set new instance
        _instance = newInstance;

        // Enter into new instance
        if (newInstance != null) {
            newInstance.onInstanceChange(this, true);
        }
    }

    /**
     * Calculates the angle in degrees from this object to the given object.<br>
     * The return value can be described as how much this object has to turn<br>
     * to have the given object directly in front of it.
     *
     * @param target the object to which to calculate the angle
     * @return the angle this object has to turn to have the given object in front of it
     */
    public double calculateDirectionTo(ILocational target) {
        int heading = calculateHeadingFrom(this, target) - _heading;
        if (heading < 0) {
            heading += 65535;
        }
        return convertHeadingToDegree(heading);
    }

    /**
     * @return {@code true} if this object is invisible, {@code false} otherwise.
     */
    public boolean isInvisible() {
        return _isInvisible;
    }

    /**
     * Sets this object as invisible or not
     *
     * @param invis
     */
    public void setInvisible(boolean invis) {
        _isInvisible = invis;
        if (invis) {
            final DeleteObject deletePacket = new DeleteObject(this);
            World.getInstance().forEachVisibleObject(this, Player.class, player ->
            {
                if (!isVisibleFor(player)) {
                    player.sendPacket(deletePacket);
                }
            });
        }

        // Broadcast information regarding the object to those which are suppose to see.
        broadcastInfo();
    }

    /**
     * @param player
     * @return {@code true} if player can see an invisible object if it's invisible, {@code false} otherwise.
     */
    public boolean isVisibleFor(Player player) {
        return !_isInvisible || player.canOverrideCond(PcCondOverride.SEE_ALL_PLAYERS);
    }

    /**
     * Broadcasts describing info to known players.
     */
    public void broadcastInfo() {
        World.getInstance().forEachVisibleObject(this, Player.class, player ->
        {
            if (isVisibleFor(player)) {
                sendInfo(player);
            }
        });
    }

    public boolean isInvul() {
        return false;
    }

    public boolean isInSurroundingRegion(WorldObject worldObject) {
        if (worldObject == null) {
            return false;
        }

        final WorldRegion worldRegion = worldObject.getWorldRegion();
        if (worldRegion == null) {
            return false;
        }

        if (_worldRegion == null) {
            return false;
        }

        return worldRegion.isSurroundingRegion(_worldRegion);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof WorldObject) && (((WorldObject) obj).getObjectId() == getObjectId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + _name + "[" + _objectId + "]";
    }
}
