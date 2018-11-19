package l2s.gameserver.model;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.lang.reference.HardReferences;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.handler.onshiftaction.OnShiftActionHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.entity.events.EventOwner;
import l2s.gameserver.network.l2.s2c.DeleteObjectPacket;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameObject extends EventOwner
{
	private static final Logger _log = LoggerFactory.getLogger(GameObject.class);

	public final static GameObject[] EMPTY_L2OBJECT_ARRAY = new GameObject[0];

	/** Основные состояния объекта */
	protected final static int CREATED = 0;
	protected final static int VISIBLE = 1;
	protected final static int DELETED = -1;

	/** Идентификатор объекта */
	protected int objectId;

	/** Позиция объекта в мире */
	private int _x;
	private int _y;
	private int _z;
	protected Reflection _reflection = ReflectionManager.MAIN;

	private WorldRegion _currentRegion;

	/** Состояние объекта */
	private final AtomicInteger _state = new AtomicInteger(CREATED);

	protected GameObject()
	{

	}

	/**
	 * Constructor<?> of L2Object.<BR><BR>
	 * @param objectId Идентификатор объекта
	 */
	public GameObject(int objectId)
	{
		this.objectId = objectId;
	}

	public HardReference<? extends GameObject> getRef()
	{
		return HardReferences.emptyRef();
	}

	private void clearRef()
	{
		HardReference<? extends GameObject> reference = getRef();
		if(reference != null)
			reference.clear();
	}

	public Reflection getReflection()
	{
		return _reflection;
	}

	public int getReflectionId()
	{
		return _reflection.getId();
	}

	public int getGeoIndex()
	{
		return _reflection.isCollapseStarted() ? 0 : _reflection.getGeoIndex();
	}

	public boolean setReflection(Reflection reflection)
	{
		if(_reflection == reflection)
			return true;

		if(reflection.isCollapseStarted())
			return false;

		boolean respawn = false;
		if(isVisible())
		{
			decayMe();
			respawn = true;
		}

		_reflection.removeObject(this);

		_reflection = reflection;

		if(respawn)
			spawnMe();

		return true;
	}

	public void setReflection(int reflectionId)
	{
		Reflection r = ReflectionManager.getInstance().get(reflectionId);
		if(r == null)
		{
			Log.debug("Trying to set unavailable reflection: " + reflectionId + " for object: " + this + "!", new Throwable().fillInStackTrace());
			return;
		}

		setReflection(r);
	}

	/**
	 * Return the identifier of the L2Object.<BR><BR>
	 *
	 * @ - deprecated?
	 */
	@Override
	public final int hashCode()
	{
		return objectId;
	}

	public final int getObjectId()
	{
		return objectId;
	}

	public int getX()
	{
		return _x;
	}

	public int getY()
	{
		return _y;
	}

	public int getZ()
	{
		return _z;
	}

	/**
	 * Возвращает позицию (x, y, z, heading)
	 * @return Location
	 */
	public Location getLoc()
	{
		return new Location(_x, _y, _z, getHeading());
	}

	public int getGeoZ(Location loc)
	{
		return GeoEngine.getHeight(loc, getGeoIndex());
	}

	/**
	 * Устанавливает позицию (x, y, z) L2Object
	 * @param loc Location
	 */
	public void setLoc(Location loc)
	{
		setXYZ(loc.x, loc.y, loc.z);
	}

	public void setXYZ(int x, int y, int z)
	{
		_x = World.validCoordX(x);
		_y = World.validCoordY(y);
		_z = World.validCoordZ(z);

		World.addVisibleObject(this, null);
	}

	/**
	 * Return the visibility state of the L2Object. <BR><BR>
	 *
	 * <B><U> Concept</U> :</B><BR><BR>
	 * A L2Object is invisible if <B>_isVisible</B>=false or <B>_worldregion</B>==null <BR><BR>
	 *
	 * @return true if visible
	 */
	public final boolean isVisible()
	{
		return _state.get() == VISIBLE;
	}

	public boolean isInvisible(GameObject observer)
	{
		return false;
	}

	public void spawnMe(Location loc)
	{
		spawnMe0(loc, null);
	}

	protected void spawnMe0(Location loc, Creature dropper)
	{
		_x = loc.x;
		_y = loc.y;
		_z = getGeoZ(loc);

		spawn0(dropper);
	}

	public final void spawnMe()
	{
		spawn0(null);
	}

	/**
	 * Добавляет обьект в мир, добавляет в текущий регион. Делает обьект видимым.
	 */
	protected void spawn0(Creature dropper)
	{
		if(_reflection.isCollapseStarted())
			return;

		if(!_state.compareAndSet(CREATED, VISIBLE))
			return;

		World.addVisibleObject(this, dropper);

		getReflection().addObject(this);

		onSpawn();
	}

	public void toggleVisible()
	{
		if(isVisible())
			decayMe();
		else
			spawnMe();
	}

	/**
	 * Do Nothing.<BR><BR>
	 *
	 * <B><U> Overriden in </U> :</B><BR><BR>
	 * <li> L2Summon :  Reset isShowSpawnAnimation flag</li>
	 * <li> L2NpcInstance    :  Reset some flags</li><BR><BR>
	 *
	 */
	protected void onSpawn()
	{

	}

	/**
	 * Удаляет объект из текущего региона, делая его невидимым.
	 * Не путать с deleteMe. Объект после decayMe подлежит реюзу через spawnMe.
	 * Если перепутать будет утечка памяти.
	 */
	public final void decayMe()
	{
		if(!_state.compareAndSet(VISIBLE, CREATED))
			return;

		World.removeVisibleObject(this);

		onDespawn();
	}

	protected void onDespawn()
	{

	}

	/**
	 * Удаляет объект из мира. После этого объект не подлежит использованию.
	 */
	public final void deleteMe()
	{
		decayMe();

		if(!_state.compareAndSet(CREATED, DELETED))
			return;

		onDelete();
	}

	public final boolean isDeleted()
	{
		return _state.get() == DELETED;
	}

	protected void onDelete()
	{
		getReflection().removeObject(this);
		clearRef();
	}

	public void onAction(Player player, boolean shift)
	{
		if(shift && OnShiftActionHolder.getInstance().callShiftAction(player, GameObject.class, this, true))
			return;

		player.sendActionFailed();
	}

	public boolean isAttackable(Creature attacker)
	{
		return false;
	}

	public String getL2ClassShortName()
	{
		return getClass().getSimpleName();
	}

	public final long getXYDeltaSq(int x, int y)
	{
		long dx = x - getX();
		long dy = y - getY();
		return dx * dx + dy * dy;
	}

	public final long getXYDeltaSq(Location loc)
	{
		return getXYDeltaSq(loc.x, loc.y);
	}

	public final long getZDeltaSq(int z)
	{
		long dz = z - getZ();
		return dz * dz;
	}

	public final long getZDeltaSq(Location loc)
	{
		return getZDeltaSq(loc.z);
	}

	public final long getXYZDeltaSq(int x, int y, int z)
	{
		return getXYDeltaSq(x, y) + getZDeltaSq(z);
	}

	public final long getXYZDeltaSq(Location loc)
	{
		return getXYDeltaSq(loc.x, loc.y) + getZDeltaSq(loc.z);
	}

	public final double getDistance(int x, int y)
	{
		return Math.sqrt(getXYDeltaSq(x, y));
	}

	public final double getDistance(int x, int y, int z)
	{
		return Math.sqrt(getXYZDeltaSq(x, y, z));
	}

	public final double getDistance(Location loc)
	{
		return getDistance(loc.x, loc.y, loc.z);
	}

	public final double getDistance3D(Location loc)
	{
		return Math.sqrt(getXYZDeltaSq(loc.x, loc.y, loc.z));
	}

	/**
	 * Проверяет в досягаемости расстояния ли объект
	 * @param obj проверяемый объект
	 * @param range расстояние
	 * @return true, если объект досягаем
	 */
	public final boolean isInRange(GameObject obj, long range)
	{
		if(obj == null)
			return false;
		if(obj.getReflection() != getReflection())
			return false;
		long dx = Math.abs(obj.getX() - getX());
		if(dx > range)
			return false;
		long dy = Math.abs(obj.getY() - getY());
		if(dy > range)
			return false;
		long dz = Math.abs(obj.getZ() - getZ());
		return dz <= 1500 && dx * dx + dy * dy <= range * range;
	}

	public final boolean isInRangeZ(GameObject obj, long range)
	{
		if(obj == null)
			return false;
		if(obj.getReflection() != getReflection())
			return false;
		long dx = Math.abs(obj.getX() - getX());
		if(dx > range)
			return false;
		long dy = Math.abs(obj.getY() - getY());
		if(dy > range)
			return false;
		long dz = Math.abs(obj.getZ() - getZ());
		return dz <= range && dx * dx + dy * dy + dz * dz <= range * range;
	}

	public final boolean isInRange(Location loc, long range)
	{
		return isInRangeSq(loc, range * range);
	}

	public final boolean isInRangeSq(Location loc, long range)
	{
		return getXYDeltaSq(loc) <= range;
	}

	public final boolean isInRangeZ(Location loc, long range)
	{
		return isInRangeZSq(loc, range * range);
	}

	public final boolean isInRangeZSq(Location loc, long range)
	{
		return getXYZDeltaSq(loc) <= range;
	}

	public final double getDistance(GameObject obj)
	{
		if(obj == null)
			return 0;
		return Math.sqrt(getXYDeltaSq(obj.getX(), obj.getY()));
	}

	public final double getDistance3D(GameObject obj)
	{
		if(obj == null)
			return 0;
		return Math.sqrt(getXYZDeltaSq(obj.getX(), obj.getY(), obj.getZ()));
	}

	public final double getRealDistance(GameObject obj)
	{
		return getRealDistance3D(obj, true);
	}

	public final double getRealDistance3D(GameObject obj)
	{
		return getRealDistance3D(obj, false);
	}

	public final double getRealDistance3D(GameObject obj, boolean ignoreZ)
	{
		double distance = ignoreZ ? getDistance(obj) : getDistance3D(obj);
		if(isCreature())
			distance -= ((Creature) this).getCollisionRadius();
		if(obj.isCreature())
			distance -= ((Creature) obj).getCollisionRadius();
		return distance > 0 ? distance : 0;
	}

	public final long getSqDistance(int x, int y)
	{
		return getXYDeltaSq(x, y);
	}

	public final long getSqDistance(GameObject obj)
	{
		if(obj == null)
			return 0;
		return getXYDeltaSq(obj.getLoc());
	}

	/**
	 * Возвращает L2Player управляющий даным обьектом.<BR>
	 * <li>Для L2Player это сам игрок.</li>
	 * <li>Для L2Summon это его хозяин.</li><BR><BR>
	 * @return L2Player управляющий даным обьектом.
	 */
	public Player getPlayer()
	{
		return null;
	}

	public int getHeading()
	{
		return 0;
	}

	public int getMoveSpeed()
	{
		return 0;
	}

	public WorldRegion getCurrentRegion()
	{
		return _currentRegion;
	}

	public void setCurrentRegion(WorldRegion region)
	{
		_currentRegion = region;
	}

	public boolean isObservePoint()
	{
		return false;
	}

	public boolean isInBoat()
	{
		return false;
	}

	public boolean isInShuttle()
	{
		return false;
	}

	public boolean isFlying()
	{
		return false;
	}

	public double getCollisionRadius()
	{
		_log.warn("getColRadius called directly from L2Object");
		Thread.dumpStack();
		return 0;
	}

	public double getCollisionHeight()
	{
		_log.warn("getColHeight called directly from L2Object");
		Thread.dumpStack();
		return 0;
	}

	public boolean isCreature()
	{
		return false;
	}

	public boolean isPlayable()
	{
		return false;
	}

	public boolean isPlayer()
	{
		return false;
	}

	public boolean isPet()
	{
		return false;
	}

	public boolean isSummon()
	{
		return false;
	}

	public boolean isServitor()
	{
		return false;
	}

	public boolean isNpc()
	{
		return false;
	}

	public boolean isMonster()
	{
		return false;
	}

	public boolean isItem()
	{
		return false;
	}

	/**
	 * True для L2RaidBossInstance, но False для KamalokaBossInstance
	 */
	public boolean isRaid()
	{
		return false;
	}

	/**
	 * True для L2BossInstance
	 */
	public boolean isBoss()
	{
		return false;
	}

	/**
	 * True для L2TrapInstance
	 */
	public boolean isTrap()
	{
		return false;
	}

	public boolean isDoor()
	{
		return false;
	}

	/**
	 * True для L2ArtefactInstance
	 */
	public boolean isArtefact()
	{
		return false;
	}

	/**
	 * True для L2SiegeGuardInstance
	 */
	public boolean isSiegeGuard()
	{
		return false;
	}

	public boolean isBoat()
	{
		return false;
	}

	public boolean isVehicle()
	{
		return false;
	}

	public boolean isShuttle()
	{
		return false;
	}

	public boolean isMinion()
	{
		return false;
	}

	public String getName()
	{
		return getClass().getSimpleName() + ":" + objectId;
	}

	public String dump()
	{
		return dump(true);
	}

	public String dump(boolean simpleTypes)
	{
		return Util.dumpObject(this, simpleTypes, true, true);
	}

	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		return Collections.emptyList();
	}

	public List<L2GameServerPacket> deletePacketList(Player forPlayer)
	{
		return Collections.<L2GameServerPacket> singletonList(new DeleteObjectPacket(this));
	}

	@Override
	public void addEvent(Event event)
	{
		event.onAddEvent(this);

		super.addEvent(event);
	}

	@Override
	public void removeEvent(Event event)
	{
		event.onRemoveEvent(this);

		super.removeEvent(event);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
			return true;
		if(obj == null)
			return false;
		if(obj.getClass() != getClass())
			return false;
		return ((GameObject) obj).getObjectId() == getObjectId();
	}
	
	public boolean isTargetable(Creature creature)
	{
		return true; //true?
	}

	public boolean isDefender()
	{
		return false;
	}

	public boolean isStaticObject()
	{
		return false;
	}
}