package l2s.gameserver.templates.npc;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.ai.NpcAI;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.model.instances.ReflectionBossInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestEventType;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.scripts.Scripts;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.templates.CreatureTemplate;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.TeleportLocation;
import l2s.gameserver.templates.skill.EffectTemplate;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NpcTemplate extends CreatureTemplate
{
	public enum ShotsType
	{
		NONE,
		SOUL,
		SPIRIT,
		BSPIRIT,
		SOUL_SPIRIT,
		SOUL_BSPIRIT
	}

	private static final Logger _log = LoggerFactory.getLogger(NpcTemplate.class);

	@SuppressWarnings("unchecked")
	public static final Constructor<NpcInstance> DEFAULT_TYPE_CONSTRUCTOR = (Constructor<NpcInstance>) NpcInstance.class.getConstructors()[0];
	@SuppressWarnings("unchecked")
	public static final Constructor<NpcAI> DEFAULT_AI_CONSTRUCTOR = (Constructor<NpcAI>) NpcAI.class.getConstructors()[0];

	private static final Map<String, Constructor<NpcAI>> AI_CONSTRUCTORS = new HashMap<String, Constructor<NpcAI>>();

	private final int _npcId;
	public final String name;
	public final String title;
	public int level;
	public final long rewardExp;
	public final long rewardSp;
	public int aggroRange;
	public final int rhand;
	public final int lhand;
	public final double rateHp;
	private Faction faction = Faction.NONE;
	public final int displayId;
	private final ShotsType _shots;
	public boolean isRaid = false;
	private StatsSet _AIParams;
	private int race = 0;
	private final int _castleId;
	private List<RewardList> _rewardList = Collections.emptyList();
	private TIntObjectMap<List<TeleportLocation>> _teleportList = new TIntObjectHashMap<List<TeleportLocation>>(1);
	private List<MinionData> _minions = Collections.emptyList();
	private Map<QuestEventType, Set<Quest>> _questEvents = Collections.emptyMap();
	private TIntObjectMap<Skill> _skills = new TIntObjectHashMap<Skill>();
	private Skill[] _damageSkills = Skill.EMPTY_ARRAY;
	private Skill[] _dotSkills = Skill.EMPTY_ARRAY;
	private Skill[] _debuffSkills = Skill.EMPTY_ARRAY;
	private Skill[] _buffSkills = Skill.EMPTY_ARRAY;
	private Skill[] _stunSkills = Skill.EMPTY_ARRAY;
	private Skill[] _healSkills = Skill.EMPTY_ARRAY;
	private Class<NpcInstance> _classType = NpcInstance.class;
	private Constructor<NpcInstance> _constructorType = DEFAULT_TYPE_CONSTRUCTOR;
	private final String _htmRoot;
	private TIntObjectMap<WalkerRoute> _walkerRoute = new TIntObjectHashMap<WalkerRoute>();
	private RandomActions _randomActions = null;

	public final int _enchantEffect;
	private final int _baseRandDam;
	private final int _baseReuseDelay;

	private final double _basePHitModify;
	private final double _basePAvoidModify;
	private final double _baseHitTimeFactor;

	private final int _baseSafeHeight;
	private final String _aiType;

	/**
	 * Constructor<?> of L2Character.<BR><BR>
	 * @param set The StatsSet object to transfer data to the method
	 */
	public NpcTemplate(StatsSet set)
	{
		super(set);
		_npcId = set.getInteger("npcId");
		displayId = set.getInteger("displayId");

		name = set.getString("name");
		title = set.getString("title");
		// sex = set.getString("sex");
		level = set.getInteger("level");
		rewardExp = set.getLong("rewardExp");
		rewardSp = set.getLong("rewardSp");
		aggroRange = set.getInteger("aggroRange");
		rhand = set.getInteger("rhand", 0);
		lhand = set.getInteger("lhand", 0);
		rateHp = set.getDouble("baseHpRate");
		_htmRoot = set.getString("htm_root", null);
		_shots = set.getEnum("shots", ShotsType.class, ShotsType.NONE);
		_castleId = set.getInteger("castle_id", 0);
		_AIParams = (StatsSet) set.getObject("aiParams", StatsSet.EMPTY);
		_enchantEffect = set.getInteger("enchant_effect", 0);

		_baseRandDam = set.getInteger("baseRandDam", 5 + (int) Math.sqrt(level));

		_baseReuseDelay = set.getInteger("baseReuseDelay", 0);
		_basePHitModify = set.getDouble("basePHitModify", 0);
		_basePAvoidModify = set.getDouble("basePAvoidModify", 0);
		_baseHitTimeFactor = set.getDouble("baseHitTimeFactor", 0);
		_baseSafeHeight = set.getInteger("baseSafeHeight", 100);

		setType(set.getString("type", null));
		_aiType = set.getString("ai_type", "NpcAI");
	}

	public Class<? extends NpcInstance> getInstanceClass()
	{
		return _classType;
	}

	public Constructor<? extends NpcInstance> getInstanceConstructor()
	{
		return _constructorType;
	}

	public boolean isInstanceOf(Class<?> _class)
	{
		return _class.isAssignableFrom(getInstanceClass());
	}

	/**
	 * Создает новый инстанс NPC. Для него следует вызывать (именно в этом порядке):
	 * <br> setSpawnedLoc (обязательно)
	 * <br> setReflection (если reflection не базовый)
	 * <br> setChampion (опционально)
	 * <br> setCurrentHpMp (если вызывался setChampion)
	 * <br> spawnMe (в качестве параметра брать getSpawnedLoc)
	 */
	public NpcInstance getNewInstance(MultiValueSet<String> set)
	{
		try
		{
			return _constructorType.newInstance(IdFactory.getInstance().getNextId(), this, set);
		}
		catch(Exception e)
		{
			_log.error("Unable to create instance of NPC " + _npcId, e);
		}

		return null;
	}

	public NpcInstance getNewInstance()
	{
		return getNewInstance(StatsSet.EMPTY);
	}

	@SuppressWarnings("unchecked")
	public NpcAI getNewAI(NpcInstance npc)
	{
		String ai = npc.getParameter("ai_type", _aiType);

		Constructor<NpcAI> constructorAI = AI_CONSTRUCTORS.get(ai);
		if(constructorAI == null)
		{
			Class<NpcAI> classAI = null;
			try
			{
				classAI = (Class<NpcAI>) Class.forName("l2s.gameserver.ai." + ai);
			}
			catch(ClassNotFoundException e)
			{
				classAI = (Class<NpcAI>) Scripts.getInstance().getClasses().get("ai." + ai);
			}

			if(classAI == null)
			{
				classAI = NpcAI.class;
				_log.error("Not found ai class for ai: " + ai + ". NpcId: " + npc.getNpcId());
			}

			constructorAI = (Constructor<NpcAI>) classAI.getConstructors()[0];

			if(classAI.isAnnotationPresent(Deprecated.class))
				_log.error("Ai type: " + ai + ", is deprecated. NpcId: " + npc.getNpcId());

			AI_CONSTRUCTORS.put(ai, constructorAI);
		}

		try
		{
			return constructorAI.newInstance(npc);
		}
		catch(Exception e)
		{
			_log.error("Unable to create ai of NPC " + npc.getNpcId(), e);
		}

		return new NpcAI(npc);
	}

	@SuppressWarnings("unchecked")
	protected void setType(String type)
	{
		Class<NpcInstance> classType = null;
		try
		{
			classType = (Class<NpcInstance>) Class.forName("l2s.gameserver.model.instances." + type + "Instance");
		}
		catch(ClassNotFoundException e)
		{
			classType = (Class<NpcInstance>) Scripts.getInstance().getClasses().get("npc.model." + type + "Instance");
		}

		if(classType == null)
		{
			_log.error("Not found type class for type: " + type + ". NpcId: " + _npcId);
		}

		if(_npcId == 0) //temp
		{
			try
			{
				classType = (Class<NpcInstance>) Class.forName("l2s.gameserver.model.instances.NpcInstance");
			}
			catch(ClassNotFoundException e)
			{
			}

			_classType = classType;
			_constructorType = (Constructor<NpcInstance>) _classType.getConstructors()[0];
		}
		else
		{
			_classType = classType;
			_constructorType = (Constructor<NpcInstance>) _classType.getConstructors()[0];
		}

		if(_classType.isAnnotationPresent(Deprecated.class))
		{
			_log.error("Npc type: " + type + ", is deprecated. NpcId: " + _npcId);
		}

		//TODO [G1ta0] сделать поле в соотвествующих классах
		isRaid = isInstanceOf(RaidBossInstance.class) && !isInstanceOf(ReflectionBossInstance.class);
	}

	public void addTeleportList(int id, List<TeleportLocation> list)
	{
		_teleportList.put(id, list);
	}

	public List<TeleportLocation> getTeleportList(int id)
	{
		return _teleportList.get(id);
	}

	public TIntObjectMap<List<TeleportLocation>> getTeleportList()
	{
		return _teleportList;
	}

	public void addRewardList(RewardList rewardList)
	{
		if(_rewardList.isEmpty())
			_rewardList = new CopyOnWriteArrayList<RewardList>();

		_rewardList.add(rewardList);
	}

	public void removeRewardList(RewardList rewardList)
	{
		_rewardList.remove(rewardList);
	}

	public Collection<RewardList> getRewards()
	{
		return _rewardList;
	}

	public void addMinion(MinionData minion)
	{
		if(_minions.isEmpty())
		{
			_minions = new ArrayList<MinionData>(1);
		}

		_minions.add(minion);
	}

	public void setFaction(Faction faction)
	{
		this.faction = faction;
	}

	public Faction getFaction()
	{
		return faction;
	}

	public void addSkill(Skill skill)
	{
		_skills.put(skill.getId(), skill);

		//TODO [G1ta0] перенести в AI
		if(skill.isNotUsedByAI() || skill.getTargetType() == Skill.SkillTargetType.TARGET_NONE || skill.getSkillType() == Skill.SkillType.NOTDONE || !skill.isActive())
		{
			return;
		}

		switch(skill.getSkillType())
		{
			case PDAM:
			case MANADAM:
			case MDAM:
			case DRAIN:
			case DRAIN_SOUL:
			{
				boolean added = false;

				for(EffectTemplate eff : skill.getEffectTemplates(EffectUseType.NORMAL))
				{
					switch(eff.getEffectType())
					{
						case Stun:
							_stunSkills = ArrayUtils.add(_stunSkills, skill);
							added = true;
							break;
						case t_hp:
							if(eff.getValue() < 0)
							{
								_dotSkills = ArrayUtils.add(_dotSkills, skill);
								added = true;
							}
							break;
						case ManaDamOverTime:
						case LDManaDamOverTime:
							_dotSkills = ArrayUtils.add(_dotSkills, skill);
							added = true;
							break;
					}
				}

				if(!added)
				{
					_damageSkills = ArrayUtils.add(_damageSkills, skill);
				}

				break;
			}
			case DOT:
			case MDOT:
			case POISON:
				_dotSkills = ArrayUtils.add(_dotSkills, skill);
				break;
			case DEBUFF:
			case SLEEP:
			case ROOT:
			case PARALYZE:
			case MUTE:
				_debuffSkills = ArrayUtils.add(_debuffSkills, skill);
				break;
			case BUFF:
				_buffSkills = ArrayUtils.add(_buffSkills, skill);
				break;
			case STUN:
				_stunSkills = ArrayUtils.add(_stunSkills, skill);
				break;
			case HEAL:
			case HEAL_PERCENT:
			case HOT:
				_healSkills = ArrayUtils.add(_healSkills, skill);
				break;
			default:

				break;
		}
	}

	public Skill[] getDamageSkills()
	{
		return _damageSkills;
	}

	public Skill[] getDotSkills()
	{
		return _dotSkills;
	}

	public Skill[] getDebuffSkills()
	{
		return _debuffSkills;
	}

	public Skill[] getBuffSkills()
	{
		return _buffSkills;
	}

	public Skill[] getStunSkills()
	{
		return _stunSkills;
	}

	public Skill[] getHealSkills()
	{
		return _healSkills;
	}

	public List<MinionData> getMinionData()
	{
		return _minions;
	}

	public TIntObjectMap<Skill> getSkills()
	{
		return _skills;
	}

	public void addQuestEvent(QuestEventType eventType, Quest quest)
	{
		if(_questEvents.isEmpty())
			_questEvents = new HashMap<QuestEventType, Set<Quest>>();

		Set<Quest> quests = _questEvents.get(eventType);
		if(quests == null)
		{
			quests = new HashSet<Quest>();
			_questEvents.put(eventType, quests);
		}
		quests.add(quest);
	}

	public Set<Quest> getEventQuests(QuestEventType eventType)
	{
		return _questEvents.get(eventType);
	}

	public int getRace()
	{
		return race;
	}

	public void setRace(int newrace)
	{
		race = newrace;
	}

	public boolean isUndead()
	{
		return race == 1;
	}

	@Override
	public String toString()
	{
		return "Npc template " + name + "[" + _npcId + "]";
	}

	@Override
	public int getId()
	{
		return _npcId;
	}

	public String getName()
	{
		return name;
	}

	public ShotsType getShots()
	{
		return _shots;
	}

	public final StatsSet getAIParams()
	{
		return _AIParams;
	}

	public final void setAIParam(String name, Object value)
	{
		if (_AIParams == StatsSet.EMPTY)
			_AIParams = new StatsSet();
		_AIParams.set(name, value);
	}

	public int getCastleId()
	{
		return _castleId;
	}

	public Map<QuestEventType, Set<Quest>> getQuestEvents()
	{
		return _questEvents;
	}

	public String getHtmRoot()
	{
		return _htmRoot;
	}

	public void addWalkerRoute(WalkerRoute walkerRoute)
	{
		if(!walkerRoute.isValid())
		{
			return;
		}

		_walkerRoute.put(walkerRoute.getId(), walkerRoute);
	}

	public WalkerRoute getWalkerRoute(int id)
	{
		return _walkerRoute.get(id);
	}

	public void setRandomActions(RandomActions randomActions)
	{
		_randomActions = randomActions;
	}

	public RandomActions getRandomActions()
	{
		return _randomActions;
	}

	public int getEnchantEffect()
	{
		return _enchantEffect;
	}

	@Override
	public int getBaseRandDam()
	{
		return _baseRandDam;
	}

	public int getBaseReuseDelay()
	{
		return _baseReuseDelay;
	}

	private final Map<ListenerHookType, Set<ListenerHook>> _listenerHooks = new HashMap<ListenerHookType, Set<ListenerHook>>();
	private boolean isNoClan;

	public void addListenerHook(ListenerHookType type, ListenerHook hook)
	{
		Set<ListenerHook> hooks = _listenerHooks.get(type);
		if(hooks == null)
		{
			hooks = new HashSet<ListenerHook>();
			_listenerHooks.put(type, hooks);
		}
		hooks.add(hook);
	}

	public Set<ListenerHook> getListenerHooks(ListenerHookType type)
	{
		Set<ListenerHook> hooks = _listenerHooks.get(type);
		if(hooks == null)
			return Collections.emptySet();
		return hooks;
	}

	public boolean isNoClan()
	{
		return isNoClan;
	}

	public void setNoClan(boolean noClan)
	{
		isNoClan = noClan;
	}
}