package l2s.gameserver.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import l2s.commons.collections.LazyArrayList;
import l2s.commons.collections.MultiValueSet;
import l2s.commons.listener.Listener;
import l2s.commons.listener.ListenerList;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.EventOwner;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.stats.funcs.FuncAdd;
import l2s.gameserver.taskmanager.EffectTaskManager;
import l2s.gameserver.templates.ZoneTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zone extends EventOwner
{
	private static final Logger _log = LoggerFactory.getLogger(Zone.class);

	public static final Zone[] EMPTY_L2ZONE_ARRAY = new Zone[0];

	public static enum ZoneType
	{
		SIEGE,
		RESIDENCE,
		HEADQUARTER,
		FISHING,
		JUMPING,
		TELEPORT,

		water,
		battle_zone,
		damage,
		instant_skill,
		mother_tree,
		peace_zone,
		poison,
		CHANGED_ZONE,
		ssq_zone,
		swamp,
		no_escape,
		no_landing,
		no_restart,
		no_summon,
		dummy,
		offshore,
		epic,
		buff_store
	}

	public enum ZoneTarget
	{
		pc,
		npc,
		only_pc
	}

	public static final String BLOCKED_ACTION_PRIVATE_STORE = "open_private_store";
	public static final String BLOCKED_ACTION_PRIVATE_WORKSHOP = "open_private_workshop";
	public static final String BLOCKED_ACTION_DROP_MERCHANT_GUARD = "drop_merchant_guard";
	public static final String BLOCKED_ACTION_SAVE_BOOKMARK = "save_bookmark";
	public static final String BLOCKED_ACTION_USE_BOOKMARK = "use_bookmark";
	public static final String BLOCKED_ACTION_MINIMAP = "open_minimap";

	/**
	 * Таймер зоны
	 */
	private abstract class ZoneTimer extends RunnableImpl
	{
		protected Creature cha;
		protected Future<?> future;
		protected boolean active;

		public ZoneTimer(Creature cha)
		{
			this.cha = cha;
		}

		public void start()
		{
			active = true;
			future = EffectTaskManager.getInstance().schedule(this, getTemplate().getInitialDelay() * 1000L);
		}

		public void stop()
		{
			active = false;
			if(future != null)
			{
				future.cancel(false);
				future = null;
			}
		}

		public void next()
		{
			if(!active)
				return;
			if(getTemplate().getUnitTick() == 0 && getTemplate().getRandomTick() == 0)
				return;
			future = EffectTaskManager.getInstance().schedule(this, (getTemplate().getUnitTick() + Rnd.get(0, getTemplate().getRandomTick())) * 1000L);
		}

		@Override
		public abstract void runImpl() throws Exception;
	}

	/**
	 * Таймер для наложения эффектов зоны
	 */
	private class SkillTimer extends ZoneTimer
	{
		public SkillTimer(Creature cha)
		{
			super(cha);
		}

		@Override
		public void runImpl() throws Exception
		{
			if(!isActive())
				return;

			if(!checkTarget(cha))
				return;

			Skill skill = getZoneSkill();
			if(skill == null)
				return;

			if(Rnd.chance(getTemplate().getSkillProb()) && !cha.isDead())
				skill.getEffects(cha, cha);

			next();
		}
	}

	/**
	 * Таймер для нанесения урона
	 */
	private class DamageTimer extends ZoneTimer
	{
		public DamageTimer(Creature cha)
		{
			super(cha);
		}

		@Override
		public void runImpl() throws Exception
		{
			if(!isActive())
				return;

			if(!checkTarget(cha))
				return;

			int hp = getDamageOnHP();
			int mp = getDamageOnMP();
			int message = getDamageMessageId();

			if(hp == 0 && mp == 0)
				return;

			if(hp > 0)
			{
				cha.reduceCurrentHp(hp, cha, null, false, false, true, false, false, false, true);
				if(message > 0)
					cha.sendPacket(new SystemMessage(message).addNumber(hp));
			}

			if(mp > 0)
			{
				cha.reduceCurrentMp(mp, null);
				if(message > 0)
					cha.sendPacket(new SystemMessage(message).addNumber(mp));
			}

			next();
		}
	}

	public class ZoneListenerList extends ListenerList<Zone>
	{
		public void onEnter(Creature actor)
		{
			if(!getListeners().isEmpty())
				for(Listener<Zone> listener : getListeners())
					((OnZoneEnterLeaveListener) listener).onZoneEnter(Zone.this, actor);
		}

		public void onLeave(Creature actor)
		{
			if(!getListeners().isEmpty())
				for(Listener<Zone> listener : getListeners())
					((OnZoneEnterLeaveListener) listener).onZoneLeave(Zone.this, actor);
		}
	}

	private ZoneType _type;
	private boolean _active;
	private final MultiValueSet<String> _params;

	private final ZoneTemplate _template;

	private Reflection _reflection;

	private final ZoneListenerList listeners = new ZoneListenerList();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private final List<Creature> _objects = new LazyArrayList<Creature>(32);
	private final Map<Creature, ZoneTimer> _zoneTimers = new ConcurrentHashMap<Creature, ZoneTimer>();

	/**
	 * Ордер в зонах, с ним мы и добавляем/убираем статы.
	 * TODO: сравнить ордер с оффом, пока от фонаря
	 */
	public final static int ZONE_STATS_ORDER = 0x40;

	public Zone(ZoneTemplate template)
	{
		this(template.getType(), template);
	}

	public Zone(ZoneType type, ZoneTemplate template)
	{
		_type = type;
		_template = template;
		_params = template.getParams();
	}

	public ZoneTemplate getTemplate()
	{
		return _template;
	}

	public final String getName()
	{
		return getTemplate().getName();
	}

	public ZoneType getType()
	{
		return _type;
	}

	public void setType(ZoneType type)
	{
		_type = type;
	}

	public Territory getTerritory()
	{
		return getTemplate().getTerritory();
	}

	public final int getEnteringMessageId()
	{
		return getTemplate().getEnteringMessageId();
	}

	public final int getLeavingMessageId()
	{
		return getTemplate().getLeavingMessageId();
	}

	public Skill getZoneSkill()
	{
		return getTemplate().getZoneSkill();
	}

	public ZoneTarget getZoneTarget()
	{
		return getTemplate().getZoneTarget();
	}

	public Race getAffectRace()
	{
		return getTemplate().getAffectRace();
	}

	/**
	 * Номер системного вообщения которое будет отослано игроку при нанесении урона зоной
	 * @return SystemMessage ID
	 */
	public int getDamageMessageId()
	{
		return getTemplate().getDamageMessageId();
	}

	/**
	 * Сколько урона зона нанесет по хп
	 * @return количество урона
	 */
	public int getDamageOnHP()
	{
		return getTemplate().getDamageOnHP();
	}

	/**
	 * Сколько урона зона нанесет по мп
	 * @return количество урона
	 */
	public int getDamageOnMP()
	{
		return getTemplate().getDamageOnMP();
	}

	/**
	 * @return Бонус к скорости движения в зоне
	 */
	public double getMoveBonus()
	{
		return getTemplate().getMoveBonus();
	}

	/**
	 * Возвращает бонус регенерации хп в этой зоне
	 * @return Бонус регенарации хп в этой зоне
	 */
	public double getRegenBonusHP()
	{
		return getTemplate().getRegenBonusHP();
	}

	/**
	 * Возвращает бонус регенерации мп в этой зоне
	 * @return Бонус регенарации мп в этой зоне
	 */
	public double getRegenBonusMP()
	{
		return getTemplate().getRegenBonusMP();
	}

	public long getRestartTime()
	{
		return getTemplate().getRestartTime();
	}

	public List<Location> getRestartPoints()
	{
		return getTemplate().getRestartPoints();
	}

	public List<Location> getPKRestartPoints()
	{
		return getTemplate().getPKRestartPoints();
	}

	public Location getSpawn()
	{
		if(getRestartPoints() == null)
			return null;
		Location loc = getRestartPoints().get(Rnd.get(getRestartPoints().size()));
		return loc.clone();
	}

	public Location getPKSpawn()
	{
		if(getPKRestartPoints() == null)
			return getSpawn();
		Location loc = getPKRestartPoints().get(Rnd.get(getPKRestartPoints().size()));
		return loc.clone();
	}

	/**
	 * Проверяет находятся ли даные координаты в зоне.
	 * _loc - стандартная территория для зоны
	 * @param x координата
	 * @param y координата
	 * @return находятся ли координаты в локации
	 */
	public boolean checkIfInZone(int x, int y)
	{
		return getTerritory().isInside(x, y);
	}

	public boolean checkIfInZone(int x, int y, int z)
	{
		return checkIfInZone(x, y, z, getReflection());
	}

	public boolean checkIfInZone(int x, int y, int z, Reflection reflection)
	{
		return isActive() && _reflection == reflection && getTerritory().isInside(x, y, z);
	}

	public boolean checkIfInZone(Creature cha)
	{
		readLock.lock();
		try
		{
			return _objects.contains(cha);
		}
		finally
		{
			readLock.unlock();
		}
	}

	public final double findDistanceToZone(GameObject obj, boolean includeZAxis)
	{
		return findDistanceToZone(obj.getX(), obj.getY(), obj.getZ(), includeZAxis);
	}

	public final double findDistanceToZone(int x, int y, int z, boolean includeZAxis)
	{
		return PositionUtils.calculateDistance(x, y, z, (getTerritory().getXmax() + getTerritory().getXmin()) / 2, (getTerritory().getYmax() + getTerritory().getYmin()) / 2, (getTerritory().getZmax() + getTerritory().getZmin()) / 2, includeZAxis);
	}

	/**
	 * Обработка входа в территорию
	 * Персонаж всегда добавляется в список вне зависимости от активности территории.
	 * Если зона акивная, то обработается вход в зону
	 * @param cha кто входит
	 */
	public void doEnter(Creature cha)
	{
		boolean added = false;

		writeLock.lock();
		try
		{
			if(!_objects.contains(cha))
				added = _objects.add(cha);
		}
		finally
		{
			writeLock.unlock();
		}

		if(added)
			onZoneEnter(cha);
	}

	/**
	 * Обработка входа в зону
	 * @param actor кто входит
	 */
	protected void onZoneEnter(Creature actor)
	{
		checkEffects(actor, true);
		addZoneStats(actor);
		actor.onZoneEnter(this);
		listeners.onEnter(actor);
	}

	/**
	 * Обработка выхода из зоны
	 * Object всегда убирается со списка вне зависимости от зоны
	 * Если зона активная, то обработается выход из зоны
	 * @param cha кто выходит
	 */
	public void doLeave(Creature cha)
	{
		boolean removed = false;

		writeLock.lock();
		try
		{
			removed = _objects.remove(cha);
		}
		finally
		{
			writeLock.unlock();
		}

		if(removed)
			onZoneLeave(cha);
	}

	/**
	 * Обработка выхода из зоны
	 * @param actor кто выходит
	 */
	protected void onZoneLeave(Creature actor)
	{
		checkEffects(actor, false);
		removeZoneStats(actor);
		actor.onZoneLeave(this);
		listeners.onLeave(actor);
	}

	/**
	 * Добавляет статы зоне
	 * @param cha персонаж которому добавляется
	 */
	private void addZoneStats(Creature cha)
	{
		boolean update = false;

		// Проверка цели
		if(checkTarget(cha))
		{
			// Скорость движения накладывается только на L2Playable
			// affectRace в базе не указан, если надо будет влияние, то поправим
			if(getMoveBonus() != 0)
				if(cha.isPlayable())
				{
					cha.addStatFunc(new FuncAdd(Stats.RUN_SPEED, ZONE_STATS_ORDER, this, getMoveBonus()));
					update = true;
				}

			// Если у нас есть что регенить
			if(getRegenBonusHP() != 0)
			{
				cha.addStatFunc(new FuncAdd(Stats.REGENERATE_HP_RATE, ZONE_STATS_ORDER, this, getRegenBonusHP()));
				update = true;
			}

			// Если у нас есть что регенить
			if(getRegenBonusMP() != 0)
			{
				cha.addStatFunc(new FuncAdd(Stats.REGENERATE_MP_RATE, ZONE_STATS_ORDER, this, getRegenBonusMP()));
				update = true;
			}
		}

		Func[] funcs = getStatFuncs();
		if(funcs.length > 0)
		{
			cha.addStatFuncs(getStatFuncs());
			update = true;
		}

		if(update)
			cha.updateStats();
	}

	/**
	 * Убирает добавленые зоной статы
	 * @param cha персонаж у которого убирается
	 */
	private void removeZoneStats(Creature cha)
	{
		Func[] funcs = getStatFuncs();
		if(getRegenBonusHP() == 0 && getRegenBonusMP() == 0 && getMoveBonus() == 0 && funcs.length == 0)
			return;

		cha.removeStatsOwner(this);
		cha.updateStats();
	}

	/**
	 * Применяет эффекты при входе/выходе из(в) зону
	 * @param cha обьект
	 * @param enter вошел или вышел
	 */
	private void checkEffects(Creature cha, boolean enter)
	{
		if(checkTarget(cha))
			if(enter)
			{
				if(getZoneSkill() != null)
				{
					ZoneTimer timer = new SkillTimer(cha);
					_zoneTimers.put(cha, timer);
					timer.start();
				}
				else if(getDamageOnHP() > 0 || getDamageOnHP() > 0)
				{
					ZoneTimer timer = new DamageTimer(cha);
					_zoneTimers.put(cha, timer);
					timer.start();
				}
			}
			else
			{
				ZoneTimer timer = _zoneTimers.remove(cha);
				if(timer != null)
					timer.stop();

				if(getZoneSkill() != null)
					cha.getAbnormalList().stop(getZoneSkill());
			}
	}

	/**
	 * Проверяет подходит ли персонаж для вызвавшего действия
	 * @param cha персонаж
	 * @return подошел ли
	 */
	private boolean checkTarget(Creature cha)
	{
		switch(getZoneTarget())
		{
			case pc:
				if(!cha.isPlayable())
					return false;
				break;
			case only_pc:
				if(!cha.isPlayer())
					return false;
				break;
			case npc:
				if(!cha.isNpc())
					return false;
				break;
		}

		// Если у нас раса не "all"
		if(getAffectRace() != null)
		{
			Player player = cha.getPlayer();
			//если не игровой персонаж
			if(player == null)
				return false;
			// если раса не подходит
			if(player.getRace() != getAffectRace())
				return false;
		}

		return true;
	}

	public Creature[] getObjects()
	{
		readLock.lock();
		try
		{
			return _objects.toArray(new Creature[_objects.size()]);
		}
		finally
		{
			readLock.unlock();
		}
	}

	public List<Player> getInsidePlayers()
	{
		List<Player> result = new LazyArrayList<Player>();
		readLock.lock();
		try
		{
			Creature cha;
			for(int i = 0; i < _objects.size(); i++)
				if((cha = _objects.get(i)) != null && cha.isPlayer())
					result.add((Player) cha);
		}
		finally
		{
			readLock.unlock();
		}
		return result;
	}

	public List<Playable> getInsidePlayables()
	{
		List<Playable> result = new LazyArrayList<Playable>();
		readLock.lock();
		try
		{
			Creature cha;
			for(int i = 0; i < _objects.size(); i++)
				if((cha = _objects.get(i)) != null && cha.isPlayable())
					result.add((Playable) cha);
		}
		finally
		{
			readLock.unlock();
		}
		return result;
	}

	public List<NpcInstance> getInsideNpcs()
	{
		List<NpcInstance> result = new LazyArrayList<NpcInstance>();
		readLock.lock();

		try
		{
			for(int i = 0; i < _objects.size(); i++)
			{
				Creature cha;
				if((cha = _objects.get(i)) != null && cha.isNpc())
					result.add((NpcInstance) cha);
			}
		}
		finally
		{
			readLock.unlock();
		}
		return result;
	}

	/**
	 * Установка активности зоны. При установки флага активности, зона добавляется в соотвествующие регионы. В случае сброса
	 * - удаляется.
	 * @param value активна ли зона
	 */
	public void setActive(boolean value)
	{
		writeLock.lock();
		try
		{
			if(_active == value)
				return;
			_active = value;
		}
		finally
		{
			writeLock.unlock();
		}

		if(isActive())
			World.addZone(Zone.this);
		else
			World.removeZone(Zone.this);
	}

	public boolean isActive()
	{
		return _active;
	}

	public void setReflection(Reflection reflection)
	{
		_reflection = reflection;
	}

	public Reflection getReflection()
	{
		return _reflection;
	}

	public void setParam(String name, String value)
	{
		_params.put(name, value);
	}

	public void setParam(String name, Object value)
	{
		_params.put(name, value);
	}

	public MultiValueSet<String> getParams()
	{
		return _params;
	}

	public <T extends Listener<Zone>> boolean addListener(T listener)
	{
		return listeners.add(listener);
	}

	public <T extends Listener<Zone>> boolean removeListener(T listener)
	{
		return listeners.remove(listener);
	}

	@Override
	public final String toString()
	{
		return "[Zone " + getType() + " name: " + getName() + "]";
	}

	public void broadcastPacket(L2GameServerPacket packet, boolean toAliveOnly)
	{
		List<Player> insideZoners = getInsidePlayers();

		if(insideZoners != null && !insideZoners.isEmpty())
			for(Player player : insideZoners)
				if(toAliveOnly)
				{
					if(!player.isDead())
						player.broadcastPacket(packet);
				}
				else
					player.broadcastPacket(packet);
	}

	public void refreshListeners()
	{
		for(Creature creature : getObjects())
		{
			listeners.onLeave(creature);
			listeners.onEnter(creature);
		}
	}

	private final Func[] getStatFuncs()
	{
		return getTemplate().getStatFuncs(this);
	}
}